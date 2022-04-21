package com.github.kineticcookie.project.stats_server.adapters

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import com.github.kineticcookie.project.domain.Wallet.{
  TransactionTime,
  WalletHourlyState
}
import com.github.kineticcookie.project.stats_server.ports.WalletStatsService
import io.circe.generic.JsonCodec
import io.circe.{Decoder, Encoder}
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter

import java.time.Instant
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

final class HttpApi(walletStats: WalletStatsService)(implicit
    as: ActorSystem,
    ec: ExecutionContext
) {
  val getEndpointImpl = HttpApi.getEndpoint.serverLogicSuccess[Future] { in =>
    walletStats.getStats(in.startDatetime, in.endDatetime)
  }

  def routes = AkkaHttpServerInterpreter().toRoute(getEndpointImpl)

  def startServer(interface: String = "0.0.0.0", port: Int = 8080) = {
    Http().newServerAt(interface, port).bind(routes)
  }
}

object HttpApi {
  @JsonCodec
  case class GetRequest(
      startDatetime: TransactionTime,
      endDatetime: TransactionTime
  )

  val getEndpoint = endpoint
    .in("get")
    .in(jsonBody[GetRequest])
    .out(jsonBody[Seq[WalletHourlyState]])
}
