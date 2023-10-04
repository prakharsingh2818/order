package com.nashtech.controllers

import com.nashtech.OrdersController
import play.api.mvc.{Action, AnyContent, ControllerComponents, Request}
import v1._
import com.nashtech.services.OrderService
import com.nashtech.order.v1.models.json._

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
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

}
