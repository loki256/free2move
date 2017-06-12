package loki256.github.com.http

import scala.concurrent.duration._
import akka.actor.ActorRef
import akka.http.scaladsl.server
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.ExceptionHandler
import akka.http.scaladsl.server.Directives._
import akka.util.Timeout
import loki256.github.com.actors.StorageEngine


class Router(dataFetcher: ActorRef) {

  implicit val timeout: Timeout = 10.seconds

  import akka.pattern.ask

  val exceptionHandler = ExceptionHandler {
    case ex: Throwable =>
      complete(
        HttpResponse(StatusCodes.BadRequest, List(), s"${ex.getMessage}")
      )
  }

  val routes: server.Route =
    handleExceptions(exceptionHandler) {
      path(IntNumber) { index =>
        get {
          val future = (dataFetcher ? StorageEngine.GetItemByIndex(index)).mapTo[StorageEngine.GetItemResponse]
          onSuccess(future) {
             case StorageEngine.GetItemResponse(Some(value), None) =>
               complete(value)
             case StorageEngine.GetItemResponse(_, Some(error)) =>
               complete(
                 HttpResponse(StatusCodes.BadRequest, List(), error)
               )
             case _ =>
               complete(HttpResponse(StatusCodes.BadRequest, List(), "Internal logic error"))
          }
        }
      } ~ {
        path("ping") {
          get {
            complete("pong")
          }
        }
      }
    }
}

