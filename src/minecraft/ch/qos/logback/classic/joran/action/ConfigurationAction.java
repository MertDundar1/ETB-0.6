package ch.qos.logback.classic.joran.action;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.turbo.ReconfigureOnChangeFilter;
import ch.qos.logback.classic.util.EnvUtil;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.status.OnConsoleStatusListener;
import ch.qos.logback.core.util.ContextUtil;
import ch.qos.logback.core.util.Duration;
import ch.qos.logback.core.util.OptionHelper;
import org.xml.sax.Attributes;











public class ConfigurationAction
  extends Action
{
  static final String INTERNAL_DEBUG_ATTR = "debug";
  static final String SCAN_ATTR = "scan";
  static final String SCAN_PERIOD_ATTR = "scanPeriod";
  static final String DEBUG_SYSTEM_PROPERTY_KEY = "logback.debug";
  
  public ConfigurationAction() {}
  
  long threshold = 0L;
  
  public void begin(InterpretationContext ic, String name, Attributes attributes) {
    threshold = System.currentTimeMillis();
    



    String debugAttrib = getSystemProperty("logback.debug");
    if (debugAttrib == null) {
      debugAttrib = ic.subst(attributes.getValue("debug"));
    }
    
    if ((OptionHelper.isEmpty(debugAttrib)) || (debugAttrib.equalsIgnoreCase("false")) || (debugAttrib.equalsIgnoreCase("null")))
    {
      addInfo("debug attribute not set");
    } else {
      OnConsoleStatusListener.addNewInstanceToContext(context);
    }
    
    processScanAttrib(ic, attributes);
    
    ContextUtil contextUtil = new ContextUtil(context);
    contextUtil.addHostNameAsProperty();
    
    if (EnvUtil.isGroovyAvailable()) {
      LoggerContext lc = (LoggerContext)context;
      contextUtil.addGroovyPackages(lc.getFrameworkPackages());
    }
    


    ic.pushObject(getContext());
  }
  


  String getSystemProperty(String name)
  {
    try
    {
      return System.getProperty(name);
    } catch (SecurityException ex) {}
    return null;
  }
  
  void processScanAttrib(InterpretationContext ic, Attributes attributes)
  {
    String scanAttrib = ic.subst(attributes.getValue("scan"));
    if ((!OptionHelper.isEmpty(scanAttrib)) && (!"false".equalsIgnoreCase(scanAttrib)))
    {
      ReconfigureOnChangeFilter rocf = new ReconfigureOnChangeFilter();
      rocf.setContext(context);
      String scanPeriodAttrib = ic.subst(attributes.getValue("scanPeriod"));
      if (!OptionHelper.isEmpty(scanPeriodAttrib)) {
        try {
          Duration duration = Duration.valueOf(scanPeriodAttrib);
          rocf.setRefreshPeriod(duration.getMilliseconds());
          addInfo("Setting ReconfigureOnChangeFilter scanning period to " + duration);
        }
        catch (NumberFormatException nfe) {
          addError("Error while converting [" + scanAttrib + "] to long", nfe);
        }
      }
      rocf.start();
      LoggerContext lc = (LoggerContext)context;
      addInfo("Adding ReconfigureOnChangeFilter as a turbo filter");
      lc.addTurboFilter(rocf);
    }
  }
  
  public void end(InterpretationContext ec, String name) {
    addInfo("End of configuration.");
    ec.popObject();
  }
}
