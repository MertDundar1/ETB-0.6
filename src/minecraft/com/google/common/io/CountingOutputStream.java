package com.google.common.io;

import com.google.common.annotations.Beta;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.annotation.Nullable;




























@Beta
public final class CountingOutputStream
  extends FilterOutputStream
{
  private long count;
  
  public CountingOutputStream(@Nullable OutputStream out)
  {
    super(out);
  }
  
  public long getCount()
  {
    return count;
  }
  
  public void write(byte[] b, int off, int len) throws IOException {
    out.write(b, off, len);
    count += len;
  }
  
  public void write(int b) throws IOException {
    out.write(b);
    count += 1L;
  }
  

  public void close()
    throws IOException
  {
    out.close();
  }
}
