package models.dreamkas

import models.TaxMode._

case class DreamkasTaxMode(mode: TaxMode) {

  import DreamkasTaxMode._

  val Code: String = taxModeMap.getOrElse(mode, 0).toString
}

object DreamkasTaxMode {

  private val taxModeMap: Map[TaxMode, Int] = Map[TaxMode, Int](
    Default -> 0,
    SimpleIncome -> 1,
    SimpleIncomeOutcome -> 2,
    TemporaryIncome -> 3,
    Patent -> 5,
    Esn -> 4
  )
}
