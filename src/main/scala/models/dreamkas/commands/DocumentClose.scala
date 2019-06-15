package models.dreamkas.commands

import akka.util.ByteString
import models.dreamkas.Password
import utils.helpers.ListHelper.ListStringExtended
import utils.helpers.StringHelper.EMPTY_STRING

final case class DocumentClose(
  buyerAddress: String = EMPTY_STRING,
  flags: Int = 0
)(implicit val password: Password) extends Command {
  private val cutFlag = 0
  private val reserved = EMPTY_STRING
  private val additionalPropsName = EMPTY_STRING
  private val additionalPropsValue = EMPTY_STRING
  private val buyer = EMPTY_STRING
  private val buyerInn = EMPTY_STRING

  private val data = List(cutFlag.toString, buyerAddress, flags.toString, reserved, reserved, reserved,
    additionalPropsValue, additionalPropsName, buyer, buyerInn).toDreamkasData

  override def request(packetIndex: Int): ByteString = CommandMain(Command.DOCUMENT_CLOSE, data).request(packetIndex)

}
