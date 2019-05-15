package ch.qos.logback.classic.spi;

import ch.qos.logback.core.CoreConstants;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;















public class ThrowableProxy
  implements IThrowableProxy
{
  private Throwable throwable;
  private String className;
  private String message;
  StackTraceElementProxy[] stackTraceElementProxyArray;
  int commonFrames;
  private ThrowableProxy cause;
  private ThrowableProxy[] suppressed = NO_SUPPRESSED;
  
  private transient PackagingDataCalculator packagingDataCalculator;
  private boolean calculatedPackageData = false;
  private static final Method GET_SUPPRESSED_METHOD;
  
  static
  {
    Method method = null;
    try {
      method = Throwable.class.getMethod("getSuppressed", new Class[0]);
    }
    catch (NoSuchMethodException e) {}
    
    GET_SUPPRESSED_METHOD = method;
  }
  
  private static final ThrowableProxy[] NO_SUPPRESSED = new ThrowableProxy[0];
  
  public ThrowableProxy(Throwable throwable)
  {
    this.throwable = throwable;
    className = throwable.getClass().getName();
    message = throwable.getMessage();
    stackTraceElementProxyArray = ThrowableProxyUtil.steArrayToStepArray(throwable.getStackTrace());
    

    Throwable nested = throwable.getCause();
    
    if (nested != null) {
      cause = new ThrowableProxy(nested);
      cause.commonFrames = ThrowableProxyUtil.findNumberOfCommonFrames(nested.getStackTrace(), stackTraceElementProxyArray);
    }
    

    if (GET_SUPPRESSED_METHOD != null) {
      try
      {
        Object obj = GET_SUPPRESSED_METHOD.invoke(throwable, new Object[0]);
        if ((obj instanceof Throwable[])) {
          Throwable[] throwableSuppressed = (Throwable[])obj;
          if (throwableSuppressed.length > 0) {
            suppressed = new ThrowableProxy[throwableSuppressed.length];
            for (int i = 0; i < throwableSuppressed.length; i++) {
              suppressed[i] = new ThrowableProxy(throwableSuppressed[i]);
              suppressed[i].commonFrames = ThrowableProxyUtil.findNumberOfCommonFrames(throwableSuppressed[i].getStackTrace(), stackTraceElementProxyArray);
            }
          }
        }
      }
      catch (IllegalAccessException e) {}catch (InvocationTargetException e) {}
    }
  }
  






  public Throwable getThrowable()
  {
    return throwable;
  }
  
  public String getMessage() {
    return message;
  }
  




  public String getClassName()
  {
    return className;
  }
  
  public StackTraceElementProxy[] getStackTraceElementProxyArray() {
    return stackTraceElementProxyArray;
  }
  
  public int getCommonFrames() {
    return commonFrames;
  }
  




  public IThrowableProxy getCause()
  {
    return cause;
  }
  
  public IThrowableProxy[] getSuppressed() {
    return suppressed;
  }
  


  public PackagingDataCalculator getPackagingDataCalculator()
  {
    if ((throwable != null) && (packagingDataCalculator == null)) {
      packagingDataCalculator = new PackagingDataCalculator();
    }
    return packagingDataCalculator;
  }
  
  public void calculatePackagingData() {
    if (calculatedPackageData) {
      return;
    }
    PackagingDataCalculator pdc = getPackagingDataCalculator();
    if (pdc != null) {
      calculatedPackageData = true;
      pdc.calculate(this);
    }
  }
  

  public void fullDump()
  {
    StringBuilder builder = new StringBuilder();
    for (StackTraceElementProxy step : stackTraceElementProxyArray) {
      String string = step.toString();
      builder.append('\t').append(string);
      ThrowableProxyUtil.subjoinPackagingData(builder, step);
      builder.append(CoreConstants.LINE_SEPARATOR);
    }
    System.out.println(builder.toString());
  }
}
