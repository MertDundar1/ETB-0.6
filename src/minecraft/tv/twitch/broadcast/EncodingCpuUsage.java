package tv.twitch.broadcast;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum EncodingCpuUsage
{
  TTV_ECU_LOW(0), 
  TTV_ECU_MEDIUM(1), 
  TTV_ECU_HIGH(2);
  
  static { s_Map = new HashMap();
    


    EnumSet localEnumSet = EnumSet.allOf(EncodingCpuUsage.class);
    
    for (EncodingCpuUsage localEncodingCpuUsage : localEnumSet)
    {
      s_Map.put(Integer.valueOf(localEncodingCpuUsage.getValue()), localEncodingCpuUsage);
    }
  }
  
  public static EncodingCpuUsage lookupValue(int paramInt)
  {
    EncodingCpuUsage localEncodingCpuUsage = (EncodingCpuUsage)s_Map.get(Integer.valueOf(paramInt));
    return localEncodingCpuUsage;
  }
  
  private static Map<Integer, EncodingCpuUsage> s_Map;
  private int m_Value;
  private EncodingCpuUsage(int paramInt)
  {
    m_Value = paramInt;
  }
  
  public int getValue()
  {
    return m_Value;
  }
}
