package ch.qos.logback.core.pattern.color;











public class BoldGreenCompositeConverter<E>
  extends ForegroundCompositeConverterBase<E>
{
  public BoldGreenCompositeConverter() {}
  










  protected String getForegroundColorCode(E event)
  {
    return "1;32";
  }
}
