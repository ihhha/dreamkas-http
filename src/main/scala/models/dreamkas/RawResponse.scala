package models.dreamkas

case class RawResponse(
  packetIndex: Int,
  cmd1Byte: Int,
  cmd2Byte: Int,
  err1Byte: Int,
  err2Byte: Int,
  crc1byte: Int,
  crc2byte: Int,
  dataArray: Array[Byte] = Array.emptyByteArray
)
