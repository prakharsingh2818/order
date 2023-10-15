package com.nashtech.actors

import akka.actor.Props
import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport

class ActorModule extends AbstractModule with AkkaGuiceSupport {
  override def configure(): Unit = {
    bindActor[PollActor]("poll-actor", ActorModule.withIoDispatcher)
    bindActor[DBPollActor]("db-poll-actor", ActorModule.withIoDispatcher)
    bindActor[OrderJournalActor]("order-journal-actor", ActorModule.withIoDispatcher)
  }
}

object ActorModule {
  def withIoDispatcher(props: Props): Props = props.withDispatcher("io-dispatcher")
}
