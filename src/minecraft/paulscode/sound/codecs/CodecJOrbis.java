package paulscode.sound.codecs;

import com.jcraft.jogg.Packet;
import com.jcraft.jogg.Page;
import com.jcraft.jogg.StreamState;
import com.jcraft.jogg.SyncState;
import com.jcraft.jorbis.Block;
import com.jcraft.jorbis.Comment;
import com.jcraft.jorbis.DspState;
import com.jcraft.jorbis.Info;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownServiceException;
import javax.sound.sampled.AudioFormat;
import paulscode.sound.ICodec;
import paulscode.sound.SoundBuffer;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemLogger;





































































public class CodecJOrbis
  implements ICodec
{
  private static final boolean GET = false;
  private static final boolean SET = true;
  private static final boolean XXX = false;
  private URL url;
  private URLConnection urlConnection = null;
  



  private InputStream inputStream;
  



  private AudioFormat audioFormat;
  



  private boolean endOfStream = false;
  



  private boolean initialized = false;
  



  private byte[] buffer = null;
  



  private int bufferSize;
  



  private int count = 0;
  



  private int index = 0;
  



  private int convertedBufferSize;
  



  private byte[] convertedBuffer = null;
  



  private float[][][] pcmInfo;
  



  private int[] pcmIndex;
  



  private Packet joggPacket = new Packet();
  


  private Page joggPage = new Page();
  


  private StreamState joggStreamState = new StreamState();
  


  private SyncState joggSyncState = new SyncState();
  


  private DspState jorbisDspState = new DspState();
  


  private Block jorbisBlock = new Block(jorbisDspState);
  


  private Comment jorbisComment = new Comment();
  


  private Info jorbisInfo = new Info();
  



  private SoundSystemLogger logger;
  



  public CodecJOrbis()
  {
    logger = SoundSystemConfig.getLogger();
  }
  






  public void reverseByteOrder(boolean b) {}
  





  public boolean initialize(URL url)
  {
    initialized(true, false);
    
    if (joggStreamState != null)
      joggStreamState.clear();
    if (jorbisBlock != null)
      jorbisBlock.clear();
    if (jorbisDspState != null)
      jorbisDspState.clear();
    if (jorbisInfo != null)
      jorbisInfo.clear();
    if (joggSyncState != null) {
      joggSyncState.clear();
    }
    if (inputStream != null)
    {
      try
      {
        inputStream.close();
      }
      catch (IOException ioe) {}
    }
    

    this.url = url;
    
    bufferSize = 8192;
    
    buffer = null;
    count = 0;
    index = 0;
    
    joggStreamState = new StreamState();
    jorbisBlock = new Block(jorbisDspState);
    jorbisDspState = new DspState();
    jorbisInfo = new Info();
    joggSyncState = new SyncState();
    
    try
    {
      urlConnection = url.openConnection();
    }
    catch (UnknownServiceException use)
    {
      errorMessage("Unable to create a UrlConnection in method 'initialize'.");
      
      printStackTrace(use);
      cleanup();
      return false;
    }
    catch (IOException ioe)
    {
      errorMessage("Unable to create a UrlConnection in method 'initialize'.");
      
      printStackTrace(ioe);
      cleanup();
      return false;
    }
    if (urlConnection != null)
    {
      try
      {
        inputStream = urlConnection.getInputStream();
      }
      catch (IOException ioe)
      {
        errorMessage("Unable to acquire inputstream in method 'initialize'.");
        
        printStackTrace(ioe);
        cleanup();
        return false;
      }
    }
    
    endOfStream(true, false);
    
    joggSyncState.init();
    joggSyncState.buffer(bufferSize);
    buffer = joggSyncState.data;
    
    try
    {
      if (!readHeader())
      {
        errorMessage("Error reading the header");
        return false;
      }
    }
    catch (IOException ioe)
    {
      errorMessage("Error reading the header");
      return false;
    }
    
    convertedBufferSize = (bufferSize * 2);
    
    jorbisDspState.synthesis_init(jorbisInfo);
    jorbisBlock.init(jorbisDspState);
    
    int channels = jorbisInfo.channels;
    int rate = jorbisInfo.rate;
    
    audioFormat = new AudioFormat(rate, 16, channels, true, false);
    
    pcmInfo = new float[1][][];
    pcmIndex = new int[jorbisInfo.channels];
    
    initialized(true, true);
    
    return true;
  }
  




  public boolean initialized()
  {
    return initialized(false, false);
  }
  






  public SoundBuffer read()
  {
    byte[] returnBuffer = null;
    
    while ((!endOfStream(false, false)) && ((returnBuffer == null) || (returnBuffer.length < SoundSystemConfig.getStreamingBufferSize())))
    {

      if (returnBuffer == null) {
        returnBuffer = readBytes();
      } else {
        returnBuffer = appendByteArrays(returnBuffer, readBytes());
      }
    }
    if (returnBuffer == null) {
      return null;
    }
    return new SoundBuffer(returnBuffer, audioFormat);
  }
  







  public SoundBuffer readAll()
  {
    byte[] returnBuffer = null;
    
    while (!endOfStream(false, false))
    {
      if (returnBuffer == null) {
        returnBuffer = readBytes();
      } else {
        returnBuffer = appendByteArrays(returnBuffer, readBytes());
      }
    }
    if (returnBuffer == null) {
      return null;
    }
    return new SoundBuffer(returnBuffer, audioFormat);
  }
  




  public boolean endOfStream()
  {
    return endOfStream(false, false);
  }
  



  public void cleanup()
  {
    joggStreamState.clear();
    jorbisBlock.clear();
    jorbisDspState.clear();
    jorbisInfo.clear();
    joggSyncState.clear();
    
    if (inputStream != null)
    {
      try
      {
        inputStream.close();
      }
      catch (IOException ioe) {}
    }
    

    joggStreamState = null;
    jorbisBlock = null;
    jorbisDspState = null;
    jorbisInfo = null;
    joggSyncState = null;
    inputStream = null;
  }
  





  public AudioFormat getAudioFormat()
  {
    return audioFormat;
  }
  





  private boolean readHeader()
    throws IOException
  {
    index = joggSyncState.buffer(bufferSize);
    
    int bytes = inputStream.read(joggSyncState.data, index, bufferSize);
    if (bytes < 0) {
      bytes = 0;
    }
    joggSyncState.wrote(bytes);
    
    if (joggSyncState.pageout(joggPage) != 1)
    {

      if (bytes < bufferSize) {
        return true;
      }
      errorMessage("Ogg header not recognized in method 'readHeader'.");
      return false;
    }
    

    joggStreamState.init(joggPage.serialno());
    
    jorbisInfo.init();
    jorbisComment.init();
    if (joggStreamState.pagein(joggPage) < 0)
    {
      errorMessage("Problem with first Ogg header page in method 'readHeader'.");
      
      return false;
    }
    
    if (joggStreamState.packetout(joggPacket) != 1)
    {
      errorMessage("Problem with first Ogg header packet in method 'readHeader'.");
      
      return false;
    }
    
    if (jorbisInfo.synthesis_headerin(jorbisComment, joggPacket) < 0)
    {
      errorMessage("File does not contain Vorbis header in method 'readHeader'.");
      
      return false;
    }
    
    int i = 0;
    while (i < 2)
    {
      while (i < 2)
      {
        int result = joggSyncState.pageout(joggPage);
        if (result == 0)
          break;
        if (result == 1)
        {
          joggStreamState.pagein(joggPage);
          while (i < 2)
          {
            result = joggStreamState.packetout(joggPacket);
            if (result == 0) {
              break;
            }
            if (result == -1)
            {
              errorMessage("Secondary Ogg header corrupt in method 'readHeader'.");
              
              return false;
            }
            
            jorbisInfo.synthesis_headerin(jorbisComment, joggPacket);
            
            i++;
          }
        }
      }
      index = joggSyncState.buffer(bufferSize);
      bytes = inputStream.read(joggSyncState.data, index, bufferSize);
      if (bytes < 0)
        bytes = 0;
      if ((bytes == 0) && (i < 2))
      {
        errorMessage("End of file reached before finished readingOgg header in method 'readHeader'");
        
        return false;
      }
      
      joggSyncState.wrote(bytes);
    }
    
    index = joggSyncState.buffer(bufferSize);
    buffer = joggSyncState.data;
    
    return true;
  }
  




  private byte[] readBytes()
  {
    if (!initialized(false, false)) {
      return null;
    }
    if (endOfStream(false, false)) {
      return null;
    }
    if (convertedBuffer == null)
      convertedBuffer = new byte[convertedBufferSize];
    byte[] returnBuffer = null;
    



    switch (joggSyncState.pageout(joggPage))
    {
    case -1: 
    case 0: 
      break;
    
    default: 
      joggStreamState.pagein(joggPage);
      if (joggPage.granulepos() == 0L)
      {
        endOfStream(true, true);
        return null;
      }
      
      for (;;)
      {
        switch (joggStreamState.packetout(joggPacket))
        {
        case 0: 
          break;
        case -1: 
          break;
        
        default: 
          if (jorbisBlock.synthesis(joggPacket) == 0) {
            jorbisDspState.synthesis_blockin(jorbisBlock);
          }
          int samples;
          while ((samples = jorbisDspState.synthesis_pcmout(pcmInfo, pcmIndex)) > 0)
          {
            float[][] pcmf = pcmInfo[0];
            int bout = samples < convertedBufferSize ? samples : convertedBufferSize;
            
            for (int i = 0; i < jorbisInfo.channels; i++)
            {
              int ptr = i * 2;
              int mono = pcmIndex[i];
              for (int j = 0; j < bout; j++)
              {
                int val = (int)(pcmf[i][(mono + j)] * 32767.0D);
                
                if (val > 32767)
                  val = 32767;
                if (val < 32768)
                  val = 32768;
                if (val < 0)
                  val |= 0x8000;
                convertedBuffer[ptr] = ((byte)val);
                convertedBuffer[(ptr + 1)] = ((byte)(val >>> 8));
                
                ptr += 2 * jorbisInfo.channels;
              }
            }
            jorbisDspState.synthesis_read(bout);
            
            returnBuffer = appendByteArrays(returnBuffer, convertedBuffer, 2 * jorbisInfo.channels * bout);
          }
        }
        
      }
      


      if (joggPage.eos() != 0) {
        endOfStream(true, true);
      }
      break;
    }
    if (!endOfStream(false, false))
    {
      index = joggSyncState.buffer(bufferSize);
      buffer = joggSyncState.data;
      try
      {
        count = inputStream.read(buffer, index, bufferSize);
      }
      catch (Exception e)
      {
        printStackTrace(e);
        return null;
      }
      if (count == -1) {
        return returnBuffer;
      }
      joggSyncState.wrote(count);
      if (count == 0) {
        endOfStream(true, true);
      }
    }
    return returnBuffer;
  }
  






  private synchronized boolean initialized(boolean action, boolean value)
  {
    if (action == true)
      initialized = value;
    return initialized;
  }
  






  private synchronized boolean endOfStream(boolean action, boolean value)
  {
    if (action == true)
      endOfStream = value;
    return endOfStream;
  }
  







  private static byte[] trimArray(byte[] array, int maxLength)
  {
    byte[] trimmedArray = null;
    if ((array != null) && (array.length > maxLength))
    {
      trimmedArray = new byte[maxLength];
      System.arraycopy(array, 0, trimmedArray, 0, maxLength);
    }
    return trimmedArray;
  }
  










  private static byte[] appendByteArrays(byte[] arrayOne, byte[] arrayTwo, int arrayTwoBytes)
  {
    int bytes = arrayTwoBytes;
    

    if ((arrayTwo == null) || (arrayTwo.length == 0)) {
      bytes = 0;
    } else if (arrayTwo.length < arrayTwoBytes) {
      bytes = arrayTwo.length;
    }
    if ((arrayOne == null) && ((arrayTwo == null) || (bytes <= 0)))
    {

      return null; }
    byte[] newArray;
    if (arrayOne == null)
    {

      byte[] newArray = new byte[bytes];
      
      System.arraycopy(arrayTwo, 0, newArray, 0, bytes);
      arrayTwo = null;
    }
    else if ((arrayTwo == null) || (bytes <= 0))
    {

      byte[] newArray = new byte[arrayOne.length];
      
      System.arraycopy(arrayOne, 0, newArray, 0, arrayOne.length);
      arrayOne = null;

    }
    else
    {
      newArray = new byte[arrayOne.length + bytes];
      System.arraycopy(arrayOne, 0, newArray, 0, arrayOne.length);
      
      System.arraycopy(arrayTwo, 0, newArray, arrayOne.length, bytes);
      
      arrayOne = null;
      arrayTwo = null;
    }
    
    return newArray;
  }
  








  private static byte[] appendByteArrays(byte[] arrayOne, byte[] arrayTwo)
  {
    if ((arrayOne == null) && (arrayTwo == null))
    {

      return null; }
    byte[] newArray;
    if (arrayOne == null)
    {

      byte[] newArray = new byte[arrayTwo.length];
      
      System.arraycopy(arrayTwo, 0, newArray, 0, arrayTwo.length);
      arrayTwo = null;
    }
    else if (arrayTwo == null)
    {

      byte[] newArray = new byte[arrayOne.length];
      
      System.arraycopy(arrayOne, 0, newArray, 0, arrayOne.length);
      arrayOne = null;

    }
    else
    {
      newArray = new byte[arrayOne.length + arrayTwo.length];
      System.arraycopy(arrayOne, 0, newArray, 0, arrayOne.length);
      
      System.arraycopy(arrayTwo, 0, newArray, arrayOne.length, arrayTwo.length);
      
      arrayOne = null;
      arrayTwo = null;
    }
    
    return newArray;
  }
  




  private void errorMessage(String message)
  {
    logger.errorMessage("CodecJOrbis", message, 0);
  }
  




  private void printStackTrace(Exception e)
  {
    logger.printStackTrace(e, 1);
  }
}
