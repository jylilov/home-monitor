package by.jylilov.homemonitor

import by.jylilov.homemonitor.test.TestContext
import cats.effect._
import cats.effect.testing.scalatest.{AsyncIOSpec, CatsResourceIO}
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import org.scalatest.funspec.FixtureAsyncFunSpec
import org.scalatest.matchers.must.Matchers

abstract class ServerIntegrationTest extends FixtureAsyncFunSpec with AsyncIOSpec with Matchers with CatsResourceIO[TestContext[IO]] {

  protected val server: IO[ExitCode]

  protected val httpClient: Resource[IO, Client[IO]] = BlazeClientBuilder[IO](ioRuntime.compute).resource
}
