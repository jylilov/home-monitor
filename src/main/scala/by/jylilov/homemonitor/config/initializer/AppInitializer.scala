package by.jylilov.homemonitor.config.initializer

import by.jylilov.homemonitor.config.AppConfig

trait AppInitializer[F[_]] {
  def init(config: AppConfig): F[Unit]
}
