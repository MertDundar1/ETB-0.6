package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
















public class DefaultHttpHeaders
  extends HttpHeaders
{
  private static final int BUCKET_SIZE = 17;
  
  private static int index(int hash)
  {
    return hash % 17;
  }
  
  private final HeaderEntry[] entries = new HeaderEntry[17];
  private final HeaderEntry head = new HeaderEntry();
  protected final boolean validate;
  
  public DefaultHttpHeaders() {
    this(true);
  }
  
  public DefaultHttpHeaders(boolean validate) {
    this.validate = validate;
    head.before = (head.after = head);
  }
  
  void validateHeaderName0(CharSequence headerName) {
    validateHeaderName(headerName);
  }
  
  public HttpHeaders add(HttpHeaders headers)
  {
    if ((headers instanceof DefaultHttpHeaders)) {
      DefaultHttpHeaders defaultHttpHeaders = (DefaultHttpHeaders)headers;
      HeaderEntry e = head.after;
      while (e != head) {
        add(key, value);
        e = after;
      }
      return this;
    }
    return super.add(headers);
  }
  

  public HttpHeaders set(HttpHeaders headers)
  {
    if ((headers instanceof DefaultHttpHeaders)) {
      clear();
      DefaultHttpHeaders defaultHttpHeaders = (DefaultHttpHeaders)headers;
      HeaderEntry e = head.after;
      while (e != head) {
        add(key, value);
        e = after;
      }
      return this;
    }
    return super.set(headers);
  }
  

  public HttpHeaders add(String name, Object value)
  {
    return add(name, value);
  }
  
  public HttpHeaders add(CharSequence name, Object value)
  {
    CharSequence strVal;
    if (validate) {
      validateHeaderName0(name);
      CharSequence strVal = toCharSequence(value);
      validateHeaderValue(strVal);
    } else {
      strVal = toCharSequence(value);
    }
    int h = hash(name);
    int i = index(h);
    add0(h, i, name, strVal);
    return this;
  }
  
  public HttpHeaders add(String name, Iterable<?> values)
  {
    return add(name, values);
  }
  
  public HttpHeaders add(CharSequence name, Iterable<?> values)
  {
    if (validate) {
      validateHeaderName0(name);
    }
    int h = hash(name);
    int i = index(h);
    for (Object v : values) {
      CharSequence vstr = toCharSequence(v);
      if (validate) {
        validateHeaderValue(vstr);
      }
      add0(h, i, name, vstr);
    }
    return this;
  }
  
  private void add0(int h, int i, CharSequence name, CharSequence value)
  {
    HeaderEntry e = entries[i]; void 
    
      tmp25_22 = new HeaderEntry(h, name, value);HeaderEntry newEntry = tmp25_22;entries[i] = tmp25_22;
    next = e;
    

    newEntry.addBefore(head);
  }
  
  public HttpHeaders remove(String name)
  {
    return remove(name);
  }
  
  public HttpHeaders remove(CharSequence name)
  {
    if (name == null) {
      throw new NullPointerException("name");
    }
    int h = hash(name);
    int i = index(h);
    remove0(h, i, name);
    return this;
  }
  
  private void remove0(int h, int i, CharSequence name) {
    HeaderEntry e = entries[i];
    if (e == null) {
      return;
    }
    

    while ((hash == h) && (equalsIgnoreCase(name, key))) {
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
      if ((hash == h) && (equalsIgnoreCase(name, key))) {
        next = next;
        next.remove();
      } else {
        e = next;
      }
    }
  }
  
  public HttpHeaders set(String name, Object value)
  {
    return set(name, value);
  }
  
  public HttpHeaders set(CharSequence name, Object value)
  {
    CharSequence strVal;
    if (validate) {
      validateHeaderName0(name);
      CharSequence strVal = toCharSequence(value);
      validateHeaderValue(strVal);
    } else {
      strVal = toCharSequence(value);
    }
    int h = hash(name);
    int i = index(h);
    remove0(h, i, name);
    add0(h, i, name, strVal);
    return this;
  }
  
  public HttpHeaders set(String name, Iterable<?> values)
  {
    return set(name, values);
  }
  
  public HttpHeaders set(CharSequence name, Iterable<?> values)
  {
    if (values == null) {
      throw new NullPointerException("values");
    }
    if (validate) {
      validateHeaderName0(name);
    }
    
    int h = hash(name);
    int i = index(h);
    
    remove0(h, i, name);
    for (Object v : values) {
      if (v == null) {
        break;
      }
      CharSequence strVal = toCharSequence(v);
      if (validate) {
        validateHeaderValue(strVal);
      }
      add0(h, i, name, strVal);
    }
    
    return this;
  }
  
  public HttpHeaders clear()
  {
    Arrays.fill(entries, null);
    head.before = (head.after = head);
    return this;
  }
  
  public String get(String name)
  {
    return get(name);
  }
  
  public String get(CharSequence name)
  {
    if (name == null) {
      throw new NullPointerException("name");
    }
    
    int h = hash(name);
    int i = index(h);
    HeaderEntry e = entries[i];
    CharSequence value = null;
    
    while (e != null) {
      if ((hash == h) && (equalsIgnoreCase(name, key))) {
        value = value;
      }
      
      e = next;
    }
    if (value == null) {
      return null;
    }
    return value.toString();
  }
  
  public List<String> getAll(String name)
  {
    return getAll(name);
  }
  
  public List<String> getAll(CharSequence name)
  {
    if (name == null) {
      throw new NullPointerException("name");
    }
    
    LinkedList<String> values = new LinkedList();
    
    int h = hash(name);
    int i = index(h);
    HeaderEntry e = entries[i];
    while (e != null) {
      if ((hash == h) && (equalsIgnoreCase(name, key))) {
        values.addFirst(e.getValue());
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
  
  public boolean contains(CharSequence name)
  {
    return get(name) != null;
  }
  
  public boolean isEmpty()
  {
    return head == head.after;
  }
  
  public boolean contains(String name, String value, boolean ignoreCaseValue)
  {
    return contains(name, value, ignoreCaseValue);
  }
  
  public boolean contains(CharSequence name, CharSequence value, boolean ignoreCaseValue)
  {
    if (name == null) {
      throw new NullPointerException("name");
    }
    
    int h = hash(name);
    int i = index(h);
    HeaderEntry e = entries[i];
    while (e != null) {
      if ((hash == h) && (equalsIgnoreCase(name, key))) {
        if (ignoreCaseValue) {
          if (equalsIgnoreCase(value, value)) {
            return true;
          }
        }
        else if (value.equals(value)) {
          return true;
        }
      }
      
      e = next;
    }
    return false;
  }
  
  public Set<String> names()
  {
    Set<String> names = new LinkedHashSet();
    HeaderEntry e = head.after;
    while (e != head) {
      names.add(e.getKey());
      e = after;
    }
    return names;
  }
  
  private static CharSequence toCharSequence(Object value) {
    if (value == null) {
      return null;
    }
    if ((value instanceof CharSequence)) {
      return (CharSequence)value;
    }
    if ((value instanceof Number)) {
      return value.toString();
    }
    if ((value instanceof Date)) {
      return HttpHeaderDateFormat.get().format((Date)value);
    }
    if ((value instanceof Calendar)) {
      return HttpHeaderDateFormat.get().format(((Calendar)value).getTime());
    }
    return value.toString();
  }
  
  void encode(ByteBuf buf) {
    HeaderEntry e = head.after;
    while (e != head) {
      e.encode(buf);
      e = after;
    }
  }
  
  private final class HeaderIterator implements Iterator<Map.Entry<String, String>>
  {
    private DefaultHttpHeaders.HeaderEntry current = head;
    
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
  
  private final class HeaderEntry implements Map.Entry<String, String> {
    final int hash;
    final CharSequence key;
    CharSequence value;
    HeaderEntry next;
    HeaderEntry before;
    HeaderEntry after;
    
    HeaderEntry(int hash, CharSequence key, CharSequence value) {
      this.hash = hash;
      this.key = key;
      this.value = value;
    }
    
    HeaderEntry() {
      hash = -1;
      key = null;
      value = null;
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
      return key.toString();
    }
    
    public String getValue()
    {
      return value.toString();
    }
    
    public String setValue(String value)
    {
      if (value == null) {
        throw new NullPointerException("value");
      }
      HttpHeaders.validateHeaderValue(value);
      CharSequence oldValue = this.value;
      this.value = value;
      return oldValue.toString();
    }
    
    public String toString()
    {
      return key.toString() + '=' + value.toString();
    }
    
    void encode(ByteBuf buf) {
      HttpHeaders.encode(key, value, buf);
    }
  }
}
