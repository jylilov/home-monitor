package by.jylilov.homemonitor.service

import by.jylilov.homemonitor.domain.{SensorData, SensorDataPost}
import by.jylilov.homemonitor.repository.SensorDataRepository
import cats.effect._
import cats.implicits._

import scala.concurrent.duration._

class DefaultSensorService[F[_] : Async](
  repository: SensorDataRepository[F]
) extends SensorService[F] {

  override def postSensorData(data: SensorDataPost): F[SensorData] =
    for {
      ts <- data.ts match {
        case Some(ts) => ts.millis.pure[F]
        case None => Clock[F].realTime
      }
      savedData <- repository.save(
        SensorData(ts.toMillis, data.temperature, data.humidity)
      )
    } yield savedData
}
