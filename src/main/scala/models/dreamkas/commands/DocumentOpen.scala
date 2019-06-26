package models.dreamkas.commands

import akka.util.ByteString
import models.TaxMode
import models.TaxMode.TaxMode
import models.TaxMode.Default
import models.api.Cashier
import models.dreamkas.commands.Command.DOCUMENT_OPEN
import models.dreamkas.commands.CommandT.FSArray
import models.dreamkas.{DocumentTypeMode, Password}
import utils.helpers.NumericHelper.IntExtended
import utils.helpers.StringHelper.StringExt

final case class DocumentOpen(
  typeMode: DocumentTypeMode,
  departmentNum: Int = 1,
  cashier: Option[Cashier] = None,
  number: Int = 0,
  taxMode: TaxMode = Default
)(implicit val password: Password) extends Command {

  override val simpleResponse: Boolean = false

  private val data = typeMode.bitString.toByteArray ++ FSArray ++
    departmentNum.byteArray ++ FSArray ++
    cashier.map(_.dreamkasData).getOrElse(Array.emptyByteArray) ++ FSArray ++
    number.byteArray ++ FSArray ++
    TaxMode.toDreamkas(taxMode).toByteArray

  override def request(packetIndex: Int): ByteString = CommandMain(DOCUMENT_OPEN, data).request(packetIndex)

}
