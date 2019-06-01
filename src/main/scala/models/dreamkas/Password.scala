package models.dreamkas

import models.dreamkas.ModelTypes.Crc

case class Password(value: String = "PIRI") {
  val crc: Crc = value.foldLeft(0)((i, s) => i ^ s.toInt)
  val bytes: Array[Byte] = value.map(_.toByte).toArray
}
