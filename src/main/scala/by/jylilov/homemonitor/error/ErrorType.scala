package by.jylilov.homemonitor.error

import io.circe.Encoder
import io.circe.syntax._

case class ErrorType(value: String)

object ErrorType {
  implicit val encoder: Encoder[ErrorType] = Encoder.instance(_.value.asJson)

  val InternalServerError: ErrorType = ErrorType("INTERNAL_SERVER_ERROR")
  val MessageFailure: ErrorType = ErrorType("MESSAGE_FAILURE")
  val UnsupportedMediaType: ErrorType = ErrorType("UNSUPPORTED_MEDIA_TYPE")
  val InvalidRequestBody: ErrorType = ErrorType("INVALID_REQUEST_BODY")
}
