package by.jylilov.homemonitor.endpoint

import by.jylilov.homemonitor.codec.JsonEntityCodec._
import by.jylilov.homemonitor.domain.SensorDataPost
import by.jylilov.homemonitor.service.SensorService
import cats.effect.Async
import cats.implicits._
import io.circe.generic.auto._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

class SensorDataEndpoints[F[_] : Async](
  sensorService: SensorService[F]
) extends Http4sDsl[F] {

  def endpoints(): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case req@POST -> Root =>
        for {
          request <- req.as[SensorDataPost]
          result <- sensorService.postSensorData(request)
          response <- Ok(result)
        } yield response
    }
}
