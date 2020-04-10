package org.scalablecomputing.spark.common.exports

import org.apache.spark.sql.{Dataset, SparkSession}

class CommonConsoleExport(spark: SparkSession, outputMode: String) {

  def export(dataset: Dataset[_]): Unit =
    dataset.writeStream
      .format("console")
      .outputMode(outputMode)
      .start()
      .awaitTermination()

}
