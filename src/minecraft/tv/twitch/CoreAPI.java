package tv.twitch;

public abstract class CoreAPI
{
  public CoreAPI() {}
  
  public abstract ErrorCode init(String paramString1, String paramString2);
  
  public abstract ErrorCode shutdown();
  
  public abstract ErrorCode setTraceLevel(MessageLevel paramMessageLevel);
  
  public abstract ErrorCode setTraceOutput(String paramString);
  
  public abstract String errorToString(ErrorCode paramErrorCode);
}
