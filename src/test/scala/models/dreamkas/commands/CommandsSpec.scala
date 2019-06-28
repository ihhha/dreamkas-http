package models.dreamkas.commands

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime, LocalTime}

import models.DocumentType.{Payment, Refund}
import models.api.{Cashier, Receipt, Ticket}
import models.dreamkas.{DocumentTypeMode, Password}
import models.{PaymentMode, PaymentType, TaxMode}
import org.scalatest.{FlatSpec, Matchers}

class CommandsSpec extends FlatSpec with Matchers {

  implicit def packetIndex: Int = 39

  val password: Password = Password("PIRI")

  val perfDateTime = LocalDateTime.parse("2019-07-07 10:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))

  val ticket1 = Ticket("Мстители", perfDateTime, 10000L, None, "Зал 5", "10", "2", 16, "АА", 123134)
  val ticket2 = Ticket("Мстители", perfDateTime, 10000L, None, "Зал 5", "12", "3", 17, "АА", 123134)
  val testReceipt = Receipt(
    List(ticket1, ticket2),
    TaxMode.Default,
    12,
    Some(Cashier(name = "Иванов А.О.")),
    PaymentType.Cash,
    PaymentMode.FullPayment,
    Payment
  )

  "TurnTo" should "produce correct request" in {

    val date = LocalDate.parse("2019-05-31")
    val time = LocalTime.parse("08:20")

    val expected = Array(2, 80, 73, 82, 73, 39, 49, 48, 51, 49, 48, 53, 49, 57, 28, 48, 56, 50, 48, 48, 48, 3, 51, 69)
      .map(_.toByte)
    TurnTo(date, time, password).request(packetIndex) shouldBe expected
  }

  "DocumentCancel" should "produce correct request" in {
    val expected = Array(2, 80, 73, 82, 73, packetIndex, 51, 50, 3, 50, 55).map(_.toByte)
    DocumentCancel(password).request(packetIndex) shouldBe expected
  }

  "PrinterDateTime" should "produce correct request" in {
    val expected = Array(2, 80, 73, 82, 73, 39, 49, 51, 3, 50, 52).map(_.toByte)
    PrinterDateTime(password).request(packetIndex) shouldBe expected
  }

  "DocumentOpen" should "produce correct request(payment)" in {
    val expected = Array(
      2, 80, 73, 82, 73, 39, 51, 48, 49, 56, 28, 49, 28, -120, -94, -96, -83, -82, -94, 32, -128, 46, -114, 46, 28, 49,
      50, 28, 48, 3, 50, 66).map(_.toByte)
    DocumentOpen(typeMode = DocumentTypeMode(Payment, packet = true),
      cashier = testReceipt.cashier,
      number = testReceipt.checkId,
      pass = password,
      taxMode = testReceipt.taxMode).request(packetIndex) shouldBe expected
  }
  it should "produce correct request (refund)" in {
    val expected = Array(
      2, 80, 73, 82, 73, 39, 51, 48, 49, 57, 28, 49, 28, -120, -94, -96, -83, -82, -94, 32, -128, 46, -114, 46, 28, 49,
      50, 28, 48, 3, 50, 65).map(_.toByte)
    DocumentOpen(typeMode = DocumentTypeMode(Refund, packet = true),
      cashier = testReceipt.cashier,
      number = testReceipt.checkId,
      pass = password,
      taxMode = testReceipt.taxMode).request(packetIndex) shouldBe expected
  }

  "DocumentAddPosition" should "produce correct request" in {
    val expected = Array(2, 80, 73, 82, 73, 39, 52, 50, 48, 55, 45, 48, 55, 45, 49, 57, 32, 49, 48, 58, 48, 48, 32, 91,
      -121, -96, -85, 32, 53, 93, 32, -116, -31, -30, -88, -30, -91, -85, -88, 28, 16, 16, 49, 50, 51, 49, 51, 52, 28,
      49, 28, 49, 48, 48, 46, 48, 28, 48, 28, 28, 28, 28, 28, 28, 52, 28, 52, 3, 70, 66).map(_.toByte)
    DocumentAddPosition(ticket1, testReceipt.taxMode, testReceipt.paymentMode, password)
      .request(packetIndex) shouldBe expected
  }

  "DocumentSubtotal" should "produce correct request" in {
    val expected = Array(2, 80, 73, 82, 73, 39, 52, 52, 3, 50, 54).map(_.toByte)
    DocumentSubTotal(password).request(packetIndex) shouldBe expected
  }

  "DocumentPayment" should "produce correct request" in {
    val expected = Array(2, 80, 73, 82, 73, 39, 52, 55, 48, 28, 50, 48, 48, 46, 48, 28, 3, 51, 57)
      .map(_.toByte)
    DocumentPayment(testReceipt, password).request(packetIndex) shouldBe expected
  }

  "DocumentClose" should "produce correct request" in {
    val expected =
      Array(2, 80, 73, 82, 73, 39, 51, 49, 48, 28, 3, 48, 56).map(_.toByte)
    DocumentClose(password).request(packetIndex) shouldBe expected
  }

}
