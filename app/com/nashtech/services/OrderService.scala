package com.nashtech.services

import akka.actor.ActorRef
import com.amazonaws.services.sqs.AmazonSQSClient
import com.google.inject.ImplementedBy
import com.nashtech.order.v1.models.Order
import org.joda.time.DateTime
import play.api.{Configuration, db}

import javax.inject.{Inject, Named, Singleton}
import scala.util.{Failure, Success, Try}

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
        val sqs = new AmazonSQSClient()
        sqs.setEndpoint(config.get[String]("aws.sqs.local.endpoint"))
        sqs.sendMessage(sqs.getQueueUrl("example").getQueueUrl, "Hello World!")
        Right(order)
      case None =>
        val sqs = new AmazonSQSClient()
        sqs.setEndpoint(config.get[String]("aws.sqs.local.endpoint"))
        sqs.sendMessage(sqs.getQueueUrl("example").getQueueUrl, "s3://example-bucket/path/to/cat.jpg")
        Left(Seq("Order Not Found"))
    }
  }
}
