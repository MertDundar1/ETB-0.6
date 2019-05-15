package paulscode.sound;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;


















































public class StreamThread
  extends SimpleThread
{
  private SoundSystemLogger logger;
  private List<Source> streamingSources;
  private final Object listLock = new Object();
  





  public StreamThread()
  {
    logger = SoundSystemConfig.getLogger();
    
    streamingSources = new LinkedList();
  }
  






  protected void cleanup()
  {
    kill();
    super.cleanup();
  }
  









  public void run()
  {
    snooze(3600000L);
    
    while (!dying())
    {
      while ((!dying()) && (!streamingSources.isEmpty()))
      {

        synchronized (listLock)
        {
          ListIterator<Source> iter = streamingSources.listIterator();
          while ((!dying()) && (iter.hasNext()))
          {
            Source src = (Source)iter.next();
            if (src == null)
            {
              iter.remove();
            }
            else if (src.stopped())
            {
              if (!rawDataStream) {
                iter.remove();
              }
            } else if (!src.active())
            {
              if ((toLoop) || (rawDataStream))
                toPlay = true;
              iter.remove();
            }
            else if (!src.paused())
            {
              src.checkFadeOut();
              if ((!src.stream()) && (!rawDataStream) && (
              
                (channel == null) || (!channel.processBuffer())))
              {

                if (nextCodec == null)
                {
                  src.readBuffersFromNextSoundInSequence();
                }
                






                if (toLoop)
                {

                  if (!src.playing())
                  {

                    SoundSystemConfig.notifyEOS(sourcename, src.getSoundSequenceQueueSize());
                    




                    if (src.checkFadeOut())
                    {



                      preLoad = true;


                    }
                    else
                    {


                      src.incrementSoundSequence();
                      preLoad = true;
                    }
                    
                  }
                  

                }
                else if (!src.playing())
                {

                  SoundSystemConfig.notifyEOS(sourcename, src.getSoundSequenceQueueSize());
                  




                  if (!src.checkFadeOut())
                  {



                    if (src.incrementSoundSequence())
                    {
                      preLoad = true;
                    } else {
                      iter.remove();
                    }
                  }
                }
              }
            }
          }
        }
        
        if ((!dying()) && (!streamingSources.isEmpty()))
          snooze(20L);
      }
      if ((!dying()) && (streamingSources.isEmpty())) {
        snooze(3600000L);
      }
    }
    cleanup();
  }
  







  public void watch(Source source)
  {
    if (source == null) {
      return;
    }
    
    if (streamingSources.contains(source)) {
      return;
    }
    



    synchronized (listLock)
    {



      ListIterator<Source> iter = streamingSources.listIterator();
      while (iter.hasNext())
      {
        Source src = (Source)iter.next();
        if (src == null)
        {
          iter.remove();
        }
        else if (channel == channel)
        {
          src.stop();
          iter.remove();
        }
      }
      

      streamingSources.add(source);
    }
  }
  




  private void message(String message)
  {
    logger.message(message, 0);
  }
  




  private void importantMessage(String message)
  {
    logger.importantMessage(message, 0);
  }
  






  private boolean errorCheck(boolean error, String message)
  {
    return logger.errorCheck(error, "StreamThread", message, 0);
  }
  




  private void errorMessage(String message)
  {
    logger.errorMessage("StreamThread", message, 0);
  }
}
