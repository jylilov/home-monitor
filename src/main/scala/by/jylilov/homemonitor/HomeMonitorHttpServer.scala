package by.jylilov.homemonitor

import by.jylilov.homemonitor.config.DbConfig
import by.jylilov.homemonitor.config.loader.ApplicationConfigLoader
import by.jylilov.homemonitor.endpoint.{SensorDataEndpoints, StatusEndpoints}
import by.jylilov.homemonitor.repository.DbSensorDataRepository
import by.jylilov.homemonitor.service.DefaultSensorService
import cats.effect._
import cats.implicits._
import org.flywaydb.core.Flyway
import org.http4s.HttpApp
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import scalikejdbc.{ConnectionPool, ConnectionPoolSettings}

import scala.concurrent.ExecutionContext

class HomeMonitorHttpServer[F[_] : Async](
  configLoader: ApplicationConfigLoader[F],
  computeExecutionContext: ExecutionContext
) {

  private implicit def logger: Logger[F] = Slf4jLogger.getLogger[F]

  // TODO extract to separate place
  private def initDb(config: DbConfig): F[String] = {
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
      _ <- Sync[F].blocking {
        val dataSource = ConnectionPool.dataSource(dbName)
        val flyway = Flyway.configure().dataSource(dataSource).load()
        flyway.migrate()
      }
    } yield dbName
  }

  private def httpApp(dbName: String): HttpApp[F] = {
    Router(
      "/sensor" -> SensorDataEndpoints.endpoints[F](
        new DefaultSensorService(
          new DbSensorDataRepository(dbName)
        )
      ),
      "/status" -> StatusEndpoints.endpoints[F]()
    ).orNotFound
  }

  def serve: F[ExitCode] = {
    for {
      config <- configLoader.load()
      dbName <- initDb(config.db)
      _ <- BlazeServerBuilder[F](computeExecutionContext)
        .bindHttp(config.httpServer.port, config.httpServer.host)
        .withBanner(null)
        .withHttpApp(httpApp(dbName))
        .serve
        .compile
        .drain
    } yield ExitCode.Success
  }
}
