package com.nashtech.actors

import com.google.inject.AbstractModule
import play.api.inject
import play.api.inject.SimpleModule

class TasksModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[ActorTask]).asEagerSingleton()
  }
}
