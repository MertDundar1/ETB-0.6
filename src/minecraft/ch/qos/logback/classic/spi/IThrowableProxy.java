package ch.qos.logback.classic.spi;

public abstract interface IThrowableProxy
{
  public abstract String getMessage();
  
  public abstract String getClassName();
  
  public abstract StackTraceElementProxy[] getStackTraceElementProxyArray();
  
  public abstract int getCommonFrames();
  
  public abstract IThrowableProxy getCause();
  
  public abstract IThrowableProxy[] getSuppressed();
}
