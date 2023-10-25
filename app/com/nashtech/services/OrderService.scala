package com.nashtech.services

import com.google.inject.ImplementedBy
import com.nashtech.order.v1.models.Order
import org.joda.time.DateTime

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@ImplementedBy(classOf[OrderServiceImpl])
trait OrderService {
  def getByNumber(merchantId: String, number: String): Either[Seq[String], Order]

  def createOrder(order: Order): Future[String]

  def getAllOrder: Seq[Order]

  def updateById(merchantId: String, id: String): Either[String, Order]

  def deleteById(merchantId: String): Either[String, Order]

  def deleteAll(): Future[String]
}

@Singleton
class OrderServiceImpl @Inject() extends OrderService {
  private val db: Map[String, Order] = Map(
    "1" -> Order(id = "1", number = "1", merchantId = "X", submittedAt = DateTime.now(), total = 302.5)
  )

  override def getByNumber(merchantId: String, number: String): Either[Seq[String], Order] = {
    db.get(number) match {
      case Some(order) => Right(order)
      case None => Left(Seq("Order Not Found"))
    }
  }

  override def createOrder(order: Order): Future[String] = ???

  override def getAllOrder: Seq[Order] = ???

  override def updateById(merchantId: String, id: String): Either[String, Order] = ???

  override def deleteById(merchantId: String): Either[String, Order] = ???

  override def deleteAll(): Future[String] = ???
}
