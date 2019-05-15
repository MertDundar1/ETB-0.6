package ch.qos.logback.core.html;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.LayoutBase;
import ch.qos.logback.core.pattern.Converter;
import ch.qos.logback.core.pattern.ConverterUtil;
import ch.qos.logback.core.pattern.parser.Node;
import ch.qos.logback.core.pattern.parser.Parser;
import ch.qos.logback.core.spi.ScanException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;























public abstract class HTMLLayoutBase<E>
  extends LayoutBase<E>
{
  protected String pattern;
  protected Converter<E> head;
  protected String title = "Logback Log Messages";
  


  protected CssBuilder cssBuilder;
  

  protected long counter = 0L;
  

  public HTMLLayoutBase() {}
  

  public void setPattern(String conversionPattern)
  {
    pattern = conversionPattern;
  }
  


  public String getPattern()
  {
    return pattern;
  }
  
  public CssBuilder getCssBuilder() {
    return cssBuilder;
  }
  
  public void setCssBuilder(CssBuilder cssBuilder) {
    this.cssBuilder = cssBuilder;
  }
  



  public void start()
  {
    int errorCount = 0;
    try
    {
      Parser<E> p = new Parser(pattern);
      p.setContext(getContext());
      Node t = p.parse();
      head = p.compile(t, getEffectiveConverterMap());
      ConverterUtil.startConverters(head);
    } catch (ScanException ex) {
      addError("Incorrect pattern found", ex);
      errorCount++;
    }
    
    if (errorCount == 0) {
      started = true;
    }
  }
  


  protected abstract Map<String, String> getDefaultConverterMap();
  


  public Map<String, String> getEffectiveConverterMap()
  {
    Map<String, String> effectiveMap = new HashMap();
    

    Map<String, String> defaultMap = getDefaultConverterMap();
    if (defaultMap != null) {
      effectiveMap.putAll(defaultMap);
    }
    

    Context context = getContext();
    if (context != null)
    {
      Map<String, String> contextMap = (Map)context.getObject("PATTERN_RULE_REGISTRY");
      
      if (contextMap != null) {
        effectiveMap.putAll(contextMap);
      }
    }
    return effectiveMap;
  }
  






  public void setTitle(String title)
  {
    this.title = title;
  }
  


  public String getTitle()
  {
    return title;
  }
  



  public String getContentType()
  {
    return "text/html";
  }
  



  public String getFileHeader()
  {
    StringBuilder sbuf = new StringBuilder();
    sbuf.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"");
    sbuf.append(" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
    sbuf.append(CoreConstants.LINE_SEPARATOR);
    sbuf.append("<html>");
    sbuf.append(CoreConstants.LINE_SEPARATOR);
    sbuf.append("  <head>");
    sbuf.append(CoreConstants.LINE_SEPARATOR);
    sbuf.append("    <title>");
    sbuf.append(title);
    sbuf.append("</title>");
    sbuf.append(CoreConstants.LINE_SEPARATOR);
    
    cssBuilder.addCss(sbuf);
    
    sbuf.append(CoreConstants.LINE_SEPARATOR);
    sbuf.append("  </head>");
    sbuf.append(CoreConstants.LINE_SEPARATOR);
    sbuf.append("<body>");
    sbuf.append(CoreConstants.LINE_SEPARATOR);
    
    return sbuf.toString();
  }
  
  public String getPresentationHeader() {
    StringBuilder sbuf = new StringBuilder();
    sbuf.append("<hr/>");
    sbuf.append(CoreConstants.LINE_SEPARATOR);
    sbuf.append("<p>Log session start time ");
    sbuf.append(new Date());
    sbuf.append("</p><p></p>");
    sbuf.append(CoreConstants.LINE_SEPARATOR);
    sbuf.append(CoreConstants.LINE_SEPARATOR);
    sbuf.append("<table cellspacing=\"0\">");
    sbuf.append(CoreConstants.LINE_SEPARATOR);
    
    buildHeaderRowForTable(sbuf);
    
    return sbuf.toString();
  }
  
  private void buildHeaderRowForTable(StringBuilder sbuf)
  {
    Converter c = head;
    
    sbuf.append("<tr class=\"header\">");
    sbuf.append(CoreConstants.LINE_SEPARATOR);
    while (c != null) {
      String name = computeConverterName(c);
      if (name == null) {
        c = c.getNext();
      }
      else {
        sbuf.append("<td class=\"");
        sbuf.append(computeConverterName(c));
        sbuf.append("\">");
        sbuf.append(computeConverterName(c));
        sbuf.append("</td>");
        sbuf.append(CoreConstants.LINE_SEPARATOR);
        c = c.getNext();
      } }
    sbuf.append("</tr>");
    sbuf.append(CoreConstants.LINE_SEPARATOR);
  }
  
  public String getPresentationFooter() {
    StringBuilder sbuf = new StringBuilder();
    sbuf.append("</table>");
    return sbuf.toString();
  }
  



  public String getFileFooter()
  {
    StringBuilder sbuf = new StringBuilder();
    sbuf.append(CoreConstants.LINE_SEPARATOR);
    sbuf.append("</body></html>");
    return sbuf.toString();
  }
  
  protected void startNewTableIfLimitReached(StringBuilder sbuf) {
    if (counter >= 10000L) {
      counter = 0L;
      sbuf.append("</table>");
      sbuf.append(CoreConstants.LINE_SEPARATOR);
      sbuf.append("<p></p>");
      sbuf.append("<table cellspacing=\"0\">");
      sbuf.append(CoreConstants.LINE_SEPARATOR);
      buildHeaderRowForTable(sbuf);
    }
  }
  
  protected String computeConverterName(Converter c) {
    String className = c.getClass().getSimpleName();
    int index = className.indexOf("Converter");
    if (index == -1) {
      return className;
    }
    return className.substring(0, index);
  }
}
