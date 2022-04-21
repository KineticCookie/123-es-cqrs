ThisBuild / scalaVersion := "2.13.8"
ThisBuild / scalacOptions ++= Seq("-Ymacro-annotations")
ThisBuild / libraryDependencies ++= Dependencies.all
ThisBuild / resolvers ++= Dependencies.resolvers
ThisBuild / Test / logBuffered := false
ThisBuild / dockerBaseImage := "openjdk:18-slim"

val core = project
  .in(file("core"))

val producer = project
  .in(file("producer"))
  .dependsOn(core)
  .enablePlugins(JavaAppPackaging, DockerPlugin)
  .settings(
    name := "producer"
  )

val consumer = project
  .in(file("consumer"))
  .dependsOn(core)
  .enablePlugins(JavaAppPackaging, DockerPlugin)
  .settings(
    name := "consumer"
  )

val statsServer = project
  .in(file("stats-server"))
  .dependsOn(core)
  .enablePlugins(JavaAppPackaging, DockerPlugin)
  .settings(
    name := "stats-server"
  )

val root =
  project.in(file(".")).aggregate(statsServer, consumer, producer, core)
