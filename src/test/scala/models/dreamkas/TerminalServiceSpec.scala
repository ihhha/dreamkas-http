package models.dreamkas

import akka.util.ByteString
import models.dreamkas.commands.CommandMainT._
import models.dreamkas.errors.DreamkasError._
import org.scalatest.{FlatSpec, Matchers, OptionValues}
import services.TerminalService

class TerminalServiceSpec extends FlatSpec with Matchers with OptionValues {

  val packetIndex = 32
  val cmd1Byte = 49
  val cmd2Byte = 48
  val err1Byte = 48
  val err2Byte = 66
  val crc1Byte = 53
  val crc2Byte = 48

  val incorrectData = ByteString(Array(1.toByte))
  val noEtxData = ByteString(Array(STX, packetIndex, 49, 48, 48, 66, 53, 48).map(_.toByte))

  val correctData = ByteString(Array(STX, packetIndex, 49, 48, 48, 48, ETX, 50, 50).map(_.toByte))
  val correctDataWithError= ByteString(Array(STX, packetIndex, 49, 48, 48, 49, ETX, 50, 51).map(_.toByte))


  "Out" should "error incorrect input" in {
    TerminalService.processOut(incorrectData, packetIndex).value shouldBe UnknownFormat
  }

  it should "error no ETX" in {
    TerminalService.processOut(noEtxData, packetIndex).value shouldBe NoEtxFound
  }

  it should "process response" in {
    TerminalService.processOut(correctData, packetIndex) shouldBe empty
  }

  it should "return error" in {
    TerminalService.processOut(correctDataWithError, packetIndex).value shouldBe FunctionUnavailableWithSuchStatus
  }

}
