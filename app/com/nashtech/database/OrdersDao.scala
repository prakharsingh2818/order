package com.nashtech.database

import anorm.{RowParser, SQL, SqlParser, ~}
import com.nashtech.order.v1.models.{Order, OrderForm}
import org.joda.time.DateTime
import play.api.db.Database

import javax.inject.Inject

class OrdersDao @Inject()(db: Database) {


  def getByNumber(merchantId: String, number: String): Order = {
    db.withConnection { implicit connection =>
      SQL(BaseQuery.selectQuery(merchantId, number)).as(OrderParser().single)
    }
  }

  def getAllOrder(merchantId: String): Seq[Order] = {
    println("+++++++++++++++++++++++++++++++")
    db.withConnection { implicit connection =>
      SQL(BaseQuery.selectAllQuery(merchantId)).as(OrderParser().*)
    }
  }

  def createOrder(orderForm: OrderForm): Order = {
    println(BaseQuery.insertQuery(orderForm))
    db.withConnection { implicit connection =>
      SQL(BaseQuery.insertQuery(orderForm)).as(OrderParser().single)
    }
  }

  def deleteAllByMerchantId(merchantId: String): Seq[Order] = {
    db.withConnection { implicit connection =>
      SQL(BaseQuery.deleteQuery(merchantId)).as(OrderParser().*)
    }
  }

  def updateOrderById(merchantId: String, orderForm: OrderForm): Order = {
    db.withConnection { implicit connection =>
      SQL(BaseQuery.updateQuery(orderForm, merchantId)).as(OrderParser().single)
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

    def selectAllQuery(merchantId: String): String = {
      val query =
        s"""
           |SELECT
           |id,
           |number,
           |merchant_id,
           |submitted_at,
           |total
           |FROM Orders
           |WHERE merchant_id = $merchantId
           |""".stripMargin
      query
    }

    def selectQuery(merchantId: String, number: String): String = {
      val query =
        s"""
           |SELECT
           |id,
           |number,
           |merchant_id,
           |submitted_at,
           |total
           |FROM Orders
           |WHERE merchant_id = '$merchantId' AND number = '$number'
           |""".stripMargin
      query
    }

    def insertQuery(orderForm: OrderForm): String = {
      println("*********************")
      val orderId = generateOrderId()
      val orderNumber = generateOrderNumber()
      val query =
        s"""
           |INSERT INTO Orders
           |(
           |id,
           |number,
           |merchant_id,
           |submitted_at,
           |total
           |)
           |VALUES
           |(
           |'$orderId',
           |'$orderNumber',
           |'${orderForm.merchantId}',
           |'${DateTime.now()}',
           |${orderForm.total}
           |)
           |RETURNING *
           |""".stripMargin
      query
    }

    def deleteQuery(merchantId: String): String = {
      val query =
        s"""
           |DELETE FROM Orders
           |WHERE merchant_id = '$merchantId'
           |RETURNING *
           |""".stripMargin
      query
    }

    def updateQuery(orderForm: OrderForm, merchantId: String): String = {
      val query =
        s"""
           |UPDATE Orders
           |SET merchant_id = '${orderForm.merchantId}',
           |total = ${orderForm.total}
           |WHERE merchant_id = '$merchantId'
           |RETURNING *
           |""".stripMargin
      query
    }
  }

}
