import actors.Terminal
import akka.actor.ActorSystem
import services.{ConfigService, HttpService}
import utils.Logging

object Main extends App with Logging {

  log.info("Starting terminal system, enter :q to exit.")

  ConfigService.getPrinter("printer1").map { device1Settings =>

    log.info(s"deviceSettings: $device1Settings")

    val system = ActorSystem("akka-serial")
    val printer1 = system.actorOf(Terminal(device1Settings), name = "printer1")

    val printer2 = ConfigService.getPrinter("printer2")
      .map { device2Settings =>
        log.info(s"deviceSettings: $device2Settings")
        system.actorOf(Terminal(device2Settings), name = "printer2")
      }

    val http = new HttpService(printer1, printer2)

    system.registerOnTermination {
      log.info("Stopped terminal system.")
      http.unbind
    }
  }.getOrElse(log.warn("Printer Not Found"))
}
