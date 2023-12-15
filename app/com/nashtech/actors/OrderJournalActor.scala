package com.nashtech.actors

import akka.actor.{ActorSystem, Cancellable}
import com.nashtech.Publisher
import play.api.db.Database

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.{FiniteDuration, SECONDS}
import scala.util.Success

@Singleton
class OrderJournalActor @Inject()(system: ActorSystem, override val db: Database)
  extends DBPollActor(table = "orders") {

  override def preStart(): Unit = {
    println("[OrderJournalActor] Inside preStart")
    startPolling()
  }

  def schedule(): Cancellable = {
    system.scheduler.scheduleWithFixedDelay(FiniteDuration(5, SECONDS), delay, self, "INSERT")(system.dispatcher)
  }

  override def process(record: ProcessQueueOrder): Unit = {
    record.operation match {
      case "INSERT" | "UPDATE" =>
        Publisher.publish(record.order)
        println("Inside OrderJournalActor")
      case "DELETE" =>
        log.info("Inside DELETE operation")
        Success(())
    }
  }
}