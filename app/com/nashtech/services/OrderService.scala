package com.nashtech.services

import akka.actor.ActorRef
import com.google.inject.ImplementedBy
import com.nashtech.database.OrdersDao
import com.nashtech.order.v1.models.{Order, OrderForm}
import org.joda.time.DateTime

import javax.inject.{Inject, Named, Singleton}
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
class OrderServiceImpl @Inject()(@Named("order-journal-actor") orderActor: ActorRef, dao: OrdersDao) extends OrderService {

  override def getByNumber(merchantId: String, number: String): Either[Seq[String], Order] = {

    Try(dao.getByNumber(merchantId, number)) match {
      case Failure(exception) => Left(Seq(exception.getMessage))
      case Success(value) => Right(value)
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
