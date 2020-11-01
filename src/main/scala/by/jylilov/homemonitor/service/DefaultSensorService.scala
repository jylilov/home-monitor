package by.jylilov.homemonitor.service

import java.util.concurrent.TimeUnit

import by.jylilov.homemonitor.domain.{SensorData, SensorDataPost}
import by.jylilov.homemonitor.repository.SensorDataRepository
import cats.Monad
import cats.effect.Clock
import cats.implicits._

class DefaultSensorService[F[_] : Monad : Clock](
  repository: SensorDataRepository[F]
) extends SensorService[F] {

  override def postSensorData(data: SensorDataPost): F[SensorData] = {

    val ts = data.ts match {
      case Some(ts) => ts.pure[F]
      case None => Clock[F].realTime(TimeUnit.MILLISECONDS)
    }

    ts.map(SensorData(_, data.temperature, data.humidity))
      .flatMap(repository.save)
  }
}
