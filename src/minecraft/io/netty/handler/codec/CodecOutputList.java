package io.netty.handler.codec;

import io.netty.util.Recycler;
import io.netty.util.Recycler.Handle;
import io.netty.util.internal.ObjectUtil;
import java.util.AbstractList;
import java.util.RandomAccess;


















final class CodecOutputList
  extends AbstractList<Object>
  implements RandomAccess
{
  private static final Recycler<CodecOutputList> RECYCLER = new Recycler()
  {

    protected CodecOutputList newObject(Recycler.Handle<CodecOutputList> handle) { return new CodecOutputList(handle, null); }
  };
  private final Recycler.Handle<CodecOutputList> handle;
  private int size;
  
  static CodecOutputList newInstance() { return (CodecOutputList)RECYCLER.get(); }
  




  private Object[] array = new Object[16];
  private boolean insertSinceRecycled;
  
  private CodecOutputList(Recycler.Handle<CodecOutputList> handle) {
    this.handle = handle;
  }
  
  public Object get(int index)
  {
    checkIndex(index);
    return array[index];
  }
  
  public int size()
  {
    return size;
  }
  
  public boolean add(Object element)
  {
    ObjectUtil.checkNotNull(element, "element");
    try {
      insert(size, element);
    }
    catch (IndexOutOfBoundsException ignore) {
      expandArray();
      insert(size, element);
    }
    size += 1;
    return true;
  }
  
  public Object set(int index, Object element)
  {
    ObjectUtil.checkNotNull(element, "element");
    checkIndex(index);
    
    Object old = array[index];
    insert(index, element);
    return old;
  }
  
  public void add(int index, Object element)
  {
    ObjectUtil.checkNotNull(element, "element");
    checkIndex(index);
    
    if (size == array.length) {
      expandArray();
    }
    
    if (index != size - 1) {
      System.arraycopy(array, index, array, index + 1, size - index);
    }
    
    insert(index, element);
    size += 1;
  }
  
  public Object remove(int index)
  {
    checkIndex(index);
    Object old = array[index];
    
    int len = size - index - 1;
    if (len > 0) {
      System.arraycopy(array, index + 1, array, index, len);
    }
    array[(--size)] = null;
    
    return old;
  }
  


  public void clear()
  {
    size = 0;
  }
  


  boolean insertSinceRecycled()
  {
    return insertSinceRecycled;
  }
  


  void recycle()
  {
    for (int i = 0; i < size; i++) {
      array[i] = null;
    }
    clear();
    insertSinceRecycled = false;
    handle.recycle(this);
  }
  


  Object getUnsafe(int index)
  {
    return array[index];
  }
  
  private void checkIndex(int index) {
    if (index >= size) {
      throw new IndexOutOfBoundsException();
    }
  }
  
  private void insert(int index, Object element) {
    array[index] = element;
    insertSinceRecycled = true;
  }
  
  private void expandArray()
  {
    int newCapacity = array.length << 1;
    
    if (newCapacity < 0) {
      throw new OutOfMemoryError();
    }
    
    Object[] newArray = new Object[newCapacity];
    System.arraycopy(array, 0, newArray, 0, array.length);
    
    array = newArray;
  }
}
