package by.jylilov.homemonitor.repository

import by.jylilov.homemonitor.domain.SensorData
import cats.effect.{Async, ContextShift}
import cats.implicits._
import doobie.Transactor
import doobie.implicits._

class DbSensorDataRepository[F[_] : ContextShift : Async] extends SensorDataRepository[F] {

  private[this] val transactor = Transactor.fromDriverManager[F](
    "org.postgresql.Driver",
    "jdbc:postgresql://localhost:5432/home_monitor",
    "postgres",
    "password"
  )

  override def save(data: SensorData): F[SensorData] = {
    sql"""
      insert into sensor_data(time, temperature, humidity)
      values (to_timestamp(${data.ts} / 1000), ${data.temperature}, ${data.humidity})
    """.update.run.transact(transactor).map(_ => data)
  }
}
