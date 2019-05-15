package gnu.trove;

import gnu.trove.map.TByteByteMap;
import gnu.trove.map.TByteCharMap;
import gnu.trove.map.TByteDoubleMap;
import gnu.trove.map.TByteObjectMap;
import gnu.trove.map.TCharCharMap;
import gnu.trove.map.TCharDoubleMap;
import gnu.trove.map.TCharLongMap;
import gnu.trove.map.TCharShortMap;
import gnu.trove.map.TDoubleByteMap;
import gnu.trove.map.TDoubleCharMap;
import gnu.trove.map.TDoubleDoubleMap;
import gnu.trove.map.TDoubleIntMap;
import gnu.trove.map.TDoubleLongMap;
import gnu.trove.map.TDoubleShortMap;
import gnu.trove.map.TFloatDoubleMap;
import gnu.trove.map.TFloatIntMap;
import gnu.trove.map.TFloatLongMap;
import gnu.trove.map.TIntByteMap;
import gnu.trove.map.TIntCharMap;
import gnu.trove.map.TIntFloatMap;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TLongByteMap;
import gnu.trove.map.TLongCharMap;
import gnu.trove.map.TLongFloatMap;
import gnu.trove.map.TLongIntMap;
import gnu.trove.map.TLongLongMap;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.TLongShortMap;
import gnu.trove.map.TObjectByteMap;
import gnu.trove.map.TObjectCharMap;
import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.TObjectShortMap;
import gnu.trove.map.TShortLongMap;
import gnu.trove.map.TShortShortMap;
import gnu.trove.set.TByteSet;
import gnu.trove.set.TCharSet;
import gnu.trove.set.TDoubleSet;
import gnu.trove.set.TFloatSet;
import gnu.trove.set.TIntSet;
import gnu.trove.set.TLongSet;
import gnu.trove.set.TShortSet;
import java.util.Map;
import java.util.Set;

public class TDecorators
{
  private TDecorators() {}
  
  public static Map<Double, Double> wrap(TDoubleDoubleMap map)
  {
    return new gnu.trove.decorator.TDoubleDoubleMapDecorator(map);
  }
  






  public static Map<Double, Float> wrap(gnu.trove.map.TDoubleFloatMap map)
  {
    return new gnu.trove.decorator.TDoubleFloatMapDecorator(map);
  }
  






  public static Map<Double, Integer> wrap(TDoubleIntMap map)
  {
    return new gnu.trove.decorator.TDoubleIntMapDecorator(map);
  }
  






  public static Map<Double, Long> wrap(TDoubleLongMap map)
  {
    return new gnu.trove.decorator.TDoubleLongMapDecorator(map);
  }
  






  public static Map<Double, Byte> wrap(TDoubleByteMap map)
  {
    return new gnu.trove.decorator.TDoubleByteMapDecorator(map);
  }
  






  public static Map<Double, Short> wrap(TDoubleShortMap map)
  {
    return new gnu.trove.decorator.TDoubleShortMapDecorator(map);
  }
  






  public static Map<Double, Character> wrap(TDoubleCharMap map)
  {
    return new gnu.trove.decorator.TDoubleCharMapDecorator(map);
  }
  






  public static Map<Float, Double> wrap(TFloatDoubleMap map)
  {
    return new gnu.trove.decorator.TFloatDoubleMapDecorator(map);
  }
  






  public static Map<Float, Float> wrap(gnu.trove.map.TFloatFloatMap map)
  {
    return new gnu.trove.decorator.TFloatFloatMapDecorator(map);
  }
  






  public static Map<Float, Integer> wrap(TFloatIntMap map)
  {
    return new gnu.trove.decorator.TFloatIntMapDecorator(map);
  }
  






  public static Map<Float, Long> wrap(TFloatLongMap map)
  {
    return new gnu.trove.decorator.TFloatLongMapDecorator(map);
  }
  






  public static Map<Float, Byte> wrap(gnu.trove.map.TFloatByteMap map)
  {
    return new gnu.trove.decorator.TFloatByteMapDecorator(map);
  }
  






  public static Map<Float, Short> wrap(gnu.trove.map.TFloatShortMap map)
  {
    return new gnu.trove.decorator.TFloatShortMapDecorator(map);
  }
  






  public static Map<Float, Character> wrap(gnu.trove.map.TFloatCharMap map)
  {
    return new gnu.trove.decorator.TFloatCharMapDecorator(map);
  }
  






  public static Map<Integer, Double> wrap(gnu.trove.map.TIntDoubleMap map)
  {
    return new gnu.trove.decorator.TIntDoubleMapDecorator(map);
  }
  






  public static Map<Integer, Float> wrap(TIntFloatMap map)
  {
    return new gnu.trove.decorator.TIntFloatMapDecorator(map);
  }
  






