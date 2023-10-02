// @GENERATOR:play-routes-compiler
// @SOURCE:conf/routes

import play.api.mvc.Call


import _root_.controllers.Assets.Asset

// @LINE:5
package v1.controllers {

  // @LINE:5
  class ReverseOrders(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:5
    def index(): Call = {
      
      Call("GET", _prefix)
    }
  
    // @LINE:6
    def getByNumber(merchant_id:String, number:String): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + play.core.routing.dynamicString(implicitly[play.api.mvc.PathBindable[String]].unbind("merchant_id", merchant_id)) + "/orders/" + play.core.routing.dynamicString(implicitly[play.api.mvc.PathBindable[String]].unbind("number", number)))
    }
  
  }


}
