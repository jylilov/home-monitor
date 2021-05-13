package by.jylilov.homemonitor.domain

import by.jylilov.homemonitor.error.{ErrorDetails, ErrorType}

case class ErrorResponseBody(
  error: ErrorType,
  details: Option[ErrorDetails] = None
)
