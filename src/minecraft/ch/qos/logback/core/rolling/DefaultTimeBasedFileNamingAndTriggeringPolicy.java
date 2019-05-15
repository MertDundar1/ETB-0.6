package ch.qos.logback.core.rolling;

import ch.qos.logback.core.rolling.helper.ArchiveRemover;
import ch.qos.logback.core.rolling.helper.FileNamePattern;
import ch.qos.logback.core.rolling.helper.TimeBasedArchiveRemover;
import java.io.File;
import java.util.Date;

















public class DefaultTimeBasedFileNamingAndTriggeringPolicy<E>
  extends TimeBasedFileNamingAndTriggeringPolicyBase<E>
{
  public DefaultTimeBasedFileNamingAndTriggeringPolicy() {}
  
  public void start()
  {
    super.start();
    archiveRemover = new TimeBasedArchiveRemover(tbrp.fileNamePattern, rc);
    archiveRemover.setContext(context);
    started = true;
  }
  
  public boolean isTriggeringEvent(File activeFile, E event) {
    long time = getCurrentTime();
    if (time >= nextCheck) {
      Date dateOfElapsedPeriod = dateInCurrentPeriod;
      addInfo("Elapsed period: " + dateOfElapsedPeriod);
      elapsedPeriodsFileName = tbrp.fileNamePatternWCS.convert(dateOfElapsedPeriod);
      
      setDateInCurrentPeriod(time);
      computeNextCheck();
      return true;
    }
    return false;
  }
  

  public String toString()
  {
    return "c.q.l.core.rolling.DefaultTimeBasedFileNamingAndTriggeringPolicy";
  }
}
