package io.netty.handler.flow;

import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Recycler;
import io.netty.util.Recycler.Handle;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.ArrayDeque;






















































public class FlowControlHandler
  extends ChannelDuplexHandler
{
  private static final InternalLogger logger = InternalLoggerFactory.getInstance(FlowControlHandler.class);
  
  private final boolean releaseMessages;
  
  private RecyclableArrayDeque queue;
  
  private ChannelConfig config;
  private boolean shouldConsume;
  
  public FlowControlHandler()
  {
    this(true);
  }
  
  public FlowControlHandler(boolean releaseMessages) {
    this.releaseMessages = releaseMessages;
  }
  



  boolean isQueueEmpty()
  {
    return queue.isEmpty();
  }
  


  private void destroy()
  {
    if (queue != null)
    {
      if (!queue.isEmpty()) {
        logger.trace("Non-empty queue: {}", queue);
        
        if (releaseMessages) {
          Object msg;
          while ((msg = queue.poll()) != null) {
            ReferenceCountUtil.safeRelease(msg);
          }
        }
      }
      
      queue.recycle();
      queue = null;
    }
  }
  
  public void handlerAdded(ChannelHandlerContext ctx) throws Exception
  {
    config = ctx.channel().config();
  }
  
  public void channelInactive(ChannelHandlerContext ctx) throws Exception
  {
    destroy();
    ctx.fireChannelInactive();
  }
  
  public void read(ChannelHandlerContext ctx) throws Exception
  {
    if (dequeue(ctx, 1) == 0)
    {


      shouldConsume = true;
      ctx.read();
    }
  }
  
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
  {
    if (queue == null) {
      queue = RecyclableArrayDeque.newInstance();
    }
    
    queue.offer(msg);
    



    int minConsume = shouldConsume ? 1 : 0;
    shouldConsume = false;
    
    dequeue(ctx, minConsume);
  }
  







  public void channelReadComplete(ChannelHandlerContext ctx)
    throws Exception
  {}
  







  private int dequeue(ChannelHandlerContext ctx, int minConsume)
  {
    if (queue != null)
    {
      int consumed = 0;
      

      while ((consumed < minConsume) || (config.isAutoRead())) {
        Object msg = queue.poll();
        if (msg == null) {
          break;
        }
        
        consumed++;
        ctx.fireChannelRead(msg);
      }
      



      if ((queue.isEmpty()) && (consumed > 0)) {
        ctx.fireChannelReadComplete();
      }
      
      return consumed;
    }
    
    return 0;
  }
  



  private static final class RecyclableArrayDeque
    extends ArrayDeque<Object>
  {
    private static final long serialVersionUID = 0L;
    

    private static final int DEFAULT_NUM_ELEMENTS = 2;
    

    private static final Recycler<RecyclableArrayDeque> RECYCLER = new Recycler()
    {
      protected FlowControlHandler.RecyclableArrayDeque newObject(Recycler.Handle<FlowControlHandler.RecyclableArrayDeque> handle) {
        return new FlowControlHandler.RecyclableArrayDeque(2, handle, null);
      }
    };
    private final Recycler.Handle<RecyclableArrayDeque> handle;
    
    public static RecyclableArrayDeque newInstance() { return (RecyclableArrayDeque)RECYCLER.get(); }
    


    private RecyclableArrayDeque(int numElements, Recycler.Handle<RecyclableArrayDeque> handle)
    {
      super();
      this.handle = handle;
    }
    
    public void recycle() {
      clear();
      handle.recycle(this);
    }
  }
}
