package by.jylilov.homemonitor.endpoint

import by.jylilov.homemonitor.HomeMonitorIntegrationTest
import org.http4s.Status

class StatusEndpointIntegrationTest extends HomeMonitorIntegrationTest {

  describe("on status request") {

    it("should return 200 Ok response") { implicit ctx =>

      val request = getRequest("/status")
      val response = executeRequest(request)

      response.asserting(_.status shouldBe Status.Ok)
    }
  }
}
