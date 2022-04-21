package com.github.kineticcookie.project.consumer.adapters

import cats.effect.IO
import com.github.kineticcookie.project.consumer.ports.HourlyStorage
import com.github.kineticcookie.project.domain.Wallet.Events
import doobie.implicits._
import doobie.postgres._
import doobie.postgres.implicits._
import doobie.util.transactor.Transactor

import scala.concurrent.ExecutionContext

class PostgresStorage(tx: Transactor[IO]) extends HourlyStorage {
  override def insert(ev: Events.SumAdded) = {
    sql"INSERT INTO hourly(time, amount) VALUES (${ev.datetime}, ${ev.amount})".update.run
      .transact(tx)
      .unsafeToFuture()
  }
}

object PostgresStorage {
  def make(
      url: String = "jdbc:postgresql://localhost:5432/user",
      user: String = "user",
      password: String = "user",
      ec: ExecutionContext
  ) = {
    implicit val cs = IO.contextShift(ec)
    val tx = Transactor.fromDriverManager[IO](
      "org.postgresql.Driver",
      url,
      user,
      password
    )
    new PostgresStorage(tx)
  }
}
