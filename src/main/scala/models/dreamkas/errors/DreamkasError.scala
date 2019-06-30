package models.dreamkas.errors

import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes.InternalServerError
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.StandardRoute
import utils.Logging

trait DreamkasError extends Logging {
  def message: String

  def httpResponse: StandardRoute = complete(HttpResponse(InternalServerError, entity = message))

  def toLog: DreamkasError = {
    log.error(s"Error: $message")
    this
  }

  def dump(): Unit = log.error(s"Error: $message")
}

object DreamkasError {

  case object NoPrinterConfigured extends DreamkasError {
    val message = "No printer configured"
  }

  case object NoPrinterConnected extends DreamkasError {
    val message = "No printer connected"
  }

  case object PingFailed extends DreamkasError {
    val message = "Ping failed"
  }

  case object UnknownFormat extends DreamkasError {
    val message = "Failed to parse printer response"
  }

  case object NoEtxFound extends DreamkasError {
    val message = "Incorrect response end. No ETX found"
  }

  case object CrcError extends DreamkasError {
    val message = "Check CRC failed"
  }

  val NO_ERROR = "00"

}
