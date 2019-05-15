package ch.qos.logback.classic.gaffer;

import ch.qos.logback.core.Appender;
import ch.qos.logback.core.spi.AppenderAttachable;
import groovy.lang.Closure;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;










public class AppenderDelegate
  extends ComponentDelegate
{
  private Map<String, Appender<?>> appendersByName;
  
  public AppenderDelegate(Appender appender)
  {
    super(appender);Map localMap = ScriptBytecodeAdapter.createMap(new Object[0]);appendersByName = localMap;
  }
  
  class _closure1 extends Closure implements GeneratedClosure { public _closure1(Object _thisObject) { super(_thisObject); }
    
    public Object doCall(Object it) { CallSite[] arrayOfCallSite = $getCallSiteArray();return ScriptBytecodeAdapter.createMap(new Object[] { arrayOfCallSite[0].callGetProperty(it), it });return null;
    }
    
    public Object doCall()
    {
      CallSite[] arrayOfCallSite = $getCallSiteArray();
      return doCall(null);
      return null;
    }
  }
  
  public AppenderDelegate(Appender appender, List<Appender<?>> appenders)
  {
    super(appender);Map localMap = ScriptBytecodeAdapter.createMap(new Object[0]);appendersByName = localMap;
    Object localObject = arrayOfCallSite[0].call(appenders, new _closure1(this));appendersByName = ((Map)ScriptBytecodeAdapter.castToType(localObject, Map.class));
  }
  
  public String getLabel() {
    CallSite[] arrayOfCallSite = $getCallSiteArray();return "appender";return null;
  }
  
  public void appenderRef(String name) {
    CallSite[] arrayOfCallSite = $getCallSiteArray(); if ((!DefaultTypeTransformation.booleanUnbox(arrayOfCallSite[1].call(AppenderAttachable.class, arrayOfCallSite[2].callGetProperty(arrayOfCallSite[3].callGroovyObjectGetProperty(this)))) ? 1 : 0) != 0) {
      Object errorMessage = arrayOfCallSite[4].call(arrayOfCallSite[5].call(arrayOfCallSite[6].call(arrayOfCallSite[7].callGetProperty(arrayOfCallSite[8].callGetProperty(arrayOfCallSite[9].callGroovyObjectGetProperty(this))), " does not implement "), arrayOfCallSite[10].callGetProperty(AppenderAttachable.class)), ".");
      throw ((Throwable)arrayOfCallSite[11].callConstructor(IllegalArgumentException.class, errorMessage));
    }
    arrayOfCallSite[12].call(arrayOfCallSite[13].callGroovyObjectGetProperty(this), arrayOfCallSite[14].call(appendersByName, name));
  }
  
  public Map<String, Appender<?>> getAppendersByName()
  {
    return appendersByName;
  }
  
  public void setAppendersByName(Map<String, Appender<?>> paramMap)
  {
    appendersByName = paramMap;
  }
}
