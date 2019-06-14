import actors.Terminal
import akka.actor.ActorSystem
import akka.serial.Serial
import services.{ConfigService, HttpService}
import utils.Logging

object Main extends App with Logging {

  log.info("Starting terminal system, enter :q to exit.")

  ConfigService.getPrinter("printer1").map { deviceSettings =>
    log.info(s"deviceSettings: $deviceSettings")
    Serial.debug(ConfigService.getSerialDebug)
    val system = ActorSystem("akka-serial")
    val terminal = system.actorOf(Terminal(deviceSettings), name = "terminal")

    val http = new HttpService(terminal, None)

    system.registerOnTermination {
      log.info("Stopped terminal system.")
      http.unbind
    }
  }.getOrElse(log.warn("Printer Not Found"))
}
