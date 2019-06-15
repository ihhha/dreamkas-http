package services

import akka.util.ByteString
import cats.syntax.either._
import models.dreamkas.RawResponse
import models.dreamkas.commands.CommandT.{ETX, STX}
import models.dreamkas.errors.DreamkasError
import models.dreamkas.errors.DreamkasError._

object TerminalService {
  def processOut(data: ByteString, commandIndex: Int): Either[DreamkasError, Unit] = {
    data.toArray match {
      case Array(STX, packetIndex, cmd1Byte, cmd2Byte, err1Byte, err2Byte, dataArray@_*) =>
        dataArray.takeRight(3) match {
          case Seq(ETX, crc1Byte, crc2Byte) =>
            val responseData = if (dataArray.size == 3) Array.emptyByteArray else dataArray.dropRight(3).toArray
            RawResponse(
              packetIndex, cmd1Byte, cmd2Byte, err1Byte, err2Byte, crc1Byte, crc2Byte, responseData
            ).result(commandIndex)
          case _ => NoEtxFound.asLeft
        }
      case _ => UnknownFormat.asLeft
    }
  }
}
