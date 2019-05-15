package tv.twitch.chat;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;




public enum ChatEvent
{
  TTV_CHAT_JOINED_CHANNEL(0), 
  TTV_CHAT_LEFT_CHANNEL(1);
  
  static { s_Map = new HashMap();
    


    EnumSet localEnumSet = EnumSet.allOf(ChatEvent.class);
    
    for (ChatEvent localChatEvent : localEnumSet)
    {
      s_Map.put(Integer.valueOf(localChatEvent.getValue()), localChatEvent);
    }
  }
  
  public static ChatEvent lookupValue(int paramInt)
  {
    ChatEvent localChatEvent = (ChatEvent)s_Map.get(Integer.valueOf(paramInt));
    return localChatEvent;
  }
  
  private static Map<Integer, ChatEvent> s_Map;
  private int m_Value;
  private ChatEvent(int paramInt)
  {
    m_Value = paramInt;
  }
  
  public int getValue()
  {
    return m_Value;
  }
}
