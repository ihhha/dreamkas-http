package utils.helpers

import scala.concurrent.Future
import scala.concurrent.duration._

import akka.actor.ActorRef
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes.NoContent
import akka.http.scaladsl.server.Directives.{complete, onSuccess}
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import models.dreamkas.ModelTypes.ErrorOr
import models.dreamkas.commands.Command
import services.HttpService.Msg

trait RouteHelper {

  val TIMEOUT = 30

  implicit val timeout: Timeout = Timeout(TIMEOUT.seconds)

  def askPrinter(printer: ActorRef, command: Msg): Future[ErrorOr] =
    (printer ? command).mapTo[ErrorOr]

  def success(printer: ActorRef, cmd: Command): Route = success(askPrinter(printer, Msg(cmd)))

  def success(magnet: Future[ErrorOr]): Route = onSuccess(magnet)(_.toResponse)

  implicit class ErrorOrOk(errorOr: ErrorOr) {

    def toResponse: Route = errorOr.fold(_.httpResponse, _ => complete(HttpResponse(NoContent)))

  }

}
