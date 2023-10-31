package com.nashtech.services

import com.google.inject.ImplementedBy
import com.nashtech.database.OrdersDao
import com.nashtech.order.v1.models.{Order, OrderForm}
import org.joda.time.DateTime
import play.api.db.Database

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

@ImplementedBy(classOf[OrderServiceImpl])
trait OrderService {
  def getByNumber(merchantId: String, number: String): Either[Seq[String], Order]

  def createOrder(order: OrderForm): Either[String, OrderForm]

  def getAllOrder(merchantId: String): Either[Seq[String], Seq[Order]]

  def updateOrder(orderId: String, updatedOrder: Order): Future[Either[String, String]]

  def deleteById(merchantId: String): Either[String, Order]

  def deleteAll(): Future[String]
}

@Singleton
class OrderServiceImpl @Inject()(dbs: Database, dao: OrdersDao) extends OrderService {
  private val db: Map[String, Order] = Map(
    "1" -> Order(id = "1", number = "1", merchantId = "X", submittedAt = DateTime.now(), total = 302.5)
  )

  override def getByNumber(merchantId: String, number: String): Either[Seq[String], Order] = {
    db.get(number) match {
      case Some(order) => Right(order)
      case None => Left(Seq("Order Not Found"))
    }
  }

  override def getAllOrder(merchantId: String): Either[Seq[String], Seq[Order]] = {
    Try(dao.getAllOrder(merchantId)) match {
      case Failure(exception) => Left(Seq(exception.getMessage))
      case Success(value) => Right(value)
    }
  }

  override def createOrder(orderform: OrderForm): Either[String, OrderForm] = {
    dao.createOrder(orderform) match {
      case Left(exception) => Left(exception)
      case Right(value) => Right(value)
    }

  }

  override def updateOrder(orderId: String, updatedOrder: Order): Future[Either[String, String]] = {
    //    if (orderIsValid(updatedOrder)) {
    //      // Implement the logic to update the order in the database.
    //      // You may use Anorm or your preferred database access method.
    //      // Replace the following code with your actual update logic.
    //      dbs.withConnection { implicit conn =>
    //        val updateQuery = SQL(
    //          """
    //          UPDATE orders
    //          SET number = {number}, total = {total}
    //          WHERE id = {id}
    //        """).on(
    //          "id" -> orderId,
    //          "number" -> updatedOrder.number,
    //          "total" -> updatedOrder.total
    //        )
    //
    //        val updatedRows = updateQuery.executeUpdate()
    //
    //        if (updatedRows > 0) {
    //          Future.successful(Right("Order updated successfully"))
    //        } else {
    //          Future.successful(Left("Order not found"))
    //        }
    //      }
    //    } else {
    //      Future.successful(Left("Invalid order"))
    //    }
    ???
  }

  override def deleteById(merchantId: String): Either[String, Order] = {
    Try(dao.deleteById(merchantId)) match {
      case Failure(exception) => Left(exception.getMessage)
      case Success(value) => Right(value)
    }
  }

  override def deleteAll(): Future[String] = ???
}
