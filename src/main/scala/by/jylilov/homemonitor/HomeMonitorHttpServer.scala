package by.jylilov.homemonitor

import by.jylilov.homemonitor.endpoint.SensorDataEndpoints
import by.jylilov.homemonitor.repository.DbSensorDataRepository
import by.jylilov.homemonitor.service.DefaultSensorService
import cats.effect.{Async, Clock, ConcurrentEffect, ContextShift, ExitCode, IO, IOApp, Sync, Timer}
import org.http4s.HttpApp
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder

import scala.concurrent.ExecutionContext.global

object HomeMonitorHttpServer extends IOApp {

  private def httpApp[F[_] : Sync : Clock : Async : ContextShift]: HttpApp[F] = {
    Router(
      "/sensor" -> SensorDataEndpoints.endpoints[F](
        new DefaultSensorService(
          new DbSensorDataRepository
        )
      )
    ).orNotFound
  }

  private def stream[F[_] : ConcurrentEffect : ContextShift : Timer]: fs2.Stream[F, ExitCode] = {
    for {
      exitCode <- BlazeServerBuilder[F](global)
        .bindHttp(9000)
        .withHttpApp(httpApp = httpApp[F])
        .serve
    } yield exitCode
  }

  override def run(args: List[String]): IO[ExitCode] = {
    stream[IO].compile.drain.as(ExitCode.Success)
  }
}
