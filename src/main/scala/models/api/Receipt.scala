package models.api

import play.api.libs.json.{Json, Reads}
import models.DocumentType.DocumentType
import models.PaymentMode.PaymentMode
import models.PaymentType.PaymentType
import models.TaxMode.TaxMode

case class Receipt(
  tickets: List[Ticket],
  quantity: Int,
  taxMode: TaxMode,
  checkId: Int,
  cashier: Option[Cashier],
  paymentType: PaymentType,
  paymentMode: PaymentMode,
  documentType: DocumentType
) {
  val amount: Long = {
    val (amount, totalDiscount) = tickets.foldLeft((0L, 0L)) {
      case ((currAmount, currTotalDiscount), ticket) => (currAmount + ticket.price, currTotalDiscount + ticket.discount)
    }
    amount - totalDiscount
  }
}

trait ReceiptJson {
  implicit val reads: Reads[Receipt] = Json.reads[Receipt]
}

object Receipt extends ReceiptJson
