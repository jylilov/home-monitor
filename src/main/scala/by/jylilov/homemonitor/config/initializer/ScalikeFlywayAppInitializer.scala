package by.jylilov.homemonitor.config.initializer

import by.jylilov.homemonitor.config.AppConfig
import cats.effect._
import cats.implicits._
import org.flywaydb.core.Flyway
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import scalikejdbc.ConnectionPool

class ScalikeFlywayAppInitializer[F[_] : Sync] extends AppInitializer[F] {

  private implicit val logger: Logger[F] = Slf4jLogger.getLogger

  override def init(config: AppConfig): F[Unit] = {
    val dbConfig = config.db
    for {
      _ <- Logger[F].info("Apply Flyway migration")
      _ <- Sync[F].blocking {
        val dataSource = ConnectionPool.dataSource(dbConfig.name)
        val flyway = Flyway.configure().dataSource(dataSource).load()
        flyway.migrate()
      }
    } yield ()
  }
}

object ScalikeFlywayAppInitializer {
  def apply[F[_] : Sync]: AppInitializer[F] = new ScalikeFlywayAppInitializer()
}
