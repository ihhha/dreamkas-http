package actors

import java.time.{LocalDate, LocalTime}

import scala.io.StdIn

import akka.actor.Actor
import models.dreamkas.Password
import models.dreamkas.commands.CommandBuilder._
import akka.actor.dsl._

class ConsoleReader extends Actor {

  import ConsoleReader._
  import context._

  var currentIndex: Int = 0x1F

  def getNextIndex: Int = {
    currentIndex = if (currentIndex > 0xF0 || currentIndex < 0x20) 0x20 else currentIndex + 1
    currentIndex
  }

  def receive = {
    case Read => implicit val password: Password = Password("PIRI")

      StdIn.readLine() match {
        case ":q" | null => parent ! EOT
        case ":turnto" => implicit def packetIndex: Int = getNextIndex

          val date = LocalDate.now()
          val time = LocalTime.now()

          parent ! Command(turnTo(date, time).toRequest)
        case ":syncDateTime" => implicit def packetIndex: Int = getNextIndex

          val date = LocalDate.now()
          val time = LocalTime.now()

          parent ! Command(setDateTime(date, time).toRequest)
        case ":status" => implicit def packetIndex: Int = getNextIndex

          parent ! Command(flagState.toRequest)
        case ":z" => implicit def packetIndex: Int = getNextIndex

          parent ! Command(reportZ().toRequest)
        case ":x" => implicit def packetIndex: Int = getNextIndex

          parent ! Command(reportX().toRequest)
        case ":printerTime" => implicit def packetIndex: Int = getNextIndex

          parent ! Command(printerDateTime.toRequest)
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

  case class Command(in: Array[Byte])

}
