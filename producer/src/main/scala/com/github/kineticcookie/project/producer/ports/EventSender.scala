package com.github.kineticcookie.project.producer.ports

import com.github.kineticcookie.project.domain.Wallet

import scala.concurrent.Future

trait EventSender {
  def send(ev: Wallet.Events.SumAdded): Future[Unit]
}
