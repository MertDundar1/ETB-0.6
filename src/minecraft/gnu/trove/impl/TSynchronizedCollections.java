package gnu.trove.impl;

import gnu.trove.TByteCollection;
import gnu.trove.TCharCollection;
import gnu.trove.TDoubleCollection;
import gnu.trove.TFloatCollection;
import gnu.trove.TIntCollection;
import gnu.trove.TLongCollection;
import gnu.trove.TShortCollection;
import gnu.trove.list.TByteList;
import gnu.trove.list.TCharList;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.TFloatList;
import gnu.trove.list.TIntList;
import gnu.trove.list.TLongList;
import gnu.trove.list.TShortList;
import gnu.trove.map.TByteByteMap;
import gnu.trove.map.TByteCharMap;
import gnu.trove.map.TByteDoubleMap;
import gnu.trove.map.TByteFloatMap;
import gnu.trove.map.TByteIntMap;
import gnu.trove.map.TByteShortMap;
import gnu.trove.map.TCharCharMap;
import gnu.trove.map.TCharDoubleMap;
import gnu.trove.map.TCharLongMap;
import gnu.trove.map.TCharShortMap;
import gnu.trove.map.TDoubleCharMap;
import gnu.trove.map.TDoubleDoubleMap;
import gnu.trove.map.TDoubleFloatMap;
import gnu.trove.map.TDoubleIntMap;
import gnu.trove.map.TDoubleLongMap;
import gnu.trove.map.TDoubleShortMap;
import gnu.trove.map.TFloatByteMap;
import gnu.trove.map.TFloatCharMap;
import gnu.trove.map.TFloatDoubleMap;
import gnu.trove.map.TFloatFloatMap;
import gnu.trove.map.TFloatLongMap;
import gnu.trove.map.TFloatShortMap;
import gnu.trove.map.TIntCharMap;
import gnu.trove.map.TIntFloatMap;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TLongByteMap;
import gnu.trove.map.TLongIntMap;
import gnu.trove.map.TLongLongMap;
import gnu.trove.map.TLongShortMap;
import gnu.trove.map.TShortDoubleMap;
import gnu.trove.map.TShortLongMap;
import gnu.trove.map.TShortShortMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TShortProcedure;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

public class TSynchronizedCollections
{
  private TSynchronizedCollections() {}
  
  public static class TSynchronizedDoubleCollection implements TDoubleCollection, Serializable
  {
    private static final long serialVersionUID = 3053995032091335093L;
    final TDoubleCollection c;
    final Object mutex;
    
    public TSynchronizedDoubleCollection(TDoubleCollection c)
    {
      if (c == null)
        throw new NullPointerException();
      this.c = c;
      mutex = this;
    }
    
    public TSynchronizedDoubleCollection(TDoubleCollection c, Object mutex) { this.c = c;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return c.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return c.isEmpty();
      } }
    
    public boolean contains(double o) { synchronized (mutex) { return c.contains(o);
      } }
    
    public double[] toArray() { synchronized (mutex) { return c.toArray();
      } }
    
    public double[] toArray(double[] a) { synchronized (mutex) { return c.toArray(a);
      }
    }
    
    public gnu.trove.iterator.TDoubleIterator iterator() { return c.iterator(); }
    
    public boolean add(double e)
    {
      synchronized (mutex) { return c.add(e);
      } }
    
    public boolean remove(double o) { synchronized (mutex) { return c.remove(o);
      }
    }
    
    public boolean containsAll(Collection<?> coll) { synchronized (mutex) { return c.containsAll(coll);
      } }
    
    public boolean containsAll(TDoubleCollection coll) { synchronized (mutex) { return c.containsAll(coll);
      } }
    
    public boolean containsAll(double[] array) { synchronized (mutex) { return c.containsAll(array);
      }
    }
    
    public boolean addAll(Collection<? extends Double> coll) { synchronized (mutex) { return c.addAll(coll);
      } }
    
    public boolean addAll(TDoubleCollection coll) { synchronized (mutex) { return c.addAll(coll);
      } }
    
    public boolean addAll(double[] array) { synchronized (mutex) { return c.addAll(array);
      }
    }
    
    public boolean removeAll(Collection<?> coll) { synchronized (mutex) { return c.removeAll(coll);
      } }
    
    public boolean removeAll(TDoubleCollection coll) { synchronized (mutex) { return c.removeAll(coll);
      } }
    
    public boolean removeAll(double[] array) { synchronized (mutex) { return c.removeAll(array);
      }
    }
    
    public boolean retainAll(Collection<?> coll) { synchronized (mutex) { return c.retainAll(coll);
      } }
    
    public boolean retainAll(TDoubleCollection coll) { synchronized (mutex) { return c.retainAll(coll);
      } }
    
    public boolean retainAll(double[] array) { synchronized (mutex) { return c.retainAll(array);
      } }
    
    public double getNoEntryValue() { return c.getNoEntryValue(); }
    
    public boolean forEach(TDoubleProcedure procedure) { synchronized (mutex) { return c.forEach(procedure);
      }
    }
    
    public void clear() { synchronized (mutex) { c.clear();
      } }
    
