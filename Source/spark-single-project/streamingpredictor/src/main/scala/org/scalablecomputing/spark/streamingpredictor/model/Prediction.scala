package org.scalablecomputing.spark.streamingpredictor.model

import java.sql.Timestamp

case class Prediction(station: Int, lastYearDate: scala.Option[Timestamp], lastYearTemperature: scala.Option[Int], newDate: Timestamp, newHour: Int, newTemperature: Int)
