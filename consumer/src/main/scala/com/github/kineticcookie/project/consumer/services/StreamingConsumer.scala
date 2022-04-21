package com.github.kineticcookie.project.consumer.services

import akka.actor.ActorSystem
import akka.kafka.scaladsl.{Committer, Consumer}
import akka.kafka.{
  CommitterSettings,
  ConnectionCheckerSettings,
  ConsumerSettings,
  Subscriptions
}
import akka.stream.scaladsl.Sink
import akka.stream.Materializer
import com.github.kineticcookie.project.consumer.ports.HourlyStorage
import com.github.kineticcookie.project.consumer.Configuration
import com.github.kineticcookie.project.consumer.Main.{as, config}
import com.github.kineticcookie.project.domain.CirceSerde.CirceDeserializer
import com.github.kineticcookie.project.domain.Wallet
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.VoidDeserializer

import scala.concurrent.duration._
import java.time.temporal.ChronoUnit

final class StreamingConsumer(
    storage: HourlyStorage,
    kafkaConsumerSettings: ConsumerSettings[Void, Wallet.Events.SumAdded],
    topic: String,
    as: ActorSystem
) {
  private implicit val mat = Materializer.createMaterializer(as)
  def start() = {
    println("starting kafka -> storage stream")
    Consumer
      .sourceWithOffsetContext(
        kafkaConsumerSettings,
        Subscriptions.topics(topic)
      )
      .map(_.value())
      .map { ev =>
        println(s"Got event from kafka $ev")
        ev
      }
      .map(x => x.copy(datetime = x.datetime.truncatedTo(ChronoUnit.HOURS)))
      .mapAsync(4)(storage.insert)
      .map { x =>
        println(s"Wrote ${x} events to db")
      }
      .via(Committer.flowWithOffsetContext(CommitterSettings(as)))
      .map { _ =>
        println("Commited event")
      }
      .toMat(Sink.ignore)(Consumer.DrainingControl.apply)
      .run()
  }
}

object StreamingConsumer {
  def make(
      storage: HourlyStorage,
      config: Configuration,
      as: ActorSystem
  ) = {
    val kafkaConsumerSettings = ConsumerSettings(
      as,
      new VoidDeserializer,
      new CirceDeserializer[Wallet.Events.SumAdded]
    )
      .withBootstrapServers(config.kafkaServer)
      .withGroupId(config.consumerGroup)
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
      .withStopTimeout(0.seconds)
      .withConnectionChecker(ConnectionCheckerSettings(3, 10.seconds, 2))
    new StreamingConsumer(storage, kafkaConsumerSettings, config.topic, as)
  }
}
