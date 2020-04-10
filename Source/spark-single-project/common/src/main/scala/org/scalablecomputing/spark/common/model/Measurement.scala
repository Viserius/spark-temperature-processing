package org.scalablecomputing.spark.common.model

import java.sql.Timestamp


case class Measurement(station: Int, date: Timestamp, hour: Int, temperature: Int)
