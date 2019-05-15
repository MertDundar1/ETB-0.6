package tv.twitch.broadcast;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum AudioEncoder
{
  TTV_AUD_ENC_DEFAULT(-1), 
  TTV_AUD_ENC_LAMEMP3(0), 
  TTV_AUD_ENC_APPLEAAC(1);
  
  static { s_Map = new HashMap();
    


    EnumSet localEnumSet = EnumSet.allOf(AudioEncoder.class);
    
    for (AudioEncoder localAudioEncoder : localEnumSet)
    {
      s_Map.put(Integer.valueOf(localAudioEncoder.getValue()), localAudioEncoder);
    }
  }
  
  public static AudioEncoder lookupValue(int paramInt)
  {
    AudioEncoder localAudioEncoder = (AudioEncoder)s_Map.get(Integer.valueOf(paramInt));
    return localAudioEncoder;
  }
  
  private static Map<Integer, AudioEncoder> s_Map;
  private int m_Value;
  private AudioEncoder(int paramInt)
  {
    m_Value = paramInt;
  }
  
  public int getValue()
  {
    return m_Value;
  }
}
