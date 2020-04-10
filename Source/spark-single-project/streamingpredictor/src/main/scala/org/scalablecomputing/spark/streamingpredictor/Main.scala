package org.scalablecomputing.spark.streamingpredictor

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.connect.json.JsonDeserializer
import org.apache.spark._
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils}
import org.apache.spark.streaming.kafka010.LocationStrategies._
import com.datastax.spark.connector._
import org.apache.spark.sql.SparkSession
import org.scalablecomputing.spark.streamingpredictor.processing.SparkRunner

object Main {

  def setupSparkSession: SparkSession =
    sql.SparkSession.builder
      .appName("temperature-prediction")
      .master("local[*]")
      .config(sparkConfiguration)
      .getOrCreate()

  def main(args: Array[String]): Unit = {
    Console.println("Running time series analysis....");
    new SparkRunner(setupSparkSession,"kafka-1-service:9092,kafka-2-service:9092,kafka-3-service:9092", "temperatures-in").run()
  }

  private def sparkConfiguration = new SparkConf(true)
    .set("spark.cassandra.connection.host", "cassandra-service")
    .set("spark.cassandra.auth.username", "cassandra")
    .set("spark.cassandra.auth.password", "cassandra")
}
