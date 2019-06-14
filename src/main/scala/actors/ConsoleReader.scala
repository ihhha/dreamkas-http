package actors

import java.time.{LocalDate, LocalTime}

import scala.concurrent.duration._
import scala.io.StdIn

import akka.actor.Actor
import akka.pattern.ask
import akka.util.Timeout
import models.TaxMode
import models.api.Cashier
import models.dreamkas.commands.{DocumentCancel, DocumentClose, DocumentOpen, DocumentPrint, PaperCut, PrinterDateTime, ReportZ, SetDateTime, TurnTo, Command => DreamkasCommand}
import models.dreamkas.errors.DreamkasError
import models.dreamkas.{Big, DocumentTypeMode, Password, Small}

class ConsoleReader extends Actor {

  import ConsoleReader._
  import context._

  def receive = {
    case Read => implicit val password: Password = Password("PIRI")

      StdIn.readLine() match {
        case ":q" | null => parent ! EOT
        case ":turnto" =>
          val date = LocalDate.now()
          val time = LocalTime.now()

          parent ! Command(TurnTo(date, time))
        case ":syncDateTime" =>

          val date = LocalDate.now()
          val time = LocalTime.now()

          parent ! Command(SetDateTime(date, time))
        case ":status" =>

          parent ! Command(DocumentCancel())
        case ":z" => parent ! Command(ReportZ())
        case ":x" => parent ! Command(PaperCut())
        case ":docin" => parent ! Command(
          DocumentOpen(
            mode = DocumentTypeMode(DocumentTypeMode.SERVICE_DOCUMENT),
            cashier = Some(Cashier("Иванов М.Ю.")),
            number = 1,
            taxMode = TaxMode.Default
          )
        )
        case ":docout" => parent ! Command(DocumentClose("адрес"))
        case ":print" => implicit val timeout = Timeout(20 seconds)
          for {
            _ <- (parent ? Command(DocumentPrint("маленький текст", Small))).mapTo[Option[DreamkasError]]
            _ <- (parent ? Command(DocumentPrint("маленький текст, удвоеный", Small, true))).mapTo[Option[DreamkasError]]
            _ <- (parent ? Command(DocumentPrint("Большой текст", Big))).mapTo[Option[DreamkasError]]
            _ <- (parent ? Command(DocumentPrint("маленький текст удвоенный", Big, true))).mapTo[Option[DreamkasError]]
          } yield ()

        case ":doccancel" => parent ! Command(DocumentCancel())
        case ":printerTime" =>

          parent ! Command(PrinterDateTime())
        case ":multi" => implicit val timeout = Timeout(1 seconds)

          val date = LocalDate.now()
          val time = LocalTime.now()

          for {
            out1 <- (parent ? Command(PrinterDateTime())).mapTo[Option[DreamkasError]]
            out2 <- parent ? Command(DocumentCancel())
            out3 <- parent ? Command(TurnTo(date, time))
          } yield List(out1, out2, out3)

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

  case class Command(command: DreamkasCommand)

}
