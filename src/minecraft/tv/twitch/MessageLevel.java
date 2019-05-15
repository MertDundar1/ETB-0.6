package tv.twitch;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;


public enum MessageLevel
{
  TTV_ML_DEBUG(0), 
  TTV_ML_INFO(1), 
  TTV_ML_WARNING(2), 
  TTV_ML_ERROR(3), 
  TTV_ML_CHAT(4), 
  
  TTV_ML_NONE(5);
  
  static { s_Map = new HashMap();
    


    EnumSet localEnumSet = EnumSet.allOf(MessageLevel.class);
    
    for (MessageLevel localMessageLevel : localEnumSet)
    {
      s_Map.put(Integer.valueOf(localMessageLevel.getValue()), localMessageLevel);
    }
  }
  
  public static MessageLevel lookupValue(int paramInt)
  {
    MessageLevel localMessageLevel = (MessageLevel)s_Map.get(Integer.valueOf(paramInt));
    return localMessageLevel;
  }
  
  private static Map<Integer, MessageLevel> s_Map;
  private int m_Value;
  private MessageLevel(int paramInt)
  {
    m_Value = paramInt;
  }
  
  public int getValue()
  {
    return m_Value;
  }
}
