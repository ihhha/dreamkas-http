package models.api

import play.api.libs.json.{Json, Reads}
import models.DocumentType.DocumentType
import models.PaymentMode.PaymentMode
import models.PaymentType.PaymentType
import models.TaxMode.TaxMode

case class Receipt(
  ticket: Ticket,
  quantity: Int,
  taxMode: TaxMode,
  checkId: Int,
  cashier: Option[Cashier],
  paymentType: PaymentType,
  paymentMode: PaymentMode,
  documentType: DocumentType
)

trait ReceiptJson {
  implicit val reads: Reads[Receipt] = Json.reads[Receipt]
}

object Receipt extends ReceiptJson
