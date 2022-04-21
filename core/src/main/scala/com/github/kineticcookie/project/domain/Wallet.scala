package com.github.kineticcookie.project.domain

import io.circe.generic.JsonCodec

import java.time.Instant

object Wallet {
  type BTCSum = Double
  type TransactionTime = Instant

  object Events {
    @JsonCodec
    case class SumAdded(datetime: TransactionTime, amount: BTCSum)
  }

  @JsonCodec
  case class WalletHourlyState(time: TransactionTime, amount: BTCSum)

  type WalletOverallState = Seq[WalletHourlyState]
}
