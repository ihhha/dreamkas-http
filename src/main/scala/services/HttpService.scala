package services

import java.time.{LocalDate, LocalTime}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives.{onSuccess, _}
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import akka.pattern.{AskTimeoutException, ask}
import akka.stream.ActorMaterializer
import akka.util.Timeout
import models.dreamkas.Password
import models.dreamkas.commands.UrlSegment._
import models.dreamkas.commands.{Command => DreamkasCommand, _}
import models.dreamkas.errors.DreamkasError
import utils.Logging

class HttpService(printer1: ActorRef, printer2: Option[ActorRef] = None) extends Logging {

  implicit val system: ActorSystem = ActorSystem("http-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  implicit val timeout: Timeout = Timeout(1.seconds)

  implicit def myExceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case _: AskTimeoutException => complete(HttpResponse(InternalServerError, entity = "Serial port timeout"))
      case err =>
        extractUri { uri =>
          log.error(s"Request to $uri could not be handled normally: ${err.getLocalizedMessage}")
          complete(HttpResponse(InternalServerError, entity = err.getLocalizedMessage))
        }
    }

  val handler: Route = path("api" / "fiskal" / IntNumber / Segment) { (terminalId, command) =>
    get {
      log.info(s"GET Request for TerminalId[$terminalId], command[$command]")
      implicit val password: Password = ConfigService.getPrinter(s"printer$terminalId")
        .map(_.password).getOrElse(Password())

      command match {
        case TURN_TO => val date = LocalDate.now()
          val time = LocalTime.now()
          success(TurnTo(date, time))
        case PRINT_DATETIME => success(PrinterDateTime())
        case FLAG_STATE => success(FlagState())
        case PAPER_CUT => success(PaperCut())
        case REPORT_X => success(ReportX())
        case REPORT_Z => success(ReportZ())
      }
    } ~
      post {
        log.info(s"POST request for TerminalId[$terminalId], command[$command]")
        complete(HttpResponse(NoContent))
      }
  }

  private def success(cmd: DreamkasCommand): Route = onSuccess(
    (printer1 ? HttpService.Command(cmd)).mapTo[Option[DreamkasError]]
  ) {
    _.map(_.httpResponse).getOrElse(complete(HttpResponse(NoContent)))
  }

  val bindingFuture: Future[Http.ServerBinding] = Http().bindAndHandle(handler, ConfigService.getHost, ConfigService.getPort)

  def unbind: Unit = {
    bindingFuture.onComplete(_ => system.terminate())
  }

}

object HttpService {

  case class Command(cmd: DreamkasCommand)

}
