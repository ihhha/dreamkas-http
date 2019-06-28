package models.dreamkas.commands

import akka.util.ByteString
import models.api.Cashier
import models.dreamkas.Password

final case class OpenSession(pass: Password, cashier: Option[Cashier] = None) extends Command {

  implicit val password: Password = pass

  override val simpleResponse: Boolean = false

  private val data = cashier.map(_.dreamkasData).getOrElse(Array.emptyByteArray)

  override def request(packetIndex: Int): ByteString = CommandMain(Command.OPEN_SESSION, data).request(packetIndex)

}
