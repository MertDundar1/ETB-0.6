package tv.twitch;




public class Core
{
  private static Core s_Instance = null;
  
  public static Core getInstance()
  {
    return s_Instance;
  }
  



  private CoreAPI m_CoreAPI = null;
  private String m_ClientId = null;
  private int m_NumInitializations = 0;
  
  public Core(CoreAPI paramCoreAPI)
  {
    m_CoreAPI = paramCoreAPI;
    
    if (s_Instance == null)
    {
      s_Instance = this;
    }
  }
  
  public boolean getIsInitialized()
  {
    return m_NumInitializations > 0;
  }
  
  public ErrorCode initialize(String paramString1, String paramString2)
  {
    if (m_NumInitializations == 0)
    {
      m_ClientId = paramString1;


    }
    else if (paramString1 != m_ClientId)
    {
      return ErrorCode.TTV_EC_INVALID_CLIENTID;
    }
    

    m_NumInitializations += 1;
    

    if (m_NumInitializations > 1)
    {
      return ErrorCode.TTV_EC_SUCCESS;
    }
    
    ErrorCode localErrorCode = m_CoreAPI.init(paramString1, paramString2);
    
    if (ErrorCode.failed(localErrorCode))
    {
      m_NumInitializations -= 1;
      m_ClientId = null;
    }
    
    return localErrorCode;
  }
  
  public ErrorCode shutdown()
  {
    if (m_NumInitializations == 0)
    {
      return ErrorCode.TTV_EC_NOT_INITIALIZED;
    }
    
    m_NumInitializations -= 1;
    
    ErrorCode localErrorCode = ErrorCode.TTV_EC_SUCCESS;
    
    if (m_NumInitializations == 0)
    {
      localErrorCode = m_CoreAPI.shutdown();
      
      if (ErrorCode.failed(localErrorCode))
      {
        m_NumInitializations += 1;


      }
      else if (s_Instance == this)
      {
        s_Instance = null;
      }
    }
    

    return localErrorCode;
  }
  
  public ErrorCode setTraceLevel(MessageLevel paramMessageLevel)
  {
    ErrorCode localErrorCode = m_CoreAPI.setTraceLevel(paramMessageLevel);
    return localErrorCode;
  }
  
  public String errorToString(ErrorCode paramErrorCode)
  {
    String str = m_CoreAPI.errorToString(paramErrorCode);
    return str;
  }
}
