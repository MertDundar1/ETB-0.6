package com.mojang.realmsclient;

import java.io.IOException;

public class RealmsVersion
{
  private static String version;
  
  public RealmsVersion() {}
  
  public static String getVersion()
  {
    if (version != null) {
      return version;
    }
    java.io.BufferedReader reader = null;
    try {
      java.io.InputStream versionStream = RealmsVersion.class.getResourceAsStream("/version");
      reader = new java.io.BufferedReader(new java.io.InputStreamReader(versionStream));
      version = reader.readLine();
      reader.close();
      return version;
    }
    catch (Exception ignore) {}finally
    {
      if (reader != null) {
        try {
          reader.close();
        }
        catch (IOException ignore) {}
      }
    }
    return null;
  }
}
