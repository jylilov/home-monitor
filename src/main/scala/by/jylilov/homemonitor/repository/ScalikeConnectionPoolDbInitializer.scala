package by.jylilov.homemonitor.repository

import by.jylilov.homemonitor.config.DbConfig
import cats.effect._
import cats.implicits._
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import scalikejdbc.{ConnectionPool, ConnectionPoolSettings}

class ScalikeConnectionPoolDbInitializer[F[_] : Sync] extends DbInitializer[F] {

  private implicit val logger: Logger[F] = Slf4jLogger.getLogger

  override def init(dbConfig: DbConfig): F[Unit] =
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

object ScalikeConnectionPoolDbInitializer {
  def apply[F[_] : Sync]: DbInitializer[F] = new ScalikeConnectionPoolDbInitializer()
}
