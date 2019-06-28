package models.dreamkas.commands

import akka.util.ByteString
import models.PaymentType
import models.api.Receipt
import models.dreamkas.Password
import models.dreamkas.commands.CommandT.FSArray
import utils.helpers.NumericHelper.{FloatExtended, IntExtended, LongExtended}
import utils.helpers.StringHelper.{EMPTY_STRING, _}

final case class DocumentPayment(
  receipt: Receipt, pass: Password
) extends Command {

  implicit val password: Password = pass

  override val simpleResponse: Boolean = false

  val amount: Float = receipt.amount.toCents
  val text: String = EMPTY_STRING

  private val data = PaymentType.toDreamkas(receipt.paymentType).byteArray ++ FSArray ++
    amount.byteArray ++ FSArray ++
    text.toCp866Bytes

  override def request(packetIndex: Int): ByteString = CommandMain(Command.DOCUMENT_PAYMENT, data).request(packetIndex)

}
