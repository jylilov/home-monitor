package by.jylilov.homemonitor

import cats.effect.testing.scalatest.AsyncIOSpec
import cats.effect.{ExitCode, IO}
import cats.implicits.catsSyntaxApplicativeId
import org.http4s.Status
import org.http4s.client.blaze.BlazeClientBuilder
import org.scalatest.funspec.AsyncFunSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

class HomeMonitorHttpServerIntegrationTest extends AsyncFunSpec with AsyncIOSpec with Matchers {

  private val httpClient = BlazeClientBuilder[IO](ioRuntime.compute).resource

  describe("App server") {

    val server = HomeMonitorHttpServer.stream[IO](ioRuntime).compile.drain.as(ExitCode.Success)

    describe("when running") {

      val ioServerFiber = server.start

      it("on status request") {

        httpClient.use { httpClient =>

          for {
            serverFiber <- ioServerFiber
            assertion <-
              httpClient
                .get("http://localhost:9000/status")(_.pure[IO])
                .asserting(_.status shouldBe Status.Ok)
            _ <- serverFiber.cancel
          } yield assertion
        }
      }
    }
  }
}
