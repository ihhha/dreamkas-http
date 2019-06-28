package models.dreamkas.commands

import akka.util.ByteString
import models.dreamkas.commands.Command.PING

case object Ping extends Command {

  val ASK = "06"

  override val simpleResponse: Boolean = true

  override def request(packetIndex: Int): ByteString =
    CommandBase(PING).request(packetIndex)

}
