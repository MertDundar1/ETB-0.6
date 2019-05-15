package tv.twitch.broadcast;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum PixelFormat
{
  TTV_PF_BGRA(66051), 
  TTV_PF_ABGR(16909056), 
  TTV_PF_RGBA(33619971), 
  TTV_PF_ARGB(50462976);
  
  static { s_Map = new HashMap();
    


    EnumSet localEnumSet = EnumSet.allOf(PixelFormat.class);
    
    for (PixelFormat localPixelFormat : localEnumSet)
    {
      s_Map.put(Integer.valueOf(localPixelFormat.getValue()), localPixelFormat);
    }
  }
  
  public static PixelFormat lookupValue(int paramInt)
  {
    PixelFormat localPixelFormat = (PixelFormat)s_Map.get(Integer.valueOf(paramInt));
    return localPixelFormat;
  }
  
  private static Map<Integer, PixelFormat> s_Map;
  private int m_Value;
  private PixelFormat(int paramInt)
  {
    m_Value = paramInt;
  }
  
  public int getValue()
  {
    return m_Value;
  }
}
