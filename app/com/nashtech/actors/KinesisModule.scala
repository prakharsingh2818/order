package com.nashtech.actors

import com.google.inject.AbstractModule
import com.nashtech.{OrderEventConsumer, OrderEventProcessor, OrderEventProcessorFactory}

class KinesisModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[OrderEventProcessorFactory]).asEagerSingleton()
    bind(classOf[OrderEventProcessor]).asEagerSingleton()
    bind(classOf[OrderEventConsumer]).asEagerSingleton()
  }
}