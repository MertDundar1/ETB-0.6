package ch.qos.logback.core;

import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.spi.LogbackLock;
import ch.qos.logback.core.status.StatusManager;
import ch.qos.logback.core.util.ExecutorServiceUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
















public class ContextBase
  implements Context, LifeCycle
{
  private long birthTime = System.currentTimeMillis();
  
  private String name;
  private StatusManager sm = new BasicStatusManager();
  


  Map<String, String> propertyMap = new HashMap();
  Map<String, Object> objectMap = new HashMap();
  
  LogbackLock configurationLock = new LogbackLock();
  private volatile ExecutorService executorService;
  private LifeCycleManager lifeCycleManager;
  private boolean started;
  
  public ContextBase() {}
  
  public StatusManager getStatusManager() { return sm; }
  











  public void setStatusManager(StatusManager statusManager)
  {
    if (statusManager == null) {
      throw new IllegalArgumentException("null StatusManager not allowed");
    }
    sm = statusManager;
  }
  
  public Map<String, String> getCopyOfPropertyMap() {
    return new HashMap(propertyMap);
  }
  
  public void putProperty(String key, String val) {
    propertyMap.put(key, val);
  }
  






  public String getProperty(String key)
  {
    if ("CONTEXT_NAME".equals(key)) {
      return getName();
    }
    return (String)propertyMap.get(key);
  }
  
  public Object getObject(String key) {
    return objectMap.get(key);
  }
  
  public void putObject(String key, Object value) {
    objectMap.put(key, value);
  }
  
  public void removeObject(String key) {
    objectMap.remove(key);
  }
  
  public String getName()
  {
    return name;
  }
  


  public void start()
  {
    started = true;
  }
  

  public void stop()
  {
    stopExecutorService();
    started = false;
  }
  
  public boolean isStarted() {
    return started;
  }
  



  public void reset()
  {
    removeShutdownHook();
    getLifeCycleManager().reset();
    propertyMap.clear();
    objectMap.clear();
  }
  





  public void setName(String name)
    throws IllegalStateException
  {
    if ((name != null) && (name.equals(this.name))) {
      return;
    }
    if ((this.name == null) || ("default".equals(this.name)))
    {
      this.name = name;
    } else {
      throw new IllegalStateException("Context has been already given a name");
    }
  }
  
  public long getBirthTime() {
    return birthTime;
  }
  
  public Object getConfigurationLock() {
    return configurationLock;
  }
  
  public ExecutorService getExecutorService() {
    if (executorService == null) {
      synchronized (this) {
        if (executorService == null) {
          executorService = ExecutorServiceUtil.newExecutorService();
        }
      }
    }
    return executorService;
  }
  
  private synchronized void stopExecutorService() {
    if (executorService != null) {
      ExecutorServiceUtil.shutdown(executorService);
      executorService = null;
    }
  }
  
  private void removeShutdownHook() {
    Thread hook = (Thread)getObject("SHUTDOWN_HOOK");
    if (hook != null) {
      removeObject("SHUTDOWN_HOOK");
      try {
        Runtime.getRuntime().removeShutdownHook(hook);
      }
      catch (IllegalStateException e) {}
    }
  }
  

  public void register(LifeCycle component)
  {
    getLifeCycleManager().register(component);
  }
  











  synchronized LifeCycleManager getLifeCycleManager()
  {
    if (lifeCycleManager == null) {
      lifeCycleManager = new LifeCycleManager();
    }
    return lifeCycleManager;
  }
  
  public String toString()
  {
    return name;
  }
}
