package models.dreamkas

import cats.syntax.either._
import models.dreamkas.commands.CommandT.ETX
import models.dreamkas.errors.DreamkasError._
import models.dreamkas.errors.{DreamkasError, ErrorWithCode}
import utils.Logging
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
) extends Logging {

  val result: Either[DreamkasError, Unit] = {

    val codesArray = Array(packetIndex, cmd1Byte, cmd2Byte, err1Byte, err2Byte).map(_.toByte)
    val etxArray = Array(ETX.toByte)

    val isCrcCorrect: Boolean =
      (codesArray ++ dataArray ++ etxArray).toCrc == Array(crc1byte.toByte, crc2byte.toByte).toUtf8String

    if (isCrcCorrect) {
      (err1Byte, err2Byte).toCode match {
        case NO_ERROR => ().asRight[DreamkasError]
        case code => ErrorWithCode(code, packetIndex).asLeft
      }
    } else {
      CrcError.asLeft
    }
  }
}
