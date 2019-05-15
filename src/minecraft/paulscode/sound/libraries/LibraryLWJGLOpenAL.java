package paulscode.sound.libraries;

import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.sound.sampled.AudioFormat;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import paulscode.sound.Channel;
import paulscode.sound.FilenameURL;
import paulscode.sound.ICodec;
import paulscode.sound.Library;
import paulscode.sound.ListenerData;
import paulscode.sound.SoundBuffer;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.Source;
import paulscode.sound.Vector3D;


























































































public class LibraryLWJGLOpenAL
  extends Library
{
  private static final boolean GET = false;
  private static final boolean SET = true;
  private static final boolean XXX = false;
  private FloatBuffer listenerPositionAL = null;
  


  private FloatBuffer listenerOrientation = null;
  


  private FloatBuffer listenerVelocity = null;
  


  private HashMap<String, IntBuffer> ALBufferMap = null;
  



  private static boolean alPitchSupported = true;
  





  public LibraryLWJGLOpenAL()
    throws SoundSystemException
  {
    ALBufferMap = new HashMap();
    reverseByteOrder = true;
  }
  



  public void init()
    throws SoundSystemException
  {
    boolean errors = false;
    

    try
    {
      AL.create();
      errors = checkALError();

    }
    catch (LWJGLException e)
    {
      errorMessage("Unable to initialize OpenAL.  Probable cause: OpenAL not supported.");
      
      printStackTrace(e);
      throw new Exception(e.getMessage(), 101);
    }
    


    if (errors) {
      importantMessage("OpenAL did not initialize properly!");
    } else {
      message("OpenAL initialized.");
    }
    
    listenerPositionAL = BufferUtils.createFloatBuffer(3).put(new float[] { listener.position.x, listener.position.y, listener.position.z });
    


    listenerOrientation = BufferUtils.createFloatBuffer(6).put(new float[] { listener.lookAt.x, listener.lookAt.y, listener.lookAt.z, listener.up.x, listener.up.y, listener.up.z });
    


    listenerVelocity = BufferUtils.createFloatBuffer(3).put(new float[] { 0.0F, 0.0F, 0.0F });
    


    listenerPositionAL.flip();
    listenerOrientation.flip();
    listenerVelocity.flip();
    

    AL10.alListener(4100, listenerPositionAL);
    errors = (checkALError()) || (errors);
    AL10.alListener(4111, listenerOrientation);
    errors = (checkALError()) || (errors);
    AL10.alListener(4102, listenerVelocity);
    errors = (checkALError()) || (errors);
    
    AL10.alDopplerFactor(SoundSystemConfig.getDopplerFactor());
    errors = (checkALError()) || (errors);
    
    AL10.alDopplerVelocity(SoundSystemConfig.getDopplerVelocity());
    errors = (checkALError()) || (errors);
    

    if (errors)
    {
      importantMessage("OpenAL did not initialize properly!");
      throw new Exception("Problem encountered while loading OpenAL or creating the listener.  Probable cause:  OpenAL not supported", 101);
    }
    





    super.init();
    

    ChannelLWJGLOpenAL channel = (ChannelLWJGLOpenAL)normalChannels.get(1);
    
    try
    {
      AL10.alSourcef(ALSource.get(0), 4099, 1.0F);
      
      if (checkALError())
      {
        alPitchSupported(true, false);
        throw new Exception("OpenAL: AL_PITCH not supported.", 108);
      }
      


      alPitchSupported(true, true);

    }
    catch (Exception e)
    {
      alPitchSupported(true, false);
      throw new Exception("OpenAL: AL_PITCH not supported.", 108);
    }
  }
  





  public static boolean libraryCompatible()
  {
    if (AL.isCreated()) {
      return true;
    }
    try
    {
      AL.create();
    }
    catch (Exception e)
    {
      return false;
    }
    
    try
    {
      AL.destroy();
    }
    catch (Exception e) {}
    

    return true;
  }
  










  protected Channel createChannel(int type)
  {
    IntBuffer ALSource = BufferUtils.createIntBuffer(1);
    try
    {
      AL10.alGenSources(ALSource);
    }
    catch (Exception e)
    {
      AL10.alGetError();
      return null;
    }
    
    if (AL10.alGetError() != 0) {
      return null;
    }
    ChannelLWJGLOpenAL channel = new ChannelLWJGLOpenAL(type, ALSource);
    return channel;
  }
  





  public void cleanup()
  {
    super.cleanup();
    
    Set<String> keys = bufferMap.keySet();
    Iterator<String> iter = keys.iterator();
    



    while (iter.hasNext())
    {
      String filename = (String)iter.next();
      IntBuffer buffer = (IntBuffer)ALBufferMap.get(filename);
      if (buffer != null)
      {
        AL10.alDeleteBuffers(buffer);
        checkALError();
        buffer.clear();
      }
    }
    
    bufferMap.clear();
    AL.destroy();
    
    bufferMap = null;
    listenerPositionAL = null;
    listenerOrientation = null;
    listenerVelocity = null;
  }
  







  public boolean loadSound(FilenameURL filenameURL)
  {
    if (bufferMap == null)
    {
      bufferMap = new HashMap();
      importantMessage("Buffer Map was null in method 'loadSound'");
    }
    
    if (ALBufferMap == null)
    {
      ALBufferMap = new HashMap();
      importantMessage("Open AL Buffer Map was null in method'loadSound'");
    }
    


    if (errorCheck(filenameURL == null, "Filename/URL not specified in method 'loadSound'"))
    {
      return false;
    }
    
    if (bufferMap.get(filenameURL.getFilename()) != null) {
      return true;
    }
    ICodec codec = SoundSystemConfig.getCodec(filenameURL.getFilename());
    if (errorCheck(codec == null, "No codec found for file '" + filenameURL.getFilename() + "' in method 'loadSound'"))
    {

      return false; }
    codec.reverseByteOrder(true);
    
    URL url = filenameURL.getURL();
    if (errorCheck(url == null, "Unable to open file '" + filenameURL.getFilename() + "' in method 'loadSound'"))
    {

      return false;
    }
    codec.initialize(url);
    SoundBuffer buffer = codec.readAll();
    codec.cleanup();
    codec = null;
    if (errorCheck(buffer == null, "Sound buffer null in method 'loadSound'"))
    {
      return false;
    }
    bufferMap.put(filenameURL.getFilename(), buffer);
    
    AudioFormat audioFormat = audioFormat;
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
        errorMessage("Illegal sample size in method 'loadSound'");
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
        errorMessage("Illegal sample size in method 'loadSound'");
        return false;
      }
    }
    else
    {
      errorMessage("File neither mono nor stereo in method 'loadSound'");
      
      return false;
    }
    
    IntBuffer intBuffer = BufferUtils.createIntBuffer(1);
    AL10.alGenBuffers(intBuffer);
    if (errorCheck(AL10.alGetError() != 0, "alGenBuffers error when loading " + filenameURL.getFilename()))
    {

      return false;
    }
    


    AL10.alBufferData(intBuffer.get(0), soundFormat, (ByteBuffer)BufferUtils.createByteBuffer(audioData.length).put(audioData).flip(), (int)audioFormat.getSampleRate());
    




    if (errorCheck(AL10.alGetError() != 0, "alBufferData error when loading " + filenameURL.getFilename()))
    {



      if (errorCheck(intBuffer == null, "Sound buffer was not created for " + filenameURL.getFilename()))
      {

        return false; }
    }
    ALBufferMap.put(filenameURL.getFilename(), intBuffer);
    
    return true;
  }
  










  public boolean loadSound(SoundBuffer buffer, String identifier)
  {
    if (bufferMap == null)
    {
      bufferMap = new HashMap();
      importantMessage("Buffer Map was null in method 'loadSound'");
    }
    
    if (ALBufferMap == null)
    {
      ALBufferMap = new HashMap();
      importantMessage("Open AL Buffer Map was null in method'loadSound'");
    }
    


    if (errorCheck(identifier == null, "Identifier not specified in method 'loadSound'"))
    {
      return false;
    }
    
    if (bufferMap.get(identifier) != null) {
      return true;
    }
    if (errorCheck(buffer == null, "Sound buffer null in method 'loadSound'"))
    {
      return false;
    }
    bufferMap.put(identifier, buffer);
    
    AudioFormat audioFormat = audioFormat;
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
        errorMessage("Illegal sample size in method 'loadSound'");
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
        errorMessage("Illegal sample size in method 'loadSound'");
        return false;
      }
    }
    else
    {
      errorMessage("File neither mono nor stereo in method 'loadSound'");
      
      return false;
    }
    
    IntBuffer intBuffer = BufferUtils.createIntBuffer(1);
    AL10.alGenBuffers(intBuffer);
    if (errorCheck(AL10.alGetError() != 0, "alGenBuffers error when saving " + identifier))
    {

      return false;
    }
    


    AL10.alBufferData(intBuffer.get(0), soundFormat, (ByteBuffer)BufferUtils.createByteBuffer(audioData.length).put(audioData).flip(), (int)audioFormat.getSampleRate());
    




    if (errorCheck(AL10.alGetError() != 0, "alBufferData error when saving " + identifier))
    {



      if (errorCheck(intBuffer == null, "Sound buffer was not created for " + identifier))
      {

        return false; }
    }
    ALBufferMap.put(identifier, intBuffer);
    
    return true;
  }
  








  public void unloadSound(String filename)
  {
    ALBufferMap.remove(filename);
    super.unloadSound(filename);
  }
  





  public void setMasterVolume(float value)
  {
    super.setMasterVolume(value);
    
    AL10.alListenerf(4106, value);
    checkALError();
  }
  
















  public void newSource(boolean priority, boolean toStream, boolean toLoop, String sourcename, FilenameURL filenameURL, float x, float y, float z, int attModel, float distOrRoll)
  {
    IntBuffer myBuffer = null;
    if (!toStream)
    {

      myBuffer = (IntBuffer)ALBufferMap.get(filenameURL.getFilename());
      

      if (myBuffer == null)
      {
        if (!loadSound(filenameURL))
        {
          errorMessage("Source '" + sourcename + "' was not created " + "because an error occurred while loading " + filenameURL.getFilename());
          

          return;
        }
      }
      

      myBuffer = (IntBuffer)ALBufferMap.get(filenameURL.getFilename());
      
      if (myBuffer == null)
      {
        errorMessage("Source '" + sourcename + "' was not created " + "because a sound buffer was not found for " + filenameURL.getFilename());
        

        return;
      }
    }
    SoundBuffer buffer = null;
    
    if (!toStream)
    {

      buffer = (SoundBuffer)bufferMap.get(filenameURL.getFilename());
      
      if (buffer == null)
      {
        if (!loadSound(filenameURL))
        {
          errorMessage("Source '" + sourcename + "' was not created " + "because an error occurred while loading " + filenameURL.getFilename());
          

          return;
        }
      }
      
      buffer = (SoundBuffer)bufferMap.get(filenameURL.getFilename());
      
      if (buffer == null)
      {
        errorMessage("Source '" + sourcename + "' was not created " + "because audio data was not found for " + filenameURL.getFilename());
        

        return;
      }
    }
    
    sourceMap.put(sourcename, new SourceLWJGLOpenAL(listenerPositionAL, myBuffer, priority, toStream, toLoop, sourcename, filenameURL, buffer, x, y, z, attModel, distOrRoll, false));
  }
  



















  public void rawDataStream(AudioFormat audioFormat, boolean priority, String sourcename, float x, float y, float z, int attModel, float distOrRoll)
  {
    sourceMap.put(sourcename, new SourceLWJGLOpenAL(listenerPositionAL, audioFormat, priority, sourcename, x, y, z, attModel, distOrRoll));
  }
  





















  public void quickPlay(boolean priority, boolean toStream, boolean toLoop, String sourcename, FilenameURL filenameURL, float x, float y, float z, int attModel, float distOrRoll, boolean temporary)
  {
    IntBuffer myBuffer = null;
    if (!toStream)
    {

      myBuffer = (IntBuffer)ALBufferMap.get(filenameURL.getFilename());
      
      if (myBuffer == null) {
        loadSound(filenameURL);
      }
      myBuffer = (IntBuffer)ALBufferMap.get(filenameURL.getFilename());
      
      if (myBuffer == null)
      {
        errorMessage("Sound buffer was not created for " + filenameURL.getFilename());
        
        return;
      }
    }
    
    SoundBuffer buffer = null;
    
    if (!toStream)
    {

      buffer = (SoundBuffer)bufferMap.get(filenameURL.getFilename());
      
      if (buffer == null)
      {
        if (!loadSound(filenameURL))
        {
          errorMessage("Source '" + sourcename + "' was not created " + "because an error occurred while loading " + filenameURL.getFilename());
          

          return;
        }
      }
      
      buffer = (SoundBuffer)bufferMap.get(filenameURL.getFilename());
      
      if (buffer == null)
      {
        errorMessage("Source '" + sourcename + "' was not created " + "because audio data was not found for " + filenameURL.getFilename());
        

        return;
      }
    }
    SourceLWJGLOpenAL s = new SourceLWJGLOpenAL(listenerPositionAL, myBuffer, priority, toStream, toLoop, sourcename, filenameURL, buffer, x, y, z, attModel, distOrRoll, false);
    






    sourceMap.put(sourcename, s);
    play(s);
    if (temporary) {
      s.setTemporary(true);
    }
  }
  




  public void copySources(HashMap<String, Source> srcMap)
  {
    if (srcMap == null)
      return;
    Set<String> keys = srcMap.keySet();
    Iterator<String> iter = keys.iterator();
    



    if (bufferMap == null)
    {
      bufferMap = new HashMap();
      importantMessage("Buffer Map was null in method 'copySources'");
    }
    
    if (ALBufferMap == null)
    {
      ALBufferMap = new HashMap();
      importantMessage("Open AL Buffer Map was null in method'copySources'");
    }
    


    sourceMap.clear();
    


    while (iter.hasNext())
    {
      String sourcename = (String)iter.next();
      Source source = (Source)srcMap.get(sourcename);
      if (source != null)
      {
        SoundBuffer buffer = null;
        if (!toStream)
        {
          loadSound(filenameURL);
          buffer = (SoundBuffer)bufferMap.get(filenameURL.getFilename());
        }
        if ((toStream) || (buffer != null)) {
          sourceMap.put(sourcename, new SourceLWJGLOpenAL(listenerPositionAL, (IntBuffer)ALBufferMap.get(filenameURL.getFilename()), source, buffer));
        }
      }
    }
  }
  










  public void setListenerPosition(float x, float y, float z)
  {
    super.setListenerPosition(x, y, z);
    
    listenerPositionAL.put(0, x);
    listenerPositionAL.put(1, y);
    listenerPositionAL.put(2, z);
    

    AL10.alListener(4100, listenerPositionAL);
    
    checkALError();
  }
  






  public void setListenerAngle(float angle)
  {
    super.setListenerAngle(angle);
    
    listenerOrientation.put(0, listener.lookAt.x);
    listenerOrientation.put(2, listener.lookAt.z);
    

    AL10.alListener(4111, listenerOrientation);
    
    checkALError();
  }
  











  public void setListenerOrientation(float lookX, float lookY, float lookZ, float upX, float upY, float upZ)
  {
    super.setListenerOrientation(lookX, lookY, lookZ, upX, upY, upZ);
    listenerOrientation.put(0, lookX);
    listenerOrientation.put(1, lookY);
    listenerOrientation.put(2, lookZ);
    listenerOrientation.put(3, upX);
    listenerOrientation.put(4, upY);
    listenerOrientation.put(5, upZ);
    AL10.alListener(4111, listenerOrientation);
    checkALError();
  }
  






  public void setListenerData(ListenerData l)
  {
    super.setListenerData(l);
    
    listenerPositionAL.put(0, position.x);
    listenerPositionAL.put(1, position.y);
    listenerPositionAL.put(2, position.z);
    AL10.alListener(4100, listenerPositionAL);
    checkALError();
    
    listenerOrientation.put(0, lookAt.x);
    listenerOrientation.put(1, lookAt.y);
    listenerOrientation.put(2, lookAt.z);
    listenerOrientation.put(3, up.x);
    listenerOrientation.put(4, up.y);
    listenerOrientation.put(5, up.z);
    AL10.alListener(4111, listenerOrientation);
    checkALError();
    
    listenerVelocity.put(0, velocity.x);
    listenerVelocity.put(1, velocity.y);
    listenerVelocity.put(2, velocity.z);
    AL10.alListener(4102, listenerVelocity);
    checkALError();
  }
  







  public void setListenerVelocity(float x, float y, float z)
  {
    super.setListenerVelocity(x, y, z);
    
    listenerVelocity.put(0, listener.velocity.x);
    listenerVelocity.put(1, listener.velocity.y);
    listenerVelocity.put(2, listener.velocity.z);
    AL10.alListener(4102, listenerVelocity);
  }
  




  public void dopplerChanged()
  {
    super.dopplerChanged();
    
    AL10.alDopplerFactor(SoundSystemConfig.getDopplerFactor());
    checkALError();
    AL10.alDopplerVelocity(SoundSystemConfig.getDopplerVelocity());
    checkALError();
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
  





  public static boolean alPitchSupported()
  {
    return alPitchSupported(false, false);
  }
  






  private static synchronized boolean alPitchSupported(boolean action, boolean value)
  {
    if (action == true)
      alPitchSupported = value;
    return alPitchSupported;
  }
  




  public static String getTitle()
  {
    return "LWJGL OpenAL";
  }
  




  public static String getDescription()
  {
    return "The LWJGL binding of OpenAL.  For more information, see http://www.lwjgl.org";
  }
  






  public String getClassName()
  {
    return "LibraryLWJGLOpenAL";
  }
  




  public static class Exception
    extends SoundSystemException
  {
    public static final int CREATE = 101;
    



    public static final int INVALID_NAME = 102;
    



    public static final int INVALID_ENUM = 103;
    



    public static final int INVALID_VALUE = 104;
    



    public static final int INVALID_OPERATION = 105;
    



    public static final int OUT_OF_MEMORY = 106;
    


    public static final int LISTENER = 107;
    


    public static final int NO_AL_PITCH = 108;
    



    public Exception(String message)
    {
      super();
    }
    






    public Exception(String message, int type)
    {
      super(type);
    }
  }
}
