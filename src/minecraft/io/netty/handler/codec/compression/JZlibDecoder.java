package io.netty.handler.codec.compression;

import com.jcraft.jzlib.Inflater;
import com.jcraft.jzlib.JZlib;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import java.util.List;















public class JZlibDecoder
  extends ZlibDecoder
{
  private final Inflater z = new Inflater();
  

  private byte[] dictionary;
  
  private volatile boolean finished;
  

  public JZlibDecoder()
  {
    this(ZlibWrapper.ZLIB);
  }
  




  public JZlibDecoder(ZlibWrapper wrapper)
  {
    if (wrapper == null) {
      throw new NullPointerException("wrapper");
    }
    
    int resultCode = z.init(ZlibUtil.convertWrapperType(wrapper));
    if (resultCode != 0) {
      ZlibUtil.fail(z, "initialization failure", resultCode);
    }
  }
  






  public JZlibDecoder(byte[] dictionary)
  {
    if (dictionary == null) {
      throw new NullPointerException("dictionary");
    }
    this.dictionary = dictionary;
    

    int resultCode = z.inflateInit(JZlib.W_ZLIB);
    if (resultCode != 0) {
      ZlibUtil.fail(z, "initialization failure", resultCode);
    }
  }
  




  public boolean isClosed()
  {
    return finished;
  }
  
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception
  {
    if (finished)
    {
      in.skipBytes(in.readableBytes());
      return;
    }
    
    if (!in.isReadable()) {
      return;
    }
    
    try
    {
      int inputLength = in.readableBytes();
      z.avail_in = inputLength;
      if (in.hasArray()) {
        z.next_in = in.array();
        z.next_in_index = (in.arrayOffset() + in.readerIndex());
      } else {
        byte[] array = new byte[inputLength];
        in.getBytes(in.readerIndex(), array);
        z.next_in = array;
        z.next_in_index = 0;
      }
      int oldNextInIndex = z.next_in_index;
      

      int maxOutputLength = inputLength << 1;
      ByteBuf decompressed = ctx.alloc().heapBuffer(maxOutputLength);
      try
      {
        for (;;) {
          z.avail_out = maxOutputLength;
          decompressed.ensureWritable(maxOutputLength);
          z.next_out = decompressed.array();
          z.next_out_index = (decompressed.arrayOffset() + decompressed.writerIndex());
          int oldNextOutIndex = z.next_out_index;
          

          int resultCode = z.inflate(2);
          int outputLength = z.next_out_index - oldNextOutIndex;
          if (outputLength > 0) {
            decompressed.writerIndex(decompressed.writerIndex() + outputLength);
          }
          
          switch (resultCode) {
          case 2: 
            if (dictionary == null) {
              ZlibUtil.fail(z, "decompression failure", resultCode);
            } else {
              resultCode = z.inflateSetDictionary(dictionary, dictionary.length);
              if (resultCode != 0) {
                ZlibUtil.fail(z, "failed to set the dictionary", resultCode);
              }
            }
            break;
          case 1: 
            finished = true;
            z.inflateEnd();
            break;
          case 0: 
            break;
          case -5: 
            if (z.avail_in > 0) break;
            break;
          case -4: case -3: 
          case -2: case -1: 
          default: 
            ZlibUtil.fail(z, "decompression failure", resultCode);
          }
        }
      } finally {
        in.skipBytes(z.next_in_index - oldNextInIndex);
        if (decompressed.isReadable()) {
          out.add(decompressed);
        } else {
          decompressed.release();
        }
        
      }
      
    }
    finally
    {
      z.next_in = null;
      z.next_out = null;
    }
  }
}
