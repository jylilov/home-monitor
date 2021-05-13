package by.jylilov.homemonitor.test

import cats.effect._
import cats.implicits._
import io.circe.Json
import org.http4s._
import org.http4s.circe._

trait HttpClientTestUtils {

  def getRequest(endpoint: String)(implicit testContext: TestContext[IO]): Request[IO] =
    request(Method.GET, endpoint)

  def postRequest(endpoint: String, body: String)(implicit testContext: TestContext[IO]): Request[IO] =
    request(Method.POST, endpoint, body)

  def postRequest(endpoint: String, body: Json)(implicit testContext: TestContext[IO]): Request[IO] =
    request(Method.POST, endpoint, body)

  def request[T](
    method: Method,
    endpoint: String,
    body: T
  )(implicit testContext: TestContext[IO], entityEncoder: EntityEncoder[IO, T]): Request[IO] =
    request(method, endpoint).withEntity(body)

  def request[T](
    method: Method,
    endpoint: String
  )(implicit testContext: TestContext[IO]): Request[IO] =
    Request[IO](
      method = method,
      uri = Uri.unsafeFromString(s"${testContext.testServerAddress}$endpoint"),
    )

  def executeRequest(request: Request[IO])(implicit testContext: TestContext[IO]): IO[Response[IO]] = {
    testContext.httpClient.use { httpClient =>
      httpClient.run(request).use(_.pure[IO])
    }
  }
}
