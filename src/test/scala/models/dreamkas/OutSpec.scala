package models.dreamkas

import akka.util.ByteString
import models.dreamkas.DreamkasError.{NoEtxFound, UnknownFormat}
import org.scalatest.{EitherValues, FlatSpec, Matchers}
import models.dreamkas.commands.CommandMainT._

class OutSpec extends FlatSpec with Matchers with EitherValues {

  val packetIndex = 32
  val cmd1Byte = 49
  val cmd2Byte = 48
  val err1Byte = 48
  val err2Byte = 66
  val crc1Byte = 53
  val crc2Byte = 48


  val incorrectData = ByteString(Array(1.toByte))
  val noEtxData = ByteString(Array(STX,32,49,48,48,66,53,48).map(_.toByte))

  val correctData = ByteString(Array(STX,32,49,48,48,66,ETX,53,48).map(_.toByte))


  "Out" should "error incorrect input" in {
    Out(incorrectData).dump.left.value shouldBe UnknownFormat
  }

  it should "error no ETX" in {
    Out(noEtxData).dump.left.value shouldBe NoEtxFound
  }

  it should "process response" in {
    Out(correctData).dump.right.value shouldBe
      RawResponse(packetIndex, cmd1Byte, cmd2Byte, err1Byte, err2Byte, crc1Byte, crc2Byte)
  }

}
