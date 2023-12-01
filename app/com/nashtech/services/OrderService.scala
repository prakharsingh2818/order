package com.nashtech.services

import akka.actor.ActorRef
import com.google.inject.ImplementedBy
import com.nashtech.{OrderEventConsumer, Publisher}
import com.nashtech.database.OrdersDao
import com.nashtech.order.v1.models.{Order, OrderForm}
import org.joda.time.DateTime
import play.api.Configuration
import software.amazon.awssdk.auth.credentials.{AwsBasicCredentials, StaticCredentialsProvider}
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

@ImplementedBy(classOf[OrderServiceImpl])
trait OrderService {
  def getByNumber(merchantId: String, number: String): Either[Seq[String], Order]

  def createOrder(order: OrderForm): Either[String, Order]

  def getAllOrder(merchantId: String): Either[Seq[String], Seq[Order]]

  def updateOrder(merchantId: String, updatedOrder: OrderForm): Either[String, Order]

  def deleteAllByMerchantId(merchantId: String): Either[String, Seq[Order]]

  def deleteAll(): Future[String]
}

@Singleton
class OrderServiceImpl @Inject()(@Named("order-journal-actor") orderActor: ActorRef,  dao: OrdersDao, config: Configuration, consumer: OrderEventConsumer) extends OrderService {
  private val db: Map[String, Order] = Map(
    "1" -> Order(id = "1", number = "1", merchantId = "X", submittedAt = DateTime.now(), total = 302.5)
  )

  override def getByNumber(merchantId: String, number: String): Either[Seq[String], Order] = {
    Try(dao.getByNumber(merchantId, number)) match {
      case Failure(exception) => Left(Seq(exception.getMessage))
      case Success(order) => // orderActor ! "Insert"
        if (true) {
          val credentials = AwsBasicCredentials.create("test", "test")

          val credentialsProvider = StaticCredentialsProvider.create(credentials)
          //  val asyncHttpClient: SdkAsyncHttpClient = NettyNioAsyncHttpClient.builder().build()

          val kinesisClient: KinesisAsyncClient = KinesisAsyncClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(credentialsProvider)
            .endpointOverride(new java.net.URI("http://localhost:4566"))
            .httpClient(NettyNioAsyncHttpClient.builder().build())
            .build()


          Publisher.publishV2(kinesisClient, order)
          Future(consumer.run(kinesisClient))
        }
        Right(order)
    }
  }

  override def getAllOrder(merchantId: String): Either[Seq[String], Seq[Order]] = {
    Try(dao.getAllOrder(merchantId)) match {
      case Failure(exception) => Left(Seq(exception.getMessage))
      case Success(value) => Right(value)
    }
  }

  override def createOrder(orderform: OrderForm): Either[String, Order] = {
    Try(dao.createOrder(orderform)) match {
      case Success(value) => Right(value)
      case Failure(exception) => Left(exception.getMessage)
    }

  }

  override def updateOrder(merchantId: String, updatedOrder: OrderForm): Either[String, Order] = {
    Try(dao.updateOrderById(merchantId, updatedOrder)) match {
      case Success(value) => Right(value)
      case Failure(exception) => Left(exception.getMessage)
    }
  }

  override def deleteAllByMerchantId(merchantId: String): Either[String, Seq[Order]] = {
    println(dao.deleteAllByMerchantId(merchantId))
    Try(dao.deleteAllByMerchantId(merchantId)) match {
      case Failure(exception) => Left(exception.getMessage)
      case Success(value) => Right(value)
    }
  }

  override def deleteAll(): Future[String] = ???
}
