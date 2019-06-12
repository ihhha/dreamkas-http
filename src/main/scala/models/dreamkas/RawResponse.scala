package models.dreamkas

import models.dreamkas.commands.CommandMainT.ETX
import models.dreamkas.errors.DreamkasError._
import models.dreamkas.errors.{DreamkasError, ErrorWithCode}
import utils.helpers.ArrayByteHelper._
import utils.helpers.TupleIntHelper._

case class RawResponse(
  packetIndex: Int,
  cmd1Byte: Int,
  cmd2Byte: Int,
  err1Byte: Int,
  err2Byte: Int,
  crc1byte: Int,
  crc2byte: Int,
  dataArray: Array[Byte] = Array.emptyByteArray
) {

  def result(index: Int): Option[DreamkasError] = {
    val isCrcCorrect: Boolean = Array(packetIndex, cmd1Byte, cmd2Byte, err1Byte, err2Byte, ETX)
      .map(_.toByte).toCrc == Array(crc1byte.toByte, crc2byte.toByte).toUtf8String

    if (isCrcCorrect) {
      if (packetIndex == index) {
        (err1Byte, err2Byte).toCode match {
          case NO_ERROR => None
          case code => Some(ErrorWithCode(code))
        }
      } else {
        Some(WrongPacketIndex)
      }
    }
    else {
      println(Array(packetIndex, cmd1Byte, cmd2Byte, err1Byte, err2Byte, ETX).map(_.toByte).toCrc)
      Some(CrcError)
    }
  }
}
