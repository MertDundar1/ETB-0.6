package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.CoalescingBufferQueue;
import io.netty.util.internal.ObjectUtil;
import java.util.ArrayDeque;




























public class DefaultHttp2ConnectionEncoder
  implements Http2ConnectionEncoder
{
  private final Http2FrameWriter frameWriter;
  private final Http2Connection connection;
  private Http2LifecycleManager lifecycleManager;
  private final ArrayDeque<Http2Settings> outstandingLocalSettingsQueue = new ArrayDeque(4);
  
  public DefaultHttp2ConnectionEncoder(Http2Connection connection, Http2FrameWriter frameWriter) {
    this.connection = ((Http2Connection)ObjectUtil.checkNotNull(connection, "connection"));
    this.frameWriter = ((Http2FrameWriter)ObjectUtil.checkNotNull(frameWriter, "frameWriter"));
    if (connection.remote().flowController() == null) {
      connection.remote().flowController(new DefaultHttp2RemoteFlowController(connection));
    }
  }
  
  public void lifecycleManager(Http2LifecycleManager lifecycleManager)
  {
    this.lifecycleManager = ((Http2LifecycleManager)ObjectUtil.checkNotNull(lifecycleManager, "lifecycleManager"));
  }
  
  public Http2FrameWriter frameWriter()
  {
    return frameWriter;
  }
  
  public Http2Connection connection()
  {
    return connection;
  }
  
  public final Http2RemoteFlowController flowController()
  {
    return (Http2RemoteFlowController)connection().remote().flowController();
  }
  
  public void remoteSettings(Http2Settings settings) throws Http2Exception
  {
    Boolean pushEnabled = settings.pushEnabled();
    Http2FrameWriter.Configuration config = configuration();
    Http2HeaderTable outboundHeaderTable = config.headerTable();
    Http2FrameSizePolicy outboundFrameSizePolicy = config.frameSizePolicy();
    if (pushEnabled != null) {
      if ((!connection.isServer()) && (pushEnabled.booleanValue())) {
        throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Client received a value of ENABLE_PUSH specified to other than 0", new Object[0]);
      }
      
      connection.remote().allowPushTo(pushEnabled.booleanValue());
    }
    
    Long maxConcurrentStreams = settings.maxConcurrentStreams();
    if (maxConcurrentStreams != null)
    {
      connection.local().maxStreams((int)Math.min(maxConcurrentStreams.longValue(), 2147483647L), Integer.MAX_VALUE);
    }
    
    Long headerTableSize = settings.headerTableSize();
    if (headerTableSize != null) {
      outboundHeaderTable.maxHeaderTableSize((int)Math.min(headerTableSize.longValue(), 2147483647L));
    }
    
    Long maxHeaderListSize = settings.maxHeaderListSize();
    if (maxHeaderListSize != null) {
      outboundHeaderTable.maxHeaderListSize(maxHeaderListSize.longValue());
    }
    
    Integer maxFrameSize = settings.maxFrameSize();
    if (maxFrameSize != null) {
      outboundFrameSizePolicy.maxFrameSize(maxFrameSize.intValue());
    }
    
    Integer initialWindowSize = settings.initialWindowSize();
    if (initialWindowSize != null) {
      flowController().initialWindowSize(initialWindowSize.intValue());
    }
  }
  

  public ChannelFuture writeData(ChannelHandlerContext ctx, int streamId, ByteBuf data, int padding, boolean endOfStream, ChannelPromise promise)
  {
    try
    {
      Http2Stream stream = requireStream(streamId);
      

      switch (2.$SwitchMap$io$netty$handler$codec$http2$Http2Stream$State[stream.state().ordinal()])
      {
      case 1: 
      case 2: 
        break;
      default: 
        throw new IllegalStateException(String.format("Stream %d in unexpected state: %s", new Object[] { Integer.valueOf(stream.id()), stream.state() }));
      }
    }
    catch (Throwable e) {
      data.release();
      return promise.setFailure(e);
    }
    
    Http2Stream stream;
    flowController().addFlowControlled(stream, new FlowControlledData(stream, data, padding, endOfStream, promise));
    
    return promise;
  }
  

  public ChannelFuture writeHeaders(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int padding, boolean endStream, ChannelPromise promise)
  {
    return writeHeaders(ctx, streamId, headers, 0, (short)16, false, padding, endStream, promise);
  }
  

  public ChannelFuture writeHeaders(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int streamDependency, short weight, boolean exclusive, int padding, boolean endOfStream, ChannelPromise promise)
  {
    try
    {
      Http2Stream stream = connection.stream(streamId);
      if (stream == null) {
        stream = connection.local().createStream(streamId, endOfStream);
      } else {
        switch (2.$SwitchMap$io$netty$handler$codec$http2$Http2Stream$State[stream.state().ordinal()]) {
        case 3: 
          stream.open(endOfStream);
          break;
        case 1: 
        case 2: 
          break;
        
        default: 
          throw new IllegalStateException(String.format("Stream %d in unexpected state: %s", new Object[] { Integer.valueOf(stream.id()), stream.state() }));
        }
        
      }
      


      Http2RemoteFlowController flowController = flowController();
      if ((!endOfStream) || (!flowController.hasFlowControlled(stream))) {
        if (endOfStream) {
          final Http2Stream finalStream = stream;
          ChannelFutureListener closeStreamLocalListener = new ChannelFutureListener()
          {
            public void operationComplete(ChannelFuture future) throws Exception {
              lifecycleManager.closeStreamLocal(finalStream, future);
            }
          };
          promise = promise.unvoid().addListener(closeStreamLocalListener);
        }
        return frameWriter.writeHeaders(ctx, streamId, headers, streamDependency, weight, exclusive, padding, endOfStream, promise);
      }
      

      flowController.addFlowControlled(stream, new FlowControlledHeaders(stream, headers, streamDependency, weight, exclusive, padding, endOfStream, promise));
      

      return promise;
    }
    catch (Http2NoMoreStreamIdsException e) {
      lifecycleManager.onError(ctx, e);
      return promise.setFailure(e);
    } catch (Throwable e) {
      return promise.setFailure(e);
    }
  }
  

  public ChannelFuture writePriority(ChannelHandlerContext ctx, int streamId, int streamDependency, short weight, boolean exclusive, ChannelPromise promise)
  {
    try
    {
      Http2Stream stream = connection.stream(streamId);
      if (stream == null) {
        stream = connection.local().createIdleStream(streamId);
      }
      


      stream.setPriority(streamDependency, weight, exclusive);

    }
    catch (Http2Exception.ClosedStreamCreationException localClosedStreamCreationException) {}catch (Throwable t)
    {
      return promise.setFailure(t);
    }
    
    return frameWriter.writePriority(ctx, streamId, streamDependency, weight, exclusive, promise);
  }
  


  public ChannelFuture writeRstStream(ChannelHandlerContext ctx, int streamId, long errorCode, ChannelPromise promise)
  {
    return lifecycleManager.resetStream(ctx, streamId, errorCode, promise);
  }
  

  public ChannelFuture writeSettings(ChannelHandlerContext ctx, Http2Settings settings, ChannelPromise promise)
  {
    outstandingLocalSettingsQueue.add(settings);
    try {
      Boolean pushEnabled = settings.pushEnabled();
      if ((pushEnabled != null) && (connection.isServer())) {
        throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Server sending SETTINGS frame with ENABLE_PUSH specified", new Object[0]);
      }
    } catch (Throwable e) {
      return promise.setFailure(e);
    }
    
    return frameWriter.writeSettings(ctx, settings, promise);
  }
  
  public ChannelFuture writeSettingsAck(ChannelHandlerContext ctx, ChannelPromise promise)
  {
    return frameWriter.writeSettingsAck(ctx, promise);
  }
  
  public ChannelFuture writePing(ChannelHandlerContext ctx, boolean ack, ByteBuf data, ChannelPromise promise)
  {
    return frameWriter.writePing(ctx, ack, data, promise);
  }
  
  public ChannelFuture writePushPromise(ChannelHandlerContext ctx, int streamId, int promisedStreamId, Http2Headers headers, int padding, ChannelPromise promise)
  {
    try
    {
      if (connection.goAwayReceived()) {
        throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Sending PUSH_PROMISE after GO_AWAY received.", new Object[0]);
      }
      
      Http2Stream stream = requireStream(streamId);
      
      connection.local().reservePushStream(promisedStreamId, stream);
    } catch (Throwable e) {
      return promise.setFailure(e);
    }
    
    return frameWriter.writePushPromise(ctx, streamId, promisedStreamId, headers, padding, promise);
  }
  

  public ChannelFuture writeGoAway(ChannelHandlerContext ctx, int lastStreamId, long errorCode, ByteBuf debugData, ChannelPromise promise)
  {
    return lifecycleManager.goAway(ctx, lastStreamId, errorCode, debugData, promise);
  }
  

  public ChannelFuture writeWindowUpdate(ChannelHandlerContext ctx, int streamId, int windowSizeIncrement, ChannelPromise promise)
  {
    return promise.setFailure(new UnsupportedOperationException("Use the Http2[Inbound|Outbound]FlowController objects to control window sizes"));
  }
  


  public ChannelFuture writeFrame(ChannelHandlerContext ctx, byte frameType, int streamId, Http2Flags flags, ByteBuf payload, ChannelPromise promise)
  {
    return frameWriter.writeFrame(ctx, frameType, streamId, flags, payload, promise);
  }
  
  public void close()
  {
    frameWriter.close();
  }
  
  public Http2Settings pollSentSettings()
  {
    return (Http2Settings)outstandingLocalSettingsQueue.poll();
  }
  
  public Http2FrameWriter.Configuration configuration()
  {
    return frameWriter.configuration();
  }
  
  private Http2Stream requireStream(int streamId) {
    Http2Stream stream = connection.stream(streamId);
    if (stream == null) { String message;
      String message;
      if (connection.streamMayHaveExisted(streamId)) {
        message = "Stream no longer exists: " + streamId;
      } else {
        message = "Stream does not exist: " + streamId;
      }
      throw new IllegalArgumentException(message);
    }
    return stream;
  }
  



  private final class FlowControlledData
    extends DefaultHttp2ConnectionEncoder.FlowControlledBase
  {
    private final CoalescingBufferQueue queue;
    

    private int dataSize;
    


    FlowControlledData(Http2Stream stream, ByteBuf buf, int padding, boolean endOfStream, ChannelPromise promise)
    {
      super(stream, padding, endOfStream, promise);
      queue = new CoalescingBufferQueue(promise.channel());
      queue.add(buf, promise);
      dataSize = queue.readableBytes();
    }
    
    public int size()
    {
      return dataSize + padding;
    }
    
    public void error(ChannelHandlerContext ctx, Throwable cause)
    {
      queue.releaseAndFailAll(cause);
      

      lifecycleManager.onError(ctx, cause);
    }
    
    public void write(ChannelHandlerContext ctx, int allowedBytes)
    {
      int queuedData = queue.readableBytes();
      if (!endOfStream) {
        if (queuedData == 0)
        {

          ChannelPromise writePromise = ctx.newPromise().addListener(this);
          queue.remove(0, writePromise).release();
          ctx.write(Unpooled.EMPTY_BUFFER, writePromise);
          return;
        }
        
        if (allowedBytes == 0) {
          return;
        }
      }
      

      int writeableData = Math.min(queuedData, allowedBytes);
      ChannelPromise writePromise = ctx.newPromise().addListener(this);
      ByteBuf toWrite = queue.remove(writeableData, writePromise);
      dataSize = queue.readableBytes();
      

      int writeablePadding = Math.min(allowedBytes - writeableData, padding);
      padding -= writeablePadding;
      

      frameWriter().writeData(ctx, stream.id(), toWrite, writeablePadding, (endOfStream) && (size() == 0), writePromise);
    }
    

    public boolean merge(ChannelHandlerContext ctx, Http2RemoteFlowController.FlowControlled next)
    {
      FlowControlledData nextData;
      if ((FlowControlledData.class != next.getClass()) || (Integer.MAX_VALUE - (nextData = (FlowControlledData)next).size() < size()))
      {
        return false; }
      FlowControlledData nextData;
      queue.copyTo(queue);
      dataSize = queue.readableBytes();
      
      padding = Math.max(padding, padding);
      endOfStream = endOfStream;
      return true;
    }
  }
  

  private final class FlowControlledHeaders
    extends DefaultHttp2ConnectionEncoder.FlowControlledBase
  {
    private final Http2Headers headers;
    
    private final int streamDependency;
    
    private final short weight;
    private final boolean exclusive;
    
    FlowControlledHeaders(Http2Stream stream, Http2Headers headers, int streamDependency, short weight, boolean exclusive, int padding, boolean endOfStream, ChannelPromise promise)
    {
      super(stream, padding, endOfStream, promise);
      this.headers = headers;
      this.streamDependency = streamDependency;
      this.weight = weight;
      this.exclusive = exclusive;
    }
    
    public int size()
    {
      return 0;
    }
    
    public void error(ChannelHandlerContext ctx, Throwable cause)
    {
      if (ctx != null) {
        lifecycleManager.onError(ctx, cause);
      }
      promise.tryFailure(cause);
    }
    
    public void write(ChannelHandlerContext ctx, int allowedBytes)
    {
      if (promise.isVoid()) {
        promise = ctx.newPromise();
      }
      promise.addListener(this);
      
      frameWriter.writeHeaders(ctx, stream.id(), headers, streamDependency, weight, exclusive, padding, endOfStream, promise);
    }
    

    public boolean merge(ChannelHandlerContext ctx, Http2RemoteFlowController.FlowControlled next)
    {
      return false;
    }
  }
  

  public abstract class FlowControlledBase
    implements Http2RemoteFlowController.FlowControlled, ChannelFutureListener
  {
    protected final Http2Stream stream;
    
    protected ChannelPromise promise;
    protected boolean endOfStream;
    protected int padding;
    
    protected FlowControlledBase(Http2Stream stream, int padding, boolean endOfStream, ChannelPromise promise)
    {
      if (padding < 0) {
        throw new IllegalArgumentException("padding must be >= 0");
      }
      this.padding = padding;
      this.endOfStream = endOfStream;
      this.stream = stream;
      this.promise = promise;
    }
    
    public void writeComplete()
    {
      if (endOfStream) {
        lifecycleManager.closeStreamLocal(stream, promise);
      }
    }
    
    public void operationComplete(ChannelFuture future) throws Exception
    {
      if (!future.isSuccess()) {
        error(flowController().channelHandlerContext(), future.cause());
      }
    }
  }
}
