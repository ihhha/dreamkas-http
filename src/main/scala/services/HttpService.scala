package services

import java.time.{LocalDate, LocalTime}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.Directives.{onSuccess, _}
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import akka.pattern.AskTimeoutException
import akka.stream.ActorMaterializer
import cats.syntax.either._
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport._
import models.DocumentType.Service
import models.api.{Cashier, Receipt, Ticket}
import models.dreamkas.ModelTypes.ErrorOr
import models.dreamkas.commands.UrlSegment._
import models.dreamkas.commands.{Command => DreamkasCommand, _}
import models.dreamkas.errors.DreamkasError
import models.dreamkas.errors.DreamkasError.NoPrinterConnected
import models.dreamkas.{Big, DocumentTypeMode, Password, Small}
import services.HttpService._
import utils.Logging
import utils.helpers.RouteHelper

class HttpService(printer1: ActorRef, printer2: Option[ActorRef] = None, origin: String)
  extends Logging with RouteHelper {

  implicit val system: ActorSystem = ActorSystem("http-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  implicit def myExceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case _: AskTimeoutException => complete(HttpResponse(InternalServerError, entity = "Serial port timeout"))
      case err =>
        extractUri { uri =>
          log.error(s"Request to $uri could not be handled normally: ${err.getLocalizedMessage}")
          complete(HttpResponse(InternalServerError, entity = err.getLocalizedMessage))
        }
    }

  val optionsRoute: Route = options(complete(HttpResponse(OK)))

  val handler: Route = respondWithHeaders(
    RawHeader("Access-Control-Allow-Origin", origin),
    RawHeader("Access-Control-Allow-Methods", "OPTIONS,POST,GET"),
    RawHeader("Access-Control-Allow-Headers", "Content-type"),
    RawHeader("Access-Control-Max-Age", "3600")
  ) {
    optionsRoute ~
      path("api" / "fiskal" / IntNumber / Segment) { (terminalId, command) =>
        terminalId match {
          case 1 => processFiscal(printer1, command, 1)
          case 2 => printer2.map(processFiscal(_, command, 2))
            .getOrElse(NoPrinterConnected.httpResponse)
          case _ => NoPrinterConnected.httpResponse
        }
      }
  }

  private def processFiscal(printer: ActorRef, command: String, terminalId: Int): Route =
    get {
      log.info(s"GET Request for TerminalId[$terminalId], command[$command]")
      implicit val password: Password = getPassword(terminalId)

      command match {
        case PING => success(printer, Ping)
        case TURN_TO => val date = LocalDate.now()
          val time = LocalTime.now()
          success(printer, TurnTo(date, time, password))
        case PRINT_DATETIME => success(printer, PrinterDateTime(password))
        case FLAG_STATE => success(printer, FlagState(password))
        case PAPER_CUT => success(printer, PaperCut(password))
        case REPORT_X => success(printer, ReportX(password))
        case CANCEL_RECEIPT => success(printer, DocumentCancel(password))
      }
    } ~
      post {
        implicit val password: Password = getPassword(terminalId)
        command match {
          case OPEN_SESSION => entity(as[Option[Cashier]]) { cashierO =>
            log.info(s"POST request for TerminalId[$terminalId], command[$command], cashierName[${cashierO.map(_.name)}]")

            success(
              askPrinter(
                printer1, Msg(OpenSession(password, cashierO))
              )
            )
          }
          case REPORT_Z => entity(as[Option[Cashier]]) { cashierO =>
            log.info(s"POST request for TerminalId[$terminalId], command[$command], cashierName[${cashierO.map(_.name)}]")

            success(
              askPrinter(
                printer1, Msg(ReportZ(cashierO, password))
              )
            )
          }
          case RECEIPT => entity(as[Receipt]) { receipt =>
            log.info(s"POST request for TerminalId[$terminalId], command[$command], receipt[$receipt]")
            withRequestTimeout(1.minute) {
              successReceipt(receipt, printer1)
            }
          }
          case TICKET => entity(as[Receipt]) { receipt =>
            log.info(s"POST request for TerminalId[$terminalId], command[$command], receipt[$receipt]")
            withRequestTimeout(1.minute) {
              successTicket(receipt, printer1)
            }
          }
        }
      }

  private def successReceipt(receipt: Receipt, printer: ActorRef)(implicit password: Password): Route = onSuccess {
    printReceipt(receipt, printer)
  }(_.toResponse)

  private def successTicket(receipt: Receipt, printer: ActorRef)(implicit password: Password): Route = onSuccess {
    printTickets(receipt.tickets, printer)
  }(_.toResponse)

  private def printReceipt(receipt: Receipt, printer: ActorRef)(implicit password: Password) = {
    val taxMode = receipt.taxMode
    val paymentMode = receipt.paymentMode

    for {
      _ <- askPrinter(printer, Msg(DocumentOpen(
        typeMode = DocumentTypeMode(receipt.documentType, packet = true),
        cashier = receipt.cashier,
        number = receipt.checkId,
        pass = password,
        taxMode = receipt.taxMode
      )))
      _ <- Future.successful(receipt.tickets.foreach { ticket =>
        printer ! MsgNoAnswer(DocumentAddPosition(ticket, taxMode, paymentMode, password))
      })
      _ <- Future.successful(printer ! MsgNoAnswer(DocumentSubTotal(password)))
      _ <- Future.successful(printer ! MsgNoAnswer(DocumentSubTotal(password)))
      _ <- Future.successful(printer ! MsgNoAnswer(DocumentPayment(receipt, password)))
      receiptResponse <- askPrinter(printer, Msg(DocumentClose(password)))
    } yield receiptResponse
  }

  private def printTickets(tickets: List[Ticket], printer: ActorRef)(implicit password: Password): Future[ErrorOr] = {

    def printTicket(ticket: Ticket): Future[ErrorOr] = {
      for {
        _ <- askPrinter(printer, Msg(DocumentOpen(
          typeMode = DocumentTypeMode(Service, packet = true), pass = password
        )))
        _ = printer ! MsgNoAnswer(DocumentPrint(s"${ticket.hall} ${ticket.perfDate} ${ticket.perfTime}", Big, password, true))
        _ = printer ! MsgNoAnswer(DocumentPrint(s"+${ticket.ageLimit} ${ticket.showName}", Big, password))
        _ = printer ! MsgNoAnswer(DocumentPrint(s"Ряд ${ticket.row} Место ${ticket.place}", Big, password, true))
        _ = printer ! MsgNoAnswer(DocumentPrint(s"пожалуйста, сохраняйте этот бланк до конца сеанса", Small, password))
        _ = printer ! MsgNoAnswer(DocumentPrint(s"Билет ${ticket.series} ${ticket.number}", Small, password))
        ticketResponse <- askPrinter(printer, Msg(DocumentClose(password)))
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

  // todo Remove after move all comands in actor
  private def getPassword(terminalId: Int) = ConfigService.getPrinter(s"printer$terminalId")
    .map(_.password).getOrElse(Password())

  val bindingFuture: Future[Http.ServerBinding] = Http().bindAndHandle(handler, ConfigService.getHost, ConfigService.getPort)

  def unbind(): Unit = {
    bindingFuture.onComplete(_ => system.terminate())
  }

}

object HttpService {

  case class Msg(cmd: DreamkasCommand)

  case class MsgNoAnswer(cmd: DreamkasCommand)

}
