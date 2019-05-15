package io.netty.handler.codec.http2.internal.hpack;









final class DynamicTable
{
  HeaderField[] headerFields;
  






  int head;
  






  int tail;
  






  private long size;
  






  private long capacity = -1L;
  


  DynamicTable(long initialCapacity)
  {
    setCapacity(initialCapacity);
  }
  

  public int length()
  {
    int length;
    int length;
    if (head < tail) {
      length = headerFields.length - tail + head;
    } else {
      length = head - tail;
    }
    return length;
  }
  


  public long size()
  {
    return size;
  }
  


  public long capacity()
  {
    return capacity;
  }
  



  public HeaderField getEntry(int index)
  {
    if ((index <= 0) || (index > length())) {
      throw new IndexOutOfBoundsException();
    }
    int i = head - index;
    if (i < 0) {
      return headerFields[(i + headerFields.length)];
    }
    return headerFields[i];
  }
  






  public void add(HeaderField header)
  {
    int headerSize = header.size();
    if (headerSize > capacity) {
      clear();
      return;
    }
    while (capacity - size < headerSize) {
      remove();
    }
    headerFields[(head++)] = header;
    size += header.size();
    if (head == headerFields.length) {
      head = 0;
    }
  }
  


  public HeaderField remove()
  {
    HeaderField removed = headerFields[tail];
    if (removed == null) {
      return null;
    }
    size -= removed.size();
    headerFields[(tail++)] = null;
    if (tail == headerFields.length) {
      tail = 0;
    }
    return removed;
  }
  


  public void clear()
  {
    while (tail != head) {
      headerFields[(tail++)] = null;
      if (tail == headerFields.length) {
        tail = 0;
      }
    }
    head = 0;
    tail = 0;
    size = 0L;
  }
  



  public void setCapacity(long capacity)
  {
    if ((capacity < 0L) || (capacity > 4294967295L)) {
      throw new IllegalArgumentException("capacity is invalid: " + capacity);
    }
    
    if (this.capacity == capacity) {
      return;
    }
    this.capacity = capacity;
    
    if (capacity == 0L) {
      clear();
    }
    else {
      while (size > capacity) {
        remove();
      }
    }
    
    int maxEntries = (int)(capacity / 32L);
    if (capacity % 32L != 0L) {
      maxEntries++;
    }
    

    if ((headerFields != null) && (headerFields.length == maxEntries)) {
      return;
    }
    
    HeaderField[] tmp = new HeaderField[maxEntries];
    

    int len = length();
    int cursor = tail;
    for (int i = 0; i < len; i++) {
      HeaderField entry = headerFields[(cursor++)];
      tmp[i] = entry;
      if (cursor == headerFields.length) {
        cursor = 0;
      }
    }
    
    tail = 0;
    head = (tail + len);
    headerFields = tmp;
  }
}
