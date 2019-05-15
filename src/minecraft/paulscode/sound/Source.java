package paulscode.sound;

import java.net.URL;
import java.util.LinkedList;
import java.util.ListIterator;
import javax.sound.sampled.AudioFormat;












































public class Source
{
  protected Class libraryType = Library.class;
  




  private static final boolean GET = false;
  




  private static final boolean SET = true;
  



  private static final boolean XXX = false;
  



  private SoundSystemLogger logger;
  



  public boolean rawDataStream = false;
  



  public AudioFormat rawDataFormat = null;
  



  public boolean temporary = false;
  




  public boolean priority = false;
  



  public boolean toStream = false;
  



  public boolean toLoop = false;
  




  public boolean toPlay = false;
  




  public String sourcename = "";
  



  public FilenameURL filenameURL = null;
  



  public Vector3D position;
  



  public int attModel = 0;
  



  public float distOrRoll = 0.0F;
  




  public Vector3D velocity;
  




  public float gain = 1.0F;
  



  public float sourceVolume = 1.0F;
  



  protected float pitch = 1.0F;
  



  public float distanceFromListener = 0.0F;
  



  public Channel channel = null;
  



  public SoundBuffer soundBuffer = null;
  



  private boolean active = true;
  



  private boolean stopped = true;
  



  private boolean paused = false;
  



  protected ICodec codec = null;
  



  protected ICodec nextCodec = null;
  



  protected LinkedList<SoundBuffer> nextBuffers = null;
  




  protected LinkedList<FilenameURL> soundSequenceQueue = null;
  



  protected final Object soundSequenceLock = new Object();
  




  public boolean preLoad = false;
  




  protected float fadeOutGain = -1.0F;
  




  protected float fadeInGain = 1.0F;
  



  protected long fadeOutMilis = 0L;
  



  protected long fadeInMilis = 0L;
  



  protected long lastFadeCheck = 0L;
  



















  public Source(boolean priority, boolean toStream, boolean toLoop, String sourcename, FilenameURL filenameURL, SoundBuffer soundBuffer, float x, float y, float z, int attModel, float distOrRoll, boolean temporary)
  {
    logger = SoundSystemConfig.getLogger();
    
    this.priority = priority;
    this.toStream = toStream;
    this.toLoop = toLoop;
    this.sourcename = sourcename;
    this.filenameURL = filenameURL;
    this.soundBuffer = soundBuffer;
    position = new Vector3D(x, y, z);
    this.attModel = attModel;
    this.distOrRoll = distOrRoll;
    velocity = new Vector3D(0.0F, 0.0F, 0.0F);
    this.temporary = temporary;
    
    if ((toStream) && (filenameURL != null)) {
      codec = SoundSystemConfig.getCodec(filenameURL.getFilename());
    }
  }
  





  public Source(Source old, SoundBuffer soundBuffer)
  {
    logger = SoundSystemConfig.getLogger();
    
    priority = priority;
    toStream = toStream;
    toLoop = toLoop;
    sourcename = sourcename;
    filenameURL = filenameURL;
    position = position.clone();
    attModel = attModel;
    distOrRoll = distOrRoll;
    velocity = velocity.clone();
    temporary = temporary;
    
    sourceVolume = sourceVolume;
    
    rawDataStream = rawDataStream;
    rawDataFormat = rawDataFormat;
    
    this.soundBuffer = soundBuffer;
    
    if ((toStream) && (filenameURL != null)) {
      codec = SoundSystemConfig.getCodec(filenameURL.getFilename());
    }
  }
  













  public Source(AudioFormat audioFormat, boolean priority, String sourcename, float x, float y, float z, int attModel, float distOrRoll)
  {
    logger = SoundSystemConfig.getLogger();
    
    this.priority = priority;
    toStream = true;
    toLoop = false;
    this.sourcename = sourcename;
    filenameURL = null;
    soundBuffer = null;
    position = new Vector3D(x, y, z);
    this.attModel = attModel;
    this.distOrRoll = distOrRoll;
    velocity = new Vector3D(0.0F, 0.0F, 0.0F);
    temporary = false;
    
    rawDataStream = true;
    rawDataFormat = audioFormat;
  }
  




