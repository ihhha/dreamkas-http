package models

import utils.CustomEnum

object PaymentType extends CustomEnum {
  type PaymentType = Value

  val Cash: PaymentType = Value("cash")
  val Cashless: PaymentType = Value("cashless")

  private val dreamkasMap: Map[PaymentType, Int] = Map(
    Cash -> 0,
    Cashless -> 1
  )

  def toDreamkas(paymentMethod: PaymentType): Int = dreamkasMap.getOrElse(paymentMethod, 0)
}
