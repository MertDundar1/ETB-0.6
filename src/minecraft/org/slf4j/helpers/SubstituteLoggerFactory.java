package org.slf4j.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;





























public class SubstituteLoggerFactory
  implements ILoggerFactory
{
  public SubstituteLoggerFactory() {}
  
  final ConcurrentMap<String, SubstituteLogger> loggers = new ConcurrentHashMap();
  
  public Logger getLogger(String name) {
    SubstituteLogger logger = (SubstituteLogger)loggers.get(name);
    if (logger == null) {
      logger = new SubstituteLogger(name);
      SubstituteLogger oldLogger = (SubstituteLogger)loggers.putIfAbsent(name, logger);
      if (oldLogger != null)
        logger = oldLogger;
    }
    return logger;
  }
  
  public List<String> getLoggerNames() {
    return new ArrayList(loggers.keySet());
  }
  
  public List<SubstituteLogger> getLoggers() {
    return new ArrayList(loggers.values());
  }
  
  public void clear() {
    loggers.clear();
  }
}
