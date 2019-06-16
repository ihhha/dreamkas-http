package actors

import scala.concurrent.duration._

import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Props, Terminated, Timers, actorRef2Scala}
import akka.io.IO
import akka.serial.Serial
import akka.util.ByteString
import models.dreamkas.errors.DreamkasError.{NoEtxFound, NoPrinterConnected}
import models.dreamkas.{DeviceSettings, Password}
import services.{HttpService, TerminalService}
import utils.helpers.ArrayByteHelper._
import cats.syntax.either._
import models.dreamkas.commands.TurnTo

class Terminal(deviceSettings: DeviceSettings) extends Actor with ActorLogging with Timers {

  import Terminal._
  import context._

  implicit val password: Password = Password("PIRI")

  timers.startSingleTimer(StartTimer, Start, 2.seconds)

  val reader: ActorRef = actorOf(Props[ConsoleReader])

  var currentIndex: Int = 0x1F

  private def getNextIndex: Int = {
    currentIndex = if (currentIndex > 0xF0 || currentIndex < 0x20) 0x20 else currentIndex + 1
    currentIndex
  }

  override def postStop(): Unit = {
    system.terminate()
  }

  def receive: Receive = {
    case Start => timers.startPeriodicTimer(ReconnectTimer, OpeningSerial, 30.seconds)
      self ! OpeningSerial
    case OpeningSerial =>
      log.info(s"Requesting manager to open port: ${deviceSettings.port}, baud: ${deviceSettings.serialSettings.baud}")
      IO(Serial) ! Serial.Open(deviceSettings.port, deviceSettings.serialSettings)

    case Serial.CommandFailed(cmd, reason) =>
      log.error(s"Connection failed with ${reason.getLocalizedMessage}.")
    case Serial.Opened(port) =>
      log.info(s"Port $port is now open.")
      timers.cancel(ReconnectTimer)
      val operator = sender
      self ! HttpService.Msg(TurnTo())
      context become opened(operator)
      context watch operator
      reader ! ConsoleReader.Read
    case HttpService.Msg(_) => sender ! NoPrinterConnected.asLeft
    case HttpService.MsgNoAnswer(_) => sender ! NoPrinterConnected.asLeft
  }

  def opened(operator: ActorRef): Receive = {
    case Serial.Closed => log.info("Operator closed normally, exiting terminal.")
      operator ! PoisonPill
      context unwatch operator
      context become receive
      self ! Start

    case Terminated(`operator`) => log.error("Operator crashed, exiting terminal.")
      operator ! PoisonPill
      context become receive
      self ! Start

    case ConsoleReader.EOT => log.info("Initiating close.")
      operator ! Serial.Close

    case ConsoleReader.ConsoleInput(input) => val data = ByteString(input)
      operator ! Serial.Write(data, length => Wrote(data.take(length)))
      reader ! ConsoleReader.Read

    case ConsoleReader.MsgNoAnswer(cmd) => val packetIndex = getNextIndex
      val data = cmd.request(packetIndex)
      operator ! Serial.Write(data, length => Wrote(data.take(length)))
      reader ! ConsoleReader.Read

    case ConsoleReader.Msg(cmd) => val packetIndex = getNextIndex
      val data = cmd.request(packetIndex)
      operator ! Serial.Write(data, length => Wrote(data.take(length)))
      context become processResponse(operator, sender, packetIndex)

    case HttpService.Msg(cmd) => val packetIndex = getNextIndex
      val data = cmd.request(packetIndex)
      operator ! Serial.Write(data, length => Wrote(data.take(length)))
      context become processResponse(operator, sender, packetIndex)

    case HttpService.MsgNoAnswer(cmd) => val packetIndex = getNextIndex
      val data = cmd.request(packetIndex)
      operator ! Serial.Write(data, length => Wrote(data.take(length)))

    case Terminal.Wrote(data) => log.info(s"Wrote data: ${formatData(data)}")
  }

  var response: ByteString = ByteString.empty

  def processResponse(operator: ActorRef, sender: ActorRef, commandIndex: Int): Receive = {

    case Terminal.Wrote(data) => log.info(s"Wrote data: ${formatData(data)}")

    case Serial.Received(data) => response = response ++ data
      log.info(s"Received data: ${formatData(data)}")
      TerminalService.processOut(response, commandIndex) match {
        case Left(NoEtxFound) =>
          log.warning("Waiting for ETX")
        case result => response = ByteString.empty
          context become opened(operator)
          sender ! result
          reader ! ConsoleReader.Read
      }

  }

}

object Terminal {

  private object ReconnectTimer

  private object StartTimer

  private case object Start

  case object OpeningSerial

  case class Wrote(data: ByteString) extends Serial.Event

  def apply(deviceSettings: DeviceSettings) = Props(classOf[Terminal], deviceSettings)

  private def formatData(data: ByteString) = data.mkString("[", ",", "]") + " " + data.toArray.toUtf8String

}
