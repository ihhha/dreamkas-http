package services

import akka.util.ByteString
import cats.syntax.either._
import models.dreamkas.ModelTypes.ErrorOr
import models.dreamkas.RawResponse
import models.dreamkas.commands.CommandT.{ETX, STX}
import models.dreamkas.commands.Ping.ASK
import models.dreamkas.errors.DreamkasError
import models.dreamkas.errors.DreamkasError._

object TerminalService {

  def processOut(data: ByteString): ErrorOr = {
    data.toArray match {
      case Array(STX, packetIndex, cmd1Byte, cmd2Byte, err1Byte, err2Byte, dataArray@_*) =>
        dataArray.takeRight(3) match {
          case Seq(ETX, crc1Byte, crc2Byte) =>
            val responseData = if (dataArray.size == 3) Array.emptyByteArray else dataArray.dropRight(3).toArray
            RawResponse(
              packetIndex, cmd1Byte, cmd2Byte, err1Byte, err2Byte, crc1Byte, crc2Byte, responseData
            ).result
          case _ => NoEtxFound.asLeft
        }
      case _ => UnknownFormat.asLeft
    }
  }

  def processPong(data: ByteString): ErrorOr = data.toArray match {
    case Array(el) if el == ASK.toByte => ().asRight[DreamkasError]
    case _ => PingFailed.asLeft[Unit]
  }
}
