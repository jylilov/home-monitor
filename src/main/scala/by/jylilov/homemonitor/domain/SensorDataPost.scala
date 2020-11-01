package by.jylilov.homemonitor.domain

case class SensorDataPost(
  ts: Option[Long],
  temperature: Double,
  humidity: Double
)
