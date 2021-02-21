package by.jylilov.homemonitor

import cats.effect.{Blocker, ContextShift, ExitCode, IO, Timer}
import org.http4s.Status
import org.http4s.client.{Client, JavaNetClientBuilder}
import org.http4s.implicits.http4sLiteralsSyntax
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

import scala.concurrent.ExecutionContext

class HomeMonitorHttpServerIntegrationTest extends AnyFunSpec with Matchers {

  private val ec = ExecutionContext.global

  implicit val cs: ContextShift[IO] = IO.contextShift(ec)
  implicit val timer: Timer[IO] = IO.timer(ec)

  val httpClient: Client[IO] = JavaNetClientBuilder[IO](Blocker.liftExecutionContext(ec)).create

  describe("App server") {

    val server = HomeMonitorHttpServer.stream[IO].compile.drain.as(ExitCode.Success)

    describe("when running") {

      val startServer = server.runCancelable(_ => IO.unit)
      val cancelServer = startServer.unsafeRunSync()

      describe("on status request") {

        val response = httpClient.get(uri"http://localhost:9000/status") { response =>
          IO.pure(response)
        }.unsafeRunSync()

        it("should return 200 OK status") {
          response.status mustBe Status.Ok
        }
      }

      cancelServer.unsafeRunSync()
    }
  }
}
