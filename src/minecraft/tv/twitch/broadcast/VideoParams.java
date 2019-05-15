package tv.twitch.broadcast;

public class VideoParams
{
  public int outputWidth;
  public int outputHeight;
  public PixelFormat pixelFormat = PixelFormat.TTV_PF_BGRA;
  public int maxKbps;
  public int targetFps;
  public EncodingCpuUsage encodingCpuUsage = EncodingCpuUsage.TTV_ECU_HIGH;
  public boolean disableAdaptiveBitrate = false;
  public boolean verticalFlip = false;
  

  public VideoParams() {}
  

  public VideoParams clone()
  {
    VideoParams localVideoParams = new VideoParams();
    
    outputWidth = outputWidth;
    outputHeight = outputHeight;
    pixelFormat = pixelFormat;
    maxKbps = maxKbps;
    targetFps = targetFps;
    encodingCpuUsage = encodingCpuUsage;
    disableAdaptiveBitrate = disableAdaptiveBitrate;
    verticalFlip = verticalFlip;
    
    return localVideoParams;
  }
}
