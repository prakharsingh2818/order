package com.nashtech.actors

import akka.actor.{ActorRef, ActorSystem}
import play.api.i18n.Lang.logger
import play.api.libs.concurrent.CustomExecutionContext

import javax.inject.{Inject, Named}
import scala.concurrent.duration.DurationInt


class TaskCustomExecutionContext @Inject() (actorSystem: ActorSystem) extends CustomExecutionContext(actorSystem, "io-dispatcher")
class ActorTask @Inject()(actorSystem: ActorSystem, executor: TaskCustomExecutionContext, @Named("order-journal-actor") orderJournalActor: ActorRef)
{
  logger.info("ActorTask")
  actorSystem.scheduler.scheduleWithFixedDelay(
    initialDelay = 1.seconds,
    delay = 10.seconds,
    receiver = orderJournalActor,
    message = "Poll"
  )(executor)
}