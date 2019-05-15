package paulscode.sound;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.sound.sampled.AudioFormat;























































public class Library
{
  private SoundSystemLogger logger;
  protected ListenerData listener;
  protected HashMap<String, SoundBuffer> bufferMap = null;
  



  protected HashMap<String, Source> sourceMap;
  



  private MidiChannel midiChannel;
  



  protected List<Channel> streamingChannels;
  



  protected List<Channel> normalChannels;
  



  private String[] streamingChannelSourceNames;
  



  private String[] normalChannelSourceNames;
  



  private int nextStreamingChannel = 0;
  



  private int nextNormalChannel = 0;
  



  protected StreamThread streamThread;
  



  protected boolean reverseByteOrder = false;
  






  public Library()
    throws SoundSystemException
  {
    logger = SoundSystemConfig.getLogger();
    

    bufferMap = new HashMap();
    

    sourceMap = new HashMap();
    
    listener = new ListenerData(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.0F, 0.0F, 1.0F, 0.0F, 0.0F);
    



    streamingChannels = new LinkedList();
    normalChannels = new LinkedList();
    streamingChannelSourceNames = new String[SoundSystemConfig.getNumberStreamingChannels()];
    
    normalChannelSourceNames = new String[SoundSystemConfig.getNumberNormalChannels()];
    

    streamThread = new StreamThread();
    streamThread.start();
  }
  











  public void cleanup()
  {
    streamThread.kill();
    streamThread.interrupt();
    

    for (int i = 0; i < 50; i++)
    {
      if (!streamThread.alive()) {
        break;
      }
      try {
        Thread.sleep(100L);
      }
      catch (Exception e) {}
    }
    

    if (streamThread.alive())
    {
      errorMessage("Stream thread did not die!");
      message("Ignoring errors... continuing clean-up.");
    }
    
    if (midiChannel != null)
    {
      midiChannel.cleanup();
      midiChannel = null;
    }
    
    Channel channel = null;
    if (streamingChannels != null)
    {
      while (!streamingChannels.isEmpty())
      {
        channel = (Channel)streamingChannels.remove(0);
        channel.close();
        channel.cleanup();
        channel = null;
      }
      streamingChannels.clear();
      streamingChannels = null;
    }
    if (normalChannels != null)
    {
      while (!normalChannels.isEmpty())
      {
        channel = (Channel)normalChannels.remove(0);
        channel.close();
        channel.cleanup();
        channel = null;
      }
      normalChannels.clear();
      normalChannels = null;
    }
    
    Set<String> keys = sourceMap.keySet();
    Iterator<String> iter = keys.iterator();
    



    while (iter.hasNext())
    {
      String sourcename = (String)iter.next();
      Source source = (Source)sourceMap.get(sourcename);
      if (source != null)
        source.cleanup();
    }
    sourceMap.clear();
    sourceMap = null;
    
    listener = null;
    streamThread = null;
  }
  


  public void init()
    throws SoundSystemException
  {
    Channel channel = null;
    

    for (int x = 0; x < SoundSystemConfig.getNumberStreamingChannels(); x++)
    {
      channel = createChannel(1);
      if (channel == null)
        break;
      streamingChannels.add(channel);
    }
    
    for (int x = 0; x < SoundSystemConfig.getNumberNormalChannels(); x++)
    {
      channel = createChannel(0);
      if (channel == null)
        break;
      normalChannels.add(channel);
    }
  }
  




  public static boolean libraryCompatible()
  {
    return true;
  }
  







  protected Channel createChannel(int type)
  {
    return new Channel(type);
  }
  





  public boolean loadSound(FilenameURL filenameURL)
  {
    return true;
  }
  








  public boolean loadSound(SoundBuffer buffer, String identifier)
  {
    return true;
  }
  




  public LinkedList<String> getAllLoadedFilenames()
  {
    LinkedList<String> filenames = new LinkedList();
    Set<String> keys = bufferMap.keySet();
    Iterator<String> iter = keys.iterator();
    

    while (iter.hasNext())
    {
      filenames.add(iter.next());
    }
    
    return filenames;
  }
  




