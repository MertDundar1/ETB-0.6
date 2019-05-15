package ch.qos.logback.core.rolling;

import ch.qos.logback.core.joran.spi.NoAutoStart;
import ch.qos.logback.core.rolling.helper.ArchiveRemover;
import ch.qos.logback.core.rolling.helper.CompressionMode;
import ch.qos.logback.core.rolling.helper.FileFilterUtil;
import ch.qos.logback.core.rolling.helper.FileNamePattern;
import ch.qos.logback.core.rolling.helper.SizeAndTimeBasedArchiveRemover;
import ch.qos.logback.core.util.FileSize;
import java.io.File;
import java.util.Date;














@NoAutoStart
public class SizeAndTimeBasedFNATP<E>
  extends TimeBasedFileNamingAndTriggeringPolicyBase<E>
{
  int currentPeriodsCounter = 0;
  FileSize maxFileSize;
  String maxFileSizeAsString;
  private int invocationCounter;
  
  public SizeAndTimeBasedFNATP() {}
  
  public void start() {
    super.start();
    
    archiveRemover = createArchiveRemover();
    archiveRemover.setContext(context);
    



    String regex = tbrp.fileNamePattern.toRegexForFixedDate(dateInCurrentPeriod);
    String stemRegex = FileFilterUtil.afterLastSlash(regex);
    

    computeCurrentPeriodsHighestCounterValue(stemRegex);
    
    started = true;
  }
  
  protected ArchiveRemover createArchiveRemover() {
    return new SizeAndTimeBasedArchiveRemover(tbrp.fileNamePattern, rc);
  }
  
  void computeCurrentPeriodsHighestCounterValue(String stemRegex) {
    File file = new File(getCurrentPeriodsFileNameWithoutCompressionSuffix());
    File parentDir = file.getParentFile();
    
    File[] matchingFileArray = FileFilterUtil.filesInFolderMatchingStemRegex(parentDir, stemRegex);
    

    if ((matchingFileArray == null) || (matchingFileArray.length == 0)) {
      currentPeriodsCounter = 0;
      return;
    }
    currentPeriodsCounter = FileFilterUtil.findHighestCounter(matchingFileArray, stemRegex);
    


    if ((tbrp.getParentsRawFileProperty() != null) || (tbrp.compressionMode != CompressionMode.NONE))
    {
      currentPeriodsCounter += 1;
    }
  }
  





  private int invocationMask = 1;
  
  public boolean isTriggeringEvent(File activeFile, E event)
  {
    long time = getCurrentTime();
    if (time >= nextCheck) {
      Date dateInElapsedPeriod = dateInCurrentPeriod;
      elapsedPeriodsFileName = tbrp.fileNamePatternWCS.convertMultipleArguments(new Object[] { dateInElapsedPeriod, Integer.valueOf(currentPeriodsCounter) });
      
      currentPeriodsCounter = 0;
      setDateInCurrentPeriod(time);
      computeNextCheck();
      return true;
    }
    

    if ((++invocationCounter & invocationMask) != invocationMask) {
      return false;
    }
    if (invocationMask < 15) {
      invocationMask = ((invocationMask << 1) + 1);
    }
    
    if (activeFile.length() >= maxFileSize.getSize()) {
      elapsedPeriodsFileName = tbrp.fileNamePatternWCS.convertMultipleArguments(new Object[] { dateInCurrentPeriod, Integer.valueOf(currentPeriodsCounter) });
      
      currentPeriodsCounter += 1;
      return true;
    }
    
    return false;
  }
  
  private String getFileNameIncludingCompressionSuffix(Date date, int counter) {
    return tbrp.fileNamePattern.convertMultipleArguments(new Object[] { dateInCurrentPeriod, Integer.valueOf(counter) });
  }
  


  public String getCurrentPeriodsFileNameWithoutCompressionSuffix()
  {
    return tbrp.fileNamePatternWCS.convertMultipleArguments(new Object[] { dateInCurrentPeriod, Integer.valueOf(currentPeriodsCounter) });
  }
  
  public String getMaxFileSize()
  {
    return maxFileSizeAsString;
  }
  
  public void setMaxFileSize(String maxFileSize) {
    maxFileSizeAsString = maxFileSize;
    this.maxFileSize = FileSize.valueOf(maxFileSize);
  }
}
