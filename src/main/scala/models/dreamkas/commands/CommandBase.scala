package models.dreamkas.commands

import akka.util.ByteString
import models.dreamkas.ModelTypes.Code

case class CommandBase(code: Code) extends CommandT {
  def request(packetIndex: Int) = ByteString(Array(code.toByte))
}