  public LinkedList<String> getAllSourcenames()
  {
    LinkedList<String> sourcenames = new LinkedList();
    Set<String> keys = sourceMap.keySet();
    Iterator<String> iter = keys.iterator();
    
    if (midiChannel != null) {
      sourcenames.add(midiChannel.getSourcename());
    }
    
    while (iter.hasNext())
    {
      sourcenames.add(iter.next());
    }
    
    return sourcenames;
  }
  







  public void unloadSound(String filename)
  {
    bufferMap.remove(filename);
  }
  












  public void rawDataStream(AudioFormat audioFormat, boolean priority, String sourcename, float posX, float posY, float posZ, int attModel, float distOrRoll)
  {
    sourceMap.put(sourcename, new Source(audioFormat, priority, sourcename, posX, posY, posZ, attModel, distOrRoll));
  }
  


















  public void newSource(boolean priority, boolean toStream, boolean toLoop, String sourcename, FilenameURL filenameURL, float posX, float posY, float posZ, int attModel, float distOrRoll)
  {
    sourceMap.put(sourcename, new Source(priority, toStream, toLoop, sourcename, filenameURL, null, posX, posY, posZ, attModel, distOrRoll, false));
  }
  




















  public void quickPlay(boolean priority, boolean toStream, boolean toLoop, String sourcename, FilenameURL filenameURL, float posX, float posY, float posZ, int attModel, float distOrRoll, boolean tmp)
  {
    sourceMap.put(sourcename, new Source(priority, toStream, toLoop, sourcename, filenameURL, null, posX, posY, posZ, attModel, distOrRoll, tmp));
  }
  










  public void setTemporary(String sourcename, boolean temporary)
  {
    Source mySource = (Source)sourceMap.get(sourcename);
    if (mySource != null) {
      mySource.setTemporary(temporary);
    }
  }
  






  public void setPosition(String sourcename, float x, float y, float z)
  {
    Source mySource = (Source)sourceMap.get(sourcename);
    if (mySource != null) {
      mySource.setPosition(x, y, z);
    }
  }
  





  public void setPriority(String sourcename, boolean pri)
  {
    Source mySource = (Source)sourceMap.get(sourcename);
    if (mySource != null) {
      mySource.setPriority(pri);
    }
  }
  





  public void setLooping(String sourcename, boolean lp)
  {
    Source mySource = (Source)sourceMap.get(sourcename);
    if (mySource != null) {
      mySource.setLooping(lp);
    }
  }
  




  public void setAttenuation(String sourcename, int model)
  {
    Source mySource = (Source)sourceMap.get(sourcename);
    if (mySource != null) {
      mySource.setAttenuation(model);
    }
  }
  




  public void setDistOrRoll(String sourcename, float dr)
  {
    Source mySource = (Source)sourceMap.get(sourcename);
    if (mySource != null) {
      mySource.setDistOrRoll(dr);
    }
  }
  






  public void setVelocity(String sourcename, float x, float y, float z)
  {
    Source mySource = (Source)sourceMap.get(sourcename);
    if (mySource != null) {
      mySource.setVelocity(x, y, z);
    }
  }
  





  public void setListenerVelocity(float x, float y, float z)
  {
    listener.setVelocity(x, y, z);
  }
  




  public void dopplerChanged() {}
  




  public float millisecondsPlayed(String sourcename)
  {
    if ((sourcename == null) || (sourcename.equals("")))
    {
      errorMessage("Sourcename not specified in method 'millisecondsPlayed'");
      
      return -1.0F;
    }
    
    if (midiSourcename(sourcename))
    {
      errorMessage("Unable to calculate milliseconds for MIDI source.");
      return -1.0F;
    }
    

    Source source = (Source)sourceMap.get(sourcename);
    if (source == null)
    {
      errorMessage("Source '" + sourcename + "' not found in " + "method 'millisecondsPlayed'");
    }
    
    return source.millisecondsPlayed();
  }
  








