package ch.qos.logback.classic.turbo;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.gaffer.GafferUtil;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.util.EnvUtil;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.event.SaxEvent;
import ch.qos.logback.core.joran.spi.ConfigurationWatchList;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.joran.util.ConfigurationWatchListUtil;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.status.StatusUtil;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import org.slf4j.Marker;





















public class ReconfigureOnChangeFilter
  extends TurboFilter
{
  public static final long DEFAULT_REFRESH_PERIOD = 60000L;
  long refreshPeriod;
  URL mainConfigurationURL;
  protected volatile long nextCheck;
  ConfigurationWatchList configurationWatchList;
  private long invocationCounter;
  private volatile long mask;
  private volatile long lastMaskCheck;
  private static final int MAX_MASK = 65535;
  private static final long MASK_INCREASE_THRESHOLD = 100L;
  private static final long MASK_DECREASE_THRESHOLD = 800L;
  
  public void start()
  {
    configurationWatchList = ConfigurationWatchListUtil.getConfigurationWatchList(context);
    if (configurationWatchList != null) {
      mainConfigurationURL = configurationWatchList.getMainURL();
      if (mainConfigurationURL == null) {
        addWarn("Due to missing top level configuration file, automatic reconfiguration is impossible.");
        return;
      }
      List<File> watchList = configurationWatchList.getCopyOfFileWatchList();
      long inSeconds = refreshPeriod / 1000L;
      addInfo("Will scan for changes in [" + watchList + "] every " + inSeconds + " seconds. ");
      
      synchronized (configurationWatchList) {
        updateNextCheck(System.currentTimeMillis());
      }
      super.start();
    } else {
      addWarn("Empty ConfigurationWatchList in context");
    }
  }
  
  public String toString()
  {
    return "ReconfigureOnChangeFilter{invocationCounter=" + invocationCounter + '}';
  }
  
  public ReconfigureOnChangeFilter()
  {
    refreshPeriod = 60000L;
    







































    invocationCounter = 0L;
    
    mask = 15L;
    lastMaskCheck = System.currentTimeMillis();
  }
  

  public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t)
  {
    if (!isStarted()) {
      return FilterReply.NEUTRAL;
    }
    



    if ((invocationCounter++ & mask) != mask) {
      return FilterReply.NEUTRAL;
    }
    
    long now = System.currentTimeMillis();
    
    synchronized (configurationWatchList) {
      updateMaskIfNecessary(now);
      if (changeDetected(now))
      {



        disableSubsequentReconfiguration();
        detachReconfigurationToNewThread();
      }
    }
    
    return FilterReply.NEUTRAL;
  }
  













  private void updateMaskIfNecessary(long now)
  {
    long timeElapsedSinceLastMaskUpdateCheck = now - lastMaskCheck;
    lastMaskCheck = now;
    if ((timeElapsedSinceLastMaskUpdateCheck < 100L) && (mask < 65535L)) {
      mask = (mask << 1 | 1L);
    } else if (timeElapsedSinceLastMaskUpdateCheck > 800L) {
      mask >>>= 2;
    }
  }
  


  void detachReconfigurationToNewThread()
  {
    addInfo("Detected change in [" + configurationWatchList.getCopyOfFileWatchList() + "]");
    context.getExecutorService().submit(new ReconfiguringThread());
  }
  
  void updateNextCheck(long now) {
    nextCheck = (now + refreshPeriod);
  }
  
  protected boolean changeDetected(long now) {
    if (now >= nextCheck) {
      updateNextCheck(now);
      return configurationWatchList.changeDetected();
    }
    return false;
  }
  
  void disableSubsequentReconfiguration() {
    nextCheck = Long.MAX_VALUE;
  }
  
  public long getRefreshPeriod() {
    return refreshPeriod;
  }
  

  public void setRefreshPeriod(long refreshPeriod) { this.refreshPeriod = refreshPeriod; }
  
  class ReconfiguringThread implements Runnable {
    ReconfiguringThread() {}
    
    public void run() { if (mainConfigurationURL == null) {
        addInfo("Due to missing top level configuration file, skipping reconfiguration");
        return;
      }
      LoggerContext lc = (LoggerContext)context;
      addInfo("Will reset and reconfigure context named [" + context.getName() + "]");
      if (mainConfigurationURL.toString().endsWith("xml")) {
        performXMLConfiguration(lc);
      } else if (mainConfigurationURL.toString().endsWith("groovy")) {
        if (EnvUtil.isGroovyAvailable()) {
          lc.reset();
          

          GafferUtil.runGafferConfiguratorOn(lc, this, mainConfigurationURL);
        } else {
          addError("Groovy classes are not available on the class path. ABORTING INITIALIZATION.");
        }
      }
    }
    
    private void performXMLConfiguration(LoggerContext lc) {
      JoranConfigurator jc = new JoranConfigurator();
      jc.setContext(context);
      StatusUtil statusUtil = new StatusUtil(context);
      List<SaxEvent> eventList = jc.recallSafeConfiguration();
      URL mainURL = ConfigurationWatchListUtil.getMainWatchURL(context);
      lc.reset();
      long threshold = System.currentTimeMillis();
      try {
        jc.doConfigure(mainConfigurationURL);
        if (statusUtil.hasXMLParsingErrors(threshold)) {
          fallbackConfiguration(lc, eventList, mainURL);
        }
      } catch (JoranException e) {
        fallbackConfiguration(lc, eventList, mainURL);
      }
    }
    
    private void fallbackConfiguration(LoggerContext lc, List<SaxEvent> eventList, URL mainURL) {
      JoranConfigurator joranConfigurator = new JoranConfigurator();
      joranConfigurator.setContext(context);
      if (eventList != null) {
        addWarn("Falling back to previously registered safe configuration.");
        try {
          lc.reset();
          JoranConfigurator.informContextOfURLUsedForConfiguration(context, mainURL);
          joranConfigurator.doConfigure(eventList);
          addInfo("Re-registering previous fallback configuration once more as a fallback configuration point");
          joranConfigurator.registerSafeConfiguration();
        } catch (JoranException e) {
          addError("Unexpected exception thrown by a configuration considered safe.", e);
        }
      } else {
        addWarn("No previous configuration to fall back on.");
      }
    }
  }
}