  public static Map<Integer, Integer> wrap(TIntIntMap map)
  {
    return new gnu.trove.decorator.TIntIntMapDecorator(map);
  }
  






  public static Map<Integer, Long> wrap(gnu.trove.map.TIntLongMap map)
  {
    return new gnu.trove.decorator.TIntLongMapDecorator(map);
  }
  






  public static Map<Integer, Byte> wrap(TIntByteMap map)
  {
    return new gnu.trove.decorator.TIntByteMapDecorator(map);
  }
  






  public static Map<Integer, Short> wrap(gnu.trove.map.TIntShortMap map)
  {
    return new gnu.trove.decorator.TIntShortMapDecorator(map);
  }
  






  public static Map<Integer, Character> wrap(TIntCharMap map)
  {
    return new gnu.trove.decorator.TIntCharMapDecorator(map);
  }
  






  public static Map<Long, Double> wrap(gnu.trove.map.TLongDoubleMap map)
  {
    return new gnu.trove.decorator.TLongDoubleMapDecorator(map);
  }
  






  public static Map<Long, Float> wrap(TLongFloatMap map)
  {
    return new gnu.trove.decorator.TLongFloatMapDecorator(map);
  }
  






  public static Map<Long, Integer> wrap(TLongIntMap map)
  {
    return new gnu.trove.decorator.TLongIntMapDecorator(map);
  }
  






  public static Map<Long, Long> wrap(TLongLongMap map)
  {
    return new gnu.trove.decorator.TLongLongMapDecorator(map);
  }
  






  public static Map<Long, Byte> wrap(TLongByteMap map)
  {
    return new gnu.trove.decorator.TLongByteMapDecorator(map);
  }
  






  public static Map<Long, Short> wrap(TLongShortMap map)
  {
    return new gnu.trove.decorator.TLongShortMapDecorator(map);
  }
  






  public static Map<Long, Character> wrap(TLongCharMap map)
  {
    return new gnu.trove.decorator.TLongCharMapDecorator(map);
  }
  






  public static Map<Byte, Double> wrap(TByteDoubleMap map)
  {
    return new gnu.trove.decorator.TByteDoubleMapDecorator(map);
  }
  






  public static Map<Byte, Float> wrap(gnu.trove.map.TByteFloatMap map)
  {
    return new gnu.trove.decorator.TByteFloatMapDecorator(map);
  }
  






  public static Map<Byte, Integer> wrap(gnu.trove.map.TByteIntMap map)
  {
    return new gnu.trove.decorator.TByteIntMapDecorator(map);
  }
  






  public static Map<Byte, Long> wrap(gnu.trove.map.TByteLongMap map)
  {
    return new gnu.trove.decorator.TByteLongMapDecorator(map);
  }
  






  public static Map<Byte, Byte> wrap(TByteByteMap map)
  {
    return new gnu.trove.decorator.TByteByteMapDecorator(map);
  }
  






  public static Map<Byte, Short> wrap(gnu.trove.map.TByteShortMap map)
  {
    return new gnu.trove.decorator.TByteShortMapDecorator(map);
  }
  






  public static Map<Byte, Character> wrap(TByteCharMap map)
  {
    return new gnu.trove.decorator.TByteCharMapDecorator(map);
  }
  






  public static Map<Short, Double> wrap(gnu.trove.map.TShortDoubleMap map)
  {
    return new gnu.trove.decorator.TShortDoubleMapDecorator(map);
  }
  






  public static Map<Short, Float> wrap(gnu.trove.map.TShortFloatMap map)
  {
    return new gnu.trove.decorator.TShortFloatMapDecorator(map);
  }
  






  public static Map<Short, Integer> wrap(gnu.trove.map.TShortIntMap map)
  {
    return new gnu.trove.decorator.TShortIntMapDecorator(map);
  }
  






  public static Map<Short, Long> wrap(TShortLongMap map)
  {
    return new gnu.trove.decorator.TShortLongMapDecorator(map);
  }
  






  public static Map<Short, Byte> wrap(gnu.trove.map.TShortByteMap map)
  {
    return new gnu.trove.decorator.TShortByteMapDecorator(map);
  }
  






  public static Map<Short, Short> wrap(TShortShortMap map)
  {
    return new gnu.trove.decorator.TShortShortMapDecorator(map);
  }
  






  public static Map<Short, Character> wrap(gnu.trove.map.TShortCharMap map)
  {
    return new gnu.trove.decorator.TShortCharMapDecorator(map);
  }
  






  public static Map<Character, Double> wrap(TCharDoubleMap map)
  {
    return new gnu.trove.decorator.TCharDoubleMapDecorator(map);
  }
  






