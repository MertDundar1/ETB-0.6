package io.netty.handler.codec.http2;

import io.netty.util.internal.MathUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PriorityQueue;
import io.netty.util.internal.PriorityQueueNode;
import java.util.Queue;








































public final class WeightedFairQueueByteDistributor
  implements StreamByteDistributor
{
  private final Http2Connection.PropertyKey stateKey;
  private final State connectionState;
  private int allocationQuantum = 1024;
  
  public WeightedFairQueueByteDistributor(Http2Connection connection) {
    stateKey = connection.newKey();
    Http2Stream connectionStream = connection.connectionStream();
    connectionStream.setProperty(stateKey, this.connectionState = new State(connectionStream, 16));
    

    connection.addListener(new Http2ConnectionAdapter()
    {
      public void onStreamAdded(Http2Stream stream) {
        stream.setProperty(stateKey, new WeightedFairQueueByteDistributor.State(WeightedFairQueueByteDistributor.this, stream));
      }
      
      public void onWeightChanged(Http2Stream stream, short oldWeight)
      {
        Http2Stream parent;
        if ((stateactiveCountForTree != 0) && ((parent = stream.parent()) != null)) {
          statetotalQueuedWeights += stream.weight() - oldWeight;
        }
      }
      
      public void onStreamClosed(Http2Stream stream)
      {
        WeightedFairQueueByteDistributor.this.state(stream).close();
      }
      
      public void onPriorityTreeParentChanged(Http2Stream stream, Http2Stream oldParent)
      {
        Http2Stream parent = stream.parent();
        if (parent != null) {
          WeightedFairQueueByteDistributor.State state = WeightedFairQueueByteDistributor.this.state(stream);
          if (activeCountForTree != 0) {
            WeightedFairQueueByteDistributor.State pState = WeightedFairQueueByteDistributor.this.state(parent);
            pState.offerAndInitializePseudoTime(state);
            pState.isActiveCountChangeForTree(activeCountForTree);
          }
        }
      }
      
      public void onPriorityTreeParentChanging(Http2Stream stream, Http2Stream newParent)
      {
        Http2Stream parent = stream.parent();
        if (parent != null) {
          WeightedFairQueueByteDistributor.State state = WeightedFairQueueByteDistributor.this.state(stream);
          if (activeCountForTree != 0) {
            WeightedFairQueueByteDistributor.State pState = WeightedFairQueueByteDistributor.this.state(parent);
            pState.remove(state);
            pState.isActiveCountChangeForTree(-activeCountForTree);
          }
        }
      }
    });
  }
  
  public void updateStreamableBytes(StreamByteDistributor.StreamState state)
  {
    state(state.stream()).updateStreamableBytes(Http2CodecUtil.streamableBytes(state), (state.hasFrame()) && (state.windowSize() >= 0));
  }
  
  public boolean distribute(int maxBytes, StreamByteDistributor.Writer writer)
    throws Http2Exception
  {
    ObjectUtil.checkNotNull(writer, "writer");
    

    if (connectionState.activeCountForTree == 0) {
      return false;
    }
    

    int oldIsActiveCountForTree;
    
    do
    {
      oldIsActiveCountForTree = connectionState.activeCountForTree;
      
      maxBytes -= distributeToChildren(maxBytes, writer, connectionState);
    } while ((connectionState.activeCountForTree != 0) && ((maxBytes > 0) || (oldIsActiveCountForTree != connectionState.activeCountForTree)));
    

    return connectionState.activeCountForTree != 0;
  }
  



  public void allocationQuantum(int allocationQuantum)
  {
    if (allocationQuantum <= 0) {
      throw new IllegalArgumentException("allocationQuantum must be > 0");
    }
    this.allocationQuantum = allocationQuantum;
  }
  
  private int distribute(int maxBytes, StreamByteDistributor.Writer writer, State state) throws Http2Exception {
    if (active) {
      int nsent = Math.min(maxBytes, streamableBytes);
      state.write(nsent, writer);
      if ((nsent == 0) && (maxBytes != 0))
      {



        state.updateStreamableBytes(streamableBytes, false);
      }
      return nsent;
    }
    
    return distributeToChildren(maxBytes, writer, state);
  }
  








  private int distributeToChildren(int maxBytes, StreamByteDistributor.Writer writer, State state)
    throws Http2Exception
  {
    long oldTotalQueuedWeights = totalQueuedWeights;
    State childState = state.poll();
    State nextChildState = state.peek();
    try
    {
      assert ((nextChildState == null) || (pseudoTimeToWrite >= pseudoTimeToWrite)) : ("nextChildState.pseudoTime(" + pseudoTimeToWrite + ") < " + " childState.pseudoTime(" + pseudoTimeToWrite + ")");
      
      int nsent = distribute(nextChildState == null ? maxBytes : Math.min(maxBytes, (int)Math.min((pseudoTimeToWrite - pseudoTimeToWrite) * stream.weight() / oldTotalQueuedWeights + allocationQuantum, 2147483647L)), writer, childState);
      





      pseudoTime += nsent;
      childState.updatePseudoTime(state, nsent, oldTotalQueuedWeights);
      return nsent;

    }
    finally
    {
      if (activeCountForTree != 0) {
        state.offer(childState);
      }
    }
  }
  
  private State state(Http2Stream stream) {
    return (State)stream.getProperty(stateKey);
  }
  


  int streamableBytes0(Http2Stream stream)
  {
    return statestreamableBytes;
  }
  

  private final class State
    implements PriorityQueueNode<State>
  {
    final Http2Stream stream;
    
    private final Queue<State> queue;
    
    int streamableBytes;
    
    int activeCountForTree;
    private int priorityQueueIndex = -1;
    
    long pseudoTimeToWrite;
    
    long pseudoTime;
    
    long totalQueuedWeights;
    
    boolean active;
    

    State(Http2Stream stream)
    {
      this(stream, 0);
    }
    
    State(Http2Stream stream, int initialSize) {
      this.stream = stream;
      queue = new PriorityQueue(initialSize);
    }
    
    void write(int numBytes, StreamByteDistributor.Writer writer) throws Http2Exception {
      try {
        writer.write(stream, numBytes);
      } catch (Throwable t) {
        throw Http2Exception.connectionError(Http2Error.INTERNAL_ERROR, t, "byte distribution write error", new Object[0]);
      }
    }
    
    void isActiveCountChangeForTree(int increment) {
      assert (activeCountForTree + increment >= 0);
      activeCountForTree += increment;
      if (!stream.isRoot()) {
        State pState = WeightedFairQueueByteDistributor.this.state(stream.parent());
        if (activeCountForTree == 0) {
          pState.remove(this);
        } else if (activeCountForTree - increment == 0) {
          pState.offerAndInitializePseudoTime(this);
        }
        pState.isActiveCountChangeForTree(increment);
      }
    }
    
    void updateStreamableBytes(int newStreamableBytes, boolean isActive) {
      if (active != isActive) {
        isActiveCountChangeForTree(isActive ? 1 : -1);
        active = isActive;
      }
      
      streamableBytes = newStreamableBytes;
    }
    


    void updatePseudoTime(State parentState, int nsent, long totalQueuedWeights)
    {
      assert ((stream.id() != 0) && (nsent >= 0));
      

      pseudoTimeToWrite = (Math.min(pseudoTimeToWrite, pseudoTime) + nsent * totalQueuedWeights / stream.weight());
    }
    





    void offerAndInitializePseudoTime(State state)
    {
      pseudoTimeToWrite = pseudoTime;
      offer(state);
    }
    
    void offer(State state) {
      queue.offer(state);
      totalQueuedWeights += stream.weight();
    }
    


    State poll()
    {
      State state = (State)queue.poll();
      
      totalQueuedWeights -= stream.weight();
      return state;
    }
    
    void remove(State state) {
      if (queue.remove(state)) {
        totalQueuedWeights -= stream.weight();
      }
    }
    
    State peek() {
      return (State)queue.peek();
    }
    
    void close() {
      updateStreamableBytes(0, false);
    }
    
    public int compareTo(State o)
    {
      return MathUtil.compare(pseudoTimeToWrite, pseudoTimeToWrite);
    }
    
    public int priorityQueueIndex()
    {
      return priorityQueueIndex;
    }
    
    public void priorityQueueIndex(int i)
    {
      priorityQueueIndex = i;
    }
  }
}
