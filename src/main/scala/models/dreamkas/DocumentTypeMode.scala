package models.dreamkas

import scala.collection.BitSet

import models.DocumentType
import models.DocumentType.DocumentType
import models.dreamkas.DocumentTypeMode._

case class DocumentTypeMode(documentType: DocumentType, packet: Boolean = false, postponePrint: Boolean = false) {

  private val packetBit = if (packet) BitSet(PACKET_BIT) else BitSet.empty
  private val postponePrintBit = if (postponePrint) BitSet(POSTPONE_PRINT) else BitSet.empty

  val bitString: String =
    (BitSet(DocumentType.toDreamkas(documentType): _*) ++ packetBit ++ postponePrintBit).toBitMask(0).toString
}

case object DocumentTypeMode {

  val PACKET_BIT = 4
  val POSTPONE_PRINT = 5

}
