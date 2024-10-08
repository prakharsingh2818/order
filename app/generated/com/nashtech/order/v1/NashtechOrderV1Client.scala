/**
 * Generated by API Builder - https://www.apibuilder.io
 * Service version: 1.0.1
 * User agent: apibuilder app.apibuilder.io/nashtech/order/latest/play_2_8_client
 */
package com.nashtech.order.v1.models {

  final case class Error(
    code: String,
    message: Seq[String]
  )

  final case class Order(
    id: String,
    number: String,
    merchantId: String,
    submittedAt: _root_.org.joda.time.DateTime,
    total: BigDecimal
  )

  final case class OrderForm(
    total: BigDecimal
  )

}

package com.nashtech.order.v1.models {

  package object json {
    import play.api.libs.json.__
    import play.api.libs.json.JsString
    import play.api.libs.json.Writes
    import play.api.libs.functional.syntax._
    import com.nashtech.order.v1.models.json._
    import io.apibuilder.common.v0.models.json._
    import io.apibuilder.generator.v0.models.json._

    private[v1] implicit val jsonReadsUUID: play.api.libs.json.Reads[_root_.java.util.UUID] = __.read[String].map { str =>
      _root_.java.util.UUID.fromString(str)
    }

    private[v1] implicit val jsonWritesUUID: play.api.libs.json.Writes[_root_.java.util.UUID] = (x: _root_.java.util.UUID) => play.api.libs.json.JsString(x.toString)

    private[v1] implicit val jsonReadsJodaDateTime: play.api.libs.json.Reads[_root_.org.joda.time.DateTime] = __.read[String].map { str =>
      _root_.org.joda.time.format.ISODateTimeFormat.dateTimeParser.parseDateTime(str)
    }

    private[v1] implicit val jsonWritesJodaDateTime: play.api.libs.json.Writes[_root_.org.joda.time.DateTime] = (x: _root_.org.joda.time.DateTime) => {
      play.api.libs.json.JsString(_root_.org.joda.time.format.ISODateTimeFormat.dateTime.print(x))
    }

    private[v1] implicit val jsonReadsJodaLocalDate: play.api.libs.json.Reads[_root_.org.joda.time.LocalDate] = __.read[String].map { str =>
      _root_.org.joda.time.format.ISODateTimeFormat.dateTimeParser.parseLocalDate(str)
    }

    private[v1] implicit val jsonWritesJodaLocalDate: play.api.libs.json.Writes[_root_.org.joda.time.LocalDate] = (x: _root_.org.joda.time.LocalDate) => {
      play.api.libs.json.JsString(_root_.org.joda.time.format.ISODateTimeFormat.date.print(x))
    }

    implicit def jsonReadsOrderError: play.api.libs.json.Reads[com.nashtech.order.v1.models.Error] = {
      for {
        code <- (__ \ "code").read[String]
        message <- (__ \ "message").read[Seq[String]]
      } yield Error(code, message)
    }

    def jsObjectError(obj: com.nashtech.order.v1.models.Error): play.api.libs.json.JsObject = {
      play.api.libs.json.Json.obj(
        "code" -> play.api.libs.json.JsString(obj.code),
        "message" -> play.api.libs.json.Json.toJson(obj.message)
      )
    }

    implicit def jsonWritesOrderError: play.api.libs.json.Writes[Error] = {
      (obj: com.nashtech.order.v1.models.Error) => {
        com.nashtech.order.v1.models.json.jsObjectError(obj)
      }
    }

    implicit def jsonReadsOrderOrder: play.api.libs.json.Reads[com.nashtech.order.v1.models.Order] = {
      for {
        id <- (__ \ "id").read[String]
        number <- (__ \ "number").read[String]
        merchantId <- (__ \ "merchant_id").read[String]
        submittedAt <- (__ \ "submitted_at").read[_root_.org.joda.time.DateTime]
        total <- (__ \ "total").read[BigDecimal]
      } yield Order(id, number, merchantId, submittedAt, total)
    }

    def jsObjectOrder(obj: com.nashtech.order.v1.models.Order): play.api.libs.json.JsObject = {
      play.api.libs.json.Json.obj(
        "id" -> play.api.libs.json.JsString(obj.id),
        "number" -> play.api.libs.json.JsString(obj.number),
        "merchant_id" -> play.api.libs.json.JsString(obj.merchantId),
        "submitted_at" -> play.api.libs.json.JsString(_root_.org.joda.time.format.ISODateTimeFormat.dateTime.print(obj.submittedAt)),
        "total" -> play.api.libs.json.JsNumber(obj.total)
      )
    }

    implicit def jsonWritesOrderOrder: play.api.libs.json.Writes[Order] = {
      (obj: com.nashtech.order.v1.models.Order) => {
        com.nashtech.order.v1.models.json.jsObjectOrder(obj)
      }
    }

    implicit def jsonReadsOrderOrderForm: play.api.libs.json.Reads[com.nashtech.order.v1.models.OrderForm] = {
      (__ \ "total").read[BigDecimal].map { x => OrderForm(total = x) }
    }

    def jsObjectOrderForm(obj: com.nashtech.order.v1.models.OrderForm): play.api.libs.json.JsObject = {
      play.api.libs.json.Json.obj(
        "total" -> play.api.libs.json.JsNumber(obj.total)
      )
    }

    implicit def jsonWritesOrderOrderForm: play.api.libs.json.Writes[OrderForm] = {
      (obj: com.nashtech.order.v1.models.OrderForm) => {
        com.nashtech.order.v1.models.json.jsObjectOrderForm(obj)
      }
    }
  }
}

