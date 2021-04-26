package by.jylilov.homemonitor.testcontainers

import cats.effect.{Resource, Sync}
import org.testcontainers.containers.GenericContainer

class TimescaleTestContainer(
  val username: String,
  val password: String
) extends GenericContainer[TimescaleTestContainer]("timescale/timescaledb:2.1.1-pg13") {
  addExposedPort(5432)
  addEnv("POSTGRES_DB", "home_monitor")
  addEnv("POSTGRES_USER", username)
  addEnv("POSTGRES_PASSWORD", password)
}

object TimescaleTestContainer {

  def apply(username: String, password: String): TimescaleTestContainer = new TimescaleTestContainer(username, password)

  def resource[F[_] : Sync](username: String, password: String): Resource[F, TimescaleTestContainer] = {
    TestContainersUtils.testContainerResource[F, TimescaleTestContainer](() => apply(username, password))
  }
}
