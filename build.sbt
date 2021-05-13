name := "home-monitor"

version := "0.1"

scalaVersion := "2.13.5"

// Cats
libraryDependencies += "org.typelevel" %% "cats-core" % "2.5.0"
libraryDependencies += "org.typelevel" %% "cats-effect" % "3.0.1"

// HTTP
libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-blaze-server",
  "org.http4s" %% "http4s-blaze-client",
  "org.http4s" %% "http4s-circe",
  "org.http4s" %% "http4s-dsl"
).map(_ % "1.0.0-M20")

// JSON
libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-optics",
  "io.circe" %% "circe-parser"
).map(_ % "0.13.0")

// JDBC connection
libraryDependencies += "org.scalikejdbc" %% "scalikejdbc" % "3.5.0"
libraryDependencies += "org.postgresql" % "postgresql" % "42.2.19"
libraryDependencies += "org.flywaydb" % "flyway-core" % "7.8.1"

// Logging
libraryDependencies ++= Seq(
  "org.typelevel" %% "log4cats-core",
  "org.typelevel" %% "log4cats-slf4j",
).map(_ % "2.0.1")
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"

// Configuration
libraryDependencies += "com.typesafe" % "config" % "1.4.1"

// Test
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.5" % Test
libraryDependencies += "org.typelevel" %% "cats-effect-testing-scalatest" % "1.0.0" % Test
libraryDependencies += "org.testcontainers" % "testcontainers" % "1.15.2" % Test