  public int feedRawAudioData(String sourcename, byte[] buffer)
  {
    if ((sourcename == null) || (sourcename.equals("")))
    {
      errorMessage("Sourcename not specified in method 'feedRawAudioData'");
      
      return -1;
    }
    
    if (midiSourcename(sourcename))
    {
      errorMessage("Raw audio data can not be fed to the MIDI channel.");
      
      return -1;
    }
    

    Source source = (Source)sourceMap.get(sourcename);
    if (source == null)
    {
      errorMessage("Source '" + sourcename + "' not found in " + "method 'feedRawAudioData'");
    }
    
    return feedRawAudioData(source, buffer);
  }
  









  public int feedRawAudioData(Source source, byte[] buffer)
  {
    if (source == null)
    {
      errorMessage("Source parameter null in method 'feedRawAudioData'");
      
      return -1;
    }
    if (!toStream)
    {
      errorMessage("Only a streaming source may be specified in method 'feedRawAudioData'");
      
      return -1;
    }
    if (!rawDataStream)
    {
      errorMessage("Streaming source already associated with a file or URL in method'feedRawAudioData'");
      
      return -1;
    }
    
    if ((!source.playing()) || (channel == null)) {
      Channel channel;
      Channel channel;
      if ((channel != null) && (channel.attachedSource == source))
      {
        channel = channel;
      } else {
        channel = getNextChannel(source);
      }
      int processed = source.feedRawAudioData(channel, buffer);
      attachedSource = source;
      streamThread.watch(source);
      streamThread.interrupt();
      return processed;
    }
    
    return source.feedRawAudioData(channel, buffer);
  }
  




  public void play(String sourcename)
  {
    if ((sourcename == null) || (sourcename.equals("")))
    {
      errorMessage("Sourcename not specified in method 'play'");
      return;
    }
    
    if (midiSourcename(sourcename))
    {
      midiChannel.play();
    }
    else
    {
      Source source = (Source)sourceMap.get(sourcename);
      if (source == null)
      {
        errorMessage("Source '" + sourcename + "' not found in " + "method 'play'");
      }
      
      play(source);
    }
  }
  




  public void play(Source source)
  {
    if (source == null) {
      return;
    }
    

    if (rawDataStream) {
      return;
    }
    if (!source.active()) {
      return;
    }
    if (!source.playing())
    {
      Channel channel = getNextChannel(source);
      
      if ((source != null) && (channel != null))
      {
        if ((channel != null) && (channel.attachedSource != source))
        {
          channel = null; }
        attachedSource = source;
        source.play(channel);
        if (toStream)
        {
          streamThread.watch(source);
          streamThread.interrupt();
        }
      }
    }
  }
  




  public void stop(String sourcename)
  {
    if ((sourcename == null) || (sourcename.equals("")))
    {
      errorMessage("Sourcename not specified in method 'stop'");
      return;
    }
    if (midiSourcename(sourcename))
    {
      midiChannel.stop();
    }
    else
    {
      Source mySource = (Source)sourceMap.get(sourcename);
      if (mySource != null) {
        mySource.stop();
      }
    }
  }
  



  public void pause(String sourcename)
  {
    if ((sourcename == null) || (sourcename.equals("")))
    {
      errorMessage("Sourcename not specified in method 'stop'");
      return;
    }
    if (midiSourcename(sourcename))
    {
      midiChannel.pause();
    }
    else
    {
      Source mySource = (Source)sourceMap.get(sourcename);
      if (mySource != null) {
        mySource.pause();
      }
    }
  }
  



  public void rewind(String sourcename)
  {
    if (midiSourcename(sourcename))
    {
      midiChannel.rewind();
    }
    else
    {
      Source mySource = (Source)sourceMap.get(sourcename);
      if (mySource != null) {
        mySource.rewind();
      }
    }
  }
  



  public void flush(String sourcename)
  {
    if (midiSourcename(sourcename)) {
      errorMessage("You can not flush the MIDI channel");
    }
    else {
      Source mySource = (Source)sourceMap.get(sourcename);
      if (mySource != null) {
        mySource.flush();
      }
    }
  }
  




  public void cull(String sourcename)
  {
    Source mySource = (Source)sourceMap.get(sourcename);
    if (mySource != null) {
      mySource.cull();
    }
  }
  



