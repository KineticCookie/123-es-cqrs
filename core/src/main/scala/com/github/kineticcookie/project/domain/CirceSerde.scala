package com.github.kineticcookie.project.domain

import io.circe._
import io.circe.parser._
import io.circe.syntax._
import org.apache.kafka.common.serialization._

import java.util

object CirceSerde {

  class JsonSerializer extends Serializer[Json] {
    private val strSer = new StringSerializer

    override def configure(configs: util.Map[String, _], isKey: Boolean): Unit =
      strSer.configure(configs, isKey)

    override def serialize(topic: String, data: Json): Array[Byte] =
      strSer.serialize(topic, data.noSpaces)

    override def close(): Unit = strSer.close()
  }

  class JsonDeserializer extends Deserializer[Json] {
    private val strDe = new StringDeserializer()

    override def close(): Unit = strDe.close()

    override def configure(configs: util.Map[String, _], isKey: Boolean): Unit =
      strDe.configure(configs, isKey)

    override def deserialize(topic: String, data: Array[Byte]): Json = {
      val str = strDe.deserialize(topic, data)
      parse(str).toTry.get
    }
  }

  class CirceSerializer[T: Encoder] extends Serializer[T] {
    private val jsonSer = new JsonSerializer

    override def configure(configs: util.Map[String, _], isKey: Boolean): Unit =
      jsonSer.configure(configs, isKey)

    override def serialize(topic: String, data: T): Array[Byte] =
      jsonSer.serialize(topic, data.asJson)

    override def close(): Unit = jsonSer.close()
  }

  class CirceDeserializer[T: Decoder] extends Deserializer[T] {
    private val jsonDe = new JsonDeserializer()

    override def close(): Unit = jsonDe.close()

    override def configure(configs: util.Map[String, _], isKey: Boolean): Unit =
      jsonDe.configure(configs, isKey)

    override def deserialize(topic: String, data: Array[Byte]): T = {
      val json = jsonDe.deserialize(topic, data)
      json.as[T].toTry.get
    }
  }
}
