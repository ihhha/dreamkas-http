package actors.serial.io

import actors.serial.io.Serial._
import akka.actor._
import akka.util.ByteString
import com.fazecast.jSerialComm.SerialPort

private[io] class SerialOperator(port: SerialPort, handler: ActorRef) extends Actor with ActorLogging {

  private case class DataAvailable(count: Int)

  case class ReaderDied(ex: Throwable)

  object Reader extends Thread {

    def loop(): Unit = {
      var stop = false
      while (port.isOpen && !stop) {
        try {
          val available = port.bytesAvailable

          if (available > 0) {
            val readBuffer = new Array[Byte](available)

            port.readBytes(readBuffer, readBuffer.length)

            val data = ByteString.fromArray(readBuffer)

            handler.tell(Serial.Received(data), self)
          }
        } catch {
          //stop and tell operator on other exception
          case ex: Exception =>
            stop = true
            self.tell(ReaderDied(ex), Actor.noSender)
        }
      }
    }

    override def run() {
      this.setName(s"serial-reader(${port.getSystemPortName})")
      loop()
    }

  }

  override def preStart() = {
    context watch handler
    handler ! Serial.Opened(port.getSystemPortName)
    Reader.start()
  }

  override def postStop: Unit = {
    handler ! Closed
    if (port.isOpen)
      port.closePort()
  }

  override def receive = {
    case Close =>
      port.closePort()
      if (sender != handler) sender ! Closed
      context.stop(self)

    case Write(data) => port.writeBytes(data.toArray, data.size)

    case Terminated(actor) => self ! Close
  }
}

private[io] object SerialOperator {
  def props(port: SerialPort, commander: ActorRef) = Props(new SerialOperator(port, commander))
}