package com.nashtech.order.v1 {

  object Bindables {

    import play.api.mvc.{PathBindable, QueryStringBindable}

    // import models directly for backwards compatibility with prior versions of the generator
    import Core._

    object Core {
      implicit def pathBindableDateTimeIso8601(implicit stringBinder: QueryStringBindable[String]): PathBindable[_root_.org.joda.time.DateTime] = ApibuilderPathBindable(ApibuilderTypes.dateTimeIso8601)
      implicit def queryStringBindableDateTimeIso8601(implicit stringBinder: QueryStringBindable[String]): QueryStringBindable[_root_.org.joda.time.DateTime] = ApibuilderQueryStringBindable(ApibuilderTypes.dateTimeIso8601)

      implicit def pathBindableDateIso8601(implicit stringBinder: QueryStringBindable[String]): PathBindable[_root_.org.joda.time.LocalDate] = ApibuilderPathBindable(ApibuilderTypes.dateIso8601)
      implicit def queryStringBindableDateIso8601(implicit stringBinder: QueryStringBindable[String]): QueryStringBindable[_root_.org.joda.time.LocalDate] = ApibuilderQueryStringBindable(ApibuilderTypes.dateIso8601)
    }

    trait ApibuilderTypeConverter[T] {

      def convert(value: String): T

      def convert(value: T): String

      def example: T

      def validValues: Seq[T] = Nil

      def errorMessage(key: String, value: String, ex: java.lang.Exception): String = {
        val base = s"Invalid value '$value' for parameter '$key'. "
        validValues.toList match {
          case Nil => base + "Ex: " + convert(example)
          case values => base + ". Valid values are: " + values.mkString("'", "', '", "'")
        }
      }
    }

    object ApibuilderTypes {
      val dateTimeIso8601: ApibuilderTypeConverter[_root_.org.joda.time.DateTime] = new ApibuilderTypeConverter[_root_.org.joda.time.DateTime] {
        override def convert(value: String): _root_.org.joda.time.DateTime = _root_.org.joda.time.format.ISODateTimeFormat.dateTimeParser.parseDateTime(value)
        override def convert(value: _root_.org.joda.time.DateTime): String = _root_.org.joda.time.format.ISODateTimeFormat.dateTime.print(value)
        override def example: _root_.org.joda.time.DateTime = _root_.org.joda.time.DateTime.now
      }

      val dateIso8601: ApibuilderTypeConverter[_root_.org.joda.time.LocalDate] = new ApibuilderTypeConverter[_root_.org.joda.time.LocalDate] {
        override def convert(value: String): _root_.org.joda.time.LocalDate = _root_.org.joda.time.format.ISODateTimeFormat.dateTimeParser.parseLocalDate(value)
        override def convert(value: _root_.org.joda.time.LocalDate): String = _root_.org.joda.time.format.ISODateTimeFormat.date.print(value)
        override def example: _root_.org.joda.time.LocalDate = _root_.org.joda.time.LocalDate.now
      }
    }

    final case class ApibuilderQueryStringBindable[T](
      converters: ApibuilderTypeConverter[T]
    ) extends QueryStringBindable[T] {

      override def bind(key: String, params: Map[String, Seq[String]]): _root_.scala.Option[_root_.scala.Either[String, T]] = {
        params.getOrElse(key, Nil).headOption.map { v =>
          try {
            Right(
              converters.convert(v)
            )
          } catch {
            case ex: java.lang.Exception => Left(
              converters.errorMessage(key, v, ex)
            )
          }
        }
      }

      override def unbind(key: String, value: T): String = {
        s"$key=${converters.convert(value)}"
      }
    }

    final case class ApibuilderPathBindable[T](
      converters: ApibuilderTypeConverter[T]
    ) extends PathBindable[T] {

      override def bind(key: String, value: String): _root_.scala.Either[String, T] = {
        try {
          Right(
            converters.convert(value)
          )
        } catch {
          case ex: java.lang.Exception => Left(
            converters.errorMessage(key, value, ex)
          )
        }
      }

      override def unbind(key: String, value: T): String = {
        converters.convert(value)
      }
    }

  }

}


