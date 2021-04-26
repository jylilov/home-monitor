package by.jylilov.homemonitor.config.loader

import by.jylilov.homemonitor.config.AppConfig

trait AppConfigLoader[F[_]] {
  def load(): F[AppConfig]
}
