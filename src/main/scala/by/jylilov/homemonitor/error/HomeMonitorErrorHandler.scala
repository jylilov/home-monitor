package by.jylilov.homemonitor.error

import by.jylilov.homemonitor.codec.JsonEntityCodec.encoder
import by.jylilov.homemonitor.domain.ErrorResponseBody
import cats.Monad
import cats.implicits._
import io.circe.generic.auto._
import org.http4s._
import org.http4s.server.ServiceErrorHandler
import org.log4s.getLogger

import scala.util.control.NonFatal

class HomeMonitorErrorHandler[F[_] : Monad] extends ServiceErrorHandler[F] {

  private[this] val logger = getLogger

  override def apply(request: Request[F]): PartialFunction[Throwable, F[Response[F]]] = {
    case e: HomeMonitorException =>
      logger.debug(e)("Error on request processing")
      Response[F](e.statusCode, request.httpVersion)
        .withEntity(ErrorResponseBody(e.error, e.details))
        .pure[F]
    case e: MessageFailure =>
      logger.debug(s"Message failure ${e.getClass.getName}: ${e.message}")
      val defaultResponse = e.toHttpResponse[F](request.httpVersion)
      val (status, errorType) = e match {
        case _: UnsupportedMediaTypeFailure => Status.UnsupportedMediaType -> ErrorType.UnsupportedMediaType
        case _: InvalidMessageBodyFailure | _: MalformedMessageBodyFailure | _: ParseFailure =>
          Status.BadRequest -> ErrorType.InvalidRequestBody
        case _ =>
          logger.warn(e)("Unexpected message failure")
          defaultResponse.status -> ErrorType.MessageFailure
      }
      Response[F](status, defaultResponse.httpVersion)
        .withEntity(ErrorResponseBody(errorType, Some(StringErrorDetails(e.message))))
        .pure[F]
    case NonFatal(e) =>
      logger.error(e)("Unexpected error")
      Response[F](Status.InternalServerError, request.httpVersion)
        .withEntity(ErrorResponseBody(ErrorType.InternalServerError))
        .pure[F]
  }
}
