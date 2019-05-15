package ch.qos.logback.core.rolling.helper;

import ch.qos.logback.core.pattern.Converter;
import ch.qos.logback.core.pattern.LiteralConverter;
import ch.qos.logback.core.spi.ContextAwareBase;
import java.io.File;
import java.util.Date;

















public abstract class DefaultArchiveRemover
  extends ContextAwareBase
  implements ArchiveRemover
{
  protected static final long UNINITIALIZED = -1L;
  protected static final long INACTIVITY_TOLERANCE_IN_MILLIS = 5529600000L;
  static final int MAX_VALUE_FOR_INACTIVITY_PERIODS = 336;
  final FileNamePattern fileNamePattern;
  final RollingCalendar rc;
  int periodOffsetForDeletionTarget;
  final boolean parentClean;
  long lastHeartBeat = -1L;
  
  public DefaultArchiveRemover(FileNamePattern fileNamePattern, RollingCalendar rc)
  {
    this.fileNamePattern = fileNamePattern;
    this.rc = rc;
    parentClean = computeParentCleaningFlag(fileNamePattern);
  }
  
  int computeElapsedPeriodsSinceLastClean(long nowInMillis)
  {
    long periodsElapsed = 0L;
    if (lastHeartBeat == -1L) {
      addInfo("first clean up after appender initialization");
      periodsElapsed = rc.periodsElapsed(nowInMillis, nowInMillis + 5529600000L);
      if (periodsElapsed > 336L)
        periodsElapsed = 336L;
    } else {
      periodsElapsed = rc.periodsElapsed(lastHeartBeat, nowInMillis);
      if (periodsElapsed < 1L) {
        addWarn("Unexpected periodsElapsed value " + periodsElapsed);
        periodsElapsed = 1L;
      }
    }
    return (int)periodsElapsed;
  }
  
  public void clean(Date now) {
    long nowInMillis = now.getTime();
    int periodsElapsed = computeElapsedPeriodsSinceLastClean(nowInMillis);
    lastHeartBeat = nowInMillis;
    if (periodsElapsed > 1) {
      addInfo("periodsElapsed = " + periodsElapsed);
    }
    for (int i = 0; i < periodsElapsed; i++) {
      cleanByPeriodOffset(now, periodOffsetForDeletionTarget - i);
    }
  }
  
  abstract void cleanByPeriodOffset(Date paramDate, int paramInt);
  
  boolean computeParentCleaningFlag(FileNamePattern fileNamePattern) {
    DateTokenConverter dtc = fileNamePattern.getPrimaryDateTokenConverter();
    
    if (dtc.getDatePattern().indexOf('/') != -1) {
      return true;
    }
    


    Converter<Object> p = headTokenConverter;
    

    while ((p != null) && 
      (!(p instanceof DateTokenConverter)))
    {

      p = p.getNext();
    }
    
    while (p != null) {
      if ((p instanceof LiteralConverter)) {
        String s = p.convert(null);
        if (s.indexOf('/') != -1) {
          return true;
        }
      }
      p = p.getNext();
    }
    

    return false;
  }
  
  void removeFolderIfEmpty(File dir) {
    removeFolderIfEmpty(dir, 0);
  }
  








  private void removeFolderIfEmpty(File dir, int depth)
  {
    if (depth >= 3) {
      return;
    }
    if ((dir.isDirectory()) && (FileFilterUtil.isEmptyDirectory(dir))) {
      addInfo("deleting folder [" + dir + "]");
      dir.delete();
      removeFolderIfEmpty(dir.getParentFile(), depth + 1);
    }
  }
  
  public void setMaxHistory(int maxHistory) {
    periodOffsetForDeletionTarget = (-maxHistory - 1);
  }
}
