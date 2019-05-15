package tv.twitch.chat;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;




public enum ChatUserMode
{
  TTV_CHAT_USERMODE_VIEWER(0), 
  
  TTV_CHAT_USERMODE_MODERATOR(1), 
  TTV_CHAT_USERMODE_BROADCASTER(2), 
  TTV_CHAT_USERMODE_ADMINSTRATOR(4), 
  TTV_CHAT_USERMODE_STAFF(8), 
  
  TTV_CHAT_USERMODE_BANNED(1073741824);
  
  static { s_Map = new HashMap();
    


    EnumSet localEnumSet = EnumSet.allOf(ChatUserMode.class);
    
    for (ChatUserMode localChatUserMode : localEnumSet)
    {
      s_Map.put(Integer.valueOf(localChatUserMode.getValue()), localChatUserMode);
    }
  }
  
  public static ChatUserMode lookupValue(int paramInt)
  {
    ChatUserMode localChatUserMode = (ChatUserMode)s_Map.get(Integer.valueOf(paramInt));
    return localChatUserMode;
  }
  
  private static Map<Integer, ChatUserMode> s_Map;
  private int m_Value;
  private ChatUserMode(int paramInt)
  {
    m_Value = paramInt;
  }
  
  public int getValue()
  {
    return m_Value;
  }
}
