package com.github.kineticcookie.project.stats_server.adapters

import cats.effect.IO
import com.github.kineticcookie.project.domain.Wallet.{
  TransactionTime,
  WalletHourlyState
}
import com.github.kineticcookie.project.stats_server.ports.WalletStorage
import doobie.util.transactor.Transactor
import doobie._
import doobie.implicits._
import doobie.postgres._
import doobie.postgres.implicits._

import scala.concurrent.{ExecutionContext, Future}

final class PostgresWalletStorage(
    tx: Transactor[IO]
) extends WalletStorage {

  override def getSums(
      start: TransactionTime,
      end: TransactionTime
  ): Future[Seq[WalletHourlyState]] = {
    sql"SELECT time, SUM(amount) FROM hourly WHERE time >= ${start} AND time <= ${end} GROUP BY time"
      .query[WalletHourlyState]
      .to[Seq]
      .transact(tx)
      .unsafeToFuture()
  }
}

object PostgresWalletStorage {
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
    new PostgresWalletStorage(tx)
  }
}
