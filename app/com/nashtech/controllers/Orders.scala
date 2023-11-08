package com.nashtech.controllers

import com.nashtech.order.v1.controllers.OrdersController
import com.nashtech.order.v1.models.{Order, OrderForm}
import com.nashtech.services.OrderService
import play.api.mvc.{Action, AnyContent, ControllerComponents, Request}

import javax.inject.Inject
import scala.concurrent.Future


class Orders @Inject()(
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

  override def getAll(request: Request[AnyContent], merchantId: String): Future[GetAll] = {
    service.getAllOrder(merchantId) match {
      case Left(_) => Future.successful(GetAll.HTTP404)
      case Right(orders) => Future.successful(GetAll.HTTP200(orders))

    }
  }

  override def post(request: Request[OrderForm], merchantId: String, body: OrderForm): Future[Post] = {
    service.createOrder(body) match {
      case Left(_) => Future.successful(Post.HTTP404)
      case Right(order: Order) => Future.successful(Post.HTTP200(order))
    }
  }

  override def put(request: Request[OrderForm], merchantId: String, body: OrderForm): Future[Put] = {
    service.updateOrder(merchantId, body) match {
      case Left(_) => Future.successful(Put.HTTP404)
      case Right(order: Order) => Future.successful(Put.HTTP200(order))
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