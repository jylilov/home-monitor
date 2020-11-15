name := "home-monitor"

version := "0.1"

scalaVersion := "2.13.3"

val Http4sVersion = "1.0.0-M4"
val CirceVersion = "0.12.3"
val Slf4jVersion = "1.7.30"
val DoobieVersion = "0.9.0"

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-blaze-server",
  "org.http4s" %% "http4s-blaze-client",
  "org.http4s" %% "http4s-circe",
  "org.http4s" %% "http4s-dsl"
).map(_ % Http4sVersion)

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % CirceVersion)

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api",
  "org.slf4j" % "slf4j-simple"
).map(_ % Slf4jVersion)

libraryDependencies ++= Seq(
  "org.tpolecat" %% "doobie-core",
  "org.tpolecat" %% "doobie-postgres",
).map(_ % DoobieVersion)


libraryDependencies += "com.typesafe" % "config" % "1.4.1"
