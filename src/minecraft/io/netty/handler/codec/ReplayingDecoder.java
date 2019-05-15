package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Signal;
import io.netty.util.internal.RecyclableArrayList;
import io.netty.util.internal.StringUtil;
import java.util.List;


































































































































































































































































public abstract class ReplayingDecoder<S>
  extends ByteToMessageDecoder
{
  static final Signal REPLAY = Signal.valueOf(ReplayingDecoder.class.getName() + ".REPLAY");
  
  private final ReplayingDecoderBuffer replayable = new ReplayingDecoderBuffer();
  private S state;
  private int checkpoint = -1;
  


  protected ReplayingDecoder()
  {
    this(null);
  }
  


  protected ReplayingDecoder(S initialState)
  {
    state = initialState;
  }
  


  protected void checkpoint()
  {
    checkpoint = internalBuffer().readerIndex();
  }
  



  protected void checkpoint(S state)
  {
    checkpoint();
    state(state);
  }
  



  protected S state()
  {
    return state;
  }
  



  protected S state(S newState)
  {
    S oldState = state;
    state = newState;
    return oldState;
  }
  
  public void channelInactive(ChannelHandlerContext ctx) throws Exception
  {
    RecyclableArrayList out = RecyclableArrayList.newInstance();
    try {
      replayable.terminate();
      callDecode(ctx, internalBuffer(), out);
      decodeLast(ctx, replayable, out);
    } catch (Signal replay) { int size;
      int i;
      replay.expect(REPLAY); } catch (DecoderException e) { int size;
      int i;
      throw e;
    } catch (Exception e) {
      throw new DecoderException(e);
    } finally {
      try {
        if (cumulation != null) {
          cumulation.release();
          cumulation = null;
        }
        int size = out.size();
        for (int i = 0; i < size; i++) {
          ctx.fireChannelRead(out.get(i));
        }
        if (size > 0)
        {
          ctx.fireChannelReadComplete();
        }
        ctx.fireChannelInactive();
      }
      finally {
        out.recycle();
      }
    }
  }
  
  protected void callDecode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out)
  {
    replayable.setCumulation(in);
    try {
      while (in.isReadable()) {
        int oldReaderIndex = this.checkpoint = in.readerIndex();
        int outSize = out.size();
        S oldState = state;
        int oldInputLength = in.readableBytes();
        try {
          decode(ctx, replayable, out);
          




          if (ctx.isRemoved()) {
            break;
          }
          
          if (outSize == out.size()) {
            if ((oldInputLength == in.readableBytes()) && (oldState == state)) {
              throw new DecoderException(StringUtil.simpleClassName(getClass()) + ".decode() must consume the inbound " + "data or change its state if it did not decode anything.");
            }
            



            continue;
          }
        }
        catch (Signal replay) {
          replay.expect(REPLAY);
          




          if (!ctx.isRemoved()) break label163; }
        break;
        
        label163:
        
        int checkpoint = this.checkpoint;
        if (checkpoint >= 0) {
          in.readerIndex(checkpoint);
        }
        


        break;
        

        if ((oldReaderIndex == in.readerIndex()) && (oldState == state)) {
          throw new DecoderException(StringUtil.simpleClassName(getClass()) + ".decode() method must consume the inbound data " + "or change its state if it decoded something.");
        }
        

        if (isSingleDecode()) {
          break;
        }
      }
    } catch (DecoderException e) {
      throw e;
    } catch (Throwable cause) {
      throw new DecoderException(cause);
    }
  }
}
