package gnu.trove.list.linked;

import gnu.trove.list.TLinkable;
import gnu.trove.procedure.TObjectProcedure;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Array;
import java.util.AbstractSequentialList;
import java.util.ListIterator;
import java.util.NoSuchElementException;


























































public class TLinkedList<T extends TLinkable<T>>
  extends AbstractSequentialList<T>
  implements Externalizable
{
  static final long serialVersionUID = 1L;
  protected T _head;
  protected T _tail;
  protected int _size = 0;
  








  public TLinkedList() {}
  








  public ListIterator<T> listIterator(int index)
  {
    return new IteratorImpl(index);
  }
  





  public int size()
  {
    return _size;
  }
  








  public void add(int index, T linkable)
  {
    if ((index < 0) || (index > size())) {
      throw new IndexOutOfBoundsException("index:" + index);
    }
    insert(index, linkable);
  }
  






  public boolean add(T linkable)
  {
    insert(_size, linkable);
    return true;
  }
  





  public void addFirst(T linkable)
  {
    insert(0, linkable);
  }
  





  public void addLast(T linkable)
  {
    insert(size(), linkable);
  }
  

  public void clear()
  {
    if (null != _head) {
      for (TLinkable<T> link = _head.getNext(); 
          link != null; 
          link = link.getNext()) {
        TLinkable<T> prev = link.getPrevious();
        prev.setNext(null);
        link.setPrevious(null);
      }
      _head = (this._tail = null);
    }
    _size = 0;
  }
  











  public Object[] toArray()
  {
    Object[] o = new Object[_size];
    int i = 0;
    for (TLinkable link = _head; link != null; link = link.getNext()) {
      o[(i++)] = link;
    }
    return o;
  }
  










  public Object[] toUnlinkedArray()
  {
    Object[] o = new Object[_size];
    int i = 0;
    for (TLinkable<T> link = _head; link != null; i++) {
      o[i] = link;
      TLinkable<T> tmp = link;
      link = link.getNext();
      tmp.setNext(null);
      tmp.setPrevious(null);
    }
    _size = 0;
    _head = (this._tail = null);
    return o;
  }
  







  public T[] toUnlinkedArray(T[] a)
  {
    int size = size();
    if (a.length < size) {
      a = (TLinkable[])Array.newInstance(a.getClass().getComponentType(), size);
    }
    
    int i = 0;
    for (T link = _head; link != null; i++) {
      a[i] = link;
      T tmp = link;
      link = link.getNext();
      tmp.setNext(null);
      tmp.setPrevious(null);
    }
    _size = 0;
    _head = (this._tail = null);
    return a;
  }
  






  public boolean contains(Object o)
  {
    for (TLinkable<T> link = _head; link != null; link = link.getNext()) {
      if (o.equals(link)) {
        return true;
      }
    }
    return false;
  }
  




  public T get(int index)
  {
    if ((index < 0) || (index >= _size)) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + _size);
    }
    

    if (index > _size >> 1) {
      int position = _size - 1;
      T node = _tail;
      
      while (position > index) {
        node = node.getPrevious();
        position--;
      }
      
      return node;
    }
    int position = 0;
    T node = _head;
    
    while (position < index) {
      node = node.getNext();
      position++;
    }
    
    return node;
  }
  






  public T getFirst()
  {
    return _head;
  }
  





  public T getLast()
  {
    return _tail;
  }
  
















  public T getNext(T current)
  {
    return current.getNext();
  }
  
















  public T getPrevious(T current)
  {
    return current.getPrevious();
  }
  






  public T removeFirst()
  {
    T o = _head;
    
    if (o == null) {
      return null;
    }
    
    T n = o.getNext();
    o.setNext(null);
    
    if (null != n) {
      n.setPrevious(null);
    }
    
    _head = n;
    if (--_size == 0) {
      _tail = null;
    }
    return o;
  }
  






  public T removeLast()
  {
    T o = _tail;
    
    if (o == null) {
      return null;
    }
    
    T prev = o.getPrevious();
    o.setPrevious(null);
    
    if (null != prev) {
      prev.setNext(null);
    }
    _tail = prev;
    if (--_size == 0) {
      _head = null;
    }
    return o;
  }
  








  protected void insert(int index, T linkable)
  {
    if (_size == 0) {
      _head = (this._tail = linkable);
    } else if (index == 0) {
      linkable.setNext(_head);
      _head.setPrevious(linkable);
      _head = linkable;
    } else if (index == _size) {
      _tail.setNext(linkable);
      linkable.setPrevious(_tail);
      _tail = linkable;
    } else {
      T node = get(index);
      
      T before = node.getPrevious();
      if (before != null) {
        before.setNext(linkable);
      }
      
      linkable.setPrevious(before);
      linkable.setNext(node);
      node.setPrevious(linkable);
    }
    _size += 1;
  }
  










  public boolean remove(Object o)
  {
    if ((o instanceof TLinkable))
    {
      TLinkable<T> link = (TLinkable)o;
      
      T p = link.getPrevious();
      T n = link.getNext();
      
      if ((n == null) && (p == null))
      {


        if (o != _head) {
          return false;
        }
        
        _head = (this._tail = null);
      } else if (n == null)
      {
        link.setPrevious(null);
        p.setNext(null);
        _tail = p;
      } else if (p == null)
      {
        link.setNext(null);
        n.setPrevious(null);
        _head = n;
      } else {
        p.setNext(n);
        n.setPrevious(p);
        link.setNext(null);
        link.setPrevious(null);
      }
      
      _size -= 1;
      return true;
    }
    return false;
  }
  










  public void addBefore(T current, T newElement)
  {
    if (current == _head) {
      addFirst(newElement);
    } else if (current == null) {
      addLast(newElement);
    } else {
      T p = current.getPrevious();
      newElement.setNext(current);
      p.setNext(newElement);
      newElement.setPrevious(p);
      current.setPrevious(newElement);
      _size += 1;
    }
  }
  









  public void addAfter(T current, T newElement)
  {
    if (current == _tail) {
      addLast(newElement);
    } else if (current == null) {
      addFirst(newElement);
    } else {
      T n = current.getNext();
      newElement.setPrevious(current);
      newElement.setNext(n);
      current.setNext(newElement);
      n.setPrevious(newElement);
      _size += 1;
    }
  }
  








  public boolean forEachValue(TObjectProcedure<T> procedure)
  {
    T node = _head;
    while (node != null) {
      boolean keep_going = procedure.execute(node);
      if (!keep_going) {
        return false;
      }
      
      node = node.getNext();
    }
    
    return true;
  }
  
  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeByte(0);
    

    out.writeInt(_size);
    

    out.writeObject(_head);
    

    out.writeObject(_tail);
  }
  



  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    in.readByte();
    

    _size = in.readInt();
    

    _head = ((TLinkable)in.readObject());
    

    _tail = ((TLinkable)in.readObject());
  }
  

  protected final class IteratorImpl
    implements ListIterator<T>
  {
    private int _nextIndex = 0;
    


    private T _next;
    

    private T _lastReturned;
    


    IteratorImpl(int position)
    {
      if ((position < 0) || (position > _size)) {
        throw new IndexOutOfBoundsException();
      }
      
      _nextIndex = position;
      if (position == 0) {
        _next = _head;
      } else if (position == _size) {
        _next = null; } else { int pos;
        if (position < _size >> 1) {
          pos = 0;
          for (_next = _head; pos < position; pos++) {
            _next = _next.getNext();
          }
        } else {
          int pos = _size - 1;
          for (_next = _tail; pos > position; pos--) {
            _next = _next.getPrevious();
          }
        }
      }
    }
    





    public final void add(T linkable)
    {
      _lastReturned = null;
      _nextIndex += 1;
      
      if (_size == 0) {
        add(linkable);
      } else {
        addBefore(_next, linkable);
      }
    }
    





    public final boolean hasNext()
    {
      return _nextIndex != _size;
    }
    





    public final boolean hasPrevious()
    {
      return _nextIndex != 0;
    }
    








    public final T next()
    {
      if (_nextIndex == _size) {
        throw new NoSuchElementException();
      }
      
      _lastReturned = _next;
      _next = _next.getNext();
      _nextIndex += 1;
      return _lastReturned;
    }
    






    public final int nextIndex()
    {
      return _nextIndex;
    }
    








    public final T previous()
    {
      if (_nextIndex == 0) {
        throw new NoSuchElementException();
      }
      
      if (_nextIndex == _size) {
        _lastReturned = (this._next = _tail);
      } else {
        _lastReturned = (this._next = _next.getPrevious());
      }
      
      _nextIndex -= 1;
      return _lastReturned;
    }
    





    public final int previousIndex()
    {
      return _nextIndex - 1;
    }
    









    public final void remove()
    {
      if (_lastReturned == null) {
        throw new IllegalStateException("must invoke next or previous before invoking remove");
      }
      
      if (_lastReturned != _next) {
        _nextIndex -= 1;
      }
      _next = _lastReturned.getNext();
      remove(_lastReturned);
      _lastReturned = null;
    }
    






    public final void set(T linkable)
    {
      if (_lastReturned == null) {
        throw new IllegalStateException();
      }
      


      if (_lastReturned == _head) {
        _head = linkable;
      }
      
      if (_lastReturned == _tail) {
        _tail = linkable;
      }
      
      swap(_lastReturned, linkable);
      _lastReturned = linkable;
    }
    






    private void swap(T from, T to)
    {
      T p = from.getPrevious();
      T n = from.getNext();
      
      if (null != p) {
        to.setPrevious(p);
        p.setNext(to);
      }
      if (null != n) {
        to.setNext(n);
        n.setPrevious(to);
      }
      from.setNext(null);
      from.setPrevious(null);
    }
  }
}
