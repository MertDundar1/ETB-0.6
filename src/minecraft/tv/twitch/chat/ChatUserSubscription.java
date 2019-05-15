package tv.twitch.chat;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;




public enum ChatUserSubscription
{
  TTV_CHAT_USERSUB_NONE(0), 
  
  TTV_CHAT_USERSUB_SUBSCRIBER(1), 
  TTV_CHAT_USERSUB_TURBO(2);
  
  static { s_Map = new HashMap();
    


    EnumSet localEnumSet = EnumSet.allOf(ChatUserSubscription.class);
    
    for (ChatUserSubscription localChatUserSubscription : localEnumSet)
    {
      s_Map.put(Integer.valueOf(localChatUserSubscription.getValue()), localChatUserSubscription);
    }
  }
  
  public static ChatUserSubscription lookupValue(int paramInt)
  {
    ChatUserSubscription localChatUserSubscription = (ChatUserSubscription)s_Map.get(Integer.valueOf(paramInt));
    return localChatUserSubscription;
  }
  
  private static Map<Integer, ChatUserSubscription> s_Map;
  private int m_Value;
  private ChatUserSubscription(int paramInt)
  {
    m_Value = paramInt;
  }
  
  public int getValue()
  {
    return m_Value;
  }
}
