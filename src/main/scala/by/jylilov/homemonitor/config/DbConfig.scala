package by.jylilov.homemonitor.config

case class DbConfig(
  name: String,
  driver: String,
  jdbcUrl: String,
  username: String,
  password: String
)
