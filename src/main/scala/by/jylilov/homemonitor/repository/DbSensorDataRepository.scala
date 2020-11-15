package by.jylilov.homemonitor.repository

import java.time.{Instant, LocalDateTime, ZoneId}

import by.jylilov.homemonitor.config.DbConfig
import by.jylilov.homemonitor.domain.SensorData
import cats.effect.{Async, ContextShift}
import cats.implicits._
import doobie.Transactor
import doobie.implicits._
import doobie.implicits.javatime._


class DbSensorDataRepository[F[_] : ContextShift : Async](dbConfig: DbConfig) extends SensorDataRepository[F] {

  private[this] val transactor = Transactor.fromDriverManager[F](
    dbConfig.driver,
    dbConfig.jdbcUrl,
    dbConfig.username,
    dbConfig.password
  )

  override def save(data: SensorData): F[SensorData] = {

    val ts = LocalDateTime.ofInstant(Instant.ofEpochMilli(data.ts), ZoneId.of("UTC"))

    val sql =
      sql"""
        insert into sensor_data(time, temperature, humidity)
        values ($ts, ${data.temperature}, ${data.humidity})
      """

    sql.update.run.transact(transactor).map(_ => data)
  }
}
