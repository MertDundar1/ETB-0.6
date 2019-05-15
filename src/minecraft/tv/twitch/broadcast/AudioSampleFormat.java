package tv.twitch.broadcast;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;


public enum AudioSampleFormat
{
  TTV_ASF_PCM_S16(0);
  
  static { s_Map = new HashMap();
    


    EnumSet localEnumSet = EnumSet.allOf(AudioSampleFormat.class);
    
    for (AudioSampleFormat localAudioSampleFormat : localEnumSet)
    {
      s_Map.put(Integer.valueOf(localAudioSampleFormat.getValue()), localAudioSampleFormat);
    }
  }
  
  public static AudioSampleFormat lookupValue(int paramInt)
  {
    AudioSampleFormat localAudioSampleFormat = (AudioSampleFormat)s_Map.get(Integer.valueOf(paramInt));
    return localAudioSampleFormat;
  }
  
  private static Map<Integer, AudioSampleFormat> s_Map;
  private int m_Value;
  private AudioSampleFormat(int paramInt)
  {
    m_Value = paramInt;
  }
  
  public int getValue()
  {
    return m_Value;
  }
}
