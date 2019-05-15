package ch.qos.logback.classic.html;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.pattern.MDCConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.helpers.Transform;
import ch.qos.logback.core.html.HTMLLayoutBase;
import ch.qos.logback.core.html.IThrowableRenderer;
import ch.qos.logback.core.pattern.Converter;
import java.util.Map;


































public class HTMLLayout
  extends HTMLLayoutBase<ILoggingEvent>
{
  static final String DEFAULT_CONVERSION_PATTERN = "%date%thread%level%logger%mdc%msg";
  IThrowableRenderer<ILoggingEvent> throwableRenderer;
  
  public HTMLLayout()
  {
    pattern = "%date%thread%level%logger%mdc%msg";
    throwableRenderer = new DefaultThrowableRenderer();
    cssBuilder = new DefaultCssBuilder();
  }
  
  public void start()
  {
    int errorCount = 0;
    if (throwableRenderer == null) {
      addError("ThrowableRender cannot be null.");
      errorCount++;
    }
    if (errorCount == 0) {
      super.start();
    }
  }
  
  protected Map<String, String> getDefaultConverterMap() {
    return PatternLayout.defaultConverterMap;
  }
  
  public String doLayout(ILoggingEvent event) {
    StringBuilder buf = new StringBuilder();
    startNewTableIfLimitReached(buf);
    
    boolean odd = true;
    if ((counter++ & 1L) == 0L) {
      odd = false;
    }
    
    String level = event.getLevel().toString().toLowerCase();
    
    buf.append(CoreConstants.LINE_SEPARATOR);
    buf.append("<tr class=\"");
    buf.append(level);
    if (odd) {
      buf.append(" odd\">");
    } else {
      buf.append(" even\">");
    }
    buf.append(CoreConstants.LINE_SEPARATOR);
    
    Converter<ILoggingEvent> c = head;
    while (c != null) {
      appendEventToBuffer(buf, c, event);
      c = c.getNext();
    }
    buf.append("</tr>");
    buf.append(CoreConstants.LINE_SEPARATOR);
    
    if (event.getThrowableProxy() != null) {
      throwableRenderer.render(buf, event);
    }
    return buf.toString();
  }
  
  private void appendEventToBuffer(StringBuilder buf, Converter<ILoggingEvent> c, ILoggingEvent event)
  {
    buf.append("<td class=\"");
    buf.append(computeConverterName(c));
    buf.append("\">");
    buf.append(Transform.escapeTags(c.convert(event)));
    buf.append("</td>");
    buf.append(CoreConstants.LINE_SEPARATOR);
  }
  
  public IThrowableRenderer getThrowableRenderer() {
    return throwableRenderer;
  }
  
  public void setThrowableRenderer(IThrowableRenderer<ILoggingEvent> throwableRenderer) {
    this.throwableRenderer = throwableRenderer;
  }
  
  protected String computeConverterName(Converter c)
  {
    if ((c instanceof MDCConverter)) {
      MDCConverter mc = (MDCConverter)c;
      String key = mc.getFirstOption();
      if (key != null) {
        return key;
      }
      return "MDC";
    }
    
    return super.computeConverterName(c);
  }
}
