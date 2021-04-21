package by.jylilov.homemonitor.repository

import by.jylilov.homemonitor.domain.{DbInfo, SensorData}
import cats.effect.Async
import scalikejdbc._

import java.time.{Instant, LocalDateTime, ZoneId}


class DbSensorDataRepository[F[_] : Async](db: DbInfo) extends SensorDataRepository[F] {

  override def save(data: SensorData): F[SensorData] = {

    Async[F].blocking {

      val ts = LocalDateTime.ofInstant(Instant.ofEpochMilli(data.ts), ZoneId.of("UTC"))

      using(NamedDB(db.name)) { db =>
        db autoCommit { implicit session =>
          sql"""
            insert into sensor_data(time, temperature, humidity)
            values ($ts, ${data.temperature}, ${data.humidity})
          """.update().apply()
        }
      }

      data
    }
  }
}
