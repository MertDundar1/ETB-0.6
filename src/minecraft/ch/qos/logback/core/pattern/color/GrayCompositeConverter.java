package ch.qos.logback.core.pattern.color;











public class GrayCompositeConverter<E>
  extends ForegroundCompositeConverterBase<E>
{
  public GrayCompositeConverter() {}
  










  protected String getForegroundColorCode(E event)
  {
    return "1;30";
  }
}
