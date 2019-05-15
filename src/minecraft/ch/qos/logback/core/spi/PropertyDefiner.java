package ch.qos.logback.core.spi;

public abstract interface PropertyDefiner
  extends ContextAware
{
  public abstract String getPropertyValue();
}
