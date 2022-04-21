package com.github.kineticcookie.project.producer.services

import com.github.kineticcookie.project.domain.Wallet
import com.github.kineticcookie.project.domain.Wallet.{BTCSum, TransactionTime}
import com.github.kineticcookie.project.producer.ports.{
  EventSender,
  WalletService
}

import scala.concurrent.Future

final class WalletServiceImpl(eventSender: EventSender) extends WalletService {
  def addToWallet(amount: BTCSum, datetime: TransactionTime): Future[Unit] =
    eventSender.send(Wallet.Events.SumAdded(datetime, amount))
}
