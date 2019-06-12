package models.dreamkas.commands

import models.dreamkas.Password

trait CommandT {
  def toRequest(packetIndex: Int)(implicit password: Password): Array[Byte]
}
