package models.dreamkas.commands

import java.time.format.DateTimeFormatter

import akka.util.ByteString
import models.GoodPropAttribute.ServiceDescirption
import models.PaymentMode.PaymentMode
import models.TaxMode.TaxMode
import models.api.Ticket
import models.dreamkas.Password
import models.dreamkas.commands.CommandT.FSArray
import models.{GoodPropAttribute, PaymentMode, TaxMode}
import utils.helpers.NumericHelper.{FloatExtended, IntExtended, LongExtended}
import utils.helpers.StringHelper.StringExt

final case class DocumentAddPosition(
  ticket: Ticket,
  taxMode: TaxMode,
  paymentMode: PaymentMode,
  quantity: Int = 1
)(implicit val password: Password) extends Command {

  private val name: String = s"${ticket.perfDate} ${ticket.perfTime} [${ticket.hall}] ${ticket.showName}".take(56)
  private val barCode: String = s"${ticket.series}${ticket.number}".take(18)
  private val price: Float = ticket.price.toCents
  private val discountType = if (ticket.discount.isEmpty) Array.emptyByteArray else "2".toByteArray
  private val discount: Option[Float] = ticket.discount.map(_.amount.toCents)

  private val data = name.toCp866Bytes ++ FSArray ++
    barCode.toByteArray ++ FSArray ++
    quantity.byteArray ++ FSArray ++
    price.byteArray ++ FSArray ++
    TaxMode.toDreamkas(taxMode).toByteArray ++ FSArray ++
    FSArray ++
    FSArray ++
    discountType ++ FSArray ++
    FSArray ++
    discount.map(_.byteArray ++ FSArray).getOrElse(FSArray) ++
    PaymentMode.toDreamkas(paymentMode).toByteArray ++ FSArray ++
    GoodPropAttribute.toDreamkas(ServiceDescirption).toByteArray

  override def request(packetIndex: Int): ByteString =
    CommandMain(Command.DOCUMENT_ADD_POSITION, data).request(packetIndex)
}
