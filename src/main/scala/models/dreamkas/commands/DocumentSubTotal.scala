package models.dreamkas.commands

import akka.util.ByteString
import models.dreamkas.Password

final case class DocumentSubTotal(implicit val password: Password) extends Command {

  override val simpleResponse: Boolean = false

  override def request(packetIndex: Int): ByteString =
    CommandSimple(Command.DOCUMENT_SUB_TOTAL).request(packetIndex)

}
