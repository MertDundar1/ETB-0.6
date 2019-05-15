package ch.qos.logback.core.status;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;













public abstract class StatusBase
  implements Status
{
  private static final List<Status> EMPTY_LIST = new ArrayList(0);
  int level;
  final String message;
  final Object origin;
  List<Status> childrenList;
  Throwable throwable;
  long date;
  
  StatusBase(int level, String msg, Object origin)
  {
    this(level, msg, origin, null);
  }
  
  StatusBase(int level, String msg, Object origin, Throwable t) {
    this.level = level;
    message = msg;
    this.origin = origin;
    throwable = t;
    date = System.currentTimeMillis();
  }
  
  public synchronized void add(Status child) {
    if (child == null) {
      throw new NullPointerException("Null values are not valid Status.");
    }
    if (childrenList == null) {
      childrenList = new ArrayList();
    }
    childrenList.add(child);
  }
  
  public synchronized boolean hasChildren() {
    return (childrenList != null) && (childrenList.size() > 0);
  }
  
  public synchronized Iterator<Status> iterator() {
    if (childrenList != null) {
      return childrenList.iterator();
    }
    return EMPTY_LIST.iterator();
  }
  
  public synchronized boolean remove(Status statusToRemove)
  {
    if (childrenList == null) {
      return false;
    }
    
    return childrenList.remove(statusToRemove);
  }
  
  public int getLevel() {
    return level;
  }
  



  public synchronized int getEffectiveLevel()
  {
    int result = level;
    

    Iterator it = iterator();
    
    while (it.hasNext()) {
      Status s = (Status)it.next();
      int effLevel = s.getEffectiveLevel();
      if (effLevel > result) {
        result = effLevel;
      }
    }
    return result;
  }
  
  public String getMessage() {
    return message;
  }
  
  public Object getOrigin() {
    return origin;
  }
  
  public Throwable getThrowable() {
    return throwable;
  }
  
  public Long getDate() {
    return Long.valueOf(date);
  }
  


  public String toString()
  {
    StringBuilder buf = new StringBuilder();
    switch (getEffectiveLevel()) {
    case 0: 
      buf.append("INFO");
      break;
    case 1: 
      buf.append("WARN");
      break;
    case 2: 
      buf.append("ERROR");
    }
    
    if (origin != null) {
      buf.append(" in ");
      buf.append(origin);
      buf.append(" -");
    }
    
    buf.append(" ");
    buf.append(message);
    
    if (throwable != null) {
      buf.append(" ");
      buf.append(throwable);
    }
    
    return buf.toString();
  }
  
  public int hashCode()
  {
    int prime = 31;
    int result = 1;
    result = 31 * result + level;
    result = 31 * result + (message == null ? 0 : message.hashCode());
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
    StatusBase other = (StatusBase)obj;
    if (level != level)
      return false;
    if (message == null) {
      if (message != null)
        return false;
    } else if (!message.equals(message))
      return false;
    return true;
  }
}
