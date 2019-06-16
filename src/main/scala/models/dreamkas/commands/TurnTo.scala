package models.dreamkas.commands

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalTime}

import akka.util.ByteString
import models.dreamkas.Password
import utils.helpers.ListHelper.ListStringExtended

final case class TurnTo(
  date: LocalDate = LocalDate.now(), time: LocalTime = LocalTime.now()
)(implicit val password: Password) extends Command {

  private val dataString = DateTimeFormatter.ofPattern("ddMMyy").format(date)
  private val timeString = DateTimeFormatter.ofPattern("HHmmss").format(time)

  private val data = List(dataString, timeString).toDreamkasData

  override def request(packetIndex: Int): ByteString = CommandMain(Command.TURN_TO, data).request(packetIndex)
}
