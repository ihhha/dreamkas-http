package models.dreamkas.commands

import akka.util.ByteString
import models.GoodPropAttribute.ServiceDescirption
import models.api.Receipt
import models.dreamkas.Password
import models.dreamkas.commands.CommandT.FSArray
import models.{GoodPropAttribute, PaymentMode, TaxMode}
import utils.helpers.NumericHelper.{FloatExtended, IntExtended, LongExtended}
import utils.helpers.StringHelper.StringExt

final case class DocumentAddPosition(
  receipt: Receipt
)(implicit val password: Password) extends Command {
  val name: String = receipt.ticket.showName
  val barCode: String = ""
  val price: Float = receipt.ticket.price.toCents
  val discount: Float = receipt.ticket.discount.toCents

  private val data = name.toCp866Bytes ++ FSArray ++
    barCode.toByteArray ++ FSArray ++
    receipt.quantity.byteArray ++ FSArray ++
    price.byteArray ++ FSArray ++
    TaxMode.toDreamkas(receipt.taxMode).toByteArray ++ FSArray ++
    discount.byteArray ++ FSArray ++
    PaymentMode.toDreamkas(receipt.paymentMode).toByteArray ++ FSArray ++
    GoodPropAttribute.toDreamkas(ServiceDescirption).toByteArray

  override def request(packetIndex: Int): ByteString =
    CommandMain(Command.DOCUMENT_ADD_POSITION, data).request(packetIndex)
}
