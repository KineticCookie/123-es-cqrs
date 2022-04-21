package com.github.kineticcookie.project.stats_server.ports

import com.github.kineticcookie.project.domain.Wallet.{
  TransactionTime,
  WalletHourlyState
}

import scala.concurrent.Future

trait WalletStatsService {
  def getStats(
      begin: TransactionTime,
      end: TransactionTime
  ): Future[Seq[WalletHourlyState]]
}
