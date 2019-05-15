package oshi.util;

import java.math.BigDecimal;























public abstract class FormatUtil
{
  private static final long kibiByte = 1024L;
  private static final long mebiByte = 1048576L;
  private static final long gibiByte = 1073741824L;
  private static final long tebiByte = 1099511627776L;
  private static final long pebiByte = 1125899906842624L;
  
  public FormatUtil() {}
  
  public static String formatBytes(long bytes)
  {
    if (bytes == 1L)
      return String.format("%d byte", new Object[] { Long.valueOf(bytes) });
    if (bytes < 1024L)
      return String.format("%d bytes", new Object[] { Long.valueOf(bytes) });
    if ((bytes < 1048576L) && (bytes % 1024L == 0L))
      return String.format("%.0f KB", new Object[] { Double.valueOf(bytes / 1024.0D) });
    if (bytes < 1048576L)
      return String.format("%.1f KB", new Object[] { Double.valueOf(bytes / 1024.0D) });
    if ((bytes < 1073741824L) && (bytes % 1048576L == 0L))
      return String.format("%.0f MB", new Object[] { Double.valueOf(bytes / 1048576.0D) });
    if (bytes < 1073741824L)
      return String.format("%.1f MB", new Object[] { Double.valueOf(bytes / 1048576.0D) });
    if ((bytes % 1073741824L == 0L) && (bytes < 1099511627776L))
      return String.format("%.0f GB", new Object[] { Double.valueOf(bytes / 1.073741824E9D) });
    if (bytes < 1099511627776L)
      return String.format("%.1f GB", new Object[] { Double.valueOf(bytes / 1.073741824E9D) });
    if ((bytes % 1099511627776L == 0L) && (bytes < 1125899906842624L))
      return String.format("%.0f TiB", new Object[] { Double.valueOf(bytes / 1.099511627776E12D) });
    if (bytes < 1125899906842624L) {
      return String.format("%.1f TiB", new Object[] { Double.valueOf(bytes / 1.099511627776E12D) });
    }
    return String.format("%d bytes", new Object[] { Long.valueOf(bytes) });
  }
  







  public static float round(float d, int decimalPlace)
  {
    BigDecimal bd = new BigDecimal(Float.toString(d));
    bd = bd.setScale(decimalPlace, 4);
    return bd.floatValue();
  }
}
