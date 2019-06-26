package models.dreamkas.commands

import scala.collection.BitSet

import akka.util.ByteString
import models.dreamkas.{FontSize, Password}
import models.dreamkas.commands.CommandT.FSArray
import utils.helpers.StringHelper.StringExt

final case class DocumentPrint(
  text: String,
  fontSize: FontSize,
  doubleTextSize: Boolean = false
)(implicit val password: Password) extends Command {

  override val simpleResponse: Boolean = false

  private val bitFontSize = BitSet(fontSize.value)
  private val bitDoubleTextSize = if (doubleTextSize) BitSet(4, 5) else BitSet.empty
  private val bitFontString = (bitFontSize ++ bitDoubleTextSize).toBitMask(0).toString

  private val data = text.toCp866Bytes ++ FSArray ++
    bitFontString.toByteArray

  override def request(packetIndex: Int): ByteString = CommandMain(Command.DOCUMENT_PRINT_TEXT, data).request(packetIndex)

}
