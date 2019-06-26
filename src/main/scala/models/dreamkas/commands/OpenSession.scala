package models.dreamkas.commands

import akka.util.ByteString
import models.api.Cashier
import models.dreamkas.Password

final case class OpenSession(cashier: Option[Cashier] = None)(implicit val password: Password) extends Command {

  override val simpleResponse: Boolean = false

  private val data = cashier.map(_.dreamkasData).getOrElse(Array.emptyByteArray)

  override def request(packetIndex: Int): ByteString = CommandMain(Command.OPEN_SESSION, data).request(packetIndex)

}
