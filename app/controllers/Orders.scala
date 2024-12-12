package com.nashtech.controllers

import com.nashtech.order.v1.controllers.OrdersController
import com.nashtech.order.v1.models.{Order, OrderForm}
import com.nashtech.services.OrderService
import play.api.mvc.{AnyContent, ControllerComponents, Request}

import javax.inject.Inject
import scala.concurrent.Future


class Orders @Inject()(
  val controllerComponents: ControllerComponents,
  service: OrderService
) extends OrdersController {

  override def getByNumber(request: Request[AnyContent], merchantId: String, number: String): Future[GetByNumber] = {
    println(s"Inside Controller")

    /*Future(service.getTest).map {
      case Left(ex) =>
        println(s"Failed Inside Controller ${ex}")
        (GetByNumber.HTTP404)
      case Right(value) =>
        println(s"Success inside Controller $value")
       (GetByNumber.HTTP200(Order(id = value, number = value, merchantId = "123", submittedAt = DateTime.now(), total = 13)))
    }.recoverWith {
      case ex => println(s"FAILED FUTURE CONTROLLER ${ex}")
       Future.successful(GetByNumber.HTTP404)
    }*/
    /*service.getTest match {
      case Left(ex) =>
        println(s"Failed Inside Controller V2 ${ex}")
        Future.successful(GetByNumber.HTTP404)
      case Right(value) =>
        println(s"Success inside Controller V2 $value ")
        Future.successful(GetByNumber.HTTP200(Order(id = value, number = value, merchantId = "123", submittedAt = DateTime.now(), total = 13)))
    }*/
    // Future.successful(GetByNumber.HTTP200(Order(id = "123", number = "123", merchantId = "123", submittedAt = DateTime.now(), total = 13)))
    service.getByNumber(merchantId, number) match {
      case Left(_) => Future.successful(GetByNumber.HTTP404)
      case Right(order) => Future.successful(GetByNumber.HTTP200(order))
    }
  }

  /*def getTest(request: Request[AnyContent], merchantId: String): Future[Result] = {
    service.getTest match {
      case Left(_) => Future.successful(NotFound)
      case Right(value) => Future.successful(Ok(value))
    }
  }*/

  override def post(request: Request[OrderForm], merchantId: String, body: OrderForm): Future[Post] = {
    println("11111111111111111")
    service.createOrder(body, merchantId) match {
      case Left(_) => Future.successful(Post.HTTP401)
      case Right(order: Order) => Future.successful(Post.HTTP200(order))
    }
  }

  override def putByNumber(request: play.api.mvc.Request[com.nashtech.order.v1.models.OrderForm],
                           merchantId: String,
                           number: String,
                           body: com.nashtech.order.v1.models.OrderForm): Future[PutByNumber] = {
    service.updateOrder(merchantId, body, number) match {
      case Left(_) => Future.successful(PutByNumber.HTTP404)
      case Right(order: Order) => Future.successful(PutByNumber.HTTP200(order))
    }
  }

  override def delete(request: Request[AnyContent], merchantId: String): Future[Delete] = {
    service.deleteAllByMerchantId(merchantId) match {
      case Left(_) =>
        Future.successful(Delete.HTTP404)
      case Right(_) => Future.successful(Delete.HTTP200)
    }
  }
}