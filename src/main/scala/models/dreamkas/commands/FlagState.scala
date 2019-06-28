package models.dreamkas.commands

import akka.util.ByteString
import models.dreamkas.Password

final case class FlagState(pass: Password) extends Command {

  implicit val password: Password = pass

  override val simpleResponse: Boolean = false

  override def request(packetIndex: Int): ByteString = CommandSimple(Command.FLAG_STATE).request(packetIndex)
}
