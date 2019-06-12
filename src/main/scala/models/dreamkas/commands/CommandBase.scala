package models.dreamkas.commands

import models.dreamkas.ModelTypes.Code
import models.dreamkas.Password

trait CommandBase extends CommandT {

  val code: Code

  def toRequest(packetIndex: Int)(implicit password: Password) = Array(code.toByte)
}
