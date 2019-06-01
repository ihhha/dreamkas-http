package models.dreamkas.commands

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalTime}

import models.dreamkas.Password

object CommandBuilder {

  def openSession(cashier: Option[String] = None)(implicit password: Password, packetIndex: Int): CommandMain = {
    val data = List(cashier).flatten
    CommandMain(OPEN_SESSION, password, packetIndex, data)
  }

  def reportX(cashier: Option[String] = None)(implicit password: Password, packetIndex: Int): CommandMain = {
    val data = List(cashier).flatten
    CommandMain(REPORT_X, password, packetIndex, data)
  }

  def reportZ(cashier: Option[String] = None)(implicit password: Password, packetIndex: Int): CommandMain = {
    val data = List(cashier).flatten
    CommandMain(REPORT_Z, password, packetIndex, data)
  }

  def turnTo(date: LocalDate, time: LocalTime)(implicit password: Password, packetIndex: Int): CommandMain = {
    val dataString = DateTimeFormatter.ofPattern("ddMMyy").format(date)
    val timeString = DateTimeFormatter.ofPattern("HHmmss").format(time)

    val data = List(dataString, timeString)

    CommandMain(TURN_TO, password, packetIndex, data)
  }

  def setDateTime(date: LocalDate, time: LocalTime)(implicit password: Password, packetIndex: Int): CommandMain = {
    val dataString = DateTimeFormatter.ofPattern("ddMMyy").format(date)
    val timeString = DateTimeFormatter.ofPattern("HHmmss").format(time)

    val data = List(dataString, timeString)

    CommandMain(SET_DATETIME, password, packetIndex, data)
  }

  def flagState(implicit password: Password, packetIndex: Int) = CommandMain(FLAG_STATE, password, packetIndex)

  def printerDateTime(implicit password: Password, packetIndex: Int) = CommandMain(PRINTER_DATETIME, password, packetIndex)

  val TURN_TO = "10"
  val FLAG_STATE = "00"
  val PRINTER_DATETIME = "13"
  val SET_DATETIME = "14"
  val REPORT_X = "20"
  val REPORT_Z = "21"
  val OPEN_SESSION = "23"
}
