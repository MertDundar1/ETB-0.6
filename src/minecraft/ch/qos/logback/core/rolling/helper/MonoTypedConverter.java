package ch.qos.logback.core.rolling.helper;

public abstract interface MonoTypedConverter
{
  public abstract boolean isApplicable(Object paramObject);
}
