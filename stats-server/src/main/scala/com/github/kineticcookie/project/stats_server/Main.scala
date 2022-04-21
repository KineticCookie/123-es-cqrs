package com.github.kineticcookie.project.stats_server

import akka.actor.ActorSystem
import com.github.kineticcookie.project.stats_server.adapters.{
  HttpApi,
  PostgresWalletStorage
}
import com.github.kineticcookie.project.stats_server.services.WalletStatsServiceImpl
import pureconfig._
import pureconfig.generic.auto._

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration._

object Main extends App {
  implicit val actorSystem: ActorSystem = ActorSystem("stats-server")
  implicit val ec: ExecutionContext = ExecutionContext.global

  val config = ConfigSource.default.loadOrThrow[Configuration]
  println(config)

  val cassandraStorage = PostgresWalletStorage.make(
    config.dbUrl,
    config.dbUser,
    config.dbPassword,
    ec
  )
  val walletStatsService = new WalletStatsServiceImpl(cassandraStorage)
  val api = new HttpApi(walletStatsService)

  val serverBinding = api
    .startServer()
    .map(_.addToCoordinatedShutdown(10.seconds))

  Await.ready(serverBinding, Duration.Inf) //block the main thread
}
