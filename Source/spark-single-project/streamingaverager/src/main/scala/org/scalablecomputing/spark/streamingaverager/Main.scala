package org.scalablecomputing.spark.streamingaverager

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.connect.json.JsonDeserializer
import org.apache.spark._
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils}
import org.apache.spark.streaming.kafka010.LocationStrategies._
import com.datastax.spark.connector._
import org.apache.spark.sql.SparkSession
import org.scalablecomputing.spark.streamingaverager.processing.SparkRunner

object Main {
  def main(args: Array[String]): Unit = {
    new SparkRunner(setupSparkSession,"kafka-1-service:9092,kafka-2-service:9092,kafka-3-service:9092",
      "temperatures-in").run()
  }

  private def setupSparkSession: SparkSession =
    sql.SparkSession.builder
      .appName("temperature-stream-processing")
      .master("local[*]")
      .config(sparkConfiguration)
      .getOrCreate()

  private def sparkConfiguration = new SparkConf(true)
    .set("spark.cassandra.connection.host", "cassandra-service")
    .set("spark.cassandra.connection.port", "9042")
    .set("spark.cassandra.auth.username", "cassandra")
    .set("spark.cassandra.auth.password", "cassandra")
}
