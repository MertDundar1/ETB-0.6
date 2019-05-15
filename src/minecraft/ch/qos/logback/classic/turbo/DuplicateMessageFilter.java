package ch.qos.logback.classic.turbo;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.Marker;





























public class DuplicateMessageFilter
  extends TurboFilter
{
  public static final int DEFAULT_CACHE_SIZE = 100;
  public static final int DEFAULT_ALLOWED_REPETITIONS = 5;
  public int allowedRepetitions = 5;
  public int cacheSize = 100;
  private LRUMessageCache msgCache;
  
  public DuplicateMessageFilter() {}
  
  public void start() {
    msgCache = new LRUMessageCache(cacheSize);
    super.start();
  }
  
  public void stop()
  {
    msgCache.clear();
    msgCache = null;
    super.stop();
  }
  

  public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t)
  {
    int count = msgCache.getMessageCountAndThenIncrement(format);
    if (count <= allowedRepetitions) {
      return FilterReply.NEUTRAL;
    }
    return FilterReply.DENY;
  }
  
  public int getAllowedRepetitions()
  {
    return allowedRepetitions;
  }
  




  public void setAllowedRepetitions(int allowedRepetitions)
  {
    this.allowedRepetitions = allowedRepetitions;
  }
  
  public int getCacheSize() {
    return cacheSize;
  }
  
  public void setCacheSize(int cacheSize) {
    this.cacheSize = cacheSize;
  }
}
