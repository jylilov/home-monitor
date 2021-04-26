package by.jylilov.homemonitor.config.initializer

import by.jylilov.homemonitor.config.AppConfig
import cats.effect._
import cats.implicits._
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import scalikejdbc.{ConnectionPool, ConnectionPoolSettings}

class ScalikeConnectionPoolAppInitializer[F[_] : Sync] extends AppInitializer[F] {

  private implicit val logger: Logger[F] = Slf4jLogger.getLogger

  override def init(config: AppConfig): F[Unit] = {
    val dbConfig = config.db
    for {
      _ <- Logger[F].info("Initializing DB connection")
      _ <- Sync[F].blocking {
        ConnectionPool.add(
          name = dbConfig.name,
          url = dbConfig.jdbcUrl,
          user = dbConfig.username,
          password = dbConfig.password,
          settings = ConnectionPoolSettings(
            driverName = dbConfig.driver
          )
        )
      }
    } yield ()
  }
}

object ScalikeConnectionPoolAppInitializer {
  def apply[F[_] : Sync]: AppInitializer[F] = new ScalikeConnectionPoolAppInitializer()
}
