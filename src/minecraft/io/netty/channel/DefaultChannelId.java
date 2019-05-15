package io.netty.channel;

import io.netty.buffer.ByteBufUtil;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.MacAddressUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.ThreadLocalRandom;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


























public final class DefaultChannelId
  implements ChannelId
{
  private static final long serialVersionUID = 3884076183504074063L;
  private static final InternalLogger logger;
  private static final Pattern MACHINE_ID_PATTERN;
  private static final int MACHINE_ID_LEN = 8;
  private static final byte[] MACHINE_ID;
  private static final int PROCESS_ID_LEN = 4;
  private static final int MAX_PROCESS_ID = 4194304;
  private static final int PROCESS_ID;
  private static final int SEQUENCE_LEN = 4;
  private static final int TIMESTAMP_LEN = 8;
  private static final int RANDOM_LEN = 4;
  private static final AtomicInteger nextSequence;
  
  public static DefaultChannelId newInstance()
  {
    DefaultChannelId id = new DefaultChannelId();
    id.init();
    return id;
  }
  
  static
  {
    logger = InternalLoggerFactory.getInstance(DefaultChannelId.class);
    
    MACHINE_ID_PATTERN = Pattern.compile("^(?:[0-9a-fA-F][:-]?){6,8}$");
    










    nextSequence = new AtomicInteger();
    










    int processId = -1;
    String customProcessId = SystemPropertyUtil.get("io.netty.processId");
    if (customProcessId != null) {
      try {
        processId = Integer.parseInt(customProcessId);
      }
      catch (NumberFormatException localNumberFormatException) {}
      

      if ((processId < 0) || (processId > 4194304)) {
        processId = -1;
        logger.warn("-Dio.netty.processId: {} (malformed)", customProcessId);
      } else if (logger.isDebugEnabled()) {
        logger.debug("-Dio.netty.processId: {} (user-set)", Integer.valueOf(processId));
      }
    }
    
    if (processId < 0) {
      processId = defaultProcessId();
      if (logger.isDebugEnabled()) {
        logger.debug("-Dio.netty.processId: {} (auto-detected)", Integer.valueOf(processId));
      }
    }
    
    PROCESS_ID = processId;
    
    byte[] machineId = null;
    String customMachineId = SystemPropertyUtil.get("io.netty.machineId");
    if (customMachineId != null) {
      if (MACHINE_ID_PATTERN.matcher(customMachineId).matches()) {
        machineId = parseMachineId(customMachineId);
        logger.debug("-Dio.netty.machineId: {} (user-set)", customMachineId);
      } else {
        logger.warn("-Dio.netty.machineId: {} (malformed)", customMachineId);
      }
    }
    
    if (machineId == null) {
      machineId = defaultMachineId();
      if (logger.isDebugEnabled()) {
        logger.debug("-Dio.netty.machineId: {} (auto-detected)", MacAddressUtil.formatAddress(machineId));
      }
    }
    
    MACHINE_ID = machineId;
  }
  

  private static byte[] parseMachineId(String value)
  {
    value = value.replaceAll("[:-]", "");
    
    byte[] machineId = new byte[8];
    for (int i = 0; i < value.length(); i += 2) {
      machineId[i] = ((byte)Integer.parseInt(value.substring(i, i + 2), 16));
    }
    
    return machineId;
  }
  
  private static byte[] defaultMachineId() {
    byte[] bestMacAddr = MacAddressUtil.bestAvailableMac();
    if (bestMacAddr == null) {
      bestMacAddr = new byte[8];
      ThreadLocalRandom.current().nextBytes(bestMacAddr);
      logger.warn("Failed to find a usable hardware address from the network interfaces; using random bytes: {}", MacAddressUtil.formatAddress(bestMacAddr));
    }
    

    return bestMacAddr;
  }
  
  private static int defaultProcessId() {
    ClassLoader loader = PlatformDependent.getClassLoader(DefaultChannelId.class);
    String value;
    try
    {
      Class<?> mgmtFactoryType = Class.forName("java.lang.management.ManagementFactory", true, loader);
      Class<?> runtimeMxBeanType = Class.forName("java.lang.management.RuntimeMXBean", true, loader);
      
      Method getRuntimeMXBean = mgmtFactoryType.getMethod("getRuntimeMXBean", EmptyArrays.EMPTY_CLASSES);
      Object bean = getRuntimeMXBean.invoke(null, EmptyArrays.EMPTY_OBJECTS);
      Method getName = runtimeMxBeanType.getMethod("getName", EmptyArrays.EMPTY_CLASSES);
      value = (String)getName.invoke(bean, EmptyArrays.EMPTY_OBJECTS);
    } catch (Exception e) { String value;
      logger.debug("Could not invoke ManagementFactory.getRuntimeMXBean().getName(); Android?", e);
      try
      {
        Class<?> processType = Class.forName("android.os.Process", true, loader);
        Method myPid = processType.getMethod("myPid", EmptyArrays.EMPTY_CLASSES);
        value = myPid.invoke(null, EmptyArrays.EMPTY_OBJECTS).toString();
      } catch (Exception e2) { String value;
        logger.debug("Could not invoke Process.myPid(); not Android?", e2);
        value = "";
      }
    }
    
    int atIndex = value.indexOf('@');
    if (atIndex >= 0) {
      value = value.substring(0, atIndex);
    }
    int pid;
    try
    {
      pid = Integer.parseInt(value);
    } catch (NumberFormatException e) {
      int pid;
      pid = -1;
    }
    
    if ((pid < 0) || (pid > 4194304)) {
      pid = ThreadLocalRandom.current().nextInt(4194305);
      logger.warn("Failed to find the current process ID from '{}'; using a random value: {}", value, Integer.valueOf(pid));
    }
    
    return pid;
  }
  
  private final byte[] data = new byte[28];
  
  private int hashCode;
  
  private transient String shortValue;
  private transient String longValue;
  
  private void init()
  {
    int i = 0;
    

    System.arraycopy(MACHINE_ID, 0, data, i, 8);
    i += 8;
    

    i = writeInt(i, PROCESS_ID);
    

    i = writeInt(i, nextSequence.getAndIncrement());
    

    i = writeLong(i, Long.reverse(System.nanoTime()) ^ System.currentTimeMillis());
    

    int random = ThreadLocalRandom.current().nextInt();
    hashCode = random;
    i = writeInt(i, random);
    
    assert (i == data.length);
  }
  
  private int writeInt(int i, int value) {
    data[(i++)] = ((byte)(value >>> 24));
    data[(i++)] = ((byte)(value >>> 16));
    data[(i++)] = ((byte)(value >>> 8));
    data[(i++)] = ((byte)value);
    return i;
  }
  
  private int writeLong(int i, long value) {
    data[(i++)] = ((byte)(int)(value >>> 56));
    data[(i++)] = ((byte)(int)(value >>> 48));
    data[(i++)] = ((byte)(int)(value >>> 40));
    data[(i++)] = ((byte)(int)(value >>> 32));
    data[(i++)] = ((byte)(int)(value >>> 24));
    data[(i++)] = ((byte)(int)(value >>> 16));
    data[(i++)] = ((byte)(int)(value >>> 8));
    data[(i++)] = ((byte)(int)value);
    return i;
  }
  
  public String asShortText()
  {
    String shortValue = this.shortValue;
    if (shortValue == null) {
      this.shortValue = (shortValue = ByteBufUtil.hexDump(data, 24, 4));
    }
    
    return shortValue;
  }
  
  public String asLongText()
  {
    String longValue = this.longValue;
    if (longValue == null) {
      this.longValue = (longValue = newLongValue());
    }
    return longValue;
  }
  
  private String newLongValue() {
    StringBuilder buf = new StringBuilder(2 * data.length + 5);
    int i = 0;
    i = appendHexDumpField(buf, i, 8);
    i = appendHexDumpField(buf, i, 4);
    i = appendHexDumpField(buf, i, 4);
    i = appendHexDumpField(buf, i, 8);
    i = appendHexDumpField(buf, i, 4);
    assert (i == data.length);
    return buf.substring(0, buf.length() - 1);
  }
  
  private int appendHexDumpField(StringBuilder buf, int i, int length) {
    buf.append(ByteBufUtil.hexDump(data, i, length));
    buf.append('-');
    i += length;
    return i;
  }
  
  public int hashCode()
  {
    return hashCode;
  }
  
  public int compareTo(ChannelId o)
  {
    return 0;
  }
  
  public boolean equals(Object obj)
  {
    if (obj == this) {
      return true;
    }
    
    if (!(obj instanceof DefaultChannelId)) {
      return false;
    }
    
    return Arrays.equals(data, data);
  }
  
  public String toString()
  {
    return asShortText();
  }
  
  private DefaultChannelId() {}
}
