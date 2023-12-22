package com.nashtech.services

import akka.actor.ActorRef
import com.google.inject.ImplementedBy
import com.nashtech.Publisher
import com.nashtech.database.OrdersDao
import com.nashtech.order.v1.models.{Order, OrderForm}
import play.api.Configuration
import software.amazon.awssdk.auth.credentials.{AwsBasicCredentials, StaticCredentialsProvider}
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

@ImplementedBy(classOf[OrderServiceImpl])
trait OrderService {
  def getByNumber(merchantId: String, number: String): Either[Seq[String], Order]

  def createOrder(order: OrderForm, merchantId: String): Either[String, Order]

  def updateOrderByNumber(merchantId: String, updatedOrder: OrderForm, number: String): Either[String, Order]

  def deleteAllByMerchantId(merchantId: String): Either[String, Seq[Order]]

}

@Singleton
class OrderServiceImpl @Inject()(dao: OrdersDao) extends OrderService {

  override def getByNumber(merchantId: String, number: String): Either[Seq[String], Order] = {
    Try(dao.getByNumber(merchantId, number)) match {
      case Failure(exception) => Left(Seq(exception.getMessage))
      case Success(order) => Right(order)
    }
  }

  override def createOrder(orderForm: OrderForm, merchantId: String): Either[String, Order] = {
    Try(dao.createOrder(orderForm, merchantId)) match {
      case Success(value) => Right(value)
      case Failure(exception) => Left(exception.getMessage)
    }

  }

  override def updateOrderByNumber(merchantId: String, updatedOrder: OrderForm, number: String): Either[String, Order] = {
    Try(dao.updateOrderByNumber(merchantId, updatedOrder, number)) match {
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

//  override def deleteAll(): Future[String] = ???
}
