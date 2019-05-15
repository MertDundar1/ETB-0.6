package io.netty.util.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;


















public final class ThrowableUtil
{
  private ThrowableUtil() {}
  
  public static <T extends Throwable> T unknownStackTrace(T cause, Class<?> clazz, String method)
  {
    cause.setStackTrace(new StackTraceElement[] { new StackTraceElement(clazz.getName(), method, null, -1) });
    return cause;
  }
  





  public static String stackTraceToString(Throwable cause)
  {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PrintStream pout = new PrintStream(out);
    cause.printStackTrace(pout);
    pout.flush();
    try {
      return new String(out.toByteArray());
    } finally {
      try {
        out.close();
      }
      catch (IOException localIOException1) {}
    }
  }
}
