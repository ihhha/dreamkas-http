package models.dreamkas.commands

import akka.util.ByteString
import models.dreamkas.ModelTypes.Code
import models.dreamkas.Password
import models.dreamkas.commands.CommandT.{ETX, STX}
import utils.helpers.ArrayByteHelper._

case class CommandMain(
  code: Code,
  data: Array[Byte]
) extends CommandT {

  def request(packetIndex: Int)(implicit password: Password): ByteString = {
    val msg = (password.bytes :+ packetIndex.toByte) ++ code.map(_.toByte) ++ data :+ ETX.toByte
    val crc = msg.toCrc.map(_.toByte).toArray.takeRight(2)
    ByteString((STX.toByte +: msg) ++ crc)
  }
}
