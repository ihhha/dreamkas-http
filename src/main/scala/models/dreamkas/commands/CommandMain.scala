package models.dreamkas.commands

import models.dreamkas.ModelTypes.Code
import models.dreamkas.Password

case class CommandMain(
  override val code: Code,
  override val password: Password,
  override val packetIndex: Int,
  override val data: List[String] = List.empty[String]
) extends CommandMainT
