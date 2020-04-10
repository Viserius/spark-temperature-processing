package org.scalablecomputing.spark.batchjobsaverager.model

import java.sql.Timestamp

case class MeasurementReducable(station: Int, date: Timestamp, endDate: Timestamp, temperature: Double, totalSummed: Int)
