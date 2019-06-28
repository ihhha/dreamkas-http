package models.dreamkas.commands

import akka.util.ByteString
import models.dreamkas.Password

final case class DocumentSubTotal(pass: Password) extends Command {

  implicit val password: Password = pass

  override val simpleResponse: Boolean = false

  override def request(packetIndex: Int): ByteString =
    CommandSimple(Command.DOCUMENT_SUB_TOTAL).request(packetIndex)

}
