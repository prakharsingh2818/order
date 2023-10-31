package com.nashtech.controllers

import com.nashtech.OrdersController
import com.nashtech.order.v1.models.OrderForm
import com.nashtech.services.OrderService
import play.api.mvc.{Action, AnyContent, ControllerComponents, Request}

import javax.inject.Singleton
import scala.concurrent.Future


@Singleton
class Orders(
              val controllerComponents: ControllerComponents,
              service: OrderService
            ) extends OrdersController {
  override def getByNumber(request: Request[AnyContent], merchantId: String, number: String): Future[GetByNumber] = {
    service.getByNumber(merchantId, number) match {
      case Left(_) => Future.successful(GetByNumber.HTTP404)
      case Right(order) => Future.successful(GetByNumber.HTTP200(order))
    }
  }

  def index: Action[AnyContent] = Action {
    Ok("Hello World")
  }

  override def post(request: Request[OrderForm], merchantId: String ,body: OrderForm): Future[Post] = {
    service.createOrder(body) match {
      case Left(_)=>Future.successful(Post.HTTP404)
      case Right(orderForm: OrderForm) => Future.successful(Post.HTTP200(orderForm))
    }
  }

  override def put(request: Request[OrderForm], merchantId: String, body: OrderForm): Future[Put] = ???

  override def delete(request: Request[AnyContent], merchantId: String): Future[Delete] = {
    service.deleteById(merchantId) match {
      case Left(_) => Future.successful(Delete.HTTP404)
      case Right(_) => Future.successful(Delete.HTTP200)
    }
  }
}
