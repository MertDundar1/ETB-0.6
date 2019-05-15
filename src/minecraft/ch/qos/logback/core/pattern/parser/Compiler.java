package ch.qos.logback.core.pattern.parser;

import ch.qos.logback.core.pattern.CompositeConverter;
import ch.qos.logback.core.pattern.Converter;
import ch.qos.logback.core.pattern.DynamicConverter;
import ch.qos.logback.core.pattern.LiteralConverter;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.status.ErrorStatus;
import ch.qos.logback.core.util.OptionHelper;
import java.util.Map;













class Compiler<E>
  extends ContextAwareBase
{
  Converter<E> head;
  Converter<E> tail;
  final Node top;
  final Map converterMap;
  
  Compiler(Node top, Map converterMap)
  {
    this.top = top;
    this.converterMap = converterMap;
  }
  
  Converter<E> compile() {
    head = (this.tail = null);
    for (Node n = top; n != null; n = next) {
      switch (type) {
      case 0: 
        addToList(new LiteralConverter((String)n.getValue()));
        break;
      case 2: 
        CompositeNode cn = (CompositeNode)n;
        CompositeConverter<E> compositeConverter = createCompositeConverter(cn);
        if (compositeConverter == null) {
          addError("Failed to create converter for [%" + cn.getValue() + "] keyword");
          addToList(new LiteralConverter("%PARSER_ERROR[" + cn.getValue() + "]"));
        }
        else {
          compositeConverter.setFormattingInfo(cn.getFormatInfo());
          compositeConverter.setOptionList(cn.getOptions());
          Compiler<E> childCompiler = new Compiler(cn.getChildNode(), converterMap);
          
          childCompiler.setContext(context);
          Converter<E> childConverter = childCompiler.compile();
          compositeConverter.setChildConverter(childConverter);
          addToList(compositeConverter); }
        break;
      case 1: 
        SimpleKeywordNode kn = (SimpleKeywordNode)n;
        DynamicConverter<E> dynaConverter = createConverter(kn);
        if (dynaConverter != null) {
          dynaConverter.setFormattingInfo(kn.getFormatInfo());
          dynaConverter.setOptionList(kn.getOptions());
          addToList(dynaConverter);
        }
        else
        {
          Converter<E> errConveter = new LiteralConverter("%PARSER_ERROR[" + kn.getValue() + "]");
          
          addStatus(new ErrorStatus("[" + kn.getValue() + "] is not a valid conversion word", this));
          
          addToList(errConveter);
        }
        break;
      }
    }
    return head;
  }
  
  private void addToList(Converter<E> c) {
    if (head == null) {
      head = (this.tail = c);
    } else {
      tail.setNext(c);
      tail = c;
    }
  }
  







  DynamicConverter<E> createConverter(SimpleKeywordNode kn)
  {
    String keyword = (String)kn.getValue();
    String converterClassStr = (String)converterMap.get(keyword);
    
    if (converterClassStr != null) {
      try {
        return (DynamicConverter)OptionHelper.instantiateByClassName(converterClassStr, DynamicConverter.class, context);
      }
      catch (Exception e) {
        addError("Failed to instantiate converter class [" + converterClassStr + "] for keyword [" + keyword + "]", e);
        
        return null;
      }
    }
    addError("There is no conversion class registered for conversion word [" + keyword + "]");
    
    return null;
  }
  








  CompositeConverter<E> createCompositeConverter(CompositeNode cn)
  {
    String keyword = (String)cn.getValue();
    String converterClassStr = (String)converterMap.get(keyword);
    
    if (converterClassStr != null) {
      try {
        return (CompositeConverter)OptionHelper.instantiateByClassName(converterClassStr, CompositeConverter.class, context);
      }
      catch (Exception e) {
        addError("Failed to instantiate converter class [" + converterClassStr + "] as a composite converter for keyword [" + keyword + "]", e);
        
        return null;
      }
    }
    addError("There is no conversion class registered for composite conversion word [" + keyword + "]");
    
    return null;
  }
}
