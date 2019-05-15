package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.event.SaxEvent;
import ch.qos.logback.core.joran.event.SaxEventRecorder;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.EventPlayer;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.joran.spi.Interpreter;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.joran.util.ConfigurationWatchListUtil;
import ch.qos.logback.core.util.Loader;
import ch.qos.logback.core.util.OptionHelper;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import org.xml.sax.Attributes;













public class IncludeAction
  extends Action
{
  private static final String INCLUDED_TAG = "included";
  private static final String FILE_ATTR = "file";
  private static final String URL_ATTR = "url";
  private static final String RESOURCE_ATTR = "resource";
  private static final String OPTIONAL_ATTR = "optional";
  private String attributeInUse;
  private boolean optional;
  
  public IncludeAction() {}
  
  public void begin(InterpretationContext ec, String name, Attributes attributes)
    throws ActionException
  {
    SaxEventRecorder recorder = new SaxEventRecorder(context);
    
    attributeInUse = null;
    optional = OptionHelper.toBoolean(attributes.getValue("optional"), false);
    
    if (!checkAttributes(attributes)) {
      return;
    }
    
    InputStream in = getInputStream(ec, attributes);
    try
    {
      if (in != null) {
        parseAndRecord(in, recorder);
        
        trimHeadAndTail(recorder);
        

        ec.getJoranInterpreter().getEventPlayer().addEventsDynamically(saxEventList, 2);
      }
    } catch (JoranException e) {
      addError("Error while parsing  " + attributeInUse, e);
    } finally {
      close(in);
    }
  }
  
  void close(InputStream in)
  {
    if (in != null) {
      try {
        in.close();
      }
      catch (IOException e) {}
    }
  }
  
  private boolean checkAttributes(Attributes attributes) {
    String fileAttribute = attributes.getValue("file");
    String urlAttribute = attributes.getValue("url");
    String resourceAttribute = attributes.getValue("resource");
    
    int count = 0;
    
    if (!OptionHelper.isEmpty(fileAttribute)) {
      count++;
    }
    if (!OptionHelper.isEmpty(urlAttribute)) {
      count++;
    }
    if (!OptionHelper.isEmpty(resourceAttribute)) {
      count++;
    }
    
    if (count == 0) {
      addError("One of \"path\", \"resource\" or \"url\" attributes must be set.");
      return false; }
    if (count > 1) {
      addError("Only one of \"file\", \"url\" or \"resource\" attributes should be set.");
      return false; }
    if (count == 1) {
      return true;
    }
    throw new IllegalStateException("Count value [" + count + "] is not expected");
  }
  
  private InputStream getInputStreamByFilePath(String pathToFile)
  {
    try {
      return new FileInputStream(pathToFile);
    } catch (IOException ioe) {
      optionalWarning("File [" + pathToFile + "] does not exist."); }
    return null;
  }
  
  URL attributeToURL(String urlAttribute)
  {
    try {
      return new URL(urlAttribute);
    } catch (MalformedURLException mue) {
      String errMsg = "URL [" + urlAttribute + "] is not well formed.";
      addError(errMsg, mue); }
    return null;
  }
  
  private InputStream getInputStreamByUrl(URL url)
  {
    return openURL(url);
  }
  
  InputStream openURL(URL url) {
    try {
      return url.openStream();
    } catch (IOException e) {
      optionalWarning("Failed to open [" + url.toString() + "]"); }
    return null;
  }
  
  URL resourceAsURL(String resourceAttribute)
  {
    URL url = Loader.getResourceBySelfClassLoader(resourceAttribute);
    if (url == null) {
      optionalWarning("Could not find resource corresponding to [" + resourceAttribute + "]");
      return null;
    }
    return url;
  }
  
  private void optionalWarning(String msg) {
    if (!optional) {
      addWarn(msg);
    }
  }
  
  URL filePathAsURL(String path) {
    URI uri = new File(path).toURI();
    try {
      return uri.toURL();
    }
    catch (MalformedURLException e) {
      e.printStackTrace(); }
    return null;
  }
  
  private InputStream getInputStreamByResource(URL url)
  {
    return openURL(url);
  }
  
  URL getInputURL(InterpretationContext ec, Attributes attributes) {
    String fileAttribute = attributes.getValue("file");
    String urlAttribute = attributes.getValue("url");
    String resourceAttribute = attributes.getValue("resource");
    
    if (!OptionHelper.isEmpty(fileAttribute)) {
      attributeInUse = ec.subst(fileAttribute);
      return filePathAsURL(attributeInUse);
    }
    
    if (!OptionHelper.isEmpty(urlAttribute)) {
      attributeInUse = ec.subst(urlAttribute);
      return attributeToURL(attributeInUse);
    }
    
    if (!OptionHelper.isEmpty(resourceAttribute)) {
      attributeInUse = ec.subst(resourceAttribute);
      return resourceAsURL(attributeInUse);
    }
    
    throw new IllegalStateException("A URL stream should have been returned");
  }
  
  InputStream getInputStream(InterpretationContext ec, Attributes attributes)
  {
    URL inputURL = getInputURL(ec, attributes);
    if (inputURL == null) {
      return null;
    }
    ConfigurationWatchListUtil.addToWatchList(context, inputURL);
    return openURL(inputURL);
  }
  


  private void trimHeadAndTail(SaxEventRecorder recorder)
  {
    List<SaxEvent> saxEventList = saxEventList;
    
    if (saxEventList.size() == 0) {
      return;
    }
    
    SaxEvent first = (SaxEvent)saxEventList.get(0);
    if ((first != null) && (qName.equalsIgnoreCase("included"))) {
      saxEventList.remove(0);
    }
    
    SaxEvent last = (SaxEvent)saxEventList.get(saxEventList.size() - 1);
    if ((last != null) && (qName.equalsIgnoreCase("included"))) {
      saxEventList.remove(saxEventList.size() - 1);
    }
  }
  
  private void parseAndRecord(InputStream inputSource, SaxEventRecorder recorder) throws JoranException
  {
    recorder.setContext(context);
    recorder.recordEvents(inputSource);
  }
  
  public void end(InterpretationContext ec, String name)
    throws ActionException
  {}
}
