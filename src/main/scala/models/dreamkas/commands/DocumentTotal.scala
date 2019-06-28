package models.dreamkas.commands

import akka.util.ByteString
import models.dreamkas.Password
import utils.helpers.NumericHelper.FloatExtended

final case class DocumentTotal(amount: Long, pass: Password) extends Command {

  implicit val password: Password = pass

  override val simpleResponse: Boolean = false

  private val data = amount.byteArray

  override def request(packetIndex: Int): ByteString =
    CommandMain(Command.DOCUMENT_TOTAL, data).request(packetIndex)

}
