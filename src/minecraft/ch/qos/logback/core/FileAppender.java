package ch.qos.logback.core;

import ch.qos.logback.core.recovery.ResilientFileOutputStream;
import ch.qos.logback.core.util.FileUtil;
import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.concurrent.locks.ReentrantLock;


























public class FileAppender<E>
  extends OutputStreamAppender<E>
{
  protected boolean append = true;
  



  protected String fileName = null;
  
  private boolean prudent = false;
  

  public FileAppender() {}
  
  public void setFile(String file)
  {
    if (file == null) {
      fileName = file;
    }
    else
    {
      fileName = file.trim();
    }
  }
  


  public boolean isAppend()
  {
    return append;
  }
  





  public final String rawFileProperty()
  {
    return fileName;
  }
  






  public String getFile()
  {
    return fileName;
  }
  




  public void start()
  {
    int errors = 0;
    if (getFile() != null) {
      addInfo("File property is set to [" + fileName + "]");
      
      if ((prudent) && 
        (!isAppend())) {
        setAppend(true);
        addWarn("Setting \"Append\" property to true on account of \"Prudent\" mode");
      }
      
      try
      {
        openFile(getFile());
      } catch (IOException e) {
        errors++;
        addError("openFile(" + fileName + "," + append + ") call failed.", e);
      }
    } else {
      errors++;
      addError("\"File\" property not set for appender named [" + name + "].");
    }
    if (errors == 0) {
      super.start();
    }
  }
  














  public void openFile(String file_name)
    throws IOException
  {
    lock.lock();
    try {
      File file = new File(file_name);
      boolean result = FileUtil.createMissingParentDirectories(file);
      if (!result) {
        addError("Failed to create parent directories for [" + file.getAbsolutePath() + "]");
      }
      

      ResilientFileOutputStream resilientFos = new ResilientFileOutputStream(file, append);
      
      resilientFos.setContext(context);
      setOutputStream(resilientFos);
    } finally {
      lock.unlock();
    }
  }
  




  public boolean isPrudent()
  {
    return prudent;
  }
  





  public void setPrudent(boolean prudent)
  {
    this.prudent = prudent;
  }
  
  public void setAppend(boolean append) {
    this.append = append;
  }
  
  private void safeWrite(E event) throws IOException {
    ResilientFileOutputStream resilientFOS = (ResilientFileOutputStream)getOutputStream();
    FileChannel fileChannel = resilientFOS.getChannel();
    if (fileChannel == null) {
      return;
    }
    

    boolean interrupted = Thread.interrupted();
    
    FileLock fileLock = null;
    try {
      fileLock = fileChannel.lock();
      long position = fileChannel.position();
      long size = fileChannel.size();
      if (size != position) {
        fileChannel.position(size);
      }
      super.writeOut(event);
    }
    catch (IOException e) {
      resilientFOS.postIOFailure(e);
    }
    finally {
      if ((fileLock != null) && (fileLock.isValid())) {
        fileLock.release();
      }
      

      if (interrupted) {
        Thread.currentThread().interrupt();
      }
    }
  }
  
  protected void writeOut(E event) throws IOException
  {
    if (prudent) {
      safeWrite(event);
    } else {
      super.writeOut(event);
    }
  }
}
