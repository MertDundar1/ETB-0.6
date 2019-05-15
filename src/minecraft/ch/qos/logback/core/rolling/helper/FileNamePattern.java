package ch.qos.logback.core.rolling.helper;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.pattern.Converter;
import ch.qos.logback.core.pattern.ConverterUtil;
import ch.qos.logback.core.pattern.LiteralConverter;
import ch.qos.logback.core.pattern.parser.Node;
import ch.qos.logback.core.pattern.parser.Parser;
import ch.qos.logback.core.pattern.util.AlmostAsIsEscapeUtil;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.ScanException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;






















public class FileNamePattern
  extends ContextAwareBase
{
  static final Map<String, String> CONVERTER_MAP = new HashMap();
  
  static { CONVERTER_MAP.put("i", IntegerTokenConverter.class.getName());
    
    CONVERTER_MAP.put("d", DateTokenConverter.class.getName());
  }
  


  String pattern;
  
  public FileNamePattern(String patternArg, Context contextArg)
  {
    setPattern(FileFilterUtil.slashify(patternArg));
    setContext(contextArg);
    parse();
    ConverterUtil.startConverters(headTokenConverter);
  }
  


  void parse()
  {
    try
    {
      String patternForParsing = escapeRightParantesis(pattern);
      Parser<Object> p = new Parser(patternForParsing, new AlmostAsIsEscapeUtil());
      p.setContext(context);
      Node t = p.parse();
      headTokenConverter = p.compile(t, CONVERTER_MAP);
    }
    catch (ScanException sce) {
      addError("Failed to parse pattern \"" + pattern + "\".", sce);
    }
  }
  
  String escapeRightParantesis(String in) {
    return pattern.replace(")", "\\)");
  }
  
  public String toString() {
    return pattern;
  }
  
  public DateTokenConverter getPrimaryDateTokenConverter() {
    Converter p = headTokenConverter;
    
    while (p != null) {
      if ((p instanceof DateTokenConverter)) {
        DateTokenConverter dtc = (DateTokenConverter)p;
        
        if (dtc.isPrimary()) {
          return dtc;
        }
      }
      p = p.getNext();
    }
    
    return null;
  }
  
  public IntegerTokenConverter getIntegerTokenConverter() {
    Converter p = headTokenConverter;
    
    while (p != null) {
      if ((p instanceof IntegerTokenConverter)) {
        return (IntegerTokenConverter)p;
      }
      
      p = p.getNext();
    }
    return null;
  }
  
  public String convertMultipleArguments(Object... objectList) {
    StringBuilder buf = new StringBuilder();
    Converter<Object> c = headTokenConverter;
    while (c != null) {
      if ((c instanceof MonoTypedConverter)) {
        MonoTypedConverter monoTyped = (MonoTypedConverter)c;
        for (Object o : objectList) {
          if (monoTyped.isApplicable(o)) {
            buf.append(c.convert(o));
          }
        }
      } else {
        buf.append(c.convert(objectList));
      }
      c = c.getNext();
    }
    return buf.toString();
  }
  
  public String convert(Object o) {
    StringBuilder buf = new StringBuilder();
    Converter<Object> p = headTokenConverter;
    while (p != null) {
      buf.append(p.convert(o));
      p = p.getNext();
    }
    return buf.toString();
  }
  
  public String convertInt(int i) {
    return convert(Integer.valueOf(i));
  }
  
  public void setPattern(String pattern) {
    if (pattern != null)
    {
      this.pattern = pattern.trim();
    }
  }
  
  public String getPattern() {
    return pattern;
  }
  


  Converter<Object> headTokenConverter;
  

  public String toRegexForFixedDate(Date date)
  {
    StringBuilder buf = new StringBuilder();
    Converter<Object> p = headTokenConverter;
    while (p != null) {
      if ((p instanceof LiteralConverter)) {
        buf.append(p.convert(null));
      } else if ((p instanceof IntegerTokenConverter)) {
        buf.append("(\\d{1,3})");
      } else if ((p instanceof DateTokenConverter)) {
        buf.append(p.convert(date));
      }
      p = p.getNext();
    }
    return buf.toString();
  }
  


  public String toRegex()
  {
    StringBuilder buf = new StringBuilder();
    Converter<Object> p = headTokenConverter;
    while (p != null) {
      if ((p instanceof LiteralConverter)) {
        buf.append(p.convert(null));
      } else if ((p instanceof IntegerTokenConverter)) {
        buf.append("\\d{1,2}");
      } else if ((p instanceof DateTokenConverter)) {
        DateTokenConverter<Object> dtc = (DateTokenConverter)p;
        buf.append(dtc.toRegex());
      }
      p = p.getNext();
    }
    return buf.toString();
  }
}
