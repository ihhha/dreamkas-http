package actors

import java.time.{LocalDate, LocalTime}

import scala.concurrent.duration._
import scala.io.StdIn

import akka.actor.Actor
import akka.pattern.ask
import akka.util.Timeout
import models.dreamkas.commands.CommandBuilder._
import models.dreamkas.commands.CommandT
import models.dreamkas.Password
import models.dreamkas.errors.DreamkasError

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

          parent ! Command(turnTo(date, time))
        case ":syncDateTime" =>

          val date = LocalDate.now()
          val time = LocalTime.now()

          parent ! Command(setDateTime(date, time))
        case ":status" =>

          parent ! Command(flagState)
        case ":z" =>

          parent ! Command(reportZ())
        case ":x" =>

          parent ! Command(reportX())
        case ":printerTime" =>

          parent ! Command(printerDateTime)
        case ":multi" => implicit val timeout = Timeout(1 seconds)

          val date = LocalDate.now()
          val time = LocalTime.now()

          for {
            out1 <- (parent ? Command(printerDateTime)).mapTo[Option[DreamkasError]]
            out2 <- parent ? Command(flagState)
            out3 <- parent ? Command(turnTo(date, time))
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

  case class Command(commandT: CommandT)

}
