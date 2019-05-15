package ch.qos.logback.classic.pattern;









public abstract class ThrowableHandlingConverter
  extends ClassicConverter
{
  public ThrowableHandlingConverter() {}
  







  boolean handlesThrowable()
  {
    return true;
  }
}