  public void activate(String sourcename)
  {
    Source mySource = (Source)sourceMap.get(sourcename);
    if (mySource != null)
    {
      mySource.activate();
      if (toPlay) {
        play(mySource);
      }
    }
  }
  



  public void setMasterVolume(float value)
  {
    SoundSystemConfig.setMasterGain(value);
    if (midiChannel != null) {
      midiChannel.resetGain();
    }
  }
  




  public void setVolume(String sourcename, float value)
  {
    if (midiSourcename(sourcename))
    {
      midiChannel.setVolume(value);
    }
    else
    {
      Source mySource = (Source)sourceMap.get(sourcename);
      if (mySource != null)
      {
        float newVolume = value;
        if (newVolume < 0.0F) {
          newVolume = 0.0F;
        } else if (newVolume > 1.0F) {
          newVolume = 1.0F;
        }
        sourceVolume = newVolume;
        mySource.positionChanged();
      }
    }
  }
  






  public float getVolume(String sourcename)
  {
    if (midiSourcename(sourcename))
    {
      return midiChannel.getVolume();
    }
    

    Source mySource = (Source)sourceMap.get(sourcename);
    if (mySource != null) {
      return sourceVolume;
    }
    return 0.0F;
  }
  






  public void setPitch(String sourcename, float value)
  {
    if (!midiSourcename(sourcename))
    {
      Source mySource = (Source)sourceMap.get(sourcename);
      if (mySource != null)
      {
        float newPitch = value;
        if (newPitch < 0.5F) {
          newPitch = 0.5F;
        } else if (newPitch > 2.0F) {
          newPitch = 2.0F;
        }
        mySource.setPitch(newPitch);
        mySource.positionChanged();
      }
    }
  }
  





  public float getPitch(String sourcename)
  {
    if (!midiSourcename(sourcename))
    {
      Source mySource = (Source)sourceMap.get(sourcename);
      if (mySource != null)
        return mySource.getPitch();
    }
    return 1.0F;
  }
  






  public void moveListener(float x, float y, float z)
  {
    setListenerPosition(listener.position.x + x, listener.position.y + y, listener.position.z + z);
  }
  








  public void setListenerPosition(float x, float y, float z)
  {
    listener.setPosition(x, y, z);
    
    Set<String> keys = sourceMap.keySet();
    Iterator<String> iter = keys.iterator();
    



    while (iter.hasNext())
    {
      String sourcename = (String)iter.next();
      Source source = (Source)sourceMap.get(sourcename);
      if (source != null) {
        source.positionChanged();
      }
    }
  }
  




  public void turnListener(float angle)
  {
    setListenerAngle(listener.angle + angle);
    
    Set<String> keys = sourceMap.keySet();
    Iterator<String> iter = keys.iterator();
    



    while (iter.hasNext())
    {
      String sourcename = (String)iter.next();
      Source source = (Source)sourceMap.get(sourcename);
      if (source != null) {
        source.positionChanged();
      }
    }
  }
  




  public void setListenerAngle(float angle)
  {
    listener.setAngle(angle);
    
    Set<String> keys = sourceMap.keySet();
    Iterator<String> iter = keys.iterator();
    



    while (iter.hasNext())
    {
      String sourcename = (String)iter.next();
      Source source = (Source)sourceMap.get(sourcename);
      if (source != null) {
        source.positionChanged();
      }
    }
  }
  









  public void setListenerOrientation(float lookX, float lookY, float lookZ, float upX, float upY, float upZ)
  {
    listener.setOrientation(lookX, lookY, lookZ, upX, upY, upZ);
    
    Set<String> keys = sourceMap.keySet();
    Iterator<String> iter = keys.iterator();
    



    while (iter.hasNext())
    {
      String sourcename = (String)iter.next();
      Source source = (Source)sourceMap.get(sourcename);
      if (source != null) {
        source.positionChanged();
      }
    }
  }
  




  public void setListenerData(ListenerData l)
  {
    listener.setData(l);
  }
  




