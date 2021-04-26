package by.jylilov.homemonitor

import by.jylilov.homemonitor.config.initializer._
import by.jylilov.homemonitor.config.loader.TypesafeAppConfigLoader
import cats.effect._

object HomeMonitorApplication extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = new HomeMonitorHttpServer(
    TypesafeAppConfigLoader[IO],
    CombinedAppInitializer(
      ScalikeConnectionPoolAppInitializer[IO],
      ScalikeFlywayAppInitializer[IO]
    ),
    runtime.compute
  ).serve
}
