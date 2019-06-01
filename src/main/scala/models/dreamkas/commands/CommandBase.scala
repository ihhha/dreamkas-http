package models.dreamkas.commands

import models.dreamkas.ModelTypes.Code

trait CommandBase {

  val code: Code

  val toRequest: Array[Byte] = Array(code.toByte)
}
