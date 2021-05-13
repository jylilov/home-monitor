package by.jylilov.homemonitor.endpoint

import by.jylilov.homemonitor.HomeMonitorIntegrationTest
import by.jylilov.homemonitor.error.ErrorType
import io.circe.optics.JsonPath._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._

class PostSensorDataIntegrationTest extends HomeMonitorIntegrationTest {

  test("should return 400 Bad request on invalid json request body") { implicit ctx =>

    val request = postRequest("/sensor", """{"temperature":0.0,"humiity":0.0}""")

    for {
      response <- executeRequest(request)
      responseBody <- response.asJson
    } yield {
      root.error.string.getOption(responseBody) shouldBe Some(ErrorType.InvalidRequestBody.value)
      response.status shouldBe Status.BadRequest
    }
  }

  test("should save sensor data on valid POST sensor data request") { implicit ctx =>

    val request = postRequest(
      "/sensor",
      Map(
        "temperature" -> 0.1,
        "humidity" -> 0.1
      ).asJson
    )

    for {
      response <- executeRequest(request)
      responseBody <- response.asJson
    } yield {
      response.status shouldBe Status.Ok
      root.ts.long.getOption(responseBody) should not be None
      root.temperature.double.getOption(responseBody) shouldBe Some(0.1)
      root.humidity.double.getOption(responseBody) shouldBe Some(0.1)
    }
  }
}