package com.nashtech.order.v1 {

  object Constants {

    val BaseUrl = "https://nashtechglobal.com"
    val Namespace = "com.nashtech.order.v1"
    val UserAgent = "apibuilder app.apibuilder.io/nashtech/order/latest/play_2_8_client"
    val Version = "1.0.1"
    val VersionMajor = 1

  }

  class Client(
    ws: play.api.libs.ws.WSClient,
    val baseUrl: String = "https://nashtechglobal.com",
    auth: scala.Option[com.nashtech.order.v1.Authorization] = None,
    defaultHeaders: Seq[(String, String)] = Nil
  ) extends interfaces.Client {
    import com.nashtech.order.v1.models.json._
    import io.apibuilder.common.v0.models.json._
    import io.apibuilder.generator.v0.models.json._

    private val logger = play.api.Logger("com.nashtech.order.v1.Client")

    logger.info(s"Initializing com.nashtech.order.v1.Client for url $baseUrl")

    def orders: Orders = Orders

    object Orders extends Orders {
      override def getByNumber(
        merchantId: String,
        number: String,
        requestHeaders: Seq[(String, String)] = Nil
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[com.nashtech.order.v1.models.Order] = {
        _executeRequest("GET", s"/${play.utils.UriEncoding.encodePathSegment(merchantId, "UTF-8")}/orders/${play.utils.UriEncoding.encodePathSegment(number, "UTF-8")}", requestHeaders = requestHeaders).map {
          case r if r.status == 200 => _root_.com.nashtech.order.v1.Client.parseJson("com.nashtech.order.v1.models.Order", r, _.validate[com.nashtech.order.v1.models.Order])
          case r if r.status == 401 => throw com.nashtech.order.v1.errors.UnitResponse(r.status)
          case r if r.status == 404 => throw com.nashtech.order.v1.errors.UnitResponse(r.status)
          case r => throw com.nashtech.order.v1.errors.FailedRequest(r.status, s"Unsupported response code[${r.status}]. Expected: 200, 401, 404")
        }
      }

      override def post(
        merchantId: String,
        orderForm: com.nashtech.order.v1.models.OrderForm,
        requestHeaders: Seq[(String, String)] = Nil
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[com.nashtech.order.v1.models.Order] = {
        val payload = play.api.libs.json.Json.toJson(orderForm)

        _executeRequest("POST", s"/${play.utils.UriEncoding.encodePathSegment(merchantId, "UTF-8")}/orders", body = Some(payload), requestHeaders = requestHeaders).map {
          case r if r.status == 200 => _root_.com.nashtech.order.v1.Client.parseJson("com.nashtech.order.v1.models.Order", r, _.validate[com.nashtech.order.v1.models.Order])
          case r if r.status == 401 => throw com.nashtech.order.v1.errors.UnitResponse(r.status)
          case r if r.status == 404 => throw com.nashtech.order.v1.errors.UnitResponse(r.status)
          case r if r.status == 422 => throw com.nashtech.order.v1.errors.ErrorResponse(r)
          case r => throw com.nashtech.order.v1.errors.FailedRequest(r.status, s"Unsupported response code[${r.status}]. Expected: 200, 401, 404, 422")
        }
      }

      override def putByNumber(
        merchantId: String,
        number: String,
        orderForm: com.nashtech.order.v1.models.OrderForm,
        requestHeaders: Seq[(String, String)] = Nil
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[com.nashtech.order.v1.models.Order] = {
        val payload = play.api.libs.json.Json.toJson(orderForm)

        _executeRequest("PUT", s"/${play.utils.UriEncoding.encodePathSegment(merchantId, "UTF-8")}/orders/${play.utils.UriEncoding.encodePathSegment(number, "UTF-8")}", body = Some(payload), requestHeaders = requestHeaders).map {
          case r if r.status == 200 => _root_.com.nashtech.order.v1.Client.parseJson("com.nashtech.order.v1.models.Order", r, _.validate[com.nashtech.order.v1.models.Order])
          case r if r.status == 401 => throw com.nashtech.order.v1.errors.UnitResponse(r.status)
          case r if r.status == 404 => throw com.nashtech.order.v1.errors.UnitResponse(r.status)
          case r if r.status == 422 => throw com.nashtech.order.v1.errors.ErrorResponse(r)
          case r => throw com.nashtech.order.v1.errors.FailedRequest(r.status, s"Unsupported response code[${r.status}]. Expected: 200, 401, 404, 422")
        }
      }

      override def delete(
        merchantId: String,
        requestHeaders: Seq[(String, String)] = Nil
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Unit] = {
        _executeRequest("DELETE", s"/${play.utils.UriEncoding.encodePathSegment(merchantId, "UTF-8")}/orders", requestHeaders = requestHeaders).map {
          case r if r.status == 200 => ()
          case r if r.status == 401 => throw com.nashtech.order.v1.errors.UnitResponse(r.status)
          case r if r.status == 404 => throw com.nashtech.order.v1.errors.UnitResponse(r.status)
          case r if r.status == 422 => throw com.nashtech.order.v1.errors.ErrorResponse(r)
          case r => throw com.nashtech.order.v1.errors.FailedRequest(r.status, s"Unsupported response code[${r.status}]. Expected: 200, 401, 404, 422")
        }
      }
    }

    def _requestHolder(path: String): play.api.libs.ws.WSRequest = {

      val holder = ws.url(baseUrl + path).addHttpHeaders(
        "User-Agent" -> Constants.UserAgent,
        "X-Apidoc-Version" -> Constants.Version,
        "X-Apidoc-Version-Major" -> Constants.VersionMajor.toString
      ).addHttpHeaders(defaultHeaders: _*)
      auth.fold(holder) {
        case Authorization.Basic(username, password) => {
          holder.withAuth(username, password.getOrElse(""), play.api.libs.ws.WSAuthScheme.BASIC)
        }
      }
    }

    def _logRequest(method: String, req: play.api.libs.ws.WSRequest): play.api.libs.ws.WSRequest = {
      val queryComponents = for {
        (name, values) <- req.queryString
        value <- values
      } yield s"$name=$value"
      val url = s"${req.url}${queryComponents.mkString("?", "&", "")}"
      auth.fold(logger.info(s"curl -X $method '$url'")) { _ =>
        logger.info(s"curl -X $method -u '[REDACTED]:' '$url'")
      }
      req
    }

    def _executeRequest(
      method: String,
      path: String,
      queryParameters: Seq[(String, String)] = Nil,
      requestHeaders: Seq[(String, String)] = Nil,
      body: Option[play.api.libs.json.JsValue] = None
    ): scala.concurrent.Future[play.api.libs.ws.WSResponse] = {
      method.toUpperCase match {
        case "GET" => {
          _logRequest("GET", _requestHolder(path).addHttpHeaders(requestHeaders: _*).addQueryStringParameters(queryParameters: _*)).get()
        }
        case "POST" => {
          _logRequest("POST", _requestHolder(path).addHttpHeaders(_withJsonContentType(requestHeaders): _*).addQueryStringParameters(queryParameters: _*)).post(body.getOrElse(play.api.libs.json.Json.obj()))
        }
        case "PUT" => {
          _logRequest("PUT", _requestHolder(path).addHttpHeaders(_withJsonContentType(requestHeaders): _*).addQueryStringParameters(queryParameters: _*)).put(body.getOrElse(play.api.libs.json.Json.obj()))
        }
        case "PATCH" => {
          _logRequest("PATCH", _requestHolder(path).addHttpHeaders(requestHeaders: _*).addQueryStringParameters(queryParameters: _*)).patch(body.getOrElse(play.api.libs.json.Json.obj()))
        }
        case "DELETE" => {
          _logRequest("DELETE", _requestHolder(path).addHttpHeaders(requestHeaders: _*).addQueryStringParameters(queryParameters: _*)).delete()
        }
         case "HEAD" => {
          _logRequest("HEAD", _requestHolder(path).addHttpHeaders(requestHeaders: _*).addQueryStringParameters(queryParameters: _*)).head()
        }
         case "OPTIONS" => {
          _logRequest("OPTIONS", _requestHolder(path).addHttpHeaders(requestHeaders: _*).addQueryStringParameters(queryParameters: _*)).options()
        }
        case _ => {
          _logRequest(method, _requestHolder(path).addHttpHeaders(requestHeaders: _*).addQueryStringParameters(queryParameters: _*))
          sys.error("Unsupported method[%s]".format(method))
        }
      }
    }

    /**
     * Adds a Content-Type: application/json header unless the specified requestHeaders
     * already contain a Content-Type header
     */
    def _withJsonContentType(headers: Seq[(String, String)]): Seq[(String, String)] = {
      headers.find { _._1.toUpperCase == "CONTENT-TYPE" } match {
        case None => headers ++ Seq("Content-Type" -> "application/json; charset=UTF-8")
        case Some(_) => headers
      }
    }

  }

  object Client {

    def parseJson[T](
      className: String,
      r: play.api.libs.ws.WSResponse,
      f: (play.api.libs.json.JsValue => play.api.libs.json.JsResult[T])
    ): T = {
      f(play.api.libs.json.Json.parse(r.body)) match {
        case play.api.libs.json.JsSuccess(x, _) => x
        case play.api.libs.json.JsError(errors) => {
          throw com.nashtech.order.v1.errors.FailedRequest(r.status, s"Invalid json for class[" + className + "]: " + errors.mkString(" "))
        }
      }
    }

  }

  sealed trait Authorization extends _root_.scala.Product with _root_.scala.Serializable
  object Authorization {
    final case class Basic(username: String, password: Option[String] = None) extends Authorization
  }

  package interfaces {

    trait Client {
      def baseUrl: String
      def orders: com.nashtech.order.v1.Orders
    }

  }

  trait Orders {
    def getByNumber(
      merchantId: String,
      number: String,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[com.nashtech.order.v1.models.Order]

    def post(
      merchantId: String,
      orderForm: com.nashtech.order.v1.models.OrderForm,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[com.nashtech.order.v1.models.Order]

    def putByNumber(
      merchantId: String,
      number: String,
      orderForm: com.nashtech.order.v1.models.OrderForm,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[com.nashtech.order.v1.models.Order]

    def delete(
      merchantId: String,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Unit]
  }

  package errors {

    import com.nashtech.order.v1.models.json._
    import io.apibuilder.common.v0.models.json._
    import io.apibuilder.generator.v0.models.json._

    final case class ErrorResponse(
      response: play.api.libs.ws.WSResponse,
      message: Option[String] = None
    ) extends Exception(message.getOrElse(s"${response.status}: ${response.body}")) {
      lazy val error = _root_.com.nashtech.order.v1.Client.parseJson("com.nashtech.order.v1.models.Error", response, _.validate[com.nashtech.order.v1.models.Error])
    }

    final case class UnitResponse(status: Int) extends Exception(s"HTTP $status")

    final case class FailedRequest(responseCode: Int, message: String, requestUri: Option[_root_.java.net.URI] = None) extends _root_.java.lang.Exception(s"HTTP $responseCode: $message")

  }

}