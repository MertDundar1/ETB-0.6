package tv.twitch.chat;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;






public enum ChatTokenizationOption
{
  TTV_CHAT_TOKENIZATION_OPTION_NONE(0), 
  TTV_CHAT_TOKENIZATION_OPTION_EMOTICON_URLS(1), 
  TTV_CHAT_TOKENIZATION_OPTION_EMOTICON_TEXTURES(2);
  
  static { s_Map = new HashMap();
    


    EnumSet localEnumSet = EnumSet.allOf(ChatTokenizationOption.class);
    
    for (ChatTokenizationOption localChatTokenizationOption : localEnumSet)
    {
      s_Map.put(Integer.valueOf(localChatTokenizationOption.getValue()), localChatTokenizationOption);
    }
  }
  
  public static ChatTokenizationOption lookupValue(int paramInt)
  {
    ChatTokenizationOption localChatTokenizationOption = (ChatTokenizationOption)s_Map.get(Integer.valueOf(paramInt));
    return localChatTokenizationOption;
  }
  
  public static int getNativeValue(HashSet<ChatTokenizationOption> paramHashSet)
  {
    if (paramHashSet == null)
    {
      return TTV_CHAT_TOKENIZATION_OPTION_NONE.getValue();
    }
    
    int i = TTV_CHAT_TOKENIZATION_OPTION_NONE.getValue();
    
    for (ChatTokenizationOption localChatTokenizationOption : paramHashSet)
    {
      if (localChatTokenizationOption != null)
      {
        i |= localChatTokenizationOption.getValue();
      }
    }
    
    return i;
  }
  
  private static Map<Integer, ChatTokenizationOption> s_Map;
  private int m_Value;
  private ChatTokenizationOption(int paramInt)
  {
    m_Value = paramInt;
  }
  
  public int getValue()
  {
    return m_Value;
  }
}