  public void copySources(HashMap<String, Source> srcMap)
  {
    if (srcMap == null)
      return;
    Set<String> keys = srcMap.keySet();
    Iterator<String> iter = keys.iterator();
    



    sourceMap.clear();
    

    while (iter.hasNext())
    {
      String sourcename = (String)iter.next();
      Source srcData = (Source)srcMap.get(sourcename);
      if (srcData != null)
      {
        loadSound(filenameURL);
        sourceMap.put(sourcename, new Source(srcData, null));
      }
    }
  }
  




  public void removeSource(String sourcename)
  {
    Source mySource = (Source)sourceMap.get(sourcename);
    if (mySource != null)
      mySource.cleanup();
    sourceMap.remove(sourcename);
  }
  



  public void removeTemporarySources()
  {
    Set<String> keys = sourceMap.keySet();
    Iterator<String> iter = keys.iterator();
    



    while (iter.hasNext())
    {
      String sourcename = (String)iter.next();
      Source srcData = (Source)sourceMap.get(sourcename);
      if ((srcData != null) && (temporary) && (!srcData.playing()))
      {

        srcData.cleanup();
        iter.remove();
      }
    }
  }
  














  private Channel getNextChannel(Source source)
  {
    if (source == null) {
      return null;
    }
    String sourcename = sourcename;
    if (sourcename == null) {
      return null;
    }
    
    String[] sourceNames;
    
    int nextChannel;
    
    List<Channel> channelList;
    String[] sourceNames;
    if (toStream)
    {
      int nextChannel = nextStreamingChannel;
      List<Channel> channelList = streamingChannels;
      sourceNames = streamingChannelSourceNames;
    }
    else
    {
      nextChannel = nextNormalChannel;
      channelList = normalChannels;
      sourceNames = normalChannelSourceNames;
    }
    
    int channels = channelList.size();
    

    for (int x = 0; x < channels; x++)
    {
      if (sourcename.equals(sourceNames[x])) {
        return (Channel)channelList.get(x);
      }
    }
    int n = nextChannel;
    

    for (x = 0; x < channels; x++)
    {
      String name = sourceNames[n];
      Source src; Source src; if (name == null) {
        src = null;
      } else {
        src = (Source)sourceMap.get(name);
      }
      if ((src == null) || (!src.playing()))
      {
        if (toStream)
        {
          nextStreamingChannel = (n + 1);
          if (nextStreamingChannel >= channels) {
            nextStreamingChannel = 0;
          }
        }
        else {
          nextNormalChannel = (n + 1);
          if (nextNormalChannel >= channels)
            nextNormalChannel = 0;
        }
        sourceNames[n] = sourcename;
        return (Channel)channelList.get(n);
      }
      n++;
      if (n >= channels) {
        n = 0;
      }
    }
    n = nextChannel;
    
    for (x = 0; x < channels; x++)
    {
      String name = sourceNames[n];
      Source src; Source src; if (name == null) {
        src = null;
      } else {
        src = (Source)sourceMap.get(name);
      }
      if ((src == null) || (!src.playing()) || (!priority))
      {
        if (toStream)
        {
          nextStreamingChannel = (n + 1);
          if (nextStreamingChannel >= channels) {
            nextStreamingChannel = 0;
          }
        }
        else {
          nextNormalChannel = (n + 1);
          if (nextNormalChannel >= channels)
            nextNormalChannel = 0;
        }
        sourceNames[n] = sourcename;
        return (Channel)channelList.get(n);
      }
      n++;
      if (n >= channels) {
        n = 0;
      }
    }
    return null;
  }
  





  public void replaySources()
  {
    Set<String> keys = sourceMap.keySet();
    Iterator<String> iter = keys.iterator();
    



    while (iter.hasNext())
    {
      String sourcename = (String)iter.next();
      Source source = (Source)sourceMap.get(sourcename);
      if ((source != null) && 
      
        (toPlay) && (!source.playing()))
      {
        play(sourcename);
        toPlay = false;
      }
    }
  }
  








  public void queueSound(String sourcename, FilenameURL filenameURL)
  {
    if (midiSourcename(sourcename))
    {
      midiChannel.queueSound(filenameURL);
    }
    else
    {
      Source mySource = (Source)sourceMap.get(sourcename);
      if (mySource != null) {
        mySource.queueSound(filenameURL);
      }
    }
  }
  






