package io.netty.handler.codec.redis;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.collection.LongObjectMap;
import java.util.HashMap;
import java.util.Map;





















public final class FixedRedisMessagePool
  implements RedisMessagePool
{
  private static final String[] DEFAULT_SIMPLE_STRINGS = { "OK", "PONG", "QUEUED" };
  




  private static final String[] DEFAULT_ERRORS = { "ERR", "ERR index out of range", "ERR no such key", "ERR source and destination objects are the same", "ERR syntax error", "BUSY Redis is busy running a script. You can only call SCRIPT KILL or SHUTDOWN NOSAVE.", "BUSYKEY Target key name already exists.", "EXECABORT Transaction discarded because of previous errors.", "LOADING Redis is loading the dataset in memory", "MASTERDOWN Link with MASTER is down and slave-serve-stale-data is set to 'no'.", "MISCONF Redis is configured to save RDB snapshots, but is currently not able to persist on disk. Commands that may modify the data set are disabled. Please check Redis logs for details about the error.", "NOAUTH Authentication required.", "NOREPLICAS Not enough good slaves to write.", "NOSCRIPT No matching script. Please use EVAL.", "OOM command not allowed when used memory > 'maxmemory'.", "READONLY You can't write against a read only slave.", "WRONGTYPE Operation against a key holding the wrong kind of value" };
  






  private static final long MIN_CACHED_INTEGER_NUMBER = -1L;
  






  private static final long MAX_CACHED_INTEGER_NUMBER = 128L;
  






  private static final int SIZE_CACHED_INTEGER_NUMBER = 129;
  





  public static final FixedRedisMessagePool INSTANCE = new FixedRedisMessagePool();
  
  private Map<ByteBuf, SimpleStringRedisMessage> byteBufToSimpleStrings;
  
  private Map<String, SimpleStringRedisMessage> stringToSimpleStrings;
  
  private Map<ByteBuf, ErrorRedisMessage> byteBufToErrors;
  
  private Map<String, ErrorRedisMessage> stringToErrors;
  private Map<ByteBuf, IntegerRedisMessage> byteBufToIntegers;
  private LongObjectMap<IntegerRedisMessage> longToIntegers;
  private LongObjectMap<byte[]> longToByteBufs;
  
  private FixedRedisMessagePool()
  {
    byteBufToSimpleStrings = new HashMap(DEFAULT_SIMPLE_STRINGS.length, 1.0F);
    stringToSimpleStrings = new HashMap(DEFAULT_SIMPLE_STRINGS.length, 1.0F);
    for (String message : DEFAULT_SIMPLE_STRINGS) {
      ByteBuf key = Unpooled.unmodifiableBuffer(Unpooled.unreleasableBuffer(Unpooled.wrappedBuffer(message.getBytes(CharsetUtil.UTF_8))));
      
      SimpleStringRedisMessage cached = new SimpleStringRedisMessage(message);
      byteBufToSimpleStrings.put(key, cached);
      stringToSimpleStrings.put(message, cached);
    }
    
    byteBufToErrors = new HashMap(DEFAULT_ERRORS.length, 1.0F);
    stringToErrors = new HashMap(DEFAULT_ERRORS.length, 1.0F);
    for (String message : DEFAULT_ERRORS) {
      ByteBuf key = Unpooled.unmodifiableBuffer(Unpooled.unreleasableBuffer(Unpooled.wrappedBuffer(message.getBytes(CharsetUtil.UTF_8))));
      
      ErrorRedisMessage cached = new ErrorRedisMessage(message);
      byteBufToErrors.put(key, cached);
      stringToErrors.put(message, cached);
    }
    
    byteBufToIntegers = new HashMap(129, 1.0F);
    longToIntegers = new LongObjectHashMap(129, 1.0F);
    longToByteBufs = new LongObjectHashMap(129, 1.0F);
    for (long value = -1L; value < 128L; value += 1L) {
      byte[] keyBytes = RedisCodecUtil.longToAsciiBytes(value);
      ByteBuf keyByteBuf = Unpooled.unmodifiableBuffer(Unpooled.unreleasableBuffer(Unpooled.wrappedBuffer(keyBytes)));
      
      IntegerRedisMessage cached = new IntegerRedisMessage(value);
      byteBufToIntegers.put(keyByteBuf, cached);
      longToIntegers.put(value, cached);
      longToByteBufs.put(value, keyBytes);
    }
  }
  
  public SimpleStringRedisMessage getSimpleString(String content)
  {
    return (SimpleStringRedisMessage)stringToSimpleStrings.get(content);
  }
  
  public SimpleStringRedisMessage getSimpleString(ByteBuf content)
  {
    return (SimpleStringRedisMessage)byteBufToSimpleStrings.get(content);
  }
  
  public ErrorRedisMessage getError(String content)
  {
    return (ErrorRedisMessage)stringToErrors.get(content);
  }
  
  public ErrorRedisMessage getError(ByteBuf content)
  {
    return (ErrorRedisMessage)byteBufToErrors.get(content);
  }
  
  public IntegerRedisMessage getInteger(long value)
  {
    return (IntegerRedisMessage)longToIntegers.get(value);
  }
  
  public IntegerRedisMessage getInteger(ByteBuf content)
  {
    return (IntegerRedisMessage)byteBufToIntegers.get(content);
  }
  
  public byte[] getByteBufOfInteger(long value)
  {
    return (byte[])longToByteBufs.get(value);
  }
}
