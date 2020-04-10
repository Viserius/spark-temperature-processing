package org.scalablecomputing.spark.streamingpredictor.model

import java.sql.Timestamp

case class PredictionReducable(station: Int, lastYearTemperature: scala.Option[Int],
                               newDate: Timestamp, newHour: Int, newTemperature: Int, lagIdx : Int,
                               historicTemperature : Double, historicNumRecords : BigInt, predictedHour: Int,
                               predictedDate : Timestamp, predictedTemperature : Double, totalWeightSum : Double)
