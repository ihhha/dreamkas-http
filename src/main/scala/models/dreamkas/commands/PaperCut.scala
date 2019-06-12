package models.dreamkas.commands

import akka.util.ByteString
import models.dreamkas.Password

final case class PaperCut(implicit val password: Password) extends Command {

  override def request(packetIndex: Int): ByteString = CommandMain(Command.PAPER_CUT).request(packetIndex)

}