    public String toString() { synchronized (mutex) { return c.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedFloatCollection
    implements TFloatCollection, Serializable
  {
    private static final long serialVersionUID = 3053995032091335093L;
    
    final TFloatCollection c;
    final Object mutex;
    
    public TSynchronizedFloatCollection(TFloatCollection c)
    {
      if (c == null)
        throw new NullPointerException();
      this.c = c;
      mutex = this;
    }
    
    public TSynchronizedFloatCollection(TFloatCollection c, Object mutex) { this.c = c;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return c.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return c.isEmpty();
      } }
    
    public boolean contains(float o) { synchronized (mutex) { return c.contains(o);
      } }
    
    public float[] toArray() { synchronized (mutex) { return c.toArray();
      } }
    
    public float[] toArray(float[] a) { synchronized (mutex) { return c.toArray(a);
      }
    }
    
    public gnu.trove.iterator.TFloatIterator iterator() { return c.iterator(); }
    
    public boolean add(float e)
    {
      synchronized (mutex) { return c.add(e);
      } }
    
    public boolean remove(float o) { synchronized (mutex) { return c.remove(o);
      }
    }
    
    public boolean containsAll(Collection<?> coll) { synchronized (mutex) { return c.containsAll(coll);
      } }
    
    public boolean containsAll(TFloatCollection coll) { synchronized (mutex) { return c.containsAll(coll);
      } }
    
    public boolean containsAll(float[] array) { synchronized (mutex) { return c.containsAll(array);
      }
    }
    
    public boolean addAll(Collection<? extends Float> coll) { synchronized (mutex) { return c.addAll(coll);
      } }
    
    public boolean addAll(TFloatCollection coll) { synchronized (mutex) { return c.addAll(coll);
      } }
    
    public boolean addAll(float[] array) { synchronized (mutex) { return c.addAll(array);
      }
    }
    
    public boolean removeAll(Collection<?> coll) { synchronized (mutex) { return c.removeAll(coll);
      } }
    
    public boolean removeAll(TFloatCollection coll) { synchronized (mutex) { return c.removeAll(coll);
      } }
    
    public boolean removeAll(float[] array) { synchronized (mutex) { return c.removeAll(array);
      }
    }
    
    public boolean retainAll(Collection<?> coll) { synchronized (mutex) { return c.retainAll(coll);
      } }
    
    public boolean retainAll(TFloatCollection coll) { synchronized (mutex) { return c.retainAll(coll);
      } }
    
    public boolean retainAll(float[] array) { synchronized (mutex) { return c.retainAll(array);
      } }
    
    public float getNoEntryValue() { return c.getNoEntryValue(); }
    
    public boolean forEach(TFloatProcedure procedure) { synchronized (mutex) { return c.forEach(procedure);
      }
    }
    
    public void clear() { synchronized (mutex) { c.clear();
      } }
    
    public String toString() { synchronized (mutex) { return c.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedIntCollection
    implements TIntCollection, Serializable
  {
    private static final long serialVersionUID = 3053995032091335093L;
    
    final TIntCollection c;
    final Object mutex;
    
    public TSynchronizedIntCollection(TIntCollection c)
    {
      if (c == null)
        throw new NullPointerException();
      this.c = c;
      mutex = this;
    }
    
    public TSynchronizedIntCollection(TIntCollection c, Object mutex) { this.c = c;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return c.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return c.isEmpty();
      } }
    
    public boolean contains(int o) { synchronized (mutex) { return c.contains(o);
      } }
    
    public int[] toArray() { synchronized (mutex) { return c.toArray();
      } }
    
    public int[] toArray(int[] a) { synchronized (mutex) { return c.toArray(a);
      }
    }
    
    public gnu.trove.iterator.TIntIterator iterator() { return c.iterator(); }
    
    public boolean add(int e)
    {
      synchronized (mutex) { return c.add(e);
      } }
    
    public boolean remove(int o) { synchronized (mutex) { return c.remove(o);
      }
    }
    
    public boolean containsAll(Collection<?> coll) { synchronized (mutex) { return c.containsAll(coll);
      } }
    
    public boolean containsAll(TIntCollection coll) { synchronized (mutex) { return c.containsAll(coll);
      } }
    
    public boolean containsAll(int[] array) { synchronized (mutex) { return c.containsAll(array);
      }
    }
    
    public boolean addAll(Collection<? extends Integer> coll) { synchronized (mutex) { return c.addAll(coll);
      } }
    
    public boolean addAll(TIntCollection coll) { synchronized (mutex) { return c.addAll(coll);
      } }
    
    public boolean addAll(int[] array) { synchronized (mutex) { return c.addAll(array);
      }
    }
    
    public boolean removeAll(Collection<?> coll) { synchronized (mutex) { return c.removeAll(coll);
      } }
    
    public boolean removeAll(TIntCollection coll) { synchronized (mutex) { return c.removeAll(coll);
      } }
    
    public boolean removeAll(int[] array) { synchronized (mutex) { return c.removeAll(array);
      }
    }
    
    public boolean retainAll(Collection<?> coll) { synchronized (mutex) { return c.retainAll(coll);
      } }
    
    public boolean retainAll(TIntCollection coll) { synchronized (mutex) { return c.retainAll(coll);
      } }
    
    public boolean retainAll(int[] array) { synchronized (mutex) { return c.retainAll(array);
      } }
    
    public int getNoEntryValue() { return c.getNoEntryValue(); }
    
    public boolean forEach(TIntProcedure procedure) { synchronized (mutex) { return c.forEach(procedure);
      }
    }
    
    public void clear() { synchronized (mutex) { c.clear();
      } }
    
    public String toString() { synchronized (mutex) { return c.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedLongCollection
    implements TLongCollection, Serializable
  {
    private static final long serialVersionUID = 3053995032091335093L;
    
    final TLongCollection c;
    final Object mutex;
    
    public TSynchronizedLongCollection(TLongCollection c)
    {
      if (c == null)
        throw new NullPointerException();
      this.c = c;
      mutex = this;
    }
    
    public TSynchronizedLongCollection(TLongCollection c, Object mutex) { this.c = c;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return c.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return c.isEmpty();
      } }
    
    public boolean contains(long o) { synchronized (mutex) { return c.contains(o);
      } }
    
    public long[] toArray() { synchronized (mutex) { return c.toArray();
      } }
    
    public long[] toArray(long[] a) { synchronized (mutex) { return c.toArray(a);
      }
    }
    
    public gnu.trove.iterator.TLongIterator iterator() { return c.iterator(); }
    
    public boolean add(long e)
    {
      synchronized (mutex) { return c.add(e);
      } }
    
    public boolean remove(long o) { synchronized (mutex) { return c.remove(o);
      }
    }
    
    public boolean containsAll(Collection<?> coll) { synchronized (mutex) { return c.containsAll(coll);
      } }
    
    public boolean containsAll(TLongCollection coll) { synchronized (mutex) { return c.containsAll(coll);
      } }
    
    public boolean containsAll(long[] array) { synchronized (mutex) { return c.containsAll(array);
      }
    }
    
    public boolean addAll(Collection<? extends Long> coll) { synchronized (mutex) { return c.addAll(coll);
      } }
    
    public boolean addAll(TLongCollection coll) { synchronized (mutex) { return c.addAll(coll);
      } }
    
    public boolean addAll(long[] array) { synchronized (mutex) { return c.addAll(array);
      }
    }
    
    public boolean removeAll(Collection<?> coll) { synchronized (mutex) { return c.removeAll(coll);
      } }
    
    public boolean removeAll(TLongCollection coll) { synchronized (mutex) { return c.removeAll(coll);
      } }
    
    public boolean removeAll(long[] array) { synchronized (mutex) { return c.removeAll(array);
      }
    }
    
    public boolean retainAll(Collection<?> coll) { synchronized (mutex) { return c.retainAll(coll);
      } }
    
    public boolean retainAll(TLongCollection coll) { synchronized (mutex) { return c.retainAll(coll);
      } }
    
    public boolean retainAll(long[] array) { synchronized (mutex) { return c.retainAll(array);
      } }
    
    public long getNoEntryValue() { return c.getNoEntryValue(); }
    
    public boolean forEach(TLongProcedure procedure) { synchronized (mutex) { return c.forEach(procedure);
      }
    }
    
    public void clear() { synchronized (mutex) { c.clear();
      } }
    
    public String toString() { synchronized (mutex) { return c.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedByteCollection
    implements TByteCollection, Serializable
  {
    private static final long serialVersionUID = 3053995032091335093L;
    
    final TByteCollection c;
    final Object mutex;
    
    public TSynchronizedByteCollection(TByteCollection c)
    {
      if (c == null)
        throw new NullPointerException();
      this.c = c;
      mutex = this;
    }
    
    public TSynchronizedByteCollection(TByteCollection c, Object mutex) { this.c = c;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return c.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return c.isEmpty();
      } }
    
    public boolean contains(byte o) { synchronized (mutex) { return c.contains(o);
      } }
    
    public byte[] toArray() { synchronized (mutex) { return c.toArray();
      } }
    
    public byte[] toArray(byte[] a) { synchronized (mutex) { return c.toArray(a);
      }
    }
    
    public gnu.trove.iterator.TByteIterator iterator() { return c.iterator(); }
    
    public boolean add(byte e)
    {
      synchronized (mutex) { return c.add(e);
      } }
    
    public boolean remove(byte o) { synchronized (mutex) { return c.remove(o);
      }
    }
    
    public boolean containsAll(Collection<?> coll) { synchronized (mutex) { return c.containsAll(coll);
      } }
    
    public boolean containsAll(TByteCollection coll) { synchronized (mutex) { return c.containsAll(coll);
      } }
    
    public boolean containsAll(byte[] array) { synchronized (mutex) { return c.containsAll(array);
      }
    }
    
    public boolean addAll(Collection<? extends Byte> coll) { synchronized (mutex) { return c.addAll(coll);
      } }
    
    public boolean addAll(TByteCollection coll) { synchronized (mutex) { return c.addAll(coll);
      } }
    
    public boolean addAll(byte[] array) { synchronized (mutex) { return c.addAll(array);
      }
    }
    
    public boolean removeAll(Collection<?> coll) { synchronized (mutex) { return c.removeAll(coll);
      } }
    
    public boolean removeAll(TByteCollection coll) { synchronized (mutex) { return c.removeAll(coll);
      } }
    
    public boolean removeAll(byte[] array) { synchronized (mutex) { return c.removeAll(array);
      }
    }
    
    public boolean retainAll(Collection<?> coll) { synchronized (mutex) { return c.retainAll(coll);
      } }
    
    public boolean retainAll(TByteCollection coll) { synchronized (mutex) { return c.retainAll(coll);
      } }
    
    public boolean retainAll(byte[] array) { synchronized (mutex) { return c.retainAll(array);
      } }
    
    public byte getNoEntryValue() { return c.getNoEntryValue(); }
    
    public boolean forEach(TByteProcedure procedure) { synchronized (mutex) { return c.forEach(procedure);
      }
    }
    
    public void clear() { synchronized (mutex) { c.clear();
      } }
    
    public String toString() { synchronized (mutex) { return c.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedShortCollection
    implements TShortCollection, Serializable
  {
    private static final long serialVersionUID = 3053995032091335093L;
    
    final TShortCollection c;
    final Object mutex;
    
    public TSynchronizedShortCollection(TShortCollection c)
    {
      if (c == null)
        throw new NullPointerException();
      this.c = c;
      mutex = this;
    }
    
    public TSynchronizedShortCollection(TShortCollection c, Object mutex) { this.c = c;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return c.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return c.isEmpty();
      } }
    
    public boolean contains(short o) { synchronized (mutex) { return c.contains(o);
      } }
    
    public short[] toArray() { synchronized (mutex) { return c.toArray();
      } }
    
    public short[] toArray(short[] a) { synchronized (mutex) { return c.toArray(a);
      }
    }
    
    public gnu.trove.iterator.TShortIterator iterator() { return c.iterator(); }
    
    public boolean add(short e)
    {
      synchronized (mutex) { return c.add(e);
      } }
    
    public boolean remove(short o) { synchronized (mutex) { return c.remove(o);
      }
    }
    
    public boolean containsAll(Collection<?> coll) { synchronized (mutex) { return c.containsAll(coll);
      } }
    
    public boolean containsAll(TShortCollection coll) { synchronized (mutex) { return c.containsAll(coll);
      } }
    
    public boolean containsAll(short[] array) { synchronized (mutex) { return c.containsAll(array);
      }
    }
    
    public boolean addAll(Collection<? extends Short> coll) { synchronized (mutex) { return c.addAll(coll);
      } }
    
    public boolean addAll(TShortCollection coll) { synchronized (mutex) { return c.addAll(coll);
      } }
    
    public boolean addAll(short[] array) { synchronized (mutex) { return c.addAll(array);
      }
    }
    
    public boolean removeAll(Collection<?> coll) { synchronized (mutex) { return c.removeAll(coll);
      } }
    
    public boolean removeAll(TShortCollection coll) { synchronized (mutex) { return c.removeAll(coll);
      } }
    
    public boolean removeAll(short[] array) { synchronized (mutex) { return c.removeAll(array);
      }
    }
    
    public boolean retainAll(Collection<?> coll) { synchronized (mutex) { return c.retainAll(coll);
      } }
    
    public boolean retainAll(TShortCollection coll) { synchronized (mutex) { return c.retainAll(coll);
      } }
    
    public boolean retainAll(short[] array) { synchronized (mutex) { return c.retainAll(array);
      } }
    
    public short getNoEntryValue() { return c.getNoEntryValue(); }
    
    public boolean forEach(TShortProcedure procedure) { synchronized (mutex) { return c.forEach(procedure);
      }
    }
    
    public void clear() { synchronized (mutex) { c.clear();
      } }
    
    public String toString() { synchronized (mutex) { return c.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedCharCollection
    implements TCharCollection, Serializable
  {
    private static final long serialVersionUID = 3053995032091335093L;
    
    final TCharCollection c;
    final Object mutex;
    
    public TSynchronizedCharCollection(TCharCollection c)
    {
      if (c == null)
        throw new NullPointerException();
      this.c = c;
      mutex = this;
    }
    
    public TSynchronizedCharCollection(TCharCollection c, Object mutex) { this.c = c;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return c.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return c.isEmpty();
      } }
    
    public boolean contains(char o) { synchronized (mutex) { return c.contains(o);
      } }
    
    public char[] toArray() { synchronized (mutex) { return c.toArray();
      } }
    
    public char[] toArray(char[] a) { synchronized (mutex) { return c.toArray(a);
      }
    }
    
    public gnu.trove.iterator.TCharIterator iterator() { return c.iterator(); }
    
    public boolean add(char e)
    {
      synchronized (mutex) { return c.add(e);
      } }
    
    public boolean remove(char o) { synchronized (mutex) { return c.remove(o);
      }
    }
    
    public boolean containsAll(Collection<?> coll) { synchronized (mutex) { return c.containsAll(coll);
      } }
    
    public boolean containsAll(TCharCollection coll) { synchronized (mutex) { return c.containsAll(coll);
      } }
    
    public boolean containsAll(char[] array) { synchronized (mutex) { return c.containsAll(array);
      }
    }
    
    public boolean addAll(Collection<? extends Character> coll) { synchronized (mutex) { return c.addAll(coll);
      } }
    
    public boolean addAll(TCharCollection coll) { synchronized (mutex) { return c.addAll(coll);
      } }
    
    public boolean addAll(char[] array) { synchronized (mutex) { return c.addAll(array);
      }
    }
    
    public boolean removeAll(Collection<?> coll) { synchronized (mutex) { return c.removeAll(coll);
      } }
    
    public boolean removeAll(TCharCollection coll) { synchronized (mutex) { return c.removeAll(coll);
      } }
    
    public boolean removeAll(char[] array) { synchronized (mutex) { return c.removeAll(array);
      }
    }
    
    public boolean retainAll(Collection<?> coll) { synchronized (mutex) { return c.retainAll(coll);
      } }
    
    public boolean retainAll(TCharCollection coll) { synchronized (mutex) { return c.retainAll(coll);
      } }
    
    public boolean retainAll(char[] array) { synchronized (mutex) { return c.retainAll(array);
      } }
    
    public char getNoEntryValue() { return c.getNoEntryValue(); }
    
    public boolean forEach(TCharProcedure procedure) { synchronized (mutex) { return c.forEach(procedure);
      }
    }
    
    public void clear() { synchronized (mutex) { c.clear();
      } }
    
    public String toString() { synchronized (mutex) { return c.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedDoubleSet
    extends TSynchronizedCollections.TSynchronizedDoubleCollection
    implements gnu.trove.set.TDoubleSet
  {
    private static final long serialVersionUID = 487447009682186044L;
    
    public TSynchronizedDoubleSet(gnu.trove.set.TDoubleSet s)
    {
      super();
    }
    
    public TSynchronizedDoubleSet(gnu.trove.set.TDoubleSet s, Object mutex) { super(mutex); }
    
    public boolean equals(Object o)
    {
      synchronized (mutex) { return c.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return c.hashCode();
      }
    }
  }
  


























  public static class TSynchronizedFloatSet
    extends TSynchronizedCollections.TSynchronizedFloatCollection
    implements gnu.trove.set.TFloatSet
  {
    private static final long serialVersionUID = 487447009682186044L;
    


























    public TSynchronizedFloatSet(gnu.trove.set.TFloatSet s)
    {
      super();
    }
    
    public TSynchronizedFloatSet(gnu.trove.set.TFloatSet s, Object mutex) { super(mutex); }
    
    public boolean equals(Object o)
    {
      synchronized (mutex) { return c.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return c.hashCode();
      }
    }
  }
  


























  public static class TSynchronizedIntSet
    extends TSynchronizedCollections.TSynchronizedIntCollection
    implements gnu.trove.set.TIntSet
  {
    private static final long serialVersionUID = 487447009682186044L;
    


























    public TSynchronizedIntSet(gnu.trove.set.TIntSet s)
    {
      super();
    }
    
    public TSynchronizedIntSet(gnu.trove.set.TIntSet s, Object mutex) { super(mutex); }
    
    public boolean equals(Object o)
    {
      synchronized (mutex) { return c.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return c.hashCode();
      }
    }
  }
  


























  public static class TSynchronizedLongSet
    extends TSynchronizedCollections.TSynchronizedLongCollection
    implements gnu.trove.set.TLongSet
  {
    private static final long serialVersionUID = 487447009682186044L;
    


























    public TSynchronizedLongSet(gnu.trove.set.TLongSet s)
    {
      super();
    }
    
    public TSynchronizedLongSet(gnu.trove.set.TLongSet s, Object mutex) { super(mutex); }
    
    public boolean equals(Object o)
    {
      synchronized (mutex) { return c.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return c.hashCode();
      }
    }
  }
  


























  public static class TSynchronizedByteSet
    extends TSynchronizedCollections.TSynchronizedByteCollection
    implements gnu.trove.set.TByteSet
  {
    private static final long serialVersionUID = 487447009682186044L;
    


























    public TSynchronizedByteSet(gnu.trove.set.TByteSet s)
    {
      super();
    }
    
    public TSynchronizedByteSet(gnu.trove.set.TByteSet s, Object mutex) { super(mutex); }
    
    public boolean equals(Object o)
    {
      synchronized (mutex) { return c.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return c.hashCode();
      }
    }
  }
  


























  public static class TSynchronizedShortSet
    extends TSynchronizedCollections.TSynchronizedShortCollection
    implements gnu.trove.set.TShortSet
  {
    private static final long serialVersionUID = 487447009682186044L;
    


























    public TSynchronizedShortSet(gnu.trove.set.TShortSet s)
    {
      super();
    }
    
    public TSynchronizedShortSet(gnu.trove.set.TShortSet s, Object mutex) { super(mutex); }
    
    public boolean equals(Object o)
    {
      synchronized (mutex) { return c.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return c.hashCode();
      }
    }
  }
  


























  public static class TSynchronizedCharSet
    extends TSynchronizedCollections.TSynchronizedCharCollection
    implements gnu.trove.set.TCharSet
  {
    private static final long serialVersionUID = 487447009682186044L;
    


























    public TSynchronizedCharSet(gnu.trove.set.TCharSet s)
    {
      super();
    }
    
    public TSynchronizedCharSet(gnu.trove.set.TCharSet s, Object mutex) { super(mutex); }
    
    public boolean equals(Object o)
    {
      synchronized (mutex) { return c.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return c.hashCode();
      }
    }
  }
  



















  public static class TSynchronizedDoubleList
    extends TSynchronizedCollections.TSynchronizedDoubleCollection
    implements TDoubleList
  {
    static final long serialVersionUID = -7754090372962971524L;
    

















    final TDoubleList list;
    


















    public TSynchronizedDoubleList(TDoubleList list)
    {
      super();
      this.list = list;
    }
    
    public TSynchronizedDoubleList(TDoubleList list, Object mutex) { super(mutex);
      this.list = list;
    }
    
    public boolean equals(Object o) {
      synchronized (mutex) { return list.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return list.hashCode();
      }
    }
    
    public double get(int index) { synchronized (mutex) { return list.get(index);
      } }
    
    public void set(int index, double element) { synchronized (mutex) { list.set(index, element);
      } }
    
    public void set(int offset, double[] values) { synchronized (mutex) { list.set(offset, values);
      } }
    
    public void set(int offset, double[] values, int valOffset, int length) { synchronized (mutex) { list.set(offset, values, valOffset, length);
      }
    }
    
    public double replace(int offset, double val) { synchronized (mutex) { return list.replace(offset, val);
      } }
    
    public void remove(int offset, int length) { synchronized (mutex) { list.remove(offset, length);
      } }
    
    public double removeAt(int offset) { synchronized (mutex) { return list.removeAt(offset);
      }
    }
    
    public void add(double[] vals) { synchronized (mutex) { list.add(vals);
      } }
    
    public void add(double[] vals, int offset, int length) { synchronized (mutex) { list.add(vals, offset, length);
      }
    }
    
    public void insert(int offset, double value) { synchronized (mutex) { list.insert(offset, value);
      } }
    
    public void insert(int offset, double[] values) { synchronized (mutex) { list.insert(offset, values);
      } }
    
    public void insert(int offset, double[] values, int valOffset, int len) { synchronized (mutex) { list.insert(offset, values, valOffset, len);
      }
    }
    
    public int indexOf(double o) { synchronized (mutex) { return list.indexOf(o);
      } }
    
    public int lastIndexOf(double o) { synchronized (mutex) { return list.lastIndexOf(o);
      }
    }
    






    public TDoubleList subList(int fromIndex, int toIndex)
    {
      synchronized (mutex) {
        return new TSynchronizedDoubleList(list.subList(fromIndex, toIndex), mutex);
      }
    }
    
    public double[] toArray(int offset, int len)
    {
      synchronized (mutex) { return list.toArray(offset, len);
      } }
    
    public double[] toArray(double[] dest, int offset, int len) { synchronized (mutex) { return list.toArray(dest, offset, len);
      } }
    
    public double[] toArray(double[] dest, int source_pos, int dest_pos, int len) { synchronized (mutex) { return list.toArray(dest, source_pos, dest_pos, len);
      }
    }
    
    public int indexOf(int offset, double value) { synchronized (mutex) { return list.indexOf(offset, value);
      } }
    
    public int lastIndexOf(int offset, double value) { synchronized (mutex) { return list.lastIndexOf(offset, value);
      }
    }
    
    public void fill(double val) { synchronized (mutex) { list.fill(val);
      } }
    
    public void fill(int fromIndex, int toIndex, double val) { synchronized (mutex) { list.fill(fromIndex, toIndex, val);
      }
    }
    
    public void reverse() { synchronized (mutex) { list.reverse();
      } }
    
    public void reverse(int from, int to) { synchronized (mutex) { list.reverse(from, to);
      }
    }
    
    public void shuffle(java.util.Random rand) { synchronized (mutex) { list.shuffle(rand);
      }
    }
    
    public void sort() { synchronized (mutex) { list.sort();
      } }
    
    public void sort(int fromIndex, int toIndex) { synchronized (mutex) { list.sort(fromIndex, toIndex);
      }
    }
    
    public int binarySearch(double value) { synchronized (mutex) { return list.binarySearch(value);
      } }
    
    public int binarySearch(double value, int fromIndex, int toIndex) { synchronized (mutex) { return list.binarySearch(value, fromIndex, toIndex);
      }
    }
    
    public TDoubleList grep(TDoubleProcedure condition) { synchronized (mutex) { return list.grep(condition);
      } }
    
    public TDoubleList inverseGrep(TDoubleProcedure condition) { synchronized (mutex) { return list.inverseGrep(condition);
      } }
    
    public double max() { synchronized (mutex) { return list.max(); } }
    public double min() { synchronized (mutex) { return list.min();
      } }
    
    public boolean forEachDescending(TDoubleProcedure procedure) { synchronized (mutex) { return list.forEachDescending(procedure);
      }
    }
    
    public void transformValues(gnu.trove.function.TDoubleFunction function) { synchronized (mutex) { list.transformValues(function);
      }
    }
    










    private Object readResolve()
    {
      return (list instanceof java.util.RandomAccess) ? new TSynchronizedCollections.TSynchronizedRandomAccessDoubleList(list) : this;
    }
  }
  


  public static class TSynchronizedRandomAccessDoubleList
    extends TSynchronizedCollections.TSynchronizedDoubleList
    implements java.util.RandomAccess
  {
    static final long serialVersionUID = 1530674583602358482L;
    

    public TSynchronizedRandomAccessDoubleList(TDoubleList list)
    {
      super();
    }
    
    public TSynchronizedRandomAccessDoubleList(TDoubleList list, Object mutex) {
      super(mutex);
    }
    
    public TDoubleList subList(int fromIndex, int toIndex) {
      synchronized (mutex) {
        return new TSynchronizedRandomAccessDoubleList(list.subList(fromIndex, toIndex), mutex);
      }
    }
    









    private Object writeReplace()
    {
      return new TSynchronizedCollections.TSynchronizedDoubleList(list);
    }
  }
  

  public static class TSynchronizedFloatList
    extends TSynchronizedCollections.TSynchronizedFloatCollection
    implements TFloatList
  {
    static final long serialVersionUID = -7754090372962971524L;
    
    final TFloatList list;
    

    public TSynchronizedFloatList(TFloatList list)
    {
      super();
      this.list = list;
    }
    
    public TSynchronizedFloatList(TFloatList list, Object mutex) { super(mutex);
      this.list = list;
    }
    
    public boolean equals(Object o) {
      synchronized (mutex) { return list.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return list.hashCode();
      }
    }
    
    public float get(int index) { synchronized (mutex) { return list.get(index);
      } }
    
    public void set(int index, float element) { synchronized (mutex) { list.set(index, element);
      } }
    
    public void set(int offset, float[] values) { synchronized (mutex) { list.set(offset, values);
      } }
    
    public void set(int offset, float[] values, int valOffset, int length) { synchronized (mutex) { list.set(offset, values, valOffset, length);
      }
    }
    
    public float replace(int offset, float val) { synchronized (mutex) { return list.replace(offset, val);
      } }
    
    public void remove(int offset, int length) { synchronized (mutex) { list.remove(offset, length);
      } }
    
    public float removeAt(int offset) { synchronized (mutex) { return list.removeAt(offset);
      }
    }
    
    public void add(float[] vals) { synchronized (mutex) { list.add(vals);
      } }
    
    public void add(float[] vals, int offset, int length) { synchronized (mutex) { list.add(vals, offset, length);
      }
    }
    
    public void insert(int offset, float value) { synchronized (mutex) { list.insert(offset, value);
      } }
    
    public void insert(int offset, float[] values) { synchronized (mutex) { list.insert(offset, values);
      } }
    
    public void insert(int offset, float[] values, int valOffset, int len) { synchronized (mutex) { list.insert(offset, values, valOffset, len);
      }
    }
    
    public int indexOf(float o) { synchronized (mutex) { return list.indexOf(o);
      } }
    
    public int lastIndexOf(float o) { synchronized (mutex) { return list.lastIndexOf(o);
      }
    }
    






    public TFloatList subList(int fromIndex, int toIndex)
    {
      synchronized (mutex) {
        return new TSynchronizedFloatList(list.subList(fromIndex, toIndex), mutex);
      }
    }
    
    public float[] toArray(int offset, int len)
    {
      synchronized (mutex) { return list.toArray(offset, len);
      } }
    
    public float[] toArray(float[] dest, int offset, int len) { synchronized (mutex) { return list.toArray(dest, offset, len);
      } }
    
    public float[] toArray(float[] dest, int source_pos, int dest_pos, int len) { synchronized (mutex) { return list.toArray(dest, source_pos, dest_pos, len);
      }
    }
    
    public int indexOf(int offset, float value) { synchronized (mutex) { return list.indexOf(offset, value);
      } }
    
    public int lastIndexOf(int offset, float value) { synchronized (mutex) { return list.lastIndexOf(offset, value);
      }
    }
    
    public void fill(float val) { synchronized (mutex) { list.fill(val);
      } }
    
    public void fill(int fromIndex, int toIndex, float val) { synchronized (mutex) { list.fill(fromIndex, toIndex, val);
      }
    }
    
    public void reverse() { synchronized (mutex) { list.reverse();
      } }
    
    public void reverse(int from, int to) { synchronized (mutex) { list.reverse(from, to);
      }
    }
    
    public void shuffle(java.util.Random rand) { synchronized (mutex) { list.shuffle(rand);
      }
    }
    
    public void sort() { synchronized (mutex) { list.sort();
      } }
    
    public void sort(int fromIndex, int toIndex) { synchronized (mutex) { list.sort(fromIndex, toIndex);
      }
    }
    
    public int binarySearch(float value) { synchronized (mutex) { return list.binarySearch(value);
      } }
    
    public int binarySearch(float value, int fromIndex, int toIndex) { synchronized (mutex) { return list.binarySearch(value, fromIndex, toIndex);
      }
    }
    
    public TFloatList grep(TFloatProcedure condition) { synchronized (mutex) { return list.grep(condition);
      } }
    
    public TFloatList inverseGrep(TFloatProcedure condition) { synchronized (mutex) { return list.inverseGrep(condition);
      } }
    
    public float max() { synchronized (mutex) { return list.max(); } }
    public float min() { synchronized (mutex) { return list.min();
      } }
    
    public boolean forEachDescending(TFloatProcedure procedure) { synchronized (mutex) { return list.forEachDescending(procedure);
      }
    }
    
    public void transformValues(gnu.trove.function.TFloatFunction function) { synchronized (mutex) { list.transformValues(function);
      }
    }
    










    private Object readResolve()
    {
      return (list instanceof java.util.RandomAccess) ? new TSynchronizedCollections.TSynchronizedRandomAccessFloatList(list) : this;
    }
  }
  


  public static class TSynchronizedRandomAccessFloatList
    extends TSynchronizedCollections.TSynchronizedFloatList
    implements java.util.RandomAccess
  {
    static final long serialVersionUID = 1530674583602358482L;
    

    public TSynchronizedRandomAccessFloatList(TFloatList list)
    {
      super();
    }
    
    public TSynchronizedRandomAccessFloatList(TFloatList list, Object mutex) {
      super(mutex);
    }
    
    public TFloatList subList(int fromIndex, int toIndex) {
      synchronized (mutex) {
        return new TSynchronizedRandomAccessFloatList(list.subList(fromIndex, toIndex), mutex);
      }
    }
    









    private Object writeReplace()
    {
      return new TSynchronizedCollections.TSynchronizedFloatList(list);
    }
  }
  

  public static class TSynchronizedIntList
    extends TSynchronizedCollections.TSynchronizedIntCollection
    implements TIntList
  {
    static final long serialVersionUID = -7754090372962971524L;
    
    final TIntList list;
    

    public TSynchronizedIntList(TIntList list)
    {
      super();
      this.list = list;
    }
    
    public TSynchronizedIntList(TIntList list, Object mutex) { super(mutex);
      this.list = list;
    }
    
    public boolean equals(Object o) {
      synchronized (mutex) { return list.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return list.hashCode();
      }
    }
    
    public int get(int index) { synchronized (mutex) { return list.get(index);
      } }
    
    public void set(int index, int element) { synchronized (mutex) { list.set(index, element);
      } }
    
    public void set(int offset, int[] values) { synchronized (mutex) { list.set(offset, values);
      } }
    
    public void set(int offset, int[] values, int valOffset, int length) { synchronized (mutex) { list.set(offset, values, valOffset, length);
      }
    }
    
    public int replace(int offset, int val) { synchronized (mutex) { return list.replace(offset, val);
      } }
    
    public void remove(int offset, int length) { synchronized (mutex) { list.remove(offset, length);
      } }
    
    public int removeAt(int offset) { synchronized (mutex) { return list.removeAt(offset);
      }
    }
    
    public void add(int[] vals) { synchronized (mutex) { list.add(vals);
      } }
    
    public void add(int[] vals, int offset, int length) { synchronized (mutex) { list.add(vals, offset, length);
      }
    }
    
    public void insert(int offset, int value) { synchronized (mutex) { list.insert(offset, value);
      } }
    
    public void insert(int offset, int[] values) { synchronized (mutex) { list.insert(offset, values);
      } }
    
    public void insert(int offset, int[] values, int valOffset, int len) { synchronized (mutex) { list.insert(offset, values, valOffset, len);
      }
    }
    
    public int indexOf(int o) { synchronized (mutex) { return list.indexOf(o);
      } }
    
    public int lastIndexOf(int o) { synchronized (mutex) { return list.lastIndexOf(o);
      }
    }
    






    public TIntList subList(int fromIndex, int toIndex)
    {
      synchronized (mutex) {
        return new TSynchronizedIntList(list.subList(fromIndex, toIndex), mutex);
      }
    }
    
    public int[] toArray(int offset, int len)
    {
      synchronized (mutex) { return list.toArray(offset, len);
      } }
    
    public int[] toArray(int[] dest, int offset, int len) { synchronized (mutex) { return list.toArray(dest, offset, len);
      } }
    
    public int[] toArray(int[] dest, int source_pos, int dest_pos, int len) { synchronized (mutex) { return list.toArray(dest, source_pos, dest_pos, len);
      }
    }
    
    public int indexOf(int offset, int value) { synchronized (mutex) { return list.indexOf(offset, value);
      } }
    
    public int lastIndexOf(int offset, int value) { synchronized (mutex) { return list.lastIndexOf(offset, value);
      }
    }
    
    public void fill(int val) { synchronized (mutex) { list.fill(val);
      } }
    
    public void fill(int fromIndex, int toIndex, int val) { synchronized (mutex) { list.fill(fromIndex, toIndex, val);
      }
    }
    
    public void reverse() { synchronized (mutex) { list.reverse();
      } }
    
    public void reverse(int from, int to) { synchronized (mutex) { list.reverse(from, to);
      }
    }
    
    public void shuffle(java.util.Random rand) { synchronized (mutex) { list.shuffle(rand);
      }
    }
    
    public void sort() { synchronized (mutex) { list.sort();
      } }
    
    public void sort(int fromIndex, int toIndex) { synchronized (mutex) { list.sort(fromIndex, toIndex);
      }
    }
    
    public int binarySearch(int value) { synchronized (mutex) { return list.binarySearch(value);
      } }
    
    public int binarySearch(int value, int fromIndex, int toIndex) { synchronized (mutex) { return list.binarySearch(value, fromIndex, toIndex);
      }
    }
    
    public TIntList grep(TIntProcedure condition) { synchronized (mutex) { return list.grep(condition);
      } }
    
    public TIntList inverseGrep(TIntProcedure condition) { synchronized (mutex) { return list.inverseGrep(condition);
      } }
    
    public int max() { synchronized (mutex) { return list.max(); } }
    public int min() { synchronized (mutex) { return list.min();
      } }
    
    public boolean forEachDescending(TIntProcedure procedure) { synchronized (mutex) { return list.forEachDescending(procedure);
      }
    }
    
    public void transformValues(gnu.trove.function.TIntFunction function) { synchronized (mutex) { list.transformValues(function);
      }
    }
    










    private Object readResolve()
    {
      return (list instanceof java.util.RandomAccess) ? new TSynchronizedCollections.TSynchronizedRandomAccessIntList(list) : this;
    }
  }
  


  public static class TSynchronizedRandomAccessIntList
    extends TSynchronizedCollections.TSynchronizedIntList
    implements java.util.RandomAccess
  {
    static final long serialVersionUID = 1530674583602358482L;
    

    public TSynchronizedRandomAccessIntList(TIntList list)
    {
      super();
    }
    
    public TSynchronizedRandomAccessIntList(TIntList list, Object mutex) {
      super(mutex);
    }
    
    public TIntList subList(int fromIndex, int toIndex) {
      synchronized (mutex) {
        return new TSynchronizedRandomAccessIntList(list.subList(fromIndex, toIndex), mutex);
      }
    }
    









    private Object writeReplace()
    {
      return new TSynchronizedCollections.TSynchronizedIntList(list);
    }
  }
  

  public static class TSynchronizedLongList
    extends TSynchronizedCollections.TSynchronizedLongCollection
    implements TLongList
  {
    static final long serialVersionUID = -7754090372962971524L;
    
    final TLongList list;
    

    public TSynchronizedLongList(TLongList list)
    {
      super();
      this.list = list;
    }
    
    public TSynchronizedLongList(TLongList list, Object mutex) { super(mutex);
      this.list = list;
    }
    
    public boolean equals(Object o) {
      synchronized (mutex) { return list.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return list.hashCode();
      }
    }
    
    public long get(int index) { synchronized (mutex) { return list.get(index);
      } }
    
    public void set(int index, long element) { synchronized (mutex) { list.set(index, element);
      } }
    
    public void set(int offset, long[] values) { synchronized (mutex) { list.set(offset, values);
      } }
    
    public void set(int offset, long[] values, int valOffset, int length) { synchronized (mutex) { list.set(offset, values, valOffset, length);
      }
    }
    
    public long replace(int offset, long val) { synchronized (mutex) { return list.replace(offset, val);
      } }
    
    public void remove(int offset, int length) { synchronized (mutex) { list.remove(offset, length);
      } }
    
    public long removeAt(int offset) { synchronized (mutex) { return list.removeAt(offset);
      }
    }
    
    public void add(long[] vals) { synchronized (mutex) { list.add(vals);
      } }
    
    public void add(long[] vals, int offset, int length) { synchronized (mutex) { list.add(vals, offset, length);
      }
    }
    
    public void insert(int offset, long value) { synchronized (mutex) { list.insert(offset, value);
      } }
    
    public void insert(int offset, long[] values) { synchronized (mutex) { list.insert(offset, values);
      } }
    
    public void insert(int offset, long[] values, int valOffset, int len) { synchronized (mutex) { list.insert(offset, values, valOffset, len);
      }
    }
    
    public int indexOf(long o) { synchronized (mutex) { return list.indexOf(o);
      } }
    
    public int lastIndexOf(long o) { synchronized (mutex) { return list.lastIndexOf(o);
      }
    }
    






    public TLongList subList(int fromIndex, int toIndex)
    {
      synchronized (mutex) {
        return new TSynchronizedLongList(list.subList(fromIndex, toIndex), mutex);
      }
    }
    
    public long[] toArray(int offset, int len)
    {
      synchronized (mutex) { return list.toArray(offset, len);
      } }
    
    public long[] toArray(long[] dest, int offset, int len) { synchronized (mutex) { return list.toArray(dest, offset, len);
      } }
    
    public long[] toArray(long[] dest, int source_pos, int dest_pos, int len) { synchronized (mutex) { return list.toArray(dest, source_pos, dest_pos, len);
      }
    }
    
    public int indexOf(int offset, long value) { synchronized (mutex) { return list.indexOf(offset, value);
      } }
    
    public int lastIndexOf(int offset, long value) { synchronized (mutex) { return list.lastIndexOf(offset, value);
      }
    }
    
    public void fill(long val) { synchronized (mutex) { list.fill(val);
      } }
    
    public void fill(int fromIndex, int toIndex, long val) { synchronized (mutex) { list.fill(fromIndex, toIndex, val);
      }
    }
    
    public void reverse() { synchronized (mutex) { list.reverse();
      } }
    
    public void reverse(int from, int to) { synchronized (mutex) { list.reverse(from, to);
      }
    }
    
    public void shuffle(java.util.Random rand) { synchronized (mutex) { list.shuffle(rand);
      }
    }
    
    public void sort() { synchronized (mutex) { list.sort();
      } }
    
    public void sort(int fromIndex, int toIndex) { synchronized (mutex) { list.sort(fromIndex, toIndex);
      }
    }
    
    public int binarySearch(long value) { synchronized (mutex) { return list.binarySearch(value);
      } }
    
    public int binarySearch(long value, int fromIndex, int toIndex) { synchronized (mutex) { return list.binarySearch(value, fromIndex, toIndex);
      }
    }
    
    public TLongList grep(TLongProcedure condition) { synchronized (mutex) { return list.grep(condition);
      } }
    
    public TLongList inverseGrep(TLongProcedure condition) { synchronized (mutex) { return list.inverseGrep(condition);
      } }
    
    public long max() { synchronized (mutex) { return list.max(); } }
    public long min() { synchronized (mutex) { return list.min();
      } }
    
    public boolean forEachDescending(TLongProcedure procedure) { synchronized (mutex) { return list.forEachDescending(procedure);
      }
    }
    
    public void transformValues(gnu.trove.function.TLongFunction function) { synchronized (mutex) { list.transformValues(function);
      }
    }
    










    private Object readResolve()
    {
      return (list instanceof java.util.RandomAccess) ? new TSynchronizedCollections.TSynchronizedRandomAccessLongList(list) : this;
    }
  }
  


  public static class TSynchronizedRandomAccessLongList
    extends TSynchronizedCollections.TSynchronizedLongList
    implements java.util.RandomAccess
  {
    static final long serialVersionUID = 1530674583602358482L;
    

    public TSynchronizedRandomAccessLongList(TLongList list)
    {
      super();
    }
    
    public TSynchronizedRandomAccessLongList(TLongList list, Object mutex) {
      super(mutex);
    }
    
    public TLongList subList(int fromIndex, int toIndex) {
      synchronized (mutex) {
        return new TSynchronizedRandomAccessLongList(list.subList(fromIndex, toIndex), mutex);
      }
    }
    









    private Object writeReplace()
    {
      return new TSynchronizedCollections.TSynchronizedLongList(list);
    }
  }
  

  public static class TSynchronizedByteList
    extends TSynchronizedCollections.TSynchronizedByteCollection
    implements TByteList
  {
    static final long serialVersionUID = -7754090372962971524L;
    
    final TByteList list;
    

    public TSynchronizedByteList(TByteList list)
    {
      super();
      this.list = list;
    }
    
    public TSynchronizedByteList(TByteList list, Object mutex) { super(mutex);
      this.list = list;
    }
    
    public boolean equals(Object o) {
      synchronized (mutex) { return list.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return list.hashCode();
      }
    }
    
    public byte get(int index) { synchronized (mutex) { return list.get(index);
      } }
    
    public void set(int index, byte element) { synchronized (mutex) { list.set(index, element);
      } }
    
    public void set(int offset, byte[] values) { synchronized (mutex) { list.set(offset, values);
      } }
    
    public void set(int offset, byte[] values, int valOffset, int length) { synchronized (mutex) { list.set(offset, values, valOffset, length);
      }
    }
    
    public byte replace(int offset, byte val) { synchronized (mutex) { return list.replace(offset, val);
      } }
    
    public void remove(int offset, int length) { synchronized (mutex) { list.remove(offset, length);
      } }
    
    public byte removeAt(int offset) { synchronized (mutex) { return list.removeAt(offset);
      }
    }
    
    public void add(byte[] vals) { synchronized (mutex) { list.add(vals);
      } }
    
    public void add(byte[] vals, int offset, int length) { synchronized (mutex) { list.add(vals, offset, length);
      }
    }
    
    public void insert(int offset, byte value) { synchronized (mutex) { list.insert(offset, value);
      } }
    
    public void insert(int offset, byte[] values) { synchronized (mutex) { list.insert(offset, values);
      } }
    
    public void insert(int offset, byte[] values, int valOffset, int len) { synchronized (mutex) { list.insert(offset, values, valOffset, len);
      }
    }
    
    public int indexOf(byte o) { synchronized (mutex) { return list.indexOf(o);
      } }
    
    public int lastIndexOf(byte o) { synchronized (mutex) { return list.lastIndexOf(o);
      }
    }
    






    public TByteList subList(int fromIndex, int toIndex)
    {
      synchronized (mutex) {
        return new TSynchronizedByteList(list.subList(fromIndex, toIndex), mutex);
      }
    }
    
    public byte[] toArray(int offset, int len)
    {
      synchronized (mutex) { return list.toArray(offset, len);
      } }
    
    public byte[] toArray(byte[] dest, int offset, int len) { synchronized (mutex) { return list.toArray(dest, offset, len);
      } }
    
    public byte[] toArray(byte[] dest, int source_pos, int dest_pos, int len) { synchronized (mutex) { return list.toArray(dest, source_pos, dest_pos, len);
      }
    }
    
    public int indexOf(int offset, byte value) { synchronized (mutex) { return list.indexOf(offset, value);
      } }
    
    public int lastIndexOf(int offset, byte value) { synchronized (mutex) { return list.lastIndexOf(offset, value);
      }
    }
    
    public void fill(byte val) { synchronized (mutex) { list.fill(val);
      } }
    
    public void fill(int fromIndex, int toIndex, byte val) { synchronized (mutex) { list.fill(fromIndex, toIndex, val);
      }
    }
    
    public void reverse() { synchronized (mutex) { list.reverse();
      } }
    
    public void reverse(int from, int to) { synchronized (mutex) { list.reverse(from, to);
      }
    }
    
    public void shuffle(java.util.Random rand) { synchronized (mutex) { list.shuffle(rand);
      }
    }
    
    public void sort() { synchronized (mutex) { list.sort();
      } }
    
    public void sort(int fromIndex, int toIndex) { synchronized (mutex) { list.sort(fromIndex, toIndex);
      }
    }
    
    public int binarySearch(byte value) { synchronized (mutex) { return list.binarySearch(value);
      } }
    
    public int binarySearch(byte value, int fromIndex, int toIndex) { synchronized (mutex) { return list.binarySearch(value, fromIndex, toIndex);
      }
    }
    
    public TByteList grep(TByteProcedure condition) { synchronized (mutex) { return list.grep(condition);
      } }
    
    public TByteList inverseGrep(TByteProcedure condition) { synchronized (mutex) { return list.inverseGrep(condition);
      } }
    
    public byte max() { synchronized (mutex) { return list.max(); } }
    public byte min() { synchronized (mutex) { return list.min();
      } }
    
    public boolean forEachDescending(TByteProcedure procedure) { synchronized (mutex) { return list.forEachDescending(procedure);
      }
    }
    
    public void transformValues(gnu.trove.function.TByteFunction function) { synchronized (mutex) { list.transformValues(function);
      }
    }
    










    private Object readResolve()
    {
      return (list instanceof java.util.RandomAccess) ? new TSynchronizedCollections.TSynchronizedRandomAccessByteList(list) : this;
    }
  }
  


  public static class TSynchronizedRandomAccessByteList
    extends TSynchronizedCollections.TSynchronizedByteList
    implements java.util.RandomAccess
  {
    static final long serialVersionUID = 1530674583602358482L;
    

    public TSynchronizedRandomAccessByteList(TByteList list)
    {
      super();
    }
    
    public TSynchronizedRandomAccessByteList(TByteList list, Object mutex) {
      super(mutex);
    }
    
    public TByteList subList(int fromIndex, int toIndex) {
      synchronized (mutex) {
        return new TSynchronizedRandomAccessByteList(list.subList(fromIndex, toIndex), mutex);
      }
    }
    









    private Object writeReplace()
    {
      return new TSynchronizedCollections.TSynchronizedByteList(list);
    }
  }
  

  public static class TSynchronizedShortList
    extends TSynchronizedCollections.TSynchronizedShortCollection
    implements TShortList
  {
    static final long serialVersionUID = -7754090372962971524L;
    
    final TShortList list;
    

    public TSynchronizedShortList(TShortList list)
    {
      super();
      this.list = list;
    }
    
    public TSynchronizedShortList(TShortList list, Object mutex) { super(mutex);
      this.list = list;
    }
    
    public boolean equals(Object o) {
      synchronized (mutex) { return list.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return list.hashCode();
      }
    }
    
    public short get(int index) { synchronized (mutex) { return list.get(index);
      } }
    
    public void set(int index, short element) { synchronized (mutex) { list.set(index, element);
      } }
    
    public void set(int offset, short[] values) { synchronized (mutex) { list.set(offset, values);
      } }
    
    public void set(int offset, short[] values, int valOffset, int length) { synchronized (mutex) { list.set(offset, values, valOffset, length);
      }
    }
    
    public short replace(int offset, short val) { synchronized (mutex) { return list.replace(offset, val);
      } }
    
    public void remove(int offset, int length) { synchronized (mutex) { list.remove(offset, length);
      } }
    
    public short removeAt(int offset) { synchronized (mutex) { return list.removeAt(offset);
      }
    }
    
    public void add(short[] vals) { synchronized (mutex) { list.add(vals);
      } }
    
    public void add(short[] vals, int offset, int length) { synchronized (mutex) { list.add(vals, offset, length);
      }
    }
    
    public void insert(int offset, short value) { synchronized (mutex) { list.insert(offset, value);
      } }
    
    public void insert(int offset, short[] values) { synchronized (mutex) { list.insert(offset, values);
      } }
    
    public void insert(int offset, short[] values, int valOffset, int len) { synchronized (mutex) { list.insert(offset, values, valOffset, len);
      }
    }
    
    public int indexOf(short o) { synchronized (mutex) { return list.indexOf(o);
      } }
    
    public int lastIndexOf(short o) { synchronized (mutex) { return list.lastIndexOf(o);
      }
    }
    






    public TShortList subList(int fromIndex, int toIndex)
    {
      synchronized (mutex) {
        return new TSynchronizedShortList(list.subList(fromIndex, toIndex), mutex);
      }
    }
    
    public short[] toArray(int offset, int len)
    {
      synchronized (mutex) { return list.toArray(offset, len);
      } }
    
    public short[] toArray(short[] dest, int offset, int len) { synchronized (mutex) { return list.toArray(dest, offset, len);
      } }
    
    public short[] toArray(short[] dest, int source_pos, int dest_pos, int len) { synchronized (mutex) { return list.toArray(dest, source_pos, dest_pos, len);
      }
    }
    
    public int indexOf(int offset, short value) { synchronized (mutex) { return list.indexOf(offset, value);
      } }
    
    public int lastIndexOf(int offset, short value) { synchronized (mutex) { return list.lastIndexOf(offset, value);
      }
    }
    
    public void fill(short val) { synchronized (mutex) { list.fill(val);
      } }
    
    public void fill(int fromIndex, int toIndex, short val) { synchronized (mutex) { list.fill(fromIndex, toIndex, val);
      }
    }
    
    public void reverse() { synchronized (mutex) { list.reverse();
      } }
    
    public void reverse(int from, int to) { synchronized (mutex) { list.reverse(from, to);
      }
    }
    
    public void shuffle(java.util.Random rand) { synchronized (mutex) { list.shuffle(rand);
      }
    }
    
    public void sort() { synchronized (mutex) { list.sort();
      } }
    
    public void sort(int fromIndex, int toIndex) { synchronized (mutex) { list.sort(fromIndex, toIndex);
      }
    }
    
    public int binarySearch(short value) { synchronized (mutex) { return list.binarySearch(value);
      } }
    
    public int binarySearch(short value, int fromIndex, int toIndex) { synchronized (mutex) { return list.binarySearch(value, fromIndex, toIndex);
      }
    }
    
    public TShortList grep(TShortProcedure condition) { synchronized (mutex) { return list.grep(condition);
      } }
    
    public TShortList inverseGrep(TShortProcedure condition) { synchronized (mutex) { return list.inverseGrep(condition);
      } }
    
    public short max() { synchronized (mutex) { return list.max(); } }
    public short min() { synchronized (mutex) { return list.min();
      } }
    
    public boolean forEachDescending(TShortProcedure procedure) { synchronized (mutex) { return list.forEachDescending(procedure);
      }
    }
    
    public void transformValues(gnu.trove.function.TShortFunction function) { synchronized (mutex) { list.transformValues(function);
      }
    }
    










    private Object readResolve()
    {
      return (list instanceof java.util.RandomAccess) ? new TSynchronizedCollections.TSynchronizedRandomAccessShortList(list) : this;
    }
  }
  


  public static class TSynchronizedRandomAccessShortList
    extends TSynchronizedCollections.TSynchronizedShortList
    implements java.util.RandomAccess
  {
    static final long serialVersionUID = 1530674583602358482L;
    

    public TSynchronizedRandomAccessShortList(TShortList list)
    {
      super();
    }
    
    public TSynchronizedRandomAccessShortList(TShortList list, Object mutex) {
      super(mutex);
    }
    
    public TShortList subList(int fromIndex, int toIndex) {
      synchronized (mutex) {
        return new TSynchronizedRandomAccessShortList(list.subList(fromIndex, toIndex), mutex);
      }
    }
    









    private Object writeReplace()
    {
      return new TSynchronizedCollections.TSynchronizedShortList(list);
    }
  }
  

  public static class TSynchronizedCharList
    extends TSynchronizedCollections.TSynchronizedCharCollection
    implements TCharList
  {
    static final long serialVersionUID = -7754090372962971524L;
    
    final TCharList list;
    

    public TSynchronizedCharList(TCharList list)
    {
      super();
      this.list = list;
    }
    
    public TSynchronizedCharList(TCharList list, Object mutex) { super(mutex);
      this.list = list;
    }
    
    public boolean equals(Object o) {
      synchronized (mutex) { return list.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return list.hashCode();
      }
    }
    
    public char get(int index) { synchronized (mutex) { return list.get(index);
      } }
    
    public void set(int index, char element) { synchronized (mutex) { list.set(index, element);
      } }
    
    public void set(int offset, char[] values) { synchronized (mutex) { list.set(offset, values);
      } }
    
    public void set(int offset, char[] values, int valOffset, int length) { synchronized (mutex) { list.set(offset, values, valOffset, length);
      }
    }
    
    public char replace(int offset, char val) { synchronized (mutex) { return list.replace(offset, val);
      } }
    
    public void remove(int offset, int length) { synchronized (mutex) { list.remove(offset, length);
      } }
    
    public char removeAt(int offset) { synchronized (mutex) { return list.removeAt(offset);
      }
    }
    
    public void add(char[] vals) { synchronized (mutex) { list.add(vals);
      } }
    
    public void add(char[] vals, int offset, int length) { synchronized (mutex) { list.add(vals, offset, length);
      }
    }
    
    public void insert(int offset, char value) { synchronized (mutex) { list.insert(offset, value);
      } }
    
    public void insert(int offset, char[] values) { synchronized (mutex) { list.insert(offset, values);
      } }
    
    public void insert(int offset, char[] values, int valOffset, int len) { synchronized (mutex) { list.insert(offset, values, valOffset, len);
      }
    }
    
    public int indexOf(char o) { synchronized (mutex) { return list.indexOf(o);
      } }
    
    public int lastIndexOf(char o) { synchronized (mutex) { return list.lastIndexOf(o);
      }
    }
    






    public TCharList subList(int fromIndex, int toIndex)
    {
      synchronized (mutex) {
        return new TSynchronizedCharList(list.subList(fromIndex, toIndex), mutex);
      }
    }
    
    public char[] toArray(int offset, int len)
    {
      synchronized (mutex) { return list.toArray(offset, len);
      } }
    
    public char[] toArray(char[] dest, int offset, int len) { synchronized (mutex) { return list.toArray(dest, offset, len);
      } }
    
    public char[] toArray(char[] dest, int source_pos, int dest_pos, int len) { synchronized (mutex) { return list.toArray(dest, source_pos, dest_pos, len);
      }
    }
    
    public int indexOf(int offset, char value) { synchronized (mutex) { return list.indexOf(offset, value);
      } }
    
    public int lastIndexOf(int offset, char value) { synchronized (mutex) { return list.lastIndexOf(offset, value);
      }
    }
    
    public void fill(char val) { synchronized (mutex) { list.fill(val);
      } }
    
    public void fill(int fromIndex, int toIndex, char val) { synchronized (mutex) { list.fill(fromIndex, toIndex, val);
      }
    }
    
    public void reverse() { synchronized (mutex) { list.reverse();
      } }
    
    public void reverse(int from, int to) { synchronized (mutex) { list.reverse(from, to);
      }
    }
    
    public void shuffle(java.util.Random rand) { synchronized (mutex) { list.shuffle(rand);
      }
    }
    
    public void sort() { synchronized (mutex) { list.sort();
      } }
    
    public void sort(int fromIndex, int toIndex) { synchronized (mutex) { list.sort(fromIndex, toIndex);
      }
    }
    
    public int binarySearch(char value) { synchronized (mutex) { return list.binarySearch(value);
      } }
    
    public int binarySearch(char value, int fromIndex, int toIndex) { synchronized (mutex) { return list.binarySearch(value, fromIndex, toIndex);
      }
    }
    
    public TCharList grep(TCharProcedure condition) { synchronized (mutex) { return list.grep(condition);
      } }
    
    public TCharList inverseGrep(TCharProcedure condition) { synchronized (mutex) { return list.inverseGrep(condition);
      } }
    
    public char max() { synchronized (mutex) { return list.max(); } }
    public char min() { synchronized (mutex) { return list.min();
      } }
    
    public boolean forEachDescending(TCharProcedure procedure) { synchronized (mutex) { return list.forEachDescending(procedure);
      }
    }
    
    public void transformValues(gnu.trove.function.TCharFunction function) { synchronized (mutex) { list.transformValues(function);
      }
    }
    










    private Object readResolve()
    {
      return (list instanceof java.util.RandomAccess) ? new TSynchronizedCollections.TSynchronizedRandomAccessCharList(list) : this;
    }
  }
  


  public static class TSynchronizedRandomAccessCharList
    extends TSynchronizedCollections.TSynchronizedCharList
    implements java.util.RandomAccess
  {
    static final long serialVersionUID = 1530674583602358482L;
    

    public TSynchronizedRandomAccessCharList(TCharList list)
    {
      super();
    }
    
    public TSynchronizedRandomAccessCharList(TCharList list, Object mutex) {
      super(mutex);
    }
    
    public TCharList subList(int fromIndex, int toIndex) {
      synchronized (mutex) {
        return new TSynchronizedRandomAccessCharList(list.subList(fromIndex, toIndex), mutex);
      }
    }
    









    private Object writeReplace()
    {
      return new TSynchronizedCollections.TSynchronizedCharList(list);
    }
  }
  

  public static class TSynchronizedDoubleDoubleMap
    implements TDoubleDoubleMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final TDoubleDoubleMap m;
    
    final Object mutex;
    

    public TSynchronizedDoubleDoubleMap(TDoubleDoubleMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedDoubleDoubleMap(TDoubleDoubleMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(double key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(double value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public double get(double key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public double put(double key, double value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public double remove(double key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Double, ? extends Double> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(TDoubleDoubleMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TDoubleSet keySet = null;
    private transient TDoubleCollection values = null;
    
    public gnu.trove.set.TDoubleSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedDoubleSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public double[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public double[] keys(double[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TDoubleCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedDoubleCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public double[] values() { synchronized (mutex) { return m.values();
      } }
    
    public double[] values(double[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TDoubleDoubleIterator iterator() { return m.iterator(); }
    


    public double getNoEntryKey() { return m.getNoEntryKey(); }
    public double getNoEntryValue() { return m.getNoEntryValue(); }
    
    public double putIfAbsent(double key, double value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TDoubleProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TDoubleProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TDoubleDoubleProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TDoubleFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TDoubleDoubleProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(double key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(double key, double amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public double adjustOrPutValue(double key, double adjust_amount, double put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedDoubleFloatMap
    implements TDoubleFloatMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final TDoubleFloatMap m;
    final Object mutex;
    
    public TSynchronizedDoubleFloatMap(TDoubleFloatMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedDoubleFloatMap(TDoubleFloatMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(double key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(float value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public float get(double key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public float put(double key, float value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public float remove(double key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Double, ? extends Float> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(TDoubleFloatMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TDoubleSet keySet = null;
    private transient TFloatCollection values = null;
    
    public gnu.trove.set.TDoubleSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedDoubleSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public double[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public double[] keys(double[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TFloatCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedFloatCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public float[] values() { synchronized (mutex) { return m.values();
      } }
    
    public float[] values(float[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TDoubleFloatIterator iterator() { return m.iterator(); }
    


    public double getNoEntryKey() { return m.getNoEntryKey(); }
    public float getNoEntryValue() { return m.getNoEntryValue(); }
    
    public float putIfAbsent(double key, float value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TDoubleProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TFloatProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TDoubleFloatProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TFloatFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TDoubleFloatProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(double key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(double key, float amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public float adjustOrPutValue(double key, float adjust_amount, float put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedDoubleIntMap
    implements TDoubleIntMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final TDoubleIntMap m;
    final Object mutex;
    
    public TSynchronizedDoubleIntMap(TDoubleIntMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedDoubleIntMap(TDoubleIntMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(double key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(int value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public int get(double key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public int put(double key, int value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public int remove(double key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Double, ? extends Integer> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(TDoubleIntMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TDoubleSet keySet = null;
    private transient TIntCollection values = null;
    
    public gnu.trove.set.TDoubleSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedDoubleSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public double[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public double[] keys(double[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TIntCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedIntCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public int[] values() { synchronized (mutex) { return m.values();
      } }
    
    public int[] values(int[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TDoubleIntIterator iterator() { return m.iterator(); }
    


    public double getNoEntryKey() { return m.getNoEntryKey(); }
    public int getNoEntryValue() { return m.getNoEntryValue(); }
    
    public int putIfAbsent(double key, int value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TDoubleProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TIntProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TDoubleIntProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TIntFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TDoubleIntProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(double key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(double key, int amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public int adjustOrPutValue(double key, int adjust_amount, int put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedDoubleLongMap
    implements TDoubleLongMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final TDoubleLongMap m;
    final Object mutex;
    
    public TSynchronizedDoubleLongMap(TDoubleLongMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedDoubleLongMap(TDoubleLongMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(double key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(long value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public long get(double key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public long put(double key, long value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public long remove(double key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Double, ? extends Long> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(TDoubleLongMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TDoubleSet keySet = null;
    private transient TLongCollection values = null;
    
    public gnu.trove.set.TDoubleSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedDoubleSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public double[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public double[] keys(double[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TLongCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedLongCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public long[] values() { synchronized (mutex) { return m.values();
      } }
    
    public long[] values(long[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TDoubleLongIterator iterator() { return m.iterator(); }
    


    public double getNoEntryKey() { return m.getNoEntryKey(); }
    public long getNoEntryValue() { return m.getNoEntryValue(); }
    
    public long putIfAbsent(double key, long value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TDoubleProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TLongProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TDoubleLongProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TLongFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TDoubleLongProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(double key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(double key, long amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public long adjustOrPutValue(double key, long adjust_amount, long put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedDoubleByteMap
    implements gnu.trove.map.TDoubleByteMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final gnu.trove.map.TDoubleByteMap m;
    final Object mutex;
    
    public TSynchronizedDoubleByteMap(gnu.trove.map.TDoubleByteMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedDoubleByteMap(gnu.trove.map.TDoubleByteMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(double key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(byte value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public byte get(double key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public byte put(double key, byte value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public byte remove(double key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Double, ? extends Byte> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(gnu.trove.map.TDoubleByteMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TDoubleSet keySet = null;
    private transient TByteCollection values = null;
    
    public gnu.trove.set.TDoubleSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedDoubleSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public double[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public double[] keys(double[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TByteCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedByteCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public byte[] values() { synchronized (mutex) { return m.values();
      } }
    
    public byte[] values(byte[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TDoubleByteIterator iterator() { return m.iterator(); }
    


    public double getNoEntryKey() { return m.getNoEntryKey(); }
    public byte getNoEntryValue() { return m.getNoEntryValue(); }
    
    public byte putIfAbsent(double key, byte value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TDoubleProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TByteProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TDoubleByteProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TByteFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TDoubleByteProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(double key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(double key, byte amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public byte adjustOrPutValue(double key, byte adjust_amount, byte put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedDoubleShortMap
    implements TDoubleShortMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final TDoubleShortMap m;
    final Object mutex;
    
    public TSynchronizedDoubleShortMap(TDoubleShortMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedDoubleShortMap(TDoubleShortMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(double key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(short value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public short get(double key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public short put(double key, short value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public short remove(double key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Double, ? extends Short> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(TDoubleShortMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TDoubleSet keySet = null;
    private transient TShortCollection values = null;
    
    public gnu.trove.set.TDoubleSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedDoubleSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public double[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public double[] keys(double[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TShortCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedShortCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public short[] values() { synchronized (mutex) { return m.values();
      } }
    
    public short[] values(short[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TDoubleShortIterator iterator() { return m.iterator(); }
    


    public double getNoEntryKey() { return m.getNoEntryKey(); }
    public short getNoEntryValue() { return m.getNoEntryValue(); }
    
    public short putIfAbsent(double key, short value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TDoubleProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TShortProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TDoubleShortProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TShortFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TDoubleShortProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(double key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(double key, short amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public short adjustOrPutValue(double key, short adjust_amount, short put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedDoubleCharMap
    implements TDoubleCharMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final TDoubleCharMap m;
    final Object mutex;
    
    public TSynchronizedDoubleCharMap(TDoubleCharMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedDoubleCharMap(TDoubleCharMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(double key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(char value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public char get(double key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public char put(double key, char value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public char remove(double key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Double, ? extends Character> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(TDoubleCharMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TDoubleSet keySet = null;
    private transient TCharCollection values = null;
    
    public gnu.trove.set.TDoubleSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedDoubleSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public double[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public double[] keys(double[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TCharCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedCharCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public char[] values() { synchronized (mutex) { return m.values();
      } }
    
    public char[] values(char[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TDoubleCharIterator iterator() { return m.iterator(); }
    


    public double getNoEntryKey() { return m.getNoEntryKey(); }
    public char getNoEntryValue() { return m.getNoEntryValue(); }
    
    public char putIfAbsent(double key, char value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TDoubleProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TCharProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TDoubleCharProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TCharFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TDoubleCharProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(double key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(double key, char amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public char adjustOrPutValue(double key, char adjust_amount, char put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedFloatDoubleMap
    implements TFloatDoubleMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final TFloatDoubleMap m;
    final Object mutex;
    
    public TSynchronizedFloatDoubleMap(TFloatDoubleMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedFloatDoubleMap(TFloatDoubleMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(float key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(double value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public double get(float key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public double put(float key, double value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public double remove(float key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Float, ? extends Double> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(TFloatDoubleMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TFloatSet keySet = null;
    private transient TDoubleCollection values = null;
    
    public gnu.trove.set.TFloatSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedFloatSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public float[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public float[] keys(float[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TDoubleCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedDoubleCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public double[] values() { synchronized (mutex) { return m.values();
      } }
    
    public double[] values(double[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TFloatDoubleIterator iterator() { return m.iterator(); }
    


    public float getNoEntryKey() { return m.getNoEntryKey(); }
    public double getNoEntryValue() { return m.getNoEntryValue(); }
    
    public double putIfAbsent(float key, double value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TFloatProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TDoubleProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TFloatDoubleProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TDoubleFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TFloatDoubleProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(float key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(float key, double amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public double adjustOrPutValue(float key, double adjust_amount, double put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedFloatFloatMap
    implements TFloatFloatMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final TFloatFloatMap m;
    final Object mutex;
    
    public TSynchronizedFloatFloatMap(TFloatFloatMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedFloatFloatMap(TFloatFloatMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(float key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(float value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public float get(float key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public float put(float key, float value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public float remove(float key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Float, ? extends Float> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(TFloatFloatMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TFloatSet keySet = null;
    private transient TFloatCollection values = null;
    
    public gnu.trove.set.TFloatSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedFloatSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public float[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public float[] keys(float[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TFloatCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedFloatCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public float[] values() { synchronized (mutex) { return m.values();
      } }
    
    public float[] values(float[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TFloatFloatIterator iterator() { return m.iterator(); }
    


    public float getNoEntryKey() { return m.getNoEntryKey(); }
    public float getNoEntryValue() { return m.getNoEntryValue(); }
    
    public float putIfAbsent(float key, float value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TFloatProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TFloatProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TFloatFloatProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TFloatFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TFloatFloatProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(float key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(float key, float amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public float adjustOrPutValue(float key, float adjust_amount, float put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedFloatIntMap
    implements gnu.trove.map.TFloatIntMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final gnu.trove.map.TFloatIntMap m;
    final Object mutex;
    
    public TSynchronizedFloatIntMap(gnu.trove.map.TFloatIntMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedFloatIntMap(gnu.trove.map.TFloatIntMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(float key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(int value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public int get(float key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public int put(float key, int value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public int remove(float key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Float, ? extends Integer> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(gnu.trove.map.TFloatIntMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TFloatSet keySet = null;
    private transient TIntCollection values = null;
    
    public gnu.trove.set.TFloatSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedFloatSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public float[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public float[] keys(float[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TIntCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedIntCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public int[] values() { synchronized (mutex) { return m.values();
      } }
    
    public int[] values(int[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TFloatIntIterator iterator() { return m.iterator(); }
    


    public float getNoEntryKey() { return m.getNoEntryKey(); }
    public int getNoEntryValue() { return m.getNoEntryValue(); }
    
    public int putIfAbsent(float key, int value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TFloatProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TIntProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TFloatIntProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TIntFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TFloatIntProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(float key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(float key, int amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public int adjustOrPutValue(float key, int adjust_amount, int put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedFloatLongMap
    implements TFloatLongMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final TFloatLongMap m;
    final Object mutex;
    
    public TSynchronizedFloatLongMap(TFloatLongMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedFloatLongMap(TFloatLongMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(float key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(long value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public long get(float key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public long put(float key, long value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public long remove(float key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Float, ? extends Long> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(TFloatLongMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TFloatSet keySet = null;
    private transient TLongCollection values = null;
    
    public gnu.trove.set.TFloatSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedFloatSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public float[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public float[] keys(float[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TLongCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedLongCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public long[] values() { synchronized (mutex) { return m.values();
      } }
    
    public long[] values(long[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TFloatLongIterator iterator() { return m.iterator(); }
    


    public float getNoEntryKey() { return m.getNoEntryKey(); }
    public long getNoEntryValue() { return m.getNoEntryValue(); }
    
    public long putIfAbsent(float key, long value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TFloatProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TLongProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TFloatLongProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TLongFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TFloatLongProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(float key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(float key, long amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public long adjustOrPutValue(float key, long adjust_amount, long put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedFloatByteMap
    implements TFloatByteMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final TFloatByteMap m;
    final Object mutex;
    
    public TSynchronizedFloatByteMap(TFloatByteMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedFloatByteMap(TFloatByteMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(float key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(byte value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public byte get(float key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public byte put(float key, byte value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public byte remove(float key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Float, ? extends Byte> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(TFloatByteMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TFloatSet keySet = null;
    private transient TByteCollection values = null;
    
    public gnu.trove.set.TFloatSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedFloatSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public float[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public float[] keys(float[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TByteCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedByteCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public byte[] values() { synchronized (mutex) { return m.values();
      } }
    
    public byte[] values(byte[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TFloatByteIterator iterator() { return m.iterator(); }
    


    public float getNoEntryKey() { return m.getNoEntryKey(); }
    public byte getNoEntryValue() { return m.getNoEntryValue(); }
    
    public byte putIfAbsent(float key, byte value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TFloatProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TByteProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TFloatByteProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TByteFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TFloatByteProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(float key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(float key, byte amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public byte adjustOrPutValue(float key, byte adjust_amount, byte put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedFloatShortMap
    implements TFloatShortMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final TFloatShortMap m;
    final Object mutex;
    
    public TSynchronizedFloatShortMap(TFloatShortMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedFloatShortMap(TFloatShortMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(float key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(short value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public short get(float key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public short put(float key, short value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public short remove(float key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Float, ? extends Short> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(TFloatShortMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TFloatSet keySet = null;
    private transient TShortCollection values = null;
    
    public gnu.trove.set.TFloatSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedFloatSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public float[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public float[] keys(float[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TShortCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedShortCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public short[] values() { synchronized (mutex) { return m.values();
      } }
    
    public short[] values(short[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TFloatShortIterator iterator() { return m.iterator(); }
    


    public float getNoEntryKey() { return m.getNoEntryKey(); }
    public short getNoEntryValue() { return m.getNoEntryValue(); }
    
    public short putIfAbsent(float key, short value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TFloatProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TShortProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TFloatShortProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TShortFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TFloatShortProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(float key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(float key, short amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public short adjustOrPutValue(float key, short adjust_amount, short put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedFloatCharMap
    implements TFloatCharMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final TFloatCharMap m;
    final Object mutex;
    
    public TSynchronizedFloatCharMap(TFloatCharMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedFloatCharMap(TFloatCharMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(float key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(char value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public char get(float key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public char put(float key, char value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public char remove(float key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Float, ? extends Character> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(TFloatCharMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TFloatSet keySet = null;
    private transient TCharCollection values = null;
    
    public gnu.trove.set.TFloatSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedFloatSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public float[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public float[] keys(float[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TCharCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedCharCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public char[] values() { synchronized (mutex) { return m.values();
      } }
    
    public char[] values(char[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TFloatCharIterator iterator() { return m.iterator(); }
    


    public float getNoEntryKey() { return m.getNoEntryKey(); }
    public char getNoEntryValue() { return m.getNoEntryValue(); }
    
    public char putIfAbsent(float key, char value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TFloatProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TCharProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TFloatCharProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TCharFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TFloatCharProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(float key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(float key, char amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public char adjustOrPutValue(float key, char adjust_amount, char put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedIntDoubleMap
    implements gnu.trove.map.TIntDoubleMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final gnu.trove.map.TIntDoubleMap m;
    final Object mutex;
    
    public TSynchronizedIntDoubleMap(gnu.trove.map.TIntDoubleMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedIntDoubleMap(gnu.trove.map.TIntDoubleMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(int key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(double value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public double get(int key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public double put(int key, double value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public double remove(int key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Integer, ? extends Double> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(gnu.trove.map.TIntDoubleMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TIntSet keySet = null;
    private transient TDoubleCollection values = null;
    
    public gnu.trove.set.TIntSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedIntSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public int[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public int[] keys(int[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TDoubleCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedDoubleCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public double[] values() { synchronized (mutex) { return m.values();
      } }
    
    public double[] values(double[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TIntDoubleIterator iterator() { return m.iterator(); }
    


    public int getNoEntryKey() { return m.getNoEntryKey(); }
    public double getNoEntryValue() { return m.getNoEntryValue(); }
    
    public double putIfAbsent(int key, double value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TIntProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TDoubleProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TIntDoubleProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TDoubleFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TIntDoubleProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(int key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(int key, double amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public double adjustOrPutValue(int key, double adjust_amount, double put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedIntFloatMap
    implements TIntFloatMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final TIntFloatMap m;
    final Object mutex;
    
    public TSynchronizedIntFloatMap(TIntFloatMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedIntFloatMap(TIntFloatMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(int key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(float value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public float get(int key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public float put(int key, float value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public float remove(int key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Integer, ? extends Float> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(TIntFloatMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TIntSet keySet = null;
    private transient TFloatCollection values = null;
    
    public gnu.trove.set.TIntSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedIntSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public int[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public int[] keys(int[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TFloatCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedFloatCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public float[] values() { synchronized (mutex) { return m.values();
      } }
    
    public float[] values(float[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TIntFloatIterator iterator() { return m.iterator(); }
    


    public int getNoEntryKey() { return m.getNoEntryKey(); }
    public float getNoEntryValue() { return m.getNoEntryValue(); }
    
    public float putIfAbsent(int key, float value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TIntProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TFloatProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TIntFloatProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TFloatFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TIntFloatProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(int key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(int key, float amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public float adjustOrPutValue(int key, float adjust_amount, float put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedIntIntMap
    implements TIntIntMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final TIntIntMap m;
    final Object mutex;
    
    public TSynchronizedIntIntMap(TIntIntMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedIntIntMap(TIntIntMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(int key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(int value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public int get(int key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public int put(int key, int value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public int remove(int key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Integer, ? extends Integer> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(TIntIntMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TIntSet keySet = null;
    private transient TIntCollection values = null;
    
    public gnu.trove.set.TIntSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedIntSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public int[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public int[] keys(int[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TIntCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedIntCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public int[] values() { synchronized (mutex) { return m.values();
      } }
    
    public int[] values(int[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TIntIntIterator iterator() { return m.iterator(); }
    


    public int getNoEntryKey() { return m.getNoEntryKey(); }
    public int getNoEntryValue() { return m.getNoEntryValue(); }
    
    public int putIfAbsent(int key, int value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TIntProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TIntProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TIntIntProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TIntFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TIntIntProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(int key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(int key, int amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public int adjustOrPutValue(int key, int adjust_amount, int put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedIntLongMap
    implements gnu.trove.map.TIntLongMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final gnu.trove.map.TIntLongMap m;
    final Object mutex;
    
    public TSynchronizedIntLongMap(gnu.trove.map.TIntLongMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedIntLongMap(gnu.trove.map.TIntLongMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(int key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(long value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public long get(int key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public long put(int key, long value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public long remove(int key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Integer, ? extends Long> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(gnu.trove.map.TIntLongMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TIntSet keySet = null;
    private transient TLongCollection values = null;
    
    public gnu.trove.set.TIntSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedIntSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public int[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public int[] keys(int[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TLongCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedLongCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public long[] values() { synchronized (mutex) { return m.values();
      } }
    
    public long[] values(long[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TIntLongIterator iterator() { return m.iterator(); }
    


    public int getNoEntryKey() { return m.getNoEntryKey(); }
    public long getNoEntryValue() { return m.getNoEntryValue(); }
    
    public long putIfAbsent(int key, long value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TIntProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TLongProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TIntLongProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TLongFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TIntLongProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(int key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(int key, long amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public long adjustOrPutValue(int key, long adjust_amount, long put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedIntByteMap
    implements gnu.trove.map.TIntByteMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final gnu.trove.map.TIntByteMap m;
    final Object mutex;
    
    public TSynchronizedIntByteMap(gnu.trove.map.TIntByteMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedIntByteMap(gnu.trove.map.TIntByteMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(int key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(byte value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public byte get(int key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public byte put(int key, byte value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public byte remove(int key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Integer, ? extends Byte> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(gnu.trove.map.TIntByteMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TIntSet keySet = null;
    private transient TByteCollection values = null;
    
    public gnu.trove.set.TIntSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedIntSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public int[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public int[] keys(int[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TByteCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedByteCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public byte[] values() { synchronized (mutex) { return m.values();
      } }
    
    public byte[] values(byte[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TIntByteIterator iterator() { return m.iterator(); }
    


    public int getNoEntryKey() { return m.getNoEntryKey(); }
    public byte getNoEntryValue() { return m.getNoEntryValue(); }
    
    public byte putIfAbsent(int key, byte value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TIntProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TByteProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TIntByteProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TByteFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TIntByteProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(int key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(int key, byte amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public byte adjustOrPutValue(int key, byte adjust_amount, byte put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedIntShortMap
    implements gnu.trove.map.TIntShortMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final gnu.trove.map.TIntShortMap m;
    final Object mutex;
    
    public TSynchronizedIntShortMap(gnu.trove.map.TIntShortMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedIntShortMap(gnu.trove.map.TIntShortMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(int key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(short value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public short get(int key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public short put(int key, short value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public short remove(int key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Integer, ? extends Short> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(gnu.trove.map.TIntShortMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TIntSet keySet = null;
    private transient TShortCollection values = null;
    
    public gnu.trove.set.TIntSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedIntSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public int[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public int[] keys(int[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TShortCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedShortCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public short[] values() { synchronized (mutex) { return m.values();
      } }
    
    public short[] values(short[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TIntShortIterator iterator() { return m.iterator(); }
    


    public int getNoEntryKey() { return m.getNoEntryKey(); }
    public short getNoEntryValue() { return m.getNoEntryValue(); }
    
    public short putIfAbsent(int key, short value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TIntProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TShortProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TIntShortProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TShortFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TIntShortProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(int key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(int key, short amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public short adjustOrPutValue(int key, short adjust_amount, short put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedIntCharMap
    implements TIntCharMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final TIntCharMap m;
    final Object mutex;
    
    public TSynchronizedIntCharMap(TIntCharMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedIntCharMap(TIntCharMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(int key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(char value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public char get(int key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public char put(int key, char value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public char remove(int key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Integer, ? extends Character> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(TIntCharMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TIntSet keySet = null;
    private transient TCharCollection values = null;
    
    public gnu.trove.set.TIntSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedIntSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public int[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public int[] keys(int[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TCharCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedCharCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public char[] values() { synchronized (mutex) { return m.values();
      } }
    
    public char[] values(char[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TIntCharIterator iterator() { return m.iterator(); }
    


    public int getNoEntryKey() { return m.getNoEntryKey(); }
    public char getNoEntryValue() { return m.getNoEntryValue(); }
    
    public char putIfAbsent(int key, char value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TIntProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TCharProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TIntCharProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TCharFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TIntCharProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(int key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(int key, char amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public char adjustOrPutValue(int key, char adjust_amount, char put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedLongDoubleMap
    implements gnu.trove.map.TLongDoubleMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final gnu.trove.map.TLongDoubleMap m;
    final Object mutex;
    
    public TSynchronizedLongDoubleMap(gnu.trove.map.TLongDoubleMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedLongDoubleMap(gnu.trove.map.TLongDoubleMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(long key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(double value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public double get(long key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public double put(long key, double value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public double remove(long key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Long, ? extends Double> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(gnu.trove.map.TLongDoubleMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TLongSet keySet = null;
    private transient TDoubleCollection values = null;
    
    public gnu.trove.set.TLongSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedLongSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public long[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public long[] keys(long[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TDoubleCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedDoubleCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public double[] values() { synchronized (mutex) { return m.values();
      } }
    
    public double[] values(double[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TLongDoubleIterator iterator() { return m.iterator(); }
    


    public long getNoEntryKey() { return m.getNoEntryKey(); }
    public double getNoEntryValue() { return m.getNoEntryValue(); }
    
    public double putIfAbsent(long key, double value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TLongProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TDoubleProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TLongDoubleProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TDoubleFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TLongDoubleProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(long key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(long key, double amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public double adjustOrPutValue(long key, double adjust_amount, double put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedLongFloatMap
    implements gnu.trove.map.TLongFloatMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final gnu.trove.map.TLongFloatMap m;
    final Object mutex;
    
    public TSynchronizedLongFloatMap(gnu.trove.map.TLongFloatMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedLongFloatMap(gnu.trove.map.TLongFloatMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(long key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(float value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public float get(long key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public float put(long key, float value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public float remove(long key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Long, ? extends Float> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(gnu.trove.map.TLongFloatMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TLongSet keySet = null;
    private transient TFloatCollection values = null;
    
    public gnu.trove.set.TLongSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedLongSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public long[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public long[] keys(long[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TFloatCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedFloatCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public float[] values() { synchronized (mutex) { return m.values();
      } }
    
    public float[] values(float[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TLongFloatIterator iterator() { return m.iterator(); }
    


    public long getNoEntryKey() { return m.getNoEntryKey(); }
    public float getNoEntryValue() { return m.getNoEntryValue(); }
    
    public float putIfAbsent(long key, float value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TLongProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TFloatProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TLongFloatProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TFloatFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TLongFloatProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(long key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(long key, float amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public float adjustOrPutValue(long key, float adjust_amount, float put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedLongIntMap
    implements TLongIntMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final TLongIntMap m;
    final Object mutex;
    
    public TSynchronizedLongIntMap(TLongIntMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedLongIntMap(TLongIntMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(long key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(int value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public int get(long key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public int put(long key, int value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public int remove(long key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Long, ? extends Integer> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(TLongIntMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TLongSet keySet = null;
    private transient TIntCollection values = null;
    
    public gnu.trove.set.TLongSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedLongSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public long[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public long[] keys(long[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TIntCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedIntCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public int[] values() { synchronized (mutex) { return m.values();
      } }
    
    public int[] values(int[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TLongIntIterator iterator() { return m.iterator(); }
    


    public long getNoEntryKey() { return m.getNoEntryKey(); }
    public int getNoEntryValue() { return m.getNoEntryValue(); }
    
    public int putIfAbsent(long key, int value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TLongProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TIntProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TLongIntProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TIntFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TLongIntProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(long key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(long key, int amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public int adjustOrPutValue(long key, int adjust_amount, int put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedLongLongMap
    implements TLongLongMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final TLongLongMap m;
    final Object mutex;
    
    public TSynchronizedLongLongMap(TLongLongMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedLongLongMap(TLongLongMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(long key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(long value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public long get(long key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public long put(long key, long value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public long remove(long key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Long, ? extends Long> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(TLongLongMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TLongSet keySet = null;
    private transient TLongCollection values = null;
    
    public gnu.trove.set.TLongSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedLongSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public long[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public long[] keys(long[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TLongCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedLongCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public long[] values() { synchronized (mutex) { return m.values();
      } }
    
    public long[] values(long[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TLongLongIterator iterator() { return m.iterator(); }
    


    public long getNoEntryKey() { return m.getNoEntryKey(); }
    public long getNoEntryValue() { return m.getNoEntryValue(); }
    
    public long putIfAbsent(long key, long value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TLongProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TLongProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TLongLongProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TLongFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TLongLongProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(long key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(long key, long amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public long adjustOrPutValue(long key, long adjust_amount, long put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedLongByteMap
    implements TLongByteMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final TLongByteMap m;
    final Object mutex;
    
    public TSynchronizedLongByteMap(TLongByteMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedLongByteMap(TLongByteMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(long key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(byte value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public byte get(long key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public byte put(long key, byte value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public byte remove(long key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Long, ? extends Byte> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(TLongByteMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TLongSet keySet = null;
    private transient TByteCollection values = null;
    
    public gnu.trove.set.TLongSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedLongSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public long[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public long[] keys(long[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TByteCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedByteCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public byte[] values() { synchronized (mutex) { return m.values();
      } }
    
    public byte[] values(byte[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TLongByteIterator iterator() { return m.iterator(); }
    


    public long getNoEntryKey() { return m.getNoEntryKey(); }
    public byte getNoEntryValue() { return m.getNoEntryValue(); }
    
    public byte putIfAbsent(long key, byte value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TLongProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TByteProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TLongByteProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TByteFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TLongByteProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(long key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(long key, byte amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public byte adjustOrPutValue(long key, byte adjust_amount, byte put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedLongShortMap
    implements TLongShortMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final TLongShortMap m;
    final Object mutex;
    
    public TSynchronizedLongShortMap(TLongShortMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedLongShortMap(TLongShortMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(long key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(short value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public short get(long key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public short put(long key, short value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public short remove(long key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Long, ? extends Short> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(TLongShortMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TLongSet keySet = null;
    private transient TShortCollection values = null;
    
    public gnu.trove.set.TLongSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedLongSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public long[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public long[] keys(long[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TShortCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedShortCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public short[] values() { synchronized (mutex) { return m.values();
      } }
    
    public short[] values(short[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TLongShortIterator iterator() { return m.iterator(); }
    


    public long getNoEntryKey() { return m.getNoEntryKey(); }
    public short getNoEntryValue() { return m.getNoEntryValue(); }
    
    public short putIfAbsent(long key, short value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TLongProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TShortProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TLongShortProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TShortFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TLongShortProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(long key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(long key, short amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public short adjustOrPutValue(long key, short adjust_amount, short put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedLongCharMap
    implements gnu.trove.map.TLongCharMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final gnu.trove.map.TLongCharMap m;
    final Object mutex;
    
    public TSynchronizedLongCharMap(gnu.trove.map.TLongCharMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedLongCharMap(gnu.trove.map.TLongCharMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(long key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(char value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public char get(long key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public char put(long key, char value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public char remove(long key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Long, ? extends Character> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(gnu.trove.map.TLongCharMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TLongSet keySet = null;
    private transient TCharCollection values = null;
    
    public gnu.trove.set.TLongSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedLongSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public long[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public long[] keys(long[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TCharCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedCharCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public char[] values() { synchronized (mutex) { return m.values();
      } }
    
    public char[] values(char[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TLongCharIterator iterator() { return m.iterator(); }
    


    public long getNoEntryKey() { return m.getNoEntryKey(); }
    public char getNoEntryValue() { return m.getNoEntryValue(); }
    
    public char putIfAbsent(long key, char value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TLongProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TCharProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TLongCharProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TCharFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TLongCharProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(long key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(long key, char amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public char adjustOrPutValue(long key, char adjust_amount, char put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedByteDoubleMap
    implements TByteDoubleMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final TByteDoubleMap m;
    final Object mutex;
    
    public TSynchronizedByteDoubleMap(TByteDoubleMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedByteDoubleMap(TByteDoubleMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(byte key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(double value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public double get(byte key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public double put(byte key, double value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public double remove(byte key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Byte, ? extends Double> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(TByteDoubleMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TByteSet keySet = null;
    private transient TDoubleCollection values = null;
    
    public gnu.trove.set.TByteSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedByteSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public byte[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public byte[] keys(byte[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TDoubleCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedDoubleCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public double[] values() { synchronized (mutex) { return m.values();
      } }
    
    public double[] values(double[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TByteDoubleIterator iterator() { return m.iterator(); }
    


    public byte getNoEntryKey() { return m.getNoEntryKey(); }
    public double getNoEntryValue() { return m.getNoEntryValue(); }
    
    public double putIfAbsent(byte key, double value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TByteProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TDoubleProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TByteDoubleProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TDoubleFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TByteDoubleProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(byte key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(byte key, double amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public double adjustOrPutValue(byte key, double adjust_amount, double put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedByteFloatMap
    implements TByteFloatMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final TByteFloatMap m;
    final Object mutex;
    
    public TSynchronizedByteFloatMap(TByteFloatMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedByteFloatMap(TByteFloatMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(byte key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(float value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public float get(byte key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public float put(byte key, float value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public float remove(byte key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Byte, ? extends Float> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(TByteFloatMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TByteSet keySet = null;
    private transient TFloatCollection values = null;
    
    public gnu.trove.set.TByteSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedByteSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public byte[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public byte[] keys(byte[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TFloatCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedFloatCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public float[] values() { synchronized (mutex) { return m.values();
      } }
    
    public float[] values(float[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TByteFloatIterator iterator() { return m.iterator(); }
    


    public byte getNoEntryKey() { return m.getNoEntryKey(); }
    public float getNoEntryValue() { return m.getNoEntryValue(); }
    
    public float putIfAbsent(byte key, float value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TByteProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TFloatProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TByteFloatProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TFloatFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TByteFloatProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(byte key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(byte key, float amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public float adjustOrPutValue(byte key, float adjust_amount, float put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedByteIntMap
    implements TByteIntMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final TByteIntMap m;
    final Object mutex;
    
    public TSynchronizedByteIntMap(TByteIntMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedByteIntMap(TByteIntMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(byte key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(int value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public int get(byte key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public int put(byte key, int value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public int remove(byte key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Byte, ? extends Integer> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(TByteIntMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TByteSet keySet = null;
    private transient TIntCollection values = null;
    
    public gnu.trove.set.TByteSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedByteSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public byte[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public byte[] keys(byte[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TIntCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedIntCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public int[] values() { synchronized (mutex) { return m.values();
      } }
    
    public int[] values(int[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TByteIntIterator iterator() { return m.iterator(); }
    


    public byte getNoEntryKey() { return m.getNoEntryKey(); }
    public int getNoEntryValue() { return m.getNoEntryValue(); }
    
    public int putIfAbsent(byte key, int value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TByteProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TIntProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TByteIntProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TIntFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TByteIntProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(byte key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(byte key, int amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public int adjustOrPutValue(byte key, int adjust_amount, int put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedByteLongMap
    implements gnu.trove.map.TByteLongMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final gnu.trove.map.TByteLongMap m;
    final Object mutex;
    
    public TSynchronizedByteLongMap(gnu.trove.map.TByteLongMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedByteLongMap(gnu.trove.map.TByteLongMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(byte key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(long value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public long get(byte key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public long put(byte key, long value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public long remove(byte key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Byte, ? extends Long> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(gnu.trove.map.TByteLongMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TByteSet keySet = null;
    private transient TLongCollection values = null;
    
    public gnu.trove.set.TByteSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedByteSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public byte[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public byte[] keys(byte[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TLongCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedLongCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public long[] values() { synchronized (mutex) { return m.values();
      } }
    
    public long[] values(long[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TByteLongIterator iterator() { return m.iterator(); }
    


    public byte getNoEntryKey() { return m.getNoEntryKey(); }
    public long getNoEntryValue() { return m.getNoEntryValue(); }
    
    public long putIfAbsent(byte key, long value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TByteProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TLongProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TByteLongProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TLongFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TByteLongProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(byte key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(byte key, long amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public long adjustOrPutValue(byte key, long adjust_amount, long put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedByteByteMap
    implements TByteByteMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final TByteByteMap m;
    final Object mutex;
    
    public TSynchronizedByteByteMap(TByteByteMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedByteByteMap(TByteByteMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(byte key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(byte value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public byte get(byte key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public byte put(byte key, byte value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public byte remove(byte key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Byte, ? extends Byte> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(TByteByteMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TByteSet keySet = null;
    private transient TByteCollection values = null;
    
    public gnu.trove.set.TByteSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedByteSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public byte[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public byte[] keys(byte[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TByteCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedByteCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public byte[] values() { synchronized (mutex) { return m.values();
      } }
    
    public byte[] values(byte[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TByteByteIterator iterator() { return m.iterator(); }
    


    public byte getNoEntryKey() { return m.getNoEntryKey(); }
    public byte getNoEntryValue() { return m.getNoEntryValue(); }
    
    public byte putIfAbsent(byte key, byte value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TByteProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TByteProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TByteByteProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TByteFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TByteByteProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(byte key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(byte key, byte amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public byte adjustOrPutValue(byte key, byte adjust_amount, byte put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedByteShortMap
    implements TByteShortMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final TByteShortMap m;
    final Object mutex;
    
    public TSynchronizedByteShortMap(TByteShortMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedByteShortMap(TByteShortMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(byte key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(short value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public short get(byte key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public short put(byte key, short value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public short remove(byte key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Byte, ? extends Short> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(TByteShortMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TByteSet keySet = null;
    private transient TShortCollection values = null;
    
    public gnu.trove.set.TByteSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedByteSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public byte[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public byte[] keys(byte[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TShortCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedShortCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public short[] values() { synchronized (mutex) { return m.values();
      } }
    
    public short[] values(short[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TByteShortIterator iterator() { return m.iterator(); }
    


    public byte getNoEntryKey() { return m.getNoEntryKey(); }
    public short getNoEntryValue() { return m.getNoEntryValue(); }
    
    public short putIfAbsent(byte key, short value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TByteProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TShortProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TByteShortProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TShortFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TByteShortProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(byte key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(byte key, short amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public short adjustOrPutValue(byte key, short adjust_amount, short put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedByteCharMap
    implements TByteCharMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final TByteCharMap m;
    final Object mutex;
    
    public TSynchronizedByteCharMap(TByteCharMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedByteCharMap(TByteCharMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(byte key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(char value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public char get(byte key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public char put(byte key, char value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public char remove(byte key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Byte, ? extends Character> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(TByteCharMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TByteSet keySet = null;
    private transient TCharCollection values = null;
    
    public gnu.trove.set.TByteSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedByteSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public byte[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public byte[] keys(byte[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TCharCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedCharCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public char[] values() { synchronized (mutex) { return m.values();
      } }
    
    public char[] values(char[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TByteCharIterator iterator() { return m.iterator(); }
    


    public byte getNoEntryKey() { return m.getNoEntryKey(); }
    public char getNoEntryValue() { return m.getNoEntryValue(); }
    
    public char putIfAbsent(byte key, char value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TByteProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TCharProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TByteCharProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TCharFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TByteCharProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(byte key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(byte key, char amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public char adjustOrPutValue(byte key, char adjust_amount, char put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedShortDoubleMap
    implements TShortDoubleMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final TShortDoubleMap m;
    final Object mutex;
    
    public TSynchronizedShortDoubleMap(TShortDoubleMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedShortDoubleMap(TShortDoubleMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(short key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(double value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public double get(short key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public double put(short key, double value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public double remove(short key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Short, ? extends Double> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(TShortDoubleMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TShortSet keySet = null;
    private transient TDoubleCollection values = null;
    
    public gnu.trove.set.TShortSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedShortSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public short[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public short[] keys(short[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TDoubleCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedDoubleCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public double[] values() { synchronized (mutex) { return m.values();
      } }
    
    public double[] values(double[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TShortDoubleIterator iterator() { return m.iterator(); }
    


    public short getNoEntryKey() { return m.getNoEntryKey(); }
    public double getNoEntryValue() { return m.getNoEntryValue(); }
    
    public double putIfAbsent(short key, double value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TShortProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TDoubleProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TShortDoubleProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TDoubleFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TShortDoubleProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(short key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(short key, double amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public double adjustOrPutValue(short key, double adjust_amount, double put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedShortFloatMap
    implements gnu.trove.map.TShortFloatMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final gnu.trove.map.TShortFloatMap m;
    final Object mutex;
    
    public TSynchronizedShortFloatMap(gnu.trove.map.TShortFloatMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedShortFloatMap(gnu.trove.map.TShortFloatMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(short key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(float value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public float get(short key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public float put(short key, float value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public float remove(short key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Short, ? extends Float> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(gnu.trove.map.TShortFloatMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TShortSet keySet = null;
    private transient TFloatCollection values = null;
    
    public gnu.trove.set.TShortSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedShortSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public short[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public short[] keys(short[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TFloatCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedFloatCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public float[] values() { synchronized (mutex) { return m.values();
      } }
    
    public float[] values(float[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TShortFloatIterator iterator() { return m.iterator(); }
    


    public short getNoEntryKey() { return m.getNoEntryKey(); }
    public float getNoEntryValue() { return m.getNoEntryValue(); }
    
    public float putIfAbsent(short key, float value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TShortProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TFloatProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TShortFloatProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TFloatFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TShortFloatProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(short key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(short key, float amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public float adjustOrPutValue(short key, float adjust_amount, float put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedShortIntMap
    implements gnu.trove.map.TShortIntMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final gnu.trove.map.TShortIntMap m;
    final Object mutex;
    
    public TSynchronizedShortIntMap(gnu.trove.map.TShortIntMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedShortIntMap(gnu.trove.map.TShortIntMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(short key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(int value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public int get(short key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public int put(short key, int value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public int remove(short key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Short, ? extends Integer> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(gnu.trove.map.TShortIntMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TShortSet keySet = null;
    private transient TIntCollection values = null;
    
    public gnu.trove.set.TShortSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedShortSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public short[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public short[] keys(short[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TIntCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedIntCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public int[] values() { synchronized (mutex) { return m.values();
      } }
    
    public int[] values(int[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TShortIntIterator iterator() { return m.iterator(); }
    


    public short getNoEntryKey() { return m.getNoEntryKey(); }
    public int getNoEntryValue() { return m.getNoEntryValue(); }
    
    public int putIfAbsent(short key, int value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TShortProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TIntProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TShortIntProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TIntFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TShortIntProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(short key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(short key, int amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public int adjustOrPutValue(short key, int adjust_amount, int put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedShortLongMap
    implements TShortLongMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final TShortLongMap m;
    final Object mutex;
    
    public TSynchronizedShortLongMap(TShortLongMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedShortLongMap(TShortLongMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(short key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(long value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public long get(short key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public long put(short key, long value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public long remove(short key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Short, ? extends Long> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(TShortLongMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TShortSet keySet = null;
    private transient TLongCollection values = null;
    
    public gnu.trove.set.TShortSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedShortSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public short[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public short[] keys(short[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TLongCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedLongCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public long[] values() { synchronized (mutex) { return m.values();
      } }
    
    public long[] values(long[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TShortLongIterator iterator() { return m.iterator(); }
    


    public short getNoEntryKey() { return m.getNoEntryKey(); }
    public long getNoEntryValue() { return m.getNoEntryValue(); }
    
    public long putIfAbsent(short key, long value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TShortProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TLongProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TShortLongProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TLongFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TShortLongProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(short key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(short key, long amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public long adjustOrPutValue(short key, long adjust_amount, long put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedShortByteMap
    implements gnu.trove.map.TShortByteMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final gnu.trove.map.TShortByteMap m;
    final Object mutex;
    
    public TSynchronizedShortByteMap(gnu.trove.map.TShortByteMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedShortByteMap(gnu.trove.map.TShortByteMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(short key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(byte value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public byte get(short key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public byte put(short key, byte value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public byte remove(short key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Short, ? extends Byte> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(gnu.trove.map.TShortByteMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TShortSet keySet = null;
    private transient TByteCollection values = null;
    
    public gnu.trove.set.TShortSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedShortSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public short[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public short[] keys(short[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TByteCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedByteCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public byte[] values() { synchronized (mutex) { return m.values();
      } }
    
    public byte[] values(byte[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TShortByteIterator iterator() { return m.iterator(); }
    


    public short getNoEntryKey() { return m.getNoEntryKey(); }
    public byte getNoEntryValue() { return m.getNoEntryValue(); }
    
    public byte putIfAbsent(short key, byte value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TShortProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TByteProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TShortByteProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TByteFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TShortByteProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(short key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(short key, byte amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public byte adjustOrPutValue(short key, byte adjust_amount, byte put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedShortShortMap
    implements TShortShortMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final TShortShortMap m;
    final Object mutex;
    
    public TSynchronizedShortShortMap(TShortShortMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedShortShortMap(TShortShortMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(short key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(short value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public short get(short key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public short put(short key, short value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public short remove(short key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Short, ? extends Short> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(TShortShortMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TShortSet keySet = null;
    private transient TShortCollection values = null;
    
    public gnu.trove.set.TShortSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedShortSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public short[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public short[] keys(short[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TShortCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedShortCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public short[] values() { synchronized (mutex) { return m.values();
      } }
    
    public short[] values(short[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TShortShortIterator iterator() { return m.iterator(); }
    


    public short getNoEntryKey() { return m.getNoEntryKey(); }
    public short getNoEntryValue() { return m.getNoEntryValue(); }
    
    public short putIfAbsent(short key, short value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TShortProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TShortProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TShortShortProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TShortFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TShortShortProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(short key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(short key, short amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public short adjustOrPutValue(short key, short adjust_amount, short put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedShortCharMap
    implements gnu.trove.map.TShortCharMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final gnu.trove.map.TShortCharMap m;
    final Object mutex;
    
    public TSynchronizedShortCharMap(gnu.trove.map.TShortCharMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedShortCharMap(gnu.trove.map.TShortCharMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(short key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(char value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public char get(short key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public char put(short key, char value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public char remove(short key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Short, ? extends Character> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(gnu.trove.map.TShortCharMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TShortSet keySet = null;
    private transient TCharCollection values = null;
    
    public gnu.trove.set.TShortSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedShortSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public short[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public short[] keys(short[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TCharCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedCharCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public char[] values() { synchronized (mutex) { return m.values();
      } }
    
    public char[] values(char[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TShortCharIterator iterator() { return m.iterator(); }
    


    public short getNoEntryKey() { return m.getNoEntryKey(); }
    public char getNoEntryValue() { return m.getNoEntryValue(); }
    
    public char putIfAbsent(short key, char value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TShortProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TCharProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TShortCharProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TCharFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TShortCharProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(short key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(short key, char amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public char adjustOrPutValue(short key, char adjust_amount, char put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedCharDoubleMap
    implements TCharDoubleMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final TCharDoubleMap m;
    final Object mutex;
    
    public TSynchronizedCharDoubleMap(TCharDoubleMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedCharDoubleMap(TCharDoubleMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(char key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(double value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public double get(char key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public double put(char key, double value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public double remove(char key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Character, ? extends Double> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(TCharDoubleMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TCharSet keySet = null;
    private transient TDoubleCollection values = null;
    
    public gnu.trove.set.TCharSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedCharSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public char[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public char[] keys(char[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TDoubleCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedDoubleCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public double[] values() { synchronized (mutex) { return m.values();
      } }
    
    public double[] values(double[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TCharDoubleIterator iterator() { return m.iterator(); }
    


    public char getNoEntryKey() { return m.getNoEntryKey(); }
    public double getNoEntryValue() { return m.getNoEntryValue(); }
    
    public double putIfAbsent(char key, double value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TCharProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TDoubleProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TCharDoubleProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TDoubleFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TCharDoubleProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(char key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(char key, double amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public double adjustOrPutValue(char key, double adjust_amount, double put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedCharFloatMap
    implements gnu.trove.map.TCharFloatMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final gnu.trove.map.TCharFloatMap m;
    final Object mutex;
    
    public TSynchronizedCharFloatMap(gnu.trove.map.TCharFloatMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedCharFloatMap(gnu.trove.map.TCharFloatMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(char key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(float value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public float get(char key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public float put(char key, float value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public float remove(char key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Character, ? extends Float> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(gnu.trove.map.TCharFloatMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TCharSet keySet = null;
    private transient TFloatCollection values = null;
    
    public gnu.trove.set.TCharSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedCharSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public char[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public char[] keys(char[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TFloatCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedFloatCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public float[] values() { synchronized (mutex) { return m.values();
      } }
    
    public float[] values(float[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TCharFloatIterator iterator() { return m.iterator(); }
    


    public char getNoEntryKey() { return m.getNoEntryKey(); }
    public float getNoEntryValue() { return m.getNoEntryValue(); }
    
    public float putIfAbsent(char key, float value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TCharProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TFloatProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TCharFloatProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TFloatFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TCharFloatProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(char key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(char key, float amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public float adjustOrPutValue(char key, float adjust_amount, float put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedCharIntMap
    implements gnu.trove.map.TCharIntMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final gnu.trove.map.TCharIntMap m;
    final Object mutex;
    
    public TSynchronizedCharIntMap(gnu.trove.map.TCharIntMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedCharIntMap(gnu.trove.map.TCharIntMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(char key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(int value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public int get(char key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public int put(char key, int value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public int remove(char key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Character, ? extends Integer> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(gnu.trove.map.TCharIntMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TCharSet keySet = null;
    private transient TIntCollection values = null;
    
    public gnu.trove.set.TCharSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedCharSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public char[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public char[] keys(char[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TIntCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedIntCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public int[] values() { synchronized (mutex) { return m.values();
      } }
    
    public int[] values(int[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TCharIntIterator iterator() { return m.iterator(); }
    


    public char getNoEntryKey() { return m.getNoEntryKey(); }
    public int getNoEntryValue() { return m.getNoEntryValue(); }
    
    public int putIfAbsent(char key, int value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TCharProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TIntProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TCharIntProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TIntFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TCharIntProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(char key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(char key, int amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public int adjustOrPutValue(char key, int adjust_amount, int put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedCharLongMap
    implements TCharLongMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final TCharLongMap m;
    final Object mutex;
    
    public TSynchronizedCharLongMap(TCharLongMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedCharLongMap(TCharLongMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(char key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(long value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public long get(char key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public long put(char key, long value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public long remove(char key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Character, ? extends Long> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(TCharLongMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TCharSet keySet = null;
    private transient TLongCollection values = null;
    
    public gnu.trove.set.TCharSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedCharSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public char[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public char[] keys(char[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TLongCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedLongCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public long[] values() { synchronized (mutex) { return m.values();
      } }
    
    public long[] values(long[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TCharLongIterator iterator() { return m.iterator(); }
    


    public char getNoEntryKey() { return m.getNoEntryKey(); }
    public long getNoEntryValue() { return m.getNoEntryValue(); }
    
    public long putIfAbsent(char key, long value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TCharProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TLongProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TCharLongProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TLongFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TCharLongProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(char key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(char key, long amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public long adjustOrPutValue(char key, long adjust_amount, long put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedCharByteMap
    implements gnu.trove.map.TCharByteMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final gnu.trove.map.TCharByteMap m;
    final Object mutex;
    
    public TSynchronizedCharByteMap(gnu.trove.map.TCharByteMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedCharByteMap(gnu.trove.map.TCharByteMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(char key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(byte value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public byte get(char key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public byte put(char key, byte value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public byte remove(char key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Character, ? extends Byte> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(gnu.trove.map.TCharByteMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TCharSet keySet = null;
    private transient TByteCollection values = null;
    
    public gnu.trove.set.TCharSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedCharSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public char[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public char[] keys(char[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TByteCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedByteCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public byte[] values() { synchronized (mutex) { return m.values();
      } }
    
    public byte[] values(byte[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TCharByteIterator iterator() { return m.iterator(); }
    


    public char getNoEntryKey() { return m.getNoEntryKey(); }
    public byte getNoEntryValue() { return m.getNoEntryValue(); }
    
    public byte putIfAbsent(char key, byte value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TCharProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TByteProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TCharByteProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TByteFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TCharByteProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(char key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(char key, byte amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public byte adjustOrPutValue(char key, byte adjust_amount, byte put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedCharShortMap
    implements TCharShortMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final TCharShortMap m;
    final Object mutex;
    
    public TSynchronizedCharShortMap(TCharShortMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedCharShortMap(TCharShortMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(char key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(short value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public short get(char key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public short put(char key, short value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public short remove(char key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Character, ? extends Short> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(TCharShortMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TCharSet keySet = null;
    private transient TShortCollection values = null;
    
    public gnu.trove.set.TCharSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedCharSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public char[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public char[] keys(char[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TShortCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedShortCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public short[] values() { synchronized (mutex) { return m.values();
      } }
    
    public short[] values(short[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TCharShortIterator iterator() { return m.iterator(); }
    


    public char getNoEntryKey() { return m.getNoEntryKey(); }
    public short getNoEntryValue() { return m.getNoEntryValue(); }
    
    public short putIfAbsent(char key, short value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TCharProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TShortProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TCharShortProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TShortFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TCharShortProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(char key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(char key, short amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public short adjustOrPutValue(char key, short adjust_amount, short put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedCharCharMap
    implements TCharCharMap, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final TCharCharMap m;
    final Object mutex;
    
    public TSynchronizedCharCharMap(TCharCharMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedCharCharMap(TCharCharMap m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(char key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(char value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public char get(char key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public char put(char key, char value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public char remove(char key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Character, ? extends Character> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(TCharCharMap map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TCharSet keySet = null;
    private transient TCharCollection values = null;
    
    public gnu.trove.set.TCharSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedCharSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public char[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public char[] keys(char[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TCharCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedCharCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public char[] values() { synchronized (mutex) { return m.values();
      } }
    
    public char[] values(char[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TCharCharIterator iterator() { return m.iterator(); }
    


    public char getNoEntryKey() { return m.getNoEntryKey(); }
    public char getNoEntryValue() { return m.getNoEntryValue(); }
    
    public char putIfAbsent(char key, char value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TCharProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TCharProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TCharCharProcedure procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TCharFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TCharCharProcedure procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(char key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(char key, char amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public char adjustOrPutValue(char key, char adjust_amount, char put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedDoubleObjectMap<V>
    implements gnu.trove.map.TDoubleObjectMap<V>, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final gnu.trove.map.TDoubleObjectMap<V> m;
    
    final Object mutex;
    
    public TSynchronizedDoubleObjectMap(gnu.trove.map.TDoubleObjectMap<V> m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedDoubleObjectMap(gnu.trove.map.TDoubleObjectMap<V> m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(double key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(Object value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public V get(double key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public V put(double key, V value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public V remove(double key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Double, ? extends V> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(gnu.trove.map.TDoubleObjectMap<V> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TDoubleSet keySet = null;
    private transient Collection<V> values = null;
    
    public gnu.trove.set.TDoubleSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedDoubleSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public double[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public double[] keys(double[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public Collection<V> valueCollection() { synchronized (mutex) {
        if (values == null) {
          values = new TSynchronizedCollections.SynchronizedCollection(m.valueCollection(), mutex);
        }
        return values;
      }
    }
    
    public V[] values() { synchronized (mutex) { return m.values();
      } }
    
    public <T> T[] values(T[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TDoubleObjectIterator<V> iterator() { return m.iterator(); }
    


    public double getNoEntryKey() { return m.getNoEntryKey(); }
    
    public V putIfAbsent(double key, V value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TDoubleProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(gnu.trove.procedure.TObjectProcedure<V> procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TDoubleObjectProcedure<V> procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TObjectFunction<V, V> function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TDoubleObjectProcedure<V> procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedFloatObjectMap<V>
    implements gnu.trove.map.TFloatObjectMap<V>, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final gnu.trove.map.TFloatObjectMap<V> m;
    final Object mutex;
    
    public TSynchronizedFloatObjectMap(gnu.trove.map.TFloatObjectMap<V> m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedFloatObjectMap(gnu.trove.map.TFloatObjectMap<V> m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(float key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(Object value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public V get(float key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public V put(float key, V value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public V remove(float key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Float, ? extends V> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(gnu.trove.map.TFloatObjectMap<V> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TFloatSet keySet = null;
    private transient Collection<V> values = null;
    
    public gnu.trove.set.TFloatSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedFloatSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public float[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public float[] keys(float[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public Collection<V> valueCollection() { synchronized (mutex) {
        if (values == null) {
          values = new TSynchronizedCollections.SynchronizedCollection(m.valueCollection(), mutex);
        }
        return values;
      }
    }
    
    public V[] values() { synchronized (mutex) { return m.values();
      } }
    
    public <T> T[] values(T[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TFloatObjectIterator<V> iterator() { return m.iterator(); }
    


    public float getNoEntryKey() { return m.getNoEntryKey(); }
    
    public V putIfAbsent(float key, V value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TFloatProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(gnu.trove.procedure.TObjectProcedure<V> procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TFloatObjectProcedure<V> procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TObjectFunction<V, V> function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TFloatObjectProcedure<V> procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedIntObjectMap<V>
    implements gnu.trove.map.TIntObjectMap<V>, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final gnu.trove.map.TIntObjectMap<V> m;
    final Object mutex;
    
    public TSynchronizedIntObjectMap(gnu.trove.map.TIntObjectMap<V> m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedIntObjectMap(gnu.trove.map.TIntObjectMap<V> m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(int key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(Object value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public V get(int key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public V put(int key, V value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public V remove(int key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Integer, ? extends V> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(gnu.trove.map.TIntObjectMap<V> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TIntSet keySet = null;
    private transient Collection<V> values = null;
    
    public gnu.trove.set.TIntSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedIntSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public int[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public int[] keys(int[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public Collection<V> valueCollection() { synchronized (mutex) {
        if (values == null) {
          values = new TSynchronizedCollections.SynchronizedCollection(m.valueCollection(), mutex);
        }
        return values;
      }
    }
    
    public V[] values() { synchronized (mutex) { return m.values();
      } }
    
    public <T> T[] values(T[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TIntObjectIterator<V> iterator() { return m.iterator(); }
    


    public int getNoEntryKey() { return m.getNoEntryKey(); }
    
    public V putIfAbsent(int key, V value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TIntProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(gnu.trove.procedure.TObjectProcedure<V> procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TIntObjectProcedure<V> procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TObjectFunction<V, V> function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TIntObjectProcedure<V> procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedLongObjectMap<V>
    implements gnu.trove.map.TLongObjectMap<V>, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final gnu.trove.map.TLongObjectMap<V> m;
    final Object mutex;
    
    public TSynchronizedLongObjectMap(gnu.trove.map.TLongObjectMap<V> m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedLongObjectMap(gnu.trove.map.TLongObjectMap<V> m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(long key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(Object value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public V get(long key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public V put(long key, V value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public V remove(long key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Long, ? extends V> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(gnu.trove.map.TLongObjectMap<V> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TLongSet keySet = null;
    private transient Collection<V> values = null;
    
    public gnu.trove.set.TLongSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedLongSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public long[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public long[] keys(long[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public Collection<V> valueCollection() { synchronized (mutex) {
        if (values == null) {
          values = new TSynchronizedCollections.SynchronizedCollection(m.valueCollection(), mutex);
        }
        return values;
      }
    }
    
    public V[] values() { synchronized (mutex) { return m.values();
      } }
    
    public <T> T[] values(T[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TLongObjectIterator<V> iterator() { return m.iterator(); }
    


    public long getNoEntryKey() { return m.getNoEntryKey(); }
    
    public V putIfAbsent(long key, V value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TLongProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(gnu.trove.procedure.TObjectProcedure<V> procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TLongObjectProcedure<V> procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TObjectFunction<V, V> function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TLongObjectProcedure<V> procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedByteObjectMap<V>
    implements gnu.trove.map.TByteObjectMap<V>, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final gnu.trove.map.TByteObjectMap<V> m;
    final Object mutex;
    
    public TSynchronizedByteObjectMap(gnu.trove.map.TByteObjectMap<V> m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedByteObjectMap(gnu.trove.map.TByteObjectMap<V> m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(byte key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(Object value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public V get(byte key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public V put(byte key, V value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public V remove(byte key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Byte, ? extends V> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(gnu.trove.map.TByteObjectMap<V> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TByteSet keySet = null;
    private transient Collection<V> values = null;
    
    public gnu.trove.set.TByteSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedByteSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public byte[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public byte[] keys(byte[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public Collection<V> valueCollection() { synchronized (mutex) {
        if (values == null) {
          values = new TSynchronizedCollections.SynchronizedCollection(m.valueCollection(), mutex);
        }
        return values;
      }
    }
    
    public V[] values() { synchronized (mutex) { return m.values();
      } }
    
    public <T> T[] values(T[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TByteObjectIterator<V> iterator() { return m.iterator(); }
    


    public byte getNoEntryKey() { return m.getNoEntryKey(); }
    
    public V putIfAbsent(byte key, V value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TByteProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(gnu.trove.procedure.TObjectProcedure<V> procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TByteObjectProcedure<V> procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TObjectFunction<V, V> function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TByteObjectProcedure<V> procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedShortObjectMap<V>
    implements gnu.trove.map.TShortObjectMap<V>, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final gnu.trove.map.TShortObjectMap<V> m;
    final Object mutex;
    
    public TSynchronizedShortObjectMap(gnu.trove.map.TShortObjectMap<V> m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedShortObjectMap(gnu.trove.map.TShortObjectMap<V> m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(short key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(Object value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public V get(short key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public V put(short key, V value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public V remove(short key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Short, ? extends V> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(gnu.trove.map.TShortObjectMap<V> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TShortSet keySet = null;
    private transient Collection<V> values = null;
    
    public gnu.trove.set.TShortSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedShortSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public short[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public short[] keys(short[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public Collection<V> valueCollection() { synchronized (mutex) {
        if (values == null) {
          values = new TSynchronizedCollections.SynchronizedCollection(m.valueCollection(), mutex);
        }
        return values;
      }
    }
    
    public V[] values() { synchronized (mutex) { return m.values();
      } }
    
    public <T> T[] values(T[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TShortObjectIterator<V> iterator() { return m.iterator(); }
    


    public short getNoEntryKey() { return m.getNoEntryKey(); }
    
    public V putIfAbsent(short key, V value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TShortProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(gnu.trove.procedure.TObjectProcedure<V> procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TShortObjectProcedure<V> procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TObjectFunction<V, V> function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TShortObjectProcedure<V> procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedCharObjectMap<V>
    implements gnu.trove.map.TCharObjectMap<V>, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final gnu.trove.map.TCharObjectMap<V> m;
    final Object mutex;
    
    public TSynchronizedCharObjectMap(gnu.trove.map.TCharObjectMap<V> m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedCharObjectMap(gnu.trove.map.TCharObjectMap<V> m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(char key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(Object value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public V get(char key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public V put(char key, V value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public V remove(char key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends Character, ? extends V> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(gnu.trove.map.TCharObjectMap<V> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient gnu.trove.set.TCharSet keySet = null;
    private transient Collection<V> values = null;
    
    public gnu.trove.set.TCharSet keySet() {
      synchronized (mutex) {
        if (keySet == null)
          keySet = new TSynchronizedCollections.TSynchronizedCharSet(m.keySet(), mutex);
        return keySet;
      }
    }
    
    public char[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public char[] keys(char[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public Collection<V> valueCollection() { synchronized (mutex) {
        if (values == null) {
          values = new TSynchronizedCollections.SynchronizedCollection(m.valueCollection(), mutex);
        }
        return values;
      }
    }
    
    public V[] values() { synchronized (mutex) { return m.values();
      } }
    
    public <T> T[] values(T[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TCharObjectIterator<V> iterator() { return m.iterator(); }
    


    public char getNoEntryKey() { return m.getNoEntryKey(); }
    
    public V putIfAbsent(char key, V value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(TCharProcedure procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(gnu.trove.procedure.TObjectProcedure<V> procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TCharObjectProcedure<V> procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TObjectFunction<V, V> function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TCharObjectProcedure<V> procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedObjectDoubleMap<K>
    implements gnu.trove.map.TObjectDoubleMap<K>, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final gnu.trove.map.TObjectDoubleMap<K> m;
    
    final Object mutex;
    
    public TSynchronizedObjectDoubleMap(gnu.trove.map.TObjectDoubleMap<K> m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedObjectDoubleMap(gnu.trove.map.TObjectDoubleMap<K> m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(Object key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(double value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public double get(Object key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public double put(K key, double value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public double remove(Object key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends K, ? extends Double> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(gnu.trove.map.TObjectDoubleMap<K> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient java.util.Set<K> keySet = null;
    private transient TDoubleCollection values = null;
    
    public java.util.Set<K> keySet() {
      synchronized (mutex) {
        if (keySet == null) {
          keySet = new TSynchronizedCollections.SynchronizedSet(m.keySet(), mutex);
        }
        return keySet;
      }
    }
    
    public Object[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public K[] keys(K[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TDoubleCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedDoubleCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public double[] values() { synchronized (mutex) { return m.values();
      } }
    
    public double[] values(double[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TObjectDoubleIterator<K> iterator() { return m.iterator(); }
    


    public double getNoEntryValue() { return m.getNoEntryValue(); }
    
    public double putIfAbsent(K key, double value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(gnu.trove.procedure.TObjectProcedure<K> procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TDoubleProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TObjectDoubleProcedure<K> procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TDoubleFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TObjectDoubleProcedure<K> procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(K key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(K key, double amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public double adjustOrPutValue(K key, double adjust_amount, double put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedObjectFloatMap<K>
    implements gnu.trove.map.TObjectFloatMap<K>, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final gnu.trove.map.TObjectFloatMap<K> m;
    final Object mutex;
    
    public TSynchronizedObjectFloatMap(gnu.trove.map.TObjectFloatMap<K> m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedObjectFloatMap(gnu.trove.map.TObjectFloatMap<K> m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(Object key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(float value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public float get(Object key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public float put(K key, float value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public float remove(Object key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends K, ? extends Float> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(gnu.trove.map.TObjectFloatMap<K> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient java.util.Set<K> keySet = null;
    private transient TFloatCollection values = null;
    
    public java.util.Set<K> keySet() {
      synchronized (mutex) {
        if (keySet == null) {
          keySet = new TSynchronizedCollections.SynchronizedSet(m.keySet(), mutex);
        }
        return keySet;
      }
    }
    
    public Object[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public K[] keys(K[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TFloatCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedFloatCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public float[] values() { synchronized (mutex) { return m.values();
      } }
    
    public float[] values(float[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TObjectFloatIterator<K> iterator() { return m.iterator(); }
    


    public float getNoEntryValue() { return m.getNoEntryValue(); }
    
    public float putIfAbsent(K key, float value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(gnu.trove.procedure.TObjectProcedure<K> procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TFloatProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TObjectFloatProcedure<K> procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TFloatFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TObjectFloatProcedure<K> procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(K key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(K key, float amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public float adjustOrPutValue(K key, float adjust_amount, float put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedObjectIntMap<K>
    implements gnu.trove.map.TObjectIntMap<K>, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final gnu.trove.map.TObjectIntMap<K> m;
    final Object mutex;
    
    public TSynchronizedObjectIntMap(gnu.trove.map.TObjectIntMap<K> m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedObjectIntMap(gnu.trove.map.TObjectIntMap<K> m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(Object key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(int value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public int get(Object key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public int put(K key, int value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public int remove(Object key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends K, ? extends Integer> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(gnu.trove.map.TObjectIntMap<K> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient java.util.Set<K> keySet = null;
    private transient TIntCollection values = null;
    
    public java.util.Set<K> keySet() {
      synchronized (mutex) {
        if (keySet == null) {
          keySet = new TSynchronizedCollections.SynchronizedSet(m.keySet(), mutex);
        }
        return keySet;
      }
    }
    
    public Object[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public K[] keys(K[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TIntCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedIntCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public int[] values() { synchronized (mutex) { return m.values();
      } }
    
    public int[] values(int[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TObjectIntIterator<K> iterator() { return m.iterator(); }
    


    public int getNoEntryValue() { return m.getNoEntryValue(); }
    
    public int putIfAbsent(K key, int value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(gnu.trove.procedure.TObjectProcedure<K> procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TIntProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TObjectIntProcedure<K> procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TIntFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TObjectIntProcedure<K> procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(K key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(K key, int amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public int adjustOrPutValue(K key, int adjust_amount, int put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedObjectLongMap<K>
    implements gnu.trove.map.TObjectLongMap<K>, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final gnu.trove.map.TObjectLongMap<K> m;
    final Object mutex;
    
    public TSynchronizedObjectLongMap(gnu.trove.map.TObjectLongMap<K> m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedObjectLongMap(gnu.trove.map.TObjectLongMap<K> m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(Object key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(long value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public long get(Object key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public long put(K key, long value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public long remove(Object key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends K, ? extends Long> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(gnu.trove.map.TObjectLongMap<K> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient java.util.Set<K> keySet = null;
    private transient TLongCollection values = null;
    
    public java.util.Set<K> keySet() {
      synchronized (mutex) {
        if (keySet == null) {
          keySet = new TSynchronizedCollections.SynchronizedSet(m.keySet(), mutex);
        }
        return keySet;
      }
    }
    
    public Object[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public K[] keys(K[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TLongCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedLongCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public long[] values() { synchronized (mutex) { return m.values();
      } }
    
    public long[] values(long[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TObjectLongIterator<K> iterator() { return m.iterator(); }
    


    public long getNoEntryValue() { return m.getNoEntryValue(); }
    
    public long putIfAbsent(K key, long value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(gnu.trove.procedure.TObjectProcedure<K> procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TLongProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TObjectLongProcedure<K> procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TLongFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TObjectLongProcedure<K> procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(K key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(K key, long amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public long adjustOrPutValue(K key, long adjust_amount, long put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedObjectByteMap<K>
    implements gnu.trove.map.TObjectByteMap<K>, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final gnu.trove.map.TObjectByteMap<K> m;
    final Object mutex;
    
    public TSynchronizedObjectByteMap(gnu.trove.map.TObjectByteMap<K> m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedObjectByteMap(gnu.trove.map.TObjectByteMap<K> m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(Object key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(byte value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public byte get(Object key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public byte put(K key, byte value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public byte remove(Object key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends K, ? extends Byte> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(gnu.trove.map.TObjectByteMap<K> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient java.util.Set<K> keySet = null;
    private transient TByteCollection values = null;
    
    public java.util.Set<K> keySet() {
      synchronized (mutex) {
        if (keySet == null) {
          keySet = new TSynchronizedCollections.SynchronizedSet(m.keySet(), mutex);
        }
        return keySet;
      }
    }
    
    public Object[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public K[] keys(K[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TByteCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedByteCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public byte[] values() { synchronized (mutex) { return m.values();
      } }
    
    public byte[] values(byte[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TObjectByteIterator<K> iterator() { return m.iterator(); }
    


    public byte getNoEntryValue() { return m.getNoEntryValue(); }
    
    public byte putIfAbsent(K key, byte value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(gnu.trove.procedure.TObjectProcedure<K> procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TByteProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TObjectByteProcedure<K> procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TByteFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TObjectByteProcedure<K> procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(K key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(K key, byte amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public byte adjustOrPutValue(K key, byte adjust_amount, byte put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedObjectShortMap<K>
    implements gnu.trove.map.TObjectShortMap<K>, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final gnu.trove.map.TObjectShortMap<K> m;
    final Object mutex;
    
    public TSynchronizedObjectShortMap(gnu.trove.map.TObjectShortMap<K> m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedObjectShortMap(gnu.trove.map.TObjectShortMap<K> m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(Object key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(short value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public short get(Object key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public short put(K key, short value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public short remove(Object key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends K, ? extends Short> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(gnu.trove.map.TObjectShortMap<K> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient java.util.Set<K> keySet = null;
    private transient TShortCollection values = null;
    
    public java.util.Set<K> keySet() {
      synchronized (mutex) {
        if (keySet == null) {
          keySet = new TSynchronizedCollections.SynchronizedSet(m.keySet(), mutex);
        }
        return keySet;
      }
    }
    
    public Object[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public K[] keys(K[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TShortCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedShortCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public short[] values() { synchronized (mutex) { return m.values();
      } }
    
    public short[] values(short[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TObjectShortIterator<K> iterator() { return m.iterator(); }
    


    public short getNoEntryValue() { return m.getNoEntryValue(); }
    
    public short putIfAbsent(K key, short value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(gnu.trove.procedure.TObjectProcedure<K> procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TShortProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TObjectShortProcedure<K> procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TShortFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TObjectShortProcedure<K> procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(K key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(K key, short amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public short adjustOrPutValue(K key, short adjust_amount, short put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  

  public static class TSynchronizedObjectCharMap<K>
    implements gnu.trove.map.TObjectCharMap<K>, Serializable
  {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final gnu.trove.map.TObjectCharMap<K> m;
    final Object mutex;
    
    public TSynchronizedObjectCharMap(gnu.trove.map.TObjectCharMap<K> m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
      mutex = this;
    }
    
    public TSynchronizedObjectCharMap(gnu.trove.map.TObjectCharMap<K> m, Object mutex) {
      this.m = m;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return m.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return m.isEmpty();
      } }
    
    public boolean containsKey(Object key) { synchronized (mutex) { return m.containsKey(key);
      } }
    
    public boolean containsValue(char value) { synchronized (mutex) { return m.containsValue(value);
      } }
    
    public char get(Object key) { synchronized (mutex) { return m.get(key);
      }
    }
    
    public char put(K key, char value) { synchronized (mutex) { return m.put(key, value);
      } }
    
    public char remove(Object key) { synchronized (mutex) { return m.remove(key);
      } }
    
    public void putAll(Map<? extends K, ? extends Character> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void putAll(gnu.trove.map.TObjectCharMap<K> map) { synchronized (mutex) { m.putAll(map);
      } }
    
    public void clear() { synchronized (mutex) { m.clear();
      } }
    
    private transient java.util.Set<K> keySet = null;
    private transient TCharCollection values = null;
    
    public java.util.Set<K> keySet() {
      synchronized (mutex) {
        if (keySet == null) {
          keySet = new TSynchronizedCollections.SynchronizedSet(m.keySet(), mutex);
        }
        return keySet;
      }
    }
    
    public Object[] keys() { synchronized (mutex) { return m.keys();
      } }
    
    public K[] keys(K[] array) { synchronized (mutex) { return m.keys(array);
      }
    }
    
    public TCharCollection valueCollection() { synchronized (mutex) {
        if (values == null)
          values = new TSynchronizedCollections.TSynchronizedCharCollection(m.valueCollection(), mutex);
        return values;
      }
    }
    
    public char[] values() { synchronized (mutex) { return m.values();
      } }
    
    public char[] values(char[] array) { synchronized (mutex) { return m.values(array);
      }
    }
    
    public gnu.trove.iterator.TObjectCharIterator<K> iterator() { return m.iterator(); }
    


    public char getNoEntryValue() { return m.getNoEntryValue(); }
    
    public char putIfAbsent(K key, char value) {
      synchronized (mutex) { return m.putIfAbsent(key, value);
      } }
    
    public boolean forEachKey(gnu.trove.procedure.TObjectProcedure<K> procedure) { synchronized (mutex) { return m.forEachKey(procedure);
      } }
    
    public boolean forEachValue(TCharProcedure procedure) { synchronized (mutex) { return m.forEachValue(procedure);
      } }
    
    public boolean forEachEntry(gnu.trove.procedure.TObjectCharProcedure<K> procedure) { synchronized (mutex) { return m.forEachEntry(procedure);
      } }
    
    public void transformValues(gnu.trove.function.TCharFunction function) { synchronized (mutex) { m.transformValues(function);
      } }
    
    public boolean retainEntries(gnu.trove.procedure.TObjectCharProcedure<K> procedure) { synchronized (mutex) { return m.retainEntries(procedure);
      } }
    
    public boolean increment(K key) { synchronized (mutex) { return m.increment(key);
      } }
    
    public boolean adjustValue(K key, char amount) { synchronized (mutex) { return m.adjustValue(key, amount);
      } }
    
    public char adjustOrPutValue(K key, char adjust_amount, char put_amount) { synchronized (mutex) { return m.adjustOrPutValue(key, adjust_amount, put_amount);
      }
    }
    
    public boolean equals(Object o) { synchronized (mutex) { return m.equals(o);
      } }
    
    public int hashCode() { synchronized (mutex) { return m.hashCode();
      } }
    
    public String toString() { synchronized (mutex) { return m.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  
  private static class SynchronizedCollection<E>
    implements Collection<E>, Serializable
  {
    private static final long serialVersionUID = 3053995032091335093L;
    final Collection<E> c;
    final Object mutex;
    
    SynchronizedCollection(Collection<E> c, Object mutex)
    {
      this.c = c;
      this.mutex = mutex;
    }
    
    public int size() {
      synchronized (mutex) { return c.size();
      } }
    
    public boolean isEmpty() { synchronized (mutex) { return c.isEmpty();
      } }
    
    public boolean contains(Object o) { synchronized (mutex) { return c.contains(o);
      } }
    
    public Object[] toArray() { synchronized (mutex) { return c.toArray();
      }
    }
    
    public <T> T[] toArray(T[] a) { synchronized (mutex) { return c.toArray(a);
      }
    }
    
    public java.util.Iterator<E> iterator() { return c.iterator(); }
    
    public boolean add(E e)
    {
      synchronized (mutex) { return c.add(e);
      } }
    
    public boolean remove(Object o) { synchronized (mutex) { return c.remove(o);
      }
    }
    
    public boolean containsAll(Collection<?> coll) { synchronized (mutex) { return c.containsAll(coll);
      } }
    
    public boolean addAll(Collection<? extends E> coll) { synchronized (mutex) { return c.addAll(coll);
      } }
    
    public boolean removeAll(Collection<?> coll) { synchronized (mutex) { return c.removeAll(coll);
      } }
    
    public boolean retainAll(Collection<?> coll) { synchronized (mutex) { return c.retainAll(coll);
      } }
    
    public void clear() { synchronized (mutex) { c.clear();
      } }
    
    public String toString() { synchronized (mutex) { return c.toString();
      } }
    
    private void writeObject(ObjectOutputStream s) throws IOException { synchronized (mutex) { s.defaultWriteObject();
      }
    }
  }
  
  private static class SynchronizedSet<E> extends TSynchronizedCollections.SynchronizedCollection<E>
    implements java.util.Set<E> {
    private static final long serialVersionUID = 487447009682186044L;
    
    SynchronizedSet(java.util.Set<E> s, Object mutex) { super(mutex); }
    public boolean equals(Object o) { synchronized (mutex) { return c.equals(o); } }
    public int hashCode() { synchronized (mutex) { return c.hashCode();
      }
    }
  }
}
