package models.dreamkas.errors

import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes.InternalServerError
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.StandardRoute
import models.dreamkas.ModelTypes.Code

trait DreamkasError {
  def message: String

  def httpResponse: StandardRoute = complete(HttpResponse(InternalServerError, entity = message))
}

object DreamkasError {

  case object NoPrinterConnected extends DreamkasError {
    val message = "No printer connected"
  }

  case object UnknownFormat extends DreamkasError {
    val message = "Failed to parse printer response"
  }

  case object NoEtxFound extends DreamkasError {
    val message = "Incorrect response end. No ETX found"
  }

  case object WrongPacketIndex extends DreamkasError {
    val message = "Wrong packet index. Get response to other command"
  }

  case object CrcError extends DreamkasError {
    val message = "Check CRC failed"
  }

  val FunctionUnavailableWithSuchStatus: DreamkasError = ErrorWithCode("01")

  val NO_ERROR = "00"

}
