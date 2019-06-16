package models

import utils.CustomEnum

object PaymentMode extends CustomEnum {
  type PaymentMode = Value

  val FullPrepayment: PaymentMode = Value("full_prepayment")
  val Prepayment: PaymentMode = Value("prepayment")
  val Advance: PaymentMode = Value("advance")
  val FullPayment: PaymentMode = Value("full_payment")
  val PartialPayment: PaymentMode = Value("partial_payment")
  val Credit: PaymentMode = Value("credit")
  val CreditPayment: PaymentMode = Value("credit_payment")

  private val dreamkasMap: Map[PaymentMode, String] = Map(
    FullPrepayment -> "1",
    Prepayment -> "2",
    Advance -> "3",
    FullPayment -> "4",
    PartialPayment -> "5",
    Credit -> "6",
    CreditPayment -> "7"
  )

  def toDreamkas(paymentMethod: PaymentMode): String = dreamkasMap.getOrElse(paymentMethod, "4")
}
