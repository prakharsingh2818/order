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
  Orders_0: v1.controllers.Orders,
  val prefix: String
) extends GeneratedRouter {

   @javax.inject.Inject()
   def this(errorHandler: play.api.http.HttpErrorHandler,
    // @LINE:5
    Orders_0: v1.controllers.Orders
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
    ("""GET""", this.prefix, """v1.controllers.Orders.index()"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """""" + "$" + """merchant_id<[^/]+>/orders/""" + "$" + """number<[^/]+>""", """v1.controllers.Orders.getByNumber(merchant_id:String, number:String)"""),
    Nil
  ).foldLeft(List.empty[(String,String,String)]) { (s,e) => e.asInstanceOf[Any] match {
    case r @ (_,_,_) => s :+ r.asInstanceOf[(String,String,String)]
    case l => s ++ l.asInstanceOf[List[(String,String,String)]]
  }}


  // @LINE:5
  private[this] lazy val v1_controllers_Orders_index0_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix)))
  )
  private[this] lazy val v1_controllers_Orders_index0_invoker = createInvoker(
    Orders_0.index(),
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "v1.controllers.Orders",
      "index",
      Nil,
      "GET",
      this.prefix + """""",
      """""",
      Seq()
    )
  )

  // @LINE:6
  private[this] lazy val v1_controllers_Orders_getByNumber1_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), DynamicPart("merchant_id", """[^/]+""",true), StaticPart("/orders/"), DynamicPart("number", """[^/]+""",true)))
  )
  private[this] lazy val v1_controllers_Orders_getByNumber1_invoker = createInvoker(
    Orders_0.getByNumber(fakeValue[String], fakeValue[String]),
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "v1.controllers.Orders",
      "getByNumber",
      Seq(classOf[String], classOf[String]),
      "GET",
      this.prefix + """""" + "$" + """merchant_id<[^/]+>/orders/""" + "$" + """number<[^/]+>""",
      """""",
      Seq()
    )
  )


  def routes: PartialFunction[RequestHeader, Handler] = {
  
    // @LINE:5
    case v1_controllers_Orders_index0_route(params@_) =>
      call { 
        v1_controllers_Orders_index0_invoker.call(Orders_0.index())
      }
  
    // @LINE:6
    case v1_controllers_Orders_getByNumber1_route(params@_) =>
      call(params.fromPath[String]("merchant_id", None), params.fromPath[String]("number", None)) { (merchant_id, number) =>
        v1_controllers_Orders_getByNumber1_invoker.call(Orders_0.getByNumber(merchant_id, number))
      }
  }
}