  public void cleanup()
  {
    if (codec != null) {
      codec.cleanup();
    }
    synchronized (soundSequenceLock)
    {
      if (soundSequenceQueue != null)
        soundSequenceQueue.clear();
      soundSequenceQueue = null;
    }
    
    sourcename = null;
    filenameURL = null;
    position = null;
    soundBuffer = null;
    codec = null;
  }
  






  public void queueSound(FilenameURL filenameURL)
  {
    if (!toStream)
    {
      errorMessage("Method 'queueSound' may only be used for streaming and MIDI sources.");
      
      return;
    }
    if (filenameURL == null)
    {
      errorMessage("File not specified in method 'queueSound'");
      return;
    }
    
    synchronized (soundSequenceLock)
    {
      if (soundSequenceQueue == null)
        soundSequenceQueue = new LinkedList();
      soundSequenceQueue.add(filenameURL);
    }
  }
  






  public void dequeueSound(String filename)
  {
    if (!toStream)
    {
      errorMessage("Method 'dequeueSound' may only be used for streaming and MIDI sources.");
      
      return;
    }
    if ((filename == null) || (filename.equals("")))
    {
      errorMessage("Filename not specified in method 'dequeueSound'");
      return;
    }
    
    synchronized (soundSequenceLock)
    {
      if (soundSequenceQueue != null)
      {
        ListIterator<FilenameURL> i = soundSequenceQueue.listIterator();
        while (i.hasNext())
        {
          if (((FilenameURL)i.next()).getFilename().equals(filename))
          {
            i.remove();
          }
        }
      }
    }
  }
  












  public void fadeOut(FilenameURL filenameURL, long milis)
  {
    if (!toStream)
    {
      errorMessage("Method 'fadeOut' may only be used for streaming and MIDI sources.");
      
      return;
    }
    if (milis < 0L)
    {
      errorMessage("Miliseconds may not be negative in method 'fadeOut'.");
      
      return;
    }
    
    fadeOutMilis = milis;
    fadeInMilis = 0L;
    fadeOutGain = 1.0F;
    lastFadeCheck = System.currentTimeMillis();
    
    synchronized (soundSequenceLock)
    {
      if (soundSequenceQueue != null) {
        soundSequenceQueue.clear();
      }
      if (filenameURL != null)
      {
        if (soundSequenceQueue == null)
          soundSequenceQueue = new LinkedList();
        soundSequenceQueue.add(filenameURL);
      }
    }
  }
  













  public void fadeOutIn(FilenameURL filenameURL, long milisOut, long milisIn)
  {
    if (!toStream)
    {
      errorMessage("Method 'fadeOutIn' may only be used for streaming and MIDI sources.");
      
      return;
    }
    if (filenameURL == null)
    {
      errorMessage("Filename/URL not specified in method 'fadeOutIn'.");
      return;
    }
    if ((milisOut < 0L) || (milisIn < 0L))
    {
      errorMessage("Miliseconds may not be negative in method 'fadeOutIn'.");
      
      return;
    }
    
    fadeOutMilis = milisOut;
    fadeInMilis = milisIn;
    
    fadeOutGain = 1.0F;
    lastFadeCheck = System.currentTimeMillis();
    
    synchronized (soundSequenceLock)
    {
      if (soundSequenceQueue == null)
        soundSequenceQueue = new LinkedList();
      soundSequenceQueue.clear();
      soundSequenceQueue.add(filenameURL);
    }
  }
  







