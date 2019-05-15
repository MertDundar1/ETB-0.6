package ch.qos.logback.core.joran;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.event.SaxEvent;
import ch.qos.logback.core.joran.event.SaxEventRecorder;
import ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry;
import ch.qos.logback.core.joran.spi.ElementPath;
import ch.qos.logback.core.joran.spi.EventPlayer;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.joran.spi.Interpreter;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.joran.spi.RuleStore;
import ch.qos.logback.core.joran.spi.SimpleRuleStore;
import ch.qos.logback.core.joran.util.ConfigurationWatchListUtil;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.status.StatusUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import org.xml.sax.InputSource;




public abstract class GenericConfigurator
  extends ContextAwareBase
{
  protected Interpreter interpreter;
  
  public GenericConfigurator() {}
  
  public final void doConfigure(URL url)
    throws JoranException
  {
    InputStream in = null;
    try {
      informContextOfURLUsedForConfiguration(getContext(), url);
      URLConnection urlConnection = url.openConnection();
      

      urlConnection.setUseCaches(false);
      
      in = urlConnection.getInputStream();
      doConfigure(in);
      String errMsg;
      String errMsg; String errMsg; return; } catch (IOException ioe) { errMsg = "Could not open URL [" + url + "].";
      addError(errMsg, ioe);
      throw new JoranException(errMsg, ioe);
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (IOException ioe) {
          errMsg = "Could not close input stream";
          addError(errMsg, ioe);
          throw new JoranException(errMsg, ioe);
        }
      }
    }
  }
  
  public final void doConfigure(String filename) throws JoranException {
    doConfigure(new File(filename));
  }
  
  public final void doConfigure(File file) throws JoranException {
    FileInputStream fis = null;
    try {
      informContextOfURLUsedForConfiguration(getContext(), file.toURI().toURL());
      fis = new FileInputStream(file);
      doConfigure(fis);
      String errMsg;
      String errMsg; String errMsg; return; } catch (IOException ioe) { errMsg = "Could not open [" + file.getPath() + "].";
      addError(errMsg, ioe);
      throw new JoranException(errMsg, ioe);
    } finally {
      if (fis != null) {
        try {
          fis.close();
        } catch (IOException ioe) {
          errMsg = "Could not close [" + file.getName() + "].";
          addError(errMsg, ioe);
          throw new JoranException(errMsg, ioe);
        }
      }
    }
  }
  
  public static void informContextOfURLUsedForConfiguration(Context context, URL url) {
    ConfigurationWatchListUtil.setMainWatchURL(context, url);
  }
  
  public final void doConfigure(InputStream inputStream) throws JoranException {
    doConfigure(new InputSource(inputStream));
  }
  

  protected abstract void addInstanceRules(RuleStore paramRuleStore);
  
  protected abstract void addImplicitRules(Interpreter paramInterpreter);
  
  protected void addDefaultNestedComponentRegistryRules(DefaultNestedComponentRegistry registry) {}
  
  protected ElementPath initialElementPath()
  {
    return new ElementPath();
  }
  
  protected void buildInterpreter() {
    RuleStore rs = new SimpleRuleStore(context);
    addInstanceRules(rs);
    interpreter = new Interpreter(context, rs, initialElementPath());
    InterpretationContext interpretationContext = interpreter.getInterpretationContext();
    interpretationContext.setContext(context);
    addImplicitRules(interpreter);
    addDefaultNestedComponentRegistryRules(interpretationContext.getDefaultNestedComponentRegistry());
  }
  


  public final void doConfigure(InputSource inputSource)
    throws JoranException
  {
    long threshold = System.currentTimeMillis();
    if (!ConfigurationWatchListUtil.wasConfigurationWatchListReset(context)) {
      informContextOfURLUsedForConfiguration(getContext(), null);
    }
    SaxEventRecorder recorder = new SaxEventRecorder(context);
    recorder.recordEvents(inputSource);
    doConfigure(saxEventList);
    
    StatusUtil statusUtil = new StatusUtil(context);
    if (statusUtil.noXMLParsingErrorsOccurred(threshold)) {
      addInfo("Registering current configuration as safe fallback point");
      registerSafeConfiguration();
    }
  }
  
  public void doConfigure(List<SaxEvent> eventList) throws JoranException
  {
    buildInterpreter();
    
    synchronized (context.getConfigurationLock()) {
      interpreter.getEventPlayer().play(eventList);
    }
  }
  





  public void registerSafeConfiguration()
  {
    context.putObject("SAFE_JORAN_CONFIGURATION", interpreter.getEventPlayer().getCopyOfPlayerEventList());
  }
  


  public List<SaxEvent> recallSafeConfiguration()
  {
    return (List)context.getObject("SAFE_JORAN_CONFIGURATION");
  }
}
