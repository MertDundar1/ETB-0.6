package org.apache.log4j;

import java.io.IOException;

public class RollingFileAppender
{
  public RollingFileAppender() {}
  
  public RollingFileAppender(Layout layout, String filename)
    throws IOException
  {}
  
  public RollingFileAppender(Layout layout, String filename, boolean append)
    throws IOException
  {}
  
  public void setMaxBackupIndex(int maxBackups) {}
  
  public void setMaximumFileSize(long maxFileSize) {}
}
