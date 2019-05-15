package tv.twitch.chat;

import java.util.HashSet;
import tv.twitch.ErrorCode;




public class Chat
{
  private static Chat s_Instance = null;
  
  public static Chat getInstance()
  {
    return s_Instance;
  }
  



  private ChatAPI m_ChatAPI = null;
  
  public Chat(ChatAPI paramChatAPI)
  {
    m_ChatAPI = paramChatAPI;
    
    if (s_Instance == null)
    {
      s_Instance = this;
    }
  }
  
  public ErrorCode initialize(HashSet<ChatTokenizationOption> paramHashSet, IChatAPIListener paramIChatAPIListener)
  {
    return m_ChatAPI.initialize(paramHashSet, paramIChatAPIListener);
  }
  
  public ErrorCode shutdown()
  {
    return m_ChatAPI.shutdown();
  }
  
  public ErrorCode connect(String paramString1, String paramString2, String paramString3, IChatChannelListener paramIChatChannelListener)
  {
    return m_ChatAPI.connect(paramString1, paramString2, paramString3, paramIChatChannelListener);
  }
  
  public ErrorCode connectAnonymous(String paramString, IChatChannelListener paramIChatChannelListener)
  {
    return m_ChatAPI.connectAnonymous(paramString, paramIChatChannelListener);
  }
  
  public ErrorCode disconnect(String paramString)
  {
    return m_ChatAPI.disconnect(paramString);
  }
  
  public ErrorCode sendMessage(String paramString1, String paramString2)
  {
    return m_ChatAPI.sendMessage(paramString1, paramString2);
  }
  
  public ErrorCode flushEvents()
  {
    return m_ChatAPI.flushEvents();
  }
  
  public ErrorCode downloadEmoticonData()
  {
    return m_ChatAPI.downloadEmoticonData();
  }
  
  public ErrorCode getEmoticonData(ChatEmoticonData paramChatEmoticonData)
  {
    return m_ChatAPI.getEmoticonData(paramChatEmoticonData);
  }
  
  public ErrorCode clearEmoticonData()
  {
    return m_ChatAPI.clearEmoticonData();
  }
  
  public ErrorCode downloadBadgeData(String paramString)
  {
    return m_ChatAPI.downloadBadgeData(paramString);
  }
  
  public ErrorCode getBadgeData(String paramString, ChatBadgeData paramChatBadgeData)
  {
    return m_ChatAPI.getBadgeData(paramString, paramChatBadgeData);
  }
  
  public ErrorCode clearBadgeData(String paramString)
  {
    return m_ChatAPI.clearBadgeData(paramString);
  }
  
  public int getMessageFlushInterval()
  {
    return m_ChatAPI.getMessageFlushInterval();
  }
  
  public ErrorCode setMessageFlushInterval(int paramInt)
  {
    return m_ChatAPI.setMessageFlushInterval(paramInt);
  }
  
  public int getUserChangeEventInterval()
  {
    return m_ChatAPI.getUserChangeEventInterval();
  }
  
  public ErrorCode setUserChangeEventInterval(int paramInt)
  {
    return m_ChatAPI.setUserChangeEventInterval(paramInt);
  }
}
