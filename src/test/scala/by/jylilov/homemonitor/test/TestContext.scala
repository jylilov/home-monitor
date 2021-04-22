package by.jylilov.homemonitor.test

import cats.effect.ExitCode
import cats.effect.kernel.Fiber

case class TestContext[F[_]](
  serverFiber: Fiber[F, Throwable, ExitCode]
)
