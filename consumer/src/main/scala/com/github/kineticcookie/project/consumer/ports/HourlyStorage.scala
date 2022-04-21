package com.github.kineticcookie.project.consumer.ports

import com.github.kineticcookie.project.domain.Wallet

import scala.concurrent.Future

trait HourlyStorage {
  def insert(ev: Wallet.Events.SumAdded): Future[Int]
}
