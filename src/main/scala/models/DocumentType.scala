package models

import utils.CustomEnum

object DocumentType extends CustomEnum {
  type DocumentType = Value

  val Service: DocumentType = Value("service")
  val Payment: DocumentType = Value("payment")
  val Refund: DocumentType = Value("refund")
  val Income: DocumentType = Value("income")
  val Outcome: DocumentType = Value("outcome")
  val Buying: DocumentType = Value("buying")
  val BuyingRefund: DocumentType = Value("buying_refund")

  private val dreamkasBitsMap: Map[DocumentType, Seq[Int]] = Map(
    Service -> Seq(0),            // 1
    Payment -> Seq(1),            // 2
    Refund -> Seq(0, 1),          // 3
    Income -> Seq(2),             // 4
    Outcome -> Seq(0, 2),         // 5
    Buying -> Seq(1, 2),          // 6
    BuyingRefund -> Seq(0, 1, 2)  // 7
  )

  def toDreamkas(paymentMethod: DocumentType): Seq[Int] = dreamkasBitsMap.getOrElse(paymentMethod, Seq(0))
}
