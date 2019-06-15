package models.dreamkas

import models.DocumentType._
import org.scalatest.{FlatSpec, Matchers}

class DocumentTypeModeSpec extends FlatSpec with Matchers {

  "Service" should "create correct bitMask" in {
    DocumentTypeMode(Service).bitString shouldBe "1"
    DocumentTypeMode(Service, true).bitString shouldBe "17"
    DocumentTypeMode(Service, false, true).bitString shouldBe "33"
    DocumentTypeMode(Service, true, true).bitString shouldBe "49"
  }

  "Payment" should "create correct bitMask" in {
    DocumentTypeMode(Payment).bitString shouldBe "2"
    DocumentTypeMode(Payment, true).bitString shouldBe "18"
    DocumentTypeMode(Payment, false, true).bitString shouldBe "34"
    DocumentTypeMode(Payment, true, true).bitString shouldBe "50"
  }
  "Refund" should "create correct bitMask" in {
    DocumentTypeMode(Refund).bitString shouldBe "3"
    DocumentTypeMode(Refund, true).bitString shouldBe "19"
    DocumentTypeMode(Refund, false, true).bitString shouldBe "35"
    DocumentTypeMode(Refund, true, true).bitString shouldBe "51"
  }
  "Income" should "create correct bitMask" in {
    DocumentTypeMode(Income).bitString shouldBe "4"
    DocumentTypeMode(Income, true).bitString shouldBe "20"
    DocumentTypeMode(Income, false, true).bitString shouldBe "36"
    DocumentTypeMode(Income, true, true).bitString shouldBe "52"
  }
  "Outcome" should "create correct bitMask" in {
    DocumentTypeMode(Outcome).bitString shouldBe "5"
    DocumentTypeMode(Outcome, true).bitString shouldBe "21"
    DocumentTypeMode(Outcome, false, true).bitString shouldBe "37"
    DocumentTypeMode(Outcome, true, true).bitString shouldBe "53"
  }
  "Buying" should "create correct bitMask" in {
    DocumentTypeMode(Buying).bitString shouldBe "6"
    DocumentTypeMode(Buying, true).bitString shouldBe "22"
    DocumentTypeMode(Buying, false, true).bitString shouldBe "38"
    DocumentTypeMode(Buying, true, true).bitString shouldBe "54"
  }
  "BuyingRefund" should "create correct bitMask" in {
    DocumentTypeMode(BuyingRefund).bitString shouldBe "7"
    DocumentTypeMode(BuyingRefund, true).bitString shouldBe "23"
    DocumentTypeMode(BuyingRefund, false, true).bitString shouldBe "39"
    DocumentTypeMode(BuyingRefund, true, true).bitString shouldBe "55"
  }

}
