package paulscode.sound;

import java.util.LinkedList;
import javax.sound.sampled.AudioFormat;












































public class Channel
{
  protected Class libraryType = Library.class;
  




  public int channelType;
  




  private SoundSystemLogger logger;
  



  public Source attachedSource = null;
  



  public int buffersUnqueued = 0;
  







  public Channel(int type)
  {
    logger = SoundSystemConfig.getLogger();
    
    channelType = type;
  }
  



  public void cleanup()
  {
    logger = null;
  }
  





  public boolean preLoadBuffers(LinkedList<byte[]> bufferList)
  {
    return true;
  }
  





  public boolean queueBuffer(byte[] buffer)
  {
    return false;
  }
  





  public int feedRawAudioData(byte[] buffer)
  {
    return 1;
  }
  




  public int buffersProcessed()
  {
    return 0;
  }
  




  public float millisecondsPlayed()
  {
    return -1.0F;
  }
  




  public boolean processBuffer()
  {
    return false;
  }
  





  public void setAudioFormat(AudioFormat audioFormat) {}
  





  public void flush() {}
  





  public void close() {}
  





  public void play() {}
  





  public void pause() {}
  




  public void stop() {}
  




  public void rewind() {}
  




  public boolean playing()
  {
    return false;
  }
  




  public String getClassName()
  {
    String libTitle = SoundSystemConfig.getLibraryTitle(libraryType);
    
    if (libTitle.equals("No Sound")) {
      return "Channel";
    }
    return "Channel" + libTitle;
  }
  




  protected void message(String message)
  {
    logger.message(message, 0);
  }
  




  protected void importantMessage(String message)
  {
    logger.importantMessage(message, 0);
  }
  






  protected boolean errorCheck(boolean error, String message)
  {
    return logger.errorCheck(error, getClassName(), message, 0);
  }
  




  protected void errorMessage(String message)
  {
    logger.errorMessage(getClassName(), message, 0);
  }
  




  protected void printStackTrace(Exception e)
  {
    logger.printStackTrace(e, 1);
  }
}
