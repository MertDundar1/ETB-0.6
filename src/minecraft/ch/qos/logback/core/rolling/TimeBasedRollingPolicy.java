package ch.qos.logback.core.rolling;

import ch.qos.logback.core.rolling.helper.ArchiveRemover;
import ch.qos.logback.core.rolling.helper.AsynchronousCompressor;
import ch.qos.logback.core.rolling.helper.CompressionMode;
import ch.qos.logback.core.rolling.helper.Compressor;
import ch.qos.logback.core.rolling.helper.FileFilterUtil;
import ch.qos.logback.core.rolling.helper.FileNamePattern;
import ch.qos.logback.core.rolling.helper.RenameUtil;
import java.io.File;
import java.util.Date;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;






















public class TimeBasedRollingPolicy<E>
  extends RollingPolicyBase
  implements TriggeringPolicy<E>
{
  static final String FNP_NOT_SET = "The FileNamePattern option must be set before using TimeBasedRollingPolicy. ";
  static final int INFINITE_HISTORY = 0;
  FileNamePattern fileNamePatternWCS;
  private Compressor compressor;
  private RenameUtil renameUtil = new RenameUtil();
  
  Future<?> future;
  private int maxHistory = 0;
  
  private ArchiveRemover archiveRemover;
  
  TimeBasedFileNamingAndTriggeringPolicy<E> timeBasedFileNamingAndTriggeringPolicy;
  boolean cleanHistoryOnStart = false;
  
  public TimeBasedRollingPolicy() {}
  
  public void start() { renameUtil.setContext(context);
    

    if (fileNamePatternStr != null) {
      fileNamePattern = new FileNamePattern(fileNamePatternStr, context);
      determineCompressionMode();
    } else {
      addWarn("The FileNamePattern option must be set before using TimeBasedRollingPolicy. ");
      addWarn("See also http://logback.qos.ch/codes.html#tbr_fnp_not_set");
      throw new IllegalStateException("The FileNamePattern option must be set before using TimeBasedRollingPolicy. See also http://logback.qos.ch/codes.html#tbr_fnp_not_set");
    }
    

    compressor = new Compressor(compressionMode);
    compressor.setContext(context);
    

    fileNamePatternWCS = new FileNamePattern(Compressor.computeFileNameStr_WCS(fileNamePatternStr, compressionMode), context);
    

    addInfo("Will use the pattern " + fileNamePatternWCS + " for the active file");
    

    if (compressionMode == CompressionMode.ZIP) {
      String zipEntryFileNamePatternStr = transformFileNamePattern2ZipEntry(fileNamePatternStr);
      zipEntryFileNamePattern = new FileNamePattern(zipEntryFileNamePatternStr, context);
    }
    
    if (timeBasedFileNamingAndTriggeringPolicy == null) {
      timeBasedFileNamingAndTriggeringPolicy = new DefaultTimeBasedFileNamingAndTriggeringPolicy();
    }
    timeBasedFileNamingAndTriggeringPolicy.setContext(context);
    timeBasedFileNamingAndTriggeringPolicy.setTimeBasedRollingPolicy(this);
    timeBasedFileNamingAndTriggeringPolicy.start();
    



    if (maxHistory != 0) {
      archiveRemover = timeBasedFileNamingAndTriggeringPolicy.getArchiveRemover();
      archiveRemover.setMaxHistory(maxHistory);
      if (cleanHistoryOnStart) {
        addInfo("Cleaning on start up");
        archiveRemover.clean(new Date(timeBasedFileNamingAndTriggeringPolicy.getCurrentTime()));
      }
    }
    
    super.start();
  }
  
  public void stop()
  {
    if (!isStarted())
      return;
    waitForAsynchronousJobToStop();
    super.stop();
  }
  
  private void waitForAsynchronousJobToStop()
  {
    if (future != null)
      try {
        future.get(30L, TimeUnit.SECONDS);
      } catch (TimeoutException e) {
        addError("Timeout while waiting for compression job to finish", e);
      } catch (Exception e) {
        addError("Unexpected exception while waiting for compression job to finish", e);
      }
  }
  
  private String transformFileNamePattern2ZipEntry(String fileNamePatternStr) {
    String slashified = FileFilterUtil.slashify(fileNamePatternStr);
    return FileFilterUtil.afterLastSlash(slashified);
  }
  
  public void setTimeBasedFileNamingAndTriggeringPolicy(TimeBasedFileNamingAndTriggeringPolicy<E> timeBasedTriggering)
  {
    timeBasedFileNamingAndTriggeringPolicy = timeBasedTriggering;
  }
  
  public TimeBasedFileNamingAndTriggeringPolicy<E> getTimeBasedFileNamingAndTriggeringPolicy() {
    return timeBasedFileNamingAndTriggeringPolicy;
  }
  


  public void rollover()
    throws RolloverFailure
  {
    String elapsedPeriodsFileName = timeBasedFileNamingAndTriggeringPolicy.getElapsedPeriodsFileName();
    

    String elapsedPeriodStem = FileFilterUtil.afterLastSlash(elapsedPeriodsFileName);
    
    if (compressionMode == CompressionMode.NONE) {
      if (getParentsRawFileProperty() != null) {
        renameUtil.rename(getParentsRawFileProperty(), elapsedPeriodsFileName);
      }
    }
    else if (getParentsRawFileProperty() == null) {
      future = asyncCompress(elapsedPeriodsFileName, elapsedPeriodsFileName, elapsedPeriodStem);
    } else {
      future = renamedRawAndAsyncCompress(elapsedPeriodsFileName, elapsedPeriodStem);
    }
    

    if (archiveRemover != null) {
      archiveRemover.clean(new Date(timeBasedFileNamingAndTriggeringPolicy.getCurrentTime()));
    }
  }
  
  Future asyncCompress(String nameOfFile2Compress, String nameOfCompressedFile, String innerEntryName) throws RolloverFailure
  {
    AsynchronousCompressor ac = new AsynchronousCompressor(compressor);
    return ac.compressAsynchronously(nameOfFile2Compress, nameOfCompressedFile, innerEntryName);
  }
  
  Future renamedRawAndAsyncCompress(String nameOfCompressedFile, String innerEntryName) throws RolloverFailure
  {
    String parentsRawFile = getParentsRawFileProperty();
    String tmpTarget = parentsRawFile + System.nanoTime() + ".tmp";
    renameUtil.rename(parentsRawFile, tmpTarget);
    return asyncCompress(tmpTarget, nameOfCompressedFile, innerEntryName);
  }
  



















  public String getActiveFileName()
  {
    String parentsRawFileProperty = getParentsRawFileProperty();
    if (parentsRawFileProperty != null) {
      return parentsRawFileProperty;
    }
    return timeBasedFileNamingAndTriggeringPolicy.getCurrentPeriodsFileNameWithoutCompressionSuffix();
  }
  

  public boolean isTriggeringEvent(File activeFile, E event)
  {
    return timeBasedFileNamingAndTriggeringPolicy.isTriggeringEvent(activeFile, event);
  }
  




  public int getMaxHistory()
  {
    return maxHistory;
  }
  





  public void setMaxHistory(int maxHistory)
  {
    this.maxHistory = maxHistory;
  }
  
  public boolean isCleanHistoryOnStart()
  {
    return cleanHistoryOnStart;
  }
  




  public void setCleanHistoryOnStart(boolean cleanHistoryOnStart)
  {
    this.cleanHistoryOnStart = cleanHistoryOnStart;
  }
  

  public String toString()
  {
    return "c.q.l.core.rolling.TimeBasedRollingPolicy";
  }
}
