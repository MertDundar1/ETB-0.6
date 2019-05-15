package ch.qos.logback.classic.util;

import java.util.ArrayList;
import java.util.List;

















public class LoggerNameUtil
{
  public LoggerNameUtil() {}
  
  public static int getFirstSeparatorIndexOf(String name)
  {
    return getSeparatorIndexOf(name, 0);
  }
  







  public static int getSeparatorIndexOf(String name, int fromIndex)
  {
    int dotIndex = name.indexOf('.', fromIndex);
    int dollarIndex = name.indexOf('$', fromIndex);
    
    if ((dotIndex == -1) && (dollarIndex == -1)) return -1;
    if (dotIndex == -1) return dollarIndex;
    if (dollarIndex == -1) { return dotIndex;
    }
    return dotIndex < dollarIndex ? dotIndex : dollarIndex;
  }
  
  public static List<String> computeNameParts(String loggerName) {
    List<String> partList = new ArrayList();
    
    int fromIndex = 0;
    for (;;) {
      int index = getSeparatorIndexOf(loggerName, fromIndex);
      if (index == -1) {
        partList.add(loggerName.substring(fromIndex));
        break;
      }
      partList.add(loggerName.substring(fromIndex, index));
      fromIndex = index + 1;
    }
    return partList;
  }
}
