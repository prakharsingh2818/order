package com.nashtech.database

import anorm.{RowParser, SQL, SqlParser, ~}
import com.nashtech.order.v1.models.{Order, OrderForm}
import org.joda.time.DateTime
import play.api.db.Database

import javax.inject.Inject

class OrdersDao @Inject()(db: Database) {

  def getAllOrder(merchantId: String): Seq[Order] = {
    db.withConnection { implicit connection =>
      SQL(BaseQuery.selectQuery(merchantId)).as(OrderParser().*)
    }
  }

  def createOrder(orderform: OrderForm): Order = {
    db.withConnection { implicit connection =>
      SQL(BaseQuery.insertQuery(orderform)).as(OrderParser().single)
    }
  }

  def deleteById(id: String): Order = {
    db.withConnection { implicit connection =>
      SQL(BaseQuery.deleteQuery(id)).as(OrderParser().single)
    }
  }

  private def generateOrderId(): String = {
    java.util.UUID.randomUUID().toString
  }

  private def generateOrderNumber(): String = {
    java.util.UUID.randomUUID().toString
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

  object BaseQuery {
    def selectQuery(merchantId: String): String = {
      s"SELECT * FROM Orders where merchant_id = $merchantId;"
    }

    def insertQuery(orderForm: OrderForm): String = {
      val id = generateOrderId()
      val orderNumber = generateOrderNumber()
      val query =
        s"""
           |INSERT INTO Orders
           |id,
           |number,
           |merchant_id,
           |submitted_at,
           |total
           |)
           |VALUES
           |(
           |$id
           |$orderNumber
           |${orderForm.merchantId}
           |${DateTime.now()}
           |${orderForm.total}
           |)
           |""".stripMargin
      query
    }

    def deleteQuery(id: String): String = {
      val query =
        s"""
           |DELETE FROM Orders
           |WHERE id = $id
           |RETURNING *;
           |""".stripMargin
      query
    }

    def updateQuery(orderForm: OrderForm, id: String): String = {
      val query =
        s"""
           |UPDATE Orders
           |SET merchant_id = ${orderForm.merchantId},
           |total = ${orderForm.total}
           |WHERE id = $id;
           |""".stripMargin
      query
    }
  }

}
