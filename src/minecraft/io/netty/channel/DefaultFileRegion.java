package io.netty.channel;

import io.netty.util.AbstractReferenceCounted;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;





















public class DefaultFileRegion
  extends AbstractReferenceCounted
  implements FileRegion
{
  private static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultFileRegion.class);
  

  private final FileChannel file;
  

  private final long position;
  
  private final long count;
  
  private long transfered;
  

  public DefaultFileRegion(FileChannel file, long position, long count)
  {
    if (file == null) {
      throw new NullPointerException("file");
    }
    if (position < 0L) {
      throw new IllegalArgumentException("position must be >= 0 but was " + position);
    }
    if (count < 0L) {
      throw new IllegalArgumentException("count must be >= 0 but was " + count);
    }
    this.file = file;
    this.position = position;
    this.count = count;
  }
  
  public long position()
  {
    return position;
  }
  
  public long count()
  {
    return count;
  }
  
  public long transfered()
  {
    return transfered;
  }
  
  public long transferTo(WritableByteChannel target, long position) throws IOException
  {
    long count = this.count - position;
    if ((count < 0L) || (position < 0L)) {
      throw new IllegalArgumentException("position out of range: " + position + " (expected: 0 - " + (this.count - 1L) + ')');
    }
    

    if (count == 0L) {
      return 0L;
    }
    
    long written = file.transferTo(this.position + position, count, target);
    if (written > 0L) {
      transfered += written;
    }
    return written;
  }
  
  protected void deallocate()
  {
    try {
      file.close();
    } catch (IOException e) {
      if (logger.isWarnEnabled()) {
        logger.warn("Failed to close a file.", e);
      }
    }
  }
}
