package ch.qos.logback.core.rolling.helper;

import ch.qos.logback.core.pattern.DynamicConverter;















public class IntegerTokenConverter
  extends DynamicConverter
  implements MonoTypedConverter
{
  public static final String CONVERTER_KEY = "i";
  
  public IntegerTokenConverter() {}
  
  public String convert(int i)
  {
    return Integer.toString(i);
  }
  
  public String convert(Object o) {
    if (o == null) {
      throw new IllegalArgumentException("Null argument forbidden");
    }
    if ((o instanceof Integer)) {
      Integer i = (Integer)o;
      return convert(i.intValue());
    }
    throw new IllegalArgumentException("Cannot convert " + o + " of type" + o.getClass().getName());
  }
  
  public boolean isApplicable(Object o) {
    return o instanceof Integer;
  }
}
