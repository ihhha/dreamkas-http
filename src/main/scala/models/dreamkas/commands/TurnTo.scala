package models.dreamkas.commands

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalTime}

import akka.util.ByteString
import models.dreamkas.Password

final case class TurnTo(date: LocalDate, time: LocalTime)(implicit val password: Password) extends Command {

  val dataString = DateTimeFormatter.ofPattern("ddMMyy").format(date)
  val timeString = DateTimeFormatter.ofPattern("HHmmss").format(time)

  val data = List(dataString, timeString)

   override def request(packetIndex: Int): ByteString = CommandMain(Command.TURN_TO, data).request(packetIndex)
}
