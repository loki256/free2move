package loki256.github.com.http

import akka.http.scaladsl.model.HttpRequest
import com.typesafe.config.Config

import scala.util.Try


class ChallengeCarJumpNetClient(config: Config) {

    private val serviceUri = Try(config.getString("services.challengeCarJumpNet")).getOrElse("http://challenge.carjump.net/A")

    val defaultHttpRequest: HttpRequest = {
      HttpRequest(uri = s"$serviceUri")
    }
}
