package models.dreamkas.commands

import java.time.{LocalDate, LocalTime}

import models.dreamkas.Password
import org.scalatest.{FlatSpec, Matchers}

class CommandsSpec extends FlatSpec with Matchers {

  implicit def packetIndex: Int = 39

  implicit val password: Password = Password("PIRI")

  "TurnTo" should "produce correct request" in {

    val date = LocalDate.parse("2019-05-31")
    val time = LocalTime.parse("08:20")

    val expected = Array(2, 80, 73, 82, 73, 39, 49, 48, 51, 49, 48, 53, 49, 57, 28, 48, 56, 50, 48, 48, 48, 3, 51, 69)
      .map(_.toByte)
    TurnTo(date, time).request(packetIndex) shouldBe expected
  }

  "DocumentCancel" should "produce correct request" in {
    val expected = Array(2, 80, 73, 82, 73, packetIndex, 51, 50, 3, 50, 55).map(_.toByte)
    DocumentCancel().request(packetIndex) shouldBe expected
  }

  "PrinterDateTime" should "produce correct request" in {
    val expected = Array(2, 80, 73, 82, 73, 39, 49, 51, 3, 50, 52).map(_.toByte)
    PrinterDateTime().request(packetIndex) shouldBe expected
  }

}
