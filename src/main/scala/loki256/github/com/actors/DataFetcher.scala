package loki256.github.com.actors

import scala.concurrent.duration._
import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer
import com.typesafe.config.Config
import loki256.github.com.http.ChallengeCarJumpNetClient

import scala.util.Try


class DataFetcher(config: Config, storageEngine: ActorRef, implicit val materializer: Materializer) extends Actor with ActorLogging {
  import DataFetcher._

  // context for scheduler
  import context.dispatcher
  import akka.pattern.pipe


  private val updateInterval = Try(config.getInt("app.updateTime")).getOrElse(60)
  log.info(s"update time (seconds): $updateInterval")
  private val clientHelper = new ChallengeCarJumpNetClient(config)

  context.system.scheduler.schedule(
    0.milliseconds,
    updateInterval.seconds,
    self,
    Update
  )

  override def receive: Receive = {

    case Update => {
      log.info("Updating")
      val _  = Http(context.system).singleRequest(clientHelper.defaultHttpRequest).pipeTo(self)
    }

    case HttpResponse(StatusCodes.OK, _, entity, _) =>
      log.debug("receive http response 200")
      val _ = Unmarshal(entity).to[String].map(HttpString).pipeTo(self)

    case resp @ HttpResponse(code, _, _, _) =>
      log.error(s"Can't update state. Wrong code from service: $code")
      val _ = resp.discardEntityBytes()

    case HttpString(value: String) =>
      log.debug(s"Received ${value.length}")
      storageEngine ! StorageEngine.Update(value)
  }
}

object DataFetcher {
  case object Update
  case class HttpString(value: String)
}
