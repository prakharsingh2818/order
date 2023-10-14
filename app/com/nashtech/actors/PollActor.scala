package com.nashtech.actors

import akka.actor.{Actor, ActorLogging, ActorSystem}
import akka.dispatch.MessageDispatcher

import scala.concurrent.duration.{FiniteDuration, SECONDS}
import scala.util.Try

sealed trait PollActorMessage
object PollActorMessage {
  final case object Poll extends PollActorMessage
}

trait PollActor extends Actor with ActorLogging {
  import PollActorMessage._

  val system: ActorSystem = context.system
  val pollContext: MessageDispatcher = system.dispatchers.lookup("poll-context")

  def initialDelay: FiniteDuration = FiniteDuration(60L, SECONDS)
  def delay: FiniteDuration = FiniteDuration(60, SECONDS)

  def processRecord(): Unit
  def startPolling() = {
    system.scheduler.scheduleWithFixedDelay(initialDelay, delay, self, Poll)(pollContext)
  }
  override def receive: Receive = {
    case Poll => safeProcessMessage()
  }

  private def safeProcessMessage(): Unit = {
    Try {
      processMessage()
    }.recover {
      case ex => log.error(cause = ex, message = "Error processing messages")
    }
  }
}
