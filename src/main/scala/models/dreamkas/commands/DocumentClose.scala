package models.dreamkas.commands

import akka.util.ByteString
import models.dreamkas.Password
import utils.helpers.ListHelper.ListStringExtended
import utils.helpers.StringHelper

final case class DocumentClose(
  buyerAddress: String,
  flags: Int = 0
)(implicit val password: Password) extends Command {
  val cutFlag = 0
  val reserved = StringHelper.EMPTY_STRING
  val additionalPropsName = StringHelper.EMPTY_STRING
  val additionalPropsValue = StringHelper.EMPTY_STRING
  val buyer = StringHelper.EMPTY_STRING
  val buyerInn = StringHelper.EMPTY_STRING

  private val data = List(cutFlag.toString, buyerAddress, flags.toString, reserved, reserved, reserved,
    additionalPropsValue, additionalPropsName, buyer, buyerInn).toDreamkasData

  override def request(packetIndex: Int): ByteString = CommandMain(Command.DOCUMENT_CLOSE, data).request(packetIndex)

}
