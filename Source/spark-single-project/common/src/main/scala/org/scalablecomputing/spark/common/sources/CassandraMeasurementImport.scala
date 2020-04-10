package org.scalablecomputing.spark.common.sources

import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}
import org.apache.spark.sql.streaming.{DataStreamWriter, OutputMode}
import com.datastax.spark.connector._
import com.datastax.spark.connector.streaming._
import org.apache.spark.sql.cassandra._
import org.scalablecomputing.spark.common.model.Measurement


class CassandraMeasurementImport(spark: SparkSession) {
  import spark.implicits._

  def dataframe: Dataset[Measurement] = spark.read
    .format("org.apache.spark.sql.cassandra")
    .cassandraFormat("measurements", "temperatures")
    .load()
    .as[Measurement]
}
