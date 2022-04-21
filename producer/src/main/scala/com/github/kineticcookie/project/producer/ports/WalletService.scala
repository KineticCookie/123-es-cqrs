package com.github.kineticcookie.project.producer.ports

import com.github.kineticcookie.project.domain.Wallet.{BTCSum, TransactionTime}

import scala.concurrent.Future

trait WalletService {
  def addToWallet(amount: BTCSum, datetime: TransactionTime): Future[Unit]
}
