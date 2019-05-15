package io.netty.handler.codec.http2.internal.hpack;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http2.Http2Error;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.util.AsciiString;
import io.netty.util.ByteProcessor;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.ThrowableUtil;






































final class HuffmanDecoder
{
  private static final Http2Exception EOS_DECODED = (Http2Exception)ThrowableUtil.unknownStackTrace(Http2Exception.connectionError(Http2Error.COMPRESSION_ERROR, "HPACK - EOS Decoded", new Object[0]), HuffmanDecoder.class, "decode(...)");
  
  private static final Http2Exception INVALID_PADDING = (Http2Exception)ThrowableUtil.unknownStackTrace(Http2Exception.connectionError(Http2Error.COMPRESSION_ERROR, "HPACK - Invalid Padding", new Object[0]), HuffmanDecoder.class, "decode(...)");
  

  private static final Node ROOT = buildTree(HpackUtil.HUFFMAN_CODES, HpackUtil.HUFFMAN_CODE_LENGTHS);
  private final DecoderProcessor processor;
  
  HuffmanDecoder(int initialCapacity)
  {
    processor = new DecoderProcessor(initialCapacity);
  }
  






  public AsciiString decode(ByteBuf buf, int length)
    throws Http2Exception
  {
    processor.reset();
    buf.forEachByte(buf.readerIndex(), length, processor);
    buf.skipBytes(length);
    return processor.end();
  }
  

  private static final class Node
  {
    private final int symbol;
    
    private final int bits;
    private final Node[] children;
    
    Node()
    {
      symbol = 0;
      bits = 8;
      children = new Node['Ā'];
    }
    





    Node(int symbol, int bits)
    {
      assert ((bits > 0) && (bits <= 8));
      this.symbol = symbol;
      this.bits = bits;
      children = null;
    }
    
    private boolean isTerminal() {
      return children == null;
    }
  }
  
  private static Node buildTree(int[] codes, byte[] lengths) {
    Node root = new Node();
    for (int i = 0; i < codes.length; i++) {
      insert(root, i, codes[i], lengths[i]);
    }
    return root;
  }
  
  private static void insert(Node root, int symbol, int code, byte length)
  {
    Node current = root;
    while (length > 8) {
      if (current.isTerminal()) {
        throw new IllegalStateException("invalid Huffman code: prefix not unique");
      }
      length = (byte)(length - 8);
      int i = code >>> length & 0xFF;
      if (children[i] == null) {
        children[i] = new Node();
      }
      current = children[i];
    }
    
    Node terminal = new Node(symbol, length);
    int shift = 8 - length;
    int start = code << shift & 0xFF;
    int end = 1 << shift;
    for (int i = start; i < start + end; i++) {
      children[i] = terminal;
    }
  }
  
  private static final class DecoderProcessor implements ByteProcessor {
    private final int initialCapacity;
    private byte[] bytes;
    private int index;
    private HuffmanDecoder.Node node;
    private int current;
    private int currentBits;
    private int symbolBits;
    
    DecoderProcessor(int initialCapacity) {
      this.initialCapacity = ObjectUtil.checkPositive(initialCapacity, "initialCapacity");
    }
    
    void reset() {
      node = HuffmanDecoder.ROOT;
      current = 0;
      currentBits = 0;
      symbolBits = 0;
      bytes = new byte[initialCapacity];
      index = 0;
    }
    

















    public boolean process(byte value)
      throws Http2Exception
    {
      current = (current << 8 | value & 0xFF);
      currentBits += 8;
      symbolBits += 8;
      do
      {
        node = HuffmanDecoder.Node.access$100(node)[(current >>> currentBits - 8 & 0xFF)];
        currentBits -= HuffmanDecoder.Node.access$300(node);
        if (HuffmanDecoder.Node.access$000(node)) {
          if (HuffmanDecoder.Node.access$400(node) == 256) {
            throw HuffmanDecoder.EOS_DECODED;
          }
          append(HuffmanDecoder.Node.access$400(node));
          node = HuffmanDecoder.ROOT;
          

          symbolBits = currentBits;
        }
      } while (currentBits >= 8);
      return true;
    }
    


    AsciiString end()
      throws Http2Exception
    {
      while (currentBits > 0) {
        node = HuffmanDecoder.Node.access$100(node)[(current << 8 - currentBits & 0xFF)];
        if ((!HuffmanDecoder.Node.access$000(node)) || (HuffmanDecoder.Node.access$300(node) > currentBits)) break;
        if (HuffmanDecoder.Node.access$400(node) == 256) {
          throw HuffmanDecoder.EOS_DECODED;
        }
        currentBits -= HuffmanDecoder.Node.access$300(node);
        append(HuffmanDecoder.Node.access$400(node));
        node = HuffmanDecoder.ROOT;
        symbolBits = currentBits;
      }
      







      int mask = (1 << symbolBits) - 1;
      if ((symbolBits > 7) || ((current & mask) != mask)) {
        throw HuffmanDecoder.INVALID_PADDING;
      }
      
      return new AsciiString(bytes, 0, index, false);
    }
    
    private void append(int i) {
      try {
        bytes[index] = ((byte)i);
      }
      catch (IndexOutOfBoundsException ignore) {
        byte[] newBytes = new byte[bytes.length + initialCapacity];
        System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
        bytes = newBytes;
        bytes[index] = ((byte)i);
      }
      index += 1;
    }
  }
}
