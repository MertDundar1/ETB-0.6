package io.netty.handler.codec.spdy;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;
















public class DefaultSpdyHeaders
  extends SpdyHeaders
{
  private static final int BUCKET_SIZE = 17;
  
  private static int hash(String name)
  {
    int h = 0;
    for (int i = name.length() - 1; i >= 0; i--) {
      char c = name.charAt(i);
      if ((c >= 'A') && (c <= 'Z')) {
        c = (char)(c + ' ');
      }
      h = 31 * h + c;
    }
    
    if (h > 0)
      return h;
    if (h == Integer.MIN_VALUE) {
      return Integer.MAX_VALUE;
    }
    return -h;
  }
  
  private static boolean eq(String name1, String name2)
  {
    int nameLen = name1.length();
    if (nameLen != name2.length()) {
      return false;
    }
    
    for (int i = nameLen - 1; i >= 0; i--) {
      char c1 = name1.charAt(i);
      char c2 = name2.charAt(i);
      if (c1 != c2) {
        if ((c1 >= 'A') && (c1 <= 'Z')) {
          c1 = (char)(c1 + ' ');
        }
        if ((c2 >= 'A') && (c2 <= 'Z')) {
          c2 = (char)(c2 + ' ');
        }
        if (c1 != c2) {
          return false;
        }
      }
    }
    return true;
  }
  
  private static int index(int hash) {
    return hash % 17;
  }
  
  private final HeaderEntry[] entries = new HeaderEntry[17];
  private final HeaderEntry head = new HeaderEntry(-1, null, null);
  
  DefaultSpdyHeaders() {
    head.before = (head.after = head);
  }
  
  public SpdyHeaders add(String name, Object value)
  {
    String lowerCaseName = name.toLowerCase();
    SpdyCodecUtil.validateHeaderName(lowerCaseName);
    String strVal = toString(value);
    SpdyCodecUtil.validateHeaderValue(strVal);
    int h = hash(lowerCaseName);
    int i = index(h);
    add0(h, i, lowerCaseName, strVal);
    return this;
  }
  
  private void add0(int h, int i, String name, String value)
  {
    HeaderEntry e = entries[i]; void 
    
      tmp24_21 = new HeaderEntry(h, name, value);HeaderEntry newEntry = tmp24_21;entries[i] = tmp24_21;
    next = e;
    

    newEntry.addBefore(head);
  }
  
  public SpdyHeaders remove(String name)
  {
    if (name == null) {
      throw new NullPointerException("name");
    }
    String lowerCaseName = name.toLowerCase();
    int h = hash(lowerCaseName);
    int i = index(h);
    remove0(h, i, lowerCaseName);
    return this;
  }
  
  private void remove0(int h, int i, String name) {
    HeaderEntry e = entries[i];
    if (e == null) {
      return;
    }
    

    while ((hash == h) && (eq(name, key))) {
      e.remove();
      HeaderEntry next = next;
      if (next != null) {
        entries[i] = next;
        e = next;
      } else {
        entries[i] = null; return;
      }
    }
    



    for (;;)
    {
      HeaderEntry next = next;
      if (next == null) {
        break;
      }
      if ((hash == h) && (eq(name, key))) {
        next = next;
        next.remove();
      } else {
        e = next;
      }
    }
  }
  
  public SpdyHeaders set(String name, Object value)
  {
    String lowerCaseName = name.toLowerCase();
    SpdyCodecUtil.validateHeaderName(lowerCaseName);
    String strVal = toString(value);
    SpdyCodecUtil.validateHeaderValue(strVal);
    int h = hash(lowerCaseName);
    int i = index(h);
    remove0(h, i, lowerCaseName);
    add0(h, i, lowerCaseName, strVal);
    return this;
  }
  
  public SpdyHeaders set(String name, Iterable<?> values)
  {
    if (values == null) {
      throw new NullPointerException("values");
    }
    
    String lowerCaseName = name.toLowerCase();
    SpdyCodecUtil.validateHeaderName(lowerCaseName);
    
    int h = hash(lowerCaseName);
    int i = index(h);
    
    remove0(h, i, lowerCaseName);
    for (Object v : values) {
      if (v == null) {
        break;
      }
      String strVal = toString(v);
      SpdyCodecUtil.validateHeaderValue(strVal);
      add0(h, i, lowerCaseName, strVal);
    }
    return this;
  }
  
  public SpdyHeaders clear()
  {
    for (int i = 0; i < entries.length; i++) {
      entries[i] = null;
    }
    head.before = (head.after = head);
    return this;
  }
  
  public String get(String name)
  {
    if (name == null) {
      throw new NullPointerException("name");
    }
    
    int h = hash(name);
    int i = index(h);
    HeaderEntry e = entries[i];
    while (e != null) {
      if ((hash == h) && (eq(name, key))) {
        return value;
      }
      
      e = next;
    }
    return null;
  }
  
  public List<String> getAll(String name)
  {
    if (name == null) {
      throw new NullPointerException("name");
    }
    
    LinkedList<String> values = new LinkedList();
    
    int h = hash(name);
    int i = index(h);
    HeaderEntry e = entries[i];
    while (e != null) {
      if ((hash == h) && (eq(name, key))) {
        values.addFirst(value);
      }
      e = next;
    }
    return values;
  }
  
  public List<Map.Entry<String, String>> entries()
  {
    List<Map.Entry<String, String>> all = new LinkedList();
    

    HeaderEntry e = head.after;
    while (e != head) {
      all.add(e);
      e = after;
    }
    return all;
  }
  
  public Iterator<Map.Entry<String, String>> iterator()
  {
    return new HeaderIterator(null);
  }
  
  public boolean contains(String name)
  {
    return get(name) != null;
  }
  
  public Set<String> names()
  {
    Set<String> names = new TreeSet();
    
    HeaderEntry e = head.after;
    while (e != head) {
      names.add(key);
      e = after;
    }
    return names;
  }
  
  public SpdyHeaders add(String name, Iterable<?> values)
  {
    SpdyCodecUtil.validateHeaderValue(name);
    int h = hash(name);
    int i = index(h);
    for (Object v : values) {
      String vstr = toString(v);
      SpdyCodecUtil.validateHeaderValue(vstr);
      add0(h, i, name, vstr);
    }
    return this;
  }
  
  public boolean isEmpty()
  {
    return head == head.after;
  }
  
  private static String toString(Object value) {
    if (value == null) {
      return null;
    }
    return value.toString();
  }
  
  private final class HeaderIterator implements Iterator<Map.Entry<String, String>>
  {
    private DefaultSpdyHeaders.HeaderEntry current = head;
    
    private HeaderIterator() {}
    
    public boolean hasNext() { return current.after != head; }
    

    public Map.Entry<String, String> next()
    {
      current = current.after;
      
      if (current == head) {
        throw new NoSuchElementException();
      }
      
      return current;
    }
    


    public void remove() { throw new UnsupportedOperationException(); }
  }
  
  private static final class HeaderEntry implements Map.Entry<String, String> {
    final int hash;
    final String key;
    String value;
    HeaderEntry next;
    HeaderEntry before;
    HeaderEntry after;
    
    HeaderEntry(int hash, String key, String value) {
      this.hash = hash;
      this.key = key;
      this.value = value;
    }
    
    void remove() {
      before.after = after;
      after.before = before;
    }
    
    void addBefore(HeaderEntry e) {
      after = e;
      before = before;
      before.after = this;
      after.before = this;
    }
    
    public String getKey()
    {
      return key;
    }
    
    public String getValue()
    {
      return value;
    }
    
    public String setValue(String value)
    {
      if (value == null) {
        throw new NullPointerException("value");
      }
      SpdyCodecUtil.validateHeaderValue(value);
      String oldValue = this.value;
      this.value = value;
      return oldValue;
    }
    
    public String toString()
    {
      return key + '=' + value;
    }
  }
}
