package com.nashtech.services

import com.google.inject.ImplementedBy
import com.nashtech.database.OrdersDao
import com.nashtech.order.v1.models.{Order, OrderForm}

import javax.inject.{Inject, Singleton}
import scala.util.{Failure, Success, Try}

@ImplementedBy(classOf[OrderServiceImpl])
trait OrderService {
  def getByNumber(merchantId: String, number: String): Either[Seq[String], Order]
  def getTest: Either[Seq[String], String]
  def createOrder(order: OrderForm, merchantId: String): Either[String, Order]

//  def getAllOrder(merchantId: String): Either[Seq[String], Seq[Order]]

  def updateOrder(merchantId: String, updatedOrder: OrderForm, number: String): Either[String, Order]

  def deleteAllByMerchantId(merchantId: String): Either[String, Seq[Order]]

//  def deleteAll(): Future[String]
}

@Singleton
class OrderServiceImpl @Inject()(dao: OrdersDao) extends OrderService {

  override def getByNumber(merchantId: String, number: String): Either[Seq[String], Order] = {
    Try(dao.getByNumber(merchantId, number)) match {
      case Failure(exception) => Left(Seq(exception.getMessage))
      case Success(order) => Right(order)
    }
  }

  override def getTest: Either[Seq[String], String] = {
    println("INSIDE SERVICE")
    Try(dao.getTest()) match {
      case Failure(exception) =>
        println(s"FAILED IN SERVICE:\n${exception}\n")
        Left(Seq(exception.getMessage))
      case Success(order) =>
        println(s"SUCCESS IN SERVICE $order")
        Right(order)
    }
  }

//  override def getAllOrder(merchantId: String): Either[Seq[String], Seq[Order]] = {
//    Try(dao.getAllOrder(merchantId)) match {
//      case Failure(exception) => Left(Seq(exception.getMessage))
//      case Success(value) => Right(value)
//    }
//  }

  override def createOrder(orderForm: OrderForm, merchantId: String): Either[String, Order] = {
    Try(dao.createOrder(orderForm, merchantId)) match {
      case Success(value) =>
        println("22222222222222222222")
        Right(value)
      case Failure(exception) => Left(exception.getMessage)
    }

  }

  override def updateOrder(merchantId: String, updatedOrder: OrderForm, number: String): Either[String, Order] = {
    Try(dao.updateOrderById(merchantId, updatedOrder, number)) match {
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
