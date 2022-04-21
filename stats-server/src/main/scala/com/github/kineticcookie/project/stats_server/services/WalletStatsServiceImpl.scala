package com.github.kineticcookie.project.stats_server.services

import com.github.kineticcookie.project.domain.Wallet.{
  TransactionTime,
  WalletHourlyState
}
import com.github.kineticcookie.project.stats_server.ports.{
  WalletStatsService,
  WalletStorage
}
import com.github.kineticcookie.project.stats_server.services.WalletStatsServiceImpl.hourly

import java.time.Instant
import java.time.temporal.ChronoUnit
import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext

final class WalletStatsServiceImpl(storage: WalletStorage)(implicit
    ec: ExecutionContext
) extends WalletStatsService {
  override def getStats(begin: TransactionTime, end: TransactionTime) = {
    val trimmedBegin = begin.truncatedTo(ChronoUnit.HOURS)
    val trimmedEnd = end.truncatedTo(ChronoUnit.HOURS)
    val spans = hourly(trimmedBegin, trimmedEnd)
    storage.getSums(begin, end).map { sums =>
      val mapped = sums.map(x => x.time -> x.amount).toMap
      var acc = 0.0
      spans.map { inst =>
        acc = acc + mapped.getOrElse(inst, 0.0)
        WalletHourlyState(inst, acc)
      }
    }
  }
}

object WalletStatsServiceImpl {
  def hourly(begin: Instant, end: Instant) = {
    val buffer = ListBuffer.empty[Instant]
    var i = begin
    while (i.isBefore(end)) {
      buffer += i
      i = i.plus(1, ChronoUnit.HOURS)
    }
    buffer.toList
  }
}
