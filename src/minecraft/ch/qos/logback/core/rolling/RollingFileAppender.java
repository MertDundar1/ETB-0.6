package ch.qos.logback.core.rolling;

import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.rolling.helper.CompressionMode;
import ch.qos.logback.core.rolling.helper.FileNamePattern;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;























public class RollingFileAppender<E>
  extends FileAppender<E>
{
  File currentlyActiveFile;
  TriggeringPolicy<E> triggeringPolicy;
  RollingPolicy rollingPolicy;
  
  public RollingFileAppender() {}
  
  private static String RFA_NO_TP_URL = "http://logback.qos.ch/codes.html#rfa_no_tp";
  private static String RFA_NO_RP_URL = "http://logback.qos.ch/codes.html#rfa_no_rp";
  private static String COLLISION_URL = "http://logback.qos.ch/codes.html#rfa_collision";
  
  public void start() {
    if (triggeringPolicy == null) {
      addWarn("No TriggeringPolicy was set for the RollingFileAppender named " + getName());
      
      addWarn("For more information, please visit " + RFA_NO_TP_URL);
      return;
    }
    

    if (!append) {
      addWarn("Append mode is mandatory for RollingFileAppender");
      append = true;
    }
    
    if (rollingPolicy == null) {
      addError("No RollingPolicy was set for the RollingFileAppender named " + getName());
      
      addError("For more information, please visit " + RFA_NO_RP_URL);
      return;
    }
    

    if (fileAndPatternCollide()) {
      addError("File property collides with fileNamePattern. Aborting.");
      addError("For more information, please visit " + COLLISION_URL);
      return;
    }
    
    if (isPrudent()) {
      if (rawFileProperty() != null) {
        addWarn("Setting \"File\" property to null on account of prudent mode");
        setFile(null);
      }
      if (rollingPolicy.getCompressionMode() != CompressionMode.NONE) {
        addError("Compression is not supported in prudent mode. Aborting");
        return;
      }
    }
    
    currentlyActiveFile = new File(getFile());
    addInfo("Active log file name: " + getFile());
    super.start();
  }
  
  private boolean fileAndPatternCollide() {
    if ((triggeringPolicy instanceof RollingPolicyBase)) {
      RollingPolicyBase base = (RollingPolicyBase)triggeringPolicy;
      FileNamePattern fileNamePattern = fileNamePattern;
      
      if ((fileNamePattern != null) && (fileName != null)) {
        String regex = fileNamePattern.toRegex();
        return fileName.matches(regex);
      }
    }
    return false;
  }
  
  public void stop()
  {
    if (rollingPolicy != null) rollingPolicy.stop();
    if (triggeringPolicy != null) triggeringPolicy.stop();
    super.stop();
  }
  


  public void setFile(String file)
  {
    if ((file != null) && ((triggeringPolicy != null) || (rollingPolicy != null))) {
      addError("File property must be set before any triggeringPolicy or rollingPolicy properties");
      addError("Visit http://logback.qos.ch/codes.html#rfa_file_after for more information");
    }
    super.setFile(file);
  }
  
  public String getFile()
  {
    return rollingPolicy.getActiveFileName();
  }
  


  public void rollover()
  {
    lock.lock();
    



    try
    {
      closeOutputStream();
      attemptRollover();
      attemptOpenFile();
    } finally {
      lock.unlock();
    }
  }
  
  private void attemptOpenFile()
  {
    try {
      currentlyActiveFile = new File(rollingPolicy.getActiveFileName());
      

      openFile(rollingPolicy.getActiveFileName());
    } catch (IOException e) {
      addError("setFile(" + fileName + ", false) call failed.", e);
    }
  }
  
  private void attemptRollover() {
    try {
      rollingPolicy.rollover();
    } catch (RolloverFailure rf) {
      addWarn("RolloverFailure occurred. Deferring roll-over.");
      
      append = true;
    }
  }
  








  protected void subAppend(E event)
  {
    synchronized (triggeringPolicy) {
      if (triggeringPolicy.isTriggeringEvent(currentlyActiveFile, event)) {
        rollover();
      }
    }
    
    super.subAppend(event);
  }
  
  public RollingPolicy getRollingPolicy() {
    return rollingPolicy;
  }
  
  public TriggeringPolicy<E> getTriggeringPolicy() {
    return triggeringPolicy;
  }
  







  public void setRollingPolicy(RollingPolicy policy)
  {
    rollingPolicy = policy;
    if ((rollingPolicy instanceof TriggeringPolicy)) {
      triggeringPolicy = ((TriggeringPolicy)policy);
    }
  }
  
  public void setTriggeringPolicy(TriggeringPolicy<E> policy)
  {
    triggeringPolicy = policy;
    if ((policy instanceof RollingPolicy)) {
      rollingPolicy = ((RollingPolicy)policy);
    }
  }
}
