package models.dreamkas.commands

import akka.util.ByteString
import models.dreamkas.Password

final case class ReportZ(cashier: Option[String] = None)(implicit val password: Password) extends Command {

  private val data = List(cashier).flatten

  override def request(packetIndex: Int): ByteString = CommandMain(Command.REPORT_Z, data).request(packetIndex)

}