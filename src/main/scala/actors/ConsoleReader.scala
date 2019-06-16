package actors

import java.time.{LocalDate, LocalDateTime, LocalTime}

import scala.concurrent.duration._
import scala.io.StdIn

import akka.actor.{Actor, ActorRef}
import akka.pattern.ask
import akka.util.Timeout
import models.DocumentType.{Payment, Refund}
import models.api.{Cashier, Receipt, Ticket}
import models.dreamkas.ModelTypes.ErrorOr
import models.dreamkas.commands.{DocumentAddPosition, DocumentCancel, DocumentClose, DocumentOpen, DocumentPayment, DocumentPrint, DocumentSubTotal, DocumentTotal, PaperCut, PrinterDateTime, ReportZ, SetDateTime, TurnTo, Command => DreamkasCommand}
import models.dreamkas.{Big, DocumentTypeMode, Password, Small}
import models.{PaymentMode, PaymentType, TaxMode}
import services.HttpService.TIMEOUT

class ConsoleReader extends Actor {

  import ConsoleReader._
  import context._

  implicit val timeout: Timeout = Timeout(TIMEOUT.seconds)

  val testTicket = Ticket("Мстители", LocalDateTime.now(), 10000L, None, "10", "2", 16, "АА", 123134)
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

          askPrinter(parent, TurnTo(date, time))
        case ":syncDateTime" =>

          val date = LocalDate.now()
          val time = LocalTime.now()

          askPrinter(parent, SetDateTime(date, time))
        case ":status" => askPrinter(parent, DocumentCancel())
        case ":z" => askPrinter(parent, ReportZ())
        case ":x" => askPrinter(parent, PaperCut())
        case ":docin" => askPrinter(parent,
          DocumentOpen(
            typeMode = DocumentTypeMode(Refund),
            cashier = Some(Cashier("Иванов М.Ю.")),
            number = 1,
            taxMode = TaxMode.Default
          )
        )
        case ":docadd" => val taxMode = testReceipt.taxMode
          val paymentMode = testReceipt.paymentMode
          testReceipt.tickets
            .foreach(ticket => parent ! MsgNoAnswer(DocumentAddPosition(ticket, taxMode, paymentMode)))
        case ":docout" => askPrinter(parent, DocumentClose("адрес"))
        case ":print" => implicit val timeout = Timeout(20 seconds)
          parent ! MsgNoAnswer(DocumentPrint("маленький текст", Small))
          parent ! MsgNoAnswer(DocumentPrint("маленький текст, удвоеный", Small, true))
          parent ! MsgNoAnswer(DocumentPrint("Большой текст", Big))
          parent ! MsgNoAnswer(DocumentPrint("маленький текст удвоенный", Big, true))
        case ":docsubtotal" => askPrinter(parent, DocumentSubTotal())
        case ":docpayment" => askPrinter(parent, DocumentPayment(testReceipt))

        case ":doccancel" => askPrinter(parent, DocumentCancel())
        case ":docsummary" => askPrinter(parent, DocumentTotal(10000))

        case ":printerTime" => askPrinter(parent, PrinterDateTime())
        case ":receipt" => implicit val timeout = Timeout(30 seconds)
          val taxMode = testReceipt.taxMode
          val paymentMode = testReceipt.paymentMode
          for {
            _ <- askPrinter(parent,
              DocumentOpen(
                typeMode = DocumentTypeMode(Payment, true),
                cashier = Some(Cashier("Иванов М.Ю.")),
                number = 1,
                taxMode = TaxMode.Default
              ))
            _ = testReceipt.tickets
              .foreach(ticket => parent ! MsgNoAnswer(DocumentAddPosition(ticket, taxMode, paymentMode)))
            _ = parent ! MsgNoAnswer(DocumentSubTotal())
            _ = parent ! MsgNoAnswer(DocumentSubTotal())
            _ = parent ! MsgNoAnswer(DocumentPayment(testReceipt))
            _ <- askPrinter(parent, DocumentClose())
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

  private def askPrinter(printer: ActorRef, cmd: DreamkasCommand)(implicit password: Password) = {
    (printer ? Msg(cmd)).mapTo[ErrorOr]
      .map(_.fold(_.dump(), identity))
  }
}

object ConsoleReader {

  case object Read

  case object EOT

  case class ConsoleInput(in: Array[Byte])

  case class Msg(command: DreamkasCommand)

  case class MsgNoAnswer(command: DreamkasCommand)

}
