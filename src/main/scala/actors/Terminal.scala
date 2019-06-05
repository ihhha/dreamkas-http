package actors

import scala.concurrent.duration._

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Terminated, Timers, actorRef2Scala}
import akka.io.IO
import akka.serial.Serial
import akka.util.ByteString
import models.dreamkas.{DeviceSettings, Out}
import services.HttpService
import utils.helpers.ArrayByteHelper._

class Terminal(deviceSettings: DeviceSettings) extends Actor with ActorLogging with Timers {

  import Terminal._
  import context._

  timers.startSingleTimer(StartTimer, Start, 2.seconds)

  val reader: ActorRef = actorOf(Props[ConsoleReader])

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
      log.error(s"Connection failed, retrying. Error: ${reason.getLocalizedMessage}")
    case Serial.Opened(port) =>
      log.info(s"Port $port is now open.")
      timers.cancel(ReconnectTimer)
      val operator = sender
      context become opened(operator)
      context watch operator
      reader ! ConsoleReader.Read
  }

  def opened(operator: ActorRef): Receive = {
    case Serial.Closed => log.info("Operator closed normally, exiting terminal.")
      context unwatch operator
      context stop self

    case Terminated(`operator`) => log.error("Operator crashed, exiting terminal.")
      context stop self

    case ConsoleReader.EOT => log.info("Initiating close.")
      operator ! Serial.Close

    case ConsoleReader.ConsoleInput(input) => val data = ByteString(input)
      operator ! Serial.Write(data, length => Wrote(data.take(length)))
      reader ! ConsoleReader.Read

    case ConsoleReader.Command(input) => val data = ByteString(input)
      operator ! Serial.Write(data, length => Wrote(data.take(length)))
      context become waitingResponse(operator, sender)
    case HttpService.OpenSession(name) =>
  }

  def waitingResponse(operator: ActorRef, sender: ActorRef): Receive = {

    case Terminal.Wrote(data) => log.info(s"Wrote data: ${formatData(data)}")

    case Serial.Received(data) => val out = Out(data)
      log.info(s"Received data: ${formatData(data)}")
      context become opened(operator)
      sender ! out
      reader ! ConsoleReader.Read
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
