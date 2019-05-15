package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelPromiseNotifier;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.concurrent.EventExecutor;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import java.util.zip.Checksum;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Exception;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.xxhash.StreamingXXHash32;
import net.jpountz.xxhash.XXHashFactory;





































































public class Lz4FrameEncoder
  extends MessageToByteEncoder<ByteBuf>
{
  private final int blockSize;
  private LZ4Compressor compressor;
  private Checksum checksum;
  private final int compressionLevel;
  private ByteBuf buffer;
  private int currentBlockLength;
  private final int compressedBlockSize;
  private volatile boolean finished;
  private volatile ChannelHandlerContext ctx;
  
  public Lz4FrameEncoder()
  {
    this(false);
  }
  







  public Lz4FrameEncoder(boolean highCompressor)
  {
    this(LZ4Factory.fastestInstance(), highCompressor, 65536, XXHashFactory.fastestInstance().newStreamingHash32(-1756908916).asChecksum());
  }
  












  public Lz4FrameEncoder(LZ4Factory factory, boolean highCompressor, int blockSize, Checksum checksum)
  {
    if (factory == null) {
      throw new NullPointerException("factory");
    }
    if (checksum == null) {
      throw new NullPointerException("checksum");
    }
    
    compressor = (highCompressor ? factory.highCompressor() : factory.fastCompressor());
    this.checksum = checksum;
    
    compressionLevel = compressionLevel(blockSize);
    this.blockSize = blockSize;
    currentBlockLength = 0;
    compressedBlockSize = (21 + compressor.maxCompressedLength(blockSize));
    
    finished = false;
  }
  


  private static int compressionLevel(int blockSize)
  {
    if ((blockSize < 64) || (blockSize > 33554432)) {
      throw new IllegalArgumentException(String.format("blockSize: %d (expected: %d-%d)", new Object[] { Integer.valueOf(blockSize), Integer.valueOf(64), Integer.valueOf(33554432) }));
    }
    
    int compressionLevel = 32 - Integer.numberOfLeadingZeros(blockSize - 1);
    compressionLevel = Math.max(0, compressionLevel - 10);
    return compressionLevel;
  }
  
  protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception
  {
    if (finished) {
      out.writeBytes(in);
      return;
    }
    
    int length = in.readableBytes();
    
    ByteBuf buffer = this.buffer;
    int blockSize = buffer.capacity();
    while (currentBlockLength + length >= blockSize) {
      int tail = blockSize - currentBlockLength;
      in.getBytes(in.readerIndex(), buffer, currentBlockLength, tail);
      currentBlockLength = blockSize;
      flushBufferedData(out);
      in.skipBytes(tail);
      length -= tail;
    }
    in.readBytes(buffer, currentBlockLength, length);
    currentBlockLength += length;
  }
  
  private void flushBufferedData(ByteBuf out) {
    int currentBlockLength = this.currentBlockLength;
    if (currentBlockLength == 0) {
      return;
    }
    checksum.reset();
    checksum.update(buffer.array(), buffer.arrayOffset(), currentBlockLength);
    int check = (int)checksum.getValue();
    
    out.ensureWritable(compressedBlockSize);
    int idx = out.writerIndex();
    try
    {
      ByteBuffer outNioBuffer = out.internalNioBuffer(idx + 21, out.writableBytes() - 21);
      int pos = outNioBuffer.position();
      
      compressor.compress(buffer.internalNioBuffer(0, currentBlockLength), outNioBuffer);
      compressedLength = outNioBuffer.position() - pos;
    } catch (LZ4Exception e) { int compressedLength;
      throw new CompressionException(e); }
    int compressedLength;
    int blockType;
    if (compressedLength >= currentBlockLength) {
      int blockType = 16;
      compressedLength = currentBlockLength;
      out.setBytes(idx + 21, buffer, 0, currentBlockLength);
    } else {
      blockType = 32;
    }
    
    out.setLong(idx, 5501767354678207339L);
    out.setByte(idx + 8, (byte)(blockType | compressionLevel));
    out.setIntLE(idx + 9, compressedLength);
    out.setIntLE(idx + 13, currentBlockLength);
    out.setIntLE(idx + 17, check);
    out.writerIndex(idx + 21 + compressedLength);
    currentBlockLength = 0;
    
    this.currentBlockLength = currentBlockLength;
  }
  
  private ChannelFuture finishEncode(ChannelHandlerContext ctx, ChannelPromise promise) {
    if (finished) {
      promise.setSuccess();
      return promise;
    }
    finished = true;
    try
    {
      ByteBuf footer = ctx.alloc().heapBuffer(compressor.maxCompressedLength(currentBlockLength) + 21);
      
      flushBufferedData(footer);
      
      int idx = footer.writerIndex();
      footer.setLong(idx, 5501767354678207339L);
      footer.setByte(idx + 8, (byte)(0x10 | compressionLevel));
      footer.setInt(idx + 9, 0);
      footer.setInt(idx + 13, 0);
      footer.setInt(idx + 17, 0);
      
      footer.writerIndex(idx + 21);
      
      return ctx.writeAndFlush(footer, promise);
    } finally {
      cleanup();
    }
  }
  
  private void cleanup() {
    compressor = null;
    checksum = null;
    if (buffer != null) {
      buffer.release();
      buffer = null;
    }
  }
  


  public boolean isClosed()
  {
    return finished;
  }
  




  public ChannelFuture close()
  {
    return close(ctx().newPromise());
  }
  




  public ChannelFuture close(final ChannelPromise promise)
  {
    ChannelHandlerContext ctx = ctx();
    EventExecutor executor = ctx.executor();
    if (executor.inEventLoop()) {
      return finishEncode(ctx, promise);
    }
    executor.execute(new Runnable()
    {
      public void run() {
        ChannelFuture f = Lz4FrameEncoder.this.finishEncode(Lz4FrameEncoder.access$000(Lz4FrameEncoder.this), promise);
        f.addListener(new ChannelPromiseNotifier(new ChannelPromise[] { promise }));
      }
    });
    return promise;
  }
  
  public void close(final ChannelHandlerContext ctx, final ChannelPromise promise)
    throws Exception
  {
    ChannelFuture f = finishEncode(ctx, ctx.newPromise());
    f.addListener(new ChannelFutureListener()
    {
      public void operationComplete(ChannelFuture f) throws Exception {
        ctx.close(promise);
      }
    });
    
    if (!f.isDone())
    {
      ctx.executor().schedule(new Runnable()
      {

        public void run() { ctx.close(promise); } }, 10L, TimeUnit.SECONDS);
    }
  }
  

  private ChannelHandlerContext ctx()
  {
    ChannelHandlerContext ctx = this.ctx;
    if (ctx == null) {
      throw new IllegalStateException("not added to a pipeline");
    }
    return ctx;
  }
  
  public void handlerAdded(ChannelHandlerContext ctx) throws Exception
  {
    this.ctx = ctx;
    
    buffer = Unpooled.wrappedBuffer(new byte[blockSize]);
  }
  
  public void handlerRemoved(ChannelHandlerContext ctx) throws Exception
  {
    super.handlerRemoved(ctx);
    cleanup();
  }
}
