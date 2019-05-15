package ch.qos.logback.core.encoder;

import ch.qos.logback.core.CoreConstants;
import java.io.IOException;
import java.io.OutputStream;










public class EchoEncoder<E>
  extends EncoderBase<E>
{
  String fileHeader;
  String fileFooter;
  
  public EchoEncoder() {}
  
  public void doEncode(E event)
    throws IOException
  {
    String val = event + CoreConstants.LINE_SEPARATOR;
    outputStream.write(val.getBytes());
    
    outputStream.flush();
  }
  
  public void close() throws IOException {
    if (fileFooter == null) {
      return;
    }
    outputStream.write(fileFooter.getBytes());
  }
  
  public void init(OutputStream os) throws IOException {
    super.init(os);
    if (fileHeader == null) {
      return;
    }
    outputStream.write(fileHeader.getBytes());
  }
}
