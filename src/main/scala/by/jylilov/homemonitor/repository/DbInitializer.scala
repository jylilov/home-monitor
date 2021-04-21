package by.jylilov.homemonitor.repository

import by.jylilov.homemonitor.config.DbConfig

trait DbInitializer[F[_]] {
  def init(dbConfig: DbConfig): F[Unit]
}
