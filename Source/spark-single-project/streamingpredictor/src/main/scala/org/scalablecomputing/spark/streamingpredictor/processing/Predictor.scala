package org.scalablecomputing.spark.streamingpredictor.processing

import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions.{col, _}
import org.apache.spark.sql.{Dataset, SparkSession}
import org.scalablecomputing.spark.common.model.Measurement
import org.scalablecomputing.spark.streamingpredictor.model.{Prediction, PredictionReducable}


class Predictor(spark: SparkSession) extends java.io.Serializable {
  import spark.implicits._

  def compute(allMeasurements: Dataset[Measurement], newMeasurements: Dataset[Measurement]): Dataset[Measurement] = {

      // Join last year measurement jobs with new measurement measurements
      val prevYearMeasurement = newMeasurements.as("measurement")
        .withColumn("measurement.isLastHourOfDay", when($"measurement.hour" === 24, typedLit(1)).otherwise(typedLit(0)))
        .join(allMeasurements.as("m"), $"measurement.station" === $"m.station" && $"m.date" === expr("date_add(add_months(measurement.date, -12),cast(measurement.hour=24 as Int))") && $"m.hour" === expr("MOD(measurement.hour, 24) + 1"), "left")
        .withColumn("m.temperature", col("m.temperature").cast("double"))
        .select(
          col("measurement.station").alias("station"),
          col("m.date").alias("lastYearDate"),
          col("m.temperature").alias("lastYearTemperature"),
          col("measurement.date").alias("newDate"),
          col("measurement.hour").alias("newHour"),
          col("measurement.temperature").alias("newTemperature")
        ).as[Prediction]

    // Add an index column to each historic value, counted per station ID, indicating the n-th lagged value
    // And filter to only obtain each stations latest 22 measurements to compute the prediction
    val historicWithIdx = allMeasurements.select(
        col("station"),
        col("date"),
        col("hour"),
        col("temperature"),
        row_number().over(
          Window.partitionBy("station")
            .orderBy(desc("date"), desc("hour"))
        ).alias("lagIdx"),
        count("station").over(
          Window.partitionBy("station")
        ).as("numRecords")
        ).where("lagIdx < 23")

    // join the historic+prevYearMeasurement with columns to calculate the prediction afterwards
    val completePredictionMeasurements = historicWithIdx.as("historic")
      .join(prevYearMeasurement.as("prevYear"), usingColumns = Seq("station"))
      .select(
        col("historic.station").alias("station"),
        col("prevYear.lastYearTemperature").alias("lastYearTemperature"),
        col("prevYear.newDate").alias("newDate"),
        col("prevYear.newHour").alias("newHour"),
        col("prevYear.newTemperature").alias("newTemperature"),
        col("historic.lagIdx").alias("lagIdx"),
        col("historic.temperature").alias("historicTemperature").cast("double"),
        col("historic.numRecords").alias("historicNumRecords")
      ).withColumn("predictedHour", expr("MOD(newHour, 24) + 1"))
      .withColumn("predictedDate", when($"newHour" === 24, date_add(col("newDate"), 1)).otherwise(col("newDate")))
      .withColumn("predictedTemperature", typedLit(0.0))
      .withColumn("totalWeightSum", typedLit(0))
      .as[PredictionReducable]


    // Group values based on station and newDate and newHour since these need to be unique
    val grouped = completePredictionMeasurements.groupByKey(x =>
      (x.station, x.newDate, x.newHour)
    )

    // Reduce the measurements by multiplying the weight (calculated with getWeight)
    // by the temperatures, and for each row increase the predictedTemperature as well as totalWeightSummed
    // each rows lagIdx is replaced with -1 to mark that that row was already analyzed
    val reduced = grouped.reduceGroups((first, second) =>
    {
      var predictedTemperature = 0.0
      var totalWeight : Double = 0.0

      if(first.lagIdx != -1){
      val firstWeight = getWeight(first.lagIdx, first.historicNumRecords)
       // We have not yet weighted this value!
      predictedTemperature += first.historicTemperature * firstWeight
        totalWeight += firstWeight
      }
      else {
       // We have already weighted this value, so not compute the weight again! Simply carry it over.
        predictedTemperature += first.predictedTemperature
        totalWeight += first.totalWeightSum
      }

      if(second.lagIdx != -1) { // We have not yet weighted this value!
        val secondWeight = getWeight(second.lagIdx, second.historicNumRecords)
        predictedTemperature += second.historicTemperature * secondWeight
        totalWeight += secondWeight
      }
      else {
        // We have already weighted this value, so not compute the weight again! Simply carry it over.
        predictedTemperature += second.predictedTemperature
        totalWeight += second.totalWeightSum
      }

      PredictionReducable(
        first.station,
        first.lastYearTemperature,
        first.newDate,
        first.newHour,
        first.newTemperature,
        -1,
        first.historicTemperature,
        first.historicNumRecords,
        first.predictedHour,
        first.predictedDate,
        predictedTemperature,
        totalWeight
      )
    }
    )

    reduced.map(kv => {
      Measurement(
        kv._2.station,
        kv._2.predictedDate,
        kv._2.predictedHour,
        getFinalPrediction(kv._2.lastYearTemperature, kv._2.newTemperature, kv._2.predictedTemperature, kv._2.totalWeightSum)
      )
    })
  }



  def getWeight(index : Int, numRecords : BigInt) : Double = {

    // When there are more than 22 records, that means that the values corresponding to the hours corresponding to
    // predictedHour+1,+2 and +3 (from the day before) are present, therefore more weight is considered for these
    if(numRecords == 22){

      if(index < 2 && index > 18){
        // From the remaining 25% of the weight, 60% of it is assigned to the 2 hours
        // before the newMeasurement and the 3 last historics predictions. So 60% of 25% is divided by 5 measurements
        //(0.6*0.25) / 5 = 0.03
        // The weight is then normalized so that this weight is equal to 0.03 / 0.00558 = 5.1
        5.1
      }
      else{
        // The weight for the other measurements is equal to 1
        // (0.4*0.25) / 17 =~ 0.00558
        1.0
      }

    }
    // If there are not 22 previous records, no weight is assigned and only the most recent 2 measurements are used
    else{
      if(index > 2){
        // If there are less than 22 records, the measurements which don't correspond to the 2 hours before
        // the newMeasurement are disregarded (their weight is 0)
        0.0
      }
      else{
        // For the case were there are less than 22 records, but still there are one or two measurements close to the predicted hour,
        // those get a weight of 1 (so they have the same weights)
        1.0
      }
    }

  }

  def getFinalPrediction(lastYearTemperature : Option[Int],
                         newTemperature : Int , predictedTemperature: Double, totalWeightSum: Double): Int = {

    var weightedMeasurements = 0.0

    // When there are no records before the newTemperature (only happens once when the application just starts)
    if(totalWeightSum == 0.0){
      weightedMeasurements = newTemperature
    }
    else{
      weightedMeasurements = (predictedTemperature / totalWeightSum)
    }

    if(lastYearTemperature.nonEmpty){
      // The formula is applied to obtain the prediction
      (0.5 * weightedMeasurements + 0.25 * newTemperature + 0.25 * lastYearTemperature.get).toInt
    }
    else{
      // If the lastYearTemperature is not present the weights are adjusted
      (weightedMeasurements * 0.7 + newTemperature * 0.3).toInt
    }

  }

}
