package models.dreamkas.commands

import akka.util.ByteString
import models.api.Discount
import models.dreamkas.Password
import models.dreamkas.commands.CommandT.FSArray
import utils.helpers.NumericHelper.{FloatExtended, IntExtended, LongExtended}
import utils.helpers.StringHelper.StringExt

final case class DocumentAddDiscount(
  discount: Discount
)(implicit val password: Password) extends Command {

  private val discountType = 1.byteArray

  private val data = discountType ++ FSArray ++
    discount.name.toCp866Bytes ++ FSArray ++
    discount.amount.toCents.byteArray

  override def request(packetIndex: Int): ByteString =
    CommandMain(Command.DOCUMENT_ADD_DISCOUNT, data).request(packetIndex)
}
