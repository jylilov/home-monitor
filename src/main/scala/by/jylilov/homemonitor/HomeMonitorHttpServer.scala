package by.jylilov.homemonitor

import by.jylilov.homemonitor.config.initializer.AppInitializer
import by.jylilov.homemonitor.config.loader.AppConfigLoader
import by.jylilov.homemonitor.config.resource.AppResources
import cats.effect._
import cats.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

import scala.concurrent.ExecutionContext

class HomeMonitorHttpServer[F[_] : Async](
  configLoader: AppConfigLoader[F],
  appInitializer: AppInitializer[F],
  computeExecutionContext: ExecutionContext
) {
  private implicit def logger: Logger[F] = Slf4jLogger.getLogger[F]

  def serve: F[ExitCode] = {
    for {
      config <- configLoader.load()
      appResources <- new AppResources(config).pure[F]
      _ <- appInitializer.init(config)
      _ <- BlazeServerBuilder[F](computeExecutionContext)
        .bindHttp(config.httpServer.port, config.httpServer.host)
        .withBanner(null)
        .withHttpApp(appResources.httpApp)
        .serve
        .compile
        .drain
    } yield ExitCode.Success
  }
}
