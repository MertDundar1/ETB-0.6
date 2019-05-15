package ch.qos.logback.core;

import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.PropertyDefiner;














public abstract class PropertyDefinerBase
  extends ContextAwareBase
  implements PropertyDefiner
{
  public PropertyDefinerBase() {}
  
  protected static String booleanAsStr(boolean bool)
  {
    return bool ? Boolean.TRUE.toString() : Boolean.FALSE.toString();
  }
}
