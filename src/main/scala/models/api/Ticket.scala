package models.api

import java.time.LocalDateTime

import play.api.libs.json.{Json, Reads}
import play.api.libs.json.Reads._

case class Ticket(
  showName: String,
  performanceDateTime: LocalDateTime,
  price: Long,
  discount: Option[Long],
  row: String,
  place: String,
  ageLimit: Int,
  series: String,
  number: Int
)

trait TicketJson {
  implicit val reads: Reads[Ticket] = Json.reads[Ticket]
}

object Ticket extends TicketJson
