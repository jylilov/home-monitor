package by.jylilov.homemonitor.error

import io.circe.Encoder
import io.circe.syntax._

sealed trait ErrorDetails

case class StringErrorDetails(details: String) extends ErrorDetails

object ErrorDetails {
  implicit val encoder: Encoder[ErrorDetails] =
    Encoder.instance {
      case StringErrorDetails(message) => message.asJson
    }
}
