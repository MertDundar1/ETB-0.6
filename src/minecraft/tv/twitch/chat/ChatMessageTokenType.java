package tv.twitch.chat;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;




public enum ChatMessageTokenType
{
  TTV_CHAT_MSGTOKEN_TEXT(0), 
  TTV_CHAT_MSGTOKEN_TEXTURE_IMAGE(1), 
  TTV_CHAT_MSGTOKEN_URL_IMAGE(2);
  
  static { s_Map = new HashMap();
    


    EnumSet localEnumSet = EnumSet.allOf(ChatMessageTokenType.class);
    
    for (ChatMessageTokenType localChatMessageTokenType : localEnumSet)
    {
      s_Map.put(Integer.valueOf(localChatMessageTokenType.getValue()), localChatMessageTokenType);
    }
  }
  
  public static ChatMessageTokenType lookupValue(int paramInt)
  {
    ChatMessageTokenType localChatMessageTokenType = (ChatMessageTokenType)s_Map.get(Integer.valueOf(paramInt));
    return localChatMessageTokenType;
  }
  
  private static Map<Integer, ChatMessageTokenType> s_Map;
  private int m_Value;
  private ChatMessageTokenType(int paramInt)
  {
    m_Value = paramInt;
  }
  
  public int getValue()
  {
    return m_Value;
  }
}
