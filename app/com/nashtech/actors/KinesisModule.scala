package com.nashtech.actors

import com.google.inject.AbstractModule
import com.nashtech.services.KinesisConsumerService

class KinesisModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[KinesisConsumerService]).asEagerSingleton()
  }
}