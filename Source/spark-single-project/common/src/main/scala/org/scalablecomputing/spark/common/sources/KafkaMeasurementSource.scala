package org.scalablecomputing.spark.common.sources

import org.apache.spark.sql.types.{DateType, IntegerType, StringType, StructType}
import org.apache.spark.sql.{DataFrame, Dataset, Encoder, Encoders, SparkSession}
import org.apache.spark.sql.functions.{col, from_json}
import org.apache.spark.sql.internal.SQLConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.scalablecomputing.spark.common.model.Measurement

class KafkaMeasurementSource(spark: SparkSession, brokers: String, topic: String) {
  import spark.implicits._

  def getInput: Dataset[Measurement] = {
    val measurementKafkaDF = readFromStream
    val measurementJsonStringDF = kafkaToJson(measurementKafkaDF)
    val measurementRawDF = jsonToDF(measurementJsonStringDF)
    val measurementDF = measurementRawDF.as[Measurement];
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
    .add("date", DateType)
    .add("hour", IntegerType)
    .add("temperature", IntegerType)

  def jsonToDF(measurementJsonStringDF: DataFrame): DataFrame =
    measurementJsonStringDF.withColumn(
      "parsedJson", from_json(col("value"), schema))
      .select("parsedJson.*")
}

