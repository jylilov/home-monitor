package by.jylilov.homemonitor.endpoint

import by.jylilov.homemonitor.HomeMonitorIntegrationTest
import org.http4s.Status
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

class StatusEndpointIntegrationTest extends HomeMonitorIntegrationTest {

  describe("on status request") {

    val request = getRequest("/status")
    val response = executeRequest(request)

    it("should return 200 Ok response") { _ =>
      response.asserting(_.status shouldBe Status.Ok)
    }
  }
}
