package by.jylilov.homemonitor

import by.jylilov.homemonitor.config.AppConfig
import by.jylilov.homemonitor.config.initializer._
import by.jylilov.homemonitor.config.loader.TypesafeAppConfigLoader
import by.jylilov.homemonitor.test.{HttpClientTestUtils, TestContext}
import by.jylilov.homemonitor.testcontainers.TimescaleTestContainer
import cats.effect._
import cats.effect.testing.scalatest.{AsyncIOSpec, CatsResourceIO}
import cats.implicits._
import org.http4s.client.blaze.BlazeClientBuilder
import org.scalatest.funspec.FixtureAsyncFunSpec
import org.scalatest.matchers.should.Matchers
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

class HomeMonitorIntegrationTest extends FixtureAsyncFunSpec
  with AsyncIOSpec
  with Matchers
  with CatsResourceIO[TestContext[IO]]
  with HttpClientTestUtils {

  private implicit val logger: Logger[IO] = Slf4jLogger.getLogger

  private val configLoader = TypesafeAppConfigLoader[IO]

  private def server(config: AppConfig, initialized: Deferred[IO, Unit]): IO[ExitCode] = {
    new HomeMonitorHttpServer[IO](
      () => config.pure[IO],
      CombinedAppInitializer(
        ScalikeConnectionPoolAppInitializer[IO],
        ScalikeFlywayAppInitializer[IO],
        _ => initialized.complete().as()
      ),
      ioRuntime.compute
    ).serve
  }

  private def testContainer(config: AppConfig): Resource[IO, TimescaleTestContainer] = {
    TimescaleTestContainer.resource[IO](config.db.username, config.db.password)
  }

  private def updateConfig(config: AppConfig, container: TimescaleTestContainer): AppConfig = {
    config.copy(
      db = config.db.copy(
        jdbcUrl = s"jdbc:postgresql://localhost:${container.getFirstMappedPort}/home_monitor"
      )
    )
  }

  override val resource: Resource[IO, TestContext[IO]] = Resource.make(
    for {
      initialized <- Deferred[IO, Unit]
      config <- configLoader.load()
      testEndpoint = s"http://${config.httpServer.host}:${config.httpServer.port}"
      serverFiber <- testContainer(config).use { container =>
        val finalConfig = updateConfig(config, container)
        server(finalConfig, initialized)
      }.start
      _ <- initialized.get
    } yield TestContext(serverFiber, testEndpoint, BlazeClientBuilder[IO](ioRuntime.compute).resource)
  ) { ctx =>
    for {
      _ <- ctx.serverFiber.cancel
      serverResult <- ctx.serverFiber.join
      _ <- serverResult.fold(IO.unit, e => Logger[IO].error(e)("Server was failed"), _ => IO.unit)
    } yield ()
  }
}
