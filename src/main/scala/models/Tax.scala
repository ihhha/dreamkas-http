package models

import utils.CustomEnum

object Tax extends CustomEnum {

  type Tax = Value

  val Nds18: Tax = Value("nds_18")
  val Nds10: Tax = Value("nds_10")
  val Nds0: Tax = Value("nds_0")
  val NoNds: Tax = Value("no_nds")
  val Nds18118: Tax = Value("nds_18_118")
  val Nds10110: Tax = Value("nds_10_110")

  private val dreamkasMap: Map[Tax, String] = Map(
    Nds18 -> "0",
    Nds10 -> "1",
    Nds0 -> "2",
    NoNds -> "3",
    Nds18118 -> "5",
    Nds10110 -> "4"
  )

  def toDreamkas(tax: Tax): String = dreamkasMap.getOrElse(tax, "3")
}
