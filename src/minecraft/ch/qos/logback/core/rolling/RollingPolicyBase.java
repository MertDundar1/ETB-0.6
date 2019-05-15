package ch.qos.logback.core.rolling;

import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.rolling.helper.CompressionMode;
import ch.qos.logback.core.rolling.helper.FileNamePattern;
import ch.qos.logback.core.spi.ContextAwareBase;


















public abstract class RollingPolicyBase
  extends ContextAwareBase
  implements RollingPolicy
{
  protected CompressionMode compressionMode = CompressionMode.NONE;
  

  FileNamePattern fileNamePattern;
  

  protected String fileNamePatternStr;
  
  private FileAppender parent;
  
  FileNamePattern zipEntryFileNamePattern;
  
  private boolean started;
  

  public RollingPolicyBase() {}
  

  protected void determineCompressionMode()
  {
    if (fileNamePatternStr.endsWith(".gz")) {
      addInfo("Will use gz compression");
      compressionMode = CompressionMode.GZ;
    } else if (fileNamePatternStr.endsWith(".zip")) {
      addInfo("Will use zip compression");
      compressionMode = CompressionMode.ZIP;
    } else {
      addInfo("No compression will be used");
      compressionMode = CompressionMode.NONE;
    }
  }
  
  public void setFileNamePattern(String fnp) {
    fileNamePatternStr = fnp;
  }
  
  public String getFileNamePattern() {
    return fileNamePatternStr;
  }
  
  public CompressionMode getCompressionMode() {
    return compressionMode;
  }
  
  public boolean isStarted() {
    return started;
  }
  
  public void start() {
    started = true;
  }
  
  public void stop() {
    started = false;
  }
  
  public void setParent(FileAppender appender) {
    parent = appender;
  }
  
  public boolean isParentPrudent() {
    return parent.isPrudent();
  }
  
  public String getParentsRawFileProperty() {
    return parent.rawFileProperty();
  }
}
