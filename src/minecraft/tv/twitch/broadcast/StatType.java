package tv.twitch.broadcast;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum StatType
{
  TTV_ST_RTMPSTATE(0), 
  TTV_ST_RTMPDATASENT(1);
  
  static { s_Map = new HashMap();
    


    EnumSet localEnumSet = EnumSet.allOf(StatType.class);
    
    for (StatType localStatType : localEnumSet)
    {
      s_Map.put(Integer.valueOf(localStatType.getValue()), localStatType);
    }
  }
  
  public static StatType lookupValue(int paramInt)
  {
    StatType localStatType = (StatType)s_Map.get(Integer.valueOf(paramInt));
    return localStatType;
  }
  
  private static Map<Integer, StatType> s_Map;
  private int m_Value;
  private StatType(int paramInt)
  {
    m_Value = paramInt;
  }
  
  public int getValue()
  {
    return m_Value;
  }
}
