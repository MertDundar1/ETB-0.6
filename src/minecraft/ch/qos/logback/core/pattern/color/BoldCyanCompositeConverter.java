package ch.qos.logback.core.pattern.color;











public class BoldCyanCompositeConverter<E>
  extends ForegroundCompositeConverterBase<E>
{
  public BoldCyanCompositeConverter() {}
  










  protected String getForegroundColorCode(E event)
  {
    return "1;36";
  }
}
