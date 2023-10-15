package com.nashtech.actors

import akka.actor.ActorSystem
import com.nashtech.actors.PollActorMessage.Poll
import play.api.libs.ws.WSClient
import play.libs.ws.WSClient

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.{FiniteDuration, SECONDS}
import scala.util.{Success, Try}

@Singleton
class OrderJournalActor @Inject()(system: ActorSystem)
extends DBPollActor(table = "orders") {

  override def preStart(): Unit = {
    println("[OrderJournalActor] Inside preStart")
    log.info("[OrderJournalActor] Inside preStart")
    self ! "Insert"
  }

  def schedule() = {
    system.scheduler.scheduleWithFixedDelay(FiniteDuration(5, SECONDS), delay, self, "Insert")(system.dispatcher)
  }
  override def process(record: ProcessQueueOrder): Try[Unit] = {
    record.operation match {
      case "Insert" | "Update" => // TODO: Publish using kinesis
      Try {
        log.info("Inside OrderJournalActor")
      }
      case "Delete" => Success(())
    }
  }
}
