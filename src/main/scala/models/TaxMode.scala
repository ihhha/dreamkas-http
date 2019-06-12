package models

object TaxMode extends Enumeration {
  type TaxMode = Value

  val Default: TaxMode = Value("default")
  val SimpleIncome: TaxMode = Value("simple_income")
  val SimpleIncomeOutcome: TaxMode = Value("simple_income_outcome")
  val TemporaryIncome: TaxMode = Value("temporary_income")
  val Patent: TaxMode = Value("patent")
  val Esn: TaxMode = Value("esn")
}
