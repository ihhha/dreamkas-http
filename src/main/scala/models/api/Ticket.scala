package models.api

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import play.api.libs.json.{Json, Reads}

case class Ticket(
  showName: String,
  performanceDateTime: LocalDateTime,
  price: Long,
  discount: Option[Discount],
  hall: String,
  row: String,
  place: String,
  ageLimit: Int,
  series: String,
  number: Int
) {
  val perfDate = DateTimeFormatter.ofPattern("dd-MM-yy").format(performanceDateTime)
  val perfTime = DateTimeFormatter.ofPattern("HH:mm").format(performanceDateTime)
}

trait TicketJson {
  implicit val reads: Reads[Ticket] = Json.reads[Ticket]
}

object Ticket extends TicketJson
