package models.dreamkas.commands

import akka.util.ByteString
import models.dreamkas.ModelTypes.Code
import models.dreamkas.Password

trait CommandBase extends CommandT {

  val code: Code

  def request(packetIndex: Int)(implicit password: Password) = ByteString(Array(code.toByte))
}
