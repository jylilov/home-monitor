package by.jylilov.homemonitor

import cats.effect._
import cats.effect.testing.scalatest.AsyncIOSpec
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import org.scalactic.source
import org.scalatest.Assertion
import org.scalatest.funspec.AsyncFunSpec
import org.scalatest.matchers.must.Matchers

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration

abstract class IntegrationSpec(
  specDescription: String = "Server"
) extends AsyncFunSpec with AsyncIOSpec with Matchers {

  protected val server: IO[ExitCode]

  protected val httpClient: Resource[IO, Client[IO]] = BlazeClientBuilder[IO](ioRuntime.compute).resource

  def describeIntegrationTest(description: String)(fun: => IO[Assertion])(implicit pos: source.Position): Unit =
    describe(specDescription) {
      val serverFiber = server.start
      it(description) {
        for {
          serverFiber <- serverFiber
          assertion <- fun
          _ <- serverFiber.cancel
        } yield assertion
      }
    }
}
