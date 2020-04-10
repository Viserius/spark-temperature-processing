package org.scalablecomputing.spark.batchjobsaverager.model

import java.sql.Timestamp

case class BatchJobAverage(station: Int, date: Timestamp, temperature: Double)
