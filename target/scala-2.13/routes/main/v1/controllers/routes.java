// @GENERATOR:play-routes-compiler
// @SOURCE:conf/routes

package v1.controllers;

import router.RoutesPrefix;

public class routes {
  
  public static final v1.controllers.ReverseOrders Orders = new v1.controllers.ReverseOrders(RoutesPrefix.byNamePrefix());

  public static class javascript {
    
    public static final v1.controllers.javascript.ReverseOrders Orders = new v1.controllers.javascript.ReverseOrders(RoutesPrefix.byNamePrefix());
  }

}
