package models.dreamkas.commands

import akka.util.ByteString
import models.dreamkas.Password
import models.dreamkas.commands.Command.PING

case class Ping(implicit password: Password) extends Command {

  override val simpleResponse: Boolean = true

  override def request(packetIndex: Int): ByteString =
    CommandBase(PING).request(packetIndex)
}

object Ping {
  val ASK = "06"
}