  public boolean checkFadeOut()
  {
    if (!toStream) {
      return false;
    }
    if ((fadeOutGain == -1.0F) && (fadeInGain == 1.0F)) {
      return false;
    }
    long currentTime = System.currentTimeMillis();
    long milisPast = currentTime - lastFadeCheck;
    lastFadeCheck = currentTime;
    
    if (fadeOutGain >= 0.0F)
    {
      if (fadeOutMilis == 0L)
      {
        fadeOutGain = -1.0F;
        fadeInGain = 0.0F;
        if (!incrementSoundSequence())
        {
          stop();
        }
        positionChanged();
        preLoad = true;
        return false;
      }
      

      float fadeOutReduction = (float)milisPast / (float)fadeOutMilis;
      fadeOutGain -= fadeOutReduction;
      if (fadeOutGain <= 0.0F)
      {
        fadeOutGain = -1.0F;
        fadeInGain = 0.0F;
        if (!incrementSoundSequence())
          stop();
        positionChanged();
        preLoad = true;
        return false;
      }
      
      positionChanged();
      return true;
    }
    
    if (fadeInGain < 1.0F)
    {
      fadeOutGain = -1.0F;
      if (fadeInMilis == 0L)
      {
        fadeOutGain = -1.0F;
        fadeInGain = 1.0F;
      }
      else
      {
        float fadeInIncrease = (float)milisPast / (float)fadeInMilis;
        fadeInGain += fadeInIncrease;
        if (fadeInGain >= 1.0F)
        {
          fadeOutGain = -1.0F;
          fadeInGain = 1.0F;
        }
      }
      positionChanged();
      return true;
    }
    return false;
  }
  







  public boolean incrementSoundSequence()
  {
    if (!toStream)
    {
      errorMessage("Method 'incrementSoundSequence' may only be used for streaming and MIDI sources.");
      
      return false;
    }
    
    synchronized (soundSequenceLock)
    {
      if ((soundSequenceQueue != null) && (soundSequenceQueue.size() > 0))
      {
        filenameURL = ((FilenameURL)soundSequenceQueue.remove(0));
        if (codec != null)
          codec.cleanup();
        codec = SoundSystemConfig.getCodec(filenameURL.getFilename());
        return true;
      }
    }
    return false;
  }
  







  public boolean readBuffersFromNextSoundInSequence()
  {
    if (!toStream)
    {
      errorMessage("Method 'readBuffersFromNextSoundInSequence' may only be used for streaming sources.");
      
      return false;
    }
    
    synchronized (soundSequenceLock)
    {
      if ((soundSequenceQueue != null) && (soundSequenceQueue.size() > 0))
      {
        if (nextCodec != null)
          nextCodec.cleanup();
        nextCodec = SoundSystemConfig.getCodec(((FilenameURL)soundSequenceQueue.get(0)).getFilename());
        
        nextCodec.initialize(((FilenameURL)soundSequenceQueue.get(0)).getURL());
        
        SoundBuffer buffer = null;
        for (int i = 0; 
            
            (i < SoundSystemConfig.getNumberStreamingBuffers()) && (!nextCodec.endOfStream()); 
            i++)
        {
          buffer = nextCodec.read();
          if (buffer != null)
          {
            if (nextBuffers == null)
              nextBuffers = new LinkedList();
            nextBuffers.add(buffer);
          }
        }
        return true;
      }
    }
    return false;
  }
  





  public int getSoundSequenceQueueSize()
  {
    if (soundSequenceQueue == null)
      return 0;
    return soundSequenceQueue.size();
  }
  




  public void setTemporary(boolean tmp)
  {
    temporary = tmp;
  }
  





  public void listenerMoved() {}
  





  public void setPosition(float x, float y, float z)
  {
    position.x = x;
    position.y = y;
    position.z = z;
  }
  





  public void positionChanged() {}
  





  public void setPriority(boolean pri)
  {
    priority = pri;
  }
  




  public void setLooping(boolean lp)
  {
    toLoop = lp;
  }
  




