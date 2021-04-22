package by.jylilov.homemonitor.endpoint

import by.jylilov.homemonitor.HomeMonitorIntegrationTest
import io.circe.syntax._
import org.http4s._
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

class PostSensorDataIntegrationTest extends HomeMonitorIntegrationTest {

  describe("on valid POST sensor data request") {

    val request = postRequest(
      "/sensor",
      Map(
        "temperature" -> 0.1,
        "humidity" -> 0.1
      ).asJson
    )
    val response = executeRequest(request)

    it("should return valid response") { _ =>
      response.asserting(_.status shouldBe Status.Ok)
    }
  }
}
