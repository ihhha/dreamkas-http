package models.dreamkas.commands

import akka.util.ByteString
import models.TaxMode.TaxMode
import models.api.Cashier
import models.dreamkas.{DocumentTypeMode, DreamkasTaxMode, Password}
import utils.helpers.StringHelper.StringExt

final case class DocumentOpen(
  mode: DocumentTypeMode,
  cashier: Option[Cashier],
  number: Int,
  taxMode: TaxMode
)(implicit val password: Password) extends Command {

  private val data = mode.bit ++
    Array(number.toByte) ++
    DreamkasTaxMode(taxMode).Code.toByteArray ++
    cashier.map(_.dreamkasData).getOrElse(Array.emptyByteArray)

  override def request(packetIndex: Int): ByteString = CommandMain(Command.DOCUMENT_OPEN, data).request(packetIndex)

}
