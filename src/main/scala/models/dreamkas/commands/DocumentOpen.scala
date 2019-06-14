package models.dreamkas.commands

import akka.util.ByteString
import models.TaxMode.TaxMode
import models.api.Cashier
import models.dreamkas.commands.CommandT.FS
import models.dreamkas.{DocumentTypeMode, Password}
import utils.helpers.StringHelper.StringExt
import utils.helpers.IntHelper.IntExtended

final case class DocumentOpen(
  mode: DocumentTypeMode,
  departmentNum: Int = 1,
  cashier: Option[Cashier],
  number: Int,
  taxMode: TaxMode
)(implicit val password: Password) extends Command {

  private val data = mode.bitString.toByteArray ++ Array(FS.toByte) ++
    departmentNum.toByteArray ++ Array(FS.toByte) ++
    //    List(DreamkasTaxMode(taxMode).Code).toDreamkasData ++
    cashier.map(_.dreamkasData).getOrElse(Array.emptyByteArray) ++ Array(FS.toByte) ++
    number.toByteArray

  override def request(packetIndex: Int): ByteString = CommandMain(Command.DOCUMENT_OPEN, data).request(packetIndex)

}
