package by.jylilov.homemonitor.endpoint

import by.jylilov.homemonitor.HomeMonitorIntegrationTest
import io.circe.syntax._
import org.http4s._

class PostSensorDataIntegrationTest extends HomeMonitorIntegrationTest {

  test("should save sensor data on valid POST sensor data request") { implicit ctx =>

    val request = postRequest(
      "/sensor",
      Map(
        "temperature" -> 0.1,
        "humidity" -> 0.1
      ).asJson
    )
    val response = executeRequest(request)

    response.asserting(_.status shouldBe Status.Ok)
  }
}
