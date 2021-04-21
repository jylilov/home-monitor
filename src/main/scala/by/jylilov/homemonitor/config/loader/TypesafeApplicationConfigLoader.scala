package by.jylilov.homemonitor.config.loader

import by.jylilov.homemonitor.config.{ApplicationConfig, DbConfig, HttpServerConfig}
import cats.effect._
import cats.implicits._
import com.typesafe.config.{Config, ConfigFactory}
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

class TypesafeApplicationConfigLoader[F[_] : Sync] extends ApplicationConfigLoader[F] {

  private implicit def logger: Logger[F] = Slf4jLogger.getLogger[F]

  def parseConfig(config: Config): F[ApplicationConfig] =
    Sync[F].delay {
      val db = DbConfig(
        config.getString("db.name"),
        config.getString("db.driver"),
        config.getString("db.jdbcUrl"),
        config.getString("db.username"),
        config.getString("db.password"),
      )
      val httpServer = HttpServerConfig(
        config.getString("http.server.host"),
        config.getInt("http.server.port")
      )
      ApplicationConfig(db, httpServer)
    }

  override def load(): F[ApplicationConfig] =
    for {
      _ <- Logger[F].info("Loading configuration")
      config <- Sync[F].blocking(ConfigFactory.load())
      parsedConfig <- parseConfig(config)
      _ <- Logger[F].debug(s"Loaded configuration: $parsedConfig")
    } yield parsedConfig
}

object TypesafeApplicationConfigLoader {
  def apply[F[_] : Sync]: TypesafeApplicationConfigLoader[F] = new TypesafeApplicationConfigLoader()
}
