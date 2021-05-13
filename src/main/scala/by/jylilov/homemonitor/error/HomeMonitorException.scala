package by.jylilov.homemonitor.error

import org.http4s.Status

class HomeMonitorException(
  val error: ErrorType,
  val details: Option[ErrorDetails] = None,
  val statusCode: Status = Status.InternalServerError,
  val debugMessage: String = null,
  cause: Throwable = null,
) extends RuntimeException(debugMessage, cause)
