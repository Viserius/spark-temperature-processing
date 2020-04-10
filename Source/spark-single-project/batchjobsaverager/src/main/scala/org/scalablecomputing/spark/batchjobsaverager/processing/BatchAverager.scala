package org.scalablecomputing.spark.batchjobsaverager.processing

import java.sql.Timestamp
import java.util

import org.apache.spark.sql.{Dataset, Row, SaveMode, SparkSession}
import org.apache.spark._
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming.OutputMode
import org.apache.spark.sql.cassandra._
import org.scalablecomputing.spark.batchjobsaverager.model.{BatchJobAverage, BatchJobRequest, MeasurementReducable}
import org.scalablecomputing.spark.common.model.Measurement


class BatchAverager(spark: SparkSession) extends java.io.Serializable {
  import spark.implicits._

  def compute(batchJobs: Dataset[BatchJobRequest], allMeasurements: Dataset[Measurement]): Dataset[BatchJobAverage]
  = {

    // Join batch jobs with actual measurements
    var withMeasurements = batchJobs.as("job")
      .join(allMeasurements.as("m"), usingColumns = Seq("station"))
      .where("m.date >= job.startDate AND m.date <= job.endDate")
      .withColumn("totalSummed", typedLit(1))
      .withColumn("temperature", col("m.temperature").cast("double"))
      .select(
        col("job.station").alias("station"),
        col("job.startDate").alias("date"),
        col("job.endDate").alias("endDate"),
        col("temperature"),
        col("totalSummed")
      ).as[MeasurementReducable]

    // Group values based on station and date
    val grouped = withMeasurements.groupByKey(x =>
      (x.station, x.date, x.endDate)
    )

    // Reduce the measurements by summing up the temps, and for each row we increase totalSummed
    val reduced = grouped.reduceGroups((first, second) =>
      {
        MeasurementReducable(
          first.station,
          first.date,
          first.endDate,
          first.temperature + second.temperature,
          first.totalSummed + second.totalSummed
        )
      }
    )

    // Map the reduced value by dividing the total summed
    reduced.map(kv =>
      BatchJobAverage(
        kv._2.station,
        kv._2.date,
        kv._2.temperature / kv._2.totalSummed
      )
    )
  }
}
