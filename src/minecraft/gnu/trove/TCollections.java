package gnu.trove;

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
import gnu.trove.map.TByteObjectMap;
import gnu.trove.map.TByteShortMap;
import gnu.trove.map.TCharCharMap;
import gnu.trove.map.TCharDoubleMap;
import gnu.trove.map.TCharLongMap;
import gnu.trove.map.TCharShortMap;
import gnu.trove.map.TDoubleByteMap;
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
import gnu.trove.map.TFloatIntMap;
import gnu.trove.map.TFloatLongMap;
import gnu.trove.map.TFloatObjectMap;
import gnu.trove.map.TFloatShortMap;
import gnu.trove.map.TIntByteMap;
import gnu.trove.map.TIntCharMap;
import gnu.trove.map.TIntFloatMap;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TLongByteMap;
import gnu.trove.map.TLongCharMap;
import gnu.trove.map.TLongFloatMap;
import gnu.trove.map.TLongIntMap;
import gnu.trove.map.TLongLongMap;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.TLongShortMap;
import gnu.trove.map.TObjectByteMap;
import gnu.trove.map.TObjectCharMap;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.TObjectShortMap;
import gnu.trove.map.TShortCharMap;
import gnu.trove.map.TShortDoubleMap;
import gnu.trove.map.TShortLongMap;
import gnu.trove.map.TShortShortMap;
import gnu.trove.set.TByteSet;
import gnu.trove.set.TCharSet;
import gnu.trove.set.TDoubleSet;
import gnu.trove.set.TFloatSet;
import gnu.trove.set.TIntSet;
import gnu.trove.set.TLongSet;
import gnu.trove.set.TShortSet;
import java.util.RandomAccess;

public class TCollections
{
  private TCollections() {}
  