  public static Map<Character, Float> wrap(gnu.trove.map.TCharFloatMap map)
  {
    return new gnu.trove.decorator.TCharFloatMapDecorator(map);
  }
  






  public static Map<Character, Integer> wrap(gnu.trove.map.TCharIntMap map)
  {
    return new gnu.trove.decorator.TCharIntMapDecorator(map);
  }
  






  public static Map<Character, Long> wrap(TCharLongMap map)
  {
    return new gnu.trove.decorator.TCharLongMapDecorator(map);
  }
  






  public static Map<Character, Byte> wrap(gnu.trove.map.TCharByteMap map)
  {
    return new gnu.trove.decorator.TCharByteMapDecorator(map);
  }
  






  public static Map<Character, Short> wrap(TCharShortMap map)
  {
    return new gnu.trove.decorator.TCharShortMapDecorator(map);
  }
  






  public static Map<Character, Character> wrap(TCharCharMap map)
  {
    return new gnu.trove.decorator.TCharCharMapDecorator(map);
  }
  







  public static <T> Map<T, Double> wrap(gnu.trove.map.TObjectDoubleMap<T> map)
  {
    return new gnu.trove.decorator.TObjectDoubleMapDecorator(map);
  }
  






  public static <T> Map<T, Float> wrap(gnu.trove.map.TObjectFloatMap<T> map)
  {
    return new gnu.trove.decorator.TObjectFloatMapDecorator(map);
  }
  






  public static <T> Map<T, Integer> wrap(gnu.trove.map.TObjectIntMap<T> map)
  {
    return new gnu.trove.decorator.TObjectIntMapDecorator(map);
  }
  






  public static <T> Map<T, Long> wrap(TObjectLongMap<T> map)
  {
    return new gnu.trove.decorator.TObjectLongMapDecorator(map);
  }
  






  public static <T> Map<T, Byte> wrap(TObjectByteMap<T> map)
  {
    return new gnu.trove.decorator.TObjectByteMapDecorator(map);
  }
  






  public static <T> Map<T, Short> wrap(TObjectShortMap<T> map)
  {
    return new gnu.trove.decorator.TObjectShortMapDecorator(map);
  }
  






  public static <T> Map<T, Character> wrap(TObjectCharMap<T> map)
  {
    return new gnu.trove.decorator.TObjectCharMapDecorator(map);
  }
  







  public static <T> Map<Double, T> wrap(gnu.trove.map.TDoubleObjectMap<T> map)
  {
    return new gnu.trove.decorator.TDoubleObjectMapDecorator(map);
  }
  






  public static <T> Map<Float, T> wrap(gnu.trove.map.TFloatObjectMap<T> map)
  {
    return new gnu.trove.decorator.TFloatObjectMapDecorator(map);
  }
  






  public static <T> Map<Integer, T> wrap(gnu.trove.map.TIntObjectMap<T> map)
  {
    return new gnu.trove.decorator.TIntObjectMapDecorator(map);
  }
  






  public static <T> Map<Long, T> wrap(TLongObjectMap<T> map)
  {
    return new gnu.trove.decorator.TLongObjectMapDecorator(map);
  }
  






  public static <T> Map<Byte, T> wrap(TByteObjectMap<T> map)
  {
    return new gnu.trove.decorator.TByteObjectMapDecorator(map);
  }
  






  public static <T> Map<Short, T> wrap(gnu.trove.map.TShortObjectMap<T> map)
  {
    return new gnu.trove.decorator.TShortObjectMapDecorator(map);
  }
  






  public static <T> Map<Character, T> wrap(gnu.trove.map.TCharObjectMap<T> map)
  {
    return new gnu.trove.decorator.TCharObjectMapDecorator(map);
  }
  







  public static Set<Double> wrap(TDoubleSet set)
  {
    return new gnu.trove.decorator.TDoubleSetDecorator(set);
  }
  






  public static Set<Float> wrap(TFloatSet set)
  {
    return new gnu.trove.decorator.TFloatSetDecorator(set);
  }
  






  public static Set<Integer> wrap(TIntSet set)
  {
    return new gnu.trove.decorator.TIntSetDecorator(set);
  }
  






  public static Set<Long> wrap(TLongSet set)
  {
    return new gnu.trove.decorator.TLongSetDecorator(set);
  }
  






  public static Set<Byte> wrap(TByteSet set)
  {
    return new gnu.trove.decorator.TByteSetDecorator(set);
  }
  






  public static Set<Short> wrap(TShortSet set)
  {
    return new gnu.trove.decorator.TShortSetDecorator(set);
  }
  






  public static Set<Character> wrap(TCharSet set)
  {
    return new gnu.trove.decorator.TCharSetDecorator(set);
  }
}
