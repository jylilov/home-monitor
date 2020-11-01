package by.jylilov.homemonitor.repository

import by.jylilov.homemonitor.domain.SensorData

trait SensorDataRepository[F[_]] {

  def save(data: SensorData): F[SensorData]
}
