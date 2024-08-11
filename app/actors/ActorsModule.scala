package com.nashtech.actors

import akka.actor.{ActorRef, ActorSystem, Props}
import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.duration.DurationInt

class ActorsModule extends AbstractModule with AkkaGuiceSupport {
  override def configure(): Unit = {
    bindActor[OrderJournalActor]("order-journal-actor")
    bindActor[MyActor]("my-actor")
    bind(classOf[NightlyEvalSchedulerStartup]).asEagerSingleton()
  }
}

object ActorsModule {
  def withIoDispatcher(props: Props): Props = props.withDispatcher("io-dispatcher")
}

@Singleton
class NightlyEvalSchedulerStartup @Inject()(system: ActorSystem, @Named("my-actor") myActor: ActorRef) {
  system.scheduler.scheduleWithFixedDelay(5.seconds, 10.seconds, myActor, "1")(system.dispatcher)
}