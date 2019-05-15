package gnu.trove.impl;

import gnu.trove.TByteCollection;
import gnu.trove.TCharCollection;
import gnu.trove.TCollections;
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
import gnu.trove.map.TByteCharMap;
import gnu.trove.map.TByteDoubleMap;
import gnu.trove.map.TByteLongMap;
import gnu.trove.map.TByteShortMap;
import gnu.trove.map.TCharDoubleMap;
import gnu.trove.map.TCharFloatMap;
import gnu.trove.map.TCharLongMap;
import gnu.trove.map.TCharShortMap;
import gnu.trove.map.TDoubleByteMap;
import gnu.trove.map.TDoubleDoubleMap;
import gnu.trove.map.TDoubleFloatMap;
import gnu.trove.map.TDoubleIntMap;
import gnu.trove.map.TFloatDoubleMap;
import gnu.trove.map.TFloatIntMap;
import gnu.trove.map.TFloatLongMap;
import gnu.trove.map.TFloatShortMap;
import gnu.trove.map.TIntByteMap;
import gnu.trove.map.TIntDoubleMap;
import gnu.trove.map.TIntFloatMap;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntShortMap;
import gnu.trove.map.TLongCharMap;
import gnu.trove.map.TLongDoubleMap;
import gnu.trove.map.TLongLongMap;
import gnu.trove.map.TLongShortMap;
import gnu.trove.map.TShortCharMap;
import gnu.trove.map.TShortDoubleMap;
import gnu.trove.map.TShortFloatMap;
import gnu.trove.map.TShortLongMap;
import gnu.trove.map.TShortShortMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.procedure.TShortProcedure;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

public class TUnmodifiableCollections
{
  private TUnmodifiableCollections() {}
  
  public static class TUnmodifiableDoubleCollection implements TDoubleCollection, Serializable
  {
    private static final long serialVersionUID = 1820017752578914078L;
    final TDoubleCollection c;
    
    public TUnmodifiableDoubleCollection(TDoubleCollection c)
    {
      if (c == null)
        throw new NullPointerException();
      this.c = c;
    }
    
    public int size() { return c.size(); }
    public boolean isEmpty() { return c.isEmpty(); }
    public boolean contains(double o) { return c.contains(o); }
    public double[] toArray() { return c.toArray(); }
    public double[] toArray(double[] a) { return c.toArray(a); }
    public String toString() { return c.toString(); }
    public double getNoEntryValue() { return c.getNoEntryValue(); }
    public boolean forEach(TDoubleProcedure procedure) { return c.forEach(procedure); }
    
