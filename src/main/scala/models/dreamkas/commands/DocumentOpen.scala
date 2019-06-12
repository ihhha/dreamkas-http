package models.dreamkas.commands

import akka.util.ByteString
import models.TaxMode.TaxMode
import models.dreamkas.{DocumentTypeMode, DreamkasTaxMode, Password}
import utils.helpers.StringHelper.EMPTY_STRING

final case class DocumentOpen(
  mode: DocumentTypeMode,
  cashierName: Option[String],
  number: Int,
  taxMode: TaxMode
)(implicit val password: Password) extends Command {

  private val cashierNameCP866 = new String(cashierName.getOrElse(EMPTY_STRING).getBytes, "cp866")

  private val data = List(mode.bit, cashierNameCP866, number.toString, DreamkasTaxMode(taxMode).Code)

  override def request(packetIndex: Int): ByteString = CommandMain(Command.DOCUMENT_OPEN, data).request(packetIndex)

}
