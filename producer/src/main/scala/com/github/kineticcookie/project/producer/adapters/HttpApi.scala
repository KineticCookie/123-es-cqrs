package com.github.kineticcookie.project.producer.adapters

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import com.github.kineticcookie.project.domain.Wallet
import com.github.kineticcookie.project.producer.ports.WalletService
import io.circe.generic.JsonCodec
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter

import scala.concurrent.{ExecutionContext, Future}

final class HttpApi(val walletService: WalletService)(implicit
    as: ActorSystem,
    ec: ExecutionContext
) {
  val addEndpointImpl = HttpApi.addEndpoint.serverLogicSuccess[Future] { in =>
    walletService
      .addToWallet(in.amount, in.datetime)
      .map(_ => HttpApi.AddResponse())
  }

  def routes = AkkaHttpServerInterpreter().toRoute(addEndpointImpl)

  def startServer(interface: String = "0.0.0.0", port: Int = 8080) = {
    Http().newServerAt(interface, port).bind(routes)
  }
}

object HttpApi {
  @JsonCodec
  case class AddResponse(msg: String = "ok")

  val addEndpoint = endpoint
    .in("add")
    .in(jsonBody[Wallet.Events.SumAdded])
    .out(jsonBody[AddResponse])
}
