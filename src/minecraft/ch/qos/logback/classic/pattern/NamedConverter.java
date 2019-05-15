package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.spi.ILoggingEvent;













public abstract class NamedConverter
  extends ClassicConverter
{
  Abbreviator abbreviator = null;
  


  public NamedConverter() {}
  

  protected abstract String getFullyQualifiedName(ILoggingEvent paramILoggingEvent);
  

  public void start()
  {
    String optStr = getFirstOption();
    if (optStr != null) {
      try {
        int targetLen = Integer.parseInt(optStr);
        if (targetLen == 0) {
          abbreviator = new ClassNameOnlyAbbreviator();
        } else if (targetLen > 0) {
          abbreviator = new TargetLengthBasedClassNameAbbreviator(targetLen);
        }
      }
      catch (NumberFormatException nfe) {}
    }
  }
  
  public String convert(ILoggingEvent event)
  {
    String fqn = getFullyQualifiedName(event);
    
    if (abbreviator == null) {
      return fqn;
    }
    return abbreviator.abbreviate(fqn);
  }
}
