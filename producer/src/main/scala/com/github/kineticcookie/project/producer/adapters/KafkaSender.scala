package com.github.kineticcookie.project.producer.adapters

import com.github.kineticcookie.project.domain.CirceSerde.CirceSerializer
import com.github.kineticcookie.project.domain.Wallet.Events
import com.github.kineticcookie.project.producer.ports.EventSender
import org.apache.kafka.clients.producer._
import org.apache.kafka.common.serialization.VoidSerializer

import java.util.Properties
import scala.concurrent._

final class KafkaSender(
    topic: String,
    producer: KafkaProducer[Void, Events.SumAdded]
) extends EventSender {
  override def send(ev: Events.SumAdded): Future[Unit] = {
    println(s"Got event: $ev")
    val record =
      new ProducerRecord[Void, Events.SumAdded](topic, null, ev)
    val promise = Promise[Unit]()
    producer.send(record, KafkaSender.PromiseCallback(promise))
    promise.future
  }

}

object KafkaSender {

  /** @param topic kafka topic to write to
    * @return
    */
  def make(
      topic: String = "wallet-add",
      bootstrapServer: String = "127.0.0.1:9093"
  ) = {
    val props = new Properties()
    props.put("bootstrap.servers", bootstrapServer)
    props.put("connections.max.idle.ms", 10000)
    props.put("request.timeout.ms", 5000)

    val producer = new KafkaProducer(
      props,
      new VoidSerializer,
      new CirceSerializer[Events.SumAdded]
    )
    new KafkaSender(topic, producer)
  }

  final case class PromiseCallback(p: Promise[Unit]) extends Callback {
    override def onCompletion(
        metadata: RecordMetadata,
        exception: Exception
    ): Unit = {
      Option(exception) match {
        case Some(value) => p.failure(value)
        case None        => p.success(())
      }
    }
  }
}