  public static TDoubleCollection unmodifiableCollection(TDoubleCollection c)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableDoubleCollection(c);
  }
  





















  public static TFloatCollection unmodifiableCollection(TFloatCollection c)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableFloatCollection(c);
  }
  





















  public static TIntCollection unmodifiableCollection(TIntCollection c)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableIntCollection(c);
  }
  





















  public static TLongCollection unmodifiableCollection(TLongCollection c)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableLongCollection(c);
  }
  





















  public static TByteCollection unmodifiableCollection(TByteCollection c)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableByteCollection(c);
  }
  





















  public static TShortCollection unmodifiableCollection(TShortCollection c)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableShortCollection(c);
  }
  





















  public static TCharCollection unmodifiableCollection(TCharCollection c)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableCharCollection(c);
  }
  














  public static TDoubleSet unmodifiableSet(TDoubleSet s)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableDoubleSet(s);
  }
  












  public static TFloatSet unmodifiableSet(TFloatSet s)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableFloatSet(s);
  }
  












  public static TIntSet unmodifiableSet(TIntSet s)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableIntSet(s);
  }
  












  public static TLongSet unmodifiableSet(TLongSet s)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableLongSet(s);
  }
  












  public static TByteSet unmodifiableSet(TByteSet s)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableByteSet(s);
  }
  












  public static TShortSet unmodifiableSet(TShortSet s)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableShortSet(s);
  }
  












  public static TCharSet unmodifiableSet(TCharSet s)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableCharSet(s);
  }
  



































































































































































  public static TDoubleList unmodifiableList(TDoubleList list)
  {
    return (list instanceof RandomAccess) ? new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableRandomAccessDoubleList(list) : new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableDoubleList(list);
  }
  
















  public static TFloatList unmodifiableList(TFloatList list)
  {
    return (list instanceof RandomAccess) ? new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableRandomAccessFloatList(list) : new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableFloatList(list);
  }
  
















  public static TIntList unmodifiableList(TIntList list)
  {
    return (list instanceof RandomAccess) ? new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableRandomAccessIntList(list) : new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableIntList(list);
  }
  
















  public static TLongList unmodifiableList(TLongList list)
  {
    return (list instanceof RandomAccess) ? new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableRandomAccessLongList(list) : new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableLongList(list);
  }
  
















  public static TByteList unmodifiableList(TByteList list)
  {
    return (list instanceof RandomAccess) ? new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableRandomAccessByteList(list) : new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableByteList(list);
  }
  
















  public static TShortList unmodifiableList(TShortList list)
  {
    return (list instanceof RandomAccess) ? new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableRandomAccessShortList(list) : new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableShortList(list);
  }
  
















  public static TCharList unmodifiableList(TCharList list)
  {
    return (list instanceof RandomAccess) ? new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableRandomAccessCharList(list) : new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableCharList(list);
  }
  
















  public static TDoubleDoubleMap unmodifiableMap(TDoubleDoubleMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableDoubleDoubleMap(m);
  }
  













  public static TDoubleFloatMap unmodifiableMap(TDoubleFloatMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableDoubleFloatMap(m);
  }
  













  public static TDoubleIntMap unmodifiableMap(TDoubleIntMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableDoubleIntMap(m);
  }
  













  public static TDoubleLongMap unmodifiableMap(TDoubleLongMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableDoubleLongMap(m);
  }
  













  public static TDoubleByteMap unmodifiableMap(TDoubleByteMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableDoubleByteMap(m);
  }
  













  public static TDoubleShortMap unmodifiableMap(TDoubleShortMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableDoubleShortMap(m);
  }
  













  public static TDoubleCharMap unmodifiableMap(TDoubleCharMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableDoubleCharMap(m);
  }
  













  public static TFloatDoubleMap unmodifiableMap(TFloatDoubleMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableFloatDoubleMap(m);
  }
  













  public static TFloatFloatMap unmodifiableMap(TFloatFloatMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableFloatFloatMap(m);
  }
  













  public static TFloatIntMap unmodifiableMap(TFloatIntMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableFloatIntMap(m);
  }
  













  public static TFloatLongMap unmodifiableMap(TFloatLongMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableFloatLongMap(m);
  }
  













  public static TFloatByteMap unmodifiableMap(TFloatByteMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableFloatByteMap(m);
  }
  













  public static TFloatShortMap unmodifiableMap(TFloatShortMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableFloatShortMap(m);
  }
  













  public static TFloatCharMap unmodifiableMap(TFloatCharMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableFloatCharMap(m);
  }
  













  public static gnu.trove.map.TIntDoubleMap unmodifiableMap(gnu.trove.map.TIntDoubleMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableIntDoubleMap(m);
  }
  













  public static TIntFloatMap unmodifiableMap(TIntFloatMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableIntFloatMap(m);
  }
  













  public static TIntIntMap unmodifiableMap(TIntIntMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableIntIntMap(m);
  }
  













  public static gnu.trove.map.TIntLongMap unmodifiableMap(gnu.trove.map.TIntLongMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableIntLongMap(m);
  }
  













  public static TIntByteMap unmodifiableMap(TIntByteMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableIntByteMap(m);
  }
  













  public static gnu.trove.map.TIntShortMap unmodifiableMap(gnu.trove.map.TIntShortMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableIntShortMap(m);
  }
  













  public static TIntCharMap unmodifiableMap(TIntCharMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableIntCharMap(m);
  }
  













  public static gnu.trove.map.TLongDoubleMap unmodifiableMap(gnu.trove.map.TLongDoubleMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableLongDoubleMap(m);
  }
  













  public static TLongFloatMap unmodifiableMap(TLongFloatMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableLongFloatMap(m);
  }
  













  public static TLongIntMap unmodifiableMap(TLongIntMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableLongIntMap(m);
  }
  













  public static TLongLongMap unmodifiableMap(TLongLongMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableLongLongMap(m);
  }
  













  public static TLongByteMap unmodifiableMap(TLongByteMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableLongByteMap(m);
  }
  













  public static TLongShortMap unmodifiableMap(TLongShortMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableLongShortMap(m);
  }
  













  public static TLongCharMap unmodifiableMap(TLongCharMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableLongCharMap(m);
  }
  













  public static TByteDoubleMap unmodifiableMap(TByteDoubleMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableByteDoubleMap(m);
  }
  













  public static TByteFloatMap unmodifiableMap(TByteFloatMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableByteFloatMap(m);
  }
  













  public static TByteIntMap unmodifiableMap(TByteIntMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableByteIntMap(m);
  }
  













  public static gnu.trove.map.TByteLongMap unmodifiableMap(gnu.trove.map.TByteLongMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableByteLongMap(m);
  }
  













  public static TByteByteMap unmodifiableMap(TByteByteMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableByteByteMap(m);
  }
  













  public static TByteShortMap unmodifiableMap(TByteShortMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableByteShortMap(m);
  }
  













  public static TByteCharMap unmodifiableMap(TByteCharMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableByteCharMap(m);
  }
  













  public static TShortDoubleMap unmodifiableMap(TShortDoubleMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableShortDoubleMap(m);
  }
  













  public static gnu.trove.map.TShortFloatMap unmodifiableMap(gnu.trove.map.TShortFloatMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableShortFloatMap(m);
  }
  













  public static gnu.trove.map.TShortIntMap unmodifiableMap(gnu.trove.map.TShortIntMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableShortIntMap(m);
  }
  













  public static TShortLongMap unmodifiableMap(TShortLongMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableShortLongMap(m);
  }
  













  public static gnu.trove.map.TShortByteMap unmodifiableMap(gnu.trove.map.TShortByteMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableShortByteMap(m);
  }
  













  public static TShortShortMap unmodifiableMap(TShortShortMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableShortShortMap(m);
  }
  













  public static TShortCharMap unmodifiableMap(TShortCharMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableShortCharMap(m);
  }
  













  public static TCharDoubleMap unmodifiableMap(TCharDoubleMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableCharDoubleMap(m);
  }
  













  public static gnu.trove.map.TCharFloatMap unmodifiableMap(gnu.trove.map.TCharFloatMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableCharFloatMap(m);
  }
  













  public static gnu.trove.map.TCharIntMap unmodifiableMap(gnu.trove.map.TCharIntMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableCharIntMap(m);
  }
  













  public static TCharLongMap unmodifiableMap(TCharLongMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableCharLongMap(m);
  }
  













  public static gnu.trove.map.TCharByteMap unmodifiableMap(gnu.trove.map.TCharByteMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableCharByteMap(m);
  }
  













  public static TCharShortMap unmodifiableMap(TCharShortMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableCharShortMap(m);
  }
  













  public static TCharCharMap unmodifiableMap(TCharCharMap m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableCharCharMap(m);
  }
  














  public static <V> gnu.trove.map.TDoubleObjectMap<V> unmodifiableMap(gnu.trove.map.TDoubleObjectMap<V> m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableDoubleObjectMap(m);
  }
  













  public static <V> TFloatObjectMap<V> unmodifiableMap(TFloatObjectMap<V> m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableFloatObjectMap(m);
  }
  













  public static <V> TIntObjectMap<V> unmodifiableMap(TIntObjectMap<V> m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableIntObjectMap(m);
  }
  













  public static <V> TLongObjectMap<V> unmodifiableMap(TLongObjectMap<V> m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableLongObjectMap(m);
  }
  













  public static <V> TByteObjectMap<V> unmodifiableMap(TByteObjectMap<V> m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableByteObjectMap(m);
  }
  













  public static <V> gnu.trove.map.TShortObjectMap<V> unmodifiableMap(gnu.trove.map.TShortObjectMap<V> m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableShortObjectMap(m);
  }
  













  public static <V> gnu.trove.map.TCharObjectMap<V> unmodifiableMap(gnu.trove.map.TCharObjectMap<V> m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableCharObjectMap(m);
  }
  














  public static <K> TObjectDoubleMap<K> unmodifiableMap(TObjectDoubleMap<K> m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableObjectDoubleMap(m);
  }
  













  public static <K> gnu.trove.map.TObjectFloatMap<K> unmodifiableMap(gnu.trove.map.TObjectFloatMap<K> m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableObjectFloatMap(m);
  }
  













  public static <K> gnu.trove.map.TObjectIntMap<K> unmodifiableMap(gnu.trove.map.TObjectIntMap<K> m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableObjectIntMap(m);
  }
  













  public static <K> TObjectLongMap<K> unmodifiableMap(TObjectLongMap<K> m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableObjectLongMap(m);
  }
  













  public static <K> TObjectByteMap<K> unmodifiableMap(TObjectByteMap<K> m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableObjectByteMap(m);
  }
  













  public static <K> TObjectShortMap<K> unmodifiableMap(TObjectShortMap<K> m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableObjectShortMap(m);
  }
  













  public static <K> TObjectCharMap<K> unmodifiableMap(TObjectCharMap<K> m)
  {
    return new gnu.trove.impl.TUnmodifiableCollections.TUnmodifiableObjectCharMap(m);
  }
  



































  public static TDoubleCollection synchronizedCollection(TDoubleCollection c)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedDoubleCollection(c);
  }
  
  static TDoubleCollection synchronizedCollection(TDoubleCollection c, Object mutex) {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedDoubleCollection(c, mutex);
  }
  






























  public static TFloatCollection synchronizedCollection(TFloatCollection c)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedFloatCollection(c);
  }
  
  static TFloatCollection synchronizedCollection(TFloatCollection c, Object mutex) {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedFloatCollection(c, mutex);
  }
  






























  public static TIntCollection synchronizedCollection(TIntCollection c)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedIntCollection(c);
  }
  
  static TIntCollection synchronizedCollection(TIntCollection c, Object mutex) {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedIntCollection(c, mutex);
  }
  






























  public static TLongCollection synchronizedCollection(TLongCollection c)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedLongCollection(c);
  }
  
  static TLongCollection synchronizedCollection(TLongCollection c, Object mutex) {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedLongCollection(c, mutex);
  }
  






























  public static TByteCollection synchronizedCollection(TByteCollection c)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedByteCollection(c);
  }
  
  static TByteCollection synchronizedCollection(TByteCollection c, Object mutex) {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedByteCollection(c, mutex);
  }
  






























  public static TShortCollection synchronizedCollection(TShortCollection c)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedShortCollection(c);
  }
  
  static TShortCollection synchronizedCollection(TShortCollection c, Object mutex) {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedShortCollection(c, mutex);
  }
  






























  public static TCharCollection synchronizedCollection(TCharCollection c)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedCharCollection(c);
  }
  
  static TCharCollection synchronizedCollection(TCharCollection c, Object mutex) {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedCharCollection(c, mutex);
  }
  

























  public static TDoubleSet synchronizedSet(TDoubleSet s)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedDoubleSet(s);
  }
  
  static TDoubleSet synchronizedSet(TDoubleSet s, Object mutex) {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedDoubleSet(s, mutex);
  }
  
























  public static TFloatSet synchronizedSet(TFloatSet s)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedFloatSet(s);
  }
  
  static TFloatSet synchronizedSet(TFloatSet s, Object mutex) {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedFloatSet(s, mutex);
  }
  
























  public static TIntSet synchronizedSet(TIntSet s)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedIntSet(s);
  }
  
  static TIntSet synchronizedSet(TIntSet s, Object mutex) {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedIntSet(s, mutex);
  }
  
























  public static TLongSet synchronizedSet(TLongSet s)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedLongSet(s);
  }
  
  static TLongSet synchronizedSet(TLongSet s, Object mutex) {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedLongSet(s, mutex);
  }
  
























  public static TByteSet synchronizedSet(TByteSet s)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedByteSet(s);
  }
  
  static TByteSet synchronizedSet(TByteSet s, Object mutex) {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedByteSet(s, mutex);
  }
  
























  public static TShortSet synchronizedSet(TShortSet s)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedShortSet(s);
  }
  
  static TShortSet synchronizedSet(TShortSet s, Object mutex) {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedShortSet(s, mutex);
  }
  
























  public static TCharSet synchronizedSet(TCharSet s)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedCharSet(s);
  }
  
  static TCharSet synchronizedSet(TCharSet s, Object mutex) {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedCharSet(s, mutex);
  }
  
































































































































































































































































































































  public static TDoubleList synchronizedList(TDoubleList list)
  {
    return (list instanceof RandomAccess) ? new gnu.trove.impl.TSynchronizedCollections.TSynchronizedRandomAccessDoubleList(list) : new gnu.trove.impl.TSynchronizedCollections.TSynchronizedDoubleList(list);
  }
  

  static TDoubleList synchronizedList(TDoubleList list, Object mutex)
  {
    return (list instanceof RandomAccess) ? new gnu.trove.impl.TSynchronizedCollections.TSynchronizedRandomAccessDoubleList(list, mutex) : new gnu.trove.impl.TSynchronizedCollections.TSynchronizedDoubleList(list, mutex);
  }
  


























  public static TFloatList synchronizedList(TFloatList list)
  {
    return (list instanceof RandomAccess) ? new gnu.trove.impl.TSynchronizedCollections.TSynchronizedRandomAccessFloatList(list) : new gnu.trove.impl.TSynchronizedCollections.TSynchronizedFloatList(list);
  }
  

  static TFloatList synchronizedList(TFloatList list, Object mutex)
  {
    return (list instanceof RandomAccess) ? new gnu.trove.impl.TSynchronizedCollections.TSynchronizedRandomAccessFloatList(list, mutex) : new gnu.trove.impl.TSynchronizedCollections.TSynchronizedFloatList(list, mutex);
  }
  


























  public static TIntList synchronizedList(TIntList list)
  {
    return (list instanceof RandomAccess) ? new gnu.trove.impl.TSynchronizedCollections.TSynchronizedRandomAccessIntList(list) : new gnu.trove.impl.TSynchronizedCollections.TSynchronizedIntList(list);
  }
  

  static TIntList synchronizedList(TIntList list, Object mutex)
  {
    return (list instanceof RandomAccess) ? new gnu.trove.impl.TSynchronizedCollections.TSynchronizedRandomAccessIntList(list, mutex) : new gnu.trove.impl.TSynchronizedCollections.TSynchronizedIntList(list, mutex);
  }
  


























  public static TLongList synchronizedList(TLongList list)
  {
    return (list instanceof RandomAccess) ? new gnu.trove.impl.TSynchronizedCollections.TSynchronizedRandomAccessLongList(list) : new gnu.trove.impl.TSynchronizedCollections.TSynchronizedLongList(list);
  }
  

  static TLongList synchronizedList(TLongList list, Object mutex)
  {
    return (list instanceof RandomAccess) ? new gnu.trove.impl.TSynchronizedCollections.TSynchronizedRandomAccessLongList(list, mutex) : new gnu.trove.impl.TSynchronizedCollections.TSynchronizedLongList(list, mutex);
  }
  


























  public static TByteList synchronizedList(TByteList list)
  {
    return (list instanceof RandomAccess) ? new gnu.trove.impl.TSynchronizedCollections.TSynchronizedRandomAccessByteList(list) : new gnu.trove.impl.TSynchronizedCollections.TSynchronizedByteList(list);
  }
  

  static TByteList synchronizedList(TByteList list, Object mutex)
  {
    return (list instanceof RandomAccess) ? new gnu.trove.impl.TSynchronizedCollections.TSynchronizedRandomAccessByteList(list, mutex) : new gnu.trove.impl.TSynchronizedCollections.TSynchronizedByteList(list, mutex);
  }
  


























  public static TShortList synchronizedList(TShortList list)
  {
    return (list instanceof RandomAccess) ? new gnu.trove.impl.TSynchronizedCollections.TSynchronizedRandomAccessShortList(list) : new gnu.trove.impl.TSynchronizedCollections.TSynchronizedShortList(list);
  }
  

  static TShortList synchronizedList(TShortList list, Object mutex)
  {
    return (list instanceof RandomAccess) ? new gnu.trove.impl.TSynchronizedCollections.TSynchronizedRandomAccessShortList(list, mutex) : new gnu.trove.impl.TSynchronizedCollections.TSynchronizedShortList(list, mutex);
  }
  


























  public static TCharList synchronizedList(TCharList list)
  {
    return (list instanceof RandomAccess) ? new gnu.trove.impl.TSynchronizedCollections.TSynchronizedRandomAccessCharList(list) : new gnu.trove.impl.TSynchronizedCollections.TSynchronizedCharList(list);
  }
  

  static TCharList synchronizedList(TCharList list, Object mutex)
  {
    return (list instanceof RandomAccess) ? new gnu.trove.impl.TSynchronizedCollections.TSynchronizedRandomAccessCharList(list, mutex) : new gnu.trove.impl.TSynchronizedCollections.TSynchronizedCharList(list, mutex);
  }
  





























  public static TDoubleDoubleMap synchronizedMap(TDoubleDoubleMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedDoubleDoubleMap(m);
  }
  


























  public static TDoubleFloatMap synchronizedMap(TDoubleFloatMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedDoubleFloatMap(m);
  }
  


























  public static TDoubleIntMap synchronizedMap(TDoubleIntMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedDoubleIntMap(m);
  }
  


























  public static TDoubleLongMap synchronizedMap(TDoubleLongMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedDoubleLongMap(m);
  }
  


























  public static TDoubleByteMap synchronizedMap(TDoubleByteMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedDoubleByteMap(m);
  }
  


























  public static TDoubleShortMap synchronizedMap(TDoubleShortMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedDoubleShortMap(m);
  }
  


























  public static TDoubleCharMap synchronizedMap(TDoubleCharMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedDoubleCharMap(m);
  }
  


























  public static TFloatDoubleMap synchronizedMap(TFloatDoubleMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedFloatDoubleMap(m);
  }
  


























  public static TFloatFloatMap synchronizedMap(TFloatFloatMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedFloatFloatMap(m);
  }
  


























  public static TFloatIntMap synchronizedMap(TFloatIntMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedFloatIntMap(m);
  }
  


























  public static TFloatLongMap synchronizedMap(TFloatLongMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedFloatLongMap(m);
  }
  


























  public static TFloatByteMap synchronizedMap(TFloatByteMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedFloatByteMap(m);
  }
  


























  public static TFloatShortMap synchronizedMap(TFloatShortMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedFloatShortMap(m);
  }
  


























  public static TFloatCharMap synchronizedMap(TFloatCharMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedFloatCharMap(m);
  }
  


























  public static gnu.trove.map.TIntDoubleMap synchronizedMap(gnu.trove.map.TIntDoubleMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedIntDoubleMap(m);
  }
  


























  public static TIntFloatMap synchronizedMap(TIntFloatMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedIntFloatMap(m);
  }
  


























  public static TIntIntMap synchronizedMap(TIntIntMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedIntIntMap(m);
  }
  


























  public static gnu.trove.map.TIntLongMap synchronizedMap(gnu.trove.map.TIntLongMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedIntLongMap(m);
  }
  


























  public static TIntByteMap synchronizedMap(TIntByteMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedIntByteMap(m);
  }
  


























  public static gnu.trove.map.TIntShortMap synchronizedMap(gnu.trove.map.TIntShortMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedIntShortMap(m);
  }
  


























  public static TIntCharMap synchronizedMap(TIntCharMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedIntCharMap(m);
  }
  


























  public static gnu.trove.map.TLongDoubleMap synchronizedMap(gnu.trove.map.TLongDoubleMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedLongDoubleMap(m);
  }
  


























  public static TLongFloatMap synchronizedMap(TLongFloatMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedLongFloatMap(m);
  }
  


























  public static TLongIntMap synchronizedMap(TLongIntMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedLongIntMap(m);
  }
  


























  public static TLongLongMap synchronizedMap(TLongLongMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedLongLongMap(m);
  }
  


























  public static TLongByteMap synchronizedMap(TLongByteMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedLongByteMap(m);
  }
  


























  public static TLongShortMap synchronizedMap(TLongShortMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedLongShortMap(m);
  }
  


























  public static TLongCharMap synchronizedMap(TLongCharMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedLongCharMap(m);
  }
  


























  public static TByteDoubleMap synchronizedMap(TByteDoubleMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedByteDoubleMap(m);
  }
  


























  public static TByteFloatMap synchronizedMap(TByteFloatMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedByteFloatMap(m);
  }
  


























  public static TByteIntMap synchronizedMap(TByteIntMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedByteIntMap(m);
  }
  


























  public static gnu.trove.map.TByteLongMap synchronizedMap(gnu.trove.map.TByteLongMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedByteLongMap(m);
  }
  


























  public static TByteByteMap synchronizedMap(TByteByteMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedByteByteMap(m);
  }
  


























  public static TByteShortMap synchronizedMap(TByteShortMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedByteShortMap(m);
  }
  


























  public static TByteCharMap synchronizedMap(TByteCharMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedByteCharMap(m);
  }
  


























  public static TShortDoubleMap synchronizedMap(TShortDoubleMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedShortDoubleMap(m);
  }
  


























  public static gnu.trove.map.TShortFloatMap synchronizedMap(gnu.trove.map.TShortFloatMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedShortFloatMap(m);
  }
  


























  public static gnu.trove.map.TShortIntMap synchronizedMap(gnu.trove.map.TShortIntMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedShortIntMap(m);
  }
  


























  public static TShortLongMap synchronizedMap(TShortLongMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedShortLongMap(m);
  }
  


























  public static gnu.trove.map.TShortByteMap synchronizedMap(gnu.trove.map.TShortByteMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedShortByteMap(m);
  }
  


























  public static TShortShortMap synchronizedMap(TShortShortMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedShortShortMap(m);
  }
  


























  public static TShortCharMap synchronizedMap(TShortCharMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedShortCharMap(m);
  }
  


























  public static TCharDoubleMap synchronizedMap(TCharDoubleMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedCharDoubleMap(m);
  }
  


























  public static gnu.trove.map.TCharFloatMap synchronizedMap(gnu.trove.map.TCharFloatMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedCharFloatMap(m);
  }
  


























  public static gnu.trove.map.TCharIntMap synchronizedMap(gnu.trove.map.TCharIntMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedCharIntMap(m);
  }
  


























  public static TCharLongMap synchronizedMap(TCharLongMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedCharLongMap(m);
  }
  


























  public static gnu.trove.map.TCharByteMap synchronizedMap(gnu.trove.map.TCharByteMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedCharByteMap(m);
  }
  


























  public static TCharShortMap synchronizedMap(TCharShortMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedCharShortMap(m);
  }
  


























  public static TCharCharMap synchronizedMap(TCharCharMap m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedCharCharMap(m);
  }
  



























  public static <V> gnu.trove.map.TDoubleObjectMap<V> synchronizedMap(gnu.trove.map.TDoubleObjectMap<V> m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedDoubleObjectMap(m);
  }
  


























  public static <V> TFloatObjectMap<V> synchronizedMap(TFloatObjectMap<V> m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedFloatObjectMap(m);
  }
  


























  public static <V> TIntObjectMap<V> synchronizedMap(TIntObjectMap<V> m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedIntObjectMap(m);
  }
  


























  public static <V> TLongObjectMap<V> synchronizedMap(TLongObjectMap<V> m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedLongObjectMap(m);
  }
  


























  public static <V> TByteObjectMap<V> synchronizedMap(TByteObjectMap<V> m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedByteObjectMap(m);
  }
  


























  public static <V> gnu.trove.map.TShortObjectMap<V> synchronizedMap(gnu.trove.map.TShortObjectMap<V> m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedShortObjectMap(m);
  }
  


























  public static <V> gnu.trove.map.TCharObjectMap<V> synchronizedMap(gnu.trove.map.TCharObjectMap<V> m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedCharObjectMap(m);
  }
  



























  public static <K> TObjectDoubleMap<K> synchronizedMap(TObjectDoubleMap<K> m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedObjectDoubleMap(m);
  }
  


























  public static <K> gnu.trove.map.TObjectFloatMap<K> synchronizedMap(gnu.trove.map.TObjectFloatMap<K> m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedObjectFloatMap(m);
  }
  


























  public static <K> gnu.trove.map.TObjectIntMap<K> synchronizedMap(gnu.trove.map.TObjectIntMap<K> m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedObjectIntMap(m);
  }
  


























  public static <K> TObjectLongMap<K> synchronizedMap(TObjectLongMap<K> m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedObjectLongMap(m);
  }
  


























  public static <K> TObjectByteMap<K> synchronizedMap(TObjectByteMap<K> m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedObjectByteMap(m);
  }
  


























  public static <K> TObjectShortMap<K> synchronizedMap(TObjectShortMap<K> m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedObjectShortMap(m);
  }
  


























  public static <K> TObjectCharMap<K> synchronizedMap(TObjectCharMap<K> m)
  {
    return new gnu.trove.impl.TSynchronizedCollections.TSynchronizedObjectCharMap(m);
  }
}
