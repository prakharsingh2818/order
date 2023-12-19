package com.nashtech.actors

import akka.actor.{Actor, ActorLogging, ActorSystem}
import play.api.i18n.Lang.logger

import javax.inject.Inject
import scala.concurrent.duration.DurationInt

class MyActor @Inject() (system: ActorSystem) extends Actor with ActorLogging {

  override def preStart(): Unit = {
    logger.info("[MyActor] Inside preStart")
    system.scheduler.scheduleWithFixedDelay(initialDelay = 5.seconds, delay = 10.seconds, message = "1", receiver = self)(system.dispatcher)
  }
  override def receive: Receive = {
    case "1" =>
      logger.info("[MyActor] Received Message")
  }
}