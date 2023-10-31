package com.nashtech.database

import anorm.{RowParser, SQL, SqlParser, SqlQuery,~}
import com.nashtech.order.v1.models.{Order, OrderForm}
import org.joda.time.DateTime
import play.api.db.Database
import com.nashtech.order.v1.anorm.parsers.Order.parser
import javax.inject.Inject

class OrdersDao @Inject()(db: Database) {

  def getAllOrder(merchantId: String): Seq[Order] = {
    db.withConnection { implicit connection =>
      val query: SqlQuery = SQL((s"select id, number, merchant_id, submitted_at, total from Orders where merchantId = $merchantId"))
      val orders: List[Order] = query.as(OrderParser().*)
      orders
    }
  }

  def createOrder(orderform: OrderForm): Either[String, OrderForm] = {
    if (orderIsValid(orderform)) {
      val orderId = generateOrderId()
      val orderNumber = generateOrderNumber()
      db.withConnection { implicit connection =>
        val insertQuery = SQL(
          """
            |INSERT INTO orders(id, number, merchant_id, submitted_at, total)
            |VALUES({id}, {number}, {merchantId}, {submittedAt}, {total})
        """).on(
          "id" -> orderId,
          "number" -> orderNumber,
          "merchantId" -> orderform.merchantId,
          "submittedAt" -> DateTime.now(),
          "total" -> orderform.total
        )
        insertQuery.executeInsert()
      }
      Right(OrderForm(orderform.merchantId, orderform.total))
    } else {
      Left("Invalid Order")
    }
  }

  def generateOrderId(): String = {
    java.util.UUID.randomUUID().toString
  }

  def generateOrderNumber(): String = {
    java.util.UUID.randomUUID().toString
  }

  def orderIsValid(order: OrderForm): Boolean = {
    order.total > 0
  }

  def deleteById(merchantId: String): Order = {
    db.withConnection { implicit connection =>
      val query: SqlQuery = SQL((s"Delete from orders where merchant_id = $merchantId returning *"))
      query.as(parser().single)
    }
  }


  private def OrderParser(): RowParser[Order] = {
    SqlParser.str("id") ~
      SqlParser.str("number") ~
      SqlParser.str("merchant_id") ~
      SqlParser.get[DateTime]("submitted_at") ~
      SqlParser.double("total") map {

      case id ~ number ~ merchantId ~ submittedAt ~ total =>
        Order(
          id, number, merchantId, submittedAt, total
        )
    }
  }

}
