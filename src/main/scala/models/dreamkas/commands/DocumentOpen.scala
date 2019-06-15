package models.dreamkas.commands

import akka.util.ByteString
import models.TaxMode
import models.TaxMode.TaxMode
import models.api.Cashier
import models.dreamkas.commands.Command.DOCUMENT_OPEN
import models.dreamkas.commands.CommandT.FSArray
import models.dreamkas.{DocumentTypeMode, Password}
import utils.helpers.NumericHelper.IntExtended
import utils.helpers.StringHelper.StringExt

final case class DocumentOpen(
  mode: DocumentTypeMode,
  departmentNum: Int = 1,
  cashier: Option[Cashier],
  number: Int,
  taxMode: TaxMode
)(implicit val password: Password) extends Command {

  private val data = mode.bitString.toByteArray ++ FSArray ++
    departmentNum.byteArray ++ FSArray ++
    TaxMode.toDreamkas(taxMode).toByteArray ++
    cashier.map(_.dreamkasData).getOrElse(Array.emptyByteArray) ++ FSArray ++
    number.byteArray

  override def request(packetIndex: Int): ByteString = CommandMain(DOCUMENT_OPEN, data).request(packetIndex)

}
