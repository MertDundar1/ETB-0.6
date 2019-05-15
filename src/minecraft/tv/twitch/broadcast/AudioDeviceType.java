package tv.twitch.broadcast;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;


public enum AudioDeviceType
{
  TTV_PLAYBACK_DEVICE(0), 
  TTV_RECORDER_DEVICE(1), 
  TTV_PASSTHROUGH_DEVICE(2), 
  
  TTV_DEVICE_NUM(3);
  
  static { s_Map = new HashMap();
    


    EnumSet localEnumSet = EnumSet.allOf(AudioDeviceType.class);
    
    for (AudioDeviceType localAudioDeviceType : localEnumSet)
    {
      s_Map.put(Integer.valueOf(localAudioDeviceType.getValue()), localAudioDeviceType);
    }
  }
  
  public static AudioDeviceType lookupValue(int paramInt)
  {
    AudioDeviceType localAudioDeviceType = (AudioDeviceType)s_Map.get(Integer.valueOf(paramInt));
    return localAudioDeviceType;
  }
  
  private static Map<Integer, AudioDeviceType> s_Map;
  private int m_Value;
  private AudioDeviceType(int paramInt)
  {
    m_Value = paramInt;
  }
  
  public int getValue()
  {
    return m_Value;
  }
}
