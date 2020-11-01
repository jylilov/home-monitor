package by.jylilov.homemonitor.service

import by.jylilov.homemonitor.domain.{SensorData, SensorDataPost}

trait SensorService[F[_]] {
  def postSensorData(data: SensorDataPost): F[SensorData]
}
