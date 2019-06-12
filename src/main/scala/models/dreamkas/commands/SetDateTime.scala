package models.dreamkas.commands

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalTime}

import akka.util.ByteString
import models.dreamkas.Password

final case class SetDateTime(date: LocalDate, time: LocalTime)(implicit val password: Password) extends Command {

  private val dataString = DateTimeFormatter.ofPattern("ddMMyy").format(date)
  private val timeString = DateTimeFormatter.ofPattern("HHmmss").format(time)

  val data = List(dataString, timeString)

  override def request(packetIndex: Int): ByteString = CommandMain(Command.SET_DATETIME, data).request(packetIndex)
}
