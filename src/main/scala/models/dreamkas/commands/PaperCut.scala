package models.dreamkas.commands

import akka.util.ByteString
import models.dreamkas.Password

final case class PaperCut(implicit val password: Password) extends Command {
  override val simpleResponse: Boolean = false

  override def request(packetIndex: Int): ByteString = CommandSimple(Command.PAPER_CUT).request(packetIndex)

}
