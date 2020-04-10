package org.scalablecomputing.spark.batchjobsaverager

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.connect.json.JsonDeserializer
import org.apache.spark._
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils}
import org.apache.spark.streaming.kafka010.LocationStrategies._
import com.datastax.spark.connector._
import org.apache.spark.sql.SparkSession
import org.scalablecomputing.spark.batchjobsaverager.processing.SparkRunner

object Main {

  def setupSparkSession(timeframe: String): SparkSession =
    sql.SparkSession.builder
      .appName("temperature-batch-" + timeframe)
      .master("local[*]")
      .config(sparkConfiguration)
      .getOrCreate()

  def main(args: Array[String]): Unit = {
    if(args.length != 1) {
      System.err.println("Invalid arguments! Only one argument is expected: daily/weekly/monthly")
      System.err.println("Provided arguments: ")
      args.foreach(arg => System.err.println(arg))
      return
    };

    if(args(0) != "daily" && args(0) != "weekly" && args(0) != "monthly" && args(0) != "all") {
      System.err.println("Argument value is invalid! Must be: daily, weekly or monthly")
      System.err.println("Provided arguments: ")
      args.foreach(arg => System.err.println(arg))
      return
    }

    new SparkRunner(setupSparkSession(args(0)),"kafka-1-service:9092,kafka-2-service:9092,kafka-3-service:9092",
      args(0)).run()
  }

  private def sparkConfiguration = new SparkConf(true)
    .set("spark.cassandra.connection.host", "cassandra-service")
    .set("spark.cassandra.connection.port", "9042")
    .set("spark.cassandra.auth.username", "cassandra")
    .set("spark.cassandra.auth.password", "cassandra")
}
