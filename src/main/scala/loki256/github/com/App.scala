package loki256.github.com

import scala.concurrent.duration._
import akka.actor.{ActorSystem, Props}
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http
import com.typesafe.config.ConfigFactory
import loki256.github.com.actors.{DataFetcher, StorageEngine}
import loki256.github.com.core.state.{CompressedStateHolder, SimpleStateHolder}
import loki256.github.com.http.Router

import scala.concurrent.Await
import scala.util.Try


object App {

  private val config = ConfigFactory.load()

  private val httpPort = config.getInt("http.port")
  private val httpHost = config.getString("http.host")

  def main(args: Array[String]): Unit = {

    // init akka
    implicit val actorSystem = ActorSystem("sample-service")
    implicit val executor = actorSystem.dispatcher
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    // terminate akka properly
    scala.sys.addShutdownHook {
      System.out.println("Terminating...")
      actorSystem.terminate()
      Await.result(actorSystem.whenTerminated, 30.seconds)
      System.out.println("Terminated... Bye")
    }

    // enable compression if configured...
    val compressionEnabled = Try { config.getBoolean("app.compression") }.getOrElse(false)
    println(s"Compression enabled: $compressionEnabled")
    val stateHolder = if (compressionEnabled) CompressedStateHolder else SimpleStateHolder

    val storageEngine = actorSystem.actorOf(Props(classOf[StorageEngine], stateHolder), name="storageEngine")
    val _ = actorSystem.actorOf(Props(classOf[DataFetcher], config, storageEngine, materializer), name="dataFetcher")

    // we don't wait for fetcher update by design
    val router = new Router(storageEngine)

    Http().bindAndHandle(router.routes, httpHost, httpPort)
    println(s"server started at $httpPort:$httpHost")
  }
}
