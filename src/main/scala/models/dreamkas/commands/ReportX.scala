package models.dreamkas.commands

import akka.util.ByteString
import models.api.Cashier
import models.dreamkas.Password

final case class ReportX(cashier: Option[Cashier] = None)(implicit val password: Password) extends Command {

  private val data = cashier.map(_.dreamkasData).getOrElse(Array.emptyByteArray)

  override def request(packetIndex: Int): ByteString = CommandMain(Command.REPORT_X, data).request(packetIndex)

}
