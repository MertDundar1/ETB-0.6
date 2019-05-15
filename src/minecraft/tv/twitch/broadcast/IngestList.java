package tv.twitch.broadcast;


public class IngestList
{
  protected IngestServer[] servers = null;
  protected IngestServer defaultServer = null;
  
  public IngestServer[] getServers()
  {
    return servers;
  }
  
  public IngestServer getDefaultServer()
  {
    return defaultServer;
  }
  
  public IngestList(IngestServer[] paramArrayOfIngestServer)
  {
    if (paramArrayOfIngestServer == null)
    {
      servers = new IngestServer[0];
    }
    else
    {
      servers = new IngestServer[paramArrayOfIngestServer.length];
      for (int i = 0; i < paramArrayOfIngestServer.length; i++)
      {
        servers[i] = paramArrayOfIngestServer[i];
        
        if (servers[i].defaultServer)
        {
          defaultServer = servers[i];
        }
      }
      
      if ((defaultServer == null) && (servers.length > 0))
      {
        defaultServer = servers[0];
      }
    }
  }
  
  public IngestServer getBestServer()
  {
    if ((servers == null) || (servers.length == 0))
    {
      return null;
    }
    
    IngestServer localIngestServer = servers[0];
    for (int i = 1; i < servers.length; i++)
    {
      if (bitrateKbps < servers[i].bitrateKbps)
      {
        localIngestServer = servers[i];
      }
    }
    
    return localIngestServer;
  }
}
