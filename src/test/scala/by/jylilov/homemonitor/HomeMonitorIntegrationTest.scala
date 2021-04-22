package by.jylilov.homemonitor

import by.jylilov.homemonitor.config.loader.TypesafeApplicationConfigLoader
import by.jylilov.homemonitor.repository.{CombinedDbInitializer, ScalikeConnectionPoolDbInitializer, ScalikeFlywayDbInitializer}
import by.jylilov.homemonitor.test.TestContext
import by.jylilov.homemonitor.testcontainers.TestContainersUtils
import cats.effect._
import cats.implicits._
import io.circe._
import org.http4s._
import org.http4s.circe._
import org.testcontainers.containers.GenericContainer
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

class HomeMonitorIntegrationTest extends ServerIntegrationTest {

  private implicit val logger: Logger[IO] = Slf4jLogger.getLogger

  protected val testEndpoint: Deferred[IO, String] = Deferred.unsafe

  private val configLoader = TypesafeApplicationConfigLoader[IO]

  protected def getRequest(endpoint: String): IO[Request[IO]] = request(Method.GET, endpoint, None)
  protected def postRequest(endpoint: String, body: Json): IO[Request[IO]] = request(Method.POST, endpoint, Some(body))

  // TODO remove dirty hack
  override def beforeAll(): Unit = {
    super.beforeAll()
    Thread.sleep(10000)
  }

  protected def request(method: Method, endpoint: String, body: Option[Json]): IO[Request[IO]] =
    for {
      testEndpoint <- testEndpoint.get
      request = Request[IO](
        method = method,
        uri = Uri.unsafeFromString(s"$testEndpoint$endpoint"),
      )
      finalRequest = body match {
        case Some(body) => request.withEntity(body)
        case None => request
      }
    } yield finalRequest

  protected def executeRequest(request: IO[Request[IO]]): IO[Response[IO]] = {
    request.flatMap { request =>
      httpClient.use { httpClient =>
        httpClient.run(request).use(_.pure[IO])
      }
    }
  }

  override protected val server: IO[ExitCode] = for {
    config <- configLoader.load()
    // TODO if config loading failed then testEndpoint will cause infinite block
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

      new HomeMonitorHttpServer[IO](
        () => IO(newConfig),
        CombinedDbInitializer(
          ScalikeConnectionPoolDbInitializer[IO],
          ScalikeFlywayDbInitializer[IO]
        ),
        ioRuntime.compute
      ).serve
    }
  } yield result

  override val resource: Resource[IO, TestContext[IO]] = Resource.make(server.start.map(TestContext[IO])) { ctx =>
    for {
      _ <- ctx.serverFiber.cancel
      serverResult <- ctx.serverFiber.join
      _ <- serverResult.fold(IO.unit, e => Logger[IO].error(e)("Server was failed"), _ => IO.unit)
    } yield ()
  }
}
