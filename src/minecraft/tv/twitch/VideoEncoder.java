package tv.twitch;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;


public enum VideoEncoder
{
  TTV_VID_ENC_DISABLE(-2), 
  TTV_VID_ENC_DEFAULT(-1), 
  TTV_VID_ENC_INTEL(0), 
  TTV_VID_ENC_APPLE(2), 
  TTV_VID_ENC_PLUGIN(100);
  
  static { s_Map = new HashMap();
    


    EnumSet localEnumSet = EnumSet.allOf(VideoEncoder.class);
    
    for (VideoEncoder localVideoEncoder : localEnumSet)
    {
      s_Map.put(Integer.valueOf(localVideoEncoder.getValue()), localVideoEncoder);
    }
  }
  
  public static VideoEncoder lookupValue(int paramInt)
  {
    VideoEncoder localVideoEncoder = (VideoEncoder)s_Map.get(Integer.valueOf(paramInt));
    return localVideoEncoder;
  }
  
  private static Map<Integer, VideoEncoder> s_Map;
  private int m_Value;
  private VideoEncoder(int paramInt)
  {
    m_Value = paramInt;
  }
  
  public int getValue()
  {
    return m_Value;
  }
}
