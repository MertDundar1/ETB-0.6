package ch.qos.logback.core.rolling;

import ch.qos.logback.core.rolling.helper.CompressionMode;
import ch.qos.logback.core.rolling.helper.Compressor;
import ch.qos.logback.core.rolling.helper.FileFilterUtil;
import ch.qos.logback.core.rolling.helper.FileNamePattern;
import ch.qos.logback.core.rolling.helper.IntegerTokenConverter;
import ch.qos.logback.core.rolling.helper.RenameUtil;
import java.io.File;
import java.util.Date;


















public class FixedWindowRollingPolicy
  extends RollingPolicyBase
{
  static final String FNP_NOT_SET = "The \"FileNamePattern\" property must be set before using FixedWindowRollingPolicy. ";
  static final String PRUDENT_MODE_UNSUPPORTED = "See also http://logback.qos.ch/codes.html#tbr_fnp_prudent_unsupported";
  static final String SEE_PARENT_FN_NOT_SET = "Please refer to http://logback.qos.ch/codes.html#fwrp_parentFileName_not_set";
  int maxIndex;
  int minIndex;
  RenameUtil util = new RenameUtil();
  

  Compressor compressor;
  

  public static final String ZIP_ENTRY_DATE_PATTERN = "yyyy-MM-dd_HHmm";
  
  private static int MAX_WINDOW_SIZE = 20;
  
  public FixedWindowRollingPolicy() {
    minIndex = 1;
    maxIndex = 7;
  }
  
  public void start() {
    util.setContext(context);
    
    if (fileNamePatternStr != null) {
      fileNamePattern = new FileNamePattern(fileNamePatternStr, context);
      determineCompressionMode();
    } else {
      addError("The \"FileNamePattern\" property must be set before using FixedWindowRollingPolicy. ");
      addError("See also http://logback.qos.ch/codes.html#tbr_fnp_not_set");
      throw new IllegalStateException("The \"FileNamePattern\" property must be set before using FixedWindowRollingPolicy. See also http://logback.qos.ch/codes.html#tbr_fnp_not_set");
    }
    
    if (isParentPrudent()) {
      addError("Prudent mode is not supported with FixedWindowRollingPolicy.");
      addError("See also http://logback.qos.ch/codes.html#tbr_fnp_prudent_unsupported");
      throw new IllegalStateException("Prudent mode is not supported.");
    }
    
    if (getParentsRawFileProperty() == null) {
      addError("The File name property must be set before using this rolling policy.");
      addError("Please refer to http://logback.qos.ch/codes.html#fwrp_parentFileName_not_set");
      throw new IllegalStateException("The \"File\" option must be set.");
    }
    
    if (maxIndex < minIndex) {
      addWarn("MaxIndex (" + maxIndex + ") cannot be smaller than MinIndex (" + minIndex + ").");
      
      addWarn("Setting maxIndex to equal minIndex.");
      maxIndex = minIndex;
    }
    
    int maxWindowSize = getMaxWindowSize();
    if (maxIndex - minIndex > maxWindowSize) {
      addWarn("Large window sizes are not allowed.");
      maxIndex = (minIndex + maxWindowSize);
      addWarn("MaxIndex reduced to " + maxIndex);
    }
    
    IntegerTokenConverter itc = fileNamePattern.getIntegerTokenConverter();
    
    if (itc == null) {
      throw new IllegalStateException("FileNamePattern [" + fileNamePattern.getPattern() + "] does not contain a valid IntegerToken");
    }
    


    if (compressionMode == CompressionMode.ZIP) {
      String zipEntryFileNamePatternStr = transformFileNamePatternFromInt2Date(fileNamePatternStr);
      zipEntryFileNamePattern = new FileNamePattern(zipEntryFileNamePatternStr, context);
    }
    compressor = new Compressor(compressionMode);
    compressor.setContext(context);
    super.start();
  }
  




  protected int getMaxWindowSize()
  {
    return MAX_WINDOW_SIZE;
  }
  
  private String transformFileNamePatternFromInt2Date(String fileNamePatternStr) {
    String slashified = FileFilterUtil.slashify(fileNamePatternStr);
    String stemOfFileNamePattern = FileFilterUtil.afterLastSlash(slashified);
    return stemOfFileNamePattern.replace("%i", "%d{yyyy-MM-dd_HHmm}");
  }
  


  public void rollover()
    throws RolloverFailure
  {
    if (maxIndex >= 0)
    {
      File file = new File(fileNamePattern.convertInt(maxIndex));
      
      if (file.exists()) {
        file.delete();
      }
      

      for (int i = maxIndex - 1; i >= minIndex; i--) {
        String toRenameStr = fileNamePattern.convertInt(i);
        File toRename = new File(toRenameStr);
        
        if (toRename.exists()) {
          util.rename(toRenameStr, fileNamePattern.convertInt(i + 1));
        } else {
          addInfo("Skipping roll-over for inexistent file " + toRenameStr);
        }
      }
      

      switch (1.$SwitchMap$ch$qos$logback$core$rolling$helper$CompressionMode[compressionMode.ordinal()]) {
      case 1: 
        util.rename(getActiveFileName(), fileNamePattern.convertInt(minIndex));
        
        break;
      case 2: 
        compressor.compress(getActiveFileName(), fileNamePattern.convertInt(minIndex), null);
        break;
      case 3: 
        compressor.compress(getActiveFileName(), fileNamePattern.convertInt(minIndex), zipEntryFileNamePattern.convert(new Date()));
      }
      
    }
  }
  


  public String getActiveFileName()
  {
    return getParentsRawFileProperty();
  }
  
  public int getMaxIndex() {
    return maxIndex;
  }
  
  public int getMinIndex() {
    return minIndex;
  }
  
  public void setMaxIndex(int maxIndex) {
    this.maxIndex = maxIndex;
  }
  
  public void setMinIndex(int minIndex) {
    this.minIndex = minIndex;
  }
}
