package models.dreamkas.commands

import models.dreamkas.ModelTypes.Code
import models.dreamkas.Password
import models.dreamkas.commands.CommandMainT._
import utils.helpers.ArrayByteHelper._

trait CommandMainT extends CommandBase {

  val password: Password
  val packetIndex: Int

  val code: Code
  val data: List[String] = List.empty[String]
  val options: List[String] = List.empty[String]

  private val dataArray = {
    data.foldLeft(Array.emptyByteArray) {
      case (res, string) => res ++ string.map(_.toByte) :+ FS.toByte
    }.dropRight(1)
  }

  override val toRequest: Array[Byte] = {
    val msg = (password.bytes :+ packetIndex.toByte) ++ code.map(_.toByte) ++ dataArray :+ ETX.toByte
    val crc = msg.toCrc.map(_.toByte).toArray
    (STX.toByte +: msg) ++ crc
  }
}

object CommandMainT {
  val STX = 0x02
  val ETX = 0x03
  val FS = 0x1C
}
