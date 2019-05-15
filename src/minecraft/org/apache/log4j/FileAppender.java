package org.apache.log4j;

public class FileAppender
  extends WriterAppender
{
  public FileAppender() {}
  
  public FileAppender(Layout layout, String filename) {}
  
  public FileAppender(Layout layout, String filename, boolean append) {}
  
  public FileAppender(Layout layout, String filename, boolean append, boolean bufferedIO, int bufferSize) {}
}
