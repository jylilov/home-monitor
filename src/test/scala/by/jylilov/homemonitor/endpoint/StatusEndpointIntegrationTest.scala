package by.jylilov.homemonitor.endpoint

import by.jylilov.homemonitor.HomeMonitorIntegrationTest
import cats.effect._
import cats.implicits._
import org.http4s.Status
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

class StatusEndpointIntegrationTest extends HomeMonitorIntegrationTest {

  describeIntegrationTest("should handle status request") {
    for {
      testEndpoint <- testEndpoint.get
      assertions <- httpClient.use { httpClient =>
        httpClient.get(s"$testEndpoint/status")(_.pure[IO])
          .asserting(_.status shouldBe Status.Ok)
      }
    } yield assertions
  }
}
