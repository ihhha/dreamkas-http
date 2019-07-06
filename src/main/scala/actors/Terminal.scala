package actors

import scala.concurrent.duration._

import actors.serial.io.Serial
import actors.serial.io.Serial.Open
import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Props, Terminated, Timers, actorRef2Scala}
import akka.io.IO
import akka.util.ByteString
import cats.syntax.either._
import models.dreamkas.commands.TurnTo
import models.dreamkas.errors.DreamkasError.{NoEtxFound, NoPrinterConnected}
import models.dreamkas.{DeviceSettings, Password}
import services.{HttpService, TerminalService}
import utils.helpers.ArrayByteHelper._

class Terminal(deviceSettings: DeviceSettings) extends Actor with ActorLogging with Timers {

  import Terminal._
  import context._

  implicit val password: Password = deviceSettings.password

  timers.startSingleTimer(StartTimer, Start, 2.seconds)

  var currentIndex: Int = 0x1F

  private def getNextIndex: Int = {
    currentIndex = if (currentIndex > 0x7E || currentIndex < 0x20) 0x20 else currentIndex + 1
    currentIndex
  }

  override def postStop(): Unit = {
    system.terminate()
  }

  def receive: Receive = {
    case Start => timers.startPeriodicTimer(ReconnectTimer, OpeningSerial, 30.seconds)
      self ! OpeningSerial
    case OpeningSerial =>
      log.info(s"Requesting manager to open port: ${deviceSettings.port}, baud: ${deviceSettings.baud}")

      IO(Serial) ! Open(self, deviceSettings.port, deviceSettings.baud)

    case Serial.CommandFailed(cmd, reason) =>
      log.error(s"Connection failed with ${reason.getLocalizedMessage}.")
    case Serial.Opened(port) =>
      log.info(s"Port $port is now open.")
      timers.cancel(ReconnectTimer)
      val operator = sender
      self ! HttpService.Msg(TurnTo(pass = password))
      context become opened(operator)
      context watch operator
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

    case HttpService.Msg(cmd) => val packetIndex = getNextIndex
      val data = cmd.request(packetIndex)
      operator ! Serial.Write(data)
      log.info(s"[Cmd:Answer] data: ${formatData(data)}")
      context become processResponse(operator, sender, cmd.simpleResponse)

    case HttpService.MsgNoAnswer(cmd) => val packetIndex = getNextIndex
      val data = cmd.request(packetIndex)
      log.info(s"[SimpleCmd] data: ${formatData(data)}")
      operator ! Serial.Write(data)
  }

  var response: ByteString = ByteString.empty

  def processResponse(operator: ActorRef, sender: ActorRef, simpleResponse: Boolean): Receive = {

    case Terminal.Wrote(data) => log.info(s"Wrote data: ${formatData(data)}")

    case Serial.Received(data) => response = response ++ data
      log.info(s"[Received]: ${formatData(data)}")

      if (simpleResponse) {
        response = ByteString.empty
        context become opened(operator)
        sender ! TerminalService.processPong(data)
      } else {
        TerminalService.processOut(response) match {
          case Left(NoEtxFound) =>
            log.info("Waiting for ETX")
          case result => response = ByteString.empty
            result.leftMap(_.toLog)
            context become opened(operator)
            sender ! result
        }
      }
  }

}

object Terminal {

  private object ReconnectTimer

  private object StartTimer

  private case object Start

  case object OpeningSerial

  case class Wrote(data: ByteString)

  def apply(deviceSettings: DeviceSettings) = Props(classOf[Terminal], deviceSettings)

  private def formatData(data: ByteString) = data.mkString("[", ",", "]") + " " + data.toArray.toUtf8String

}
