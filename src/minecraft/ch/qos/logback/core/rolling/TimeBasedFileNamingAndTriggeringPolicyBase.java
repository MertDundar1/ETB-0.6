package ch.qos.logback.core.rolling;

import ch.qos.logback.core.rolling.helper.ArchiveRemover;
import ch.qos.logback.core.rolling.helper.DateTokenConverter;
import ch.qos.logback.core.rolling.helper.FileNamePattern;
import ch.qos.logback.core.rolling.helper.RollingCalendar;
import ch.qos.logback.core.spi.ContextAwareBase;
import java.io.File;
import java.util.Date;











public abstract class TimeBasedFileNamingAndTriggeringPolicyBase<E>
  extends ContextAwareBase
  implements TimeBasedFileNamingAndTriggeringPolicy<E>
{
  protected TimeBasedRollingPolicy<E> tbrp;
  
  public TimeBasedFileNamingAndTriggeringPolicyBase() {}
  
  protected ArchiveRemover archiveRemover = null;
  protected String elapsedPeriodsFileName;
  protected RollingCalendar rc;
  
  protected long artificialCurrentTime = -1L;
  protected Date dateInCurrentPeriod = null;
  protected long nextCheck;
  
  protected boolean started = false;
  
  public boolean isStarted() {
    return started;
  }
  
  public void start() {
    DateTokenConverter dtc = tbrp.fileNamePattern.getPrimaryDateTokenConverter();
    if (dtc == null) {
      throw new IllegalStateException("FileNamePattern [" + tbrp.fileNamePattern.getPattern() + "] does not contain a valid DateToken");
    }
    


    rc = new RollingCalendar();
    rc.init(dtc.getDatePattern());
    addInfo("The date pattern is '" + dtc.getDatePattern() + "' from file name pattern '" + tbrp.fileNamePattern.getPattern() + "'.");
    

    rc.printPeriodicity(this);
    
    setDateInCurrentPeriod(new Date(getCurrentTime()));
    if (tbrp.getParentsRawFileProperty() != null) {
      File currentFile = new File(tbrp.getParentsRawFileProperty());
      if ((currentFile.exists()) && (currentFile.canRead())) {
        setDateInCurrentPeriod(new Date(currentFile.lastModified()));
      }
    }
    
    addInfo("Setting initial period to " + dateInCurrentPeriod);
    computeNextCheck();
  }
  
  public void stop() {
    started = false;
  }
  
  protected void computeNextCheck() {
    nextCheck = rc.getNextTriggeringMillis(dateInCurrentPeriod);
  }
  
  protected void setDateInCurrentPeriod(long now) {
    dateInCurrentPeriod.setTime(now);
  }
  

  public void setDateInCurrentPeriod(Date _dateInCurrentPeriod)
  {
    dateInCurrentPeriod = _dateInCurrentPeriod;
  }
  
  public String getElapsedPeriodsFileName() {
    return elapsedPeriodsFileName;
  }
  
  public String getCurrentPeriodsFileNameWithoutCompressionSuffix() {
    return tbrp.fileNamePatternWCS.convert(dateInCurrentPeriod);
  }
  
  public void setCurrentTime(long timeInMillis) {
    artificialCurrentTime = timeInMillis;
  }
  
  public long getCurrentTime()
  {
    if (artificialCurrentTime >= 0L) {
      return artificialCurrentTime;
    }
    return System.currentTimeMillis();
  }
  
  public void setTimeBasedRollingPolicy(TimeBasedRollingPolicy<E> _tbrp)
  {
    tbrp = _tbrp;
  }
  
  public ArchiveRemover getArchiveRemover()
  {
    return archiveRemover;
  }
}
