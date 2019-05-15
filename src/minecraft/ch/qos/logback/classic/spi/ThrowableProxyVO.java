package ch.qos.logback.classic.spi;

import java.io.Serializable;
import java.util.Arrays;












public class ThrowableProxyVO
  implements IThrowableProxy, Serializable
{
  private static final long serialVersionUID = -773438177285807139L;
  private String className;
  private String message;
  private int commonFramesCount;
  private StackTraceElementProxy[] stackTraceElementProxyArray;
  private IThrowableProxy cause;
  private IThrowableProxy[] suppressed;
  
  public ThrowableProxyVO() {}
  
  public String getMessage()
  {
    return message;
  }
  
  public String getClassName() {
    return className;
  }
  
  public int getCommonFrames() {
    return commonFramesCount;
  }
  
  public IThrowableProxy getCause() {
    return cause;
  }
  
  public StackTraceElementProxy[] getStackTraceElementProxyArray() {
    return stackTraceElementProxyArray;
  }
  
  public IThrowableProxy[] getSuppressed() {
    return suppressed;
  }
  
  public int hashCode()
  {
    int prime = 31;
    int result = 1;
    result = 31 * result + (className == null ? 0 : className.hashCode());
    
    return result;
  }
  
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ThrowableProxyVO other = (ThrowableProxyVO)obj;
    
    if (className == null) {
      if (className != null)
        return false;
    } else if (!className.equals(className)) {
      return false;
    }
    if (!Arrays.equals(stackTraceElementProxyArray, stackTraceElementProxyArray)) {
      return false;
    }
    if (!Arrays.equals(suppressed, suppressed)) {
      return false;
    }
    if (cause == null) {
      if (cause != null)
        return false;
    } else if (!cause.equals(cause)) {
      return false;
    }
    return true;
  }
  
  public static ThrowableProxyVO build(IThrowableProxy throwableProxy) {
    if (throwableProxy == null) {
      return null;
    }
    ThrowableProxyVO tpvo = new ThrowableProxyVO();
    className = throwableProxy.getClassName();
    message = throwableProxy.getMessage();
    commonFramesCount = throwableProxy.getCommonFrames();
    stackTraceElementProxyArray = throwableProxy.getStackTraceElementProxyArray();
    IThrowableProxy cause = throwableProxy.getCause();
    if (cause != null) {
      cause = build(cause);
    }
    IThrowableProxy[] suppressed = throwableProxy.getSuppressed();
    if (suppressed != null) {
      suppressed = new IThrowableProxy[suppressed.length];
      for (int i = 0; i < suppressed.length; i++) {
        suppressed[i] = build(suppressed[i]);
      }
    }
    return tpvo;
  }
}
