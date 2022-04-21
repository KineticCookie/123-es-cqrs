package com.github.kineticcookie.project.consumer

import cats.effect.IO
import com.github.kineticcookie.project.domain.Wallet
import doobie.Transactor
import org.scalatest.funspec.AsyncFunSpec

import java.time.Instant
import scala.concurrent.ExecutionContext
import doobie._
import doobie.implicits._
import doobie.postgres._
import doobie.postgres.implicits._

class DBSpec extends AsyncFunSpec {
  describe("DB") {
    it("should insert to db") {
      implicit val ec = ExecutionContext.global
      implicit val cs = IO.contextShift(ec)

      val tx = Transactor.fromDriverManager[IO](
        "org.postgresql.Driver",
        "jdbc:postgresql://localhost:5432/user",
        "user",
        "user"
      )
      val ev = Wallet.Events.SumAdded(Instant.now(), 100)
      sql"INSERT INTO hourly(time, amount) VALUES (${ev.datetime}, ${ev.amount})".update.run
        .transact(tx)
        .unsafeToFuture()
        .map(x => assert(x == 1))
    }
  }
}
