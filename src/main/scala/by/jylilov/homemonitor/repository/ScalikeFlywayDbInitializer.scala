package by.jylilov.homemonitor.repository

import by.jylilov.homemonitor.config.DbConfig
import cats.effect._
import cats.implicits._
import org.flywaydb.core.Flyway
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import scalikejdbc.ConnectionPool

class ScalikeFlywayDbInitializer[F[_] : Sync] extends DbInitializer[F] {

  private implicit val logger: Logger[F] = Slf4jLogger.getLogger

  override def init(dbConfig: DbConfig): F[Unit] =
    for {
      _ <- Logger[F].info("Apply Flyway migration")
      _ <- Sync[F].blocking {
        val dataSource = ConnectionPool.dataSource(dbConfig.name)
        val flyway = Flyway.configure().dataSource(dataSource).load()
        flyway.migrate()
      }
    } yield ()
}

object ScalikeFlywayDbInitializer {
  def apply[F[_] : Sync]: DbInitializer[F] = new ScalikeFlywayDbInitializer()
}
