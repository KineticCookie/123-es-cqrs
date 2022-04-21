import sbt._

object Dependencies {
  val circeVersion = "0.14.1"
  val tapirVersion = "1.0.0-M6"
  val kafkaClientVersion = "2.4.0-SNAPSHOT"
  val scalaTestVersion = "3.2.11"
  val doobieVersion = "0.13.4"

  val http = Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-akka-http-server" % tapirVersion, // downloads 10.2.9 akka http
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirVersion,
    "de.heikoseeberger" %% "akka-http-circe" % "1.39.2"
  )

  val json = Seq(
    "io.circe" %% "circe-core" % circeVersion,
    "io.circe" %% "circe-generic" % circeVersion,
    "io.circe" %% "circe-parser" % circeVersion
  )

  val kafka = Seq(
    "org.apache.kafka" % "kafka-clients" % "3.1.0",
    "com.typesafe.akka" %% "akka-stream-kafka" % "3.0.0"
  )

  val db = Seq(
    "org.tpolecat" %% "doobie-core" % doobieVersion,
    "org.tpolecat" %% "doobie-postgres" % doobieVersion
  )

  val utils = Seq(
    "ch.qos.logback" % "logback-classic" % "1.2.11",
    "com.github.pureconfig" %% "pureconfig" % "0.17.1"
  )

  val test = Seq(
    "org.scalactic" %% "scalactic" % scalaTestVersion,
    "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
  )

  val all = utils ++ http ++ json ++ kafka ++ db ++ test

  val resolvers = Seq(
    "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
  )
}
