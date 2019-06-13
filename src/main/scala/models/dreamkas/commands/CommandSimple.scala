package models.dreamkas.commands

import akka.util.ByteString
import models.dreamkas.ModelTypes.Code
import models.dreamkas.Password
import models.dreamkas.commands.CommandT.{ETX, STX}
import utils.helpers.ArrayByteHelper._

case class CommandSimple(
  code: Code
) extends CommandT {

  override def request(packetIndex: Int)(implicit password: Password): ByteString = {
    val msg = (password.bytes :+ packetIndex.toByte) ++ code.map(_.toByte) :+ ETX.toByte
    val crc = msg.toCrc.map(_.toByte).toArray
    ByteString((STX.toByte +: msg) ++ crc)
  }
}
