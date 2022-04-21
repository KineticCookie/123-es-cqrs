package com.github.kineticcookie.project.producer

import akka.actor.ActorSystem
import com.github.kineticcookie.project.producer.adapters.{HttpApi, KafkaSender}
import com.github.kineticcookie.project.producer.services.WalletServiceImpl

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration._
import pureconfig._
import pureconfig.generic.auto._

import java.time.format.DateTimeFormatter

object Main extends App {
  implicit val actorSystem: ActorSystem = ActorSystem("producer-as")
  implicit val ec: ExecutionContext = ExecutionContext.global

  val config = ConfigSource.default.loadOrThrow[Configuration]
  println(config)
  val kafka = KafkaSender.make(config.topic, config.kafkaServer)
  val walletService = new WalletServiceImpl(kafka)
  val api = new HttpApi(walletService)

  val serverBinding = api
    .startServer()
    .map(_.addToCoordinatedShutdown(10.seconds))
  DateTimeFormatter.ISO_OFFSET_DATE_TIME
  Await.ready(serverBinding, Duration.Inf) //block the main thread
}
