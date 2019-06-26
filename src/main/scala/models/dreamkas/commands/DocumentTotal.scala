package models.dreamkas.commands

import akka.util.ByteString
import models.dreamkas.Password
import utils.helpers.NumericHelper.FloatExtended

final case class DocumentTotal(
  amount: Long
)(implicit val password: Password) extends Command {

  override val simpleResponse: Boolean = false

  private val data = amount.byteArray

  override def request(packetIndex: Int): ByteString =
    CommandMain(Command.DOCUMENT_TOTAL, data).request(packetIndex)

}
