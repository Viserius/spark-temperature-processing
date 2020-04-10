package org.scalablecomputing.spark.batchjobsaverager.processing

import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}
import org.scalablecomputing.spark.batchjobsaverager.model.{BatchJobAverage, BatchJobRequest}
import org.scalablecomputing.spark.batchjobsaverager.sources.KafkaBatchJobSource
import org.scalablecomputing.spark.common.exports.CassandraExport
import org.scalablecomputing.spark.common.model.Measurement
import org.scalablecomputing.spark.common.sources.{CassandraMeasurementImport, KafkaMeasurementSource}

class SparkRunner(spark: SparkSession, brokers: String, timespan: String) {

  def run(): Unit = {
    // --- Retrieve inputs ---
    val newBatchJobsSet: Dataset[BatchJobRequest] = new KafkaBatchJobSource(spark, brokers, "batchjobs-" + timespan).getInput // streaming temps
    val allMeasurementsSet: Dataset[Measurement] = new CassandraMeasurementImport(spark).dataframe // historic temps

    // --- Processing ---
    val batchAveragerSet: Dataset[BatchJobAverage] =
      new BatchAverager(spark).compute(newBatchJobsSet, allMeasurementsSet)

    // --- Export results ---
    new CassandraExport(spark, "batch_averages_" + timespan).export(batchAveragerSet)
    //new ConsoleExport(spark).export(batchAveragerSet)
  }

}