    public gnu.trove.iterator.TDoubleIterator iterator() {
      new gnu.trove.iterator.TDoubleIterator() {
        gnu.trove.iterator.TDoubleIterator i = c.iterator();
        
        public boolean hasNext() { return i.hasNext(); }
        public double next() { return i.next(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public boolean add(double e) { throw new UnsupportedOperationException(); }
    public boolean remove(double o) { throw new UnsupportedOperationException(); }
    
    public boolean containsAll(Collection<?> coll) { return c.containsAll(coll); }
    public boolean containsAll(TDoubleCollection coll) { return c.containsAll(coll); }
    public boolean containsAll(double[] array) { return c.containsAll(array); }
    
    public boolean addAll(TDoubleCollection coll) { throw new UnsupportedOperationException(); }
    public boolean addAll(Collection<? extends Double> coll) { throw new UnsupportedOperationException(); }
    public boolean addAll(double[] array) { throw new UnsupportedOperationException(); }
    
    public boolean removeAll(Collection<?> coll) { throw new UnsupportedOperationException(); }
    public boolean removeAll(TDoubleCollection coll) { throw new UnsupportedOperationException(); }
    public boolean removeAll(double[] array) { throw new UnsupportedOperationException(); }
    
    public boolean retainAll(Collection<?> coll) { throw new UnsupportedOperationException(); }
    public boolean retainAll(TDoubleCollection coll) { throw new UnsupportedOperationException(); }
    public boolean retainAll(double[] array) { throw new UnsupportedOperationException(); }
    
    public void clear() { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableFloatCollection
    implements TFloatCollection, Serializable
  {
    private static final long serialVersionUID = 1820017752578914078L;
    
    final TFloatCollection c;
    
    public TUnmodifiableFloatCollection(TFloatCollection c)
    {
      if (c == null)
        throw new NullPointerException();
      this.c = c;
    }
    
    public int size() { return c.size(); }
    public boolean isEmpty() { return c.isEmpty(); }
    public boolean contains(float o) { return c.contains(o); }
    public float[] toArray() { return c.toArray(); }
    public float[] toArray(float[] a) { return c.toArray(a); }
    public String toString() { return c.toString(); }
    public float getNoEntryValue() { return c.getNoEntryValue(); }
    public boolean forEach(TFloatProcedure procedure) { return c.forEach(procedure); }
    
    public gnu.trove.iterator.TFloatIterator iterator() {
      new gnu.trove.iterator.TFloatIterator() {
        gnu.trove.iterator.TFloatIterator i = c.iterator();
        
        public boolean hasNext() { return i.hasNext(); }
        public float next() { return i.next(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public boolean add(float e) { throw new UnsupportedOperationException(); }
    public boolean remove(float o) { throw new UnsupportedOperationException(); }
    
    public boolean containsAll(Collection<?> coll) { return c.containsAll(coll); }
    public boolean containsAll(TFloatCollection coll) { return c.containsAll(coll); }
    public boolean containsAll(float[] array) { return c.containsAll(array); }
    
    public boolean addAll(TFloatCollection coll) { throw new UnsupportedOperationException(); }
    public boolean addAll(Collection<? extends Float> coll) { throw new UnsupportedOperationException(); }
    public boolean addAll(float[] array) { throw new UnsupportedOperationException(); }
    
    public boolean removeAll(Collection<?> coll) { throw new UnsupportedOperationException(); }
    public boolean removeAll(TFloatCollection coll) { throw new UnsupportedOperationException(); }
    public boolean removeAll(float[] array) { throw new UnsupportedOperationException(); }
    
    public boolean retainAll(Collection<?> coll) { throw new UnsupportedOperationException(); }
    public boolean retainAll(TFloatCollection coll) { throw new UnsupportedOperationException(); }
    public boolean retainAll(float[] array) { throw new UnsupportedOperationException(); }
    
    public void clear() { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableIntCollection
    implements TIntCollection, Serializable
  {
    private static final long serialVersionUID = 1820017752578914078L;
    
    final TIntCollection c;
    
    public TUnmodifiableIntCollection(TIntCollection c)
    {
      if (c == null)
        throw new NullPointerException();
      this.c = c;
    }
    
    public int size() { return c.size(); }
    public boolean isEmpty() { return c.isEmpty(); }
    public boolean contains(int o) { return c.contains(o); }
    public int[] toArray() { return c.toArray(); }
    public int[] toArray(int[] a) { return c.toArray(a); }
    public String toString() { return c.toString(); }
    public int getNoEntryValue() { return c.getNoEntryValue(); }
    public boolean forEach(TIntProcedure procedure) { return c.forEach(procedure); }
    
    public gnu.trove.iterator.TIntIterator iterator() {
      new gnu.trove.iterator.TIntIterator() {
        gnu.trove.iterator.TIntIterator i = c.iterator();
        
        public boolean hasNext() { return i.hasNext(); }
        public int next() { return i.next(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public boolean add(int e) { throw new UnsupportedOperationException(); }
    public boolean remove(int o) { throw new UnsupportedOperationException(); }
    
    public boolean containsAll(Collection<?> coll) { return c.containsAll(coll); }
    public boolean containsAll(TIntCollection coll) { return c.containsAll(coll); }
    public boolean containsAll(int[] array) { return c.containsAll(array); }
    
    public boolean addAll(TIntCollection coll) { throw new UnsupportedOperationException(); }
    public boolean addAll(Collection<? extends Integer> coll) { throw new UnsupportedOperationException(); }
    public boolean addAll(int[] array) { throw new UnsupportedOperationException(); }
    
    public boolean removeAll(Collection<?> coll) { throw new UnsupportedOperationException(); }
    public boolean removeAll(TIntCollection coll) { throw new UnsupportedOperationException(); }
    public boolean removeAll(int[] array) { throw new UnsupportedOperationException(); }
    
    public boolean retainAll(Collection<?> coll) { throw new UnsupportedOperationException(); }
    public boolean retainAll(TIntCollection coll) { throw new UnsupportedOperationException(); }
    public boolean retainAll(int[] array) { throw new UnsupportedOperationException(); }
    
    public void clear() { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableLongCollection
    implements TLongCollection, Serializable
  {
    private static final long serialVersionUID = 1820017752578914078L;
    
    final TLongCollection c;
    
    public TUnmodifiableLongCollection(TLongCollection c)
    {
      if (c == null)
        throw new NullPointerException();
      this.c = c;
    }
    
    public int size() { return c.size(); }
    public boolean isEmpty() { return c.isEmpty(); }
    public boolean contains(long o) { return c.contains(o); }
    public long[] toArray() { return c.toArray(); }
    public long[] toArray(long[] a) { return c.toArray(a); }
    public String toString() { return c.toString(); }
    public long getNoEntryValue() { return c.getNoEntryValue(); }
    public boolean forEach(TLongProcedure procedure) { return c.forEach(procedure); }
    
    public gnu.trove.iterator.TLongIterator iterator() {
      new gnu.trove.iterator.TLongIterator() {
        gnu.trove.iterator.TLongIterator i = c.iterator();
        
        public boolean hasNext() { return i.hasNext(); }
        public long next() { return i.next(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public boolean add(long e) { throw new UnsupportedOperationException(); }
    public boolean remove(long o) { throw new UnsupportedOperationException(); }
    
    public boolean containsAll(Collection<?> coll) { return c.containsAll(coll); }
    public boolean containsAll(TLongCollection coll) { return c.containsAll(coll); }
    public boolean containsAll(long[] array) { return c.containsAll(array); }
    
    public boolean addAll(TLongCollection coll) { throw new UnsupportedOperationException(); }
    public boolean addAll(Collection<? extends Long> coll) { throw new UnsupportedOperationException(); }
    public boolean addAll(long[] array) { throw new UnsupportedOperationException(); }
    
    public boolean removeAll(Collection<?> coll) { throw new UnsupportedOperationException(); }
    public boolean removeAll(TLongCollection coll) { throw new UnsupportedOperationException(); }
    public boolean removeAll(long[] array) { throw new UnsupportedOperationException(); }
    
    public boolean retainAll(Collection<?> coll) { throw new UnsupportedOperationException(); }
    public boolean retainAll(TLongCollection coll) { throw new UnsupportedOperationException(); }
    public boolean retainAll(long[] array) { throw new UnsupportedOperationException(); }
    
    public void clear() { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableByteCollection
    implements TByteCollection, Serializable
  {
    private static final long serialVersionUID = 1820017752578914078L;
    
    final TByteCollection c;
    
    public TUnmodifiableByteCollection(TByteCollection c)
    {
      if (c == null)
        throw new NullPointerException();
      this.c = c;
    }
    
    public int size() { return c.size(); }
    public boolean isEmpty() { return c.isEmpty(); }
    public boolean contains(byte o) { return c.contains(o); }
    public byte[] toArray() { return c.toArray(); }
    public byte[] toArray(byte[] a) { return c.toArray(a); }
    public String toString() { return c.toString(); }
    public byte getNoEntryValue() { return c.getNoEntryValue(); }
    public boolean forEach(TByteProcedure procedure) { return c.forEach(procedure); }
    
    public gnu.trove.iterator.TByteIterator iterator() {
      new gnu.trove.iterator.TByteIterator() {
        gnu.trove.iterator.TByteIterator i = c.iterator();
        
        public boolean hasNext() { return i.hasNext(); }
        public byte next() { return i.next(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public boolean add(byte e) { throw new UnsupportedOperationException(); }
    public boolean remove(byte o) { throw new UnsupportedOperationException(); }
    
    public boolean containsAll(Collection<?> coll) { return c.containsAll(coll); }
    public boolean containsAll(TByteCollection coll) { return c.containsAll(coll); }
    public boolean containsAll(byte[] array) { return c.containsAll(array); }
    
    public boolean addAll(TByteCollection coll) { throw new UnsupportedOperationException(); }
    public boolean addAll(Collection<? extends Byte> coll) { throw new UnsupportedOperationException(); }
    public boolean addAll(byte[] array) { throw new UnsupportedOperationException(); }
    
    public boolean removeAll(Collection<?> coll) { throw new UnsupportedOperationException(); }
    public boolean removeAll(TByteCollection coll) { throw new UnsupportedOperationException(); }
    public boolean removeAll(byte[] array) { throw new UnsupportedOperationException(); }
    
    public boolean retainAll(Collection<?> coll) { throw new UnsupportedOperationException(); }
    public boolean retainAll(TByteCollection coll) { throw new UnsupportedOperationException(); }
    public boolean retainAll(byte[] array) { throw new UnsupportedOperationException(); }
    
    public void clear() { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableShortCollection
    implements TShortCollection, Serializable
  {
    private static final long serialVersionUID = 1820017752578914078L;
    
    final TShortCollection c;
    
    public TUnmodifiableShortCollection(TShortCollection c)
    {
      if (c == null)
        throw new NullPointerException();
      this.c = c;
    }
    
    public int size() { return c.size(); }
    public boolean isEmpty() { return c.isEmpty(); }
    public boolean contains(short o) { return c.contains(o); }
    public short[] toArray() { return c.toArray(); }
    public short[] toArray(short[] a) { return c.toArray(a); }
    public String toString() { return c.toString(); }
    public short getNoEntryValue() { return c.getNoEntryValue(); }
    public boolean forEach(TShortProcedure procedure) { return c.forEach(procedure); }
    
    public gnu.trove.iterator.TShortIterator iterator() {
      new gnu.trove.iterator.TShortIterator() {
        gnu.trove.iterator.TShortIterator i = c.iterator();
        
        public boolean hasNext() { return i.hasNext(); }
        public short next() { return i.next(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public boolean add(short e) { throw new UnsupportedOperationException(); }
    public boolean remove(short o) { throw new UnsupportedOperationException(); }
    
    public boolean containsAll(Collection<?> coll) { return c.containsAll(coll); }
    public boolean containsAll(TShortCollection coll) { return c.containsAll(coll); }
    public boolean containsAll(short[] array) { return c.containsAll(array); }
    
    public boolean addAll(TShortCollection coll) { throw new UnsupportedOperationException(); }
    public boolean addAll(Collection<? extends Short> coll) { throw new UnsupportedOperationException(); }
    public boolean addAll(short[] array) { throw new UnsupportedOperationException(); }
    
    public boolean removeAll(Collection<?> coll) { throw new UnsupportedOperationException(); }
    public boolean removeAll(TShortCollection coll) { throw new UnsupportedOperationException(); }
    public boolean removeAll(short[] array) { throw new UnsupportedOperationException(); }
    
    public boolean retainAll(Collection<?> coll) { throw new UnsupportedOperationException(); }
    public boolean retainAll(TShortCollection coll) { throw new UnsupportedOperationException(); }
    public boolean retainAll(short[] array) { throw new UnsupportedOperationException(); }
    
    public void clear() { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableCharCollection
    implements TCharCollection, Serializable
  {
    private static final long serialVersionUID = 1820017752578914078L;
    
    final TCharCollection c;
    
    public TUnmodifiableCharCollection(TCharCollection c)
    {
      if (c == null)
        throw new NullPointerException();
      this.c = c;
    }
    
    public int size() { return c.size(); }
    public boolean isEmpty() { return c.isEmpty(); }
    public boolean contains(char o) { return c.contains(o); }
    public char[] toArray() { return c.toArray(); }
    public char[] toArray(char[] a) { return c.toArray(a); }
    public String toString() { return c.toString(); }
    public char getNoEntryValue() { return c.getNoEntryValue(); }
    public boolean forEach(TCharProcedure procedure) { return c.forEach(procedure); }
    
    public gnu.trove.iterator.TCharIterator iterator() {
      new gnu.trove.iterator.TCharIterator() {
        gnu.trove.iterator.TCharIterator i = c.iterator();
        
        public boolean hasNext() { return i.hasNext(); }
        public char next() { return i.next(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public boolean add(char e) { throw new UnsupportedOperationException(); }
    public boolean remove(char o) { throw new UnsupportedOperationException(); }
    
    public boolean containsAll(Collection<?> coll) { return c.containsAll(coll); }
    public boolean containsAll(TCharCollection coll) { return c.containsAll(coll); }
    public boolean containsAll(char[] array) { return c.containsAll(array); }
    
    public boolean addAll(TCharCollection coll) { throw new UnsupportedOperationException(); }
    public boolean addAll(Collection<? extends Character> coll) { throw new UnsupportedOperationException(); }
    public boolean addAll(char[] array) { throw new UnsupportedOperationException(); }
    
    public boolean removeAll(Collection<?> coll) { throw new UnsupportedOperationException(); }
    public boolean removeAll(TCharCollection coll) { throw new UnsupportedOperationException(); }
    public boolean removeAll(char[] array) { throw new UnsupportedOperationException(); }
    
    public boolean retainAll(Collection<?> coll) { throw new UnsupportedOperationException(); }
    public boolean retainAll(TCharCollection coll) { throw new UnsupportedOperationException(); }
    public boolean retainAll(char[] array) { throw new UnsupportedOperationException(); }
    
    public void clear() { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableDoubleSet
    extends TUnmodifiableCollections.TUnmodifiableDoubleCollection
    implements gnu.trove.set.TDoubleSet, Serializable
  {
    private static final long serialVersionUID = -9215047833775013803L;
    

    public TUnmodifiableDoubleSet(gnu.trove.set.TDoubleSet s) { super(); }
    public boolean equals(Object o) { return (o == this) || (c.equals(o)); }
    public int hashCode() { return c.hashCode(); }
  }
  

  public static class TUnmodifiableFloatSet
    extends TUnmodifiableCollections.TUnmodifiableFloatCollection
    implements gnu.trove.set.TFloatSet, Serializable
  {
    private static final long serialVersionUID = -9215047833775013803L;
    
    public TUnmodifiableFloatSet(gnu.trove.set.TFloatSet s) { super(); }
    public boolean equals(Object o) { return (o == this) || (c.equals(o)); }
    public int hashCode() { return c.hashCode(); }
  }
  

  public static class TUnmodifiableIntSet
    extends TUnmodifiableCollections.TUnmodifiableIntCollection
    implements gnu.trove.set.TIntSet, Serializable
  {
    private static final long serialVersionUID = -9215047833775013803L;
    
    public TUnmodifiableIntSet(gnu.trove.set.TIntSet s) { super(); }
    public boolean equals(Object o) { return (o == this) || (c.equals(o)); }
    public int hashCode() { return c.hashCode(); }
  }
  

  public static class TUnmodifiableLongSet
    extends TUnmodifiableCollections.TUnmodifiableLongCollection
    implements gnu.trove.set.TLongSet, Serializable
  {
    private static final long serialVersionUID = -9215047833775013803L;
    
    public TUnmodifiableLongSet(gnu.trove.set.TLongSet s) { super(); }
    public boolean equals(Object o) { return (o == this) || (c.equals(o)); }
    public int hashCode() { return c.hashCode(); }
  }
  

  public static class TUnmodifiableByteSet
    extends TUnmodifiableCollections.TUnmodifiableByteCollection
    implements gnu.trove.set.TByteSet, Serializable
  {
    private static final long serialVersionUID = -9215047833775013803L;
    
    public TUnmodifiableByteSet(gnu.trove.set.TByteSet s) { super(); }
    public boolean equals(Object o) { return (o == this) || (c.equals(o)); }
    public int hashCode() { return c.hashCode(); }
  }
  

  public static class TUnmodifiableShortSet
    extends TUnmodifiableCollections.TUnmodifiableShortCollection
    implements gnu.trove.set.TShortSet, Serializable
  {
    private static final long serialVersionUID = -9215047833775013803L;
    
    public TUnmodifiableShortSet(gnu.trove.set.TShortSet s) { super(); }
    public boolean equals(Object o) { return (o == this) || (c.equals(o)); }
    public int hashCode() { return c.hashCode(); }
  }
  

  public static class TUnmodifiableCharSet
    extends TUnmodifiableCollections.TUnmodifiableCharCollection
    implements gnu.trove.set.TCharSet, Serializable
  {
    private static final long serialVersionUID = -9215047833775013803L;
    
    public TUnmodifiableCharSet(gnu.trove.set.TCharSet s) { super(); }
    public boolean equals(Object o) { return (o == this) || (c.equals(o)); }
    public int hashCode() { return c.hashCode(); }
  }
  
































































  public static class TUnmodifiableDoubleList
    extends TUnmodifiableCollections.TUnmodifiableDoubleCollection
    implements TDoubleList
  {
    static final long serialVersionUID = -283967356065247728L;
    































































    final TDoubleList list;
    































































    public TUnmodifiableDoubleList(TDoubleList list)
    {
      super();
      this.list = list;
    }
    
    public boolean equals(Object o) { return (o == this) || (list.equals(o)); }
    public int hashCode() { return list.hashCode(); }
    
    public double get(int index) { return list.get(index); }
    public int indexOf(double o) { return list.indexOf(o); }
    public int lastIndexOf(double o) { return list.lastIndexOf(o); }
    
    public double[] toArray(int offset, int len) {
      return list.toArray(offset, len);
    }
    
    public double[] toArray(double[] dest, int offset, int len) { return list.toArray(dest, offset, len); }
    
    public double[] toArray(double[] dest, int source_pos, int dest_pos, int len) {
      return list.toArray(dest, source_pos, dest_pos, len);
    }
    
    public boolean forEachDescending(TDoubleProcedure procedure) {
      return list.forEachDescending(procedure);
    }
    
    public int binarySearch(double value) { return list.binarySearch(value); }
    
    public int binarySearch(double value, int fromIndex, int toIndex) { return list.binarySearch(value, fromIndex, toIndex); }
    

    public int indexOf(int offset, double value) { return list.indexOf(offset, value); }
    public int lastIndexOf(int offset, double value) { return list.lastIndexOf(offset, value); }
    public TDoubleList grep(TDoubleProcedure condition) { return list.grep(condition); }
    public TDoubleList inverseGrep(TDoubleProcedure condition) { return list.inverseGrep(condition); }
    
    public double max() { return list.max(); }
    public double min() { return list.min(); }
    
    public TDoubleList subList(int fromIndex, int toIndex) {
      return new TUnmodifiableDoubleList(list.subList(fromIndex, toIndex));
    }
    





































    private Object readResolve()
    {
      return (list instanceof java.util.RandomAccess) ? new TUnmodifiableCollections.TUnmodifiableRandomAccessDoubleList(list) : this;
    }
    


    public void add(double[] vals) { throw new UnsupportedOperationException(); }
    public void add(double[] vals, int offset, int length) { throw new UnsupportedOperationException(); }
    
    public double removeAt(int offset) { throw new UnsupportedOperationException(); }
    public void remove(int offset, int length) { throw new UnsupportedOperationException(); }
    
    public void insert(int offset, double value) { throw new UnsupportedOperationException(); }
    public void insert(int offset, double[] values) { throw new UnsupportedOperationException(); }
    public void insert(int offset, double[] values, int valOffset, int len) { throw new UnsupportedOperationException(); }
    
    public void set(int offset, double val) { throw new UnsupportedOperationException(); }
    public void set(int offset, double[] values) { throw new UnsupportedOperationException(); }
    public void set(int offset, double[] values, int valOffset, int length) { throw new UnsupportedOperationException(); }
    
    public double replace(int offset, double val) { throw new UnsupportedOperationException(); }
    
    public void transformValues(gnu.trove.function.TDoubleFunction function) { throw new UnsupportedOperationException(); }
    
    public void reverse() { throw new UnsupportedOperationException(); }
    public void reverse(int from, int to) { throw new UnsupportedOperationException(); }
    public void shuffle(java.util.Random rand) { throw new UnsupportedOperationException(); }
    
    public void sort() { throw new UnsupportedOperationException(); }
    public void sort(int fromIndex, int toIndex) { throw new UnsupportedOperationException(); }
    public void fill(double val) { throw new UnsupportedOperationException(); }
    public void fill(int fromIndex, int toIndex, double val) { throw new UnsupportedOperationException(); }
  }
  
  public static class TUnmodifiableRandomAccessDoubleList extends TUnmodifiableCollections.TUnmodifiableDoubleList implements java.util.RandomAccess
  {
    private static final long serialVersionUID = -2542308836966382001L;
    
    public TUnmodifiableRandomAccessDoubleList(TDoubleList list) {
      super();
    }
    
    public TDoubleList subList(int fromIndex, int toIndex) { return new TUnmodifiableRandomAccessDoubleList(list.subList(fromIndex, toIndex)); }
    








    private Object writeReplace()
    {
      return new TUnmodifiableCollections.TUnmodifiableDoubleList(list);
    }
  }
  
  public static class TUnmodifiableFloatList
    extends TUnmodifiableCollections.TUnmodifiableFloatCollection
    implements TFloatList
  {
    static final long serialVersionUID = -283967356065247728L;
    final TFloatList list;
    
    public TUnmodifiableFloatList(TFloatList list)
    {
      super();
      this.list = list;
    }
    
    public boolean equals(Object o) { return (o == this) || (list.equals(o)); }
    public int hashCode() { return list.hashCode(); }
    
    public float get(int index) { return list.get(index); }
    public int indexOf(float o) { return list.indexOf(o); }
    public int lastIndexOf(float o) { return list.lastIndexOf(o); }
    
    public float[] toArray(int offset, int len) {
      return list.toArray(offset, len);
    }
    
    public float[] toArray(float[] dest, int offset, int len) { return list.toArray(dest, offset, len); }
    
    public float[] toArray(float[] dest, int source_pos, int dest_pos, int len) {
      return list.toArray(dest, source_pos, dest_pos, len);
    }
    
    public boolean forEachDescending(TFloatProcedure procedure) {
      return list.forEachDescending(procedure);
    }
    
    public int binarySearch(float value) { return list.binarySearch(value); }
    
    public int binarySearch(float value, int fromIndex, int toIndex) { return list.binarySearch(value, fromIndex, toIndex); }
    

    public int indexOf(int offset, float value) { return list.indexOf(offset, value); }
    public int lastIndexOf(int offset, float value) { return list.lastIndexOf(offset, value); }
    public TFloatList grep(TFloatProcedure condition) { return list.grep(condition); }
    public TFloatList inverseGrep(TFloatProcedure condition) { return list.inverseGrep(condition); }
    
    public float max() { return list.max(); }
    public float min() { return list.min(); }
    
    public TFloatList subList(int fromIndex, int toIndex) {
      return new TUnmodifiableFloatList(list.subList(fromIndex, toIndex));
    }
    





































    private Object readResolve()
    {
      return (list instanceof java.util.RandomAccess) ? new TUnmodifiableCollections.TUnmodifiableRandomAccessFloatList(list) : this;
    }
    


    public void add(float[] vals) { throw new UnsupportedOperationException(); }
    public void add(float[] vals, int offset, int length) { throw new UnsupportedOperationException(); }
    
    public float removeAt(int offset) { throw new UnsupportedOperationException(); }
    public void remove(int offset, int length) { throw new UnsupportedOperationException(); }
    
    public void insert(int offset, float value) { throw new UnsupportedOperationException(); }
    public void insert(int offset, float[] values) { throw new UnsupportedOperationException(); }
    public void insert(int offset, float[] values, int valOffset, int len) { throw new UnsupportedOperationException(); }
    
    public void set(int offset, float val) { throw new UnsupportedOperationException(); }
    public void set(int offset, float[] values) { throw new UnsupportedOperationException(); }
    public void set(int offset, float[] values, int valOffset, int length) { throw new UnsupportedOperationException(); }
    
    public float replace(int offset, float val) { throw new UnsupportedOperationException(); }
    
    public void transformValues(gnu.trove.function.TFloatFunction function) { throw new UnsupportedOperationException(); }
    
    public void reverse() { throw new UnsupportedOperationException(); }
    public void reverse(int from, int to) { throw new UnsupportedOperationException(); }
    public void shuffle(java.util.Random rand) { throw new UnsupportedOperationException(); }
    
    public void sort() { throw new UnsupportedOperationException(); }
    public void sort(int fromIndex, int toIndex) { throw new UnsupportedOperationException(); }
    public void fill(float val) { throw new UnsupportedOperationException(); }
    public void fill(int fromIndex, int toIndex, float val) { throw new UnsupportedOperationException(); }
  }
  
  public static class TUnmodifiableRandomAccessFloatList extends TUnmodifiableCollections.TUnmodifiableFloatList implements java.util.RandomAccess
  {
    private static final long serialVersionUID = -2542308836966382001L;
    
    public TUnmodifiableRandomAccessFloatList(TFloatList list) {
      super();
    }
    
    public TFloatList subList(int fromIndex, int toIndex) { return new TUnmodifiableRandomAccessFloatList(list.subList(fromIndex, toIndex)); }
    








    private Object writeReplace()
    {
      return new TUnmodifiableCollections.TUnmodifiableFloatList(list);
    }
  }
  
  public static class TUnmodifiableIntList
    extends TUnmodifiableCollections.TUnmodifiableIntCollection
    implements TIntList
  {
    static final long serialVersionUID = -283967356065247728L;
    final TIntList list;
    
    public TUnmodifiableIntList(TIntList list)
    {
      super();
      this.list = list;
    }
    
    public boolean equals(Object o) { return (o == this) || (list.equals(o)); }
    public int hashCode() { return list.hashCode(); }
    
    public int get(int index) { return list.get(index); }
    public int indexOf(int o) { return list.indexOf(o); }
    public int lastIndexOf(int o) { return list.lastIndexOf(o); }
    
    public int[] toArray(int offset, int len) {
      return list.toArray(offset, len);
    }
    
    public int[] toArray(int[] dest, int offset, int len) { return list.toArray(dest, offset, len); }
    
    public int[] toArray(int[] dest, int source_pos, int dest_pos, int len) {
      return list.toArray(dest, source_pos, dest_pos, len);
    }
    
    public boolean forEachDescending(TIntProcedure procedure) {
      return list.forEachDescending(procedure);
    }
    
    public int binarySearch(int value) { return list.binarySearch(value); }
    
    public int binarySearch(int value, int fromIndex, int toIndex) { return list.binarySearch(value, fromIndex, toIndex); }
    

    public int indexOf(int offset, int value) { return list.indexOf(offset, value); }
    public int lastIndexOf(int offset, int value) { return list.lastIndexOf(offset, value); }
    public TIntList grep(TIntProcedure condition) { return list.grep(condition); }
    public TIntList inverseGrep(TIntProcedure condition) { return list.inverseGrep(condition); }
    
    public int max() { return list.max(); }
    public int min() { return list.min(); }
    
    public TIntList subList(int fromIndex, int toIndex) {
      return new TUnmodifiableIntList(list.subList(fromIndex, toIndex));
    }
    





































    private Object readResolve()
    {
      return (list instanceof java.util.RandomAccess) ? new TUnmodifiableCollections.TUnmodifiableRandomAccessIntList(list) : this;
    }
    


    public void add(int[] vals) { throw new UnsupportedOperationException(); }
    public void add(int[] vals, int offset, int length) { throw new UnsupportedOperationException(); }
    
    public int removeAt(int offset) { throw new UnsupportedOperationException(); }
    public void remove(int offset, int length) { throw new UnsupportedOperationException(); }
    
    public void insert(int offset, int value) { throw new UnsupportedOperationException(); }
    public void insert(int offset, int[] values) { throw new UnsupportedOperationException(); }
    public void insert(int offset, int[] values, int valOffset, int len) { throw new UnsupportedOperationException(); }
    
    public void set(int offset, int val) { throw new UnsupportedOperationException(); }
    public void set(int offset, int[] values) { throw new UnsupportedOperationException(); }
    public void set(int offset, int[] values, int valOffset, int length) { throw new UnsupportedOperationException(); }
    
    public int replace(int offset, int val) { throw new UnsupportedOperationException(); }
    
    public void transformValues(gnu.trove.function.TIntFunction function) { throw new UnsupportedOperationException(); }
    
    public void reverse() { throw new UnsupportedOperationException(); }
    public void reverse(int from, int to) { throw new UnsupportedOperationException(); }
    public void shuffle(java.util.Random rand) { throw new UnsupportedOperationException(); }
    
    public void sort() { throw new UnsupportedOperationException(); }
    public void sort(int fromIndex, int toIndex) { throw new UnsupportedOperationException(); }
    public void fill(int val) { throw new UnsupportedOperationException(); }
    public void fill(int fromIndex, int toIndex, int val) { throw new UnsupportedOperationException(); }
  }
  
  public static class TUnmodifiableRandomAccessIntList extends TUnmodifiableCollections.TUnmodifiableIntList implements java.util.RandomAccess
  {
    private static final long serialVersionUID = -2542308836966382001L;
    
    public TUnmodifiableRandomAccessIntList(TIntList list) {
      super();
    }
    
    public TIntList subList(int fromIndex, int toIndex) { return new TUnmodifiableRandomAccessIntList(list.subList(fromIndex, toIndex)); }
    








    private Object writeReplace()
    {
      return new TUnmodifiableCollections.TUnmodifiableIntList(list);
    }
  }
  
  public static class TUnmodifiableLongList
    extends TUnmodifiableCollections.TUnmodifiableLongCollection
    implements TLongList
  {
    static final long serialVersionUID = -283967356065247728L;
    final TLongList list;
    
    public TUnmodifiableLongList(TLongList list)
    {
      super();
      this.list = list;
    }
    
    public boolean equals(Object o) { return (o == this) || (list.equals(o)); }
    public int hashCode() { return list.hashCode(); }
    
    public long get(int index) { return list.get(index); }
    public int indexOf(long o) { return list.indexOf(o); }
    public int lastIndexOf(long o) { return list.lastIndexOf(o); }
    
    public long[] toArray(int offset, int len) {
      return list.toArray(offset, len);
    }
    
    public long[] toArray(long[] dest, int offset, int len) { return list.toArray(dest, offset, len); }
    
    public long[] toArray(long[] dest, int source_pos, int dest_pos, int len) {
      return list.toArray(dest, source_pos, dest_pos, len);
    }
    
    public boolean forEachDescending(TLongProcedure procedure) {
      return list.forEachDescending(procedure);
    }
    
    public int binarySearch(long value) { return list.binarySearch(value); }
    
    public int binarySearch(long value, int fromIndex, int toIndex) { return list.binarySearch(value, fromIndex, toIndex); }
    

    public int indexOf(int offset, long value) { return list.indexOf(offset, value); }
    public int lastIndexOf(int offset, long value) { return list.lastIndexOf(offset, value); }
    public TLongList grep(TLongProcedure condition) { return list.grep(condition); }
    public TLongList inverseGrep(TLongProcedure condition) { return list.inverseGrep(condition); }
    
    public long max() { return list.max(); }
    public long min() { return list.min(); }
    
    public TLongList subList(int fromIndex, int toIndex) {
      return new TUnmodifiableLongList(list.subList(fromIndex, toIndex));
    }
    





































    private Object readResolve()
    {
      return (list instanceof java.util.RandomAccess) ? new TUnmodifiableCollections.TUnmodifiableRandomAccessLongList(list) : this;
    }
    


    public void add(long[] vals) { throw new UnsupportedOperationException(); }
    public void add(long[] vals, int offset, int length) { throw new UnsupportedOperationException(); }
    
    public long removeAt(int offset) { throw new UnsupportedOperationException(); }
    public void remove(int offset, int length) { throw new UnsupportedOperationException(); }
    
    public void insert(int offset, long value) { throw new UnsupportedOperationException(); }
    public void insert(int offset, long[] values) { throw new UnsupportedOperationException(); }
    public void insert(int offset, long[] values, int valOffset, int len) { throw new UnsupportedOperationException(); }
    
    public void set(int offset, long val) { throw new UnsupportedOperationException(); }
    public void set(int offset, long[] values) { throw new UnsupportedOperationException(); }
    public void set(int offset, long[] values, int valOffset, int length) { throw new UnsupportedOperationException(); }
    
    public long replace(int offset, long val) { throw new UnsupportedOperationException(); }
    
    public void transformValues(gnu.trove.function.TLongFunction function) { throw new UnsupportedOperationException(); }
    
    public void reverse() { throw new UnsupportedOperationException(); }
    public void reverse(int from, int to) { throw new UnsupportedOperationException(); }
    public void shuffle(java.util.Random rand) { throw new UnsupportedOperationException(); }
    
    public void sort() { throw new UnsupportedOperationException(); }
    public void sort(int fromIndex, int toIndex) { throw new UnsupportedOperationException(); }
    public void fill(long val) { throw new UnsupportedOperationException(); }
    public void fill(int fromIndex, int toIndex, long val) { throw new UnsupportedOperationException(); }
  }
  
  public static class TUnmodifiableRandomAccessLongList extends TUnmodifiableCollections.TUnmodifiableLongList implements java.util.RandomAccess
  {
    private static final long serialVersionUID = -2542308836966382001L;
    
    public TUnmodifiableRandomAccessLongList(TLongList list) {
      super();
    }
    
    public TLongList subList(int fromIndex, int toIndex) { return new TUnmodifiableRandomAccessLongList(list.subList(fromIndex, toIndex)); }
    








    private Object writeReplace()
    {
      return new TUnmodifiableCollections.TUnmodifiableLongList(list);
    }
  }
  
  public static class TUnmodifiableByteList
    extends TUnmodifiableCollections.TUnmodifiableByteCollection
    implements TByteList
  {
    static final long serialVersionUID = -283967356065247728L;
    final TByteList list;
    
    public TUnmodifiableByteList(TByteList list)
    {
      super();
      this.list = list;
    }
    
    public boolean equals(Object o) { return (o == this) || (list.equals(o)); }
    public int hashCode() { return list.hashCode(); }
    
    public byte get(int index) { return list.get(index); }
    public int indexOf(byte o) { return list.indexOf(o); }
    public int lastIndexOf(byte o) { return list.lastIndexOf(o); }
    
    public byte[] toArray(int offset, int len) {
      return list.toArray(offset, len);
    }
    
    public byte[] toArray(byte[] dest, int offset, int len) { return list.toArray(dest, offset, len); }
    
    public byte[] toArray(byte[] dest, int source_pos, int dest_pos, int len) {
      return list.toArray(dest, source_pos, dest_pos, len);
    }
    
    public boolean forEachDescending(TByteProcedure procedure) {
      return list.forEachDescending(procedure);
    }
    
    public int binarySearch(byte value) { return list.binarySearch(value); }
    
    public int binarySearch(byte value, int fromIndex, int toIndex) { return list.binarySearch(value, fromIndex, toIndex); }
    

    public int indexOf(int offset, byte value) { return list.indexOf(offset, value); }
    public int lastIndexOf(int offset, byte value) { return list.lastIndexOf(offset, value); }
    public TByteList grep(TByteProcedure condition) { return list.grep(condition); }
    public TByteList inverseGrep(TByteProcedure condition) { return list.inverseGrep(condition); }
    
    public byte max() { return list.max(); }
    public byte min() { return list.min(); }
    
    public TByteList subList(int fromIndex, int toIndex) {
      return new TUnmodifiableByteList(list.subList(fromIndex, toIndex));
    }
    





































    private Object readResolve()
    {
      return (list instanceof java.util.RandomAccess) ? new TUnmodifiableCollections.TUnmodifiableRandomAccessByteList(list) : this;
    }
    


    public void add(byte[] vals) { throw new UnsupportedOperationException(); }
    public void add(byte[] vals, int offset, int length) { throw new UnsupportedOperationException(); }
    
    public byte removeAt(int offset) { throw new UnsupportedOperationException(); }
    public void remove(int offset, int length) { throw new UnsupportedOperationException(); }
    
    public void insert(int offset, byte value) { throw new UnsupportedOperationException(); }
    public void insert(int offset, byte[] values) { throw new UnsupportedOperationException(); }
    public void insert(int offset, byte[] values, int valOffset, int len) { throw new UnsupportedOperationException(); }
    
    public void set(int offset, byte val) { throw new UnsupportedOperationException(); }
    public void set(int offset, byte[] values) { throw new UnsupportedOperationException(); }
    public void set(int offset, byte[] values, int valOffset, int length) { throw new UnsupportedOperationException(); }
    
    public byte replace(int offset, byte val) { throw new UnsupportedOperationException(); }
    
    public void transformValues(gnu.trove.function.TByteFunction function) { throw new UnsupportedOperationException(); }
    
    public void reverse() { throw new UnsupportedOperationException(); }
    public void reverse(int from, int to) { throw new UnsupportedOperationException(); }
    public void shuffle(java.util.Random rand) { throw new UnsupportedOperationException(); }
    
    public void sort() { throw new UnsupportedOperationException(); }
    public void sort(int fromIndex, int toIndex) { throw new UnsupportedOperationException(); }
    public void fill(byte val) { throw new UnsupportedOperationException(); }
    public void fill(int fromIndex, int toIndex, byte val) { throw new UnsupportedOperationException(); }
  }
  
  public static class TUnmodifiableRandomAccessByteList extends TUnmodifiableCollections.TUnmodifiableByteList implements java.util.RandomAccess
  {
    private static final long serialVersionUID = -2542308836966382001L;
    
    public TUnmodifiableRandomAccessByteList(TByteList list) {
      super();
    }
    
    public TByteList subList(int fromIndex, int toIndex) { return new TUnmodifiableRandomAccessByteList(list.subList(fromIndex, toIndex)); }
    








    private Object writeReplace()
    {
      return new TUnmodifiableCollections.TUnmodifiableByteList(list);
    }
  }
  
  public static class TUnmodifiableShortList
    extends TUnmodifiableCollections.TUnmodifiableShortCollection
    implements TShortList
  {
    static final long serialVersionUID = -283967356065247728L;
    final TShortList list;
    
    public TUnmodifiableShortList(TShortList list)
    {
      super();
      this.list = list;
    }
    
    public boolean equals(Object o) { return (o == this) || (list.equals(o)); }
    public int hashCode() { return list.hashCode(); }
    
    public short get(int index) { return list.get(index); }
    public int indexOf(short o) { return list.indexOf(o); }
    public int lastIndexOf(short o) { return list.lastIndexOf(o); }
    
    public short[] toArray(int offset, int len) {
      return list.toArray(offset, len);
    }
    
    public short[] toArray(short[] dest, int offset, int len) { return list.toArray(dest, offset, len); }
    
    public short[] toArray(short[] dest, int source_pos, int dest_pos, int len) {
      return list.toArray(dest, source_pos, dest_pos, len);
    }
    
    public boolean forEachDescending(TShortProcedure procedure) {
      return list.forEachDescending(procedure);
    }
    
    public int binarySearch(short value) { return list.binarySearch(value); }
    
    public int binarySearch(short value, int fromIndex, int toIndex) { return list.binarySearch(value, fromIndex, toIndex); }
    

    public int indexOf(int offset, short value) { return list.indexOf(offset, value); }
    public int lastIndexOf(int offset, short value) { return list.lastIndexOf(offset, value); }
    public TShortList grep(TShortProcedure condition) { return list.grep(condition); }
    public TShortList inverseGrep(TShortProcedure condition) { return list.inverseGrep(condition); }
    
    public short max() { return list.max(); }
    public short min() { return list.min(); }
    
    public TShortList subList(int fromIndex, int toIndex) {
      return new TUnmodifiableShortList(list.subList(fromIndex, toIndex));
    }
    





































    private Object readResolve()
    {
      return (list instanceof java.util.RandomAccess) ? new TUnmodifiableCollections.TUnmodifiableRandomAccessShortList(list) : this;
    }
    


    public void add(short[] vals) { throw new UnsupportedOperationException(); }
    public void add(short[] vals, int offset, int length) { throw new UnsupportedOperationException(); }
    
    public short removeAt(int offset) { throw new UnsupportedOperationException(); }
    public void remove(int offset, int length) { throw new UnsupportedOperationException(); }
    
    public void insert(int offset, short value) { throw new UnsupportedOperationException(); }
    public void insert(int offset, short[] values) { throw new UnsupportedOperationException(); }
    public void insert(int offset, short[] values, int valOffset, int len) { throw new UnsupportedOperationException(); }
    
    public void set(int offset, short val) { throw new UnsupportedOperationException(); }
    public void set(int offset, short[] values) { throw new UnsupportedOperationException(); }
    public void set(int offset, short[] values, int valOffset, int length) { throw new UnsupportedOperationException(); }
    
    public short replace(int offset, short val) { throw new UnsupportedOperationException(); }
    
    public void transformValues(gnu.trove.function.TShortFunction function) { throw new UnsupportedOperationException(); }
    
    public void reverse() { throw new UnsupportedOperationException(); }
    public void reverse(int from, int to) { throw new UnsupportedOperationException(); }
    public void shuffle(java.util.Random rand) { throw new UnsupportedOperationException(); }
    
    public void sort() { throw new UnsupportedOperationException(); }
    public void sort(int fromIndex, int toIndex) { throw new UnsupportedOperationException(); }
    public void fill(short val) { throw new UnsupportedOperationException(); }
    public void fill(int fromIndex, int toIndex, short val) { throw new UnsupportedOperationException(); }
  }
  
  public static class TUnmodifiableRandomAccessShortList extends TUnmodifiableCollections.TUnmodifiableShortList implements java.util.RandomAccess
  {
    private static final long serialVersionUID = -2542308836966382001L;
    
    public TUnmodifiableRandomAccessShortList(TShortList list) {
      super();
    }
    
    public TShortList subList(int fromIndex, int toIndex) { return new TUnmodifiableRandomAccessShortList(list.subList(fromIndex, toIndex)); }
    








    private Object writeReplace()
    {
      return new TUnmodifiableCollections.TUnmodifiableShortList(list);
    }
  }
  
  public static class TUnmodifiableCharList
    extends TUnmodifiableCollections.TUnmodifiableCharCollection
    implements TCharList
  {
    static final long serialVersionUID = -283967356065247728L;
    final TCharList list;
    
    public TUnmodifiableCharList(TCharList list)
    {
      super();
      this.list = list;
    }
    
    public boolean equals(Object o) { return (o == this) || (list.equals(o)); }
    public int hashCode() { return list.hashCode(); }
    
    public char get(int index) { return list.get(index); }
    public int indexOf(char o) { return list.indexOf(o); }
    public int lastIndexOf(char o) { return list.lastIndexOf(o); }
    
    public char[] toArray(int offset, int len) {
      return list.toArray(offset, len);
    }
    
    public char[] toArray(char[] dest, int offset, int len) { return list.toArray(dest, offset, len); }
    
    public char[] toArray(char[] dest, int source_pos, int dest_pos, int len) {
      return list.toArray(dest, source_pos, dest_pos, len);
    }
    
    public boolean forEachDescending(TCharProcedure procedure) {
      return list.forEachDescending(procedure);
    }
    
    public int binarySearch(char value) { return list.binarySearch(value); }
    
    public int binarySearch(char value, int fromIndex, int toIndex) { return list.binarySearch(value, fromIndex, toIndex); }
    

    public int indexOf(int offset, char value) { return list.indexOf(offset, value); }
    public int lastIndexOf(int offset, char value) { return list.lastIndexOf(offset, value); }
    public TCharList grep(TCharProcedure condition) { return list.grep(condition); }
    public TCharList inverseGrep(TCharProcedure condition) { return list.inverseGrep(condition); }
    
    public char max() { return list.max(); }
    public char min() { return list.min(); }
    
    public TCharList subList(int fromIndex, int toIndex) {
      return new TUnmodifiableCharList(list.subList(fromIndex, toIndex));
    }
    





































    private Object readResolve()
    {
      return (list instanceof java.util.RandomAccess) ? new TUnmodifiableCollections.TUnmodifiableRandomAccessCharList(list) : this;
    }
    


    public void add(char[] vals) { throw new UnsupportedOperationException(); }
    public void add(char[] vals, int offset, int length) { throw new UnsupportedOperationException(); }
    
    public char removeAt(int offset) { throw new UnsupportedOperationException(); }
    public void remove(int offset, int length) { throw new UnsupportedOperationException(); }
    
    public void insert(int offset, char value) { throw new UnsupportedOperationException(); }
    public void insert(int offset, char[] values) { throw new UnsupportedOperationException(); }
    public void insert(int offset, char[] values, int valOffset, int len) { throw new UnsupportedOperationException(); }
    
    public void set(int offset, char val) { throw new UnsupportedOperationException(); }
    public void set(int offset, char[] values) { throw new UnsupportedOperationException(); }
    public void set(int offset, char[] values, int valOffset, int length) { throw new UnsupportedOperationException(); }
    
    public char replace(int offset, char val) { throw new UnsupportedOperationException(); }
    
    public void transformValues(gnu.trove.function.TCharFunction function) { throw new UnsupportedOperationException(); }
    
    public void reverse() { throw new UnsupportedOperationException(); }
    public void reverse(int from, int to) { throw new UnsupportedOperationException(); }
    public void shuffle(java.util.Random rand) { throw new UnsupportedOperationException(); }
    
    public void sort() { throw new UnsupportedOperationException(); }
    public void sort(int fromIndex, int toIndex) { throw new UnsupportedOperationException(); }
    public void fill(char val) { throw new UnsupportedOperationException(); }
    public void fill(int fromIndex, int toIndex, char val) { throw new UnsupportedOperationException(); }
  }
  
  public static class TUnmodifiableRandomAccessCharList extends TUnmodifiableCollections.TUnmodifiableCharList implements java.util.RandomAccess
  {
    private static final long serialVersionUID = -2542308836966382001L;
    
    public TUnmodifiableRandomAccessCharList(TCharList list) {
      super();
    }
    
    public TCharList subList(int fromIndex, int toIndex) { return new TUnmodifiableRandomAccessCharList(list.subList(fromIndex, toIndex)); }
    








    private Object writeReplace()
    {
      return new TUnmodifiableCollections.TUnmodifiableCharList(list);
    }
  }
  

  public static class TUnmodifiableDoubleDoubleMap
    implements TDoubleDoubleMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final TDoubleDoubleMap m;
    

    public TUnmodifiableDoubleDoubleMap(TDoubleDoubleMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(double key) { return m.containsKey(key); }
    public boolean containsValue(double val) { return m.containsValue(val); }
    public double get(double key) { return m.get(key); }
    
    public double put(double key, double value) { throw new UnsupportedOperationException(); }
    public double remove(double key) { throw new UnsupportedOperationException(); }
    public void putAll(TDoubleDoubleMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Double, ? extends Double> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TDoubleSet keySet = null;
    private transient TDoubleCollection values = null;
    
    public gnu.trove.set.TDoubleSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public double[] keys() { return m.keys(); }
    public double[] keys(double[] array) { return m.keys(array); }
    
    public TDoubleCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public double[] values() { return m.values(); }
    public double[] values(double[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public double getNoEntryKey() { return m.getNoEntryKey(); }
    public double getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TDoubleProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TDoubleProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TDoubleDoubleProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TDoubleDoubleIterator iterator() {
      new gnu.trove.iterator.TDoubleDoubleIterator() {
        gnu.trove.iterator.TDoubleDoubleIterator iter = m.iterator();
        
        public double key() { return iter.key(); }
        public double value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public double setValue(double val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public double putIfAbsent(double key, double value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TDoubleFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TDoubleDoubleProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(double key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(double key, double amount) { throw new UnsupportedOperationException(); }
    public double adjustOrPutValue(double key, double adjust_amount, double put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableDoubleFloatMap
    implements TDoubleFloatMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final TDoubleFloatMap m;
    
    public TUnmodifiableDoubleFloatMap(TDoubleFloatMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(double key) { return m.containsKey(key); }
    public boolean containsValue(float val) { return m.containsValue(val); }
    public float get(double key) { return m.get(key); }
    
    public float put(double key, float value) { throw new UnsupportedOperationException(); }
    public float remove(double key) { throw new UnsupportedOperationException(); }
    public void putAll(TDoubleFloatMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Double, ? extends Float> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TDoubleSet keySet = null;
    private transient TFloatCollection values = null;
    
    public gnu.trove.set.TDoubleSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public double[] keys() { return m.keys(); }
    public double[] keys(double[] array) { return m.keys(array); }
    
    public TFloatCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public float[] values() { return m.values(); }
    public float[] values(float[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public double getNoEntryKey() { return m.getNoEntryKey(); }
    public float getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TDoubleProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TFloatProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TDoubleFloatProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TDoubleFloatIterator iterator() {
      new gnu.trove.iterator.TDoubleFloatIterator() {
        gnu.trove.iterator.TDoubleFloatIterator iter = m.iterator();
        
        public double key() { return iter.key(); }
        public float value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public float setValue(float val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public float putIfAbsent(double key, float value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TFloatFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TDoubleFloatProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(double key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(double key, float amount) { throw new UnsupportedOperationException(); }
    public float adjustOrPutValue(double key, float adjust_amount, float put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableDoubleIntMap
    implements TDoubleIntMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final TDoubleIntMap m;
    
    public TUnmodifiableDoubleIntMap(TDoubleIntMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(double key) { return m.containsKey(key); }
    public boolean containsValue(int val) { return m.containsValue(val); }
    public int get(double key) { return m.get(key); }
    
    public int put(double key, int value) { throw new UnsupportedOperationException(); }
    public int remove(double key) { throw new UnsupportedOperationException(); }
    public void putAll(TDoubleIntMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Double, ? extends Integer> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TDoubleSet keySet = null;
    private transient TIntCollection values = null;
    
    public gnu.trove.set.TDoubleSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public double[] keys() { return m.keys(); }
    public double[] keys(double[] array) { return m.keys(array); }
    
    public TIntCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public int[] values() { return m.values(); }
    public int[] values(int[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public double getNoEntryKey() { return m.getNoEntryKey(); }
    public int getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TDoubleProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TIntProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TDoubleIntProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TDoubleIntIterator iterator() {
      new gnu.trove.iterator.TDoubleIntIterator() {
        gnu.trove.iterator.TDoubleIntIterator iter = m.iterator();
        
        public double key() { return iter.key(); }
        public int value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public int setValue(int val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public int putIfAbsent(double key, int value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TIntFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TDoubleIntProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(double key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(double key, int amount) { throw new UnsupportedOperationException(); }
    public int adjustOrPutValue(double key, int adjust_amount, int put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableDoubleLongMap
    implements gnu.trove.map.TDoubleLongMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final gnu.trove.map.TDoubleLongMap m;
    
    public TUnmodifiableDoubleLongMap(gnu.trove.map.TDoubleLongMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(double key) { return m.containsKey(key); }
    public boolean containsValue(long val) { return m.containsValue(val); }
    public long get(double key) { return m.get(key); }
    
    public long put(double key, long value) { throw new UnsupportedOperationException(); }
    public long remove(double key) { throw new UnsupportedOperationException(); }
    public void putAll(gnu.trove.map.TDoubleLongMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Double, ? extends Long> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TDoubleSet keySet = null;
    private transient TLongCollection values = null;
    
    public gnu.trove.set.TDoubleSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public double[] keys() { return m.keys(); }
    public double[] keys(double[] array) { return m.keys(array); }
    
    public TLongCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public long[] values() { return m.values(); }
    public long[] values(long[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public double getNoEntryKey() { return m.getNoEntryKey(); }
    public long getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TDoubleProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TLongProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TDoubleLongProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TDoubleLongIterator iterator() {
      new gnu.trove.iterator.TDoubleLongIterator() {
        gnu.trove.iterator.TDoubleLongIterator iter = m.iterator();
        
        public double key() { return iter.key(); }
        public long value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public long setValue(long val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public long putIfAbsent(double key, long value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TLongFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TDoubleLongProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(double key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(double key, long amount) { throw new UnsupportedOperationException(); }
    public long adjustOrPutValue(double key, long adjust_amount, long put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableDoubleByteMap
    implements TDoubleByteMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final TDoubleByteMap m;
    
    public TUnmodifiableDoubleByteMap(TDoubleByteMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(double key) { return m.containsKey(key); }
    public boolean containsValue(byte val) { return m.containsValue(val); }
    public byte get(double key) { return m.get(key); }
    
    public byte put(double key, byte value) { throw new UnsupportedOperationException(); }
    public byte remove(double key) { throw new UnsupportedOperationException(); }
    public void putAll(TDoubleByteMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Double, ? extends Byte> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TDoubleSet keySet = null;
    private transient TByteCollection values = null;
    
    public gnu.trove.set.TDoubleSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public double[] keys() { return m.keys(); }
    public double[] keys(double[] array) { return m.keys(array); }
    
    public TByteCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public byte[] values() { return m.values(); }
    public byte[] values(byte[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public double getNoEntryKey() { return m.getNoEntryKey(); }
    public byte getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TDoubleProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TByteProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TDoubleByteProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TDoubleByteIterator iterator() {
      new gnu.trove.iterator.TDoubleByteIterator() {
        gnu.trove.iterator.TDoubleByteIterator iter = m.iterator();
        
        public double key() { return iter.key(); }
        public byte value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public byte setValue(byte val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public byte putIfAbsent(double key, byte value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TByteFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TDoubleByteProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(double key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(double key, byte amount) { throw new UnsupportedOperationException(); }
    public byte adjustOrPutValue(double key, byte adjust_amount, byte put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableDoubleShortMap
    implements gnu.trove.map.TDoubleShortMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final gnu.trove.map.TDoubleShortMap m;
    
    public TUnmodifiableDoubleShortMap(gnu.trove.map.TDoubleShortMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(double key) { return m.containsKey(key); }
    public boolean containsValue(short val) { return m.containsValue(val); }
    public short get(double key) { return m.get(key); }
    
    public short put(double key, short value) { throw new UnsupportedOperationException(); }
    public short remove(double key) { throw new UnsupportedOperationException(); }
    public void putAll(gnu.trove.map.TDoubleShortMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Double, ? extends Short> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TDoubleSet keySet = null;
    private transient TShortCollection values = null;
    
    public gnu.trove.set.TDoubleSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public double[] keys() { return m.keys(); }
    public double[] keys(double[] array) { return m.keys(array); }
    
    public TShortCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public short[] values() { return m.values(); }
    public short[] values(short[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public double getNoEntryKey() { return m.getNoEntryKey(); }
    public short getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TDoubleProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TShortProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TDoubleShortProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TDoubleShortIterator iterator() {
      new gnu.trove.iterator.TDoubleShortIterator() {
        gnu.trove.iterator.TDoubleShortIterator iter = m.iterator();
        
        public double key() { return iter.key(); }
        public short value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public short setValue(short val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public short putIfAbsent(double key, short value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TShortFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TDoubleShortProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(double key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(double key, short amount) { throw new UnsupportedOperationException(); }
    public short adjustOrPutValue(double key, short adjust_amount, short put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableDoubleCharMap
    implements gnu.trove.map.TDoubleCharMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final gnu.trove.map.TDoubleCharMap m;
    
    public TUnmodifiableDoubleCharMap(gnu.trove.map.TDoubleCharMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(double key) { return m.containsKey(key); }
    public boolean containsValue(char val) { return m.containsValue(val); }
    public char get(double key) { return m.get(key); }
    
    public char put(double key, char value) { throw new UnsupportedOperationException(); }
    public char remove(double key) { throw new UnsupportedOperationException(); }
    public void putAll(gnu.trove.map.TDoubleCharMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Double, ? extends Character> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TDoubleSet keySet = null;
    private transient TCharCollection values = null;
    
    public gnu.trove.set.TDoubleSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public double[] keys() { return m.keys(); }
    public double[] keys(double[] array) { return m.keys(array); }
    
    public TCharCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public char[] values() { return m.values(); }
    public char[] values(char[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public double getNoEntryKey() { return m.getNoEntryKey(); }
    public char getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TDoubleProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TCharProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TDoubleCharProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TDoubleCharIterator iterator() {
      new gnu.trove.iterator.TDoubleCharIterator() {
        gnu.trove.iterator.TDoubleCharIterator iter = m.iterator();
        
        public double key() { return iter.key(); }
        public char value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public char setValue(char val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public char putIfAbsent(double key, char value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TCharFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TDoubleCharProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(double key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(double key, char amount) { throw new UnsupportedOperationException(); }
    public char adjustOrPutValue(double key, char adjust_amount, char put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableFloatDoubleMap
    implements TFloatDoubleMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final TFloatDoubleMap m;
    
    public TUnmodifiableFloatDoubleMap(TFloatDoubleMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(float key) { return m.containsKey(key); }
    public boolean containsValue(double val) { return m.containsValue(val); }
    public double get(float key) { return m.get(key); }
    
    public double put(float key, double value) { throw new UnsupportedOperationException(); }
    public double remove(float key) { throw new UnsupportedOperationException(); }
    public void putAll(TFloatDoubleMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Float, ? extends Double> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TFloatSet keySet = null;
    private transient TDoubleCollection values = null;
    
    public gnu.trove.set.TFloatSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public float[] keys() { return m.keys(); }
    public float[] keys(float[] array) { return m.keys(array); }
    
    public TDoubleCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public double[] values() { return m.values(); }
    public double[] values(double[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public float getNoEntryKey() { return m.getNoEntryKey(); }
    public double getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TFloatProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TDoubleProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TFloatDoubleProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TFloatDoubleIterator iterator() {
      new gnu.trove.iterator.TFloatDoubleIterator() {
        gnu.trove.iterator.TFloatDoubleIterator iter = m.iterator();
        
        public float key() { return iter.key(); }
        public double value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public double setValue(double val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public double putIfAbsent(float key, double value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TDoubleFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TFloatDoubleProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(float key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(float key, double amount) { throw new UnsupportedOperationException(); }
    public double adjustOrPutValue(float key, double adjust_amount, double put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableFloatFloatMap
    implements gnu.trove.map.TFloatFloatMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final gnu.trove.map.TFloatFloatMap m;
    
    public TUnmodifiableFloatFloatMap(gnu.trove.map.TFloatFloatMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(float key) { return m.containsKey(key); }
    public boolean containsValue(float val) { return m.containsValue(val); }
    public float get(float key) { return m.get(key); }
    
    public float put(float key, float value) { throw new UnsupportedOperationException(); }
    public float remove(float key) { throw new UnsupportedOperationException(); }
    public void putAll(gnu.trove.map.TFloatFloatMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Float, ? extends Float> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TFloatSet keySet = null;
    private transient TFloatCollection values = null;
    
    public gnu.trove.set.TFloatSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public float[] keys() { return m.keys(); }
    public float[] keys(float[] array) { return m.keys(array); }
    
    public TFloatCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public float[] values() { return m.values(); }
    public float[] values(float[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public float getNoEntryKey() { return m.getNoEntryKey(); }
    public float getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TFloatProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TFloatProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TFloatFloatProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TFloatFloatIterator iterator() {
      new gnu.trove.iterator.TFloatFloatIterator() {
        gnu.trove.iterator.TFloatFloatIterator iter = m.iterator();
        
        public float key() { return iter.key(); }
        public float value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public float setValue(float val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public float putIfAbsent(float key, float value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TFloatFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TFloatFloatProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(float key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(float key, float amount) { throw new UnsupportedOperationException(); }
    public float adjustOrPutValue(float key, float adjust_amount, float put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableFloatIntMap
    implements TFloatIntMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final TFloatIntMap m;
    
    public TUnmodifiableFloatIntMap(TFloatIntMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(float key) { return m.containsKey(key); }
    public boolean containsValue(int val) { return m.containsValue(val); }
    public int get(float key) { return m.get(key); }
    
    public int put(float key, int value) { throw new UnsupportedOperationException(); }
    public int remove(float key) { throw new UnsupportedOperationException(); }
    public void putAll(TFloatIntMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Float, ? extends Integer> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TFloatSet keySet = null;
    private transient TIntCollection values = null;
    
    public gnu.trove.set.TFloatSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public float[] keys() { return m.keys(); }
    public float[] keys(float[] array) { return m.keys(array); }
    
    public TIntCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public int[] values() { return m.values(); }
    public int[] values(int[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public float getNoEntryKey() { return m.getNoEntryKey(); }
    public int getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TFloatProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TIntProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TFloatIntProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TFloatIntIterator iterator() {
      new gnu.trove.iterator.TFloatIntIterator() {
        gnu.trove.iterator.TFloatIntIterator iter = m.iterator();
        
        public float key() { return iter.key(); }
        public int value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public int setValue(int val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public int putIfAbsent(float key, int value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TIntFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TFloatIntProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(float key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(float key, int amount) { throw new UnsupportedOperationException(); }
    public int adjustOrPutValue(float key, int adjust_amount, int put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableFloatLongMap
    implements TFloatLongMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final TFloatLongMap m;
    
    public TUnmodifiableFloatLongMap(TFloatLongMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(float key) { return m.containsKey(key); }
    public boolean containsValue(long val) { return m.containsValue(val); }
    public long get(float key) { return m.get(key); }
    
    public long put(float key, long value) { throw new UnsupportedOperationException(); }
    public long remove(float key) { throw new UnsupportedOperationException(); }
    public void putAll(TFloatLongMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Float, ? extends Long> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TFloatSet keySet = null;
    private transient TLongCollection values = null;
    
    public gnu.trove.set.TFloatSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public float[] keys() { return m.keys(); }
    public float[] keys(float[] array) { return m.keys(array); }
    
    public TLongCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public long[] values() { return m.values(); }
    public long[] values(long[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public float getNoEntryKey() { return m.getNoEntryKey(); }
    public long getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TFloatProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TLongProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TFloatLongProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TFloatLongIterator iterator() {
      new gnu.trove.iterator.TFloatLongIterator() {
        gnu.trove.iterator.TFloatLongIterator iter = m.iterator();
        
        public float key() { return iter.key(); }
        public long value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public long setValue(long val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public long putIfAbsent(float key, long value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TLongFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TFloatLongProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(float key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(float key, long amount) { throw new UnsupportedOperationException(); }
    public long adjustOrPutValue(float key, long adjust_amount, long put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableFloatByteMap
    implements gnu.trove.map.TFloatByteMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final gnu.trove.map.TFloatByteMap m;
    
    public TUnmodifiableFloatByteMap(gnu.trove.map.TFloatByteMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(float key) { return m.containsKey(key); }
    public boolean containsValue(byte val) { return m.containsValue(val); }
    public byte get(float key) { return m.get(key); }
    
    public byte put(float key, byte value) { throw new UnsupportedOperationException(); }
    public byte remove(float key) { throw new UnsupportedOperationException(); }
    public void putAll(gnu.trove.map.TFloatByteMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Float, ? extends Byte> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TFloatSet keySet = null;
    private transient TByteCollection values = null;
    
    public gnu.trove.set.TFloatSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public float[] keys() { return m.keys(); }
    public float[] keys(float[] array) { return m.keys(array); }
    
    public TByteCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public byte[] values() { return m.values(); }
    public byte[] values(byte[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public float getNoEntryKey() { return m.getNoEntryKey(); }
    public byte getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TFloatProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TByteProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TFloatByteProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TFloatByteIterator iterator() {
      new gnu.trove.iterator.TFloatByteIterator() {
        gnu.trove.iterator.TFloatByteIterator iter = m.iterator();
        
        public float key() { return iter.key(); }
        public byte value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public byte setValue(byte val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public byte putIfAbsent(float key, byte value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TByteFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TFloatByteProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(float key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(float key, byte amount) { throw new UnsupportedOperationException(); }
    public byte adjustOrPutValue(float key, byte adjust_amount, byte put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableFloatShortMap
    implements TFloatShortMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final TFloatShortMap m;
    
    public TUnmodifiableFloatShortMap(TFloatShortMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(float key) { return m.containsKey(key); }
    public boolean containsValue(short val) { return m.containsValue(val); }
    public short get(float key) { return m.get(key); }
    
    public short put(float key, short value) { throw new UnsupportedOperationException(); }
    public short remove(float key) { throw new UnsupportedOperationException(); }
    public void putAll(TFloatShortMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Float, ? extends Short> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TFloatSet keySet = null;
    private transient TShortCollection values = null;
    
    public gnu.trove.set.TFloatSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public float[] keys() { return m.keys(); }
    public float[] keys(float[] array) { return m.keys(array); }
    
    public TShortCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public short[] values() { return m.values(); }
    public short[] values(short[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public float getNoEntryKey() { return m.getNoEntryKey(); }
    public short getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TFloatProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TShortProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TFloatShortProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TFloatShortIterator iterator() {
      new gnu.trove.iterator.TFloatShortIterator() {
        gnu.trove.iterator.TFloatShortIterator iter = m.iterator();
        
        public float key() { return iter.key(); }
        public short value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public short setValue(short val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public short putIfAbsent(float key, short value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TShortFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TFloatShortProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(float key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(float key, short amount) { throw new UnsupportedOperationException(); }
    public short adjustOrPutValue(float key, short adjust_amount, short put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableFloatCharMap
    implements gnu.trove.map.TFloatCharMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final gnu.trove.map.TFloatCharMap m;
    
    public TUnmodifiableFloatCharMap(gnu.trove.map.TFloatCharMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(float key) { return m.containsKey(key); }
    public boolean containsValue(char val) { return m.containsValue(val); }
    public char get(float key) { return m.get(key); }
    
    public char put(float key, char value) { throw new UnsupportedOperationException(); }
    public char remove(float key) { throw new UnsupportedOperationException(); }
    public void putAll(gnu.trove.map.TFloatCharMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Float, ? extends Character> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TFloatSet keySet = null;
    private transient TCharCollection values = null;
    
    public gnu.trove.set.TFloatSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public float[] keys() { return m.keys(); }
    public float[] keys(float[] array) { return m.keys(array); }
    
    public TCharCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public char[] values() { return m.values(); }
    public char[] values(char[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public float getNoEntryKey() { return m.getNoEntryKey(); }
    public char getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TFloatProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TCharProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TFloatCharProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TFloatCharIterator iterator() {
      new gnu.trove.iterator.TFloatCharIterator() {
        gnu.trove.iterator.TFloatCharIterator iter = m.iterator();
        
        public float key() { return iter.key(); }
        public char value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public char setValue(char val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public char putIfAbsent(float key, char value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TCharFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TFloatCharProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(float key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(float key, char amount) { throw new UnsupportedOperationException(); }
    public char adjustOrPutValue(float key, char adjust_amount, char put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableIntDoubleMap
    implements TIntDoubleMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final TIntDoubleMap m;
    
    public TUnmodifiableIntDoubleMap(TIntDoubleMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(int key) { return m.containsKey(key); }
    public boolean containsValue(double val) { return m.containsValue(val); }
    public double get(int key) { return m.get(key); }
    
    public double put(int key, double value) { throw new UnsupportedOperationException(); }
    public double remove(int key) { throw new UnsupportedOperationException(); }
    public void putAll(TIntDoubleMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Integer, ? extends Double> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TIntSet keySet = null;
    private transient TDoubleCollection values = null;
    
    public gnu.trove.set.TIntSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public int[] keys() { return m.keys(); }
    public int[] keys(int[] array) { return m.keys(array); }
    
    public TDoubleCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public double[] values() { return m.values(); }
    public double[] values(double[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public int getNoEntryKey() { return m.getNoEntryKey(); }
    public double getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TIntProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TDoubleProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TIntDoubleProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TIntDoubleIterator iterator() {
      new gnu.trove.iterator.TIntDoubleIterator() {
        gnu.trove.iterator.TIntDoubleIterator iter = m.iterator();
        
        public int key() { return iter.key(); }
        public double value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public double setValue(double val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public double putIfAbsent(int key, double value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TDoubleFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TIntDoubleProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(int key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(int key, double amount) { throw new UnsupportedOperationException(); }
    public double adjustOrPutValue(int key, double adjust_amount, double put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableIntFloatMap
    implements TIntFloatMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final TIntFloatMap m;
    
    public TUnmodifiableIntFloatMap(TIntFloatMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(int key) { return m.containsKey(key); }
    public boolean containsValue(float val) { return m.containsValue(val); }
    public float get(int key) { return m.get(key); }
    
    public float put(int key, float value) { throw new UnsupportedOperationException(); }
    public float remove(int key) { throw new UnsupportedOperationException(); }
    public void putAll(TIntFloatMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Integer, ? extends Float> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TIntSet keySet = null;
    private transient TFloatCollection values = null;
    
    public gnu.trove.set.TIntSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public int[] keys() { return m.keys(); }
    public int[] keys(int[] array) { return m.keys(array); }
    
    public TFloatCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public float[] values() { return m.values(); }
    public float[] values(float[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public int getNoEntryKey() { return m.getNoEntryKey(); }
    public float getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TIntProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TFloatProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TIntFloatProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TIntFloatIterator iterator() {
      new gnu.trove.iterator.TIntFloatIterator() {
        gnu.trove.iterator.TIntFloatIterator iter = m.iterator();
        
        public int key() { return iter.key(); }
        public float value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public float setValue(float val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public float putIfAbsent(int key, float value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TFloatFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TIntFloatProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(int key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(int key, float amount) { throw new UnsupportedOperationException(); }
    public float adjustOrPutValue(int key, float adjust_amount, float put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableIntIntMap
    implements TIntIntMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final TIntIntMap m;
    
    public TUnmodifiableIntIntMap(TIntIntMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(int key) { return m.containsKey(key); }
    public boolean containsValue(int val) { return m.containsValue(val); }
    public int get(int key) { return m.get(key); }
    
    public int put(int key, int value) { throw new UnsupportedOperationException(); }
    public int remove(int key) { throw new UnsupportedOperationException(); }
    public void putAll(TIntIntMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Integer, ? extends Integer> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TIntSet keySet = null;
    private transient TIntCollection values = null;
    
    public gnu.trove.set.TIntSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public int[] keys() { return m.keys(); }
    public int[] keys(int[] array) { return m.keys(array); }
    
    public TIntCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public int[] values() { return m.values(); }
    public int[] values(int[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public int getNoEntryKey() { return m.getNoEntryKey(); }
    public int getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TIntProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TIntProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TIntIntProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TIntIntIterator iterator() {
      new gnu.trove.iterator.TIntIntIterator() {
        gnu.trove.iterator.TIntIntIterator iter = m.iterator();
        
        public int key() { return iter.key(); }
        public int value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public int setValue(int val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public int putIfAbsent(int key, int value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TIntFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TIntIntProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(int key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(int key, int amount) { throw new UnsupportedOperationException(); }
    public int adjustOrPutValue(int key, int adjust_amount, int put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableIntLongMap
    implements gnu.trove.map.TIntLongMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final gnu.trove.map.TIntLongMap m;
    
    public TUnmodifiableIntLongMap(gnu.trove.map.TIntLongMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(int key) { return m.containsKey(key); }
    public boolean containsValue(long val) { return m.containsValue(val); }
    public long get(int key) { return m.get(key); }
    
    public long put(int key, long value) { throw new UnsupportedOperationException(); }
    public long remove(int key) { throw new UnsupportedOperationException(); }
    public void putAll(gnu.trove.map.TIntLongMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Integer, ? extends Long> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TIntSet keySet = null;
    private transient TLongCollection values = null;
    
    public gnu.trove.set.TIntSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public int[] keys() { return m.keys(); }
    public int[] keys(int[] array) { return m.keys(array); }
    
    public TLongCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public long[] values() { return m.values(); }
    public long[] values(long[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public int getNoEntryKey() { return m.getNoEntryKey(); }
    public long getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TIntProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TLongProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TIntLongProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TIntLongIterator iterator() {
      new gnu.trove.iterator.TIntLongIterator() {
        gnu.trove.iterator.TIntLongIterator iter = m.iterator();
        
        public int key() { return iter.key(); }
        public long value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public long setValue(long val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public long putIfAbsent(int key, long value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TLongFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TIntLongProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(int key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(int key, long amount) { throw new UnsupportedOperationException(); }
    public long adjustOrPutValue(int key, long adjust_amount, long put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableIntByteMap
    implements TIntByteMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final TIntByteMap m;
    
    public TUnmodifiableIntByteMap(TIntByteMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(int key) { return m.containsKey(key); }
    public boolean containsValue(byte val) { return m.containsValue(val); }
    public byte get(int key) { return m.get(key); }
    
    public byte put(int key, byte value) { throw new UnsupportedOperationException(); }
    public byte remove(int key) { throw new UnsupportedOperationException(); }
    public void putAll(TIntByteMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Integer, ? extends Byte> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TIntSet keySet = null;
    private transient TByteCollection values = null;
    
    public gnu.trove.set.TIntSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public int[] keys() { return m.keys(); }
    public int[] keys(int[] array) { return m.keys(array); }
    
    public TByteCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public byte[] values() { return m.values(); }
    public byte[] values(byte[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public int getNoEntryKey() { return m.getNoEntryKey(); }
    public byte getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TIntProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TByteProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TIntByteProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TIntByteIterator iterator() {
      new gnu.trove.iterator.TIntByteIterator() {
        gnu.trove.iterator.TIntByteIterator iter = m.iterator();
        
        public int key() { return iter.key(); }
        public byte value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public byte setValue(byte val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public byte putIfAbsent(int key, byte value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TByteFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TIntByteProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(int key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(int key, byte amount) { throw new UnsupportedOperationException(); }
    public byte adjustOrPutValue(int key, byte adjust_amount, byte put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableIntShortMap
    implements TIntShortMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final TIntShortMap m;
    
    public TUnmodifiableIntShortMap(TIntShortMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(int key) { return m.containsKey(key); }
    public boolean containsValue(short val) { return m.containsValue(val); }
    public short get(int key) { return m.get(key); }
    
    public short put(int key, short value) { throw new UnsupportedOperationException(); }
    public short remove(int key) { throw new UnsupportedOperationException(); }
    public void putAll(TIntShortMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Integer, ? extends Short> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TIntSet keySet = null;
    private transient TShortCollection values = null;
    
    public gnu.trove.set.TIntSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public int[] keys() { return m.keys(); }
    public int[] keys(int[] array) { return m.keys(array); }
    
    public TShortCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public short[] values() { return m.values(); }
    public short[] values(short[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public int getNoEntryKey() { return m.getNoEntryKey(); }
    public short getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TIntProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TShortProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TIntShortProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TIntShortIterator iterator() {
      new gnu.trove.iterator.TIntShortIterator() {
        gnu.trove.iterator.TIntShortIterator iter = m.iterator();
        
        public int key() { return iter.key(); }
        public short value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public short setValue(short val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public short putIfAbsent(int key, short value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TShortFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TIntShortProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(int key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(int key, short amount) { throw new UnsupportedOperationException(); }
    public short adjustOrPutValue(int key, short adjust_amount, short put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableIntCharMap
    implements gnu.trove.map.TIntCharMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final gnu.trove.map.TIntCharMap m;
    
    public TUnmodifiableIntCharMap(gnu.trove.map.TIntCharMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(int key) { return m.containsKey(key); }
    public boolean containsValue(char val) { return m.containsValue(val); }
    public char get(int key) { return m.get(key); }
    
    public char put(int key, char value) { throw new UnsupportedOperationException(); }
    public char remove(int key) { throw new UnsupportedOperationException(); }
    public void putAll(gnu.trove.map.TIntCharMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Integer, ? extends Character> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TIntSet keySet = null;
    private transient TCharCollection values = null;
    
    public gnu.trove.set.TIntSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public int[] keys() { return m.keys(); }
    public int[] keys(int[] array) { return m.keys(array); }
    
    public TCharCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public char[] values() { return m.values(); }
    public char[] values(char[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public int getNoEntryKey() { return m.getNoEntryKey(); }
    public char getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TIntProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TCharProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TIntCharProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TIntCharIterator iterator() {
      new gnu.trove.iterator.TIntCharIterator() {
        gnu.trove.iterator.TIntCharIterator iter = m.iterator();
        
        public int key() { return iter.key(); }
        public char value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public char setValue(char val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public char putIfAbsent(int key, char value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TCharFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TIntCharProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(int key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(int key, char amount) { throw new UnsupportedOperationException(); }
    public char adjustOrPutValue(int key, char adjust_amount, char put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableLongDoubleMap
    implements TLongDoubleMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final TLongDoubleMap m;
    
    public TUnmodifiableLongDoubleMap(TLongDoubleMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(long key) { return m.containsKey(key); }
    public boolean containsValue(double val) { return m.containsValue(val); }
    public double get(long key) { return m.get(key); }
    
    public double put(long key, double value) { throw new UnsupportedOperationException(); }
    public double remove(long key) { throw new UnsupportedOperationException(); }
    public void putAll(TLongDoubleMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Long, ? extends Double> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TLongSet keySet = null;
    private transient TDoubleCollection values = null;
    
    public gnu.trove.set.TLongSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public long[] keys() { return m.keys(); }
    public long[] keys(long[] array) { return m.keys(array); }
    
    public TDoubleCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public double[] values() { return m.values(); }
    public double[] values(double[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public long getNoEntryKey() { return m.getNoEntryKey(); }
    public double getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TLongProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TDoubleProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TLongDoubleProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TLongDoubleIterator iterator() {
      new gnu.trove.iterator.TLongDoubleIterator() {
        gnu.trove.iterator.TLongDoubleIterator iter = m.iterator();
        
        public long key() { return iter.key(); }
        public double value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public double setValue(double val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public double putIfAbsent(long key, double value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TDoubleFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TLongDoubleProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(long key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(long key, double amount) { throw new UnsupportedOperationException(); }
    public double adjustOrPutValue(long key, double adjust_amount, double put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableLongFloatMap
    implements gnu.trove.map.TLongFloatMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final gnu.trove.map.TLongFloatMap m;
    
    public TUnmodifiableLongFloatMap(gnu.trove.map.TLongFloatMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(long key) { return m.containsKey(key); }
    public boolean containsValue(float val) { return m.containsValue(val); }
    public float get(long key) { return m.get(key); }
    
    public float put(long key, float value) { throw new UnsupportedOperationException(); }
    public float remove(long key) { throw new UnsupportedOperationException(); }
    public void putAll(gnu.trove.map.TLongFloatMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Long, ? extends Float> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TLongSet keySet = null;
    private transient TFloatCollection values = null;
    
    public gnu.trove.set.TLongSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public long[] keys() { return m.keys(); }
    public long[] keys(long[] array) { return m.keys(array); }
    
    public TFloatCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public float[] values() { return m.values(); }
    public float[] values(float[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public long getNoEntryKey() { return m.getNoEntryKey(); }
    public float getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TLongProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TFloatProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TLongFloatProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TLongFloatIterator iterator() {
      new gnu.trove.iterator.TLongFloatIterator() {
        gnu.trove.iterator.TLongFloatIterator iter = m.iterator();
        
        public long key() { return iter.key(); }
        public float value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public float setValue(float val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public float putIfAbsent(long key, float value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TFloatFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TLongFloatProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(long key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(long key, float amount) { throw new UnsupportedOperationException(); }
    public float adjustOrPutValue(long key, float adjust_amount, float put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableLongIntMap
    implements gnu.trove.map.TLongIntMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final gnu.trove.map.TLongIntMap m;
    
    public TUnmodifiableLongIntMap(gnu.trove.map.TLongIntMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(long key) { return m.containsKey(key); }
    public boolean containsValue(int val) { return m.containsValue(val); }
    public int get(long key) { return m.get(key); }
    
    public int put(long key, int value) { throw new UnsupportedOperationException(); }
    public int remove(long key) { throw new UnsupportedOperationException(); }
    public void putAll(gnu.trove.map.TLongIntMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Long, ? extends Integer> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TLongSet keySet = null;
    private transient TIntCollection values = null;
    
    public gnu.trove.set.TLongSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public long[] keys() { return m.keys(); }
    public long[] keys(long[] array) { return m.keys(array); }
    
    public TIntCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public int[] values() { return m.values(); }
    public int[] values(int[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public long getNoEntryKey() { return m.getNoEntryKey(); }
    public int getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TLongProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TIntProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TLongIntProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TLongIntIterator iterator() {
      new gnu.trove.iterator.TLongIntIterator() {
        gnu.trove.iterator.TLongIntIterator iter = m.iterator();
        
        public long key() { return iter.key(); }
        public int value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public int setValue(int val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public int putIfAbsent(long key, int value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TIntFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TLongIntProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(long key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(long key, int amount) { throw new UnsupportedOperationException(); }
    public int adjustOrPutValue(long key, int adjust_amount, int put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableLongLongMap
    implements TLongLongMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final TLongLongMap m;
    
    public TUnmodifiableLongLongMap(TLongLongMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(long key) { return m.containsKey(key); }
    public boolean containsValue(long val) { return m.containsValue(val); }
    public long get(long key) { return m.get(key); }
    
    public long put(long key, long value) { throw new UnsupportedOperationException(); }
    public long remove(long key) { throw new UnsupportedOperationException(); }
    public void putAll(TLongLongMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Long, ? extends Long> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TLongSet keySet = null;
    private transient TLongCollection values = null;
    
    public gnu.trove.set.TLongSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public long[] keys() { return m.keys(); }
    public long[] keys(long[] array) { return m.keys(array); }
    
    public TLongCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public long[] values() { return m.values(); }
    public long[] values(long[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public long getNoEntryKey() { return m.getNoEntryKey(); }
    public long getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TLongProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TLongProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TLongLongProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TLongLongIterator iterator() {
      new gnu.trove.iterator.TLongLongIterator() {
        gnu.trove.iterator.TLongLongIterator iter = m.iterator();
        
        public long key() { return iter.key(); }
        public long value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public long setValue(long val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public long putIfAbsent(long key, long value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TLongFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TLongLongProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(long key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(long key, long amount) { throw new UnsupportedOperationException(); }
    public long adjustOrPutValue(long key, long adjust_amount, long put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableLongByteMap
    implements gnu.trove.map.TLongByteMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final gnu.trove.map.TLongByteMap m;
    
    public TUnmodifiableLongByteMap(gnu.trove.map.TLongByteMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(long key) { return m.containsKey(key); }
    public boolean containsValue(byte val) { return m.containsValue(val); }
    public byte get(long key) { return m.get(key); }
    
    public byte put(long key, byte value) { throw new UnsupportedOperationException(); }
    public byte remove(long key) { throw new UnsupportedOperationException(); }
    public void putAll(gnu.trove.map.TLongByteMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Long, ? extends Byte> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TLongSet keySet = null;
    private transient TByteCollection values = null;
    
    public gnu.trove.set.TLongSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public long[] keys() { return m.keys(); }
    public long[] keys(long[] array) { return m.keys(array); }
    
    public TByteCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public byte[] values() { return m.values(); }
    public byte[] values(byte[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public long getNoEntryKey() { return m.getNoEntryKey(); }
    public byte getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TLongProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TByteProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TLongByteProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TLongByteIterator iterator() {
      new gnu.trove.iterator.TLongByteIterator() {
        gnu.trove.iterator.TLongByteIterator iter = m.iterator();
        
        public long key() { return iter.key(); }
        public byte value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public byte setValue(byte val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public byte putIfAbsent(long key, byte value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TByteFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TLongByteProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(long key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(long key, byte amount) { throw new UnsupportedOperationException(); }
    public byte adjustOrPutValue(long key, byte adjust_amount, byte put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableLongShortMap
    implements TLongShortMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final TLongShortMap m;
    
    public TUnmodifiableLongShortMap(TLongShortMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(long key) { return m.containsKey(key); }
    public boolean containsValue(short val) { return m.containsValue(val); }
    public short get(long key) { return m.get(key); }
    
    public short put(long key, short value) { throw new UnsupportedOperationException(); }
    public short remove(long key) { throw new UnsupportedOperationException(); }
    public void putAll(TLongShortMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Long, ? extends Short> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TLongSet keySet = null;
    private transient TShortCollection values = null;
    
    public gnu.trove.set.TLongSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public long[] keys() { return m.keys(); }
    public long[] keys(long[] array) { return m.keys(array); }
    
    public TShortCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public short[] values() { return m.values(); }
    public short[] values(short[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public long getNoEntryKey() { return m.getNoEntryKey(); }
    public short getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TLongProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TShortProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TLongShortProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TLongShortIterator iterator() {
      new gnu.trove.iterator.TLongShortIterator() {
        gnu.trove.iterator.TLongShortIterator iter = m.iterator();
        
        public long key() { return iter.key(); }
        public short value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public short setValue(short val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public short putIfAbsent(long key, short value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TShortFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TLongShortProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(long key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(long key, short amount) { throw new UnsupportedOperationException(); }
    public short adjustOrPutValue(long key, short adjust_amount, short put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableLongCharMap
    implements TLongCharMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final TLongCharMap m;
    
    public TUnmodifiableLongCharMap(TLongCharMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(long key) { return m.containsKey(key); }
    public boolean containsValue(char val) { return m.containsValue(val); }
    public char get(long key) { return m.get(key); }
    
    public char put(long key, char value) { throw new UnsupportedOperationException(); }
    public char remove(long key) { throw new UnsupportedOperationException(); }
    public void putAll(TLongCharMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Long, ? extends Character> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TLongSet keySet = null;
    private transient TCharCollection values = null;
    
    public gnu.trove.set.TLongSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public long[] keys() { return m.keys(); }
    public long[] keys(long[] array) { return m.keys(array); }
    
    public TCharCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public char[] values() { return m.values(); }
    public char[] values(char[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public long getNoEntryKey() { return m.getNoEntryKey(); }
    public char getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TLongProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TCharProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TLongCharProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TLongCharIterator iterator() {
      new gnu.trove.iterator.TLongCharIterator() {
        gnu.trove.iterator.TLongCharIterator iter = m.iterator();
        
        public long key() { return iter.key(); }
        public char value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public char setValue(char val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public char putIfAbsent(long key, char value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TCharFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TLongCharProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(long key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(long key, char amount) { throw new UnsupportedOperationException(); }
    public char adjustOrPutValue(long key, char adjust_amount, char put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableByteDoubleMap
    implements TByteDoubleMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final TByteDoubleMap m;
    
    public TUnmodifiableByteDoubleMap(TByteDoubleMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(byte key) { return m.containsKey(key); }
    public boolean containsValue(double val) { return m.containsValue(val); }
    public double get(byte key) { return m.get(key); }
    
    public double put(byte key, double value) { throw new UnsupportedOperationException(); }
    public double remove(byte key) { throw new UnsupportedOperationException(); }
    public void putAll(TByteDoubleMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Byte, ? extends Double> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TByteSet keySet = null;
    private transient TDoubleCollection values = null;
    
    public gnu.trove.set.TByteSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public byte[] keys() { return m.keys(); }
    public byte[] keys(byte[] array) { return m.keys(array); }
    
    public TDoubleCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public double[] values() { return m.values(); }
    public double[] values(double[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public byte getNoEntryKey() { return m.getNoEntryKey(); }
    public double getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TByteProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TDoubleProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TByteDoubleProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TByteDoubleIterator iterator() {
      new gnu.trove.iterator.TByteDoubleIterator() {
        gnu.trove.iterator.TByteDoubleIterator iter = m.iterator();
        
        public byte key() { return iter.key(); }
        public double value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public double setValue(double val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public double putIfAbsent(byte key, double value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TDoubleFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TByteDoubleProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(byte key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(byte key, double amount) { throw new UnsupportedOperationException(); }
    public double adjustOrPutValue(byte key, double adjust_amount, double put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableByteFloatMap
    implements gnu.trove.map.TByteFloatMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final gnu.trove.map.TByteFloatMap m;
    
    public TUnmodifiableByteFloatMap(gnu.trove.map.TByteFloatMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(byte key) { return m.containsKey(key); }
    public boolean containsValue(float val) { return m.containsValue(val); }
    public float get(byte key) { return m.get(key); }
    
    public float put(byte key, float value) { throw new UnsupportedOperationException(); }
    public float remove(byte key) { throw new UnsupportedOperationException(); }
    public void putAll(gnu.trove.map.TByteFloatMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Byte, ? extends Float> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TByteSet keySet = null;
    private transient TFloatCollection values = null;
    
    public gnu.trove.set.TByteSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public byte[] keys() { return m.keys(); }
    public byte[] keys(byte[] array) { return m.keys(array); }
    
    public TFloatCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public float[] values() { return m.values(); }
    public float[] values(float[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public byte getNoEntryKey() { return m.getNoEntryKey(); }
    public float getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TByteProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TFloatProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TByteFloatProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TByteFloatIterator iterator() {
      new gnu.trove.iterator.TByteFloatIterator() {
        gnu.trove.iterator.TByteFloatIterator iter = m.iterator();
        
        public byte key() { return iter.key(); }
        public float value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public float setValue(float val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public float putIfAbsent(byte key, float value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TFloatFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TByteFloatProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(byte key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(byte key, float amount) { throw new UnsupportedOperationException(); }
    public float adjustOrPutValue(byte key, float adjust_amount, float put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableByteIntMap
    implements gnu.trove.map.TByteIntMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final gnu.trove.map.TByteIntMap m;
    
    public TUnmodifiableByteIntMap(gnu.trove.map.TByteIntMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(byte key) { return m.containsKey(key); }
    public boolean containsValue(int val) { return m.containsValue(val); }
    public int get(byte key) { return m.get(key); }
    
    public int put(byte key, int value) { throw new UnsupportedOperationException(); }
    public int remove(byte key) { throw new UnsupportedOperationException(); }
    public void putAll(gnu.trove.map.TByteIntMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Byte, ? extends Integer> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TByteSet keySet = null;
    private transient TIntCollection values = null;
    
    public gnu.trove.set.TByteSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public byte[] keys() { return m.keys(); }
    public byte[] keys(byte[] array) { return m.keys(array); }
    
    public TIntCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public int[] values() { return m.values(); }
    public int[] values(int[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public byte getNoEntryKey() { return m.getNoEntryKey(); }
    public int getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TByteProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TIntProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TByteIntProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TByteIntIterator iterator() {
      new gnu.trove.iterator.TByteIntIterator() {
        gnu.trove.iterator.TByteIntIterator iter = m.iterator();
        
        public byte key() { return iter.key(); }
        public int value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public int setValue(int val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public int putIfAbsent(byte key, int value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TIntFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TByteIntProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(byte key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(byte key, int amount) { throw new UnsupportedOperationException(); }
    public int adjustOrPutValue(byte key, int adjust_amount, int put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableByteLongMap
    implements TByteLongMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final TByteLongMap m;
    
    public TUnmodifiableByteLongMap(TByteLongMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(byte key) { return m.containsKey(key); }
    public boolean containsValue(long val) { return m.containsValue(val); }
    public long get(byte key) { return m.get(key); }
    
    public long put(byte key, long value) { throw new UnsupportedOperationException(); }
    public long remove(byte key) { throw new UnsupportedOperationException(); }
    public void putAll(TByteLongMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Byte, ? extends Long> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TByteSet keySet = null;
    private transient TLongCollection values = null;
    
    public gnu.trove.set.TByteSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public byte[] keys() { return m.keys(); }
    public byte[] keys(byte[] array) { return m.keys(array); }
    
    public TLongCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public long[] values() { return m.values(); }
    public long[] values(long[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public byte getNoEntryKey() { return m.getNoEntryKey(); }
    public long getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TByteProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TLongProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TByteLongProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TByteLongIterator iterator() {
      new gnu.trove.iterator.TByteLongIterator() {
        gnu.trove.iterator.TByteLongIterator iter = m.iterator();
        
        public byte key() { return iter.key(); }
        public long value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public long setValue(long val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public long putIfAbsent(byte key, long value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TLongFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TByteLongProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(byte key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(byte key, long amount) { throw new UnsupportedOperationException(); }
    public long adjustOrPutValue(byte key, long adjust_amount, long put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableByteByteMap
    implements gnu.trove.map.TByteByteMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final gnu.trove.map.TByteByteMap m;
    
    public TUnmodifiableByteByteMap(gnu.trove.map.TByteByteMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(byte key) { return m.containsKey(key); }
    public boolean containsValue(byte val) { return m.containsValue(val); }
    public byte get(byte key) { return m.get(key); }
    
    public byte put(byte key, byte value) { throw new UnsupportedOperationException(); }
    public byte remove(byte key) { throw new UnsupportedOperationException(); }
    public void putAll(gnu.trove.map.TByteByteMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Byte, ? extends Byte> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TByteSet keySet = null;
    private transient TByteCollection values = null;
    
    public gnu.trove.set.TByteSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public byte[] keys() { return m.keys(); }
    public byte[] keys(byte[] array) { return m.keys(array); }
    
    public TByteCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public byte[] values() { return m.values(); }
    public byte[] values(byte[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public byte getNoEntryKey() { return m.getNoEntryKey(); }
    public byte getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TByteProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TByteProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TByteByteProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TByteByteIterator iterator() {
      new gnu.trove.iterator.TByteByteIterator() {
        gnu.trove.iterator.TByteByteIterator iter = m.iterator();
        
        public byte key() { return iter.key(); }
        public byte value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public byte setValue(byte val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public byte putIfAbsent(byte key, byte value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TByteFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TByteByteProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(byte key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(byte key, byte amount) { throw new UnsupportedOperationException(); }
    public byte adjustOrPutValue(byte key, byte adjust_amount, byte put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableByteShortMap
    implements TByteShortMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final TByteShortMap m;
    
    public TUnmodifiableByteShortMap(TByteShortMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(byte key) { return m.containsKey(key); }
    public boolean containsValue(short val) { return m.containsValue(val); }
    public short get(byte key) { return m.get(key); }
    
    public short put(byte key, short value) { throw new UnsupportedOperationException(); }
    public short remove(byte key) { throw new UnsupportedOperationException(); }
    public void putAll(TByteShortMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Byte, ? extends Short> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TByteSet keySet = null;
    private transient TShortCollection values = null;
    
    public gnu.trove.set.TByteSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public byte[] keys() { return m.keys(); }
    public byte[] keys(byte[] array) { return m.keys(array); }
    
    public TShortCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public short[] values() { return m.values(); }
    public short[] values(short[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public byte getNoEntryKey() { return m.getNoEntryKey(); }
    public short getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TByteProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TShortProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TByteShortProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TByteShortIterator iterator() {
      new gnu.trove.iterator.TByteShortIterator() {
        gnu.trove.iterator.TByteShortIterator iter = m.iterator();
        
        public byte key() { return iter.key(); }
        public short value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public short setValue(short val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public short putIfAbsent(byte key, short value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TShortFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TByteShortProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(byte key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(byte key, short amount) { throw new UnsupportedOperationException(); }
    public short adjustOrPutValue(byte key, short adjust_amount, short put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableByteCharMap
    implements TByteCharMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final TByteCharMap m;
    
    public TUnmodifiableByteCharMap(TByteCharMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(byte key) { return m.containsKey(key); }
    public boolean containsValue(char val) { return m.containsValue(val); }
    public char get(byte key) { return m.get(key); }
    
    public char put(byte key, char value) { throw new UnsupportedOperationException(); }
    public char remove(byte key) { throw new UnsupportedOperationException(); }
    public void putAll(TByteCharMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Byte, ? extends Character> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TByteSet keySet = null;
    private transient TCharCollection values = null;
    
    public gnu.trove.set.TByteSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public byte[] keys() { return m.keys(); }
    public byte[] keys(byte[] array) { return m.keys(array); }
    
    public TCharCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public char[] values() { return m.values(); }
    public char[] values(char[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public byte getNoEntryKey() { return m.getNoEntryKey(); }
    public char getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TByteProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TCharProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TByteCharProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TByteCharIterator iterator() {
      new gnu.trove.iterator.TByteCharIterator() {
        gnu.trove.iterator.TByteCharIterator iter = m.iterator();
        
        public byte key() { return iter.key(); }
        public char value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public char setValue(char val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public char putIfAbsent(byte key, char value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TCharFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TByteCharProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(byte key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(byte key, char amount) { throw new UnsupportedOperationException(); }
    public char adjustOrPutValue(byte key, char adjust_amount, char put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableShortDoubleMap
    implements TShortDoubleMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final TShortDoubleMap m;
    
    public TUnmodifiableShortDoubleMap(TShortDoubleMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(short key) { return m.containsKey(key); }
    public boolean containsValue(double val) { return m.containsValue(val); }
    public double get(short key) { return m.get(key); }
    
    public double put(short key, double value) { throw new UnsupportedOperationException(); }
    public double remove(short key) { throw new UnsupportedOperationException(); }
    public void putAll(TShortDoubleMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Short, ? extends Double> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TShortSet keySet = null;
    private transient TDoubleCollection values = null;
    
    public gnu.trove.set.TShortSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public short[] keys() { return m.keys(); }
    public short[] keys(short[] array) { return m.keys(array); }
    
    public TDoubleCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public double[] values() { return m.values(); }
    public double[] values(double[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public short getNoEntryKey() { return m.getNoEntryKey(); }
    public double getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TShortProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TDoubleProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TShortDoubleProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TShortDoubleIterator iterator() {
      new gnu.trove.iterator.TShortDoubleIterator() {
        gnu.trove.iterator.TShortDoubleIterator iter = m.iterator();
        
        public short key() { return iter.key(); }
        public double value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public double setValue(double val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public double putIfAbsent(short key, double value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TDoubleFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TShortDoubleProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(short key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(short key, double amount) { throw new UnsupportedOperationException(); }
    public double adjustOrPutValue(short key, double adjust_amount, double put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableShortFloatMap
    implements TShortFloatMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final TShortFloatMap m;
    
    public TUnmodifiableShortFloatMap(TShortFloatMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(short key) { return m.containsKey(key); }
    public boolean containsValue(float val) { return m.containsValue(val); }
    public float get(short key) { return m.get(key); }
    
    public float put(short key, float value) { throw new UnsupportedOperationException(); }
    public float remove(short key) { throw new UnsupportedOperationException(); }
    public void putAll(TShortFloatMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Short, ? extends Float> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TShortSet keySet = null;
    private transient TFloatCollection values = null;
    
    public gnu.trove.set.TShortSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public short[] keys() { return m.keys(); }
    public short[] keys(short[] array) { return m.keys(array); }
    
    public TFloatCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public float[] values() { return m.values(); }
    public float[] values(float[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public short getNoEntryKey() { return m.getNoEntryKey(); }
    public float getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TShortProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TFloatProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TShortFloatProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TShortFloatIterator iterator() {
      new gnu.trove.iterator.TShortFloatIterator() {
        gnu.trove.iterator.TShortFloatIterator iter = m.iterator();
        
        public short key() { return iter.key(); }
        public float value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public float setValue(float val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public float putIfAbsent(short key, float value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TFloatFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TShortFloatProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(short key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(short key, float amount) { throw new UnsupportedOperationException(); }
    public float adjustOrPutValue(short key, float adjust_amount, float put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableShortIntMap
    implements gnu.trove.map.TShortIntMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final gnu.trove.map.TShortIntMap m;
    
    public TUnmodifiableShortIntMap(gnu.trove.map.TShortIntMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(short key) { return m.containsKey(key); }
    public boolean containsValue(int val) { return m.containsValue(val); }
    public int get(short key) { return m.get(key); }
    
    public int put(short key, int value) { throw new UnsupportedOperationException(); }
    public int remove(short key) { throw new UnsupportedOperationException(); }
    public void putAll(gnu.trove.map.TShortIntMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Short, ? extends Integer> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TShortSet keySet = null;
    private transient TIntCollection values = null;
    
    public gnu.trove.set.TShortSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public short[] keys() { return m.keys(); }
    public short[] keys(short[] array) { return m.keys(array); }
    
    public TIntCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public int[] values() { return m.values(); }
    public int[] values(int[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public short getNoEntryKey() { return m.getNoEntryKey(); }
    public int getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TShortProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TIntProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TShortIntProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TShortIntIterator iterator() {
      new gnu.trove.iterator.TShortIntIterator() {
        gnu.trove.iterator.TShortIntIterator iter = m.iterator();
        
        public short key() { return iter.key(); }
        public int value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public int setValue(int val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public int putIfAbsent(short key, int value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TIntFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TShortIntProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(short key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(short key, int amount) { throw new UnsupportedOperationException(); }
    public int adjustOrPutValue(short key, int adjust_amount, int put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableShortLongMap
    implements TShortLongMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final TShortLongMap m;
    
    public TUnmodifiableShortLongMap(TShortLongMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(short key) { return m.containsKey(key); }
    public boolean containsValue(long val) { return m.containsValue(val); }
    public long get(short key) { return m.get(key); }
    
    public long put(short key, long value) { throw new UnsupportedOperationException(); }
    public long remove(short key) { throw new UnsupportedOperationException(); }
    public void putAll(TShortLongMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Short, ? extends Long> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TShortSet keySet = null;
    private transient TLongCollection values = null;
    
    public gnu.trove.set.TShortSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public short[] keys() { return m.keys(); }
    public short[] keys(short[] array) { return m.keys(array); }
    
    public TLongCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public long[] values() { return m.values(); }
    public long[] values(long[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public short getNoEntryKey() { return m.getNoEntryKey(); }
    public long getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TShortProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TLongProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TShortLongProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TShortLongIterator iterator() {
      new gnu.trove.iterator.TShortLongIterator() {
        gnu.trove.iterator.TShortLongIterator iter = m.iterator();
        
        public short key() { return iter.key(); }
        public long value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public long setValue(long val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public long putIfAbsent(short key, long value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TLongFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TShortLongProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(short key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(short key, long amount) { throw new UnsupportedOperationException(); }
    public long adjustOrPutValue(short key, long adjust_amount, long put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableShortByteMap
    implements gnu.trove.map.TShortByteMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final gnu.trove.map.TShortByteMap m;
    
    public TUnmodifiableShortByteMap(gnu.trove.map.TShortByteMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(short key) { return m.containsKey(key); }
    public boolean containsValue(byte val) { return m.containsValue(val); }
    public byte get(short key) { return m.get(key); }
    
    public byte put(short key, byte value) { throw new UnsupportedOperationException(); }
    public byte remove(short key) { throw new UnsupportedOperationException(); }
    public void putAll(gnu.trove.map.TShortByteMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Short, ? extends Byte> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TShortSet keySet = null;
    private transient TByteCollection values = null;
    
    public gnu.trove.set.TShortSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public short[] keys() { return m.keys(); }
    public short[] keys(short[] array) { return m.keys(array); }
    
    public TByteCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public byte[] values() { return m.values(); }
    public byte[] values(byte[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public short getNoEntryKey() { return m.getNoEntryKey(); }
    public byte getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TShortProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TByteProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TShortByteProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TShortByteIterator iterator() {
      new gnu.trove.iterator.TShortByteIterator() {
        gnu.trove.iterator.TShortByteIterator iter = m.iterator();
        
        public short key() { return iter.key(); }
        public byte value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public byte setValue(byte val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public byte putIfAbsent(short key, byte value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TByteFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TShortByteProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(short key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(short key, byte amount) { throw new UnsupportedOperationException(); }
    public byte adjustOrPutValue(short key, byte adjust_amount, byte put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableShortShortMap
    implements TShortShortMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final TShortShortMap m;
    
    public TUnmodifiableShortShortMap(TShortShortMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(short key) { return m.containsKey(key); }
    public boolean containsValue(short val) { return m.containsValue(val); }
    public short get(short key) { return m.get(key); }
    
    public short put(short key, short value) { throw new UnsupportedOperationException(); }
    public short remove(short key) { throw new UnsupportedOperationException(); }
    public void putAll(TShortShortMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Short, ? extends Short> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TShortSet keySet = null;
    private transient TShortCollection values = null;
    
    public gnu.trove.set.TShortSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public short[] keys() { return m.keys(); }
    public short[] keys(short[] array) { return m.keys(array); }
    
    public TShortCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public short[] values() { return m.values(); }
    public short[] values(short[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public short getNoEntryKey() { return m.getNoEntryKey(); }
    public short getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TShortProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TShortProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TShortShortProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TShortShortIterator iterator() {
      new gnu.trove.iterator.TShortShortIterator() {
        gnu.trove.iterator.TShortShortIterator iter = m.iterator();
        
        public short key() { return iter.key(); }
        public short value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public short setValue(short val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public short putIfAbsent(short key, short value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TShortFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TShortShortProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(short key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(short key, short amount) { throw new UnsupportedOperationException(); }
    public short adjustOrPutValue(short key, short adjust_amount, short put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableShortCharMap
    implements TShortCharMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final TShortCharMap m;
    
    public TUnmodifiableShortCharMap(TShortCharMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(short key) { return m.containsKey(key); }
    public boolean containsValue(char val) { return m.containsValue(val); }
    public char get(short key) { return m.get(key); }
    
    public char put(short key, char value) { throw new UnsupportedOperationException(); }
    public char remove(short key) { throw new UnsupportedOperationException(); }
    public void putAll(TShortCharMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Short, ? extends Character> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TShortSet keySet = null;
    private transient TCharCollection values = null;
    
    public gnu.trove.set.TShortSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public short[] keys() { return m.keys(); }
    public short[] keys(short[] array) { return m.keys(array); }
    
    public TCharCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public char[] values() { return m.values(); }
    public char[] values(char[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public short getNoEntryKey() { return m.getNoEntryKey(); }
    public char getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TShortProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TCharProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TShortCharProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TShortCharIterator iterator() {
      new gnu.trove.iterator.TShortCharIterator() {
        gnu.trove.iterator.TShortCharIterator iter = m.iterator();
        
        public short key() { return iter.key(); }
        public char value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public char setValue(char val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public char putIfAbsent(short key, char value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TCharFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TShortCharProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(short key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(short key, char amount) { throw new UnsupportedOperationException(); }
    public char adjustOrPutValue(short key, char adjust_amount, char put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableCharDoubleMap
    implements TCharDoubleMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final TCharDoubleMap m;
    
    public TUnmodifiableCharDoubleMap(TCharDoubleMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(char key) { return m.containsKey(key); }
    public boolean containsValue(double val) { return m.containsValue(val); }
    public double get(char key) { return m.get(key); }
    
    public double put(char key, double value) { throw new UnsupportedOperationException(); }
    public double remove(char key) { throw new UnsupportedOperationException(); }
    public void putAll(TCharDoubleMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Character, ? extends Double> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TCharSet keySet = null;
    private transient TDoubleCollection values = null;
    
    public gnu.trove.set.TCharSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public char[] keys() { return m.keys(); }
    public char[] keys(char[] array) { return m.keys(array); }
    
    public TDoubleCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public double[] values() { return m.values(); }
    public double[] values(double[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public char getNoEntryKey() { return m.getNoEntryKey(); }
    public double getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TCharProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TDoubleProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TCharDoubleProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TCharDoubleIterator iterator() {
      new gnu.trove.iterator.TCharDoubleIterator() {
        gnu.trove.iterator.TCharDoubleIterator iter = m.iterator();
        
        public char key() { return iter.key(); }
        public double value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public double setValue(double val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public double putIfAbsent(char key, double value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TDoubleFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TCharDoubleProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(char key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(char key, double amount) { throw new UnsupportedOperationException(); }
    public double adjustOrPutValue(char key, double adjust_amount, double put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableCharFloatMap
    implements TCharFloatMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final TCharFloatMap m;
    
    public TUnmodifiableCharFloatMap(TCharFloatMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(char key) { return m.containsKey(key); }
    public boolean containsValue(float val) { return m.containsValue(val); }
    public float get(char key) { return m.get(key); }
    
    public float put(char key, float value) { throw new UnsupportedOperationException(); }
    public float remove(char key) { throw new UnsupportedOperationException(); }
    public void putAll(TCharFloatMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Character, ? extends Float> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TCharSet keySet = null;
    private transient TFloatCollection values = null;
    
    public gnu.trove.set.TCharSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public char[] keys() { return m.keys(); }
    public char[] keys(char[] array) { return m.keys(array); }
    
    public TFloatCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public float[] values() { return m.values(); }
    public float[] values(float[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public char getNoEntryKey() { return m.getNoEntryKey(); }
    public float getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TCharProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TFloatProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TCharFloatProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TCharFloatIterator iterator() {
      new gnu.trove.iterator.TCharFloatIterator() {
        gnu.trove.iterator.TCharFloatIterator iter = m.iterator();
        
        public char key() { return iter.key(); }
        public float value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public float setValue(float val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public float putIfAbsent(char key, float value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TFloatFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TCharFloatProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(char key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(char key, float amount) { throw new UnsupportedOperationException(); }
    public float adjustOrPutValue(char key, float adjust_amount, float put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableCharIntMap
    implements gnu.trove.map.TCharIntMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final gnu.trove.map.TCharIntMap m;
    
    public TUnmodifiableCharIntMap(gnu.trove.map.TCharIntMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(char key) { return m.containsKey(key); }
    public boolean containsValue(int val) { return m.containsValue(val); }
    public int get(char key) { return m.get(key); }
    
    public int put(char key, int value) { throw new UnsupportedOperationException(); }
    public int remove(char key) { throw new UnsupportedOperationException(); }
    public void putAll(gnu.trove.map.TCharIntMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Character, ? extends Integer> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TCharSet keySet = null;
    private transient TIntCollection values = null;
    
    public gnu.trove.set.TCharSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public char[] keys() { return m.keys(); }
    public char[] keys(char[] array) { return m.keys(array); }
    
    public TIntCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public int[] values() { return m.values(); }
    public int[] values(int[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public char getNoEntryKey() { return m.getNoEntryKey(); }
    public int getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TCharProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TIntProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TCharIntProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TCharIntIterator iterator() {
      new gnu.trove.iterator.TCharIntIterator() {
        gnu.trove.iterator.TCharIntIterator iter = m.iterator();
        
        public char key() { return iter.key(); }
        public int value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public int setValue(int val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public int putIfAbsent(char key, int value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TIntFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TCharIntProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(char key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(char key, int amount) { throw new UnsupportedOperationException(); }
    public int adjustOrPutValue(char key, int adjust_amount, int put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableCharLongMap
    implements TCharLongMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final TCharLongMap m;
    
    public TUnmodifiableCharLongMap(TCharLongMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(char key) { return m.containsKey(key); }
    public boolean containsValue(long val) { return m.containsValue(val); }
    public long get(char key) { return m.get(key); }
    
    public long put(char key, long value) { throw new UnsupportedOperationException(); }
    public long remove(char key) { throw new UnsupportedOperationException(); }
    public void putAll(TCharLongMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Character, ? extends Long> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TCharSet keySet = null;
    private transient TLongCollection values = null;
    
    public gnu.trove.set.TCharSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public char[] keys() { return m.keys(); }
    public char[] keys(char[] array) { return m.keys(array); }
    
    public TLongCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public long[] values() { return m.values(); }
    public long[] values(long[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public char getNoEntryKey() { return m.getNoEntryKey(); }
    public long getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TCharProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TLongProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TCharLongProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TCharLongIterator iterator() {
      new gnu.trove.iterator.TCharLongIterator() {
        gnu.trove.iterator.TCharLongIterator iter = m.iterator();
        
        public char key() { return iter.key(); }
        public long value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public long setValue(long val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public long putIfAbsent(char key, long value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TLongFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TCharLongProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(char key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(char key, long amount) { throw new UnsupportedOperationException(); }
    public long adjustOrPutValue(char key, long adjust_amount, long put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableCharByteMap
    implements gnu.trove.map.TCharByteMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final gnu.trove.map.TCharByteMap m;
    
    public TUnmodifiableCharByteMap(gnu.trove.map.TCharByteMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(char key) { return m.containsKey(key); }
    public boolean containsValue(byte val) { return m.containsValue(val); }
    public byte get(char key) { return m.get(key); }
    
    public byte put(char key, byte value) { throw new UnsupportedOperationException(); }
    public byte remove(char key) { throw new UnsupportedOperationException(); }
    public void putAll(gnu.trove.map.TCharByteMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Character, ? extends Byte> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TCharSet keySet = null;
    private transient TByteCollection values = null;
    
    public gnu.trove.set.TCharSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public char[] keys() { return m.keys(); }
    public char[] keys(char[] array) { return m.keys(array); }
    
    public TByteCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public byte[] values() { return m.values(); }
    public byte[] values(byte[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public char getNoEntryKey() { return m.getNoEntryKey(); }
    public byte getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TCharProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TByteProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TCharByteProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TCharByteIterator iterator() {
      new gnu.trove.iterator.TCharByteIterator() {
        gnu.trove.iterator.TCharByteIterator iter = m.iterator();
        
        public char key() { return iter.key(); }
        public byte value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public byte setValue(byte val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public byte putIfAbsent(char key, byte value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TByteFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TCharByteProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(char key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(char key, byte amount) { throw new UnsupportedOperationException(); }
    public byte adjustOrPutValue(char key, byte adjust_amount, byte put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableCharShortMap
    implements TCharShortMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final TCharShortMap m;
    
    public TUnmodifiableCharShortMap(TCharShortMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(char key) { return m.containsKey(key); }
    public boolean containsValue(short val) { return m.containsValue(val); }
    public short get(char key) { return m.get(key); }
    
    public short put(char key, short value) { throw new UnsupportedOperationException(); }
    public short remove(char key) { throw new UnsupportedOperationException(); }
    public void putAll(TCharShortMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Character, ? extends Short> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TCharSet keySet = null;
    private transient TShortCollection values = null;
    
    public gnu.trove.set.TCharSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public char[] keys() { return m.keys(); }
    public char[] keys(char[] array) { return m.keys(array); }
    
    public TShortCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public short[] values() { return m.values(); }
    public short[] values(short[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public char getNoEntryKey() { return m.getNoEntryKey(); }
    public short getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TCharProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TShortProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TCharShortProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TCharShortIterator iterator() {
      new gnu.trove.iterator.TCharShortIterator() {
        gnu.trove.iterator.TCharShortIterator iter = m.iterator();
        
        public char key() { return iter.key(); }
        public short value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public short setValue(short val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public short putIfAbsent(char key, short value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TShortFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TCharShortProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(char key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(char key, short amount) { throw new UnsupportedOperationException(); }
    public short adjustOrPutValue(char key, short adjust_amount, short put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableCharCharMap
    implements gnu.trove.map.TCharCharMap, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final gnu.trove.map.TCharCharMap m;
    
    public TUnmodifiableCharCharMap(gnu.trove.map.TCharCharMap m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(char key) { return m.containsKey(key); }
    public boolean containsValue(char val) { return m.containsValue(val); }
    public char get(char key) { return m.get(key); }
    
    public char put(char key, char value) { throw new UnsupportedOperationException(); }
    public char remove(char key) { throw new UnsupportedOperationException(); }
    public void putAll(gnu.trove.map.TCharCharMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Character, ? extends Character> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TCharSet keySet = null;
    private transient TCharCollection values = null;
    
    public gnu.trove.set.TCharSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public char[] keys() { return m.keys(); }
    public char[] keys(char[] array) { return m.keys(array); }
    
    public TCharCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public char[] values() { return m.values(); }
    public char[] values(char[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public char getNoEntryKey() { return m.getNoEntryKey(); }
    public char getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TCharProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TCharProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TCharCharProcedure procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TCharCharIterator iterator() {
      new gnu.trove.iterator.TCharCharIterator() {
        gnu.trove.iterator.TCharCharIterator iter = m.iterator();
        
        public char key() { return iter.key(); }
        public char value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public char setValue(char val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public char putIfAbsent(char key, char value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TCharFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TCharCharProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(char key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(char key, char amount) { throw new UnsupportedOperationException(); }
    public char adjustOrPutValue(char key, char adjust_amount, char put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableDoubleObjectMap<V>
    implements gnu.trove.map.TDoubleObjectMap<V>, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final gnu.trove.map.TDoubleObjectMap<V> m;
    

    public TUnmodifiableDoubleObjectMap(gnu.trove.map.TDoubleObjectMap<V> m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(double key) { return m.containsKey(key); }
    public boolean containsValue(Object val) { return m.containsValue(val); }
    public V get(double key) { return m.get(key); }
    
    public V put(double key, V value) { throw new UnsupportedOperationException(); }
    public V remove(double key) { throw new UnsupportedOperationException(); }
    public void putAll(gnu.trove.map.TDoubleObjectMap<V> m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Double, ? extends V> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TDoubleSet keySet = null;
    private transient Collection<V> values = null;
    
    public gnu.trove.set.TDoubleSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public double[] keys() { return m.keys(); }
    public double[] keys(double[] array) { return m.keys(array); }
    
    public Collection<V> valueCollection() {
      if (values == null)
        values = java.util.Collections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public V[] values() { return m.values(); }
    public <T> T[] values(T[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public double getNoEntryKey() { return m.getNoEntryKey(); }
    
    public boolean forEachKey(TDoubleProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TObjectProcedure<V> procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TDoubleObjectProcedure<V> procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TDoubleObjectIterator<V> iterator() {
      new gnu.trove.iterator.TDoubleObjectIterator() {
        gnu.trove.iterator.TDoubleObjectIterator<V> iter = m.iterator();
        
        public double key() { return iter.key(); }
        public V value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public V setValue(V val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public V putIfAbsent(double key, V value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TObjectFunction<V, V> function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TDoubleObjectProcedure<V> procedure) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableFloatObjectMap<V>
    implements gnu.trove.map.TFloatObjectMap<V>, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final gnu.trove.map.TFloatObjectMap<V> m;
    
    public TUnmodifiableFloatObjectMap(gnu.trove.map.TFloatObjectMap<V> m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(float key) { return m.containsKey(key); }
    public boolean containsValue(Object val) { return m.containsValue(val); }
    public V get(float key) { return m.get(key); }
    
    public V put(float key, V value) { throw new UnsupportedOperationException(); }
    public V remove(float key) { throw new UnsupportedOperationException(); }
    public void putAll(gnu.trove.map.TFloatObjectMap<V> m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Float, ? extends V> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TFloatSet keySet = null;
    private transient Collection<V> values = null;
    
    public gnu.trove.set.TFloatSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public float[] keys() { return m.keys(); }
    public float[] keys(float[] array) { return m.keys(array); }
    
    public Collection<V> valueCollection() {
      if (values == null)
        values = java.util.Collections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public V[] values() { return m.values(); }
    public <T> T[] values(T[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public float getNoEntryKey() { return m.getNoEntryKey(); }
    
    public boolean forEachKey(TFloatProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TObjectProcedure<V> procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TFloatObjectProcedure<V> procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TFloatObjectIterator<V> iterator() {
      new gnu.trove.iterator.TFloatObjectIterator() {
        gnu.trove.iterator.TFloatObjectIterator<V> iter = m.iterator();
        
        public float key() { return iter.key(); }
        public V value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public V setValue(V val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public V putIfAbsent(float key, V value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TObjectFunction<V, V> function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TFloatObjectProcedure<V> procedure) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableIntObjectMap<V>
    implements gnu.trove.map.TIntObjectMap<V>, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final gnu.trove.map.TIntObjectMap<V> m;
    
    public TUnmodifiableIntObjectMap(gnu.trove.map.TIntObjectMap<V> m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(int key) { return m.containsKey(key); }
    public boolean containsValue(Object val) { return m.containsValue(val); }
    public V get(int key) { return m.get(key); }
    
    public V put(int key, V value) { throw new UnsupportedOperationException(); }
    public V remove(int key) { throw new UnsupportedOperationException(); }
    public void putAll(gnu.trove.map.TIntObjectMap<V> m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Integer, ? extends V> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TIntSet keySet = null;
    private transient Collection<V> values = null;
    
    public gnu.trove.set.TIntSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public int[] keys() { return m.keys(); }
    public int[] keys(int[] array) { return m.keys(array); }
    
    public Collection<V> valueCollection() {
      if (values == null)
        values = java.util.Collections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public V[] values() { return m.values(); }
    public <T> T[] values(T[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public int getNoEntryKey() { return m.getNoEntryKey(); }
    
    public boolean forEachKey(TIntProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TObjectProcedure<V> procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TIntObjectProcedure<V> procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TIntObjectIterator<V> iterator() {
      new gnu.trove.iterator.TIntObjectIterator() {
        gnu.trove.iterator.TIntObjectIterator<V> iter = m.iterator();
        
        public int key() { return iter.key(); }
        public V value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public V setValue(V val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public V putIfAbsent(int key, V value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TObjectFunction<V, V> function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TIntObjectProcedure<V> procedure) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableLongObjectMap<V>
    implements gnu.trove.map.TLongObjectMap<V>, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final gnu.trove.map.TLongObjectMap<V> m;
    
    public TUnmodifiableLongObjectMap(gnu.trove.map.TLongObjectMap<V> m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(long key) { return m.containsKey(key); }
    public boolean containsValue(Object val) { return m.containsValue(val); }
    public V get(long key) { return m.get(key); }
    
    public V put(long key, V value) { throw new UnsupportedOperationException(); }
    public V remove(long key) { throw new UnsupportedOperationException(); }
    public void putAll(gnu.trove.map.TLongObjectMap<V> m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Long, ? extends V> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TLongSet keySet = null;
    private transient Collection<V> values = null;
    
    public gnu.trove.set.TLongSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public long[] keys() { return m.keys(); }
    public long[] keys(long[] array) { return m.keys(array); }
    
    public Collection<V> valueCollection() {
      if (values == null)
        values = java.util.Collections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public V[] values() { return m.values(); }
    public <T> T[] values(T[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public long getNoEntryKey() { return m.getNoEntryKey(); }
    
    public boolean forEachKey(TLongProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TObjectProcedure<V> procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TLongObjectProcedure<V> procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TLongObjectIterator<V> iterator() {
      new gnu.trove.iterator.TLongObjectIterator() {
        gnu.trove.iterator.TLongObjectIterator<V> iter = m.iterator();
        
        public long key() { return iter.key(); }
        public V value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public V setValue(V val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public V putIfAbsent(long key, V value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TObjectFunction<V, V> function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TLongObjectProcedure<V> procedure) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableByteObjectMap<V>
    implements gnu.trove.map.TByteObjectMap<V>, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final gnu.trove.map.TByteObjectMap<V> m;
    
    public TUnmodifiableByteObjectMap(gnu.trove.map.TByteObjectMap<V> m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(byte key) { return m.containsKey(key); }
    public boolean containsValue(Object val) { return m.containsValue(val); }
    public V get(byte key) { return m.get(key); }
    
    public V put(byte key, V value) { throw new UnsupportedOperationException(); }
    public V remove(byte key) { throw new UnsupportedOperationException(); }
    public void putAll(gnu.trove.map.TByteObjectMap<V> m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Byte, ? extends V> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TByteSet keySet = null;
    private transient Collection<V> values = null;
    
    public gnu.trove.set.TByteSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public byte[] keys() { return m.keys(); }
    public byte[] keys(byte[] array) { return m.keys(array); }
    
    public Collection<V> valueCollection() {
      if (values == null)
        values = java.util.Collections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public V[] values() { return m.values(); }
    public <T> T[] values(T[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public byte getNoEntryKey() { return m.getNoEntryKey(); }
    
    public boolean forEachKey(TByteProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TObjectProcedure<V> procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TByteObjectProcedure<V> procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TByteObjectIterator<V> iterator() {
      new gnu.trove.iterator.TByteObjectIterator() {
        gnu.trove.iterator.TByteObjectIterator<V> iter = m.iterator();
        
        public byte key() { return iter.key(); }
        public V value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public V setValue(V val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public V putIfAbsent(byte key, V value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TObjectFunction<V, V> function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TByteObjectProcedure<V> procedure) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableShortObjectMap<V>
    implements gnu.trove.map.TShortObjectMap<V>, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final gnu.trove.map.TShortObjectMap<V> m;
    
    public TUnmodifiableShortObjectMap(gnu.trove.map.TShortObjectMap<V> m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(short key) { return m.containsKey(key); }
    public boolean containsValue(Object val) { return m.containsValue(val); }
    public V get(short key) { return m.get(key); }
    
    public V put(short key, V value) { throw new UnsupportedOperationException(); }
    public V remove(short key) { throw new UnsupportedOperationException(); }
    public void putAll(gnu.trove.map.TShortObjectMap<V> m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Short, ? extends V> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TShortSet keySet = null;
    private transient Collection<V> values = null;
    
    public gnu.trove.set.TShortSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public short[] keys() { return m.keys(); }
    public short[] keys(short[] array) { return m.keys(array); }
    
    public Collection<V> valueCollection() {
      if (values == null)
        values = java.util.Collections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public V[] values() { return m.values(); }
    public <T> T[] values(T[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public short getNoEntryKey() { return m.getNoEntryKey(); }
    
    public boolean forEachKey(TShortProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TObjectProcedure<V> procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TShortObjectProcedure<V> procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TShortObjectIterator<V> iterator() {
      new gnu.trove.iterator.TShortObjectIterator() {
        gnu.trove.iterator.TShortObjectIterator<V> iter = m.iterator();
        
        public short key() { return iter.key(); }
        public V value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public V setValue(V val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public V putIfAbsent(short key, V value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TObjectFunction<V, V> function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TShortObjectProcedure<V> procedure) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableCharObjectMap<V>
    implements gnu.trove.map.TCharObjectMap<V>, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final gnu.trove.map.TCharObjectMap<V> m;
    
    public TUnmodifiableCharObjectMap(gnu.trove.map.TCharObjectMap<V> m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(char key) { return m.containsKey(key); }
    public boolean containsValue(Object val) { return m.containsValue(val); }
    public V get(char key) { return m.get(key); }
    
    public V put(char key, V value) { throw new UnsupportedOperationException(); }
    public V remove(char key) { throw new UnsupportedOperationException(); }
    public void putAll(gnu.trove.map.TCharObjectMap<V> m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends Character, ? extends V> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient gnu.trove.set.TCharSet keySet = null;
    private transient Collection<V> values = null;
    
    public gnu.trove.set.TCharSet keySet() {
      if (keySet == null)
        keySet = TCollections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public char[] keys() { return m.keys(); }
    public char[] keys(char[] array) { return m.keys(array); }
    
    public Collection<V> valueCollection() {
      if (values == null)
        values = java.util.Collections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public V[] values() { return m.values(); }
    public <T> T[] values(T[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public char getNoEntryKey() { return m.getNoEntryKey(); }
    
    public boolean forEachKey(TCharProcedure procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TObjectProcedure<V> procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TCharObjectProcedure<V> procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TCharObjectIterator<V> iterator() {
      new gnu.trove.iterator.TCharObjectIterator() {
        gnu.trove.iterator.TCharObjectIterator<V> iter = m.iterator();
        
        public char key() { return iter.key(); }
        public V value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public V setValue(V val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public V putIfAbsent(char key, V value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TObjectFunction<V, V> function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TCharObjectProcedure<V> procedure) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableObjectDoubleMap<K>
    implements gnu.trove.map.TObjectDoubleMap<K>, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final gnu.trove.map.TObjectDoubleMap<K> m;
    

    public TUnmodifiableObjectDoubleMap(gnu.trove.map.TObjectDoubleMap<K> m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(Object key) { return m.containsKey(key); }
    public boolean containsValue(double val) { return m.containsValue(val); }
    public double get(Object key) { return m.get(key); }
    
    public double put(K key, double value) { throw new UnsupportedOperationException(); }
    public double remove(Object key) { throw new UnsupportedOperationException(); }
    public void putAll(gnu.trove.map.TObjectDoubleMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends K, ? extends Double> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient java.util.Set<K> keySet = null;
    private transient TDoubleCollection values = null;
    
    public java.util.Set<K> keySet() {
      if (keySet == null)
        keySet = java.util.Collections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public Object[] keys() { return m.keys(); }
    public K[] keys(K[] array) { return m.keys(array); }
    
    public TDoubleCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public double[] values() { return m.values(); }
    public double[] values(double[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public double getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TObjectProcedure<K> procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TDoubleProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TObjectDoubleProcedure<K> procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TObjectDoubleIterator<K> iterator() {
      new gnu.trove.iterator.TObjectDoubleIterator() {
        gnu.trove.iterator.TObjectDoubleIterator<K> iter = m.iterator();
        
        public K key() { return iter.key(); }
        public double value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public double setValue(double val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public double putIfAbsent(K key, double value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TDoubleFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TObjectDoubleProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(K key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(K key, double amount) { throw new UnsupportedOperationException(); }
    public double adjustOrPutValue(K key, double adjust_amount, double put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableObjectFloatMap<K>
    implements gnu.trove.map.TObjectFloatMap<K>, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final gnu.trove.map.TObjectFloatMap<K> m;
    
    public TUnmodifiableObjectFloatMap(gnu.trove.map.TObjectFloatMap<K> m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(Object key) { return m.containsKey(key); }
    public boolean containsValue(float val) { return m.containsValue(val); }
    public float get(Object key) { return m.get(key); }
    
    public float put(K key, float value) { throw new UnsupportedOperationException(); }
    public float remove(Object key) { throw new UnsupportedOperationException(); }
    public void putAll(gnu.trove.map.TObjectFloatMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends K, ? extends Float> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient java.util.Set<K> keySet = null;
    private transient TFloatCollection values = null;
    
    public java.util.Set<K> keySet() {
      if (keySet == null)
        keySet = java.util.Collections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public Object[] keys() { return m.keys(); }
    public K[] keys(K[] array) { return m.keys(array); }
    
    public TFloatCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public float[] values() { return m.values(); }
    public float[] values(float[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public float getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TObjectProcedure<K> procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TFloatProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TObjectFloatProcedure<K> procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TObjectFloatIterator<K> iterator() {
      new gnu.trove.iterator.TObjectFloatIterator() {
        gnu.trove.iterator.TObjectFloatIterator<K> iter = m.iterator();
        
        public K key() { return iter.key(); }
        public float value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public float setValue(float val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public float putIfAbsent(K key, float value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TFloatFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TObjectFloatProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(K key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(K key, float amount) { throw new UnsupportedOperationException(); }
    public float adjustOrPutValue(K key, float adjust_amount, float put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableObjectIntMap<K>
    implements gnu.trove.map.TObjectIntMap<K>, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final gnu.trove.map.TObjectIntMap<K> m;
    
    public TUnmodifiableObjectIntMap(gnu.trove.map.TObjectIntMap<K> m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(Object key) { return m.containsKey(key); }
    public boolean containsValue(int val) { return m.containsValue(val); }
    public int get(Object key) { return m.get(key); }
    
    public int put(K key, int value) { throw new UnsupportedOperationException(); }
    public int remove(Object key) { throw new UnsupportedOperationException(); }
    public void putAll(gnu.trove.map.TObjectIntMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends K, ? extends Integer> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient java.util.Set<K> keySet = null;
    private transient TIntCollection values = null;
    
    public java.util.Set<K> keySet() {
      if (keySet == null)
        keySet = java.util.Collections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public Object[] keys() { return m.keys(); }
    public K[] keys(K[] array) { return m.keys(array); }
    
    public TIntCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public int[] values() { return m.values(); }
    public int[] values(int[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public int getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TObjectProcedure<K> procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TIntProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TObjectIntProcedure<K> procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TObjectIntIterator<K> iterator() {
      new gnu.trove.iterator.TObjectIntIterator() {
        gnu.trove.iterator.TObjectIntIterator<K> iter = m.iterator();
        
        public K key() { return iter.key(); }
        public int value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public int setValue(int val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public int putIfAbsent(K key, int value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TIntFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TObjectIntProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(K key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(K key, int amount) { throw new UnsupportedOperationException(); }
    public int adjustOrPutValue(K key, int adjust_amount, int put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableObjectLongMap<K>
    implements gnu.trove.map.TObjectLongMap<K>, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final gnu.trove.map.TObjectLongMap<K> m;
    
    public TUnmodifiableObjectLongMap(gnu.trove.map.TObjectLongMap<K> m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(Object key) { return m.containsKey(key); }
    public boolean containsValue(long val) { return m.containsValue(val); }
    public long get(Object key) { return m.get(key); }
    
    public long put(K key, long value) { throw new UnsupportedOperationException(); }
    public long remove(Object key) { throw new UnsupportedOperationException(); }
    public void putAll(gnu.trove.map.TObjectLongMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends K, ? extends Long> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient java.util.Set<K> keySet = null;
    private transient TLongCollection values = null;
    
    public java.util.Set<K> keySet() {
      if (keySet == null)
        keySet = java.util.Collections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public Object[] keys() { return m.keys(); }
    public K[] keys(K[] array) { return m.keys(array); }
    
    public TLongCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public long[] values() { return m.values(); }
    public long[] values(long[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public long getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TObjectProcedure<K> procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TLongProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TObjectLongProcedure<K> procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TObjectLongIterator<K> iterator() {
      new gnu.trove.iterator.TObjectLongIterator() {
        gnu.trove.iterator.TObjectLongIterator<K> iter = m.iterator();
        
        public K key() { return iter.key(); }
        public long value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public long setValue(long val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public long putIfAbsent(K key, long value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TLongFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TObjectLongProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(K key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(K key, long amount) { throw new UnsupportedOperationException(); }
    public long adjustOrPutValue(K key, long adjust_amount, long put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableObjectByteMap<K>
    implements gnu.trove.map.TObjectByteMap<K>, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final gnu.trove.map.TObjectByteMap<K> m;
    
    public TUnmodifiableObjectByteMap(gnu.trove.map.TObjectByteMap<K> m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(Object key) { return m.containsKey(key); }
    public boolean containsValue(byte val) { return m.containsValue(val); }
    public byte get(Object key) { return m.get(key); }
    
    public byte put(K key, byte value) { throw new UnsupportedOperationException(); }
    public byte remove(Object key) { throw new UnsupportedOperationException(); }
    public void putAll(gnu.trove.map.TObjectByteMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends K, ? extends Byte> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient java.util.Set<K> keySet = null;
    private transient TByteCollection values = null;
    
    public java.util.Set<K> keySet() {
      if (keySet == null)
        keySet = java.util.Collections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public Object[] keys() { return m.keys(); }
    public K[] keys(K[] array) { return m.keys(array); }
    
    public TByteCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public byte[] values() { return m.values(); }
    public byte[] values(byte[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public byte getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TObjectProcedure<K> procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TByteProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TObjectByteProcedure<K> procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TObjectByteIterator<K> iterator() {
      new gnu.trove.iterator.TObjectByteIterator() {
        gnu.trove.iterator.TObjectByteIterator<K> iter = m.iterator();
        
        public K key() { return iter.key(); }
        public byte value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public byte setValue(byte val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public byte putIfAbsent(K key, byte value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TByteFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TObjectByteProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(K key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(K key, byte amount) { throw new UnsupportedOperationException(); }
    public byte adjustOrPutValue(K key, byte adjust_amount, byte put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableObjectShortMap<K>
    implements gnu.trove.map.TObjectShortMap<K>, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final gnu.trove.map.TObjectShortMap<K> m;
    
    public TUnmodifiableObjectShortMap(gnu.trove.map.TObjectShortMap<K> m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(Object key) { return m.containsKey(key); }
    public boolean containsValue(short val) { return m.containsValue(val); }
    public short get(Object key) { return m.get(key); }
    
    public short put(K key, short value) { throw new UnsupportedOperationException(); }
    public short remove(Object key) { throw new UnsupportedOperationException(); }
    public void putAll(gnu.trove.map.TObjectShortMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends K, ? extends Short> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient java.util.Set<K> keySet = null;
    private transient TShortCollection values = null;
    
    public java.util.Set<K> keySet() {
      if (keySet == null)
        keySet = java.util.Collections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public Object[] keys() { return m.keys(); }
    public K[] keys(K[] array) { return m.keys(array); }
    
    public TShortCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public short[] values() { return m.values(); }
    public short[] values(short[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public short getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TObjectProcedure<K> procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TShortProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TObjectShortProcedure<K> procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TObjectShortIterator<K> iterator() {
      new gnu.trove.iterator.TObjectShortIterator() {
        gnu.trove.iterator.TObjectShortIterator<K> iter = m.iterator();
        
        public K key() { return iter.key(); }
        public short value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public short setValue(short val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public short putIfAbsent(K key, short value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TShortFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TObjectShortProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(K key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(K key, short amount) { throw new UnsupportedOperationException(); }
    public short adjustOrPutValue(K key, short adjust_amount, short put_amount) { throw new UnsupportedOperationException(); }
  }
  

  public static class TUnmodifiableObjectCharMap<K>
    implements gnu.trove.map.TObjectCharMap<K>, Serializable
  {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final gnu.trove.map.TObjectCharMap<K> m;
    
    public TUnmodifiableObjectCharMap(gnu.trove.map.TObjectCharMap<K> m)
    {
      if (m == null)
        throw new NullPointerException();
      this.m = m;
    }
    
    public int size() { return m.size(); }
    public boolean isEmpty() { return m.isEmpty(); }
    public boolean containsKey(Object key) { return m.containsKey(key); }
    public boolean containsValue(char val) { return m.containsValue(val); }
    public char get(Object key) { return m.get(key); }
    
    public char put(K key, char value) { throw new UnsupportedOperationException(); }
    public char remove(Object key) { throw new UnsupportedOperationException(); }
    public void putAll(gnu.trove.map.TObjectCharMap m) { throw new UnsupportedOperationException(); }
    public void putAll(Map<? extends K, ? extends Character> map) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }
    
    private transient java.util.Set<K> keySet = null;
    private transient TCharCollection values = null;
    
    public java.util.Set<K> keySet() {
      if (keySet == null)
        keySet = java.util.Collections.unmodifiableSet(m.keySet());
      return keySet; }
    
    public Object[] keys() { return m.keys(); }
    public K[] keys(K[] array) { return m.keys(array); }
    
    public TCharCollection valueCollection() {
      if (values == null)
        values = TCollections.unmodifiableCollection(m.valueCollection());
      return values; }
    
    public char[] values() { return m.values(); }
    public char[] values(char[] array) { return m.values(array); }
    
    public boolean equals(Object o) { return (o == this) || (m.equals(o)); }
    public int hashCode() { return m.hashCode(); }
    public String toString() { return m.toString(); }
    public char getNoEntryValue() { return m.getNoEntryValue(); }
    
    public boolean forEachKey(TObjectProcedure<K> procedure) {
      return m.forEachKey(procedure);
    }
    
    public boolean forEachValue(TCharProcedure procedure) { return m.forEachValue(procedure); }
    
    public boolean forEachEntry(gnu.trove.procedure.TObjectCharProcedure<K> procedure) {
      return m.forEachEntry(procedure);
    }
    
    public gnu.trove.iterator.TObjectCharIterator<K> iterator() {
      new gnu.trove.iterator.TObjectCharIterator() {
        gnu.trove.iterator.TObjectCharIterator<K> iter = m.iterator();
        
        public K key() { return iter.key(); }
        public char value() { return iter.value(); }
        public void advance() { iter.advance(); }
        public boolean hasNext() { return iter.hasNext(); }
        public char setValue(char val) { throw new UnsupportedOperationException(); }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    
    public char putIfAbsent(K key, char value) { throw new UnsupportedOperationException(); }
    public void transformValues(gnu.trove.function.TCharFunction function) { throw new UnsupportedOperationException(); }
    public boolean retainEntries(gnu.trove.procedure.TObjectCharProcedure procedure) { throw new UnsupportedOperationException(); }
    public boolean increment(K key) { throw new UnsupportedOperationException(); }
    public boolean adjustValue(K key, char amount) { throw new UnsupportedOperationException(); }
    public char adjustOrPutValue(K key, char adjust_amount, char put_amount) { throw new UnsupportedOperationException(); }
  }
}
