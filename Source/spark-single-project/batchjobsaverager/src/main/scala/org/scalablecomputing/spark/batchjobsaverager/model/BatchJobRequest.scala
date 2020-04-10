package org.scalablecomputing.spark.batchjobsaverager.model

import java.sql.Timestamp

case class BatchJobRequest(station: Int, startDate: Timestamp, endDate: Timestamp)
