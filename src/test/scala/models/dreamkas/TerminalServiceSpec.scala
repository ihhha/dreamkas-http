package models.dreamkas

import akka.util.ByteString
import models.dreamkas.commands.CommandT._
import models.dreamkas.errors.DreamkasError._
import models.dreamkas.errors.ErrorWithCode
import org.scalatest.{EitherValues, FlatSpec, Matchers}
import services.TerminalService

class TerminalServiceSpec extends FlatSpec with Matchers with EitherValues {

  val packetIndex = 32
  val cmd1Byte = 49
  val cmd2Byte = 48
  val err1Byte = 48
  val err2Byte = 66
  val crc1Byte = 53
  val crc2Byte = 48

  val incorrectData = ByteString(Array(1.toByte))
  val noEtxData = ByteString(Array(STX, packetIndex, 49, 48, 48, 66, 53, 48).map(_.toByte))

  val correctDataWithError = ByteString(Array(STX, packetIndex, 49, 48, 48, 49, ETX, 50, 51).map(_.toByte))
  val correctData1 = ByteString(Array(STX, 32, 48, 48, 48, 48, 48, 28, 50, 28, 48, 28, ETX, 48, 68).map(_.toByte))
  val correctData2 = ByteString(
    Array(STX, packetIndex + 1, 49, 51, 48, 48, 49, 50, 48, 54, 49, 57, 28, 49, 57, 49, 52, 49, 54, 28, ETX, 50, 55)
      .map(_.toByte)
  )

  "Out" should "error incorrect input" in {
    TerminalService.processOut(incorrectData).left.value shouldBe UnknownFormat
  }

  it should "error no ETX" in {
    TerminalService.processOut(noEtxData).left.value shouldBe NoEtxFound
  }

  it should "process response" in {
    TerminalService.processOut(correctData1).right.value shouldBe ()
    TerminalService.processOut(correctData2).right.value shouldBe ()
  }

  it should "return error" in {
    TerminalService.processOut(correctDataWithError).left.value shouldBe ErrorWithCode("01", packetIndex
    )
  }

}
