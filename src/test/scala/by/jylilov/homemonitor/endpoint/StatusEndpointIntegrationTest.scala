package by.jylilov.homemonitor.endpoint

import by.jylilov.homemonitor.HomeMonitorIntegrationTest
import org.http4s.Status

class StatusEndpointIntegrationTest extends HomeMonitorIntegrationTest {

  test("should return 200 Ok response on status request") { implicit ctx =>

    val request = getRequest("/status")
    val response = executeRequest(request)

    response.asserting(_.status shouldBe Status.Ok)
  }
}
