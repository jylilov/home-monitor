package by.jylilov.homemonitor.test

import cats.effect.kernel.Fiber
import cats.effect.{ExitCode, IO, Resource}
import org.http4s.client.Client

case class TestContext[F[_]](
  serverFiber: Fiber[F, Throwable, ExitCode],
  testServerAddress: String,
  httpClient: Resource[IO, Client[IO]]
)
