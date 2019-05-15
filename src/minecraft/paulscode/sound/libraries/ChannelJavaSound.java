package paulscode.sound.libraries;

import java.util.LinkedList;
import java.util.List;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.FloatControl.Type;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import paulscode.sound.Channel;
import paulscode.sound.SoundBuffer;
import paulscode.sound.Source;












































public class ChannelJavaSound
  extends Channel
{
  public Clip clip = null;
  




  SoundBuffer soundBuffer;
  




  public SourceDataLine sourceDataLine = null;
  



  private List<SoundBuffer> streamBuffers;
  


  private int processed = 0;
  





  private Mixer myMixer = null;
  


  private AudioFormat myFormat = null;
  



  private FloatControl gainControl = null;
  


  private FloatControl panControl = null;
  



  private FloatControl sampleRateControl = null;
  



  private float initialGain = 0.0F;
  



  private float initialSampleRate = 0.0F;
  




  private boolean toLoop = false;
  







  public ChannelJavaSound(int type, Mixer mixer)
  {
    super(type);
    libraryType = LibraryJavaSound.class;
    
    myMixer = mixer;
    clip = null;
    sourceDataLine = null;
    streamBuffers = new LinkedList();
  }
  





  public void cleanup()
  {
    if (streamBuffers != null)
    {
      SoundBuffer buf = null;
      while (!streamBuffers.isEmpty())
      {
        buf = (SoundBuffer)streamBuffers.remove(0);
        buf.cleanup();
        buf = null;
      }
      streamBuffers.clear();
    }
    
    clip = null;
    soundBuffer = null;
    sourceDataLine = null;
    streamBuffers.clear();
    myMixer = null;
    myFormat = null;
    streamBuffers = null;
    
    super.cleanup();
  }
  




  public void newMixer(Mixer m)
  {
    if (myMixer != m)
    {
      try
      {
        if (clip != null) {
          clip.close();
        } else if (sourceDataLine != null) {
          sourceDataLine.close();
        }
      }
      catch (SecurityException e) {}
      
      myMixer = m;
      if (attachedSource != null)
      {
        if ((channelType == 0) && (soundBuffer != null))
        {
          attachBuffer(soundBuffer);
        } else if (myFormat != null) {
          resetStream(myFormat);
        }
      }
    }
  }
  





  public boolean attachBuffer(SoundBuffer buffer)
  {
    if (errorCheck(channelType != 0, "Buffers may only be attached to non-streaming sources"))
    {

      return false;
    }
    
    if (errorCheck(myMixer == null, "Mixer null in method 'attachBuffer'"))
    {
      return false;
    }
    
    if (errorCheck(buffer == null, "Buffer null in method 'attachBuffer'"))
    {
      return false;
    }
    
    if (errorCheck(audioData == null, "Buffer missing audio data in method 'attachBuffer'"))
    {

      return false;
    }
    
    if (errorCheck(audioFormat == null, "Buffer missing format information in method 'attachBuffer'"))
    {

      return false;
    }
    
    DataLine.Info lineInfo = new DataLine.Info(Clip.class, audioFormat);
    if (errorCheck(!AudioSystem.isLineSupported(lineInfo), "Line not supported in method 'attachBuffer'"))
    {
      return false;
    }
    Clip newClip = null;
    try
    {
      newClip = (Clip)myMixer.getLine(lineInfo);
    }
    catch (Exception e)
    {
      errorMessage("Unable to create clip in method 'attachBuffer'");
      printStackTrace(e);
      return false;
    }
    
    if (errorCheck(newClip == null, "New clip null in method 'attachBuffer'"))
    {
      return false;
    }
    
    if (clip != null)
    {
      clip.stop();
      clip.flush();
      clip.close();
    }
    

    clip = newClip;
    soundBuffer = buffer;
    myFormat = audioFormat;
    newClip = null;
    
    try
    {
      clip.open(myFormat, audioData, 0, audioData.length);
    }
    catch (Exception e)
    {
      errorMessage("Unable to attach buffer to clip in method 'attachBuffer'");
      
      printStackTrace(e);
      return false;
    }
    
    resetControls();
    

    return true;
  }
  





  public void setAudioFormat(AudioFormat audioFormat)
  {
    resetStream(audioFormat);
    if ((attachedSource != null) && (attachedSource.rawDataStream) && (attachedSource.active()) && (sourceDataLine != null))
    {
      sourceDataLine.start();
    }
  }
  





  public boolean resetStream(AudioFormat format)
  {
    if (errorCheck(myMixer == null, "Mixer null in method 'resetStream'"))
    {
      return false;
    }
    
    if (errorCheck(format == null, "AudioFormat null in method 'resetStream'"))
    {
      return false;
    }
    
    DataLine.Info lineInfo = new DataLine.Info(SourceDataLine.class, format);
    if (errorCheck(!AudioSystem.isLineSupported(lineInfo), "Line not supported in method 'resetStream'"))
    {
      return false;
    }
    SourceDataLine newSourceDataLine = null;
    try
    {
      newSourceDataLine = (SourceDataLine)myMixer.getLine(lineInfo);
    }
    catch (Exception e)
    {
      errorMessage("Unable to create a SourceDataLine in method 'resetStream'");
      
      printStackTrace(e);
      return false;
    }
    
    if (errorCheck(newSourceDataLine == null, "New SourceDataLine null in method 'resetStream'"))
    {
      return false;
    }
    streamBuffers.clear();
    processed = 0;
    

    if (sourceDataLine != null)
    {
      sourceDataLine.stop();
      sourceDataLine.flush();
      sourceDataLine.close();
    }
    

    sourceDataLine = newSourceDataLine;
    myFormat = format;
    newSourceDataLine = null;
    
    try
    {
      sourceDataLine.open(myFormat);
    }
    catch (Exception e)
    {
      errorMessage("Unable to open the new SourceDataLine in method 'resetStream'");
      
      printStackTrace(e);
      return false;
    }
    
    resetControls();
    

    return true;
  }
  



  private void resetControls()
  {
    switch (channelType)
    {

    case 0: 
      try
      {
        if (!clip.isControlSupported(FloatControl.Type.PAN)) {
          panControl = null;
        }
        else {
          panControl = ((FloatControl)clip.getControl(FloatControl.Type.PAN));
        }
      }
      catch (IllegalArgumentException iae)
      {
        panControl = null;
      }
      
      try
      {
        if (!clip.isControlSupported(FloatControl.Type.MASTER_GAIN))
        {
          gainControl = null;
          initialGain = 0.0F;

        }
        else
        {
          gainControl = ((FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN));
          

          initialGain = gainControl.getValue();
        }
      }
      catch (IllegalArgumentException iae)
      {
        gainControl = null;
        initialGain = 0.0F;
      }
      
      try
      {
        if (!clip.isControlSupported(FloatControl.Type.SAMPLE_RATE))
        {
          sampleRateControl = null;
          initialSampleRate = 0.0F;

        }
        else
        {
          sampleRateControl = ((FloatControl)clip.getControl(FloatControl.Type.SAMPLE_RATE));
          

          initialSampleRate = sampleRateControl.getValue();
        }
      }
      catch (IllegalArgumentException iae)
      {
        sampleRateControl = null;
        initialSampleRate = 0.0F;
      }
    


    case 1: 
      try
      {
        if (!sourceDataLine.isControlSupported(FloatControl.Type.PAN))
        {
          panControl = null;
        }
        else {
          panControl = ((FloatControl)sourceDataLine.getControl(FloatControl.Type.PAN));
        }
      }
      catch (IllegalArgumentException iae)
      {
        panControl = null;
      }
      
      try
      {
        if (!sourceDataLine.isControlSupported(FloatControl.Type.MASTER_GAIN))
        {

          gainControl = null;
          initialGain = 0.0F;

        }
        else
        {
          gainControl = ((FloatControl)sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN));
          

          initialGain = gainControl.getValue();
        }
      }
      catch (IllegalArgumentException iae)
      {
        gainControl = null;
        initialGain = 0.0F;
      }
      
      try
      {
        if (!sourceDataLine.isControlSupported(FloatControl.Type.SAMPLE_RATE))
        {

          sampleRateControl = null;
          initialSampleRate = 0.0F;

        }
        else
        {
          sampleRateControl = ((FloatControl)sourceDataLine.getControl(FloatControl.Type.SAMPLE_RATE));
          

          initialSampleRate = sampleRateControl.getValue();
        }
      }
      catch (IllegalArgumentException iae)
      {
        sampleRateControl = null;
        initialSampleRate = 0.0F;
      }
    
    default: 
      errorMessage("Unrecognized channel type in method 'resetControls'");
      
      panControl = null;
      gainControl = null;
      sampleRateControl = null;
    }
    
  }
  




  public void setLooping(boolean value)
  {
    toLoop = value;
  }
  






  public void setPan(float p)
  {
    if (panControl == null)
      return;
    float pan = p;
    
    if (pan < -1.0F)
      pan = -1.0F;
    if (pan > 1.0F) {
      pan = 1.0F;
    }
    panControl.setValue(pan);
  }
  





  public void setGain(float g)
  {
    if (gainControl == null) {
      return;
    }
    
    float gain = g;
    if (gain < 0.0F)
      gain = 0.0F;
    if (gain > 1.0F) {
      gain = 1.0F;
    }
    double minimumDB = gainControl.getMinimum();
    double maximumDB = initialGain;
    




    double ampGainDB = 0.5D * maximumDB - minimumDB;
    double cste = Math.log(10.0D) / 20.0D;
    float valueDB = (float)(minimumDB + 1.0D / cste * Math.log(1.0D + (Math.exp(cste * ampGainDB) - 1.0D) * gain));
    


    gainControl.setValue(valueDB);
  }
  





  public void setPitch(float p)
  {
    if (sampleRateControl == null)
    {
      return;
    }
    float sampleRate = p;
    

    if (sampleRate < 0.5F)
      sampleRate = 0.5F;
    if (sampleRate > 2.0F) {
      sampleRate = 2.0F;
    }
    sampleRate *= initialSampleRate;
    

    sampleRateControl.setValue(sampleRate);
  }
  







  public boolean preLoadBuffers(LinkedList<byte[]> bufferList)
  {
    if (errorCheck(channelType != 1, "Buffers may only be queued for streaming sources."))
    {
      return false;
    }
    
    if (errorCheck(sourceDataLine == null, "SourceDataLine null in method 'preLoadBuffers'."))
    {
      return false;
    }
    sourceDataLine.start();
    
    if (bufferList.isEmpty()) {
      return true;
    }
    
    byte[] preLoad = (byte[])bufferList.remove(0);
    

    if (errorCheck(preLoad == null, "Missing sound-bytes in method 'preLoadBuffers'."))
    {
      return false;
    }
    

    while (!bufferList.isEmpty())
    {
      streamBuffers.add(new SoundBuffer((byte[])bufferList.remove(0), myFormat));
    }
    


    sourceDataLine.write(preLoad, 0, preLoad.length);
    

    processed = 0;
    
    return true;
  }
  







  public boolean queueBuffer(byte[] buffer)
  {
    if (errorCheck(channelType != 1, "Buffers may only be queued for streaming sources."))
    {
      return false;
    }
    
    if (errorCheck(sourceDataLine == null, "SourceDataLine null in method 'queueBuffer'."))
    {
      return false;
    }
    
    if (errorCheck(myFormat == null, "AudioFormat null in method 'queueBuffer'"))
    {
      return false;
    }
    
    streamBuffers.add(new SoundBuffer(buffer, myFormat));
    

    processBuffer();
    
    processed = 0;
    return true;
  }
  







  public boolean processBuffer()
  {
    if (errorCheck(channelType != 1, "Buffers are only processed for streaming sources."))
    {
      return false;
    }
    
    if (errorCheck(sourceDataLine == null, "SourceDataLine null in method 'processBuffer'."))
    {
      return false;
    }
    if ((streamBuffers == null) || (streamBuffers.isEmpty())) {
      return false;
    }
    
    SoundBuffer nextBuffer = (SoundBuffer)streamBuffers.remove(0);
    
    sourceDataLine.write(audioData, 0, audioData.length);
    
    if (!sourceDataLine.isActive())
      sourceDataLine.start();
    nextBuffer.cleanup();
    nextBuffer = null;
    
    return true;
  }
  







  public int feedRawAudioData(byte[] buffer)
  {
    if (errorCheck(channelType != 1, "Raw audio data can only be processed by streaming sources."))
    {
      return -1;
    }
    if (errorCheck(streamBuffers == null, "StreamBuffers queue null in method 'feedRawAudioData'."))
    {
      return -1;
    }
    streamBuffers.add(new SoundBuffer(buffer, myFormat));
    return buffersProcessed();
  }
  






  public int buffersProcessed()
  {
    processed = 0;
    

    if (errorCheck(channelType != 1, "Buffers may only be queued for streaming sources."))
    {

      if (streamBuffers != null)
        streamBuffers.clear();
      return 0;
    }
    

    if (sourceDataLine == null)
    {
      if (streamBuffers != null)
        streamBuffers.clear();
      return 0;
    }
    
    if (sourceDataLine.available() > 0)
    {
      processed = 1;
    }
    
    return processed;
  }
  






  public void flush()
  {
    if (channelType != 1) {
      return;
    }
    
    if (errorCheck(sourceDataLine == null, "SourceDataLine null in method 'flush'."))
    {
      return;
    }
    sourceDataLine.stop();
    sourceDataLine.flush();
    sourceDataLine.drain();
    
    streamBuffers.clear();
    processed = 0;
  }
  




  public void close()
  {
    switch (channelType)
    {
    case 0: 
      if (clip != null)
      {
        clip.stop();
        clip.flush();
        clip.close();
      }
      break;
    case 1: 
      if (sourceDataLine != null)
      {
        flush();
        sourceDataLine.close();
      }
      


      break;
    }
    
  }
  



  public void play()
  {
    switch (channelType)
    {
    case 0: 
      if (clip != null)
      {
        if (toLoop)
        {
          clip.stop();
          clip.loop(-1);
        }
        else
        {
          clip.stop();
          clip.start();
        }
      }
      
      break;
    case 1: 
      if (sourceDataLine != null)
      {
        sourceDataLine.start();
      }
      


      break;
    }
    
  }
  


  public void pause()
  {
    switch (channelType)
    {
    case 0: 
      if (clip != null)
        clip.stop();
      break;
    case 1: 
      if (sourceDataLine != null) {
        sourceDataLine.stop();
      }
      


      break;
    }
    
  }
  


  public void stop()
  {
    switch (channelType)
    {
    case 0: 
      if (clip != null)
      {
        clip.stop();
        clip.setFramePosition(0);
      }
      break;
    case 1: 
      if (sourceDataLine != null) {
        sourceDataLine.stop();
      }
      


      break;
    }
    
  }
  


  public void rewind()
  {
    switch (channelType)
    {
    case 0: 
      if (clip != null)
      {
        boolean rePlay = clip.isRunning();
        clip.stop();
        clip.setFramePosition(0);
        if (rePlay)
        {
          if (toLoop) {
            clip.loop(-1);
          } else
            clip.start(); }
      }
      break;
    case 1: 
      break;
    }
    
  }
  








  public float millisecondsPlayed()
  {
    switch (channelType)
    {
    case 0: 
      if (clip == null)
        return -1.0F;
      return (float)clip.getMicrosecondPosition() / 1000.0F;
    case 1: 
      if (sourceDataLine == null)
        return -1.0F;
      return (float)sourceDataLine.getMicrosecondPosition() / 1000.0F;
    }
    return -1.0F;
  }
  







  public boolean playing()
  {
    switch (channelType)
    {
    case 0: 
      if (clip == null)
        return false;
      return clip.isActive();
    case 1: 
      if (sourceDataLine == null)
        return false;
      return sourceDataLine.isActive();
    }
    return false;
  }
}
