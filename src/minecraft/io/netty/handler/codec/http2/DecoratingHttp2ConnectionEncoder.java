package io.netty.handler.codec.http2;

import io.netty.util.internal.ObjectUtil;

















public class DecoratingHttp2ConnectionEncoder
  extends DecoratingHttp2FrameWriter
  implements Http2ConnectionEncoder
{
  private final Http2ConnectionEncoder delegate;
  
  public DecoratingHttp2ConnectionEncoder(Http2ConnectionEncoder delegate)
  {
    super(delegate);
    this.delegate = ((Http2ConnectionEncoder)ObjectUtil.checkNotNull(delegate, "delegate"));
  }
  
  public void lifecycleManager(Http2LifecycleManager lifecycleManager)
  {
    delegate.lifecycleManager(lifecycleManager);
  }
  
  public Http2Connection connection()
  {
    return delegate.connection();
  }
  
  public Http2RemoteFlowController flowController()
  {
    return delegate.flowController();
  }
  
  public Http2FrameWriter frameWriter()
  {
    return delegate.frameWriter();
  }
  
  public Http2Settings pollSentSettings()
  {
    return delegate.pollSentSettings();
  }
  
  public void remoteSettings(Http2Settings settings) throws Http2Exception
  {
    delegate.remoteSettings(settings);
  }
}
