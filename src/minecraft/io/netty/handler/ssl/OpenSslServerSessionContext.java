package io.netty.handler.ssl;

import org.apache.tomcat.jni.SSLContext;


















public final class OpenSslServerSessionContext
  extends OpenSslSessionContext
{
  OpenSslServerSessionContext(ReferenceCountedOpenSslContext context)
  {
    super(context);
  }
  
  public void setSessionTimeout(int seconds)
  {
    if (seconds < 0) {
      throw new IllegalArgumentException();
    }
    SSLContext.setSessionCacheTimeout(context.ctx, seconds);
  }
  
  public int getSessionTimeout()
  {
    return (int)SSLContext.getSessionCacheTimeout(context.ctx);
  }
  
  public void setSessionCacheSize(int size)
  {
    if (size < 0) {
      throw new IllegalArgumentException();
    }
    SSLContext.setSessionCacheSize(context.ctx, size);
  }
  
  public int getSessionCacheSize()
  {
    return (int)SSLContext.getSessionCacheSize(context.ctx);
  }
  
  public void setSessionCacheEnabled(boolean enabled)
  {
    long mode = enabled ? 2L : 0L;
    SSLContext.setSessionCacheMode(context.ctx, mode);
  }
  
  public boolean isSessionCacheEnabled()
  {
    return SSLContext.getSessionCacheMode(context.ctx) == 2L;
  }
  








  public boolean setSessionIdContext(byte[] sidCtx)
  {
    return SSLContext.setSessionIdContext(context.ctx, sidCtx);
  }
}
