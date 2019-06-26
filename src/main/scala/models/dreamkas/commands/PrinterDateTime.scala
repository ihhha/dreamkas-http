package models.dreamkas.commands

import akka.util.ByteString
import models.dreamkas.Password

final case class PrinterDateTime(implicit val password: Password) extends Command {
  override val simpleResponse: Boolean = false

  override def request(packetIndex: Int): ByteString = CommandSimple(Command.PRINTER_DATETIME).request(packetIndex)
}
