package actors

import com.google.inject.AbstractModule

class TasksModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[ActorTask]).asEagerSingleton()
  }
}
