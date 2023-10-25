package com.nashtech.controllers

import com.nashtech.OrdersController
import com.nashtech.database.Connection
import com.nashtech.order.v1.models.Order
import com.nashtech.services.OrderService
import play.api.mvc.{Action, AnyContent, ControllerComponents, Request}

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future


@Singleton
class Orders @Inject()(
                        val controllerComponents: ControllerComponents,
                        service: OrderService,
                        connection: Connection

                      ) extends OrdersController {
  override def getByNumber(request: Request[AnyContent], merchantId: String, number: String): Future[GetByNumber] = {
   Future.successful(
    service.getByNumber(merchantId, number) match {
      case Left(_) => GetByNumber.HTTP404
      case Right(order) =>connection.getAllOrder
        GetByNumber.HTTP200(order)
    })
  }

  def index: Action[AnyContent] = Action {
    Ok("Hello World")
  }

  override def post(request: Request[Order], merchantId: String, body: Order): Future[Post] = ???
}
