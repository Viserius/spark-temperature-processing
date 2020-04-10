package org.scalablecomputing.spark.streamingaverager.processing

import org.apache.spark.sql.{Column, DataFrame, Dataset, SparkSession}
import org.apache.spark._
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._
import org.scalablecomputing.spark.common.model.Measurement
import org.scalablecomputing.spark.streamingaverager.model.StationAverages


class StreamingAverager(spark: SparkSession) extends java.io.Serializable {
  import spark.implicits._

  def compute(averages: Dataset[StationAverages], streamingMeasurements: Dataset[Measurement],
              allMeasurements: Dataset[Measurement]): Dataset[StationAverages] = {

    // Add an index column to each historic value, counted per station ID, indicating the n-th lagged value
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
    )

    // Obtain single row of N-th row that needs to be taken out of the average
    val historicDay = historicWithIdx.filter("lagIdx = " + (24-1))
    val historicWeek = historicWithIdx.filter("lagIdx = " + (24*7-1))
    val historicMonth = historicWithIdx.filter("lagIdx = " + (24*31-1))

    val withAverages = streamingMeasurements.join(averages, usingColumns = Seq("station"), "left")
    val withAveragesAndHistoric = withAverages.as("new")
      .join(historicDay.as("day"), usingColumns = Seq("station"), "left")
      .join(historicWeek.as("week"), usingColumns = Seq("station"), "left")
      .join(historicMonth.as("month"), usingColumns = Seq("station"), "left")
    .select(
      col("new.*"),
      col("day.temperature").alias("dayTemp"),
      col("week.temperature").alias("weekTemp"),
      col("month.temperature").alias("monthTemp"),
      col("day.numRecords")
    );

    withAveragesAndHistoric.map(row => {
      StationAverages(
        row.getAs[Int]("station"),
        UpdateAverage(
          row.getAs[Double]("daily_average"),
          row.getAs[Int]("dayTemp"),
          row.getAs[Int]("temperature"),
          row.getAs[Long]("numRecords"),
          24
        ),
        UpdateAverage(
          row.getAs[Double]("weekly_average"),
          row.getAs[Int]("weekTemp"),
          row.getAs[Int]("temperature"),
          row.getAs[Long]("numRecords"),
          24 * 7
        ),
        UpdateAverage(
          row.getAs[Double]("monthly_average"),
          row.getAs[Int]("monthTemp"),
          row.getAs[Int]("temperature"),
          row.getAs[Long]("numRecords"),
          24 * 31
        )
      )
    })
  }

  def UpdateAverage(oldAverage: Double, rollOutTemp: Int, newTemp: Int, numRecords: Long, averageSize: Int): Double = {
    // If we have less historic values than avgN, no need to roll out and compute cumulative average
    if(numRecords == 0)
      newTemp // first average value, nothing to compute further
    if(oldAverage == 0.0)
      computeCumulativeAverage(oldAverage, newTemp, averageSize, numRecords)

    // Otherwise, if we have avgN historic values or more, the last value is to be rolled out
    else
      computeMovingAverage(oldAverage, newTemp, rollOutTemp, averageSize)
  }

  def computeCumulativeAverage(oldAverage: Double,
                               temperature: Int, averageSize: Int, numRecords: Long): Double = {
    // 1. multiplying it by their dividend,
    // 2. adding the new value,
    // 3. dividing again
    (oldAverage * numRecords + temperature) / (numRecords + 1)
  }

  def computeMovingAverage(oldAverage: Double,
                           temperature: Int, rollOutTemp: Int, averageSize: Int): Double = {

    // 1. Multiply by dividend so we get the total sum
    // 2. Add the new temperature
    // 3. Remove the temperature that "rolls out"
    // 4. Divide again
    (oldAverage * averageSize + temperature - rollOutTemp) / averageSize
  }

}
