package models.dreamkas.commands

import akka.util.ByteString
import models.dreamkas.Password

trait CommandT {
  def request(packetIndex: Int)(implicit password: Password): ByteString
}
