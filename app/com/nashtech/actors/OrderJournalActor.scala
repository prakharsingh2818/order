package com.nashtech.actors

import akka.actor.{ActorSystem, Cancellable}
import play.api.db.Database

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.{FiniteDuration, SECONDS}
import scala.util.{Success, Try}

@Singleton
class OrderJournalActor @Inject()(system: ActorSystem, override val db: Database)
  extends DBPollActor(table = "orders") {

  override def preStart(): Unit = {
    // super.preStart()
    println("[OrderJournalActor] Inside preStart")
    log.info("[OrderJournalActor] Inside preStart")
    // self ! "Insert"
    startPolling()
  }

  def schedule(): Cancellable = {
    system.scheduler.scheduleWithFixedDelay(FiniteDuration(5, SECONDS), delay, self, "Inser")(system.dispatcher)
  }

  override def process(record: ProcessQueueOrder): Try[Unit] = {
    record.operation match {
      case "INSERT" | "UPDATE" => // TODO: Publish using kinesis
        log.info("Inside OrderJournalActor")
        throw new ArithmeticException("Exception Occur")
      case "DELETE" =>
        log.info("Inside DELETE operation")
        Success(())
    }
  }
}