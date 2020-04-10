package org.scalablecomputing.spark.streamingaverager.sources

import org.apache.spark.sql.cassandra._
import org.apache.spark.sql.{Dataset, SparkSession}
import org.scalablecomputing.spark.streamingaverager.model.StationAverages


class CassandraAveragesImport(spark: SparkSession) {
  import spark.implicits._

  def dataframe: Dataset[StationAverages] = spark.read
    .format("org.apache.spark.sql.cassandra")
    .cassandraFormat("averages", "temperatures")
    .load()
    .as[StationAverages]
}
