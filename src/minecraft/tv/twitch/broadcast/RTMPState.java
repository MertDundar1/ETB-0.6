package tv.twitch.broadcast;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum RTMPState
{
  Invalid(-1), 
  Idle(0), 
  Initialize(1), 
  Handshake(2), 
  Connect(3), 
  CreateStream(4), 
  Publish(5), 
  SendVideo(6), 
  Shutdown(7), 
  Error(8);
  
  static { s_Map = new HashMap();
    


    EnumSet localEnumSet = EnumSet.allOf(RTMPState.class);
    
    for (RTMPState localRTMPState : localEnumSet)
    {
      s_Map.put(Integer.valueOf(localRTMPState.getValue()), localRTMPState);
    }
  }
  
  public static RTMPState lookupValue(int paramInt)
  {
    RTMPState localRTMPState = (RTMPState)s_Map.get(Integer.valueOf(paramInt));
    return localRTMPState;
  }
  
  private static Map<Integer, RTMPState> s_Map;
  private int m_Value;
  private RTMPState(int paramInt)
  {
    m_Value = paramInt;
  }
  
  public int getValue()
  {
    return m_Value;
  }
}
