package io.netty.util.internal.shaded.org.jctools.util;

import java.lang.reflect.Field;
import sun.misc.Unsafe;
























public class UnsafeAccess
{
  public static final boolean SUPPORTS_GET_AND_SET;
  public static final Unsafe UNSAFE;
  
  public UnsafeAccess() {}
  
  static
  {
    try
    {
      Field field = Unsafe.class.getDeclaredField("theUnsafe");
      field.setAccessible(true);
      UNSAFE = (Unsafe)field.get(null);
    } catch (Exception e) {
      SUPPORTS_GET_AND_SET = false;
      throw new RuntimeException(e);
    }
    boolean getAndSetSupport = false;
    try {
      Unsafe.class.getMethod("getAndSetObject", new Class[] { Object.class, Long.TYPE, Object.class });
      getAndSetSupport = true;
    }
    catch (Exception localException1) {}
    SUPPORTS_GET_AND_SET = getAndSetSupport;
  }
}
