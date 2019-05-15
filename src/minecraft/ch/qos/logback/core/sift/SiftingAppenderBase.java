package ch.qos.logback.core.sift;

import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.util.Duration;
























public abstract class SiftingAppenderBase<E>
  extends AppenderBase<E>
{
  protected AppenderTracker<E> appenderTracker;
  AppenderFactory<E> appenderFactory;
  Duration timeout = new Duration(1800000L);
  int maxAppenderCount = Integer.MAX_VALUE;
  Discriminator<E> discriminator;
  
  public SiftingAppenderBase() {}
  
  public Duration getTimeout() { return timeout; }
  
  public void setTimeout(Duration timeout)
  {
    this.timeout = timeout;
  }
  
  public int getMaxAppenderCount() {
    return maxAppenderCount;
  }
  
  public void setMaxAppenderCount(int maxAppenderCount) {
    this.maxAppenderCount = maxAppenderCount;
  }
  



  public void setAppenderFactory(AppenderFactory<E> appenderFactory)
  {
    this.appenderFactory = appenderFactory;
  }
  
  public void start()
  {
    int errors = 0;
    if (discriminator == null) {
      addError("Missing discriminator. Aborting");
      errors++;
    }
    if (!discriminator.isStarted()) {
      addError("Discriminator has not started successfully. Aborting");
      errors++;
    }
    if (appenderFactory == null) {
      addError("AppenderFactory has not been set. Aborting");
      errors++;
    } else {
      appenderTracker = new AppenderTracker(context, appenderFactory);
      appenderTracker.setMaxComponents(maxAppenderCount);
      appenderTracker.setTimeout(timeout.getMilliseconds());
    }
    if (errors == 0) {
      super.start();
    }
  }
  
  public void stop()
  {
    for (Appender<E> appender : appenderTracker.allComponents()) {
      appender.stop();
    }
  }
  
  protected abstract long getTimestamp(E paramE);
  
  protected void append(E event)
  {
    if (!isStarted()) {
      return;
    }
    String discriminatingValue = discriminator.getDiscriminatingValue(event);
    long timestamp = getTimestamp(event);
    
    Appender<E> appender = (Appender)appenderTracker.getOrCreate(discriminatingValue, timestamp);
    
    if (eventMarksEndOfLife(event)) {
      appenderTracker.endOfLife(discriminatingValue);
    }
    appenderTracker.removeStaleComponents(timestamp);
    appender.doAppend(event);
  }
  
  protected abstract boolean eventMarksEndOfLife(E paramE);
  
  public Discriminator<E> getDiscriminator() {
    return discriminator;
  }
  
  public void setDiscriminator(Discriminator<E> discriminator) {
    this.discriminator = discriminator;
  }
  









  public AppenderTracker<E> getAppenderTracker()
  {
    return appenderTracker;
  }
  
  public String getDiscriminatorKey() {
    if (discriminator != null) {
      return discriminator.getKey();
    }
    return null;
  }
}
