package by.jylilov.homemonitor

import by.jylilov.homemonitor.config.loader.TypesafeApplicationConfigLoader
import by.jylilov.homemonitor.repository.{CombinedDbInitializer, ScalikeConnectionPoolDbInitializer, ScalikeFlywayDbInitializer}
import cats.effect.{ExitCode, IO, IOApp}

object HomeMonitorApplication extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = new HomeMonitorHttpServer(
    TypesafeApplicationConfigLoader[IO],
    CombinedDbInitializer(
      ScalikeConnectionPoolDbInitializer[IO],
      ScalikeFlywayDbInitializer[IO]
    ),
    runtime.compute
  ).serve
}
