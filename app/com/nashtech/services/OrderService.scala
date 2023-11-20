package com.nashtech.services

import akka.actor.ActorRef
import com.google.inject.ImplementedBy
import com.nashtech.Publisher
import com.nashtech.order.v1.models.Order
import org.joda.time.DateTime
import play.api.Configuration
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.kinesis.KinesisClient

import java.net.URI
import javax.inject.{Inject, Named, Singleton}

@ImplementedBy(classOf[OrderServiceImpl])
trait OrderService {
  def getByNumber(merchantId: String, number: String): Either[Seq[String], Order]
}

@Singleton
class OrderServiceImpl @Inject()(@Named("order-journal-actor") orderActor: ActorRef, config: Configuration) extends OrderService {
  private val db: Map[String, Order] = Map(
    "1" -> Order(id = "1", number = "1", merchantId = "X", submittedAt = DateTime.now(), total = 302.5)
  )

  override def getByNumber(merchantId: String, number: String): Either[Seq[String], Order] = {
    db.get(number) match {
      case Some(order) =>
        orderActor ! "Insert"
        /*val kinesisClient: AmazonKinesis = AmazonKinesisClient.builder().build()
        Publisher.publish(kinesisClient)*/
        /*val res1 = Await.result(wsClient.url("http://localhost:4566/health").get(), 10.second) match {
          case AhcWSResponse(underlying) => underlying.status == 200
          case _ => false
        }
        val res2 = Await.result(wsClient.url("http://localhost:4566/health").get(), 10.second) match {
          case AhcWSResponse(underlying) => underlying.status == 200
          case _ => false
        }*/
        if (true) {
          val kinesisClient = KinesisClient.builder()
            .region(Region.US_EAST_1)
            .endpointOverride(new URI("http://localhost:4566"))
            .httpClient(UrlConnectionHttpClient.builder().build())
            .build()
          Publisher.publishV2(kinesisClient, order)
        }
        else {
          println("\n\nCould Not connect to localhost!!!\n\n")
        }
        // kinesisClient.setEndpoint(config.get[String]("aws.sqs.local.endpoint"))
        // sqs.getRecords()
        Right(order)
      case None =>
        /*val sqs = new AmazonSQSClient()
        sqs.setEndpoint(config.get[String]("aws.sqs.local.endpoint"))
        sqs.sendMessage(sqs.getQueueUrl("example").getQueueUrl, "s3://example-bucket/path/to/cat.jpg")*/
        Left(Seq("Order Not Found"))
    }
  }
}
