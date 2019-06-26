package models.dreamkas.commands

import akka.util.ByteString

trait Command {

  def request(packetIndex: Int): ByteString

  val simpleResponse: Boolean

}

object Command {
  val PING = "05"

  val TURN_TO = "10"
  val FLAG_STATE = "00"
  val PRINTER_DATETIME = "13"
  val SET_DATETIME = "14"
  val REPORT_X = "20"
  val REPORT_Z = "21"
  val OPEN_SESSION = "23"
  val PAPER_CUT = "34"

  val DOCUMENT_OPEN = "30"
  val DOCUMENT_CLOSE = "31"
  val DOCUMENT_CANCEL = "32"
  val DOCUMENT_PRINT_TEXT = "40"
  val DOCUMENT_ADD_POSITION = "42"
  val DOCUMENT_ADD_DISCOUNT = "45"
  val DOCUMENT_TOTAL = "64"
  val DOCUMENT_SUB_TOTAL = "44"
  val DOCUMENT_PAYMENT = "47"
}