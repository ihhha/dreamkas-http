import actors.Terminal
import akka.actor.ActorSystem
import akka.serial.Serial
import services.{ConfigService, HttpService}

object Main extends App {

  println("Starting terminal system, enter :q to exit.")

  ConfigService.getPrinter("printer1").map { deviceSettings =>
    println(s"deviceSettings: $deviceSettings")
    Serial.debug(ConfigService.getSerialDebug)
    val system = ActorSystem("akka-serial")
    val terminal = system.actorOf(Terminal(deviceSettings), name = "terminal")

    new HttpService(terminal, None)

    system.registerOnTermination(println("Stopped terminal system."))
  }.getOrElse(println("Printer Not Found"))
}
