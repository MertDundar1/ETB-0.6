package io.netty.handler.codec.http2;

import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.BooleanSupplier;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.ArrayDeque;
import java.util.Deque;




























public class DefaultHttp2RemoteFlowController
  implements Http2RemoteFlowController
{
  private static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultHttp2RemoteFlowController.class);
  
  private static final int MIN_WRITABLE_CHUNK = 32768;
  private final Http2Connection connection;
  private final Http2Connection.PropertyKey stateKey;
  private final StreamByteDistributor streamByteDistributor;
  private final FlowState connectionState;
  private int initialWindowSize = 65535;
  private WritabilityMonitor monitor;
  private ChannelHandlerContext ctx;
  
  public DefaultHttp2RemoteFlowController(Http2Connection connection) {
    this(connection, (Http2RemoteFlowController.Listener)null);
  }
  
  public DefaultHttp2RemoteFlowController(Http2Connection connection, StreamByteDistributor streamByteDistributor)
  {
    this(connection, streamByteDistributor, null);
  }
  
  public DefaultHttp2RemoteFlowController(Http2Connection connection, Http2RemoteFlowController.Listener listener) {
    this(connection, new WeightedFairQueueByteDistributor(connection), listener);
  }
  

  public DefaultHttp2RemoteFlowController(Http2Connection connection, StreamByteDistributor streamByteDistributor, Http2RemoteFlowController.Listener listener)
  {
    this.connection = ((Http2Connection)ObjectUtil.checkNotNull(connection, "connection"));
    this.streamByteDistributor = ((StreamByteDistributor)ObjectUtil.checkNotNull(streamByteDistributor, "streamWriteDistributor"));
    

    stateKey = connection.newKey();
    connectionState = new FlowState(connection.connectionStream());
    connection.connectionStream().setProperty(stateKey, connectionState);
    

    listener(listener);
    monitor.windowSize(connectionState, initialWindowSize);
    

    connection.addListener(new Http2ConnectionAdapter()
    {

      public void onStreamAdded(Http2Stream stream)
      {
        stream.setProperty(stateKey, new DefaultHttp2RemoteFlowController.FlowState(DefaultHttp2RemoteFlowController.this, stream));
      }
      


      public void onStreamActive(Http2Stream stream)
      {
        monitor.windowSize(DefaultHttp2RemoteFlowController.this.state(stream), initialWindowSize);
      }
      


      public void onStreamClosed(Http2Stream stream)
      {
        DefaultHttp2RemoteFlowController.this.state(stream).cancel();
      }
      
      public void onStreamHalfClosed(Http2Stream stream)
      {
        if (Http2Stream.State.HALF_CLOSED_LOCAL.equals(stream.state()))
        {










          DefaultHttp2RemoteFlowController.this.state(stream).cancel();
        }
      }
    });
  }
  




  public void channelHandlerContext(ChannelHandlerContext ctx)
    throws Http2Exception
  {
    this.ctx = ((ChannelHandlerContext)ObjectUtil.checkNotNull(ctx, "ctx"));
    


    channelWritabilityChanged();
    




    if (isChannelWritable()) {
      writePendingBytes();
    }
  }
  
  public ChannelHandlerContext channelHandlerContext()
  {
    return ctx;
  }
  
  public void initialWindowSize(int newWindowSize) throws Http2Exception
  {
    assert ((ctx == null) || (ctx.executor().inEventLoop()));
    monitor.initialWindowSize(newWindowSize);
  }
  
  public int initialWindowSize()
  {
    return initialWindowSize;
  }
  
  public int windowSize(Http2Stream stream)
  {
    return state(stream).windowSize();
  }
  
  public boolean isWritable(Http2Stream stream)
  {
    return monitor.isWritable(state(stream));
  }
  
  public void channelWritabilityChanged() throws Http2Exception
  {
    monitor.channelWritabilityChange();
  }
  
  private boolean isChannelWritable() {
    return (ctx != null) && (isChannelWritable0());
  }
  
  private boolean isChannelWritable0() {
    return ctx.channel().isWritable();
  }
  
  public void listener(Http2RemoteFlowController.Listener listener)
  {
    monitor = (listener == null ? new WritabilityMonitor(null) : new ListenerWritabilityMonitor(listener));
  }
  
  public void incrementWindowSize(Http2Stream stream, int delta) throws Http2Exception
  {
    assert ((ctx == null) || (ctx.executor().inEventLoop()));
    monitor.incrementWindowSize(state(stream), delta);
  }
  

  public void addFlowControlled(Http2Stream stream, Http2RemoteFlowController.FlowControlled frame)
  {
    assert ((ctx == null) || (ctx.executor().inEventLoop()));
    ObjectUtil.checkNotNull(frame, "frame");
    try {
      monitor.enqueueFrame(state(stream), frame);
    } catch (Throwable t) {
      frame.error(ctx, t);
    }
  }
  
  public boolean hasFlowControlled(Http2Stream stream)
  {
    return state(stream).hasFrame();
  }
  
  private FlowState state(Http2Stream stream) {
    return (FlowState)stream.getProperty(stateKey);
  }
  


  private int connectionWindowSize()
  {
    return connectionState.windowSize();
  }
  





  private int minUsableChannelBytes()
  {
    return Math.max(ctx.channel().config().getWriteBufferLowWaterMark(), 32768);
  }
  
  private int maxUsableChannelBytes()
  {
    int channelWritableBytes = (int)Math.min(2147483647L, ctx.channel().bytesBeforeUnwritable());
    int useableBytes = channelWritableBytes > 0 ? Math.max(channelWritableBytes, minUsableChannelBytes()) : 0;
    

    return Math.min(connectionState.windowSize(), useableBytes);
  }
  



  private int writableBytes()
  {
    return Math.min(connectionWindowSize(), maxUsableChannelBytes());
  }
  
  public void writePendingBytes() throws Http2Exception
  {
    monitor.writePendingBytes();
  }
  

  private final class FlowState
    implements StreamByteDistributor.StreamState
  {
    private final Http2Stream stream;
    
    private final Deque<Http2RemoteFlowController.FlowControlled> pendingWriteQueue;
    
    private int window;
    
    private int pendingBytes;
    
    private boolean markedWritable;
    
    private boolean writing;
    
    private boolean cancelled;
    
    private BooleanSupplier isWritableSupplier = new BooleanSupplier()
    {
      public boolean get() throws Exception {
        return windowSize() > pendingBytes();
      }
    };
    
    FlowState(Http2Stream stream) {
      this.stream = stream;
      pendingWriteQueue = new ArrayDeque(2);
    }
    


    boolean isWritable()
    {
      try
      {
        return isWritableSupplier.get();
      } catch (Throwable cause) {
        throw new Error("isWritableSupplier should never throw!", cause);
      }
    }
    



    public Http2Stream stream()
    {
      return stream;
    }
    


    boolean markedWritability()
    {
      return markedWritable;
    }
    


    void markedWritability(boolean isWritable)
    {
      markedWritable = isWritable;
    }
    
    public int windowSize()
    {
      return window;
    }
    


    void windowSize(int initialWindowSize)
    {
      window = initialWindowSize;
    }
    



    int writeAllocatedBytes(int allocated)
    {
      int initialAllocated = allocated;
      

      Throwable cause = null;
      int writtenBytes;
      try {
        assert (!writing);
        writing = true;
        

        boolean writeOccurred = false;
        Http2RemoteFlowController.FlowControlled frame; int maxBytes; while ((!cancelled) && ((frame = peek()) != null)) {
          maxBytes = Math.min(allocated, writableWindow());
          if ((maxBytes <= 0) && (frame.size() > 0)) {
            break;
          }
          

          writeOccurred = true;
          int initialFrameSize = frame.size();
          try {
            frame.write(ctx, Math.max(0, maxBytes));
            if (frame.size() == 0)
            {


              pendingWriteQueue.remove();
              frame.writeComplete();
            }
          }
          finally {
            allocated -= initialFrameSize - frame.size();
          }
        }
        
        if (!writeOccurred) {
          int writtenBytes;
          return -1;
        }
      }
      catch (Throwable t) {
        int writtenBytes;
        cancelled = true;
        cause = t;
      } finally { int writtenBytes;
        writing = false;
        

        writtenBytes = initialAllocated - allocated;
        
        decrementPendingBytes(writtenBytes, false);
        decrementFlowControlWindow(writtenBytes);
        


        if (cancelled) {
          cancel(cause);
        }
      }
      return writtenBytes;
    }
    

    int incrementStreamWindow(int delta)
      throws Http2Exception
    {
      if ((delta > 0) && (Integer.MAX_VALUE - delta < window)) {
        throw Http2Exception.streamError(stream.id(), Http2Error.FLOW_CONTROL_ERROR, "Window size overflow for stream: %d", new Object[] { Integer.valueOf(stream.id()) });
      }
      
      window += delta;
      
      streamByteDistributor.updateStreamableBytes(this);
      return window;
    }
    


    private int writableWindow()
    {
      return Math.min(window, DefaultHttp2RemoteFlowController.this.connectionWindowSize());
    }
    
    public int pendingBytes()
    {
      return pendingBytes;
    }
    


    void enqueueFrame(Http2RemoteFlowController.FlowControlled frame)
    {
      Http2RemoteFlowController.FlowControlled last = (Http2RemoteFlowController.FlowControlled)pendingWriteQueue.peekLast();
      if (last == null) {
        enqueueFrameWithoutMerge(frame);
        return;
      }
      
      int lastSize = last.size();
      if (last.merge(ctx, frame)) {
        incrementPendingBytes(last.size() - lastSize, true);
        return;
      }
      enqueueFrameWithoutMerge(frame);
    }
    
    private void enqueueFrameWithoutMerge(Http2RemoteFlowController.FlowControlled frame) {
      pendingWriteQueue.offer(frame);
      

      incrementPendingBytes(frame.size(), true);
    }
    
    public boolean hasFrame()
    {
      return !pendingWriteQueue.isEmpty();
    }
    


    private Http2RemoteFlowController.FlowControlled peek()
    {
      return (Http2RemoteFlowController.FlowControlled)pendingWriteQueue.peek();
    }
    


    void cancel()
    {
      cancel(null);
    }
    



    private void cancel(Throwable cause)
    {
      cancelled = true;
      
      if (writing) {
        return;
      }
      for (;;)
      {
        Http2RemoteFlowController.FlowControlled frame = (Http2RemoteFlowController.FlowControlled)pendingWriteQueue.poll();
        if (frame == null) {
          break;
        }
        writeError(frame, Http2Exception.streamError(stream.id(), Http2Error.INTERNAL_ERROR, cause, "Stream closed before write could take place", new Object[0]));
      }
      

      streamByteDistributor.updateStreamableBytes(this);
      
      isWritableSupplier = BooleanSupplier.FALSE_SUPPLIER;
      monitor.stateCancelled(this);
    }
    



    private void incrementPendingBytes(int numBytes, boolean updateStreamableBytes)
    {
      pendingBytes += numBytes;
      monitor.incrementPendingBytes(numBytes);
      if (updateStreamableBytes) {
        streamByteDistributor.updateStreamableBytes(this);
      }
    }
    


    private void decrementPendingBytes(int bytes, boolean updateStreamableBytes)
    {
      incrementPendingBytes(-bytes, updateStreamableBytes);
    }
    

    private void decrementFlowControlWindow(int bytes)
    {
      try
      {
        int negativeBytes = -bytes;
        connectionState.incrementStreamWindow(negativeBytes);
        incrementStreamWindow(negativeBytes);
      }
      catch (Http2Exception e) {
        throw new IllegalStateException("Invalid window state when writing frame: " + e.getMessage(), e);
      }
    }
    



    private void writeError(Http2RemoteFlowController.FlowControlled frame, Http2Exception cause)
    {
      assert (ctx != null);
      decrementPendingBytes(frame.size(), true);
      frame.error(ctx, cause);
    }
  }
  

  private class WritabilityMonitor
  {
    private boolean inWritePendingBytes;
    
    private long totalPendingBytes;
    private final StreamByteDistributor.Writer writer = new StreamByteDistributor.Writer()
    {
      public void write(Http2Stream stream, int numBytes) {
        DefaultHttp2RemoteFlowController.this.state(stream).writeAllocatedBytes(numBytes);
      }
    };
    


    private WritabilityMonitor() {}
    


    void channelWritabilityChange()
      throws Http2Exception
    {}
    


    void stateCancelled(DefaultHttp2RemoteFlowController.FlowState state) {}
    


    void windowSize(DefaultHttp2RemoteFlowController.FlowState state, int initialWindowSize)
    {
      state.windowSize(initialWindowSize);
    }
    




    void incrementWindowSize(DefaultHttp2RemoteFlowController.FlowState state, int delta)
      throws Http2Exception
    {
      state.incrementStreamWindow(delta);
    }
    




    void enqueueFrame(DefaultHttp2RemoteFlowController.FlowState state, Http2RemoteFlowController.FlowControlled frame)
      throws Http2Exception
    {
      state.enqueueFrame(frame);
    }
    




    final void incrementPendingBytes(int delta)
    {
      totalPendingBytes += delta;
    }
    







    final boolean isWritable(DefaultHttp2RemoteFlowController.FlowState state)
    {
      return (isWritableConnection()) && (state.isWritable());
    }
    



    final void writePendingBytes()
      throws Http2Exception
    {
      if (inWritePendingBytes) {
        return;
      }
      inWritePendingBytes = true;
      try {
        int bytesToWrite = DefaultHttp2RemoteFlowController.this.writableBytes();
        


        while ((streamByteDistributor.distribute(bytesToWrite, writer)) && ((bytesToWrite = DefaultHttp2RemoteFlowController.this.writableBytes()) > 0)) { if (!DefaultHttp2RemoteFlowController.this.isChannelWritable0()) {
            break;
          }
        }
      }
      finally
      {
        inWritePendingBytes = false;
      }
    }
    
    void initialWindowSize(int newWindowSize) throws Http2Exception {
      if (newWindowSize < 0) {
        throw new IllegalArgumentException("Invalid initial window size: " + newWindowSize);
      }
      
      final int delta = newWindowSize - initialWindowSize;
      initialWindowSize = newWindowSize;
      connection.forEachActiveStream(new Http2StreamVisitor()
      {
        public boolean visit(Http2Stream stream) throws Http2Exception {
          DefaultHttp2RemoteFlowController.this.state(stream).incrementStreamWindow(delta);
          return true;
        }
      });
      
      if ((delta > 0) && (DefaultHttp2RemoteFlowController.this.isChannelWritable()))
      {
        writePendingBytes();
      }
    }
    
    final boolean isWritableConnection() {
      return (connectionState.windowSize() - totalPendingBytes > 0L) && (DefaultHttp2RemoteFlowController.this.isChannelWritable());
    }
  }
  



  private final class ListenerWritabilityMonitor
    extends DefaultHttp2RemoteFlowController.WritabilityMonitor
  {
    private final Http2RemoteFlowController.Listener listener;
    


    private final Http2StreamVisitor checkStreamWritabilityVisitor = new Http2StreamVisitor()
    {
      public boolean visit(Http2Stream stream) throws Http2Exception {
        DefaultHttp2RemoteFlowController.FlowState state = DefaultHttp2RemoteFlowController.this.state(stream);
        if (isWritable(state) != state.markedWritability()) {
          DefaultHttp2RemoteFlowController.ListenerWritabilityMonitor.this.notifyWritabilityChanged(state);
        }
        return true;
      }
    };
    
    ListenerWritabilityMonitor(Http2RemoteFlowController.Listener listener) { super(null);
      this.listener = listener;
    }
    
    void windowSize(DefaultHttp2RemoteFlowController.FlowState state, int initialWindowSize)
    {
      super.windowSize(state, initialWindowSize);
      try {
        checkStateWritability(state);
      } catch (Http2Exception e) {
        throw new RuntimeException("Caught unexpected exception from window", e);
      }
    }
    
    void incrementWindowSize(DefaultHttp2RemoteFlowController.FlowState state, int delta) throws Http2Exception
    {
      super.incrementWindowSize(state, delta);
      checkStateWritability(state);
    }
    
    void initialWindowSize(int newWindowSize) throws Http2Exception
    {
      super.initialWindowSize(newWindowSize);
      if (isWritableConnection())
      {

        checkAllWritabilityChanged();
      }
    }
    
    void enqueueFrame(DefaultHttp2RemoteFlowController.FlowState state, Http2RemoteFlowController.FlowControlled frame) throws Http2Exception
    {
      super.enqueueFrame(state, frame);
      checkConnectionThenStreamWritabilityChanged(state);
    }
    
    void stateCancelled(DefaultHttp2RemoteFlowController.FlowState state)
    {
      try {
        checkConnectionThenStreamWritabilityChanged(state);
      } catch (Http2Exception e) {
        throw new RuntimeException("Caught unexpected exception from checkAllWritabilityChanged", e);
      }
    }
    
    void channelWritabilityChange() throws Http2Exception
    {
      if (connectionState.markedWritability() != DefaultHttp2RemoteFlowController.this.isChannelWritable()) {
        checkAllWritabilityChanged();
      }
    }
    
    private void checkStateWritability(DefaultHttp2RemoteFlowController.FlowState state) throws Http2Exception {
      if (isWritable(state) != state.markedWritability()) {
        if (state == connectionState) {
          checkAllWritabilityChanged();
        } else {
          notifyWritabilityChanged(state);
        }
      }
    }
    
    private void notifyWritabilityChanged(DefaultHttp2RemoteFlowController.FlowState state) {
      state.markedWritability(!state.markedWritability());
      try {
        listener.writabilityChanged(DefaultHttp2RemoteFlowController.FlowState.access$1400(state));
      } catch (Throwable cause) {
        DefaultHttp2RemoteFlowController.logger.error("Caught Throwable from listener.writabilityChanged", cause);
      }
    }
    
    private void checkConnectionThenStreamWritabilityChanged(DefaultHttp2RemoteFlowController.FlowState state) throws Http2Exception
    {
      if (isWritableConnection() != connectionState.markedWritability()) {
        checkAllWritabilityChanged();
      } else if (isWritable(state) != state.markedWritability()) {
        notifyWritabilityChanged(state);
      }
    }
    
    private void checkAllWritabilityChanged() throws Http2Exception
    {
      connectionState.markedWritability(isWritableConnection());
      connection.forEachActiveStream(checkStreamWritabilityVisitor);
    }
  }
}
