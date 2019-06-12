package services

import java.time.{LocalDate, LocalTime}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import models.dreamkas.Password
import models.dreamkas.commands.{TurnTo, Command => DreamkasCommand}
import models.dreamkas.errors.DreamkasError

class HttpService(printer1: ActorRef, printer2: Option[ActorRef] = None) {

  implicit val system: ActorSystem = ActorSystem("http-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  implicit val timeout: Timeout = Timeout(1 seconds)

  implicit def myExceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case err =>
        extractUri { uri =>
          println(s"Request to $uri could not be handled normally")
          println(s"[error] ${err.getLocalizedMessage}")
          complete(HttpResponse(InternalServerError, entity = "Bad numbers, bad result!!!"))
        }
    }

  val handler: Route = path("api" / "fiskal" / IntNumber) { terminalId =>

    implicit val password: Password = ConfigService.getPrinter(s"printer$terminalId")
      .map(_.password).getOrElse(Password("PIRI"))

    val date = LocalDate.now()
    val time = LocalTime.now()
    onSuccess(
      (printer1 ? HttpService.Command(TurnTo(date, time))).mapTo[Option[DreamkasError]]
    ) {
      _.map(_.httpResponse).getOrElse(complete(HttpResponse(NoContent)))
    }
  }

  val bindingFuture: Future[Http.ServerBinding] = Http().bindAndHandle(handler, ConfigService.getHost, ConfigService.getPort)

  def unbind: Unit = {
    bindingFuture.onComplete(_ => system.terminate())
  }

}

object HttpService {

  case class Command(cmd: DreamkasCommand)

}
