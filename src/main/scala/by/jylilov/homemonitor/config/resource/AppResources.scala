package by.jylilov.homemonitor.config.resource

import by.jylilov.homemonitor.config.AppConfig
import by.jylilov.homemonitor.domain.DbInfo
import by.jylilov.homemonitor.endpoint.{SensorDataEndpoints, StatusEndpoints}
import by.jylilov.homemonitor.repository.{DbSensorDataRepository, SensorDataRepository}
import by.jylilov.homemonitor.service.{DefaultSensorService, SensorService}
import cats.effect._
import org.http4s._
import org.http4s.implicits._
import org.http4s.server.Router

class AppResources[F[_] : Async](config: AppConfig) {

  val sensorDataDb: DbInfo = DbInfo(config.db.name)

  val sensorDataRepository: SensorDataRepository[F] = new DbSensorDataRepository[F](sensorDataDb)

  val sensorService: SensorService[F] = new DefaultSensorService[F](sensorDataRepository)

  val sensorEndpoints: HttpRoutes[F] = new SensorDataEndpoints[F](sensorService).endpoints()
  val statusEndpoints: HttpRoutes[F] = new StatusEndpoints[F].endpoints()

  val httpApp: HttpApp[F] = Router(
    "/sensor" -> sensorEndpoints,
    "/status" -> statusEndpoints
  ).orNotFound
}
