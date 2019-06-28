package models.dreamkas.commands

import akka.util.ByteString
import models.dreamkas.Password

final case class DocumentCancel(pass: Password) extends Command {
  override val simpleResponse: Boolean = false
  implicit val password: Password = pass

  override def request(packetIndex: Int): ByteString = CommandSimple(Command.DOCUMENT_CANCEL).request(packetIndex)
}
