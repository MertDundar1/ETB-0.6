package io.netty.handler.ssl;

import io.netty.util.internal.ObjectUtil;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionContext;
import org.apache.tomcat.jni.SSLContext;
import org.apache.tomcat.jni.SessionTicketKey;



















public abstract class OpenSslSessionContext
  implements SSLSessionContext
{
  private static final Enumeration<byte[]> EMPTY = new EmptyEnumeration(null);
  

  private final OpenSslSessionStats stats;
  
  final ReferenceCountedOpenSslContext context;
  

  OpenSslSessionContext(ReferenceCountedOpenSslContext context)
  {
    this.context = context;
    stats = new OpenSslSessionStats(context);
  }
  
  public SSLSession getSession(byte[] bytes)
  {
    if (bytes == null) {
      throw new NullPointerException("bytes");
    }
    return null;
  }
  
  public Enumeration<byte[]> getIds()
  {
    return EMPTY;
  }
  



  @Deprecated
  public void setTicketKeys(byte[] keys)
  {
    ObjectUtil.checkNotNull(keys, "keys");
    SSLContext.clearOptions(context.ctx, 16384);
    SSLContext.setSessionTicketKeys(context.ctx, keys);
  }
  


  public void setTicketKeys(OpenSslSessionTicketKey... keys)
  {
    ObjectUtil.checkNotNull(keys, "keys");
    SSLContext.clearOptions(context.ctx, 16384);
    SessionTicketKey[] ticketKeys = new SessionTicketKey[keys.length];
    for (int i = 0; i < ticketKeys.length; i++) {
      ticketKeys[i] = key;
    }
    SSLContext.setSessionTicketKeys(context.ctx, ticketKeys);
  }
  



  public abstract void setSessionCacheEnabled(boolean paramBoolean);
  



  public abstract boolean isSessionCacheEnabled();
  


  public OpenSslSessionStats stats()
  {
    return stats;
  }
  
  private static final class EmptyEnumeration implements Enumeration<byte[]> {
    private EmptyEnumeration() {}
    
    public boolean hasMoreElements() { return false; }
    

    public byte[] nextElement()
    {
      throw new NoSuchElementException();
    }
  }
}
