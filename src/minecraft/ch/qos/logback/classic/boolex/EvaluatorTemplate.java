package ch.qos.logback.classic.boolex;

import ch.qos.logback.classic.spi.ILoggingEvent;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;















public class EvaluatorTemplate
  implements IEvaluator, GroovyObject
{
  public EvaluatorTemplate()
  {
    EvaluatorTemplate this;
    CallSite[] arrayOfCallSite = $getCallSiteArray();
    MetaClass localMetaClass = $getStaticMetaClass();
    metaClass = localMetaClass;
  }
  
  public boolean doEvaluate(ILoggingEvent event)
  {
    CallSite[] arrayOfCallSite = $getCallSiteArray();ILoggingEvent e = event;return DefaultTypeTransformation.booleanUnbox(e);return DefaultTypeTransformation.booleanUnbox(Integer.valueOf(0));
  }
}
