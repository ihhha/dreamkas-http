package models.dreamkas.commands

import java.time.{LocalDate, LocalTime}

import models.dreamkas.Password
import models.dreamkas.commands.CommandBuilder._
import org.scalatest.{FlatSpec, Matchers}

class CommandBuilderSpec extends FlatSpec with Matchers {

  implicit def packetIndex: Int = 39

  implicit val password: Password = Password("PIRI")

  "TurnTo" should "produce correct request" in {

    val date = LocalDate.parse("2019-05-31")
    val time = LocalTime.parse("08:20")

    val expected = Array(2, 80, 73, 82, 73, 39, 49, 48, 51, 49, 48, 53, 49, 57, 28, 48, 56, 50, 48, 48, 48, 3, 51, 69)
      .map(_.toByte)
    turnTo(date, time).toRequest(packetIndex) shouldBe expected
  }

  "FlagState" should "produce correct request" in {
    val expected = Array(2, 80, 73, 82, 73, packetIndex, 48, 48, 3, 50, 54).map(_.toByte)
    flagState.toRequest(packetIndex) shouldBe expected
  }

  "PrinterDateTime" should "produce correct request" in {
    val expected = Array(2, 80, 73, 82, 73, 39, 49, 51, 3, 50, 52).map(_.toByte)
    printerDateTime.toRequest(packetIndex) shouldBe expected
  }

}
