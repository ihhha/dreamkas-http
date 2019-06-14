package models.dreamkas.commands

import akka.util.ByteString
import models.dreamkas.Password

trait CommandT {
  def request(packetIndex: Int)(implicit password: Password): ByteString
}

object CommandT {
  val STX = 0x02
  val ETX = 0x03
  val FS = 0x1C

  val FSArray = Array(FS.toByte)
}