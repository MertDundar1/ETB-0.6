package ch.qos.logback.core.util;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.rolling.RolloverFailure;
import ch.qos.logback.core.spi.ContextAwareBase;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;




public class FileUtil
  extends ContextAwareBase
{
  static final int BUF_SIZE = 32768;
  
  public FileUtil(Context context)
  {
    setContext(context);
  }
  
  public static URL fileToURL(File file) {
    try {
      return file.toURI().toURL();
    } catch (MalformedURLException e) {
      throw new RuntimeException("Unexpected exception on file [" + file + "]", e);
    }
  }
  








  public static boolean createMissingParentDirectories(File file)
  {
    File parent = file.getParentFile();
    if (parent == null)
    {

      return true;
    }
    


    parent.mkdirs();
    return parent.exists();
  }
  
  public String resourceAsString(ClassLoader classLoader, String resourceName)
  {
    URL url = classLoader.getResource(resourceName);
    if (url == null) {
      addError("Failed to find resource [" + resourceName + "]");
      return null;
    }
    
    InputStreamReader isr = null;
    try {
      URLConnection urlConnection = url.openConnection();
      urlConnection.setUseCaches(false);
      isr = new InputStreamReader(urlConnection.getInputStream());
      char[] buf = new char[''];
      StringBuilder builder = new StringBuilder();
      int count = -1;
      while ((count = isr.read(buf, 0, buf.length)) != -1) {
        builder.append(buf, 0, count);
      }
      return builder.toString();
    } catch (IOException e) {
      addError("Failed to open " + resourceName, e);
    } finally {
      if (isr != null) {
        try {
          isr.close();
        }
        catch (IOException e) {}
      }
    }
    
    return null;
  }
  
  public void copy(String src, String destination)
    throws RolloverFailure
  {
    BufferedInputStream bis = null;
    BufferedOutputStream bos = null;
    try {
      bis = new BufferedInputStream(new FileInputStream(src));
      bos = new BufferedOutputStream(new FileOutputStream(destination));
      byte[] inbuf = new byte[32768];
      
      int n;
      while ((n = bis.read(inbuf)) != -1) {
        bos.write(inbuf, 0, n);
      }
      
      bis.close();
      bis = null;
      bos.close();
      bos = null;
      String msg;
      return; } catch (IOException ioe) { msg = "Failed to copy [" + src + "] to [" + destination + "]";
      addError(msg, ioe);
      throw new RolloverFailure(msg);
    } finally {
      if (bis != null) {
        try {
          bis.close();
        }
        catch (IOException e) {}
      }
      
      if (bos != null) {
        try {
          bos.close();
        }
        catch (IOException e) {}
      }
    }
  }
}
