package ch.qos.logback.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;





























public class FileSize
{
  private static final String LENGTH_PART = "([0-9]+)";
  private static final int DOUBLE_GROUP = 1;
  private static final String UNIT_PART = "(|kb|mb|gb)s?";
  private static final int UNIT_GROUP = 2;
  private static final Pattern FILE_SIZE_PATTERN = Pattern.compile("([0-9]+)\\s*(|kb|mb|gb)s?", 2);
  
  static final long KB_COEFFICIENT = 1024L;
  
  static final long MB_COEFFICIENT = 1048576L;
  static final long GB_COEFFICIENT = 1073741824L;
  final long size;
  
  FileSize(long size)
  {
    this.size = size;
  }
  
  public long getSize() {
    return size;
  }
  
  public static FileSize valueOf(String fileSizeStr) {
    Matcher matcher = FILE_SIZE_PATTERN.matcher(fileSizeStr);
    

    if (matcher.matches()) {
      String lenStr = matcher.group(1);
      String unitStr = matcher.group(2);
      
      long lenValue = Long.valueOf(lenStr).longValue();
      long coefficient; if (unitStr.equalsIgnoreCase("")) {
        coefficient = 1L; } else { long coefficient;
        if (unitStr.equalsIgnoreCase("kb")) {
          coefficient = 1024L; } else { long coefficient;
          if (unitStr.equalsIgnoreCase("mb")) {
            coefficient = 1048576L; } else { long coefficient;
            if (unitStr.equalsIgnoreCase("gb")) {
              coefficient = 1073741824L;
            } else
              throw new IllegalStateException("Unexpected " + unitStr); } } }
      long coefficient;
      return new FileSize(lenValue * coefficient);
    }
    throw new IllegalArgumentException("String value [" + fileSizeStr + "] is not in the expected format.");
  }
}