  public void dequeueSound(String sourcename, String filename)
  {
    if (midiSourcename(sourcename))
    {
      midiChannel.dequeueSound(filename);
    }
    else
    {
      Source mySource = (Source)sourceMap.get(sourcename);
      if (mySource != null) {
        mySource.dequeueSound(filename);
      }
    }
  }
  













  public void fadeOut(String sourcename, FilenameURL filenameURL, long milis)
  {
    if (midiSourcename(sourcename))
    {
      midiChannel.fadeOut(filenameURL, milis);
    }
    else
    {
      Source mySource = (Source)sourceMap.get(sourcename);
      if (mySource != null) {
        mySource.fadeOut(filenameURL, milis);
      }
    }
  }
  














  public void fadeOutIn(String sourcename, FilenameURL filenameURL, long milisOut, long milisIn)
  {
    if (midiSourcename(sourcename))
    {
      midiChannel.fadeOutIn(filenameURL, milisOut, milisIn);
    }
    else
    {
      Source mySource = (Source)sourceMap.get(sourcename);
      if (mySource != null) {
        mySource.fadeOutIn(filenameURL, milisOut, milisIn);
      }
    }
  }
  










  public void checkFadeVolumes()
  {
    if (midiChannel != null) {
      midiChannel.resetGain();
    }
    
    for (int x = 0; x < streamingChannels.size(); x++)
    {
      Channel c = (Channel)streamingChannels.get(x);
      if (c != null)
      {
        Source s = attachedSource;
        if (s != null)
          s.checkFadeOut();
      }
    }
    Channel c = null;
    Source s = null;
  }
  







  public void loadMidi(boolean toLoop, String sourcename, FilenameURL filenameURL)
  {
    if (filenameURL == null)
    {
      errorMessage("Filename/URL not specified in method 'loadMidi'.");
      return;
    }
    
    if (!filenameURL.getFilename().matches(SoundSystemConfig.EXTENSION_MIDI))
    {

      errorMessage("Filename/identifier doesn't end in '.mid' or'.midi' in method loadMidi.");
      
      return;
    }
    
    if (midiChannel == null)
    {
      midiChannel = new MidiChannel(toLoop, sourcename, filenameURL);
    }
    else
    {
      midiChannel.switchSource(toLoop, sourcename, filenameURL);
    }
  }
  



  public void unloadMidi()
  {
    if (midiChannel != null)
      midiChannel.cleanup();
    midiChannel = null;
  }
  





  public boolean midiSourcename(String sourcename)
  {
    if ((midiChannel == null) || (sourcename == null)) {
      return false;
    }
    if ((midiChannel.getSourcename() == null) || (sourcename.equals(""))) {
      return false;
    }
    if (sourcename.equals(midiChannel.getSourcename())) {
      return true;
    }
    return false;
  }
  






  public Source getSource(String sourcename)
  {
    return (Source)sourceMap.get(sourcename);
  }
  





  public MidiChannel getMidiChannel()
  {
    return midiChannel;
  }
  





  public void setMidiChannel(MidiChannel c)
  {
    if ((midiChannel != null) && (midiChannel != c)) {
      midiChannel.cleanup();
    }
    midiChannel = c;
  }
  



  public void listenerMoved()
  {
    Set<String> keys = sourceMap.keySet();
    Iterator<String> iter = keys.iterator();
    



    while (iter.hasNext())
    {
      String sourcename = (String)iter.next();
      Source srcData = (Source)sourceMap.get(sourcename);
      if (srcData != null)
      {
        srcData.listenerMoved();
      }
    }
  }
  




  public HashMap<String, Source> getSources()
  {
    return sourceMap;
  }
  




  public ListenerData getListenerData()
  {
    return listener;
  }
  





  public boolean reverseByteOrder()
  {
    return reverseByteOrder;
  }
  



  public static String getTitle()
  {
    return "No Sound";
  }
  




  public static String getDescription()
  {
    return "Silent Mode";
  }
  




  public String getClassName()
  {
    return "Library";
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
