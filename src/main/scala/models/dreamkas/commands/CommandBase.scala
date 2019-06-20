package models.dreamkas.commands

import akka.util.ByteString
import models.dreamkas.ModelTypes.Code
import models.dreamkas.Password

case class CommandBase(code: Code) extends CommandT {
  def request(packetIndex: Int)(implicit password: Password) = ByteString(Array(code.toByte))
}
