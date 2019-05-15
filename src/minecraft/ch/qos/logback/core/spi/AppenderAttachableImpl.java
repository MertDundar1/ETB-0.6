package ch.qos.logback.core.spi;

import ch.qos.logback.core.Appender;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;




















public class AppenderAttachableImpl<E>
  implements AppenderAttachable<E>
{
  private final CopyOnWriteArrayList<Appender<E>> appenderList = new CopyOnWriteArrayList();
  

  public AppenderAttachableImpl() {}
  
  public void addAppender(Appender<E> newAppender)
  {
    if (newAppender == null) {
      throw new IllegalArgumentException("Null argument disallowed");
    }
    appenderList.addIfAbsent(newAppender);
  }
  


  public int appendLoopOnAppenders(E e)
  {
    int size = 0;
    for (Appender<E> appender : appenderList) {
      appender.doAppend(e);
      size++;
    }
    return size;
  }
  





  public Iterator<Appender<E>> iteratorForAppenders()
  {
    return appenderList.iterator();
  }
  





  public Appender<E> getAppender(String name)
  {
    if (name == null) {
      return null;
    }
    Appender<E> found = null;
    for (Appender<E> appender : appenderList) {
      if (name.equals(appender.getName())) {
        return appender;
      }
    }
    return null;
  }
  





  public boolean isAttached(Appender<E> appender)
  {
    if (appender == null) {
      return false;
    }
    for (Appender<E> a : appenderList) {
      if (a == appender) return true;
    }
    return false;
  }
  


  public void detachAndStopAllAppenders()
  {
    for (Appender<E> a : appenderList) {
      a.stop();
    }
    appenderList.clear();
  }
  
  static final long START = ;
  



  public boolean detachAppender(Appender<E> appender)
  {
    if (appender == null) {
      return false;
    }
    
    boolean result = appenderList.remove(appender);
    return result;
  }
  



  public boolean detachAppender(String name)
  {
    if (name == null) {
      return false;
    }
    boolean removed = false;
    for (Appender<E> a : appenderList) {
      if (name.equals(a.getName())) {
        removed = appenderList.remove(a);
        break;
      }
    }
    return removed;
  }
}
