package ch.qos.logback.core.rolling.helper;

import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.status.ErrorStatus;
import ch.qos.logback.core.status.WarnStatus;
import ch.qos.logback.core.util.FileUtil;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;





















public class Compressor
  extends ContextAwareBase
{
  final CompressionMode compressionMode;
  static final int BUFFER_SIZE = 8192;
  
  public Compressor(CompressionMode compressionMode)
  {
    this.compressionMode = compressionMode;
  }
  




  public void compress(String nameOfFile2Compress, String nameOfCompressedFile, String innerEntryName)
  {
    switch (1.$SwitchMap$ch$qos$logback$core$rolling$helper$CompressionMode[compressionMode.ordinal()]) {
    case 1: 
      gzCompress(nameOfFile2Compress, nameOfCompressedFile);
      break;
    case 2: 
      zipCompress(nameOfFile2Compress, nameOfCompressedFile, innerEntryName);
      break;
    case 3: 
      throw new UnsupportedOperationException("compress method called in NONE compression mode");
    }
  }
  
  private void zipCompress(String nameOfFile2zip, String nameOfZippedFile, String innerEntryName)
  {
    File file2zip = new File(nameOfFile2zip);
    
    if (!file2zip.exists()) {
      addStatus(new WarnStatus("The file to compress named [" + nameOfFile2zip + "] does not exist.", this));
      

      return;
    }
    
    if (innerEntryName == null) {
      addStatus(new WarnStatus("The innerEntryName parameter cannot be null", this));
      return;
    }
    
    if (!nameOfZippedFile.endsWith(".zip")) {
      nameOfZippedFile = nameOfZippedFile + ".zip";
    }
    
    File zippedFile = new File(nameOfZippedFile);
    
    if (zippedFile.exists()) {
      addStatus(new WarnStatus("The target compressed file named [" + nameOfZippedFile + "] exist already.", this));
      

      return;
    }
    
    addInfo("ZIP compressing [" + file2zip + "] as [" + zippedFile + "]");
    createMissingTargetDirsIfNecessary(zippedFile);
    
    BufferedInputStream bis = null;
    ZipOutputStream zos = null;
    try {
      bis = new BufferedInputStream(new FileInputStream(nameOfFile2zip));
      zos = new ZipOutputStream(new FileOutputStream(nameOfZippedFile));
      
      ZipEntry zipEntry = computeZipEntry(innerEntryName);
      zos.putNextEntry(zipEntry);
      
      byte[] inbuf = new byte[' '];
      
      int n;
      while ((n = bis.read(inbuf)) != -1) {
        zos.write(inbuf, 0, n);
      }
      
      bis.close();
      bis = null;
      zos.close();
      zos = null;
      
      if (!file2zip.delete()) {
        addStatus(new WarnStatus("Could not delete [" + nameOfFile2zip + "].", this));
      }
      return;
    } catch (Exception e) {
      addStatus(new ErrorStatus("Error occurred while compressing [" + nameOfFile2zip + "] into [" + nameOfZippedFile + "].", this, e));
    }
    finally {
      if (bis != null) {
        try {
          bis.close();
        }
        catch (IOException e) {}
      }
      
      if (zos != null) {
        try {
          zos.close();
        }
        catch (IOException e) {}
      }
    }
  }
  

















  ZipEntry computeZipEntry(File zippedFile)
  {
    return computeZipEntry(zippedFile.getName());
  }
  
  ZipEntry computeZipEntry(String filename) {
    String nameOfFileNestedWithinArchive = computeFileNameStr_WCS(filename, compressionMode);
    return new ZipEntry(nameOfFileNestedWithinArchive);
  }
  
  private void gzCompress(String nameOfFile2gz, String nameOfgzedFile)
  {
    File file2gz = new File(nameOfFile2gz);
    
    if (!file2gz.exists()) {
      addStatus(new WarnStatus("The file to compress named [" + nameOfFile2gz + "] does not exist.", this));
      

      return;
    }
    

    if (!nameOfgzedFile.endsWith(".gz")) {
      nameOfgzedFile = nameOfgzedFile + ".gz";
    }
    
    File gzedFile = new File(nameOfgzedFile);
    
    if (gzedFile.exists()) {
      addWarn("The target compressed file named [" + nameOfgzedFile + "] exist already. Aborting file compression.");
      
      return;
    }
    
    addInfo("GZ compressing [" + file2gz + "] as [" + gzedFile + "]");
    createMissingTargetDirsIfNecessary(gzedFile);
    
    BufferedInputStream bis = null;
    GZIPOutputStream gzos = null;
    try {
      bis = new BufferedInputStream(new FileInputStream(nameOfFile2gz));
      gzos = new GZIPOutputStream(new FileOutputStream(nameOfgzedFile));
      byte[] inbuf = new byte[' '];
      
      int n;
      while ((n = bis.read(inbuf)) != -1) {
        gzos.write(inbuf, 0, n);
      }
      
      bis.close();
      bis = null;
      gzos.close();
      gzos = null;
      
      if (!file2gz.delete()) {
        addStatus(new WarnStatus("Could not delete [" + nameOfFile2gz + "].", this));
      }
      return;
    } catch (Exception e) {
      addStatus(new ErrorStatus("Error occurred while compressing [" + nameOfFile2gz + "] into [" + nameOfgzedFile + "].", this, e));
    }
    finally {
      if (bis != null) {
        try {
          bis.close();
        }
        catch (IOException e) {}
      }
      
      if (gzos != null) {
        try {
          gzos.close();
        }
        catch (IOException e) {}
      }
    }
  }
  

  public static String computeFileNameStr_WCS(String fileNamePatternStr, CompressionMode compressionMode)
  {
    int len = fileNamePatternStr.length();
    switch (1.$SwitchMap$ch$qos$logback$core$rolling$helper$CompressionMode[compressionMode.ordinal()]) {
    case 1: 
      if (fileNamePatternStr.endsWith(".gz")) {
        return fileNamePatternStr.substring(0, len - 3);
      }
      return fileNamePatternStr;
    case 2: 
      if (fileNamePatternStr.endsWith(".zip")) {
        return fileNamePatternStr.substring(0, len - 4);
      }
      return fileNamePatternStr;
    case 3: 
      return fileNamePatternStr;
    }
    throw new IllegalStateException("Execution should not reach this point");
  }
  
  void createMissingTargetDirsIfNecessary(File file)
  {
    boolean result = FileUtil.createMissingParentDirectories(file);
    if (!result) {
      addError("Failed to create parent directories for [" + file.getAbsolutePath() + "]");
    }
  }
  

  public String toString()
  {
    return getClass().getName();
  }
}
