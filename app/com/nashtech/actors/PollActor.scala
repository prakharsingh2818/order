package com.nashtech.actors

import akka.actor.{Actor, ActorLogging, ActorSystem}
import akka.dispatch.MessageDispatcher

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.{FiniteDuration, SECONDS}
import scala.util.Try

sealed trait PollActorMessage
object PollActorMessage {
  final case object Poll extends PollActorMessage
}

@Singleton
trait PollActor extends Actor with ActorLogging {
  import PollActorMessage._

  val system: ActorSystem = context.system
  val pollContext: MessageDispatcher = system.dispatchers.lookup("poll-context")

  def initialDelay: FiniteDuration = FiniteDuration(10L, SECONDS)

  def delay: FiniteDuration = FiniteDuration(10, SECONDS)

  def processRecord(): Unit

  def startPolling() = {
    system.scheduler.scheduleWithFixedDelay(initialDelay, delay, self, Poll)(pollContext)
  }

  override def receive: Receive = {
    case Poll =>
      log.info("Inside receive method")
      println("Inside receive method")
      safeProcessMessage()
  }

  private def safeProcessMessage(): Unit = {
    Try {
      log.info("Inside safeProcessMessage method")
      println("Inside safeProcessMessage method")
      processRecord()
    }.recover {
      case ex =>
        log.info("Discontinuing with safeProcessMessage method")
        println("Discontinuing with safeProcessMessage method")
        log.error(cause = ex, message = "Error processing messages")
    }
  }
}
