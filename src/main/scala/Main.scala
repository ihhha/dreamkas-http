import actors.Terminal
import akka.actor.ActorSystem
import akka.serial.Serial
import services.ConfigService

object Main extends App {

  println("Starting terminal system, enter :q to exit.")

  ConfigService.getPrinter("printer1").map { deviceSettings =>
    println(s"deviceSettings: $deviceSettings")
    Serial.debug(true)
    val system = ActorSystem("akka-serial")
    val terminal = system.actorOf(Terminal(deviceSettings), name = "terminal")
    system.registerOnTermination(println("Stopped terminal system."))
  }.getOrElse(println("Printer Not Found"))
}
