// @GENERATOR:play-routes-compiler
// @SOURCE:conf/routes

import play.api.routing.JavaScriptReverseRoute


import _root_.controllers.Assets.Asset

// @LINE:5
package com.nashtech.controllers.javascript {

  // @LINE:5
  class ReverseOrders(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:5
    def getByNumber: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "com.nashtech.controllers.Orders.getByNumber",
      """
        function(merchant_id0,number1) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + encodeURIComponent((""" + implicitly[play.api.mvc.PathBindable[String]].javascriptUnbind + """)("merchant_id", merchant_id0)) + "/orders/" + encodeURIComponent((""" + implicitly[play.api.mvc.PathBindable[String]].javascriptUnbind + """)("number", number1))})
        }
      """
    )
  
    // @LINE:6
    def post: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "com.nashtech.controllers.Orders.post",
      """
        function(merchant_id0) {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + encodeURIComponent((""" + implicitly[play.api.mvc.PathBindable[String]].javascriptUnbind + """)("merchant_id", merchant_id0)) + "/orders"})
        }
      """
    )
  
  }


}
