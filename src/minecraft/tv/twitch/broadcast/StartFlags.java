package tv.twitch.broadcast;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum StartFlags
{
  None(0), 
  TTV_Start_BandwidthTest(1);
  
  static { s_Map = new HashMap();
    


    EnumSet localEnumSet = EnumSet.allOf(StartFlags.class);
    
    for (StartFlags localStartFlags : localEnumSet)
    {
      s_Map.put(Integer.valueOf(localStartFlags.getValue()), localStartFlags);
    }
  }
  
  public static StartFlags lookupValue(int paramInt)
  {
    StartFlags localStartFlags = (StartFlags)s_Map.get(Integer.valueOf(paramInt));
    return localStartFlags;
  }
  
  private static Map<Integer, StartFlags> s_Map;
  private int m_Value;
  private StartFlags(int paramInt)
  {
    m_Value = paramInt;
  }
  
  public int getValue()
  {
    return m_Value;
  }
}
