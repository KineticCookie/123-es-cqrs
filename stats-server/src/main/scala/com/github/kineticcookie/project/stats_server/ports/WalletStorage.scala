package com.github.kineticcookie.project.stats_server.ports

import com.github.kineticcookie.project.domain.Wallet.{
  TransactionTime,
  WalletHourlyState
}

import scala.concurrent.Future

trait WalletStorage {
  def getSums(
      start: TransactionTime,
      end: TransactionTime
  ): Future[Seq[WalletHourlyState]]
}
