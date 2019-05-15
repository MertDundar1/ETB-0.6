package ch.qos.logback.core.pattern.color;









public class BlueCompositeConverter<E>
  extends ForegroundCompositeConverterBase<E>
{
  public BlueCompositeConverter() {}
  








  protected String getForegroundColorCode(E event)
  {
    return "34";
  }
}