  public void setAttenuation(int model)
  {
    attModel = model;
  }
  





  public void setDistOrRoll(float dr)
  {
    distOrRoll = dr;
  }
  






  public void setVelocity(float x, float y, float z)
  {
    velocity.x = x;
    velocity.y = y;
    velocity.z = z;
  }
  




  public float getDistanceFromListener()
  {
    return distanceFromListener;
  }
  




  public void setPitch(float value)
  {
    float newPitch = value;
    if (newPitch < 0.5F) {
      newPitch = 0.5F;
    } else if (newPitch > 2.0F)
      newPitch = 2.0F;
    pitch = newPitch;
  }
  




  public float getPitch()
  {
    return pitch;
  }
  





  public boolean reverseByteOrder()
  {
    return SoundSystemConfig.reverseByteOrder(libraryType);
  }
  


















  public void changeSource(boolean priority, boolean toStream, boolean toLoop, String sourcename, FilenameURL filenameURL, SoundBuffer soundBuffer, float x, float y, float z, int attModel, float distOrRoll, boolean temporary)
  {
    this.priority = priority;
    this.toStream = toStream;
    this.toLoop = toLoop;
    this.sourcename = sourcename;
    this.filenameURL = filenameURL;
    this.soundBuffer = soundBuffer;
    position.x = x;
    position.y = y;
    position.z = z;
    this.attModel = attModel;
    this.distOrRoll = distOrRoll;
    this.temporary = temporary;
  }
  






  public int feedRawAudioData(Channel c, byte[] buffer)
  {
    if (!active(false, false))
    {
      toPlay = true;
      return -1;
    }
    if (channel != c)
    {
      channel = c;
      channel.close();
      channel.setAudioFormat(rawDataFormat);
      positionChanged();
    }
    

    stopped(true, false);
    paused(true, false);
    
    return channel.feedRawAudioData(buffer);
  }
  




  public void play(Channel c)
  {
    if (!active(false, false))
    {
      if (toLoop)
        toPlay = true;
      return;
    }
    if (channel != c)
    {
      channel = c;
      channel.close();
    }
    
    stopped(true, false);
    paused(true, false);
  }
  





  public boolean stream()
  {
    if (channel == null) {
      return false;
    }
    if (preLoad)
    {
      if (rawDataStream) {
        preLoad = false;
      } else {
        return preLoad();
      }
    }
    if (rawDataStream)
    {
      if ((stopped()) || (paused()))
        return true;
      if (channel.buffersProcessed() > 0)
        channel.processBuffer();
      return true;
    }
    

    if (codec == null)
      return false;
    if (stopped())
      return false;
    if (paused()) {
      return true;
    }
    int processed = channel.buffersProcessed();
    
    SoundBuffer buffer = null;
    for (int i = 0; i < processed; i++)
    {
      buffer = codec.read();
      if (buffer != null)
      {
        if (audioData != null)
          channel.queueBuffer(audioData);
        buffer.cleanup();
        buffer = null;
        return true;
      }
      if (codec.endOfStream())
      {
        synchronized (soundSequenceLock)
        {
          if (SoundSystemConfig.getStreamQueueFormatsMatch())
          {
            if ((soundSequenceQueue != null) && (soundSequenceQueue.size() > 0))
            {

              if (codec != null)
                codec.cleanup();
              filenameURL = ((FilenameURL)soundSequenceQueue.remove(0));
              codec = SoundSystemConfig.getCodec(filenameURL.getFilename());
              
              codec.initialize(filenameURL.getURL());
              buffer = codec.read();
              if (buffer != null)
              {
                if (audioData != null)
                  channel.queueBuffer(audioData);
                buffer.cleanup();
                buffer = null;
                return true;
              }
            }
            else if (toLoop)
            {
              codec.initialize(filenameURL.getURL());
              buffer = codec.read();
              if (buffer != null)
              {
                if (audioData != null)
                  channel.queueBuffer(audioData);
                buffer.cleanup();
                buffer = null;
                return true;
              }
            }
          }
        }
      }
    }
    



































    return false;
  }
  




