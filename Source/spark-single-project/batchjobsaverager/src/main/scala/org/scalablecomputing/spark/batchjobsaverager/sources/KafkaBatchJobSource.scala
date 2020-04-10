package org.scalablecomputing.spark.batchjobsaverager.sources

import org.apache.spark.sql.functions.{col, from_json}
import org.apache.spark.sql.types.{IntegerType, StructType, TimestampType}
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}
import org.scalablecomputing.spark.batchjobsaverager.model.BatchJobRequest

class KafkaBatchJobSource(spark: SparkSession, brokers: String, topic: String) {

  import spark.implicits._

  def getInput: Dataset[BatchJobRequest] = {
    val measurementKafkaDF = readFromStream
    val measurementJsonStringDF = kafkaToJson(measurementKafkaDF)
    val measurementRawDF = jsonToDF(measurementJsonStringDF)
    val measurementDF = measurementRawDF.as[BatchJobRequest];
    measurementDF
  }

  def readFromStream: DataFrame =
    spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", brokers)
      .option("subscribe", topic)
      .option("startingOffsets", "earliest")
      .option("failOnDataLoss", "false")
      .load()

  def kafkaToJson(measurementKafkaDF: DataFrame): DataFrame =
    measurementKafkaDF.selectExpr("CAST(value AS STRING)")

  def schema: StructType = new StructType()
    .add("station", IntegerType)
    .add("startDate", TimestampType)
    .add("endDate", TimestampType)

  def jsonToDF(measurementJsonStringDF: DataFrame): DataFrame =
    measurementJsonStringDF.withColumn(
      "parsedJson", from_json(col("value"), schema))
        .select("parsedJson.*")
}
