package com.nashtech.database
import akka.http.scaladsl.model.headers.Connection
import com.nashtech.order.v1.models.Order
import com.nashtech.database
import scala.concurrent.Future

class DaoImpl extends DAO {

  val statement =

  override def createOrder(order: Order): Future[String] = ???

  override def getOrderById(id: String, merchantId: String): Either[String, Order] = ???

  override def getAllOrder: Either[Seq[String], Order] = ???

  override def updateById(merchantId: String, id: String): Either[String, Order] = ???

  override def deleteById(merchantId: String): Either[String, Order] = ???

  override def deleteAll(): Future[String] = ???
}