  public boolean preLoad()
  {
    if (channel == null) {
      return false;
    }
    if (codec == null) {
      return false;
    }
    SoundBuffer buffer = null;
    
    boolean noNextBuffers = false;
    synchronized (soundSequenceLock)
    {
      if ((nextBuffers == null) || (nextBuffers.isEmpty())) {
        noNextBuffers = true;
      }
    }
    if ((nextCodec != null) && (!noNextBuffers))
    {
      codec = nextCodec;
      nextCodec = null;
      synchronized (soundSequenceLock)
      {
        while (!nextBuffers.isEmpty())
        {
          buffer = (SoundBuffer)nextBuffers.remove(0);
          if (buffer != null)
          {
            if (audioData != null)
              channel.queueBuffer(audioData);
            buffer.cleanup();
            buffer = null;
          }
        }
      }
    }
    else
    {
      nextCodec = null;
      URL url = filenameURL.getURL();
      
      codec.initialize(url);
      for (int i = 0; i < SoundSystemConfig.getNumberStreamingBuffers(); 
          i++)
      {
        buffer = codec.read();
        if (buffer != null)
        {
          if (audioData != null)
            channel.queueBuffer(audioData);
          buffer.cleanup();
          buffer = null;
        }
      }
    }
    
    return true;
  }
  



  public void pause()
  {
    toPlay = false;
    paused(true, true);
    if (channel != null) {
      channel.pause();
    } else {
      errorMessage("Channel null in method 'pause'");
    }
  }
  


  public void stop()
  {
    toPlay = false;
    stopped(true, true);
    paused(true, false);
    if (channel != null) {
      channel.stop();
    } else {
      errorMessage("Channel null in method 'stop'");
    }
  }
  


  public void rewind()
  {
    if (paused(false, false))
    {
      stop();
    }
    if (channel != null)
    {
      boolean rePlay = playing();
      channel.rewind();
      if ((toStream) && (rePlay))
      {
        stop();
        play(channel);
      }
    }
    else {
      errorMessage("Channel null in method 'rewind'");
    }
  }
  


  public void flush()
  {
    if (channel != null) {
      channel.flush();
    } else {
      errorMessage("Channel null in method 'flush'");
    }
  }
  



  public void cull()
  {
    if (!active(false, false))
      return;
    if ((playing()) && (toLoop))
      toPlay = true;
    if (rawDataStream)
      toPlay = true;
    active(true, false);
    if (channel != null)
      channel.close();
    channel = null;
  }
  



  public void activate()
  {
    active(true, true);
  }
  




  public boolean active()
  {
    return active(false, false);
  }
  




  public boolean playing()
  {
    if ((channel == null) || (channel.attachedSource != this))
      return false;
    if ((paused()) || (stopped())) {
      return false;
    }
    return channel.playing();
  }
  




  public boolean stopped()
  {
    return stopped(false, false);
  }
  




  public boolean paused()
  {
    return paused(false, false);
  }
  




  public float millisecondsPlayed()
  {
    if (channel == null) {
      return -1.0F;
    }
    return channel.millisecondsPlayed();
  }
  




  private synchronized boolean active(boolean action, boolean value)
  {
    if (action == true)
      active = value;
    return active;
  }
  




  private synchronized boolean stopped(boolean action, boolean value)
  {
    if (action == true)
      stopped = value;
    return stopped;
  }
  




  private synchronized boolean paused(boolean action, boolean value)
  {
    if (action == true)
      paused = value;
    return paused;
  }
  




  public String getClassName()
  {
    String libTitle = SoundSystemConfig.getLibraryTitle(libraryType);
    
    if (libTitle.equals("No Sound")) {
      return "Source";
    }
    return "Source" + libTitle;
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
