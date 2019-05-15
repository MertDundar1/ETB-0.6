package ch.qos.logback.core.pattern;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.ContextAware;
















public class ConverterUtil
{
  public ConverterUtil() {}
  
  public static <E> void startConverters(Converter<E> head)
  {
    Converter<E> c = head;
    while (c != null)
    {
      if ((c instanceof CompositeConverter)) {
        CompositeConverter<E> cc = (CompositeConverter)c;
        Converter<E> childConverter = childConverter;
        startConverters(childConverter);
        cc.start();
      } else if ((c instanceof DynamicConverter)) {
        DynamicConverter<E> dc = (DynamicConverter)c;
        dc.start();
      }
      c = c.getNext();
    }
  }
  
  public static <E> Converter<E> findTail(Converter<E> head)
  {
    Converter<E> p = head;
    while (p != null) {
      Converter<E> next = p.getNext();
      if (next == null) {
        break;
      }
      p = next;
    }
    
    return p;
  }
  
  public static <E> void setContextForConverters(Context context, Converter<E> head) {
    Converter<E> c = head;
    while (c != null) {
      if ((c instanceof ContextAware)) {
        ((ContextAware)c).setContext(context);
      }
      c = c.getNext();
    }
  }
}
