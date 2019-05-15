package ch.qos.logback.core.sift;

import ch.qos.logback.core.Appender;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.helpers.NOPAppender;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.spi.AbstractComponentTracker;
import ch.qos.logback.core.spi.ContextAwareImpl;






















public class AppenderTracker<E>
  extends AbstractComponentTracker<Appender<E>>
{
  int nopaWarningCount = 0;
  
  final Context context;
  final AppenderFactory<E> appenderFactory;
  final ContextAwareImpl contextAware;
  
  public AppenderTracker(Context context, AppenderFactory<E> appenderFactory)
  {
    this.context = context;
    this.appenderFactory = appenderFactory;
    contextAware = new ContextAwareImpl(context, this);
  }
  

  protected void processPriorToRemoval(Appender<E> component)
  {
    component.stop();
  }
  
  protected Appender<E> buildComponent(String key)
  {
    Appender<E> appender = null;
    try {
      appender = appenderFactory.buildAppender(context, key);
    } catch (JoranException je) {
      contextAware.addError("Error while building appender with discriminating value [" + key + "]");
    }
    if (appender == null) {
      appender = buildNOPAppender(key);
    }
    
    return appender;
  }
  
  private NOPAppender<E> buildNOPAppender(String key) {
    if (nopaWarningCount < 4) {
      nopaWarningCount += 1;
      contextAware.addError("Building NOPAppender for discriminating value [" + key + "]");
    }
    NOPAppender<E> nopa = new NOPAppender();
    nopa.setContext(context);
    nopa.start();
    return nopa;
  }
  
  protected boolean isComponentStale(Appender<E> appender)
  {
    return !appender.isStarted();
  }
}
