package models.dreamkas.commands

import akka.util.ByteString

trait Command {

  def request(packetIndex: Int): ByteString

}

object Command {
  val TURN_TO = "10"
  val FLAG_STATE = "00"
  val PRINTER_DATETIME = "13"
  val SET_DATETIME = "14"
  val REPORT_X = "20"
  val REPORT_Z = "21"
  val OPEN_SESSION = "23"

  val PRINT_TEXT = "40"
}