package models.dreamkas

import scala.collection.BitSet

import models.dreamkas.DocumentTypeMode._
import utils.helpers.StringHelper.StringExt

case class DocumentTypeMode(documentType: Int, packet: Boolean, postponePrint: Boolean) {

  private val packetBit = if (packet) BitSet(PACKET_BIT) else BitSet.empty
  private val postponePrintBit = if (postponePrint) BitSet(POSTPONE_PRINT) else BitSet.empty

  val bit: Array[Byte] = (BitSet(documentType) ++ packetBit ++ postponePrintBit).toBitMask(0).toString.toByteArray

}

case object DocumentTypeMode {
  val SERVICE_DOCUMENT = 0
  val PAYMENT = 1
  val REFUND = 2
  val INCOME = 3
  val OUTCOME = 4
  val BUYING = 5
  val BUYING_REFUND = 6

  val PACKET_BIT = 4
  val POSTPONE_PRINT = 5

}