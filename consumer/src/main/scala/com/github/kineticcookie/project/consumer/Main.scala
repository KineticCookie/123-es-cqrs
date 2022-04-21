package com.github.kineticcookie.project.consumer

import akka.actor.ActorSystem
import com.github.kineticcookie.project.consumer.adapters.PostgresStorage
import com.github.kineticcookie.project.consumer.services.StreamingConsumer
import pureconfig._
import pureconfig.generic.auto._

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration._

object Main extends App {
  implicit val as = ActorSystem("consumer-as")
  implicit val ec = ExecutionContext.global

  val config = ConfigSource.default.loadOrThrow[Configuration]
  println(config)

  val storage =
    PostgresStorage.make(config.dbUrl, config.dbUser, config.dbPassword, ec)

  val streaming = StreamingConsumer.make(storage, config, as)
  val program = streaming.start()
  Await.ready(program.streamCompletion, Duration.Inf)
  println("End")
}
