package by.jylilov.homemonitor.test

import cats.effect._
import cats.implicits._
import io.circe.Json
import org.http4s.circe._
import org.http4s.{Method, Request, Response, Uri}

trait HttpClientTestUtils {

  def getRequest(endpoint: String)(implicit testContext: TestContext[IO]): Request[IO] =
    request(Method.GET, endpoint, None)

  def postRequest(endpoint: String, body: Json)(implicit testContext: TestContext[IO]): Request[IO] =
    request(Method.POST, endpoint, Some(body))

  def request(
    method: Method,
    endpoint: String,
    body: Option[Json]
  )(
    implicit testContext: TestContext[IO]
  ): Request[IO] = {
    val request = Request[IO](
      method = method,
      uri = Uri.unsafeFromString(s"${testContext.testServerAddress}$endpoint"),
    )
    body match {
      case Some(body) => request.withEntity(body)
      case None => request
    }
  }

  def executeRequest(request: Request[IO])(implicit testContext: TestContext[IO]): IO[Response[IO]] = {
    testContext.httpClient.use { httpClient =>
      httpClient.run(request).use(_.pure[IO])
    }
  }
}
