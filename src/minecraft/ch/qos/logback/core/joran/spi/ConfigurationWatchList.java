package ch.qos.logback.core.joran.spi;

import ch.qos.logback.core.spi.ContextAwareBase;
import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;















public class ConfigurationWatchList
  extends ContextAwareBase
{
  URL mainURL;
  
  public ConfigurationWatchList() {}
  
  List<File> fileWatchList = new ArrayList();
  List<Long> lastModifiedList = new ArrayList();
  
  public void clear() {
    mainURL = null;
    lastModifiedList.clear();
    fileWatchList.clear();
  }
  




  public void setMainURL(URL mainURL)
  {
    this.mainURL = mainURL;
    if (mainURL != null)
      addAsFileToWatch(mainURL);
  }
  
  private void addAsFileToWatch(URL url) {
    File file = convertToFile(url);
    if (file != null) {
      fileWatchList.add(file);
      lastModifiedList.add(Long.valueOf(file.lastModified()));
    }
  }
  
  public void addToWatchList(URL url) {
    addAsFileToWatch(url);
  }
  
  public URL getMainURL() {
    return mainURL;
  }
  
  public List<File> getCopyOfFileWatchList() {
    return new ArrayList(fileWatchList);
  }
  
  public boolean changeDetected() {
    int len = fileWatchList.size();
    for (int i = 0; i < len; i++) {
      long lastModified = ((Long)lastModifiedList.get(i)).longValue();
      File file = (File)fileWatchList.get(i);
      if (lastModified != file.lastModified()) {
        return true;
      }
    }
    return false;
  }
  

  File convertToFile(URL url)
  {
    String protocol = url.getProtocol();
    if ("file".equals(protocol)) {
      return new File(URLDecoder.decode(url.getFile()));
    }
    addInfo("URL [" + url + "] is not of type file");
    return null;
  }
}
