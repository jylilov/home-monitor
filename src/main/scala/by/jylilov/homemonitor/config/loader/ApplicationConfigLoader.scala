package by.jylilov.homemonitor.config.loader

import by.jylilov.homemonitor.config.ApplicationConfig

trait ApplicationConfigLoader[F[_]] {
  def load(): F[ApplicationConfig]
}
