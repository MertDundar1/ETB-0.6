package tv.twitch.chat;

import tv.twitch.ErrorCode;

public abstract interface IChatChannelListener
{
  public abstract void chatStatusCallback(String paramString, ErrorCode paramErrorCode);
  
  public abstract void chatChannelMembershipCallback(String paramString, ChatEvent paramChatEvent, ChatChannelInfo paramChatChannelInfo);
  
  public abstract void chatChannelUserChangeCallback(String paramString, ChatUserInfo[] paramArrayOfChatUserInfo1, ChatUserInfo[] paramArrayOfChatUserInfo2, ChatUserInfo[] paramArrayOfChatUserInfo3);
  
  public abstract void chatChannelRawMessageCallback(String paramString, ChatRawMessage[] paramArrayOfChatRawMessage);
  
  public abstract void chatChannelTokenizedMessageCallback(String paramString, ChatTokenizedMessage[] paramArrayOfChatTokenizedMessage);
  
  public abstract void chatClearCallback(String paramString1, String paramString2);
  
  public abstract void chatBadgeDataDownloadCallback(String paramString, ErrorCode paramErrorCode);
}
