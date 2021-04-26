package by.jylilov.homemonitor.endpoint

import cats.effect._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

class StatusEndpoints[F[_] : Sync] extends Http4sDsl[F] {
  def endpoints(): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root => Ok()
    }
}
