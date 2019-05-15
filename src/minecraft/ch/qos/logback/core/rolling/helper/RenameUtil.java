package ch.qos.logback.core.rolling.helper;

import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.RolloverFailure;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.util.EnvUtil;
import ch.qos.logback.core.util.FileUtil;
import java.io.File;



























public class RenameUtil
  extends ContextAwareBase
{
  static String RENAMING_ERROR_URL = "http://logback.qos.ch/codes.html#renamingError";
  



  public RenameUtil() {}
  


  public void rename(String src, String target)
    throws RolloverFailure
  {
    if (src.equals(target)) {
      addWarn("Source and target files are the same [" + src + "]. Skipping.");
      return;
    }
    File srcFile = new File(src);
    
    if (srcFile.exists()) {
      File targetFile = new File(target);
      createMissingTargetDirsIfNecessary(targetFile);
      
      addInfo("Renaming file [" + srcFile + "] to [" + targetFile + "]");
      
      boolean result = srcFile.renameTo(targetFile);
      
      if (!result) {
        addWarn("Failed to rename file [" + srcFile + "] as [" + targetFile + "].");
        if (areOnDifferentVolumes(srcFile, targetFile)) {
          addWarn("Detected different file systems for source [" + src + "] and target [" + target + "]. Attempting rename by copying.");
          renameByCopying(src, target);
          return;
        }
        addWarn("Please consider leaving the [file] option of " + RollingFileAppender.class.getSimpleName() + " empty.");
        addWarn("See also " + RENAMING_ERROR_URL);
      }
    }
    else {
      throw new RolloverFailure("File [" + src + "] does not exist.");
    }
  }
  







  boolean areOnDifferentVolumes(File srcFile, File targetFile)
    throws RolloverFailure
  {
    if (!EnvUtil.isJDK7OrHigher()) {
      return false;
    }
    File parentOfTarget = targetFile.getParentFile();
    try
    {
      boolean onSameFileStore = FileStoreUtil.areOnSameFileStore(srcFile, parentOfTarget);
      return !onSameFileStore;
    } catch (RolloverFailure rf) {
      addWarn("Error while checking file store equality", rf); }
    return false;
  }
  




  public void renameByCopying(String src, String target)
    throws RolloverFailure
  {
    FileUtil fileUtil = new FileUtil(getContext());
    fileUtil.copy(src, target);
    
    File srcFile = new File(src);
    if (!srcFile.delete()) {
      addWarn("Could not delete " + src);
    }
  }
  
  void createMissingTargetDirsIfNecessary(File toFile) throws RolloverFailure
  {
    boolean result = FileUtil.createMissingParentDirectories(toFile);
    if (!result) {
      throw new RolloverFailure("Failed to create parent directories for [" + toFile.getAbsolutePath() + "]");
    }
  }
  

  public String toString()
  {
    return "c.q.l.co.rolling.helper.RenameUtil";
  }
}
