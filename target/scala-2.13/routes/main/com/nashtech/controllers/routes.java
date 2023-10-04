// @GENERATOR:play-routes-compiler
// @SOURCE:conf/routes

package com.nashtech.controllers;

import router.RoutesPrefix;

public class routes {
  
  public static final com.nashtech.controllers.ReverseOrders Orders = new com.nashtech.controllers.ReverseOrders(RoutesPrefix.byNamePrefix());

  public static class javascript {
    
    public static final com.nashtech.controllers.javascript.ReverseOrders Orders = new com.nashtech.controllers.javascript.ReverseOrders(RoutesPrefix.byNamePrefix());
  }

}
