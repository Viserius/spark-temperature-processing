package org.scalablecomputing.spark.common.exports

import org.apache.spark.sql.cassandra._
import org.apache.spark.sql.streaming.OutputMode
import org.apache.spark.sql.{Dataset, SaveMode, SparkSession}


class CassandraExport(spark: SparkSession, tableName : String) {

  def export(dataset: Dataset[_]): Unit = {
    dataset.writeStream
      .foreachBatch( (batchDF: Dataset[_], batchId: Long) =>
        batchDF.write
          .cassandraFormat(tableName, "temperatures")
          .mode(SaveMode.Append)
          .save()
      )
      .outputMode(OutputMode.Update())
      .start()
      .awaitTermination()
  }
}
