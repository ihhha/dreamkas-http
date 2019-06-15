package models.dreamkas.commands

import java.time.{LocalDate, LocalDateTime, LocalTime}

import models.DocumentType.Payment
import models.api.{Cashier, Receipt, Ticket}
import models.dreamkas.{DocumentTypeMode, Password}
import models.{PaymentMode, PaymentType, TaxMode}
import org.scalatest.{FlatSpec, Matchers}

class CommandsSpec extends FlatSpec with Matchers {

  implicit def packetIndex: Int = 39

  implicit val password: Password = Password("PIRI")

  val testReceipt = Receipt(
    Ticket("Мстители", LocalDateTime.now(), 10000L, 0L, "10", "2", 16, "АА", 123134),
    1, TaxMode.Default, 12, Some(Cashier(name = "Иванов А.О.")), PaymentType.Cash, PaymentMode.FullPayment, Payment
  )

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

  "DocumentOpen" should "produce correct request" in {
    val expected = Array(
      2, 80, 73, 82, 73, 39, 51, 48, 49, 56, 28, 49, 28, 48, -120, -94, -96, -83
      , -82, -94, 32, -128, 46, -114, 46, 28, 49, 50, 3, 51, 55).map(_.toByte)
    DocumentOpen(mode = DocumentTypeMode(Payment, packet = true),
      cashier = testReceipt.cashier,
      number = testReceipt.checkId,
      taxMode = testReceipt.taxMode).request(packetIndex) shouldBe expected
  }

  "DocumentAddPosition" should "produce correct request" in {
    val expected = Array(2, 80, 73, 82, 73, 39, 52, 50, -116, -31, -30, -88, -30, -91, -85, -88, 28, 28, 49, 28, 49,
      48, 48, 46, 48, 28, 48, 28, 48, 46, 48, 28, 52, 28, 52, 3, 53, 70).map(_.toByte)
    DocumentAddPosition(testReceipt).request(packetIndex) shouldBe expected
  }

  "DocumentSubtotal" should "produce correct request" in {
    val expected = Array(2, 80, 73, 82, 73, 39, 52, 52, 3, 50, 54).map(_.toByte)
    DocumentSubTotal().request(packetIndex) shouldBe expected
  }

  "DocumentPayment" should "produce correct request" in {
    val expected = Array(2, 80, 73, 82, 73, 39, 52, 55, 48, 28, 49, 48, 48, 46, 48, 28, 3, 51, 65).map(_.toByte)
    DocumentPayment(testReceipt).request(packetIndex) shouldBe expected
  }

  "DocumentClose" should "produce correct request" in {
    val expected =
      Array(2, 80, 73, 82, 73, 39, 51, 49, 48, 28, 28, 48, 28, 28, 28, 28, 28, 28, 28, 3, 51, 56).map(_.toByte)
    DocumentClose().request(packetIndex) shouldBe expected
  }

}