package models.api

import play.api.libs.json.{Json, Reads}

case class Discount(name: String, amount: Long)

trait DiscountJson {
  implicit val reads: Reads[Discount] = Json.reads[Discount]
}

object Discount extends DiscountJson