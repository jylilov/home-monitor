package by.jylilov.homemonitor

import by.jylilov.homemonitor.config.loader.TypesafeApplicationConfigLoader
import by.jylilov.homemonitor.testcontainers.TestContainersUtils
import cats.effect._
import org.testcontainers.containers.GenericContainer

class HomeMonitorIntegrationTest extends ServerIntegrationTest("HomeMonitor server") {

  protected val testEndpoint: Deferred[IO, String] = Deferred.unsafe

  private val configLoader = TypesafeApplicationConfigLoader[IO]

  override protected val server: IO[ExitCode] = for {
    config <- configLoader.load()
    _ <- testEndpoint.complete(s"http://${config.httpServer.host}:${config.httpServer.port}")
    result <- TestContainersUtils.testContainerResource[IO]({ () =>
      val container = new GenericContainer("timescale/timescaledb:2.1.1-pg13")
      container.addExposedPort(5432)
      container.addEnv("POSTGRES_DB", "home_monitor")
      container.addEnv("POSTGRES_USER", config.db.username)
      container.addEnv("POSTGRES_PASSWORD", config.db.password)
      container
    }).use { container =>

      val newConfig = config.copy(
        db = config.db.copy(
          jdbcUrl = s"jdbc:postgresql://localhost:${container.getFirstMappedPort}/home_monitor"
        )
      )

      new HomeMonitorHttpServer[IO](() => IO(newConfig), ioRuntime.compute).serve
    }
  } yield result
}
