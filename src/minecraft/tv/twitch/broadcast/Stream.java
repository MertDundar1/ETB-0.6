package tv.twitch.broadcast;

import java.util.HashSet;
import tv.twitch.AuthToken;
import tv.twitch.ErrorCode;




public class Stream
{
  static Stream s_Instance = null;
  
  public static Stream getInstance()
  {
    return s_Instance;
  }
  



  StreamAPI m_StreamAPI = null;
  
  public Stream(StreamAPI paramStreamAPI)
  {
    m_StreamAPI = paramStreamAPI;
    
    if (s_Instance == null)
    {
      s_Instance = this;
    }
  }
  
  protected void finalize()
  {
    if (s_Instance == this)
    {
      s_Instance = null;
    }
  }
  



  public IStreamCallbacks getStreamCallbacks()
  {
    return m_StreamAPI.getStreamCallbacks();
  }
  
  public void setStreamCallbacks(IStreamCallbacks paramIStreamCallbacks) {
    m_StreamAPI.setStreamCallbacks(paramIStreamCallbacks);
  }
  
  public IStatCallbacks getStatCallbacks()
  {
    return m_StreamAPI.getStatCallbacks();
  }
  
  public void setStatCallbacks(IStatCallbacks paramIStatCallbacks) {
    m_StreamAPI.setStatCallbacks(paramIStatCallbacks);
  }
  
  public FrameBuffer allocateFrameBuffer(int paramInt)
  {
    return new FrameBuffer(m_StreamAPI, paramInt);
  }
  
  public ErrorCode memsetFrameBuffer(FrameBuffer paramFrameBuffer, int paramInt)
  {
    return m_StreamAPI.memsetFrameBuffer(paramFrameBuffer.getAddress(), paramFrameBuffer.getSize(), paramInt);
  }
  
  public ErrorCode randomizeFrameBuffer(FrameBuffer paramFrameBuffer)
  {
    return m_StreamAPI.randomizeFrameBuffer(paramFrameBuffer.getAddress(), paramFrameBuffer.getSize());
  }
  
  public ErrorCode requestAuthToken(AuthParams paramAuthParams, HashSet<AuthFlag> paramHashSet)
  {
    ErrorCode localErrorCode = m_StreamAPI.requestAuthToken(paramAuthParams, paramHashSet);
    return localErrorCode;
  }
  
  public ErrorCode login(AuthToken paramAuthToken)
  {
    ErrorCode localErrorCode = m_StreamAPI.login(paramAuthToken);
    return localErrorCode;
  }
  
  public ErrorCode getIngestServers(AuthToken paramAuthToken)
  {
    return m_StreamAPI.getIngestServers(paramAuthToken);
  }
  
  public ErrorCode getUserInfo(AuthToken paramAuthToken)
  {
    return m_StreamAPI.getUserInfo(paramAuthToken);
  }
  
  public ErrorCode getStreamInfo(AuthToken paramAuthToken, String paramString)
  {
    return m_StreamAPI.getStreamInfo(paramAuthToken, paramString);
  }
  
  public ErrorCode setStreamInfo(AuthToken paramAuthToken, String paramString, StreamInfoForSetting paramStreamInfoForSetting)
  {
    return m_StreamAPI.setStreamInfo(paramAuthToken, paramString, paramStreamInfoForSetting);
  }
  
  public ErrorCode getArchivingState(AuthToken paramAuthToken)
  {
    return m_StreamAPI.getArchivingState(paramAuthToken);
  }
  
  public ErrorCode runCommercial(AuthToken paramAuthToken)
  {
    return m_StreamAPI.runCommercial(paramAuthToken);
  }
  
  public ErrorCode setVolume(AudioDeviceType paramAudioDeviceType, float paramFloat)
  {
    return m_StreamAPI.setVolume(paramAudioDeviceType, paramFloat);
  }
  
  public float getVolume(AudioDeviceType paramAudioDeviceType)
  {
    return m_StreamAPI.getVolume(paramAudioDeviceType);
  }
  
  public ErrorCode getGameNameList(String paramString)
  {
    return m_StreamAPI.getGameNameList(paramString);
  }
  
  public ErrorCode getDefaultParams(VideoParams paramVideoParams)
  {
    return m_StreamAPI.getDefaultParams(paramVideoParams);
  }
  
  public int[] getMaxResolution(int paramInt1, int paramInt2, float paramFloat1, float paramFloat2)
  {
    return m_StreamAPI.getMaxResolution(paramInt1, paramInt2, paramFloat1, paramFloat2);
  }
  
  public ErrorCode pollTasks()
  {
    ErrorCode localErrorCode = m_StreamAPI.pollTasks();
    return localErrorCode;
  }
  
  public ErrorCode pollStats()
  {
    ErrorCode localErrorCode = m_StreamAPI.pollStats();
    return localErrorCode;
  }
  
  public ErrorCode sendActionMetaData(AuthToken paramAuthToken, String paramString1, long paramLong, String paramString2, String paramString3)
  {
    ErrorCode localErrorCode = m_StreamAPI.sendActionMetaData(paramAuthToken, paramString1, paramLong, paramString2, paramString3);
    return localErrorCode;
  }
  
  public long sendStartSpanMetaData(AuthToken paramAuthToken, String paramString1, long paramLong, String paramString2, String paramString3)
  {
    long l = m_StreamAPI.sendStartSpanMetaData(paramAuthToken, paramString1, paramLong, paramString2, paramString3);
    return l;
  }
  
  public ErrorCode sendEndSpanMetaData(AuthToken paramAuthToken, String paramString1, long paramLong1, long paramLong2, String paramString2, String paramString3)
  {
    ErrorCode localErrorCode = m_StreamAPI.sendEndSpanMetaData(paramAuthToken, paramString1, paramLong1, paramLong2, paramString2, paramString3);
    return localErrorCode;
  }
  
  public ErrorCode submitVideoFrame(FrameBuffer paramFrameBuffer)
  {
    ErrorCode localErrorCode = m_StreamAPI.submitVideoFrame(paramFrameBuffer.getAddress());
    return localErrorCode;
  }
  
  public ErrorCode captureFrameBuffer_ReadPixels(FrameBuffer paramFrameBuffer)
  {
    ErrorCode localErrorCode = m_StreamAPI.captureFrameBuffer_ReadPixels(paramFrameBuffer.getAddress());
    return localErrorCode;
  }
  
  public ErrorCode start(VideoParams paramVideoParams, AudioParams paramAudioParams, IngestServer paramIngestServer, StartFlags paramStartFlags, boolean paramBoolean)
  {
    if (paramStartFlags == null)
    {
      paramStartFlags = StartFlags.None;
    }
    
    ErrorCode localErrorCode = m_StreamAPI.start(paramVideoParams, paramAudioParams, paramIngestServer, paramStartFlags.getValue(), paramBoolean);
    return localErrorCode;
  }
  
  public ErrorCode stop(boolean paramBoolean)
  {
    ErrorCode localErrorCode = m_StreamAPI.stop(paramBoolean);
    return localErrorCode;
  }
  
  public ErrorCode pauseVideo()
  {
    ErrorCode localErrorCode = m_StreamAPI.pauseVideo();
    return localErrorCode;
  }
  
  public long getStreamTime()
  {
    return m_StreamAPI.getStreamTime();
  }
}
