package ch.qos.logback.core.recovery;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;








public class ResilientFileOutputStream
  extends ResilientOutputStreamBase
{
  private File file;
  private FileOutputStream fos;
  
  public ResilientFileOutputStream(File file, boolean append)
    throws FileNotFoundException
  {
    this.file = file;
    fos = new FileOutputStream(file, append);
    os = new BufferedOutputStream(fos);
    presumedClean = true;
  }
  
  public FileChannel getChannel() {
    if (os == null) {
      return null;
    }
    return fos.getChannel();
  }
  
  public File getFile() {
    return file;
  }
  
  String getDescription()
  {
    return "file [" + file + "]";
  }
  
  OutputStream openNewOutputStream()
    throws IOException
  {
    fos = new FileOutputStream(file, true);
    return new BufferedOutputStream(fos);
  }
  
  public String toString()
  {
    return "c.q.l.c.recovery.ResilientFileOutputStream@" + System.identityHashCode(this);
  }
}
