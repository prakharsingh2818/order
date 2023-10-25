package com.nashtech.database

import anorm.{RowParser, SQL, SqlParser, SqlQuery}
import com.nashtech.order.v1.models.Order
import org.joda.time.DateTime
import play.api.db.Database

import javax.inject.Inject

class Connection @Inject() (db: Database) {

  def getAllOrder: Seq[Order] = {
    db.withConnection { implicit connection =>
      val query: SqlQuery = SQL("select id, number, merchant_id, submitted_at, total from Orders")
      val orders: List[Order] = query.as(OrderParser().*)
      orders
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
