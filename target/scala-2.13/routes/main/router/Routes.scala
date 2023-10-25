// @GENERATOR:play-routes-compiler
// @SOURCE:conf/routes

package router

import play.core.routing._
import play.core.routing.HandlerInvokerFactory._

import play.api.mvc._

import _root_.controllers.Assets.Asset

class Routes(
  override val errorHandler: play.api.http.HttpErrorHandler, 
  // @LINE:5
  Orders_0: com.nashtech.controllers.Orders,
  val prefix: String
) extends GeneratedRouter {

   @javax.inject.Inject()
   def this(errorHandler: play.api.http.HttpErrorHandler,
    // @LINE:5
    Orders_0: com.nashtech.controllers.Orders
  ) = this(errorHandler, Orders_0, "/")

  def withPrefix(addPrefix: String): Routes = {
    val prefix = play.api.routing.Router.concatPrefix(addPrefix, this.prefix)
    router.RoutesPrefix.setPrefix(prefix)
    new Routes(errorHandler, Orders_0, prefix)
  }

  private[this] val defaultPrefix: String = {
    if (this.prefix.endsWith("/")) "" else "/"
  }

  def documentation = List(
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """""" + "$" + """merchant_id<[^/]+>/orders/""" + "$" + """number<[^/]+>""", """com.nashtech.controllers.Orders.getByNumber(merchant_id:String, number:String)"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """""" + "$" + """merchant_id<[^/]+>/orders""", """com.nashtech.controllers.Orders.post(merchant_id:String)"""),
    Nil
  ).foldLeft(List.empty[(String,String,String)]) { (s,e) => e.asInstanceOf[Any] match {
    case r @ (_,_,_) => s :+ r.asInstanceOf[(String,String,String)]
    case l => s ++ l.asInstanceOf[List[(String,String,String)]]
  }}


  // @LINE:5
  private[this] lazy val com_nashtech_controllers_Orders_getByNumber0_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), DynamicPart("merchant_id", """[^/]+""",true), StaticPart("/orders/"), DynamicPart("number", """[^/]+""",true)))
  )
  private[this] lazy val com_nashtech_controllers_Orders_getByNumber0_invoker = createInvoker(
    Orders_0.getByNumber(fakeValue[String], fakeValue[String]),
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "com.nashtech.controllers.Orders",
      "getByNumber",
      Seq(classOf[String], classOf[String]),
      "GET",
      this.prefix + """""" + "$" + """merchant_id<[^/]+>/orders/""" + "$" + """number<[^/]+>""",
      """""",
      Seq()
    )
  )

  // @LINE:6
  private[this] lazy val com_nashtech_controllers_Orders_post1_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), DynamicPart("merchant_id", """[^/]+""",true), StaticPart("/orders")))
  )
  private[this] lazy val com_nashtech_controllers_Orders_post1_invoker = createInvoker(
    Orders_0.post(fakeValue[String]),
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "com.nashtech.controllers.Orders",
      "post",
      Seq(classOf[String]),
      "POST",
      this.prefix + """""" + "$" + """merchant_id<[^/]+>/orders""",
      """""",
      Seq()
    )
  )


  def routes: PartialFunction[RequestHeader, Handler] = {
  
    // @LINE:5
    case com_nashtech_controllers_Orders_getByNumber0_route(params@_) =>
      call(params.fromPath[String]("merchant_id", None), params.fromPath[String]("number", None)) { (merchant_id, number) =>
        com_nashtech_controllers_Orders_getByNumber0_invoker.call(Orders_0.getByNumber(merchant_id, number))
      }
  
    // @LINE:6
    case com_nashtech_controllers_Orders_post1_route(params@_) =>
      call(params.fromPath[String]("merchant_id", None)) { (merchant_id) =>
        com_nashtech_controllers_Orders_post1_invoker.call(Orders_0.post(merchant_id))
      }
  }
}
