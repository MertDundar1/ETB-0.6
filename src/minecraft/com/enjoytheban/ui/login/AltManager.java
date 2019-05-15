package com.enjoytheban.ui.login;

import java.util.List;

public class AltManager {
  static List<Alt> alts;
  static Alt lastAlt;
  
  public AltManager() {}
  
  public static void init() {
    setupAlts();
    getAlts();
  }
  
  public Alt getLastAlt() {
    return lastAlt;
  }
  
  public void setLastAlt(Alt alt)
  {
    lastAlt = alt;
  }
  
  public static void setupAlts()
  {
    alts = new java.util.ArrayList();
  }
  
  public static List<Alt> getAlts()
  {
    return alts;
  }
}
