package models.dreamkas

import akka.util.ByteString
import cats.syntax.either._
import models.dreamkas.DreamkasError.{NoEtxFound, UnknownFormat}
import models.dreamkas.commands.CommandMainT._

case class Out(data: ByteString) {
  val dump: Either[DreamkasError, RawResponse] = data.toArray match {
    case Array(STX, packetIndex, cmd1Byte, cmd2Byte, err1Byte, err2Byte, dataArray@_*) =>
      dataArray.takeRight(3) match {
        case Seq(ETX, crc1Byte, crc2Byte) =>
          val responseData = if (dataArray.size == 3) Array.emptyByteArray else dataArray.dropRight(3).toArray
          RawResponse(
          packetIndex, cmd1Byte, cmd2Byte, err1Byte, err2Byte, crc1Byte, crc2Byte, responseData
        ).asRight[DreamkasError]
        case _ => NoEtxFound.asLeft[RawResponse]
      }
    case _ => UnknownFormat.asLeft[RawResponse]
  }
}
