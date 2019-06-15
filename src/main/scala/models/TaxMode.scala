package models

import utils.CustomEnum

object TaxMode extends CustomEnum {

  type TaxMode = Value

  val Default: TaxMode = Value("default")
  val SimpleIncome: TaxMode = Value("simple_income")
  val SimpleIncomeOutcome: TaxMode = Value("simple_income_outcome")
  val TemporaryIncome: TaxMode = Value("temporary_income")
  val Patent: TaxMode = Value("patent")
  val Esn: TaxMode = Value("esn")

  private val dreamkasMap: Map[TaxMode, String] = Map(
    Default -> "0",
    SimpleIncome -> "1",
    SimpleIncomeOutcome -> "2",
    TemporaryIncome -> "3",
    Patent -> "5",
    Esn -> "4"
  )

  def toDreamkas(taxMode: TaxMode): String = dreamkasMap.getOrElse(taxMode, "0")
}
