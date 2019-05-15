package org.slf4j.helpers;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.slf4j.Marker;































public class BasicMarker
  implements Marker
{
  private static final long serialVersionUID = 1803952589649545191L;
  private final String name;
  private List<Marker> refereceList;
  
  BasicMarker(String name)
  {
    if (name == null) {
      throw new IllegalArgumentException("A marker name cannot be null");
    }
    this.name = name;
  }
  
  public String getName() {
    return name;
  }
  
  public synchronized void add(Marker reference) {
    if (reference == null) {
      throw new IllegalArgumentException("A null value cannot be added to a Marker as reference.");
    }
    


    if (contains(reference)) {
      return;
    }
    if (reference.contains(this))
    {
      return;
    }
    
    if (refereceList == null) {
      refereceList = new Vector();
    }
    refereceList.add(reference);
  }
  

  public synchronized boolean hasReferences()
  {
    return (refereceList != null) && (refereceList.size() > 0);
  }
  
  public boolean hasChildren() {
    return hasReferences();
  }
  
  public synchronized Iterator<Marker> iterator() {
    if (refereceList != null) {
      return refereceList.iterator();
    }
    return Collections.EMPTY_LIST.iterator();
  }
  
  public synchronized boolean remove(Marker referenceToRemove)
  {
    if (refereceList == null) {
      return false;
    }
    
    int size = refereceList.size();
    for (int i = 0; i < size; i++) {
      Marker m = (Marker)refereceList.get(i);
      if (referenceToRemove.equals(m)) {
        refereceList.remove(i);
        return true;
      }
    }
    return false;
  }
  
  public boolean contains(Marker other) {
    if (other == null) {
      throw new IllegalArgumentException("Other cannot be null");
    }
    
    if (equals(other)) {
      return true;
    }
    
    if (hasReferences()) {
      for (int i = 0; i < refereceList.size(); i++) {
        Marker ref = (Marker)refereceList.get(i);
        if (ref.contains(other)) {
          return true;
        }
      }
    }
    return false;
  }
  


  public boolean contains(String name)
  {
    if (name == null) {
      throw new IllegalArgumentException("Other cannot be null");
    }
    
    if (this.name.equals(name)) {
      return true;
    }
    
    if (hasReferences()) {
      for (int i = 0; i < refereceList.size(); i++) {
        Marker ref = (Marker)refereceList.get(i);
        if (ref.contains(name)) {
          return true;
        }
      }
    }
    return false;
  }
  
  private static String OPEN = "[ ";
  private static String CLOSE = " ]";
  private static String SEP = ", ";
  
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (!(obj instanceof Marker)) {
      return false;
    }
    Marker other = (Marker)obj;
    return name.equals(other.getName());
  }
  
  public int hashCode() {
    return name.hashCode();
  }
  
  public String toString() {
    if (!hasReferences()) {
      return getName();
    }
    Iterator<Marker> it = iterator();
    
    StringBuffer sb = new StringBuffer(getName());
    sb.append(' ').append(OPEN);
    while (it.hasNext()) {
      Marker reference = (Marker)it.next();
      sb.append(reference.getName());
      if (it.hasNext()) {
        sb.append(SEP);
      }
    }
    sb.append(CLOSE);
    
    return sb.toString();
  }
}
