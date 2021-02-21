package by.jylilov.homemonitor

import by.jylilov.homemonitor.config.{DbConfig, HomeMonitorApplicationConfig, HttpServerConfig}
import by.jylilov.homemonitor.endpoint.{SensorDataEndpoints, StatusEndpoints}
import by.jylilov.homemonitor.repository.DbSensorDataRepository
import by.jylilov.homemonitor.service.DefaultSensorService
import cats.effect.{Async, Clock, ConcurrentEffect, ContextShift, ExitCode, IO, IOApp, Sync, Timer}
import com.typesafe.config.ConfigFactory
import org.http4s.HttpApp
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder

import scala.concurrent.ExecutionContext.global

object HomeMonitorHttpServer extends IOApp {

  def loadConfig(): HomeMonitorApplicationConfig = {

    val config = ConfigFactory.load()

    val httpServer = HttpServerConfig(
      config.getString("http.server.host"),
      config.getInt("http.server.port")
    )

    val db = DbConfig(
      config.getString("db.driver"),
      config.getString("db.jdbcUrl"),
      config.getString("db.username"),
      config.getString("db.password")
    )

    HomeMonitorApplicationConfig(db, httpServer)
  }

  private def httpApp[F[_] : Sync : Clock : Async : ContextShift]: HttpApp[F] = {
    Router(
      "/sensor" -> SensorDataEndpoints.endpoints[F](
        new DefaultSensorService(
          new DbSensorDataRepository(
            loadConfig().db
          )
        )
      ),
      "/status" -> StatusEndpoints.endpoints[F]()
    ).orNotFound
  }

  def stream[F[_] : ConcurrentEffect : ContextShift : Timer]: fs2.Stream[F, ExitCode] = {
    val config = loadConfig()
    for {
      exitCode <- BlazeServerBuilder[F](global)
        .bindHttp(config.httpServer.port, config.httpServer.host)
        .withHttpApp(httpApp = httpApp[F])
        .serve
    } yield exitCode
  }

  override def run(args: List[String]): IO[ExitCode] = {
    stream[IO].compile.drain.as(ExitCode.Success)
  }
}
