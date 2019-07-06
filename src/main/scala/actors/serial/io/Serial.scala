package actors.serial.io

import akka.actor._
import akka.util.ByteString
import com.fazecast.jSerialComm.SerialPort

/**
  * Serial port extension based on the rxtx library for the akka IO layer.
  */
object Serial extends ExtensionId[SerialExt] with ExtensionIdProvider {
  override def lookup = Serial

  override def createExtension(system: ExtendedActorSystem): SerialExt = new SerialExt(system)

  /** Messages used by the serial IO. */
  sealed trait Message

  /** Messages that are sent to the serial port. */
  sealed trait Command extends Message

  /** Messages received from the serial port. */
  sealed trait Event extends Message

  case class CommandFailed(command: Command, reason: Throwable)

  // Communication with manager

  /** Command that may be sent to the manager actor. */
  sealed trait ManagerCommand extends Command

  /** Command that may be sent to the operator actor. */
  sealed trait OperatorCommand extends Command

  /** Open a serial port. Response: Opened | CommandFailed */
  case class Open(
    handler: ActorRef,
    port: String,
    baudRate: Int = 57600,
    dataBits: Int = 8,
    parity: Int = SerialPort.NO_PARITY,
    stopBits: Int = SerialPort.ONE_STOP_BIT,
  ) extends ManagerCommand

  /**
    *  Serial port is now open.
    *  Communication is handled by the operator actor.
    *  The sender of the Open message will now receive incoming communication from the
    *  serial port.
    */
  case class Opened(port: String) extends Event

  /** List all available serial ports. Response: Ports | CommandFailed */
  case object ListPorts extends ManagerCommand

  /** Available serial ports. */
  case class Ports(ports: Vector[String]) extends Event

  // Communication with Operator

  /** Request that the operator should close the port. Response: Closed */
  case object Close extends OperatorCommand

  /** The port was closed. Either by request or by an external event (i.e. unplugging) */
  case object Closed extends Event

  /** Data was received on the serial port. */
  case class Received(data: ByteString) extends Event

  /** Write data on the serial port. Response: ack (if ack != NoAck) */
  case class Write(data: ByteString) extends OperatorCommand

  /** Ack for a write. */
  trait AckEvent extends Event

  /** Special ack event (is not sent). */
  object NoAck extends AckEvent

  class SerialException(message: String) extends Exception(message)

}

class SerialExt(system: ExtendedActorSystem) extends akka.io.IO.Extension {
  lazy val manager = system.actorOf(Props(classOf[SerialManager]), name = "IO-SERIAL")
}
