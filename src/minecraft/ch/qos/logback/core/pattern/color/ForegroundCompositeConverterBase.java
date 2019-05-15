package ch.qos.logback.core.pattern.color;

import ch.qos.logback.core.pattern.CompositeConverter;


















public abstract class ForegroundCompositeConverterBase<E>
  extends CompositeConverter<E>
{
  private static final String SET_DEFAULT_COLOR = "\033[0;39m";
  
  public ForegroundCompositeConverterBase() {}
  
  protected String transform(E event, String in)
  {
    StringBuilder sb = new StringBuilder();
    sb.append("\033[");
    sb.append(getForegroundColorCode(event));
    sb.append("m");
    sb.append(in);
    sb.append("\033[0;39m");
    return sb.toString();
  }
  
  protected abstract String getForegroundColorCode(E paramE);
}
