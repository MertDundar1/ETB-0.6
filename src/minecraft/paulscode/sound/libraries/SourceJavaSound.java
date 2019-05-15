package paulscode.sound.libraries;

import java.util.LinkedList;
import javax.sound.sampled.AudioFormat;
import paulscode.sound.Channel;
import paulscode.sound.FilenameURL;
import paulscode.sound.ICodec;
import paulscode.sound.ListenerData;
import paulscode.sound.SoundBuffer;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.Source;
import paulscode.sound.Vector3D;








































public class SourceJavaSound
  extends Source
{
  protected ChannelJavaSound channelJavaSound = (ChannelJavaSound)channel;
  



  public ListenerData listener;
  



  private float pan = 0.0F;
  




















  public SourceJavaSound(ListenerData listener, boolean priority, boolean toStream, boolean toLoop, String sourcename, FilenameURL filenameURL, SoundBuffer soundBuffer, float x, float y, float z, int attModel, float distOrRoll, boolean temporary)
  {
    super(priority, toStream, toLoop, sourcename, filenameURL, soundBuffer, x, y, z, attModel, distOrRoll, temporary);
    
    libraryType = LibraryJavaSound.class;
    

    this.listener = listener;
    positionChanged();
  }
  







  public SourceJavaSound(ListenerData listener, Source old, SoundBuffer soundBuffer)
  {
    super(old, soundBuffer);
    libraryType = LibraryJavaSound.class;
    

    this.listener = listener;
    positionChanged();
  }
  















  public SourceJavaSound(ListenerData listener, AudioFormat audioFormat, boolean priority, String sourcename, float x, float y, float z, int attModel, float distOrRoll)
  {
    super(audioFormat, priority, sourcename, x, y, z, attModel, distOrRoll);
    
    libraryType = LibraryJavaSound.class;
    

    this.listener = listener;
    positionChanged();
  }
  





  public void cleanup()
  {
    super.cleanup();
  }
  





















  public void changeSource(boolean priority, boolean toStream, boolean toLoop, String sourcename, FilenameURL filenameURL, SoundBuffer soundBuffer, float x, float y, float z, int attModel, float distOrRoll, boolean temporary)
  {
    super.changeSource(priority, toStream, toLoop, sourcename, filenameURL, soundBuffer, x, y, z, attModel, distOrRoll, temporary);
    

    if (channelJavaSound != null)
      channelJavaSound.setLooping(toLoop);
    positionChanged();
  }
  




  public void listenerMoved()
  {
    positionChanged();
  }
  







  public void setVelocity(float x, float y, float z)
  {
    super.setVelocity(x, y, z);
    positionChanged();
  }
  







  public void setPosition(float x, float y, float z)
  {
    super.setPosition(x, y, z);
    positionChanged();
  }
  




  public void positionChanged()
  {
    calculateGain();
    calculatePan();
    calculatePitch();
  }
  





  public void setPitch(float value)
  {
    super.setPitch(value);
    calculatePitch();
  }
  





  public void setAttenuation(int model)
  {
    super.setAttenuation(model);
    calculateGain();
  }
  






  public void setDistOrRoll(float dr)
  {
    super.setDistOrRoll(dr);
    calculateGain();
  }
  





  public void play(Channel c)
  {
    if (!active())
    {
      if (toLoop)
        toPlay = true;
      return;
    }
    
    if (c == null)
    {
      errorMessage("Unable to play source, because channel was null");
      return;
    }
    
    boolean newChannel = channel != c;
    if ((channel != null) && (channel.attachedSource != this)) {
      newChannel = true;
    }
    boolean wasPaused = paused();
    boolean wasStopped = stopped();
    
    super.play(c);
    
    channelJavaSound = ((ChannelJavaSound)channel);
    


    if (newChannel)
    {
      if (channelJavaSound != null) {
        channelJavaSound.setLooping(toLoop);
      }
      if (!toStream)
      {


        if (soundBuffer == null)
        {
          errorMessage("No sound buffer to play");
          return;
        }
        
        channelJavaSound.attachBuffer(soundBuffer);
      }
    }
    positionChanged();
    

    if ((wasStopped) || (!playing()))
    {
      if ((toStream) && (!wasPaused))
      {
        preLoad = true;
      }
      channel.play();
    }
  }
  





  public boolean preLoad()
  {
    if (codec == null)
    {
      return false;
    }
    
    boolean noNextBuffers = false;
    synchronized (soundSequenceLock)
    {
      if ((nextBuffers == null) || (nextBuffers.isEmpty())) {
        noNextBuffers = true;
      }
    }
    LinkedList<byte[]> preLoadBuffers = new LinkedList();
    if ((nextCodec != null) && (!noNextBuffers))
    {
      codec = nextCodec;
      nextCodec = null;
      synchronized (soundSequenceLock)
      {
        while (!nextBuffers.isEmpty())
        {
          soundBuffer = ((SoundBuffer)nextBuffers.remove(0));
          if ((soundBuffer != null) && (soundBuffer.audioData != null)) {
            preLoadBuffers.add(soundBuffer.audioData);
          }
        }
      }
    }
    else {
      codec.initialize(filenameURL.getURL());
      
      for (int i = 0; i < SoundSystemConfig.getNumberStreamingBuffers(); i++)
      {
        soundBuffer = codec.read();
        
        if ((soundBuffer == null) || (soundBuffer.audioData == null)) {
          break;
        }
        preLoadBuffers.add(soundBuffer.audioData);
      }
      channelJavaSound.resetStream(codec.getAudioFormat());
    }
    positionChanged();
    
    channel.preLoadBuffers(preLoadBuffers);
    
    preLoad = false;
    return true;
  }
  




  public void calculateGain()
  {
    float distX = position.x - listener.position.x;
    float distY = position.y - listener.position.y;
    float distZ = position.z - listener.position.z;
    
    distanceFromListener = ((float)Math.sqrt(distX * distX + distY * distY + distZ * distZ));
    


    switch (attModel)
    {
    case 2: 
      if (distanceFromListener <= 0.0F)
      {
        gain = 1.0F;
      }
      else if (distanceFromListener >= distOrRoll)
      {
        gain = 0.0F;
      }
      else
      {
        gain = (1.0F - distanceFromListener / distOrRoll);
      }
      break;
    case 1: 
      if (distanceFromListener <= 0.0F)
      {
        gain = 1.0F;
      }
      else
      {
        float tweakFactor = 5.0E-4F;
        float attenuationFactor = distOrRoll * distanceFromListener * distanceFromListener * tweakFactor;
        



        if (attenuationFactor < 0.0F) {
          attenuationFactor = 0.0F;
        }
        gain = (1.0F / (1.0F + attenuationFactor));
      }
      break;
    default: 
      gain = 1.0F;
    }
    
    
    if (gain > 1.0F)
      gain = 1.0F;
    if (gain < 0.0F) {
      gain = 0.0F;
    }
    gain *= sourceVolume * SoundSystemConfig.getMasterGain() * Math.abs(fadeOutGain) * fadeInGain;
    


    if ((channel != null) && (channel.attachedSource == this) && (channelJavaSound != null))
    {
      channelJavaSound.setGain(gain);
    }
  }
  



  public void calculatePan()
  {
    Vector3D side = listener.up.cross(listener.lookAt);
    side.normalize();
    float x = position.dot(position.subtract(listener.position), side);
    float z = position.dot(position.subtract(listener.position), listener.lookAt);
    
    side = null;
    float angle = (float)Math.atan2(x, z);
    pan = ((float)-Math.sin(angle));
    
    if ((channel != null) && (channel.attachedSource == this) && (channelJavaSound != null))
    {

      if (attModel == 0) {
        channelJavaSound.setPan(0.0F);
      } else {
        channelJavaSound.setPan(pan);
      }
    }
  }
  



  public void calculatePitch()
  {
    if ((channel != null) && (channel.attachedSource == this) && (channelJavaSound != null))
    {


      if (SoundSystemConfig.getDopplerFactor() == 0.0F)
      {
        channelJavaSound.setPitch(pitch);
      }
      else
      {
        float SS = 343.3F;
        
        Vector3D SV = velocity;
        Vector3D LV = listener.velocity;
        float DV = SoundSystemConfig.getDopplerVelocity();
        float DF = SoundSystemConfig.getDopplerFactor();
        Vector3D SL = listener.position.subtract(position);
        
        float vls = SL.dot(LV) / SL.length();
        float vss = SL.dot(SV) / SL.length();
        
        vss = min(vss, SS / DF);
        vls = min(vls, SS / DF);
        float newPitch = pitch * (SS * DV - DF * vls) / (SS * DV - DF * vss);
        

        if (newPitch < 0.5F) {
          newPitch = 0.5F;
        } else if (newPitch > 2.0F) {
          newPitch = 2.0F;
        }
        channelJavaSound.setPitch(newPitch);
      }
    }
  }
  
  public float min(float a, float b)
  {
    if (a < b)
      return a;
    return b;
  }
}
