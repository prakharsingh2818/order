package com.nashtech.actors

import akka.actor.Props
import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport

class ActorsModule extends AbstractModule with AkkaGuiceSupport {
  override def configure(): Unit = {
    bindActor[PollActor]("poll-actor", ActorsModule.withIoDispatcher)
    // bindActor[DBPollActor]("db-poll-actor", ActorsModule.withIoDispatcher)
    bindActor[OrderJournalActor]("order-journal-actor", ActorsModule.withIoDispatcher)
  }
}

object ActorsModule {
  def withIoDispatcher(props: Props): Props = props.withDispatcher("io-dispatcher")
}
