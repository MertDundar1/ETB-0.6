package com.google.common.eventbus;

import com.google.common.base.Preconditions;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.annotation.Nullable;








































class EventSubscriber
{
  private final Object target;
  private final Method method;
  
  EventSubscriber(Object target, Method method)
  {
    Preconditions.checkNotNull(target, "EventSubscriber target cannot be null.");
    
    Preconditions.checkNotNull(method, "EventSubscriber method cannot be null.");
    
    this.target = target;
    this.method = method;
    method.setAccessible(true);
  }
  






  public void handleEvent(Object event)
    throws InvocationTargetException
  {
    Preconditions.checkNotNull(event);
    try {
      method.invoke(target, new Object[] { event });
    } catch (IllegalArgumentException e) {
      throw new Error("Method rejected target/argument: " + event, e);
    } catch (IllegalAccessException e) {
      throw new Error("Method became inaccessible: " + event, e);
    } catch (InvocationTargetException e) {
      if ((e.getCause() instanceof Error)) {
        throw ((Error)e.getCause());
      }
      throw e;
    }
  }
  
  public String toString() {
    return "[wrapper " + method + "]";
  }
  
  public int hashCode() {
    int PRIME = 31;
    return (31 + method.hashCode()) * 31 + System.identityHashCode(target);
  }
  
  public boolean equals(@Nullable Object obj)
  {
    if ((obj instanceof EventSubscriber)) {
      EventSubscriber that = (EventSubscriber)obj;
      


      return (target == target) && (method.equals(method));
    }
    return false;
  }
  
  public Object getSubscriber() {
    return target;
  }
  
  public Method getMethod() {
    return method;
  }
}
