package com.nashtech.database

import com.nashtech.order.v1.models.Order

import scala.concurrent.Future

trait DAO {

  def createOrder(order: Order): Future[String]

  def getOrderById(id: String, merchantId: String): Either[String, Order]

  def getAllOrder: Either[Seq[String], Order]

  def updateById(merchantId: String, id: String): Either[String, Order]

  def deleteById(merchantId: String): Either[String, Order]

  def deleteAll(): Future[String]

}
