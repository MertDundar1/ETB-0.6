package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.List;








































public class DefaultHttp2ConnectionDecoder
  implements Http2ConnectionDecoder
{
  private static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultHttp2ConnectionDecoder.class);
  private Http2FrameListener internalFrameListener = new PrefaceFrameListener(null);
  
  private final Http2Connection connection;
  private Http2LifecycleManager lifecycleManager;
  private final Http2ConnectionEncoder encoder;
  private final Http2FrameReader frameReader;
  private Http2FrameListener listener;
  private final Http2PromisedRequestVerifier requestVerifier;
  
  public DefaultHttp2ConnectionDecoder(Http2Connection connection, Http2ConnectionEncoder encoder, Http2FrameReader frameReader)
  {
    this(connection, encoder, frameReader, Http2PromisedRequestVerifier.ALWAYS_VERIFY);
  }
  


  public DefaultHttp2ConnectionDecoder(Http2Connection connection, Http2ConnectionEncoder encoder, Http2FrameReader frameReader, Http2PromisedRequestVerifier requestVerifier)
  {
    this.connection = ((Http2Connection)ObjectUtil.checkNotNull(connection, "connection"));
    this.frameReader = ((Http2FrameReader)ObjectUtil.checkNotNull(frameReader, "frameReader"));
    this.encoder = ((Http2ConnectionEncoder)ObjectUtil.checkNotNull(encoder, "encoder"));
    this.requestVerifier = ((Http2PromisedRequestVerifier)ObjectUtil.checkNotNull(requestVerifier, "requestVerifier"));
    if (connection.local().flowController() == null) {
      connection.local().flowController(new DefaultHttp2LocalFlowController(connection));
    }
    ((Http2LocalFlowController)connection.local().flowController()).frameWriter(encoder.frameWriter());
  }
  
  public void lifecycleManager(Http2LifecycleManager lifecycleManager)
  {
    this.lifecycleManager = ((Http2LifecycleManager)ObjectUtil.checkNotNull(lifecycleManager, "lifecycleManager"));
  }
  
  public Http2Connection connection()
  {
    return connection;
  }
  
  public final Http2LocalFlowController flowController()
  {
    return (Http2LocalFlowController)connection.local().flowController();
  }
  
  public void frameListener(Http2FrameListener listener)
  {
    this.listener = ((Http2FrameListener)ObjectUtil.checkNotNull(listener, "listener"));
  }
  
  public Http2FrameListener frameListener()
  {
    return listener;
  }
  
  Http2FrameListener internalFrameListener()
  {
    return internalFrameListener;
  }
  
  public boolean prefaceReceived()
  {
    return FrameReadListener.class == internalFrameListener.getClass();
  }
  
  public void decodeFrame(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Http2Exception
  {
    frameReader.readFrame(ctx, in, internalFrameListener);
  }
  
  public Http2Settings localSettings()
  {
    Http2Settings settings = new Http2Settings();
    Http2FrameReader.Configuration config = frameReader.configuration();
    Http2HeaderTable headerTable = config.headerTable();
    Http2FrameSizePolicy frameSizePolicy = config.frameSizePolicy();
    settings.initialWindowSize(flowController().initialWindowSize());
    settings.maxConcurrentStreams(connection.remote().maxActiveStreams());
    settings.headerTableSize(headerTable.maxHeaderTableSize());
    settings.maxFrameSize(frameSizePolicy.maxFrameSize());
    settings.maxHeaderListSize(headerTable.maxHeaderListSize());
    if (!connection.isServer())
    {
      settings.pushEnabled(connection.local().allowPushTo());
    }
    return settings;
  }
  
  public void close()
  {
    frameReader.close();
  }
  
  private int unconsumedBytes(Http2Stream stream) {
    return flowController().unconsumedBytes(stream);
  }
  
  void onGoAwayRead0(ChannelHandlerContext ctx, int lastStreamId, long errorCode, ByteBuf debugData) throws Http2Exception
  {
    if ((connection.goAwayReceived()) && (connection.local().lastStreamKnownByPeer() < lastStreamId)) {
      throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "lastStreamId MUST NOT increase. Current value: %d new value: %d", new Object[] { Integer.valueOf(connection.local().lastStreamKnownByPeer()), Integer.valueOf(lastStreamId) });
    }
    
    listener.onGoAwayRead(ctx, lastStreamId, errorCode, debugData);
    connection.goAwayReceived(lastStreamId, errorCode, debugData);
  }
  
  void onUnknownFrame0(ChannelHandlerContext ctx, byte frameType, int streamId, Http2Flags flags, ByteBuf payload) throws Http2Exception
  {
    listener.onUnknownFrame(ctx, frameType, streamId, flags, payload);
  }
  
  private final class FrameReadListener
    implements Http2FrameListener
  {
    private FrameReadListener() {}
    
    public int onDataRead(ChannelHandlerContext ctx, int streamId, ByteBuf data, int padding, boolean endOfStream) throws Http2Exception
    {
      Http2Stream stream = connection.stream(streamId);
      Http2LocalFlowController flowController = flowController();
      int bytesToReturn = data.readableBytes() + padding;
      
      try
      {
        shouldIgnore = shouldIgnoreHeadersOrDataFrame(ctx, streamId, stream, "DATA");
      }
      catch (Http2Exception e) {
        boolean shouldIgnore;
        flowController.receiveFlowControlledFrame(stream, data, padding, endOfStream);
        flowController.consumeBytes(stream, bytesToReturn);
        throw e;
      } catch (Throwable t) {
        throw Http2Exception.connectionError(Http2Error.INTERNAL_ERROR, t, "Unhandled error on data stream id %d", new Object[] { Integer.valueOf(streamId) });
      }
      boolean shouldIgnore;
      if (shouldIgnore)
      {

        flowController.receiveFlowControlledFrame(stream, data, padding, endOfStream);
        flowController.consumeBytes(stream, bytesToReturn);
        

        verifyStreamMayHaveExisted(streamId);
        

        return bytesToReturn;
      }
      
      Http2Exception error = null;
      switch (DefaultHttp2ConnectionDecoder.1.$SwitchMap$io$netty$handler$codec$http2$Http2Stream$State[stream.state().ordinal()]) {
      case 1: 
      case 2: 
        break;
      case 3: 
      case 4: 
        error = Http2Exception.streamError(stream.id(), Http2Error.STREAM_CLOSED, "Stream %d in unexpected state: %s", new Object[] { Integer.valueOf(stream.id()), stream.state() });
        
        break;
      default: 
        error = Http2Exception.streamError(stream.id(), Http2Error.PROTOCOL_ERROR, "Stream %d in unexpected state: %s", new Object[] { Integer.valueOf(stream.id()), stream.state() });
      }
      
      

      int unconsumedBytes = DefaultHttp2ConnectionDecoder.this.unconsumedBytes(stream);
      try {
        flowController.receiveFlowControlledFrame(stream, data, padding, endOfStream);
        
        unconsumedBytes = DefaultHttp2ConnectionDecoder.this.unconsumedBytes(stream);
        

        if (error != null) {
          throw error;
        }
        


        bytesToReturn = listener.onDataRead(ctx, streamId, data, padding, endOfStream);
        return bytesToReturn;

      }
      catch (Http2Exception e)
      {
        int delta = unconsumedBytes - DefaultHttp2ConnectionDecoder.this.unconsumedBytes(stream);
        bytesToReturn -= delta;
        throw e;

      }
      catch (RuntimeException e)
      {
        int delta = unconsumedBytes - DefaultHttp2ConnectionDecoder.this.unconsumedBytes(stream);
        bytesToReturn -= delta;
        throw e;
      }
      finally {
        flowController.consumeBytes(stream, bytesToReturn);
        
        if (endOfStream) {
          lifecycleManager.closeStreamRemote(stream, ctx.newSucceededFuture());
        }
      }
    }
    
    public void onHeadersRead(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int padding, boolean endOfStream)
      throws Http2Exception
    {
      onHeadersRead(ctx, streamId, headers, 0, (short)16, false, padding, endOfStream);
    }
    
    public void onHeadersRead(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int streamDependency, short weight, boolean exclusive, int padding, boolean endOfStream)
      throws Http2Exception
    {
      Http2Stream stream = connection.stream(streamId);
      boolean allowHalfClosedRemote = false;
      if ((stream == null) && (!connection.streamMayHaveExisted(streamId))) {
        stream = connection.remote().createStream(streamId, endOfStream);
        
        allowHalfClosedRemote = stream.state() == Http2Stream.State.HALF_CLOSED_REMOTE;
      }
      
      if (shouldIgnoreHeadersOrDataFrame(ctx, streamId, stream, "HEADERS")) {
        return;
      }
      
      switch (DefaultHttp2ConnectionDecoder.1.$SwitchMap$io$netty$handler$codec$http2$Http2Stream$State[stream.state().ordinal()]) {
      case 5: 
        stream.open(endOfStream);
        break;
      case 1: 
      case 2: 
        break;
      
      case 3: 
        if (!allowHalfClosedRemote) {
          throw Http2Exception.streamError(stream.id(), Http2Error.STREAM_CLOSED, "Stream %d in unexpected state: %s", new Object[] { Integer.valueOf(stream.id()), stream.state() });
        }
        
        break;
      case 4: 
        throw Http2Exception.streamError(stream.id(), Http2Error.STREAM_CLOSED, "Stream %d in unexpected state: %s", new Object[] { Integer.valueOf(stream.id()), stream.state() });
      

      default: 
        throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Stream %d in unexpected state: %s", new Object[] { Integer.valueOf(stream.id()), stream.state() });
      }
      
      

      try
      {
        stream.setPriority(streamDependency, weight, exclusive);
      }
      catch (Http2Exception.ClosedStreamCreationException localClosedStreamCreationException) {}
      


      listener.onHeadersRead(ctx, streamId, headers, streamDependency, weight, exclusive, padding, endOfStream);
      

      if (endOfStream) {
        lifecycleManager.closeStreamRemote(stream, ctx.newSucceededFuture());
      }
    }
    
    public void onPriorityRead(ChannelHandlerContext ctx, int streamId, int streamDependency, short weight, boolean exclusive)
      throws Http2Exception
    {
      Http2Stream stream = connection.stream(streamId);
      try
      {
        if (stream == null) {
          if (connection.streamMayHaveExisted(streamId)) {
            DefaultHttp2ConnectionDecoder.logger.info("{} ignoring PRIORITY frame for stream {}. Stream doesn't exist but may  have existed", ctx.channel(), Integer.valueOf(streamId));
            
            return;
          }
          


          stream = connection.remote().createIdleStream(streamId);
        } else if (streamCreatedAfterGoAwaySent(streamId)) {
          DefaultHttp2ConnectionDecoder.logger.info("{} ignoring PRIORITY frame for stream {}. Stream created after GOAWAY sent. Last known stream by peer {}", new Object[] { ctx.channel(), Integer.valueOf(streamId), Integer.valueOf(connection.remote().lastStreamKnownByPeer()) });
          

          return;
        }
        


        stream.setPriority(streamDependency, weight, exclusive);
      }
      catch (Http2Exception.ClosedStreamCreationException localClosedStreamCreationException) {}
      


      listener.onPriorityRead(ctx, streamId, streamDependency, weight, exclusive);
    }
    
    public void onRstStreamRead(ChannelHandlerContext ctx, int streamId, long errorCode) throws Http2Exception
    {
      Http2Stream stream = connection.stream(streamId);
      if (stream == null) {
        verifyStreamMayHaveExisted(streamId);
        return;
      }
      
      switch (DefaultHttp2ConnectionDecoder.1.$SwitchMap$io$netty$handler$codec$http2$Http2Stream$State[stream.state().ordinal()]) {
      case 6: 
        throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "RST_STREAM received for IDLE stream %d", new Object[] { Integer.valueOf(streamId) });
      case 4: 
        return;
      }
      
      

      listener.onRstStreamRead(ctx, streamId, errorCode);
      
      lifecycleManager.closeStream(stream, ctx.newSucceededFuture());
    }
    
    public void onSettingsAckRead(ChannelHandlerContext ctx)
      throws Http2Exception
    {
      Http2Settings settings = encoder.pollSentSettings();
      
      if (settings != null) {
        applyLocalSettings(settings);
      }
      
      listener.onSettingsAckRead(ctx);
    }
    

    private void applyLocalSettings(Http2Settings settings)
      throws Http2Exception
    {
      Boolean pushEnabled = settings.pushEnabled();
      Http2FrameReader.Configuration config = frameReader.configuration();
      Http2HeaderTable headerTable = config.headerTable();
      Http2FrameSizePolicy frameSizePolicy = config.frameSizePolicy();
      if (pushEnabled != null) {
        if (connection.isServer()) {
          throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Server sending SETTINGS frame with ENABLE_PUSH specified", new Object[0]);
        }
        connection.local().allowPushTo(pushEnabled.booleanValue());
      }
      
      Long maxConcurrentStreams = settings.maxConcurrentStreams();
      if (maxConcurrentStreams != null) {
        int value = (int)Math.min(maxConcurrentStreams.longValue(), 2147483647L);
        connection.remote().maxStreams(value, calculateMaxStreams(value));
      }
      
      Long headerTableSize = settings.headerTableSize();
      if (headerTableSize != null) {
        headerTable.maxHeaderTableSize(headerTableSize.longValue());
      }
      
      Long maxHeaderListSize = settings.maxHeaderListSize();
      if (maxHeaderListSize != null) {
        headerTable.maxHeaderListSize(maxHeaderListSize.longValue());
      }
      
      Integer maxFrameSize = settings.maxFrameSize();
      if (maxFrameSize != null) {
        frameSizePolicy.maxFrameSize(maxFrameSize.intValue());
      }
      
      Integer initialWindowSize = settings.initialWindowSize();
      if (initialWindowSize != null) {
        flowController().initialWindowSize(initialWindowSize.intValue());
      }
    }
    





    private int calculateMaxStreams(int maxConcurrentStreams)
    {
      int maxStreams = maxConcurrentStreams + 100;
      return maxStreams < 0 ? Integer.MAX_VALUE : maxStreams;
    }
    
    public void onSettingsRead(ChannelHandlerContext ctx, Http2Settings settings) throws Http2Exception
    {
      encoder.remoteSettings(settings);
      

      encoder.writeSettingsAck(ctx, ctx.newPromise());
      
      listener.onSettingsRead(ctx, settings);
    }
    

    public void onPingRead(ChannelHandlerContext ctx, ByteBuf data)
      throws Http2Exception
    {
      encoder.writePing(ctx, true, data.retainedSlice(), ctx.newPromise());
      
      listener.onPingRead(ctx, data);
    }
    
    public void onPingAckRead(ChannelHandlerContext ctx, ByteBuf data) throws Http2Exception
    {
      listener.onPingAckRead(ctx, data);
    }
    

    public void onPushPromiseRead(ChannelHandlerContext ctx, int streamId, int promisedStreamId, Http2Headers headers, int padding)
      throws Http2Exception
    {
      if (connection().isServer()) {
        throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "A client cannot push.", new Object[0]);
      }
      
      Http2Stream parentStream = connection.stream(streamId);
      
      if (shouldIgnoreHeadersOrDataFrame(ctx, streamId, parentStream, "PUSH_PROMISE")) {
        return;
      }
      
      if (parentStream == null) {
        throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Stream %d does not exist", new Object[] { Integer.valueOf(streamId) });
      }
      
      switch (DefaultHttp2ConnectionDecoder.1.$SwitchMap$io$netty$handler$codec$http2$Http2Stream$State[parentStream.state().ordinal()])
      {
      case 1: 
      case 2: 
        break;
      
      default: 
        throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Stream %d in unexpected state for receiving push promise: %s", new Object[] { Integer.valueOf(parentStream.id()), parentStream.state() });
      }
      
      

      if (!requestVerifier.isAuthoritative(ctx, headers)) {
        throw Http2Exception.streamError(promisedStreamId, Http2Error.PROTOCOL_ERROR, "Promised request on stream %d for promised stream %d is not authoritative", new Object[] { Integer.valueOf(streamId), Integer.valueOf(promisedStreamId) });
      }
      

      if (!requestVerifier.isCacheable(headers)) {
        throw Http2Exception.streamError(promisedStreamId, Http2Error.PROTOCOL_ERROR, "Promised request on stream %d for promised stream %d is not known to be cacheable", new Object[] { Integer.valueOf(streamId), Integer.valueOf(promisedStreamId) });
      }
      

      if (!requestVerifier.isSafe(headers)) {
        throw Http2Exception.streamError(promisedStreamId, Http2Error.PROTOCOL_ERROR, "Promised request on stream %d for promised stream %d is not known to be safe", new Object[] { Integer.valueOf(streamId), Integer.valueOf(promisedStreamId) });
      }
      



      connection.remote().reservePushStream(promisedStreamId, parentStream);
      
      listener.onPushPromiseRead(ctx, streamId, promisedStreamId, headers, padding);
    }
    
    public void onGoAwayRead(ChannelHandlerContext ctx, int lastStreamId, long errorCode, ByteBuf debugData)
      throws Http2Exception
    {
      onGoAwayRead0(ctx, lastStreamId, errorCode, debugData);
    }
    
    public void onWindowUpdateRead(ChannelHandlerContext ctx, int streamId, int windowSizeIncrement)
      throws Http2Exception
    {
      Http2Stream stream = connection.stream(streamId);
      if ((stream == null) || (stream.state() == Http2Stream.State.CLOSED) || (streamCreatedAfterGoAwaySent(streamId)))
      {
        verifyStreamMayHaveExisted(streamId);
        return;
      }
      

      encoder.flowController().incrementWindowSize(stream, windowSizeIncrement);
      
      listener.onWindowUpdateRead(ctx, streamId, windowSizeIncrement);
    }
    
    public void onUnknownFrame(ChannelHandlerContext ctx, byte frameType, int streamId, Http2Flags flags, ByteBuf payload)
      throws Http2Exception
    {
      onUnknownFrame0(ctx, frameType, streamId, flags, payload);
    }
    



    private boolean shouldIgnoreHeadersOrDataFrame(ChannelHandlerContext ctx, int streamId, Http2Stream stream, String frameName)
      throws Http2Exception
    {
      if (stream == null) {
        if (streamCreatedAfterGoAwaySent(streamId)) {
          DefaultHttp2ConnectionDecoder.logger.info("{} ignoring {} frame for stream {}. Stream sent after GOAWAY sent", new Object[] { ctx.channel(), frameName, Integer.valueOf(streamId) });
          
          return true;
        }
        


        throw Http2Exception.streamError(streamId, Http2Error.STREAM_CLOSED, "Received %s frame for an unknown stream %d", new Object[] { frameName, Integer.valueOf(streamId) });
      }
      if ((stream.isResetSent()) || (streamCreatedAfterGoAwaySent(streamId))) {
        if (DefaultHttp2ConnectionDecoder.logger.isInfoEnabled()) {
          DefaultHttp2ConnectionDecoder.logger.info("{} ignoring {} frame for stream {} {}", new Object[] { ctx.channel(), frameName, "Stream created after GOAWAY sent. Last known stream by peer " + connection.remote().lastStreamKnownByPeer() });
        }
        


        return true;
      }
      return false;
    }
    











    private boolean streamCreatedAfterGoAwaySent(int streamId)
    {
      Http2Connection.Endpoint<?> remote = connection.remote();
      return (connection.goAwaySent()) && (remote.isValidStreamId(streamId)) && (streamId > remote.lastStreamKnownByPeer());
    }
    
    private void verifyStreamMayHaveExisted(int streamId) throws Http2Exception
    {
      if (!connection.streamMayHaveExisted(streamId)) {
        throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Stream %d does not exist", new Object[] { Integer.valueOf(streamId) });
      }
    }
  }
  
  private final class PrefaceFrameListener
    implements Http2FrameListener
  {
    private PrefaceFrameListener() {}
    
    private void verifyPrefaceReceived()
      throws Http2Exception
    {
      if (!prefaceReceived()) {
        throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Received non-SETTINGS as first frame.", new Object[0]);
      }
    }
    
    public int onDataRead(ChannelHandlerContext ctx, int streamId, ByteBuf data, int padding, boolean endOfStream)
      throws Http2Exception
    {
      verifyPrefaceReceived();
      return internalFrameListener.onDataRead(ctx, streamId, data, padding, endOfStream);
    }
    
    public void onHeadersRead(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int padding, boolean endOfStream)
      throws Http2Exception
    {
      verifyPrefaceReceived();
      internalFrameListener.onHeadersRead(ctx, streamId, headers, padding, endOfStream);
    }
    
    public void onHeadersRead(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int streamDependency, short weight, boolean exclusive, int padding, boolean endOfStream)
      throws Http2Exception
    {
      verifyPrefaceReceived();
      internalFrameListener.onHeadersRead(ctx, streamId, headers, streamDependency, weight, exclusive, padding, endOfStream);
    }
    

    public void onPriorityRead(ChannelHandlerContext ctx, int streamId, int streamDependency, short weight, boolean exclusive)
      throws Http2Exception
    {
      verifyPrefaceReceived();
      internalFrameListener.onPriorityRead(ctx, streamId, streamDependency, weight, exclusive);
    }
    
    public void onRstStreamRead(ChannelHandlerContext ctx, int streamId, long errorCode) throws Http2Exception
    {
      verifyPrefaceReceived();
      internalFrameListener.onRstStreamRead(ctx, streamId, errorCode);
    }
    
    public void onSettingsAckRead(ChannelHandlerContext ctx) throws Http2Exception
    {
      verifyPrefaceReceived();
      internalFrameListener.onSettingsAckRead(ctx);
    }
    

    public void onSettingsRead(ChannelHandlerContext ctx, Http2Settings settings)
      throws Http2Exception
    {
      if (!prefaceReceived()) {
        internalFrameListener = new DefaultHttp2ConnectionDecoder.FrameReadListener(DefaultHttp2ConnectionDecoder.this, null);
      }
      internalFrameListener.onSettingsRead(ctx, settings);
    }
    
    public void onPingRead(ChannelHandlerContext ctx, ByteBuf data) throws Http2Exception
    {
      verifyPrefaceReceived();
      internalFrameListener.onPingRead(ctx, data);
    }
    
    public void onPingAckRead(ChannelHandlerContext ctx, ByteBuf data) throws Http2Exception
    {
      verifyPrefaceReceived();
      internalFrameListener.onPingAckRead(ctx, data);
    }
    
    public void onPushPromiseRead(ChannelHandlerContext ctx, int streamId, int promisedStreamId, Http2Headers headers, int padding)
      throws Http2Exception
    {
      verifyPrefaceReceived();
      internalFrameListener.onPushPromiseRead(ctx, streamId, promisedStreamId, headers, padding);
    }
    
    public void onGoAwayRead(ChannelHandlerContext ctx, int lastStreamId, long errorCode, ByteBuf debugData)
      throws Http2Exception
    {
      onGoAwayRead0(ctx, lastStreamId, errorCode, debugData);
    }
    
    public void onWindowUpdateRead(ChannelHandlerContext ctx, int streamId, int windowSizeIncrement)
      throws Http2Exception
    {
      verifyPrefaceReceived();
      internalFrameListener.onWindowUpdateRead(ctx, streamId, windowSizeIncrement);
    }
    
    public void onUnknownFrame(ChannelHandlerContext ctx, byte frameType, int streamId, Http2Flags flags, ByteBuf payload)
      throws Http2Exception
    {
      onUnknownFrame0(ctx, frameType, streamId, flags, payload);
    }
  }
}
