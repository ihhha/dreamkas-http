import actors.Terminal
import akka.actor.ActorSystem
import jssc.{SerialPort, SerialPortList}
import services.{ConfigService, HttpService}
import utils.Logging

object Main extends App with Logging {

  ConfigService.getPrinter("printer1").map { device1Settings =>

    log.info(s"Available serial ports: ${SerialPortList.getPortNames().mkString(", ")}")

    log.info(s"deviceSettings: $device1Settings")

    val system = ActorSystem("akka-serial")
    val printer1 = system.actorOf(Terminal(device1Settings), name = "printer1")

    val sp = new SerialPort(device1Settings.port)

    val printer2 = ConfigService.getPrinter("printer2")
      .map { device2Settings =>
        log.info(s"deviceSettings: $device2Settings")
        system.actorOf(Terminal(device2Settings), name = "printer2")
      }

    val http = new HttpService(printer1, printer2, ConfigService.getOrigin)

    system.registerOnTermination {
      log.info("Stopped terminal system.")
      http.unbind()
    }
  }.getOrElse(log.warn("Printer not configured"))
}
