package com.mojang.realmsclient.util;

import java.util.Map;

public class UploadTokenCache {
  public UploadTokenCache() {}
  
  private static Map<Long, String> tokenCache = new java.util.HashMap();
  
  public static String get(long worldId) {
    return (String)tokenCache.get(Long.valueOf(worldId));
  }
  
  public static void invalidate(long world) {
    tokenCache.remove(Long.valueOf(world));
  }
  
  public static void put(long wid, String token)
  {
    tokenCache.put(Long.valueOf(wid), token);
  }
}
