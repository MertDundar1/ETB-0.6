package paulscode.sound.libraries;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;
import javax.sound.sampled.AudioFormat;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import paulscode.sound.Channel;
import paulscode.sound.FilenameURL;
import paulscode.sound.ICodec;
import paulscode.sound.SoundBuffer;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.Source;
import paulscode.sound.Vector3D;














































































public class SourceLWJGLOpenAL
  extends Source
{
  private ChannelLWJGLOpenAL channelOpenAL = (ChannelLWJGLOpenAL)channel;
  







  private IntBuffer myBuffer;
  







  private FloatBuffer listenerPosition;
  







  private FloatBuffer sourcePosition;
  







  private FloatBuffer sourceVelocity;
  







  public SourceLWJGLOpenAL(FloatBuffer listenerPosition, IntBuffer myBuffer, boolean priority, boolean toStream, boolean toLoop, String sourcename, FilenameURL filenameURL, SoundBuffer soundBuffer, float x, float y, float z, int attModel, float distOrRoll, boolean temporary)
  {
    super(priority, toStream, toLoop, sourcename, filenameURL, soundBuffer, x, y, z, attModel, distOrRoll, temporary);
    
    if (codec != null)
      codec.reverseByteOrder(true);
    this.listenerPosition = listenerPosition;
    this.myBuffer = myBuffer;
    libraryType = LibraryLWJGLOpenAL.class;
    pitch = 1.0F;
    resetALInformation();
  }
  








  public SourceLWJGLOpenAL(FloatBuffer listenerPosition, IntBuffer myBuffer, Source old, SoundBuffer soundBuffer)
  {
    super(old, soundBuffer);
    if (codec != null)
      codec.reverseByteOrder(true);
    this.listenerPosition = listenerPosition;
    this.myBuffer = myBuffer;
    libraryType = LibraryLWJGLOpenAL.class;
    pitch = 1.0F;
    resetALInformation();
  }
  
















  public SourceLWJGLOpenAL(FloatBuffer listenerPosition, AudioFormat audioFormat, boolean priority, String sourcename, float x, float y, float z, int attModel, float distOrRoll)
  {
    super(audioFormat, priority, sourcename, x, y, z, attModel, distOrRoll);
    
    this.listenerPosition = listenerPosition;
    libraryType = LibraryLWJGLOpenAL.class;
    pitch = 1.0F;
    resetALInformation();
  }
  





  public void cleanup()
  {
    super.cleanup();
  }
  























  public void changeSource(FloatBuffer listenerPosition, IntBuffer myBuffer, boolean priority, boolean toStream, boolean toLoop, String sourcename, FilenameURL filenameURL, SoundBuffer soundBuffer, float x, float y, float z, int attModel, float distOrRoll, boolean temporary)
  {
    super.changeSource(priority, toStream, toLoop, sourcename, filenameURL, soundBuffer, x, y, z, attModel, distOrRoll, temporary);
    

    this.listenerPosition = listenerPosition;
    this.myBuffer = myBuffer;
    pitch = 1.0F;
    resetALInformation();
  }
  








  public boolean incrementSoundSequence()
  {
    if (!toStream)
    {
      errorMessage("Method 'incrementSoundSequence' may only be used for streaming sources.");
      
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
        if (codec != null)
        {
          codec.reverseByteOrder(true);
          if (codec.getAudioFormat() == null) {
            codec.initialize(filenameURL.getURL());
          }
          AudioFormat audioFormat = codec.getAudioFormat();
          
          if (audioFormat == null)
          {
            errorMessage("Audio Format null in method 'incrementSoundSequence'");
            
            return false;
          }
          
          int soundFormat = 0;
          if (audioFormat.getChannels() == 1)
          {
            if (audioFormat.getSampleSizeInBits() == 8)
            {
              soundFormat = 4352;
            }
            else if (audioFormat.getSampleSizeInBits() == 16)
            {
              soundFormat = 4353;
            }
            else
            {
              errorMessage("Illegal sample size in method 'incrementSoundSequence'");
              
              return false;
            }
          }
          else if (audioFormat.getChannels() == 2)
          {
            if (audioFormat.getSampleSizeInBits() == 8)
            {
              soundFormat = 4354;
            }
            else if (audioFormat.getSampleSizeInBits() == 16)
            {
              soundFormat = 4355;
            }
            else
            {
              errorMessage("Illegal sample size in method 'incrementSoundSequence'");
              
              return false;
            }
          }
          else
          {
            errorMessage("Audio data neither mono nor stereo in method 'incrementSoundSequence'");
            
            return false;
          }
          

          channelOpenAL.setFormat(soundFormat, (int)audioFormat.getSampleRate());
          
          preLoad = true;
        }
        return true;
      }
    }
    return false;
  }
  




  public void listenerMoved()
  {
    positionChanged();
  }
  







  public void setPosition(float x, float y, float z)
  {
    super.setPosition(x, y, z);
    

    if (sourcePosition == null) {
      resetALInformation();
    } else {
      positionChanged();
    }
    
    sourcePosition.put(0, x);
    sourcePosition.put(1, y);
    sourcePosition.put(2, z);
    

    if ((channel != null) && (channel.attachedSource == this) && (channelOpenAL != null) && (channelOpenAL.ALSource != null))
    {


      AL10.alSource(channelOpenAL.ALSource.get(0), 4100, sourcePosition);
      
      checkALError();
    }
  }
  




  public void positionChanged()
  {
    calculateDistance();
    calculateGain();
    
    if ((channel != null) && (channel.attachedSource == this) && (channelOpenAL != null) && (channelOpenAL.ALSource != null))
    {

      AL10.alSourcef(channelOpenAL.ALSource.get(0), 4106, gain * sourceVolume * Math.abs(fadeOutGain) * fadeInGain);
      


      checkALError();
    }
    checkPitch();
  }
  



  private void checkPitch()
  {
    if ((channel != null) && (channel.attachedSource == this) && (LibraryLWJGLOpenAL.alPitchSupported()) && (channelOpenAL != null) && (channelOpenAL.ALSource != null))
    {


      AL10.alSourcef(channelOpenAL.ALSource.get(0), 4099, pitch);
      
      checkALError();
    }
  }
  





  public void setLooping(boolean lp)
  {
    super.setLooping(lp);
    

    if ((channel != null) && (channel.attachedSource == this) && (channelOpenAL != null) && (channelOpenAL.ALSource != null))
    {

      if (lp) {
        AL10.alSourcei(channelOpenAL.ALSource.get(0), 4103, 1);
      }
      else {
        AL10.alSourcei(channelOpenAL.ALSource.get(0), 4103, 0);
      }
      checkALError();
    }
  }
  





  public void setAttenuation(int model)
  {
    super.setAttenuation(model);
    
    if ((channel != null) && (channel.attachedSource == this) && (channelOpenAL != null) && (channelOpenAL.ALSource != null))
    {


      if (model == 1) {
        AL10.alSourcef(channelOpenAL.ALSource.get(0), 4129, distOrRoll);
      }
      else {
        AL10.alSourcef(channelOpenAL.ALSource.get(0), 4129, 0.0F);
      }
      checkALError();
    }
  }
  






  public void setDistOrRoll(float dr)
  {
    super.setDistOrRoll(dr);
    
    if ((channel != null) && (channel.attachedSource == this) && (channelOpenAL != null) && (channelOpenAL.ALSource != null))
    {


      if (attModel == 1) {
        AL10.alSourcef(channelOpenAL.ALSource.get(0), 4129, dr);
      }
      else {
        AL10.alSourcef(channelOpenAL.ALSource.get(0), 4129, 0.0F);
      }
      checkALError();
    }
  }
  







  public void setVelocity(float x, float y, float z)
  {
    super.setVelocity(x, y, z);
    
    sourceVelocity = BufferUtils.createFloatBuffer(3).put(new float[] { x, y, z });
    
    sourceVelocity.flip();
    
    if ((channel != null) && (channel.attachedSource == this) && (channelOpenAL != null) && (channelOpenAL.ALSource != null))
    {

      AL10.alSource(channelOpenAL.ALSource.get(0), 4102, sourceVelocity);
      
      checkALError();
    }
  }
  





  public void setPitch(float value)
  {
    super.setPitch(value);
    checkPitch();
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
    
    super.play(c);
    
    channelOpenAL = ((ChannelLWJGLOpenAL)channel);
    


    if (newChannel)
    {
      setPosition(position.x, position.y, position.z);
      checkPitch();
      

      if ((channelOpenAL != null) && (channelOpenAL.ALSource != null))
      {
        if (LibraryLWJGLOpenAL.alPitchSupported())
        {
          AL10.alSourcef(channelOpenAL.ALSource.get(0), 4099, pitch);
          
          checkALError();
        }
        AL10.alSource(channelOpenAL.ALSource.get(0), 4100, sourcePosition);
        
        checkALError();
        
        AL10.alSource(channelOpenAL.ALSource.get(0), 4102, sourceVelocity);
        

        checkALError();
        
        if (attModel == 1) {
          AL10.alSourcef(channelOpenAL.ALSource.get(0), 4129, distOrRoll);
        }
        else {
          AL10.alSourcef(channelOpenAL.ALSource.get(0), 4129, 0.0F);
        }
        checkALError();
        
        if ((toLoop) && (!toStream)) {
          AL10.alSourcei(channelOpenAL.ALSource.get(0), 4103, 1);
        }
        else {
          AL10.alSourcei(channelOpenAL.ALSource.get(0), 4103, 0);
        }
        checkALError();
      }
      if (!toStream)
      {


        if (myBuffer == null)
        {
          errorMessage("No sound buffer to play");
          return;
        }
        
        channelOpenAL.attachBuffer(myBuffer);
      }
    }
    

    if (!playing())
    {
      if ((toStream) && (!wasPaused))
      {
        if (codec == null)
        {
          errorMessage("Decoder null in method 'play'");
          return;
        }
        if (codec.getAudioFormat() == null) {
          codec.initialize(filenameURL.getURL());
        }
        AudioFormat audioFormat = codec.getAudioFormat();
        
        if (audioFormat == null)
        {
          errorMessage("Audio Format null in method 'play'");
          return;
        }
        
        int soundFormat = 0;
        if (audioFormat.getChannels() == 1)
        {
          if (audioFormat.getSampleSizeInBits() == 8)
          {
            soundFormat = 4352;
          }
          else if (audioFormat.getSampleSizeInBits() == 16)
          {
            soundFormat = 4353;
          }
          else
          {
            errorMessage("Illegal sample size in method 'play'");
          }
          
        }
        else if (audioFormat.getChannels() == 2)
        {
          if (audioFormat.getSampleSizeInBits() == 8)
          {
            soundFormat = 4354;
          }
          else if (audioFormat.getSampleSizeInBits() == 16)
          {
            soundFormat = 4355;
          }
          else
          {
            errorMessage("Illegal sample size in method 'play'");
          }
          
        }
        else
        {
          errorMessage("Audio data neither mono nor stereo in method 'play'");
          
          return;
        }
        

        channelOpenAL.setFormat(soundFormat, (int)audioFormat.getSampleRate());
        
        preLoad = true;
      }
      channel.play();
      if (pitch != 1.0F) {
        checkPitch();
      }
    }
  }
  




  public boolean preLoad()
  {
    if (codec == null) {
      return false;
    }
    codec.initialize(filenameURL.getURL());
    LinkedList<byte[]> preLoadBuffers = new LinkedList();
    for (int i = 0; i < SoundSystemConfig.getNumberStreamingBuffers(); i++)
    {
      soundBuffer = codec.read();
      
      if ((soundBuffer == null) || (soundBuffer.audioData == null)) {
        break;
      }
      preLoadBuffers.add(soundBuffer.audioData);
    }
    positionChanged();
    
    channel.preLoadBuffers(preLoadBuffers);
    
    preLoad = false;
    return true;
  }
  




  private void resetALInformation()
  {
    sourcePosition = BufferUtils.createFloatBuffer(3).put(new float[] { position.x, position.y, position.z });
    
    sourceVelocity = BufferUtils.createFloatBuffer(3).put(new float[] { velocity.x, velocity.y, velocity.z });
    


    sourcePosition.flip();
    sourceVelocity.flip();
    
    positionChanged();
  }
  



  private void calculateDistance()
  {
    if (listenerPosition != null)
    {

      double dX = position.x - listenerPosition.get(0);
      double dY = position.y - listenerPosition.get(1);
      double dZ = position.z - listenerPosition.get(2);
      distanceFromListener = ((float)Math.sqrt(dX * dX + dY * dY + dZ * dZ));
    }
  }
  





  private void calculateGain()
  {
    if (attModel == 2)
    {
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
      if (gain > 1.0F)
        gain = 1.0F;
      if (gain < 0.0F) {
        gain = 0.0F;
      }
    }
    else {
      gain = 1.0F;
    }
  }
  




  private boolean checkALError()
  {
    switch ()
    {
    case 0: 
      return false;
    case 40961: 
      errorMessage("Invalid name parameter.");
      return true;
    case 40962: 
      errorMessage("Invalid parameter.");
      return true;
    case 40963: 
      errorMessage("Invalid enumerated parameter value.");
      return true;
    case 40964: 
      errorMessage("Illegal call.");
      return true;
    case 40965: 
      errorMessage("Unable to allocate memory.");
      return true;
    }
    errorMessage("An unrecognized error occurred.");
    return true;
  }
}
