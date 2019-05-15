package ch.qos.logback.classic.encoder;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.PatternLayoutEncoderBase;











public class PatternLayoutEncoder
  extends PatternLayoutEncoderBase<ILoggingEvent>
{
  public PatternLayoutEncoder() {}
  
  public void start()
  {
    PatternLayout patternLayout = new PatternLayout();
    patternLayout.setContext(context);
    patternLayout.setPattern(getPattern());
    patternLayout.setOutputPatternAsHeader(outputPatternAsHeader);
    patternLayout.start();
    layout = patternLayout;
    super.start();
  }
}
