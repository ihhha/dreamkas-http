package models.dreamkas.commands

import akka.util.ByteString
import models.PaymentType
import models.api.Receipt
import models.dreamkas.Password
import models.dreamkas.commands.CommandT.FSArray
import utils.helpers.NumericHelper.{FloatExtended, IntExtended, LongExtended}
import utils.helpers.StringHelper.{EMPTY_STRING, _}

final case class DocumentPayment(
  receipt: Receipt
)(implicit val password: Password) extends Command {

  val amount: Float = (receipt.quantity * receipt.ticket.price).toCents
  val text: String = EMPTY_STRING

  private val data = PaymentType.toDreamkas(receipt.paymentType).byteArray ++ FSArray ++
    amount.byteArray ++ FSArray ++
    text.toCp866Bytes

  override def request(packetIndex: Int): ByteString = CommandMain(Command.DOCUMENT_PAYMENT, data).request(packetIndex)

}
