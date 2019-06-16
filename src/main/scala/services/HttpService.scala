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
import cats.syntax.either._
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport._
import models.DocumentType.{Refund, Service}
import models.api.{Cashier, Receipt, Ticket}
import models.dreamkas.ModelTypes.ErrorOr
import models.dreamkas.commands.UrlSegment._
import models.dreamkas.commands.{Command => DreamkasCommand, _}
import models.dreamkas.errors.DreamkasError
import models.dreamkas.{Big, DocumentTypeMode, Password, Small}
import services.HttpService.{ErrorOrOk, Msg, MsgNoAnswer, TIMEOUT}
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
      val printer = printer1
      log.info(s"GET Request for TerminalId[$terminalId], command[$command]")
      implicit val password: Password = getPassword(terminalId)

      command match {
        case TURN_TO => val date = LocalDate.now()
          val time = LocalTime.now()
          success(printer, TurnTo(date, time))
        case PRINT_DATETIME => success(printer, PrinterDateTime())
        case FLAG_STATE => success(printer, DocumentCancel())
        case PAPER_CUT => success(printer, PaperCut())
        case REPORT_X => success(printer, ReportX())
      }
    } ~
      post {
        implicit val password: Password = getPassword(terminalId)
        command match {
          case OPEN_SESSION => entity(as[Option[Cashier]]) { cashierO =>
            log.info(s"POST request for TerminalId[$terminalId], command[$command], cashierName[${cashierO.map(_.name)}]")

            success(
              askPrinter(
                printer1, Msg(OpenSession(cashierO))
              )
            )
          }
          case REPORT_Z => entity(as[Option[Cashier]]) { cashierO =>
            log.info(s"POST request for TerminalId[$terminalId], command[$command], cashierName[${cashierO.map(_.name)}]")

            success(
              askPrinter(
                printer1, Msg(ReportZ(cashierO))
              )
            )
          }
          case RECEIPT => entity(as[Receipt]) { receipt =>
            log.info(s"POST request for TerminalId[$terminalId], command[$command], receipt[$receipt]")
            withRequestTimeout(1.minute) {
              successReceipt(receipt, printer1)
            }
          }
        }
      }
  }

  private def askPrinter(printer: ActorRef, command: Msg): Future[ErrorOr] =
    (printer1 ? command).mapTo[ErrorOr]

  private def successReceipt(receipt: Receipt, printer: ActorRef)(implicit password: Password): Route = onSuccess {
    val result: Future[ErrorOr] = if (receipt.documentType == Refund) {
      printReceipt(receipt, printer)
    } else {
      for {
        receiptResponce <- printReceipt(receipt, printer)
        _ <- printTickets(receipt.tickets, printer)
      } yield receiptResponce
    }
    result
  }(_.toResponse)

  private def printReceipt(receipt: Receipt, printer: ActorRef)(implicit password: Password) = {
    val taxMode = receipt.taxMode
    val paymentMode = receipt.paymentMode

    for {
      _ <- askPrinter(printer, Msg(DocumentOpen(
        typeMode = DocumentTypeMode(receipt.documentType, packet = true),
        cashier = receipt.cashier,
        number = receipt.checkId,
        taxMode = receipt.taxMode
      )))
      _ = receipt.tickets.foreach { ticket =>
        printer ! MsgNoAnswer(DocumentAddPosition(ticket, taxMode, paymentMode))
      }
      _ = printer ! MsgNoAnswer(DocumentSubTotal())
      _ = printer ! MsgNoAnswer(DocumentSubTotal())
      _ = printer ! MsgNoAnswer(DocumentPayment(receipt))
      receiptResponse <- askPrinter(printer, Msg(DocumentClose()))
    } yield receiptResponse
  }

  private def printTickets(tickets: List[Ticket], printer: ActorRef)(implicit password: Password): Future[ErrorOr] = {

    def printTicket(ticket: Ticket): Future[ErrorOr] = {
      for {
        _ <- askPrinter(printer, Msg(DocumentOpen(
          typeMode = DocumentTypeMode(Service, packet = true)
        )))
        _ = printer ! MsgNoAnswer(DocumentPrint(s"${ticket.hall} ${ticket.perfDate} ${ticket.perfTime}", Big, true))
        _ = printer ! MsgNoAnswer(DocumentPrint(s"+${ticket.ageLimit} ${ticket.showName}", Big))
        _ = printer ! MsgNoAnswer(DocumentPrint(s"Ряд ${ticket.row} Место ${ticket.place}", Big, true))
        _ = printer ! MsgNoAnswer(DocumentPrint(s"пожалуйста, сохраняйте этот бланк до конца сеанса", Small))
        _ = printer ! MsgNoAnswer(DocumentPrint(s"Билет ${ticket.series} ${ticket.number}", Small))
        ticketResponse <- askPrinter(printer, Msg(DocumentClose()))
      } yield {
        ticketResponse.leftMap(_.toLog)
      }
    }

    tickets.foldLeft(Future.successful(List.empty[ErrorOr])) { (resF, ticket) =>
      for {
        prev <- resF
        next <- printTicket(ticket)
      } yield prev :+ next
    }.map(_.headOption.getOrElse(().asRight[DreamkasError]))
  }

  private def success(printer: ActorRef, cmd: DreamkasCommand): Route = success(askPrinter(printer, Msg(cmd)))

  private def success(magnet: Future[ErrorOr]): Route = onSuccess(magnet)(_.toResponse)

  private def getPassword(terminalId: Int) = ConfigService.getPrinter(s"printer$terminalId")
    .map(_.password).getOrElse(Password())

  val bindingFuture: Future[Http.ServerBinding] = Http().bindAndHandle(handler, ConfigService.getHost, ConfigService.getPort)

  def unbind: Unit = {
    bindingFuture.onComplete(_ => system.terminate())
  }

}

object HttpService {

  implicit class ErrorOrOk(errorOr: ErrorOr) {

    def toResponse: Route = errorOr.fold(_.httpResponse, _ => complete(HttpResponse(NoContent)))

  }

  val TIMEOUT = 30

  case class Msg(cmd: DreamkasCommand)

  case class MsgNoAnswer(cmd: DreamkasCommand)

}
