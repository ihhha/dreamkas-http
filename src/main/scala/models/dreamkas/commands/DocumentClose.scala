package models.dreamkas.commands

import akka.util.ByteString
import models.dreamkas.Password
import utils.helpers.ListHelper.ListStringExtended
import utils.helpers.StringHelper.EMPTY_STRING

final case class DocumentClose(
  buyerAddress: String = EMPTY_STRING,
  flags: Int = 0
)(implicit val password: Password) extends Command {

  override val simpleResponse: Boolean = false

  private val cutFlag = 0

  private val data = List(cutFlag.toString, buyerAddress).toDreamkasData

  override def request(packetIndex: Int): ByteString = CommandMain(Command.DOCUMENT_CLOSE, data).request(packetIndex)

}
