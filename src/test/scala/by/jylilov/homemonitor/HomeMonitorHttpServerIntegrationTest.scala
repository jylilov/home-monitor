package by.jylilov.homemonitor

import by.jylilov.homemonitor.testcontainers.TestContainersUtils
import cats.effect._
import cats.implicits._
import org.http4s.Status
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.testcontainers.containers.GenericContainer

class HomeMonitorHttpServerIntegrationTest extends IntegrationSpec {

  override protected val server: IO[ExitCode] = {
    TestContainersUtils.testContainerResource[IO](
      new GenericContainer("timescale/timescaledb:2.1.1-pg13")
        .withEnv("POSTGRES_PASSWORD", "root")
    ).use { _ =>
      HomeMonitorHttpServer.stream[IO](ioRuntime).as(ExitCode.Success)
    }
  }

  describeIntegrationTest("should handle status request") {
    httpClient.use { httpClient =>
      httpClient.get("http://localhost:9000/status")(_.pure[IO])
        .asserting(_.status shouldBe Status.Ok)
    }
  }
}
