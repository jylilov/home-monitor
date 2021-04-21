package by.jylilov.homemonitor

import by.jylilov.homemonitor.config.ApplicationConfig
import by.jylilov.homemonitor.config.loader.ApplicationConfigLoader
import by.jylilov.homemonitor.domain.DbInfo
import by.jylilov.homemonitor.endpoint.{SensorDataEndpoints, StatusEndpoints}
import by.jylilov.homemonitor.repository.{DbInitializer, DbSensorDataRepository}
import by.jylilov.homemonitor.service.DefaultSensorService
import cats.effect._
import cats.implicits._
import org.http4s.HttpApp
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

import scala.concurrent.ExecutionContext

class HomeMonitorHttpServer[F[_] : Async](
  configLoader: ApplicationConfigLoader[F],
  dbInitializer: DbInitializer[F],
  computeExecutionContext: ExecutionContext
) {

  private implicit def logger: Logger[F] = Slf4jLogger.getLogger[F]

  private def httpApp(config: ApplicationConfig): HttpApp[F] = {
    Router(
      "/sensor" -> SensorDataEndpoints.endpoints[F](
        new DefaultSensorService(
          new DbSensorDataRepository(DbInfo(config.db.name))
        )
      ),
      "/status" -> StatusEndpoints.endpoints[F]()
    ).orNotFound
  }

  def serve: F[ExitCode] = {
    for {
      config <- configLoader.load()
      _ <- dbInitializer.init(config.db)
      _ <- BlazeServerBuilder[F](computeExecutionContext)
        .bindHttp(config.httpServer.port, config.httpServer.host)
        .withBanner(null)
        .withHttpApp(httpApp(config))
        .serve
        .compile
        .drain
    } yield ExitCode.Success
  }
}
