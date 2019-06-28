package models.dreamkas.commands

trait CommandT

object CommandT {
  val STX = 0x02
  val ETX = 0x03
  val FS = 0x1C

  val FSArray = Array(FS.toByte)
}