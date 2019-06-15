package actors

import java.time.{LocalDate, LocalDateTime, LocalTime}

import scala.concurrent.duration._
import scala.io.StdIn

import akka.actor.Actor
import akka.pattern.ask
import akka.util.Timeout
import models.DocumentType.Payment
import models.api.{Cashier, Receipt, Ticket}
import models.dreamkas.ModelTypes.errorOr
import models.dreamkas.commands.{DocumentAddPosition, DocumentCancel, DocumentClose, DocumentOpen, DocumentPayment, DocumentPrint, DocumentSubTotal, DocumentTotal, PaperCut, PrinterDateTime, ReportZ, SetDateTime, TurnTo, Command => DreamkasCommand}
import models.dreamkas.{Big, DocumentTypeMode, Password, Small}
import models.{PaymentMode, PaymentType, TaxMode}

class ConsoleReader extends Actor {

  import ConsoleReader._
  import context._

  val testTicket = Ticket("Мстители", LocalDateTime.now(), 10000L, 0L, "10", "2", 16, "АА", 123134)
  val testReceipt = Receipt(
    List(testTicket),
    1, TaxMode.Default, 12, None, PaymentType.Cash, PaymentMode.FullPayment, Payment
  )

  def receive = {
    case Read => implicit val password: Password = Password("PIRI")

      StdIn.readLine() match {
        case ":q" | null => parent ! EOT
        case ":turnto" =>
          val date = LocalDate.now()
          val time = LocalTime.now()

          parent ! Msg(TurnTo(date, time))
        case ":syncDateTime" =>

          val date = LocalDate.now()
          val time = LocalTime.now()

          parent ! Msg(SetDateTime(date, time))
        case ":status" =>

          parent ! Msg(DocumentCancel())
        case ":z" => parent ! Msg(ReportZ())
        case ":x" => parent ! Msg(PaperCut())
        case ":docin" => parent ! Msg(
          DocumentOpen(
            mode = DocumentTypeMode(Payment, true),
            cashier = Some(Cashier("Иванов М.Ю.")),
            number = 1,
            taxMode = TaxMode.Default
          )
        )
        case ":docadd" => val taxMode = testReceipt.taxMode
          val paymentMode = testReceipt.paymentMode
          testReceipt.tickets
            .foreach(ticket => parent ! MsgNoAnswer(DocumentAddPosition(ticket, taxMode, paymentMode)))
        case ":docout" => parent ! Msg(DocumentClose("адрес"))
        case ":print" => implicit val timeout = Timeout(20 seconds)
          parent ! MsgNoAnswer(DocumentPrint("маленький текст", Small))
          parent ! MsgNoAnswer(DocumentPrint("маленький текст, удвоеный", Small, true))
          parent ! MsgNoAnswer(DocumentPrint("Большой текст", Big))
          parent ! MsgNoAnswer(DocumentPrint("маленький текст удвоенный", Big, true))
        case ":docsubtotal" => parent ! MsgNoAnswer(DocumentSubTotal())
        case ":docpayment" => parent ! MsgNoAnswer(DocumentPayment(testReceipt))

        case ":doccancel" => parent ! Msg(DocumentCancel())
        case ":docsummary" => parent ! Msg(DocumentTotal(10000))

        case ":printerTime" =>

          parent ! Msg(PrinterDateTime())
        case ":receipt" => implicit val timeout = Timeout(30 seconds)
          val taxMode = testReceipt.taxMode
          val paymentMode = testReceipt.paymentMode
          for {
            _ <- (parent ? Msg(
              DocumentOpen(
                mode = DocumentTypeMode(Payment, true),
                cashier = Some(Cashier("Иванов М.Ю.")),
                number = 1,
                taxMode = TaxMode.Default
              )
            )).mapTo[errorOr]
            _ = testReceipt.tickets
              .foreach(ticket => parent ! MsgNoAnswer(DocumentAddPosition(ticket, taxMode, paymentMode)))
            _ = parent ! MsgNoAnswer(DocumentSubTotal())
            _ = parent ! MsgNoAnswer(DocumentSubTotal())
            _ = parent ! MsgNoAnswer(DocumentPayment(testReceipt))
            _ <- (parent ? Msg(DocumentClose())).mapTo[errorOr]
          } yield ()

        case s => parent ! ConsoleInput(StringWithHexToByte(s))
      }
  }

  def StringWithHexToByte(string: String): Array[Byte] = {
    def helper(chars: List[Char], result: Array[Byte]): Array[Byte] = chars match {
      case Nil => result.reverse
      case '$' :: a :: b :: tail => val newResult = s"$a$b".toByte +: result
        helper(tail, newResult)
      case char :: tail => helper(tail, char.toByte +: result)
    }

    helper(string.toList, Array.emptyByteArray)
  }
}

object ConsoleReader {

  case object Read

  case object EOT

  case class ConsoleInput(in: Array[Byte])

  case class Msg(command: DreamkasCommand)

  case class MsgNoAnswer(command: DreamkasCommand)

}
