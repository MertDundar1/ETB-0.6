package paulscode.sound.libraries;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.FloatControl.Type;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Mixer.Info;
import javax.sound.sampled.SourceDataLine;
import paulscode.sound.Channel;
import paulscode.sound.FilenameURL;
import paulscode.sound.ICodec;
import paulscode.sound.Library;
import paulscode.sound.SoundBuffer;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.SoundSystemLogger;
import paulscode.sound.Source;




















































public class LibraryJavaSound
  extends Library
{
  private static final boolean GET = false;
  private static final boolean SET = true;
  private static final int XXX = 0;
  private final int maxClipSize = 1048576;
  



  private static Mixer myMixer = null;
  



  private static MixerRanking myMixerRanking = null;
  



  private static LibraryJavaSound instance = null;
  



  private static int minSampleRate = 4000;
  



  private static int maxSampleRate = 48000;
  



  private static int lineCount = 32;
  



  private static boolean useGainControl = true;
  



  private static boolean usePanControl = true;
  



  private static boolean useSampleRateControl = true;
  






  public LibraryJavaSound()
    throws SoundSystemException
  {
    instance = this;
  }
  



  public void init()
    throws SoundSystemException
  {
    MixerRanking mixerRanker = null;
    
    if (myMixer == null)
    {

      for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo())
      {
        if (mixerInfo.getName().equals("Java Sound Audio Engine"))
        {

          mixerRanker = new MixerRanking();
          try
          {
            mixerRanker.rank(mixerInfo);
          }
          catch (Exception ljse)
          {
            break;
          }
          
          if (rank < 14) {
            break;
          }
          myMixer = AudioSystem.getMixer(mixerInfo);
          mixerRanking(true, mixerRanker);
          break;
        }
      }
      
      if (myMixer == null)
      {

        MixerRanking bestRankedMixer = mixerRanker;
        for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo())
        {
          mixerRanker = new MixerRanking();
          
          try
          {
            mixerRanker.rank(mixerInfo);
          }
          catch (Exception ljse) {}
          

          if ((bestRankedMixer == null) || (rank > rank))
          {
            bestRankedMixer = mixerRanker;
          }
        }
        if (bestRankedMixer == null) {
          throw new Exception("No useable mixers found!", new MixerRanking());
        }
        
        try
        {
          myMixer = AudioSystem.getMixer(mixerInfo);
          mixerRanking(true, bestRankedMixer);

        }
        catch (Exception e)
        {
          throw new Exception("No useable mixers available!", new MixerRanking());
        }
      }
    }
    


    setMasterVolume(1.0F);
    

    message("JavaSound initialized.");
    
    super.init();
  }
  





  public static boolean libraryCompatible()
  {
    for (Mixer.Info mixerInfo : )
    {
      if (mixerInfo.getName().equals("Java Sound Audio Engine"))
        return true;
    }
    return false;
  }
  







  protected Channel createChannel(int type)
  {
    return new ChannelJavaSound(type, myMixer);
  }
  




  public void cleanup()
  {
    super.cleanup();
    instance = null;
    myMixer = null;
    myMixerRanking = null;
  }
  







  public boolean loadSound(FilenameURL filenameURL)
  {
    if (bufferMap == null)
    {
      bufferMap = new HashMap();
      importantMessage("Buffer Map was null in method 'loadSound'");
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
    URL url = filenameURL.getURL();
    
    if (errorCheck(url == null, "Unable to open file '" + filenameURL.getFilename() + "' in method 'loadSound'"))
    {

      return false;
    }
    codec.initialize(url);
    SoundBuffer buffer = codec.readAll();
    codec.cleanup();
    codec = null;
    if (buffer != null) {
      bufferMap.put(filenameURL.getFilename(), buffer);
    } else {
      errorMessage("Sound buffer null in method 'loadSound'");
    }
    return true;
  }
  










  public boolean loadSound(SoundBuffer buffer, String identifier)
  {
    if (bufferMap == null)
    {
      bufferMap = new HashMap();
      importantMessage("Buffer Map was null in method 'loadSound'");
    }
    

    if (errorCheck(identifier == null, "Identifier not specified in method 'loadSound'"))
    {
      return false;
    }
    
    if (bufferMap.get(identifier) != null) {
      return true;
    }
    
    if (buffer != null) {
      bufferMap.put(identifier, buffer);
    } else {
      errorMessage("Sound buffer null in method 'loadSound'");
    }
    return true;
  }
  





  public void setMasterVolume(float value)
  {
    super.setMasterVolume(value);
    
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
  















  public void newSource(boolean priority, boolean toStream, boolean toLoop, String sourcename, FilenameURL filenameURL, float x, float y, float z, int attModel, float distOrRoll)
  {
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
    
    if ((!toStream) && (buffer != null)) {
      buffer.trimData(1048576);
    }
    sourceMap.put(sourcename, new SourceJavaSound(listener, priority, toStream, toLoop, sourcename, filenameURL, buffer, x, y, z, attModel, distOrRoll, false));
  }
  


















  public void rawDataStream(AudioFormat audioFormat, boolean priority, String sourcename, float x, float y, float z, int attModel, float distOrRoll)
  {
    sourceMap.put(sourcename, new SourceJavaSound(listener, audioFormat, priority, sourcename, x, y, z, attModel, distOrRoll));
  }
  





















  public void quickPlay(boolean priority, boolean toStream, boolean toLoop, String sourcename, FilenameURL filenameURL, float x, float y, float z, int attModel, float distOrRoll, boolean temporary)
  {
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
    
    if ((!toStream) && (buffer != null)) {
      buffer.trimData(1048576);
    }
    sourceMap.put(sourcename, new SourceJavaSound(listener, priority, toStream, toLoop, sourcename, filenameURL, buffer, x, y, z, attModel, distOrRoll, temporary));
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
        if ((!toStream) && (buffer != null))
        {
          buffer.trimData(1048576);
        }
        if ((toStream) || (buffer != null))
        {
          sourceMap.put(sourcename, new SourceJavaSound(listener, source, buffer));
        }
      }
    }
  }
  








  public void setListenerVelocity(float x, float y, float z)
  {
    super.setListenerVelocity(x, y, z);
    
    listenerMoved();
  }
  




  public void dopplerChanged()
  {
    super.dopplerChanged();
    
    listenerMoved();
  }
  




  public static Mixer getMixer()
  {
    return mixer(false, null);
  }
  




  public static void setMixer(Mixer m)
    throws SoundSystemException
  {
    mixer(true, m);
    SoundSystemException e = SoundSystem.getLastException();
    SoundSystem.setException(null);
    if (e != null) {
      throw e;
    }
  }
  





  private static synchronized Mixer mixer(boolean action, Mixer m)
  {
    if (action == true)
    {
      if (m == null) {
        return myMixer;
      }
      MixerRanking mixerRanker = new MixerRanking();
      try
      {
        mixerRanker.rank(m.getMixerInfo());
      }
      catch (Exception ljse)
      {
        SoundSystemConfig.getLogger().printStackTrace(ljse, 1);
        SoundSystem.setException(ljse);
      }
      myMixer = m;
      mixerRanking(true, mixerRanker);
      
      if (instance != null)
      {
        ListIterator<Channel> itr = instancenormalChannels.listIterator();
        
        SoundSystem.setException(null);
        while (itr.hasNext())
        {
          ChannelJavaSound c = (ChannelJavaSound)itr.next();
          c.newMixer(m);
        }
        itr = instancestreamingChannels.listIterator();
        while (itr.hasNext())
        {
          ChannelJavaSound c = (ChannelJavaSound)itr.next();
          c.newMixer(m);
        }
      }
    }
    return myMixer;
  }
  
  public static MixerRanking getMixerRanking()
  {
    return mixerRanking(false, null);
  }
  

  private static synchronized MixerRanking mixerRanking(boolean action, MixerRanking value)
  {
    if (action == true)
      myMixerRanking = value;
    return myMixerRanking;
  }
  
  public static void setMinSampleRate(int value)
  {
    minSampleRate(true, value);
  }
  
  private static synchronized int minSampleRate(boolean action, int value) {
    if (action == true)
      minSampleRate = value;
    return minSampleRate;
  }
  
  public static void setMaxSampleRate(int value) {
    maxSampleRate(true, value);
  }
  
  private static synchronized int maxSampleRate(boolean action, int value) {
    if (action == true)
      maxSampleRate = value;
    return maxSampleRate;
  }
  
  public static void setLineCount(int value) {
    lineCount(true, value);
  }
  
  private static synchronized int lineCount(boolean action, int value) {
    if (action == true)
      lineCount = value;
    return lineCount;
  }
  
  public static void useGainControl(boolean value) {
    useGainControl(true, value);
  }
  
  private static synchronized boolean useGainControl(boolean action, boolean value)
  {
    if (action == true)
      useGainControl = value;
    return useGainControl;
  }
  
  public static void usePanControl(boolean value) {
    usePanControl(true, value);
  }
  
  private static synchronized boolean usePanControl(boolean action, boolean value)
  {
    if (action == true)
      usePanControl = value;
    return usePanControl;
  }
  
  public static void useSampleRateControl(boolean value) {
    useSampleRateControl(true, value);
  }
  
  private static synchronized boolean useSampleRateControl(boolean action, boolean value)
  {
    if (action == true)
      useSampleRateControl = value;
    return useSampleRateControl;
  }
  




  public static String getTitle()
  {
    return "Java Sound";
  }
  




  public static String getDescription()
  {
    return "The Java Sound API.  For more information, see http://java.sun.com/products/java-media/sound/";
  }
  






  public String getClassName()
  {
    return "LibraryJavaSound";
  }
  





  public static class MixerRanking
  {
    public static final int HIGH = 1;
    




    public static final int MEDIUM = 2;
    




    public static final int LOW = 3;
    




    public static final int NONE = 4;
    



    public static int MIXER_EXISTS_PRIORITY = 1;
    



    public static int MIN_SAMPLE_RATE_PRIORITY = 1;
    



    public static int MAX_SAMPLE_RATE_PRIORITY = 1;
    



    public static int LINE_COUNT_PRIORITY = 1;
    


    public static int GAIN_CONTROL_PRIORITY = 2;
    


    public static int PAN_CONTROL_PRIORITY = 2;
    


    public static int SAMPLE_RATE_CONTROL_PRIORITY = 3;
    


    public Mixer.Info mixerInfo = null;
    



    public int rank = 0;
    


    public boolean mixerExists = false;
    



    public boolean minSampleRateOK = false;
    



    public boolean maxSampleRateOK = false;
    



    public boolean lineCountOK = false;
    


    public boolean gainControlOK = false;
    


    public boolean panControlOK = false;
    



    public boolean sampleRateControlOK = false;
    




    public int minSampleRatePossible = -1;
    



    public int maxSampleRatePossible = -1;
    


    public int maxLinesPossible = 0;
    










    public MixerRanking() {}
    










    public MixerRanking(Mixer.Info i, int r, boolean e, boolean mnsr, boolean mxsr, boolean lc, boolean gc, boolean pc, boolean src)
    {
      mixerInfo = i;
      rank = r;
      mixerExists = e;
      minSampleRateOK = mnsr;
      maxSampleRateOK = mxsr;
      lineCountOK = lc;
      gainControlOK = gc;
      panControlOK = pc;
      sampleRateControlOK = src;
    }
    





    public void rank(Mixer.Info i)
      throws LibraryJavaSound.Exception
    {
      if (i == null) {
        throw new LibraryJavaSound.Exception("No Mixer info specified in method 'MixerRanking.rank'", this);
      }
      mixerInfo = i;
      
      try
      {
        m = AudioSystem.getMixer(mixerInfo);
      }
      catch (Exception e)
      {
        throw new LibraryJavaSound.Exception("Unable to acquire the specified Mixer in method 'MixerRanking.rank'", this);
      }
      
      if (m == null) {
        throw new LibraryJavaSound.Exception("Unable to acquire the specified Mixer in method 'MixerRanking.rank'", this);
      }
      mixerExists = true;
      



      try
      {
        format = new AudioFormat(LibraryJavaSound.minSampleRate(false, 0), 16, 2, true, false);
        
        lineInfo = new DataLine.Info(SourceDataLine.class, format);
      }
      catch (Exception e)
      {
        throw new LibraryJavaSound.Exception("Invalid minimum sample-rate specified in method 'MixerRanking.rank'", this);
      }
      
      if (!AudioSystem.isLineSupported(lineInfo))
      {
        if (MIN_SAMPLE_RATE_PRIORITY == 1) {
          throw new LibraryJavaSound.Exception("Specified minimum sample-rate not possible for Mixer '" + mixerInfo.getName() + "'", this);
        }
        

      }
      else {
        minSampleRateOK = true;
      }
      try
      {
        format = new AudioFormat(LibraryJavaSound.maxSampleRate(false, 0), 16, 2, true, false);
        
        lineInfo = new DataLine.Info(SourceDataLine.class, format);
      }
      catch (Exception e)
      {
        throw new LibraryJavaSound.Exception("Invalid maximum sample-rate specified in method 'MixerRanking.rank'", this);
      }
      
      if (!AudioSystem.isLineSupported(lineInfo))
      {
        if (MAX_SAMPLE_RATE_PRIORITY == 1) {
          throw new LibraryJavaSound.Exception("Specified maximum sample-rate not possible for Mixer '" + mixerInfo.getName() + "'", this);
        }
        

      }
      else {
        maxSampleRateOK = true;
      }
      




      if (minSampleRateOK)
      {
        minSampleRatePossible = LibraryJavaSound.minSampleRate(false, 0);

      }
      else
      {
        int lL = LibraryJavaSound.minSampleRate(false, 0);
        int uL = LibraryJavaSound.maxSampleRate(false, 0);
        while (uL - lL > 1)
        {
          int testSampleRate = lL + (uL - lL) / 2;
          format = new AudioFormat(testSampleRate, 16, 2, true, false);
          lineInfo = new DataLine.Info(SourceDataLine.class, format);
          if (AudioSystem.isLineSupported(lineInfo))
          {
            minSampleRatePossible = testSampleRate;
            uL = testSampleRate;
          }
          else
          {
            lL = testSampleRate;
          }
        }
      }
      if (maxSampleRateOK)
      {
        maxSampleRatePossible = LibraryJavaSound.maxSampleRate(false, 0);
      }
      else if (minSampleRatePossible != -1)
      {

        int uL = LibraryJavaSound.maxSampleRate(false, 0);
        int lL = minSampleRatePossible;
        while (uL - lL > 1)
        {
          int testSampleRate = lL + (uL - lL) / 2;
          format = new AudioFormat(testSampleRate, 16, 2, true, false);
          lineInfo = new DataLine.Info(SourceDataLine.class, format);
          if (AudioSystem.isLineSupported(lineInfo))
          {
            maxSampleRatePossible = testSampleRate;
            lL = testSampleRate;
          }
          else
          {
            uL = testSampleRate;
          }
        }
      }
      
      if ((minSampleRatePossible == -1) || (maxSampleRatePossible == -1)) {
        throw new LibraryJavaSound.Exception("No possible sample-rate found for Mixer '" + mixerInfo.getName() + "'", this);
      }
      


      AudioFormat format = new AudioFormat(minSampleRatePossible, 16, 2, true, false);
      Clip clip = null;
      try
      {
        DataLine.Info clipLineInfo = new DataLine.Info(Clip.class, format);
        
        clip = (Clip)m.getLine(clipLineInfo);
        byte[] buffer = new byte[10];
        clip.open(format, buffer, 0, buffer.length);
      }
      catch (Exception e)
      {
        throw new LibraryJavaSound.Exception("Unable to attach an actual audio buffer to an actual Clip... Mixer '" + mixerInfo.getName() + "' is unuseable.", this);
      }
      




      maxLinesPossible = 1;
      DataLine.Info lineInfo = new DataLine.Info(SourceDataLine.class, format);
      SourceDataLine[] lines = new SourceDataLine[LibraryJavaSound.lineCount(false, 0) - 1];
      
      int c = 0;
      
      for (int x = 1; x < lines.length + 1; x++)
      {
        try
        {
          lines[(x - 1)] = ((SourceDataLine)m.getLine(lineInfo));
        }
        catch (Exception e)
        {
          if (x == 0) {
            throw new LibraryJavaSound.Exception("No output lines possible for Mixer '" + mixerInfo.getName() + "'", this);
          }
          
          if (LINE_COUNT_PRIORITY == 1) {
            throw new LibraryJavaSound.Exception("Specified maximum number of lines not possible for Mixer '" + mixerInfo.getName() + "'", this);
          }
          
          break;
        }
        maxLinesPossible = (x + 1);
      }
      try
      {
        clip.close();
      }
      catch (Exception e) {}
      
      clip = null;
      if (maxLinesPossible == LibraryJavaSound.lineCount(false, 0))
      {
        lineCountOK = true;
      }
      

      if (!LibraryJavaSound.useGainControl(false, false))
      {
        GAIN_CONTROL_PRIORITY = 4;
      }
      else if (!lines[0].isControlSupported(FloatControl.Type.MASTER_GAIN))
      {

        if (GAIN_CONTROL_PRIORITY == 1) {
          throw new LibraryJavaSound.Exception("Gain control not available for Mixer '" + mixerInfo.getName() + "'", this);
        }
        

      }
      else {
        gainControlOK = true;
      }
      if (!LibraryJavaSound.usePanControl(false, false))
      {
        PAN_CONTROL_PRIORITY = 4;
      }
      else if (!lines[0].isControlSupported(FloatControl.Type.PAN))
      {
        if (PAN_CONTROL_PRIORITY == 1) {
          throw new LibraryJavaSound.Exception("Pan control not available for Mixer '" + mixerInfo.getName() + "'", this);
        }
        

      }
      else {
        panControlOK = true;
      }
      if (!LibraryJavaSound.useSampleRateControl(false, false))
      {
        SAMPLE_RATE_CONTROL_PRIORITY = 4;
      }
      else if (!lines[0].isControlSupported(FloatControl.Type.SAMPLE_RATE))
      {

        if (SAMPLE_RATE_CONTROL_PRIORITY == 1) {
          throw new LibraryJavaSound.Exception("Sample-rate control not available for Mixer '" + mixerInfo.getName() + "'", this);
        }
        

      }
      else
      {
        sampleRateControlOK = true;
      }
      

      rank += getRankValue(mixerExists, MIXER_EXISTS_PRIORITY);
      rank += getRankValue(minSampleRateOK, MIN_SAMPLE_RATE_PRIORITY);
      rank += getRankValue(maxSampleRateOK, MAX_SAMPLE_RATE_PRIORITY);
      rank += getRankValue(lineCountOK, LINE_COUNT_PRIORITY);
      rank += getRankValue(gainControlOK, GAIN_CONTROL_PRIORITY);
      rank += getRankValue(panControlOK, PAN_CONTROL_PRIORITY);
      rank += getRankValue(sampleRateControlOK, SAMPLE_RATE_CONTROL_PRIORITY);
      


      Mixer m = null;
      format = null;
      lineInfo = null;
      lines = null;
    }
    







    private int getRankValue(boolean property, int priority)
    {
      if (property) {
        return 2;
      }
      
      if (priority == 4) {
        return 2;
      }
      if (priority == 3) {
        return 1;
      }
      return 0;
    }
  }
  





  public static class Exception
    extends SoundSystemException
  {
    public static final int MIXER_PROBLEM = 101;
    




    public static LibraryJavaSound.MixerRanking mixerRanking = null;
    





    public Exception(String message)
    {
      super();
    }
    






    public Exception(String message, int type)
    {
      super(type);
    }
    







    public Exception(String message, LibraryJavaSound.MixerRanking rank)
    {
      super(101);
      mixerRanking = rank;
    }
  }
}
