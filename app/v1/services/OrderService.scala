package v1.services

import com.google.inject.ImplementedBy
import com.nashtech.order.v1.models.Order
import org.joda.time.DateTime

import javax.inject.Singleton

@ImplementedBy(classOf[OrderServiceImpl])
trait OrderService {
  def getByNumber(merchantId: String, number: String): Either[Seq[String], Order]
}

@Singleton
class OrderServiceImpl extends OrderService {
  private val db: Map[String, Order] = Map(
    "1" -> Order(id = "1", number = "1", merchantId = "X", submittedAt = DateTime.now(), total = 302.5)
  )

  override def getByNumber(merchantId: String, number: String): Either[Seq[String], Order] = {
    db.get(number) match {
      case Some(order) => Right(order)
      case None => Left(Seq("Order Not Found"))
    }
  }
}
