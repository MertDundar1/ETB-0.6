package tv.twitch.broadcast;

import tv.twitch.AuthToken;
import tv.twitch.ErrorCode;

public abstract interface IStreamCallbacks
{
  public abstract void requestAuthTokenCallback(ErrorCode paramErrorCode, AuthToken paramAuthToken);
  
  public abstract void loginCallback(ErrorCode paramErrorCode, ChannelInfo paramChannelInfo);
  
  public abstract void getIngestServersCallback(ErrorCode paramErrorCode, IngestList paramIngestList);
  
  public abstract void getUserInfoCallback(ErrorCode paramErrorCode, UserInfo paramUserInfo);
  
  public abstract void getStreamInfoCallback(ErrorCode paramErrorCode, StreamInfo paramStreamInfo);
  
  public abstract void getArchivingStateCallback(ErrorCode paramErrorCode, ArchivingState paramArchivingState);
  
  public abstract void runCommercialCallback(ErrorCode paramErrorCode);
  
  public abstract void setStreamInfoCallback(ErrorCode paramErrorCode);
  
  public abstract void getGameNameListCallback(ErrorCode paramErrorCode, GameInfoList paramGameInfoList);
  
  public abstract void bufferUnlockCallback(long paramLong);
  
  public abstract void startCallback(ErrorCode paramErrorCode);
  
  public abstract void stopCallback(ErrorCode paramErrorCode);
  
  public abstract void sendActionMetaDataCallback(ErrorCode paramErrorCode);
  
  public abstract void sendStartSpanMetaDataCallback(ErrorCode paramErrorCode);
  
  public abstract void sendEndSpanMetaDataCallback(ErrorCode paramErrorCode);
}
