package actors.serial.io

import scala.util.{Failure, Success, Try}

import actors.serial.io.Serial._
import akka.actor._
import com.fazecast.jSerialComm.SerialPort

/**
  *  Opens the serial port and then starts a SerialOperator to handle the communication over
  *  that port.
  */
private[io] class SerialManager extends Actor {
  override def receive = {

    case c@Open(handler, port, baudRate, dataBits, parity, stopBits) =>
      Try {
        val serialPort = SerialPort.getCommPort(port)

        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0)

        serialPort.setComPortParameters(baudRate, dataBits, stopBits, parity)
        serialPort.openPort()
        if (serialPort.isOpen) serialPort else throw new SerialException(s"Could not open the port $port")
      } match {
        case Success(serialPort) => val operator = context.actorOf(SerialOperator.props(serialPort, handler))
        case Failure(error) => handler ! CommandFailed(c, error)
      }
  }
}
