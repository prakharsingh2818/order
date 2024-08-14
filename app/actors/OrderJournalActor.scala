package actors

import akka.actor.{ActorSystem, Cancellable}
import play.api.db.Database
import play.api.i18n.Lang.logger

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.{FiniteDuration, SECONDS}
import scala.util.Success

@Singleton
class OrderJournalActor @Inject()(system: ActorSystem, override val db: Database)
  extends DBPollActor(table = "orders") {

  override def preStart(): Unit = {
    logger.info("[OrderJournalActor] Inside preStart")
    startPolling()
  }

  def schedule(): Cancellable = {
    system.scheduler.scheduleWithFixedDelay(FiniteDuration(5, SECONDS), delay, self, "INSERT")(system.dispatcher)
  }

  override def process(record: ProcessQueueOrder): Unit = {
    record.operation match {
      case "INSERT" | "UPDATE" =>
//        throw new ArithmeticException("Exception Occur")
        logger.info("Inside OrderJournalActor before publishing")
        Publisher.publish(record.order)
        logger.info("Inside OrderJournalActor after publishing")
      case "DELETE" =>
        log.info("Inside DELETE operation")
        Success(())
    }
  }
}