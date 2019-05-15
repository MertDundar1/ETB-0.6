package ch.qos.logback.core;

import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.FilterAttachableImpl;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.status.WarnStatus;
import java.util.List;






















public abstract class AppenderBase<E>
  extends ContextAwareBase
  implements Appender<E>
{
  protected volatile boolean started = false;
  

  public AppenderBase() {}
  

  private boolean guard = false;
  


  protected String name;
  

  private FilterAttachableImpl<E> fai = new FilterAttachableImpl();
  
  public String getName() {
    return name; }
  

  private int statusRepeatCount = 0;
  private int exceptionCount = 0;
  

  static final int ALLOWED_REPEATS = 5;
  


  public synchronized void doAppend(E eventObject)
  {
    if (guard) {
      return;
    }
    try
    {
      guard = true;
      
      if (!started) {
        if (statusRepeatCount++ < 5) {
          addStatus(new WarnStatus("Attempted to append to non started appender [" + name + "].", this));
        }
        

      }
      else
      {
        if (getFilterChainDecision(eventObject) == FilterReply.DENY) {
          return;
        }
        

        append(eventObject);
      }
    } catch (Exception e) {
      if (exceptionCount++ < 5) {
        addError("Appender [" + name + "] failed to append.", e);
      }
    } finally {
      guard = false;
    }
  }
  

  protected abstract void append(E paramE);
  

  public void setName(String name)
  {
    this.name = name;
  }
  
  public void start() {
    started = true;
  }
  
  public void stop() {
    started = false;
  }
  
  public boolean isStarted() {
    return started;
  }
  
  public String toString() {
    return getClass().getName() + "[" + name + "]";
  }
  
  public void addFilter(Filter<E> newFilter) {
    fai.addFilter(newFilter);
  }
  
  public void clearAllFilters() {
    fai.clearAllFilters();
  }
  
  public List<Filter<E>> getCopyOfAttachedFiltersList() {
    return fai.getCopyOfAttachedFiltersList();
  }
  
  public FilterReply getFilterChainDecision(E event) {
    return fai.getFilterChainDecision(event);
  }
}
