package ch.qos.logback.core.status;

import ch.qos.logback.core.Context;
import java.io.PrintStream;

















public class OnConsoleStatusListener
  extends OnPrintStreamStatusListenerBase
{
  public OnConsoleStatusListener() {}
  
  protected PrintStream getPrintStream()
  {
    return System.out;
  }
  






  public static void addNewInstanceToContext(Context context)
  {
    OnConsoleStatusListener onConsoleStatusListener = new OnConsoleStatusListener();
    onConsoleStatusListener.setContext(context);
    onConsoleStatusListener.start();
    context.getStatusManager().add(onConsoleStatusListener);
  }
}
