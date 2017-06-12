package loki256.github.com.actors

import akka.actor.{Actor, ActorLogging}
import loki256.github.com.core.state.StateHolder


class StorageEngine(stateHolder: StateHolder) extends Actor with ActorLogging {

  import StorageEngine._

  override def receive: Receive = {

    case GetItemByIndex(index: Int) =>
      if (stateHolder.isStateInitialized) {
        val result = stateHolder.getItemByIndex(index)
        result match {
          case Some(value) =>
            sender ! GetItemResponse(Some(value))
          case None =>
            sender ! GetItemResponse(None, Some("No such index"))
          }
      } else {
        sender ! GetItemResponse(None, Some("Not initialized"))
      }

    case Update(value: String) =>
      log.debug(s"Received ${value.length}")
      stateHolder.updateState(value.split("\n"))
  }
}


object StorageEngine {
  case class Update(value: String)
  case class GetItemByIndex(index: Int)
  case class GetItemResponse(result: Option[String], error: Option[String] = None)
}
