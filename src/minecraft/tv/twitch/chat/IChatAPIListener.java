package tv.twitch.chat;

import tv.twitch.ErrorCode;

public abstract interface IChatAPIListener
{
  public abstract void chatInitializationCallback(ErrorCode paramErrorCode);
  
  public abstract void chatShutdownCallback(ErrorCode paramErrorCode);
  
  public abstract void chatEmoticonDataDownloadCallback(ErrorCode paramErrorCode);
}
