package models.dreamkas.commands

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalTime}

import models.dreamkas.Password

object CommandBuilder {

  def openSession(cashier: Option[String] = None): CommandMain = {
    val data = List(cashier).flatten
    CommandMain(OPEN_SESSION, data)
  }

  def reportX(cashier: Option[String] = None): CommandMain = {
    val data = List(cashier).flatten
    CommandMain(REPORT_X, data)
  }

  def reportZ(cashier: Option[String] = None): CommandMain = {
    val data = List(cashier).flatten
    CommandMain(REPORT_Z, data)
  }

  def turnTo(date: LocalDate, time: LocalTime): CommandMain = {
    val dataString = DateTimeFormatter.ofPattern("ddMMyy").format(date)
    val timeString = DateTimeFormatter.ofPattern("HHmmss").format(time)

    val data = List(dataString, timeString)

    CommandMain(TURN_TO, data)
  }

  def setDateTime(date: LocalDate, time: LocalTime): CommandMain = {
    val dataString = DateTimeFormatter.ofPattern("ddMMyy").format(date)
    val timeString = DateTimeFormatter.ofPattern("HHmmss").format(time)

    val data = List(dataString, timeString)

    CommandMain(SET_DATETIME, data)
  }

  def flagState = CommandMain(FLAG_STATE)

  def printerDateTime = CommandMain(PRINTER_DATETIME)

  val TURN_TO = "10"
  val FLAG_STATE = "00"
  val PRINTER_DATETIME = "13"
  val SET_DATETIME = "14"
  val REPORT_X = "20"
  val REPORT_Z = "21"
  val OPEN_SESSION = "23"
}
