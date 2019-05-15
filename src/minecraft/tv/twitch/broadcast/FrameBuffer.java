package tv.twitch.broadcast;

import java.util.HashMap;
import java.util.Map;


public class FrameBuffer
{
  private static Map<Long, FrameBuffer> s_OutstandingBuffers = new HashMap();
  
  public static FrameBuffer lookupBuffer(long paramLong)
  {
    return (FrameBuffer)s_OutstandingBuffers.get(Long.valueOf(paramLong));
  }
  
  protected static void registerBuffer(FrameBuffer paramFrameBuffer)
  {
    if (paramFrameBuffer.getAddress() != 0L)
    {
      s_OutstandingBuffers.put(Long.valueOf(paramFrameBuffer.getAddress()), paramFrameBuffer);
    }
  }
  
  protected static void unregisterBuffer(FrameBuffer paramFrameBuffer)
  {
    s_OutstandingBuffers.remove(Long.valueOf(paramFrameBuffer.getAddress()));
  }
  
  protected long m_NativeAddress = 0L;
  protected int m_Size = 0;
  protected StreamAPI m_API = null;
  
  FrameBuffer(StreamAPI paramStreamAPI, int paramInt)
  {
    m_NativeAddress = paramStreamAPI.allocateFrameBuffer(paramInt);
    
    if (m_NativeAddress == 0L)
    {
      return;
    }
    
    m_API = paramStreamAPI;
    m_Size = paramInt;
    
    registerBuffer(this);
  }
  
  public boolean getIsValid()
  {
    return m_NativeAddress != 0L;
  }
  
  public int getSize()
  {
    return m_Size;
  }
  
  public long getAddress()
  {
    return m_NativeAddress;
  }
  
  public void free()
  {
    if (m_NativeAddress != 0L)
    {
      unregisterBuffer(this);
      
      m_API.freeFrameBuffer(m_NativeAddress);
      m_NativeAddress = 0L;
    }
  }
  
  protected void finalize()
  {
    free();
  }
}
