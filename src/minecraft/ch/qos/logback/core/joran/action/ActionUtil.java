package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.util.ContextUtil;
import ch.qos.logback.core.util.OptionHelper;
import java.util.Properties;











public class ActionUtil
{
  public ActionUtil() {}
  
  public static enum Scope
  {
    LOCAL,  CONTEXT,  SYSTEM;
    

    private Scope() {}
  }
  

  public static Scope stringToScope(String scopeStr)
  {
    if (Scope.SYSTEM.toString().equalsIgnoreCase(scopeStr))
      return Scope.SYSTEM;
    if (Scope.CONTEXT.toString().equalsIgnoreCase(scopeStr)) {
      return Scope.CONTEXT;
    }
    return Scope.LOCAL;
  }
  
  public static void setProperty(InterpretationContext ic, String key, String value, Scope scope) {
    switch (1.$SwitchMap$ch$qos$logback$core$joran$action$ActionUtil$Scope[scope.ordinal()]) {
    case 1: 
      ic.addSubstitutionProperty(key, value);
      break;
    case 2: 
      ic.getContext().putProperty(key, value);
      break;
    case 3: 
      OptionHelper.setSystemProperty(ic, key, value);
    }
    
  }
  



  public static void setProperties(InterpretationContext ic, Properties props, Scope scope)
  {
    switch (1.$SwitchMap$ch$qos$logback$core$joran$action$ActionUtil$Scope[scope.ordinal()]) {
    case 1: 
      ic.addSubstitutionProperties(props);
      break;
    case 2: 
      ContextUtil cu = new ContextUtil(ic.getContext());
      cu.addProperties(props);
      break;
    case 3: 
      OptionHelper.setSystemProperties(ic, props);
    }
  }
}
