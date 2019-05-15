package com.enjoytheban.api;

import java.lang.reflect.Method;

public class AALAPI {
  public AALAPI() {}
  
  public static String getUsername() { if (username == null) {
      username = getUsernameUncached();
    }
    return username;
  }
  
  private static String username;
  private static String getUsernameUncached() {
    Class api;
    try { api = Class.forName("net.aal.API");
    } catch (ClassNotFoundException ignored) { Class api;
      api = null;
    }
    if (api == null) {
      return "debug-mode";
    }
    try {
      return (String)api.getMethod("getUsername", new Class[0]).invoke(null, new Object[0]);
    } catch (Exception e) {
      e.printStackTrace(); }
    return "error-getting-username";
  }
}
