package org.apache.log4j;

import java.util.Stack;
import org.slf4j.MDC;





















public class NDC
{
  public static final String PREFIX = "NDC";
  
  public NDC() {}
  
  public static void clear()
  {
    int depth = getDepth();
    for (int i = 0; i < depth; i++) {
      String key = "NDC" + i;
      MDC.remove(key);
    }
  }
  
  public static Stack cloneStack()
  {
    return null;
  }
  

  public static void inherit(Stack stack) {}
  
  public static String get()
  {
    return null;
  }
  
  public static int getDepth() {
    int i = 0;
    for (;;) {
      String val = MDC.get("NDC" + i);
      if (val == null) break;
      i++;
    }
    


    return i;
  }
  
  public static String pop() {
    int next = getDepth();
    if (next == 0) {
      return "";
    }
    int last = next - 1;
    String key = "NDC" + last;
    String val = MDC.get(key);
    MDC.remove(key);
    return val;
  }
  
  public static String peek() {
    int next = getDepth();
    if (next == 0) {
      return "";
    }
    int last = next - 1;
    String key = "NDC" + last;
    String val = MDC.get(key);
    return val;
  }
  
  public static void push(String message) {
    int next = getDepth();
    MDC.put("NDC" + next, message);
  }
  
  public static void remove() {}
  
  public static void setMaxDepth(int maxDepth) {}
}
