package models.dreamkas

import scala.collection.BitSet

import models.DocumentType
import models.DocumentType.DocumentType
import models.dreamkas.DocumentTypeMode._

case class DocumentTypeMode(
  documentType: DocumentType,
  packet: Boolean = false,
  postponePrint: Boolean = false,
  noPrint: Boolean = false,
  noSummaryTotal: Boolean = false
) {

  private val packetBit = if (packet) BitSet(PACKET_BIT) else BitSet.empty
  private val postponePrintBit = if (postponePrint) BitSet(POSTPONE_PRINT) else BitSet.empty
  private val noPrintBit = if (noPrint) BitSet(NO_PRINT) else BitSet.empty
  private val noSummaryTotalBit = if (noSummaryTotal) BitSet(NO_SUMMARY_TOTAL) else BitSet.empty

  val bitString: String = {
    (
      BitSet(DocumentType.toDreamkas(documentType): _*) ++
        packetBit ++
        postponePrintBit ++
        noPrintBit ++
        noSummaryTotalBit
      ).toBitMask(0).toString
  }
}

case object DocumentTypeMode {

  val PACKET_BIT = 4
  val POSTPONE_PRINT = 5
  val NO_PRINT = 7
  val NO_SUMMARY_TOTAL = 8

}
