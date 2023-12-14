package com.nashtech.actors

import akka.actor.{ActorSystem, Cancellable}
import com.nashtech.Publisher
import com.nashtech.database.OrdersDao
import play.api.db.Database
import software.amazon.awssdk.auth.credentials.{AwsBasicCredentials, StaticCredentialsProvider}
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.{FiniteDuration, SECONDS}
import scala.util.{Failure, Success, Try}

@Singleton
class OrderJournalActor @Inject()(system: ActorSystem, override val db: Database, dao: OrdersDao)
  extends DBPollActor(table = "orders") {

  override def preStart(): Unit = {
    // super.preStart()
    println("[OrderJournalActor] Inside preStart")
//    log.info("[OrderJournalActor] Inside preStart")
    self ! "INSERT"
    startPolling()
  }

  def schedule(): Cancellable = {
    system.scheduler.scheduleWithFixedDelay(FiniteDuration(5, SECONDS), delay, self, "INSERT")(system.dispatcher)
  }

  override def process(record: ProcessQueueOrder): Unit = {
    record.operation match {
      case "INSERT" | "UPDATE" => // TODO: Publish using kinesis
        if (true) {
          val credentials = AwsBasicCredentials.create("test", "test")

          val credentialsProvider = StaticCredentialsProvider.create(credentials)

          val kinesisClient: KinesisAsyncClient = KinesisAsyncClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(credentialsProvider)
            .endpointOverride(new java.net.URI("http://localhost:4566"))
            .httpClient(NettyNioAsyncHttpClient.builder().build())
            .build()
          Try(dao.getByNumber(record.merchantId, record.number)) match {
            case Failure(exception) => println(s"error while processing the records ${exception.getMessage}")
            case Success(value) => Publisher.publish(kinesisClient, value)
          }
        }
       println("Inside OrderJournalActor")
      case "DELETE" =>
        log.info("Inside DELETE operation")
        Success(())
    }
  }
}