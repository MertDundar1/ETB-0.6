package ch.qos.logback.core.pattern;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.LayoutBase;
import ch.qos.logback.core.pattern.parser.Node;
import ch.qos.logback.core.pattern.parser.Parser;
import ch.qos.logback.core.spi.ScanException;
import ch.qos.logback.core.status.ErrorStatus;
import ch.qos.logback.core.status.StatusManager;
import java.util.HashMap;
import java.util.Map;


















public abstract class PatternLayoutBase<E>
  extends LayoutBase<E>
{
  Converter<E> head;
  String pattern;
  protected PostCompileProcessor<E> postCompileProcessor;
  Map<String, String> instanceConverterMap = new HashMap();
  protected boolean outputPatternAsHeader = false;
  



  public PatternLayoutBase() {}
  


  public abstract Map<String, String> getDefaultConverterMap();
  


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
    
    effectiveMap.putAll(instanceConverterMap);
    return effectiveMap;
  }
  
  public void start() {
    if ((pattern == null) || (pattern.length() == 0)) {
      addError("Empty or null pattern.");
      return;
    }
    try {
      Parser<E> p = new Parser(pattern);
      if (getContext() != null) {
        p.setContext(getContext());
      }
      Node t = p.parse();
      head = p.compile(t, getEffectiveConverterMap());
      if (postCompileProcessor != null) {
        postCompileProcessor.process(head);
      }
      ConverterUtil.setContextForConverters(getContext(), head);
      ConverterUtil.startConverters(head);
      super.start();
    } catch (ScanException sce) {
      StatusManager sm = getContext().getStatusManager();
      sm.add(new ErrorStatus("Failed to parse pattern \"" + getPattern() + "\".", this, sce));
    }
  }
  

  public void setPostCompileProcessor(PostCompileProcessor<E> postCompileProcessor)
  {
    this.postCompileProcessor = postCompileProcessor;
  }
  


  /**
   * @deprecated
   */
  protected void setContextForConverters(Converter<E> head)
  {
    ConverterUtil.setContextForConverters(getContext(), head);
  }
  
  protected String writeLoopOnConverters(E event) {
    StringBuilder buf = new StringBuilder(128);
    Converter<E> c = head;
    while (c != null) {
      c.write(buf, event);
      c = c.getNext();
    }
    return buf.toString();
  }
  
  public String getPattern() {
    return pattern;
  }
  
  public void setPattern(String pattern) {
    this.pattern = pattern;
  }
  
  public String toString() {
    return getClass().getName() + "(\"" + getPattern() + "\")";
  }
  
  public Map<String, String> getInstanceConverterMap() {
    return instanceConverterMap;
  }
  
  protected String getPresentationHeaderPrefix()
  {
    return "";
  }
  
  public boolean isOutputPatternAsHeader() {
    return outputPatternAsHeader;
  }
  
  public void setOutputPatternAsHeader(boolean outputPatternAsHeader) {
    this.outputPatternAsHeader = outputPatternAsHeader;
  }
  
  public String getPresentationHeader()
  {
    if (outputPatternAsHeader) {
      return getPresentationHeaderPrefix() + pattern;
    }
    return super.getPresentationHeader();
  }
}
