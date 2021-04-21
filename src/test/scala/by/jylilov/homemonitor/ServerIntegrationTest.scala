package by.jylilov.homemonitor

import cats.effect._
import cats.effect.testing.scalatest.AsyncIOSpec
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import org.scalactic.source
import org.scalatest.Assertion
import org.scalatest.funspec.AsyncFunSpec
import org.scalatest.matchers.must.Matchers
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

abstract class ServerIntegrationTest(
  specDescription: String = "Server"
) extends AsyncFunSpec with AsyncIOSpec with Matchers {

  private implicit val logger: Logger[IO] = Slf4jLogger.getLogger

  protected val server: IO[ExitCode]

  protected val httpClient: Resource[IO, Client[IO]] = BlazeClientBuilder[IO](ioRuntime.compute).resource

  def describeIntegrationTest(description: String)(testFun: => IO[Assertion])(implicit pos: source.Position): Unit =
    describe(specDescription) {
      it(description) {
        for {
          serverFiber <- server.start
          testFiber <- testFun.start
          testResult <- testFiber.join
          _ <- serverFiber.cancel
          serverResult <- serverFiber.join
          _ <- serverResult.fold(IO.unit, e => Logger[IO].error(e)("Server was failed"), _ => IO.unit)
          result <- testResult.embedNever
        } yield result
      }
    }
}
