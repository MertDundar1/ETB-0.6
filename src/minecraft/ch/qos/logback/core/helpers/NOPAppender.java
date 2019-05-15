package ch.qos.logback.core.helpers;

import ch.qos.logback.core.AppenderBase;

public final class NOPAppender<E>
  extends AppenderBase<E>
{
  public NOPAppender() {}
  
  protected void append(E eventObject) {}
}
