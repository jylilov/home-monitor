package by.jylilov.homemonitor

import by.jylilov.homemonitor.config.loader.TypesafeApplicationConfigLoader
import cats.effect.{ExitCode, IO, IOApp}

object HomeMonitorApplication extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = new HomeMonitorHttpServer(
    TypesafeApplicationConfigLoader[IO],
    runtime.compute
  ).serve
}
