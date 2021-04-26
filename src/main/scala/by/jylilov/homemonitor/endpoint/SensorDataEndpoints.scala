package by.jylilov.homemonitor.endpoint

import by.jylilov.homemonitor.domain.SensorDataPost
import by.jylilov.homemonitor.service.SensorService
import cats.effect.Async
import cats.implicits._
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.circe.jsonOf
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityDecoder, HttpRoutes}

class SensorDataEndpoints[F[_] : Async](
  sensorService: SensorService[F]
) extends Http4sDsl[F] {

  private implicit val decoder: EntityDecoder[F, SensorDataPost] =
    jsonOf[F, SensorDataPost]

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
