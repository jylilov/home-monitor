package by.jylilov.homemonitor

import by.jylilov.homemonitor.config.{DbConfig, HomeMonitorApplicationConfig, HttpServerConfig}
import by.jylilov.homemonitor.endpoint.{SensorDataEndpoints, StatusEndpoints}
import by.jylilov.homemonitor.repository.DbSensorDataRepository
import by.jylilov.homemonitor.service.DefaultSensorService
import cats.effect._
import cats.effect.unsafe.IORuntime
import com.typesafe.config.ConfigFactory
import org.http4s.HttpApp
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import scalikejdbc.{ConnectionPool, ConnectionPoolSettings}

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

  def initDb(config: DbConfig): String = {

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

  def stream[F[_] : Async](runtime: IORuntime): fs2.Stream[F, ExitCode] = {
    val config = loadConfig()
    val dbName = initDb(config.db)
    for {
      exitCode <- BlazeServerBuilder[F](runtime.compute)
        .bindHttp(config.httpServer.port, config.httpServer.host)
        .withHttpApp(httpApp[F](dbName))
        .serve
    } yield exitCode
  }

  override def run(args: List[String]): IO[ExitCode] = {
    stream[IO](runtime).compile.drain.as(ExitCode.Success)
  }
}
