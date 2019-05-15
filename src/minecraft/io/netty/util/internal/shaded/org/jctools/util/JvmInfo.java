package io.netty.util.internal.shaded.org.jctools.util;
import sun.misc.Unsafe;

public abstract interface JvmInfo { public static final int CACHE_LINE_SIZE = Integer.getInteger("jctools.cacheLineSize", 64).intValue();
  public static final int PAGE_SIZE = UnsafeAccess.UNSAFE.pageSize();
  public static final int CPUs = Runtime.getRuntime().availableProcessors();
}
