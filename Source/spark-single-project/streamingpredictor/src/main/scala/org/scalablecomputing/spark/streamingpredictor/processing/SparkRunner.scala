package org.scalablecomputing.spark.streamingpredictor.processing

import org.apache.spark.sql.{Dataset, SparkSession}
import org.scalablecomputing.spark.common.exports.CassandraExport
import org.scalablecomputing.spark.common.model.Measurement
import org.scalablecomputing.spark.common.sources.{CassandraMeasurementImport, KafkaMeasurementSource}

class SparkRunner(spark: SparkSession, brokers: String, topic: String) {

  def run(): Unit = {
    // --- Retrieve inputs ---
    val allMeasurementsSet: Dataset[Measurement] = new CassandraMeasurementImport(spark).dataframe // historic temps
    val newMeasurementsSet: Dataset[Measurement] = new KafkaMeasurementSource(spark, brokers, topic).getInput // streaming temps

    // --- Processing ---
    // Utilize historic data and streamed value to predict futures values
    val predictedDataSet: Dataset[Measurement] = new Predictor(spark).compute(allMeasurementsSet, newMeasurementsSet)

    // --- Export results ---
    new CassandraExport(spark, "predictions").export(predictedDataSet)
  }

}