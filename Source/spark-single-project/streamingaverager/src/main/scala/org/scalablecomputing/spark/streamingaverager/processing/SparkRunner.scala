package org.scalablecomputing.spark.streamingaverager.processing

import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}
import org.scalablecomputing.spark.common.exports.CassandraExport
import org.scalablecomputing.spark.common.model.Measurement
import org.scalablecomputing.spark.common.sources.{CassandraMeasurementImport, KafkaMeasurementSource}
import org.scalablecomputing.spark.streamingaverager.model.StationAverages
import org.scalablecomputing.spark.streamingaverager.sources.CassandraAveragesImport

class SparkRunner(spark: SparkSession, brokers: String, topic: String) {

  def run(): Unit = {
    // --- Retrieve inputs ---
    val newMeasurementsSet: Dataset[Measurement] = new KafkaMeasurementSource(spark, brokers, topic).getInput // streaming temps
    val allMeasurementsSet: Dataset[Measurement] = new CassandraMeasurementImport(spark).dataframe // historic temps
    val averagesSet: Dataset[StationAverages] = new CassandraAveragesImport(spark).dataframe // averages

    // --- Processing ---
    // Retrieve historic measurements for computing averages
    val streamingAveragesSet: Dataset[StationAverages] = new StreamingAverager(spark).compute(averagesSet,
      newMeasurementsSet, allMeasurementsSet)

    // --- Export results ---
    new CassandraExport(spark, "averages").export(streamingAveragesSet)
  }

}