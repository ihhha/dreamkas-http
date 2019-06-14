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
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport._
import models.api.Cashier
import models.dreamkas.Password
import models.dreamkas.commands.UrlSegment._
import models.dreamkas.commands.{Command => DreamkasCommand, _}
import models.dreamkas.errors.DreamkasError
import services.HttpService.TIMEOUT
import utils.Logging

class HttpService(printer1: ActorRef, printer2: Option[ActorRef] = None) extends Logging {

  implicit val system: ActorSystem = ActorSystem("http-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  implicit val timeout: Timeout = Timeout(TIMEOUT.seconds)

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
      implicit val password: Password = getPassword(terminalId)

      command match {
        case TURN_TO => val date = LocalDate.now()
          val time = LocalTime.now()
          success(TurnTo(date, time))
        case PRINT_DATETIME => success(PrinterDateTime())
        case FLAG_STATE => success(DocumentCancel())
        case PAPER_CUT => success(PaperCut())
        case REPORT_X => success(ReportX())
      }
    } ~
      post {
        implicit val password: Password = getPassword(terminalId)
        command match {
          case OPEN_SESSION => entity(as[Option[Cashier]]) { cashierO =>
            log.info(s"POST request for TerminalId[$terminalId], command[$command], cashierName[${cashierO.map(_.name)}]")

            success(OpenSession(cashierO))
          }
          case REPORT_Z => entity(as[Option[Cashier]]) { cashierO =>
            log.info(s"POST request for TerminalId[$terminalId], command[$command], cashierName[${cashierO.map(_.name)}]")

            success(ReportZ(cashierO))
          }
        }
      }
  }

  private def success(cmd: DreamkasCommand): Route = onSuccess(
    (printer1 ? HttpService.Command(cmd)).mapTo[Option[DreamkasError]]
  ) {
    _.map(_.httpResponse).getOrElse(complete(HttpResponse(NoContent)))
  }

  private def getPassword(terminalId: Int) = ConfigService.getPrinter(s"printer$terminalId")
    .map(_.password).getOrElse(Password())

  val bindingFuture: Future[Http.ServerBinding] = Http().bindAndHandle(handler, ConfigService.getHost, ConfigService.getPort)

  def unbind: Unit = {
    bindingFuture.onComplete(_ => system.terminate())
  }

}

object HttpService {
  val TIMEOUT = 30

  case class Command(cmd: DreamkasCommand)

}
