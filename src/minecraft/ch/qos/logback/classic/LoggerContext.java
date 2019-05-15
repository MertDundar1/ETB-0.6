package ch.qos.logback.classic;

import ch.qos.logback.classic.spi.LoggerComparator;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.classic.spi.LoggerContextVO;
import ch.qos.logback.classic.spi.TurboFilterList;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.classic.util.LoggerNameUtil;
import ch.qos.logback.core.ContextBase;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.status.StatusListener;
import ch.qos.logback.core.status.StatusManager;
import ch.qos.logback.core.status.WarnStatus;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.ILoggerFactory;
import org.slf4j.Marker;




















public class LoggerContext
  extends ContextBase
  implements ILoggerFactory, LifeCycle
{
  final Logger root;
  private int size;
  private int noAppenderWarning = 0;
  private final List<LoggerContextListener> loggerContextListenerList = new ArrayList();
  
  private Map<String, Logger> loggerCache;
  
  private LoggerContextVO loggerContextRemoteView;
  private final TurboFilterList turboFilterList = new TurboFilterList();
  private boolean packagingDataEnabled = true;
  
  private int maxCallerDataDepth = 8;
  
  int resetCount = 0;
  private List<String> frameworkPackages;
  
  public LoggerContext()
  {
    loggerCache = new ConcurrentHashMap();
    
    loggerContextRemoteView = new LoggerContextVO(this);
    root = new Logger("ROOT", null, this);
    root.setLevel(Level.DEBUG);
    loggerCache.put("ROOT", root);
    initEvaluatorMap();
    size = 1;
    frameworkPackages = new ArrayList();
  }
  
  void initEvaluatorMap() {
    putObject("EVALUATOR_MAP", new HashMap());
  }
  



  private void updateLoggerContextVO()
  {
    loggerContextRemoteView = new LoggerContextVO(this);
  }
  
  public void putProperty(String key, String val)
  {
    super.putProperty(key, val);
    updateLoggerContextVO();
  }
  
  public void setName(String name)
  {
    super.setName(name);
    updateLoggerContextVO();
  }
  
  public final Logger getLogger(Class clazz) {
    return getLogger(clazz.getName());
  }
  
  public final Logger getLogger(String name)
  {
    if (name == null) {
      throw new IllegalArgumentException("name argument cannot be null");
    }
    


    if ("ROOT".equalsIgnoreCase(name)) {
      return root;
    }
    
    int i = 0;
    Logger logger = root;
    


    Logger childLogger = (Logger)loggerCache.get(name);
    
    if (childLogger != null) {
      return childLogger;
    }
    


    for (;;)
    {
      int h = LoggerNameUtil.getSeparatorIndexOf(name, i);
      String childName; String childName; if (h == -1) {
        childName = name;
      } else {
        childName = name.substring(0, h);
      }
      
      i = h + 1;
      synchronized (logger) {
        childLogger = logger.getChildByName(childName);
        if (childLogger == null) {
          childLogger = logger.createChildByName(childName);
          loggerCache.put(childName, childLogger);
          incSize();
        }
      }
      logger = childLogger;
      if (h == -1) {
        return childLogger;
      }
    }
  }
  
  private void incSize() {
    size += 1;
  }
  
  int size() {
    return size;
  }
  





  public Logger exists(String name)
  {
    return (Logger)loggerCache.get(name);
  }
  
  final void noAppenderDefinedWarning(Logger logger) {
    if (noAppenderWarning++ == 0) {
      getStatusManager().add(new WarnStatus("No appenders present in context [" + getName() + "] for logger [" + logger.getName() + "].", logger));
    }
  }
  

  public List<Logger> getLoggerList()
  {
    Collection<Logger> collection = loggerCache.values();
    List<Logger> loggerList = new ArrayList(collection);
    Collections.sort(loggerList, new LoggerComparator());
    return loggerList;
  }
  
  public LoggerContextVO getLoggerContextRemoteView() {
    return loggerContextRemoteView;
  }
  
  public void setPackagingDataEnabled(boolean packagingDataEnabled) {
    this.packagingDataEnabled = packagingDataEnabled;
  }
  
  public boolean isPackagingDataEnabled() {
    return packagingDataEnabled;
  }
  








  public void reset()
  {
    resetCount += 1;
    super.reset();
    initEvaluatorMap();
    root.recursiveReset();
    resetTurboFilterList();
    fireOnReset();
    resetListenersExceptResetResistant();
    resetStatusListeners();
  }
  
  private void resetStatusListeners() {
    StatusManager sm = getStatusManager();
    for (StatusListener sl : sm.getCopyOfStatusListenerList()) {
      sm.remove(sl);
    }
  }
  
  public TurboFilterList getTurboFilterList() {
    return turboFilterList;
  }
  
  public void addTurboFilter(TurboFilter newFilter) {
    turboFilterList.add(newFilter);
  }
  



  public void resetTurboFilterList()
  {
    for (TurboFilter tf : turboFilterList) {
      tf.stop();
    }
    turboFilterList.clear();
  }
  

  final FilterReply getTurboFilterChainDecision_0_3OrMore(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t)
  {
    if (turboFilterList.size() == 0) {
      return FilterReply.NEUTRAL;
    }
    return turboFilterList.getTurboFilterChainDecision(marker, logger, level, format, params, t);
  }
  


  final FilterReply getTurboFilterChainDecision_1(Marker marker, Logger logger, Level level, String format, Object param, Throwable t)
  {
    if (turboFilterList.size() == 0) {
      return FilterReply.NEUTRAL;
    }
    return turboFilterList.getTurboFilterChainDecision(marker, logger, level, format, new Object[] { param }, t);
  }
  


  final FilterReply getTurboFilterChainDecision_2(Marker marker, Logger logger, Level level, String format, Object param1, Object param2, Throwable t)
  {
    if (turboFilterList.size() == 0) {
      return FilterReply.NEUTRAL;
    }
    return turboFilterList.getTurboFilterChainDecision(marker, logger, level, format, new Object[] { param1, param2 }, t);
  }
  

  public void addListener(LoggerContextListener listener)
  {
    loggerContextListenerList.add(listener);
  }
  
  public void removeListener(LoggerContextListener listener) {
    loggerContextListenerList.remove(listener);
  }
  
  private void resetListenersExceptResetResistant() {
    List<LoggerContextListener> toRetain = new ArrayList();
    
    for (LoggerContextListener lcl : loggerContextListenerList) {
      if (lcl.isResetResistant()) {
        toRetain.add(lcl);
      }
    }
    loggerContextListenerList.retainAll(toRetain);
  }
  
  private void resetAllListeners() {
    loggerContextListenerList.clear();
  }
  
  public List<LoggerContextListener> getCopyOfListenerList() {
    return new ArrayList(loggerContextListenerList);
  }
  
  void fireOnLevelChange(Logger logger, Level level) {
    for (LoggerContextListener listener : loggerContextListenerList) {
      listener.onLevelChange(logger, level);
    }
  }
  
  private void fireOnReset() {
    for (LoggerContextListener listener : loggerContextListenerList) {
      listener.onReset(this);
    }
  }
  
  private void fireOnStart() {
    for (LoggerContextListener listener : loggerContextListenerList) {
      listener.onStart(this);
    }
  }
  
  private void fireOnStop() {
    for (LoggerContextListener listener : loggerContextListenerList) {
      listener.onStop(this);
    }
  }
  

  public void start()
  {
    super.start();
    fireOnStart();
  }
  
  public void stop() {
    reset();
    fireOnStop();
    resetAllListeners();
    super.stop();
  }
  
  public String toString()
  {
    return getClass().getName() + "[" + getName() + "]";
  }
  
  public int getMaxCallerDataDepth() {
    return maxCallerDataDepth;
  }
  
  public void setMaxCallerDataDepth(int maxCallerDataDepth) {
    this.maxCallerDataDepth = maxCallerDataDepth;
  }
  








  public List<String> getFrameworkPackages()
  {
    return frameworkPackages;
  }
}
