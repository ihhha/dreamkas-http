package models.dreamkas.commands

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
  ticket: Ticket, taxMode: TaxMode, paymentMode: PaymentMode, quantity: Int = 1
)(implicit val password: Password) extends Command {
  val name: String = ticket.showName
  val barCode: String = ""
  val price: Float = ticket.price.toCents
  val discount: Float = ticket.discount.toCents

  private val data = name.toCp866Bytes ++ FSArray ++
    barCode.toByteArray ++ FSArray ++
    quantity.byteArray ++ FSArray ++
    price.byteArray ++ FSArray ++
    TaxMode.toDreamkas(taxMode).toByteArray ++ FSArray ++
    discount.byteArray ++ FSArray ++
    PaymentMode.toDreamkas(paymentMode).toByteArray ++ FSArray ++
    GoodPropAttribute.toDreamkas(ServiceDescirption).toByteArray

  override def request(packetIndex: Int): ByteString =
    CommandMain(Command.DOCUMENT_ADD_POSITION, data).request(packetIndex)
}
