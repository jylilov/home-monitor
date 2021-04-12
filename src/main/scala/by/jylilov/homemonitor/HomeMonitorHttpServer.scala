package by.jylilov.homemonitor

import by.jylilov.homemonitor.config.{DbConfig, HomeMonitorApplicationConfig, HttpServerConfig}
import by.jylilov.homemonitor.endpoint.{SensorDataEndpoints, StatusEndpoints}
import by.jylilov.homemonitor.repository.DbSensorDataRepository
import by.jylilov.homemonitor.service.DefaultSensorService
import cats.effect._
import cats.effect.unsafe.IORuntime
import cats.implicits._
import com.typesafe.config.{Config, ConfigFactory}
import org.http4s.HttpApp
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import scalikejdbc.{ConnectionPool, ConnectionPoolSettings}

object HomeMonitorHttpServer extends IOApp {

  private implicit def logger[F[_] : Sync]: Logger[F] = Slf4jLogger.getLogger[F]

  def parseConfig[F[_] : Sync](config: Config): F[HomeMonitorApplicationConfig] =
    Sync[F].delay {
      val db = DbConfig(
        config.getString("db.driver"),
        config.getString("db.jdbcUrl"),
        config.getString("db.username"),
        config.getString("db.password")
      )
      val httpServer = HttpServerConfig(
        config.getString("http.server.host"),
        config.getInt("http.server.port")
      )
      HomeMonitorApplicationConfig(db, httpServer)
    }

  def loadConfig[F[_] : Sync](): F[HomeMonitorApplicationConfig] = {
    for {
      _ <- Logger[F].info("Loading configuration")
      config <- Sync[F].blocking(ConfigFactory.load())
      parsedConfig <- parseConfig(config)
      _ <- Logger[F].debug(s"Loaded configuration: $parsedConfig")
    } yield parsedConfig
  }

  def initDb[F[_] : Sync](config: DbConfig): F[String] = {
    for {
      _ <- Logger[F].info("Initializing DB connection")
      dbName <- Sync[F].blocking {
        val name = "application_db"

        ConnectionPool.add(
          name = name,
          url = config.jdbcUrl,
          user = config.username,
          password = config.password,
          settings = ConnectionPoolSettings(
            driverName = config.driver
          )
        )
        name
      }
    } yield dbName
  }

  private def httpApp[F[_] : Async](dbName: String): HttpApp[F] = {
    Router(
      "/sensor" -> SensorDataEndpoints.endpoints[F](
        new DefaultSensorService(
          new DbSensorDataRepository(dbName)
        )
      ),
      "/status" -> StatusEndpoints.endpoints[F]()
    ).orNotFound
  }

  def stream[F[_] : Async](runtime: IORuntime): F[Unit] = {
    for {
      config <- loadConfig[F]()
      dbName <- initDb[F](config.db)
      _ <- BlazeServerBuilder[F](runtime.compute)
        .bindHttp(config.httpServer.port, config.httpServer.host)
        .withBanner(null)
        .withHttpApp(httpApp[F](dbName))
        .serve
        .compile
        .drain
    } yield ()
  }

  override def run(args: List[String]): IO[ExitCode] = {
    stream[IO](runtime).as(ExitCode.Success)
  }
}
