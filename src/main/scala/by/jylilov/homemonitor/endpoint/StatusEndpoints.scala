package by.jylilov.homemonitor.endpoint

import cats.effect.Sync
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

class StatusEndpoints[F[_] : Sync] extends Http4sDsl[F] {
  def endpoints(): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root => Ok()
    }
}

object StatusEndpoints {
  def endpoints[F[_] : Sync](): HttpRoutes[F] = new StatusEndpoints[F].endpoints()
}
