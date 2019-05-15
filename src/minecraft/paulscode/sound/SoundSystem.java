package paulscode.sound;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Set;
import javax.sound.sampled.AudioFormat;




























































































public class SoundSystem
{
  private static final boolean GET = false;
  private static final boolean SET = true;
  private static final boolean XXX = false;
  protected SoundSystemLogger logger;
  protected Library soundLibrary;
  protected List<CommandObject> commandQueue;
  private List<CommandObject> sourcePlayList;
  protected CommandThread commandThread;
  public Random randomNumberGenerator;
  protected String className = "SoundSystem";
  



  private static Class currentLibrary = null;
  



  private static boolean initialized = false;
  



  private static SoundSystemException lastException = null;
  








  public SoundSystem()
  {
    logger = SoundSystemConfig.getLogger();
    
    if (logger == null)
    {
      logger = new SoundSystemLogger();
      SoundSystemConfig.setLogger(logger);
    }
    
    linkDefaultLibrariesAndCodecs();
    
    LinkedList<Class> libraries = SoundSystemConfig.getLibraries();
    
    if (libraries != null)
    {
      ListIterator<Class> i = libraries.listIterator();
      
      while (i.hasNext())
      {
        Class c = (Class)i.next();
        try
        {
          init(c);
          return;
        }
        catch (SoundSystemException sse)
        {
          logger.printExceptionMessage(sse, 1);
        }
      }
    }
    try
    {
      init(Library.class);
      return;
    }
    catch (SoundSystemException sse)
    {
      logger.printExceptionMessage(sse, 1);
    }
  }
  






  public SoundSystem(Class libraryClass)
    throws SoundSystemException
  {
    logger = SoundSystemConfig.getLogger();
    
    if (logger == null)
    {
      logger = new SoundSystemLogger();
      SoundSystemConfig.setLogger(logger);
    }
    linkDefaultLibrariesAndCodecs();
    
    init(libraryClass);
  }
  








  protected void linkDefaultLibrariesAndCodecs() {}
  








  protected void init(Class libraryClass)
    throws SoundSystemException
  {
    message("", 0);
    message("Starting up " + className + "...", 0);
    

    randomNumberGenerator = new Random();
    
    commandQueue = new LinkedList();
    
    sourcePlayList = new LinkedList();
    

    commandThread = new CommandThread(this);
    commandThread.start();
    
    snooze(200L);
    
    newLibrary(libraryClass);
    message("", 0);
  }
  




  public void cleanup()
  {
    boolean killException = false;
    message("", 0);
    message(className + " shutting down...", 0);
    

    try
    {
      commandThread.kill();
      commandThread.interrupt();
    }
    catch (Exception e)
    {
      killException = true;
    }
    
    if (!killException)
    {

      for (int i = 0; i < 50; i++)
      {
        if (!commandThread.alive())
          break;
        snooze(100L);
      }
    }
    

    if ((killException) || (commandThread.alive()))
    {
      errorMessage("Command thread did not die!", 0);
      message("Ignoring errors... continuing clean-up.", 0);
    }
    
    initialized(true, false);
    currentLibrary(true, null);
    
    try
    {
      if (soundLibrary != null) {
        soundLibrary.cleanup();
      }
    }
    catch (Exception e) {
      errorMessage("Problem during Library.cleanup()!", 0);
      message("Ignoring errors... continuing clean-up.", 0);
    }
    

    try
    {
      if (commandQueue != null) {
        commandQueue.clear();
      }
    }
    catch (Exception e) {
      errorMessage("Unable to clear the command queue!", 0);
      message("Ignoring errors... continuing clean-up.", 0);
    }
    

    try
    {
      if (sourcePlayList != null) {
        sourcePlayList.clear();
      }
    }
    catch (Exception e) {
      errorMessage("Unable to clear the source management list!", 0);
      message("Ignoring errors... continuing clean-up.", 0);
    }
    

    randomNumberGenerator = null;
    soundLibrary = null;
    commandQueue = null;
    sourcePlayList = null;
    commandThread = null;
    
    importantMessage("Author: Paul Lamb, www.paulscode.com", 1);
    message("", 0);
  }
  







  public void interruptCommandThread()
  {
    if (commandThread == null)
    {
      errorMessage("Command Thread null in method 'interruptCommandThread'", 0);
      
      return;
    }
    
    commandThread.interrupt();
  }
  









  public void loadSound(String filename)
  {
    CommandQueue(new CommandObject(2, new FilenameURL(filename)));
    

    commandThread.interrupt();
  }
  









  public void loadSound(URL url, String identifier)
  {
    CommandQueue(new CommandObject(2, new FilenameURL(url, identifier)));
    

    commandThread.interrupt();
  }
  









  public void loadSound(byte[] data, AudioFormat format, String identifier)
  {
    CommandQueue(new CommandObject(3, identifier, new SoundBuffer(data, format)));
    


    commandThread.interrupt();
  }
  











  public void unloadSound(String filename)
  {
    CommandQueue(new CommandObject(4, filename));
    
    commandThread.interrupt();
  }
  













  public void queueSound(String sourcename, String filename)
  {
    CommandQueue(new CommandObject(5, sourcename, new FilenameURL(filename)));
    

    commandThread.interrupt();
  }
  












  public void queueSound(String sourcename, URL url, String identifier)
  {
    CommandQueue(new CommandObject(5, sourcename, new FilenameURL(url, identifier)));
    

    commandThread.interrupt();
  }
  








  public void dequeueSound(String sourcename, String filename)
  {
    CommandQueue(new CommandObject(6, sourcename, filename));
    

    commandThread.interrupt();
  }
  


















  public void fadeOut(String sourcename, String filename, long milis)
  {
    FilenameURL fu = null;
    if (filename != null) {
      fu = new FilenameURL(filename);
    }
    CommandQueue(new CommandObject(7, sourcename, fu, milis));
    

    commandThread.interrupt();
  }
  


















  public void fadeOut(String sourcename, URL url, String identifier, long milis)
  {
    FilenameURL fu = null;
    if ((url != null) && (identifier != null)) {
      fu = new FilenameURL(url, identifier);
    }
    CommandQueue(new CommandObject(7, sourcename, fu, milis));
    

    commandThread.interrupt();
  }
  





















  public void fadeOutIn(String sourcename, String filename, long milisOut, long milisIn)
  {
    CommandQueue(new CommandObject(8, sourcename, new FilenameURL(filename), milisOut, milisIn));
    



    commandThread.interrupt();
  }
  




















  public void fadeOutIn(String sourcename, URL url, String identifier, long milisOut, long milisIn)
  {
    CommandQueue(new CommandObject(8, sourcename, new FilenameURL(url, identifier), milisOut, milisIn));
    



    commandThread.interrupt();
  }
  












  public void checkFadeVolumes()
  {
    CommandQueue(new CommandObject(9));
    
    commandThread.interrupt();
  }
  













  public void backgroundMusic(String sourcename, String filename, boolean toLoop)
  {
    CommandQueue(new CommandObject(12, true, true, toLoop, sourcename, new FilenameURL(filename), 0.0F, 0.0F, 0.0F, 0, 0.0F, false));
    


    CommandQueue(new CommandObject(24, sourcename));
    
    commandThread.interrupt();
  }
  












  public void backgroundMusic(String sourcename, URL url, String identifier, boolean toLoop)
  {
    CommandQueue(new CommandObject(12, true, true, toLoop, sourcename, new FilenameURL(url, identifier), 0.0F, 0.0F, 0.0F, 0, 0.0F, false));
    




    CommandQueue(new CommandObject(24, sourcename));
    
    commandThread.interrupt();
  }
  
















  public void newSource(boolean priority, String sourcename, String filename, boolean toLoop, float x, float y, float z, int attmodel, float distOrRoll)
  {
    CommandQueue(new CommandObject(10, priority, false, toLoop, sourcename, new FilenameURL(filename), x, y, z, attmodel, distOrRoll));
    


    commandThread.interrupt();
  }
  

















  public void newSource(boolean priority, String sourcename, URL url, String identifier, boolean toLoop, float x, float y, float z, int attmodel, float distOrRoll)
  {
    CommandQueue(new CommandObject(10, priority, false, toLoop, sourcename, new FilenameURL(url, identifier), x, y, z, attmodel, distOrRoll));
    



    commandThread.interrupt();
  }
  



















  public void newStreamingSource(boolean priority, String sourcename, String filename, boolean toLoop, float x, float y, float z, int attmodel, float distOrRoll)
  {
    CommandQueue(new CommandObject(10, priority, true, toLoop, sourcename, new FilenameURL(filename), x, y, z, attmodel, distOrRoll));
    


    commandThread.interrupt();
  }
  


















  public void newStreamingSource(boolean priority, String sourcename, URL url, String identifier, boolean toLoop, float x, float y, float z, int attmodel, float distOrRoll)
  {
    CommandQueue(new CommandObject(10, priority, true, toLoop, sourcename, new FilenameURL(url, identifier), x, y, z, attmodel, distOrRoll));
    


    commandThread.interrupt();
  }
  
















  public void rawDataStream(AudioFormat audioFormat, boolean priority, String sourcename, float x, float y, float z, int attModel, float distOrRoll)
  {
    CommandQueue(new CommandObject(11, audioFormat, priority, sourcename, x, y, z, attModel, distOrRoll));
    

    commandThread.interrupt();
  }
  


















  public String quickPlay(boolean priority, String filename, boolean toLoop, float x, float y, float z, int attmodel, float distOrRoll)
  {
    String sourcename = "Source_" + randomNumberGenerator.nextInt() + "_" + randomNumberGenerator.nextInt();
    



    CommandQueue(new CommandObject(12, priority, false, toLoop, sourcename, new FilenameURL(filename), x, y, z, attmodel, distOrRoll, true));
    


    CommandQueue(new CommandObject(24, sourcename));
    
    commandThread.interrupt();
    

    return sourcename;
  }
  



















  public String quickPlay(boolean priority, URL url, String identifier, boolean toLoop, float x, float y, float z, int attmodel, float distOrRoll)
  {
    String sourcename = "Source_" + randomNumberGenerator.nextInt() + "_" + randomNumberGenerator.nextInt();
    



    CommandQueue(new CommandObject(12, priority, false, toLoop, sourcename, new FilenameURL(url, identifier), x, y, z, attmodel, distOrRoll, true));
    



    CommandQueue(new CommandObject(24, sourcename));
    
    commandThread.interrupt();
    

    return sourcename;
  }
  























  public String quickStream(boolean priority, String filename, boolean toLoop, float x, float y, float z, int attmodel, float distOrRoll)
  {
    String sourcename = "Source_" + randomNumberGenerator.nextInt() + "_" + randomNumberGenerator.nextInt();
    



    CommandQueue(new CommandObject(12, priority, true, toLoop, sourcename, new FilenameURL(filename), x, y, z, attmodel, distOrRoll, true));
    


    CommandQueue(new CommandObject(24, sourcename));
    
    commandThread.interrupt();
    

    return sourcename;
  }
  





















  public String quickStream(boolean priority, URL url, String identifier, boolean toLoop, float x, float y, float z, int attmodel, float distOrRoll)
  {
    String sourcename = "Source_" + randomNumberGenerator.nextInt() + "_" + randomNumberGenerator.nextInt();
    



    CommandQueue(new CommandObject(12, priority, true, toLoop, sourcename, new FilenameURL(url, identifier), x, y, z, attmodel, distOrRoll, true));
    



    CommandQueue(new CommandObject(24, sourcename));
    
    commandThread.interrupt();
    

    return sourcename;
  }
  







  public void setPosition(String sourcename, float x, float y, float z)
  {
    CommandQueue(new CommandObject(13, sourcename, x, y, z));
    
    commandThread.interrupt();
  }
  




  public void setVolume(String sourcename, float value)
  {
    CommandQueue(new CommandObject(14, sourcename, value));
    
    commandThread.interrupt();
  }
  






  public float getVolume(String sourcename)
  {
    synchronized (SoundSystemConfig.THREAD_SYNC)
    {
      if (soundLibrary != null) {
        return soundLibrary.getVolume(sourcename);
      }
      return 0.0F;
    }
  }
  





  public void setPitch(String sourcename, float value)
  {
    CommandQueue(new CommandObject(15, sourcename, value));
    
    commandThread.interrupt();
  }
  





  public float getPitch(String sourcename)
  {
    if (soundLibrary != null) {
      return soundLibrary.getPitch(sourcename);
    }
    return 1.0F;
  }
  






  public void setPriority(String sourcename, boolean pri)
  {
    CommandQueue(new CommandObject(16, sourcename, pri));
    
    commandThread.interrupt();
  }
  




  public void setLooping(String sourcename, boolean lp)
  {
    CommandQueue(new CommandObject(17, sourcename, lp));
    
    commandThread.interrupt();
  }
  






  public void setAttenuation(String sourcename, int model)
  {
    CommandQueue(new CommandObject(18, sourcename, model));
    
    commandThread.interrupt();
  }
  






  public void setDistOrRoll(String sourcename, float dr)
  {
    CommandQueue(new CommandObject(19, sourcename, dr));
    
    commandThread.interrupt();
  }
  






  public void changeDopplerFactor(float dopplerFactor)
  {
    CommandQueue(new CommandObject(20, dopplerFactor));
    
    commandThread.interrupt();
  }
  






  public void changeDopplerVelocity(float dopplerVelocity)
  {
    CommandQueue(new CommandObject(21, dopplerVelocity));
    
    commandThread.interrupt();
  }
  









  public void setVelocity(String sourcename, float x, float y, float z)
  {
    CommandQueue(new CommandObject(22, sourcename, x, y, z));
    
    commandThread.interrupt();
  }
  








  public void setListenerVelocity(float x, float y, float z)
  {
    CommandQueue(new CommandObject(23, x, y, z));
    
    commandThread.interrupt();
  }
  




  public float millisecondsPlayed(String sourcename)
  {
    synchronized (SoundSystemConfig.THREAD_SYNC)
    {
      return soundLibrary.millisecondsPlayed(sourcename);
    }
  }
  













  public void feedRawAudioData(String sourcename, byte[] buffer)
  {
    CommandQueue(new CommandObject(25, sourcename, buffer));
    
    commandThread.interrupt();
  }
  



  public void play(String sourcename)
  {
    CommandQueue(new CommandObject(24, sourcename));
    commandThread.interrupt();
  }
  



  public void pause(String sourcename)
  {
    CommandQueue(new CommandObject(26, sourcename));
    commandThread.interrupt();
  }
  



  public void stop(String sourcename)
  {
    CommandQueue(new CommandObject(27, sourcename));
    commandThread.interrupt();
  }
  



  public void rewind(String sourcename)
  {
    CommandQueue(new CommandObject(28, sourcename));
    commandThread.interrupt();
  }
  



  public void flush(String sourcename)
  {
    CommandQueue(new CommandObject(29, sourcename));
    commandThread.interrupt();
  }
  





  public void cull(String sourcename)
  {
    CommandQueue(new CommandObject(30, sourcename));
    commandThread.interrupt();
  }
  





  public void activate(String sourcename)
  {
    CommandQueue(new CommandObject(31, sourcename));
    commandThread.interrupt();
  }
  












  public void setTemporary(String sourcename, boolean temporary)
  {
    CommandQueue(new CommandObject(32, sourcename, temporary));
    
    commandThread.interrupt();
  }
  




  public void removeSource(String sourcename)
  {
    CommandQueue(new CommandObject(33, sourcename));
    
    commandThread.interrupt();
  }
  





  public void moveListener(float x, float y, float z)
  {
    CommandQueue(new CommandObject(34, x, y, z));
    
    commandThread.interrupt();
  }
  





  public void setListenerPosition(float x, float y, float z)
  {
    CommandQueue(new CommandObject(35, x, y, z));
    
    commandThread.interrupt();
  }
  




  public void turnListener(float angle)
  {
    CommandQueue(new CommandObject(36, angle));
    
    commandThread.interrupt();
  }
  



  public void setListenerAngle(float angle)
  {
    CommandQueue(new CommandObject(37, angle));
    
    commandThread.interrupt();
  }
  









  public void setListenerOrientation(float lookX, float lookY, float lookZ, float upX, float upY, float upZ)
  {
    CommandQueue(new CommandObject(38, lookX, lookY, lookZ, upX, upY, upZ));
    
    commandThread.interrupt();
  }
  




  public void setMasterVolume(float value)
  {
    CommandQueue(new CommandObject(39, value));
    
    commandThread.interrupt();
  }
  




  public float getMasterVolume()
  {
    return SoundSystemConfig.getMasterGain();
  }
  





  public ListenerData getListenerData()
  {
    synchronized (SoundSystemConfig.THREAD_SYNC)
    {
      return soundLibrary.getListenerData();
    }
  }
  






  public boolean switchLibrary(Class libraryClass)
    throws SoundSystemException
  {
    synchronized (SoundSystemConfig.THREAD_SYNC)
    {
      initialized(true, false);
      
      HashMap<String, Source> sourceMap = null;
      ListenerData listenerData = null;
      
      boolean wasMidiChannel = false;
      MidiChannel midiChannel = null;
      FilenameURL midiFilenameURL = null;
      String midiSourcename = "";
      boolean midiToLoop = true;
      
      if (soundLibrary != null)
      {
        currentLibrary(true, null);
        sourceMap = copySources(soundLibrary.getSources());
        listenerData = soundLibrary.getListenerData();
        midiChannel = soundLibrary.getMidiChannel();
        if (midiChannel != null)
        {
          wasMidiChannel = true;
          midiToLoop = midiChannel.getLooping();
          midiSourcename = midiChannel.getSourcename();
          midiFilenameURL = midiChannel.getFilenameURL();
        }
        
        soundLibrary.cleanup();
        soundLibrary = null;
      }
      message("", 0);
      message("Switching to " + SoundSystemConfig.getLibraryTitle(libraryClass), 0);
      
      message("(" + SoundSystemConfig.getLibraryDescription(libraryClass) + ")", 1);
      

      try
      {
        soundLibrary = ((Library)libraryClass.newInstance());
      }
      catch (InstantiationException ie)
      {
        errorMessage("The specified library did not load properly", 1);
      }
      catch (IllegalAccessException iae)
      {
        errorMessage("The specified library did not load properly", 1);
      }
      catch (ExceptionInInitializerError eiie)
      {
        errorMessage("The specified library did not load properly", 1);
      }
      catch (SecurityException se)
      {
        errorMessage("The specified library did not load properly", 1);
      }
      
      if (errorCheck(soundLibrary == null, "Library null after initialization in method 'switchLibrary'", 1))
      {

        SoundSystemException sse = new SoundSystemException(className + " did not load properly.  " + "Library was null after initialization.", 4);
        


        lastException(true, sse);
        initialized(true, true);
        throw sse;
      }
      
      try
      {
        soundLibrary.init();
      }
      catch (SoundSystemException sse)
      {
        lastException(true, sse);
        initialized(true, true);
        throw sse;
      }
      
      soundLibrary.setListenerData(listenerData);
      if (wasMidiChannel)
      {
        if (midiChannel != null)
          midiChannel.cleanup();
        midiChannel = new MidiChannel(midiToLoop, midiSourcename, midiFilenameURL);
        
        soundLibrary.setMidiChannel(midiChannel);
      }
      soundLibrary.copySources(sourceMap);
      
      message("", 0);
      
      lastException(true, null);
      initialized(true, true);
      
      return true;
    }
  }
  






  public boolean newLibrary(Class libraryClass)
    throws SoundSystemException
  {
    initialized(true, false);
    
    CommandQueue(new CommandObject(40, libraryClass));
    
    commandThread.interrupt();
    
    for (int x = 0; (!initialized(false, false)) && (x < 100); x++)
    {
      snooze(400L);
      commandThread.interrupt();
    }
    
    if (!initialized(false, false))
    {
      SoundSystemException sse = new SoundSystemException(className + " did not load after 30 seconds.", 4);
      


      lastException(true, sse);
      throw sse;
    }
    

    SoundSystemException sse = lastException(false, null);
    if (sse != null) {
      throw sse;
    }
    return true;
  }
  








  private void CommandNewLibrary(Class libraryClass)
  {
    initialized(true, false);
    
    String headerMessage = "Initializing ";
    if (soundLibrary != null)
    {
      currentLibrary(true, null);
      
      headerMessage = "Switching to ";
      soundLibrary.cleanup();
      soundLibrary = null;
    }
    message(headerMessage + SoundSystemConfig.getLibraryTitle(libraryClass), 0);
    
    message("(" + SoundSystemConfig.getLibraryDescription(libraryClass) + ")", 1);
    

    try
    {
      soundLibrary = ((Library)libraryClass.newInstance());
    }
    catch (InstantiationException ie)
    {
      errorMessage("The specified library did not load properly", 1);
    }
    catch (IllegalAccessException iae)
    {
      errorMessage("The specified library did not load properly", 1);
    }
    catch (ExceptionInInitializerError eiie)
    {
      errorMessage("The specified library did not load properly", 1);
    }
    catch (SecurityException se)
    {
      errorMessage("The specified library did not load properly", 1);
    }
    
    if (errorCheck(soundLibrary == null, "Library null after initialization in method 'newLibrary'", 1))
    {

      lastException(true, new SoundSystemException(className + " did not load properly.  " + "Library was null after initialization.", 4));
      


      importantMessage("Switching to silent mode", 1);
      
      try
      {
        soundLibrary = new Library();
      }
      catch (SoundSystemException sse)
      {
        lastException(true, new SoundSystemException("Silent mode did not load properly.  Library was null after initialization.", 4));
        


        initialized(true, true);
        return;
      }
    }
    
    try
    {
      soundLibrary.init();
    }
    catch (SoundSystemException sse)
    {
      lastException(true, sse);
      initialized(true, true);
      return;
    }
    
    lastException(true, null);
    initialized(true, true);
  }
  






  private void CommandInitialize()
  {
    try
    {
      if (errorCheck(soundLibrary == null, "Library null after initialization in method 'CommandInitialize'", 1))
      {


        SoundSystemException sse = new SoundSystemException(className + " did not load properly.  " + "Library was null after initialization.", 4);
        


        lastException(true, sse);
        throw sse;
      }
      soundLibrary.init();
    }
    catch (SoundSystemException sse)
    {
      lastException(true, sse);
      initialized(true, true);
    }
  }
  





  private void CommandLoadSound(FilenameURL filenameURL)
  {
    if (soundLibrary != null) {
      soundLibrary.loadSound(filenameURL);
    } else {
      errorMessage("Variable 'soundLibrary' null in method 'CommandLoadSound'", 0);
    }
  }
  








  private void CommandLoadSound(SoundBuffer buffer, String identifier)
  {
    if (soundLibrary != null) {
      soundLibrary.loadSound(buffer, identifier);
    } else {
      errorMessage("Variable 'soundLibrary' null in method 'CommandLoadSound'", 0);
    }
  }
  





  private void CommandUnloadSound(String filename)
  {
    if (soundLibrary != null) {
      soundLibrary.unloadSound(filename);
    } else {
      errorMessage("Variable 'soundLibrary' null in method 'CommandLoadSound'", 0);
    }
  }
  









  private void CommandQueueSound(String sourcename, FilenameURL filenameURL)
  {
    if (soundLibrary != null) {
      soundLibrary.queueSound(sourcename, filenameURL);
    } else {
      errorMessage("Variable 'soundLibrary' null in method 'CommandQueueSound'", 0);
    }
  }
  








  private void CommandDequeueSound(String sourcename, String filename)
  {
    if (soundLibrary != null) {
      soundLibrary.dequeueSound(sourcename, filename);
    } else {
      errorMessage("Variable 'soundLibrary' null in method 'CommandDequeueSound'", 0);
    }
  }
  















  private void CommandFadeOut(String sourcename, FilenameURL filenameURL, long milis)
  {
    if (soundLibrary != null) {
      soundLibrary.fadeOut(sourcename, filenameURL, milis);
    } else {
      errorMessage("Variable 'soundLibrary' null in method 'CommandFadeOut'", 0);
    }
  }
  
















  private void CommandFadeOutIn(String sourcename, FilenameURL filenameURL, long milisOut, long milisIn)
  {
    if (soundLibrary != null) {
      soundLibrary.fadeOutIn(sourcename, filenameURL, milisOut, milisIn);
    }
    else {
      errorMessage("Variable 'soundLibrary' null in method 'CommandFadeOutIn'", 0);
    }
  }
  












  private void CommandCheckFadeVolumes()
  {
    if (soundLibrary != null) {
      soundLibrary.checkFadeVolumes();
    } else {
      errorMessage("Variable 'soundLibrary' null in method 'CommandCheckFadeVolumes'", 0);
    }
  }
  


















  private void CommandNewSource(boolean priority, boolean toStream, boolean toLoop, String sourcename, FilenameURL filenameURL, float x, float y, float z, int attModel, float distORroll)
  {
    if (soundLibrary != null)
    {
      if ((filenameURL.getFilename().matches(SoundSystemConfig.EXTENSION_MIDI)) && (!SoundSystemConfig.midiCodec()))
      {


        soundLibrary.loadMidi(toLoop, sourcename, filenameURL);
      }
      else
      {
        soundLibrary.newSource(priority, toStream, toLoop, sourcename, filenameURL, x, y, z, attModel, distORroll);
      }
      

    }
    else {
      errorMessage("Variable 'soundLibrary' null in method 'CommandNewSource'", 0);
    }
  }
  















  private void CommandRawDataStream(AudioFormat audioFormat, boolean priority, String sourcename, float x, float y, float z, int attModel, float distOrRoll)
  {
    if (soundLibrary != null) {
      soundLibrary.rawDataStream(audioFormat, priority, sourcename, x, y, z, attModel, distOrRoll);
    }
    else {
      errorMessage("Variable 'soundLibrary' null in method 'CommandRawDataStream'", 0);
    }
  }
  




















  private void CommandQuickPlay(boolean priority, boolean toStream, boolean toLoop, String sourcename, FilenameURL filenameURL, float x, float y, float z, int attModel, float distORroll, boolean temporary)
  {
    if (soundLibrary != null)
    {
      if ((filenameURL.getFilename().matches(SoundSystemConfig.EXTENSION_MIDI)) && (!SoundSystemConfig.midiCodec()))
      {

        soundLibrary.loadMidi(toLoop, sourcename, filenameURL);
      }
      else
      {
        soundLibrary.quickPlay(priority, toStream, toLoop, sourcename, filenameURL, x, y, z, attModel, distORroll, temporary);
      }
      

    }
    else {
      errorMessage("Variable 'soundLibrary' null in method 'CommandQuickPlay'", 0);
    }
  }
  









  private void CommandSetPosition(String sourcename, float x, float y, float z)
  {
    if (soundLibrary != null) {
      soundLibrary.setPosition(sourcename, x, y, z);
    } else {
      errorMessage("Variable 'soundLibrary' null in method 'CommandMoveSource'", 0);
    }
  }
  






  private void CommandSetVolume(String sourcename, float value)
  {
    if (soundLibrary != null) {
      soundLibrary.setVolume(sourcename, value);
    } else {
      errorMessage("Variable 'soundLibrary' null in method 'CommandSetVolume'", 0);
    }
  }
  






  private void CommandSetPitch(String sourcename, float value)
  {
    if (soundLibrary != null) {
      soundLibrary.setPitch(sourcename, value);
    } else {
      errorMessage("Variable 'soundLibrary' null in method 'CommandSetPitch'", 0);
    }
  }
  







  private void CommandSetPriority(String sourcename, boolean pri)
  {
    if (soundLibrary != null) {
      soundLibrary.setPriority(sourcename, pri);
    } else {
      errorMessage("Variable 'soundLibrary' null in method 'CommandSetPriority'", 0);
    }
  }
  






  private void CommandSetLooping(String sourcename, boolean lp)
  {
    if (soundLibrary != null) {
      soundLibrary.setLooping(sourcename, lp);
    } else {
      errorMessage("Variable 'soundLibrary' null in method 'CommandSetLooping'", 0);
    }
  }
  








  private void CommandSetAttenuation(String sourcename, int model)
  {
    if (soundLibrary != null) {
      soundLibrary.setAttenuation(sourcename, model);
    } else {
      errorMessage("Variable 'soundLibrary' null in method 'CommandSetAttenuation'", 0);
    }
  }
  







  private void CommandSetDistOrRoll(String sourcename, float dr)
  {
    if (soundLibrary != null) {
      soundLibrary.setDistOrRoll(sourcename, dr);
    } else {
      errorMessage("Variable 'soundLibrary' null in method 'CommandSetDistOrRoll'", 0);
    }
  }
  








  private void CommandChangeDopplerFactor(float dopplerFactor)
  {
    if (soundLibrary != null)
    {
      SoundSystemConfig.setDopplerFactor(dopplerFactor);
      soundLibrary.dopplerChanged();
    }
    else {
      errorMessage("Variable 'soundLibrary' null in method 'CommandSetDopplerFactor'", 0);
    }
  }
  








  private void CommandChangeDopplerVelocity(float dopplerVelocity)
  {
    if (soundLibrary != null)
    {
      SoundSystemConfig.setDopplerVelocity(dopplerVelocity);
      soundLibrary.dopplerChanged();
    }
    else {
      errorMessage("Variable 'soundLibrary' null in method 'CommandSetDopplerFactor'", 0);
    }
  }
  











  private void CommandSetVelocity(String sourcename, float x, float y, float z)
  {
    if (soundLibrary != null) {
      soundLibrary.setVelocity(sourcename, x, y, z);
    } else {
      errorMessage("Variable 'soundLibrary' null in method 'CommandVelocity'", 0);
    }
  }
  










  private void CommandSetListenerVelocity(float x, float y, float z)
  {
    if (soundLibrary != null) {
      soundLibrary.setListenerVelocity(x, y, z);
    } else {
      errorMessage("Variable 'soundLibrary' null in method 'CommandSetListenerVelocity'", 0);
    }
  }
  






  private void CommandPlay(String sourcename)
  {
    if (soundLibrary != null) {
      soundLibrary.play(sourcename);
    } else {
      errorMessage("Variable 'soundLibrary' null in method 'CommandPlay'", 0);
    }
  }
  








  private void CommandFeedRawAudioData(String sourcename, byte[] buffer)
  {
    if (soundLibrary != null) {
      soundLibrary.feedRawAudioData(sourcename, buffer);
    } else {
      errorMessage("Variable 'soundLibrary' null in method 'CommandFeedRawAudioData'", 0);
    }
  }
  





  private void CommandPause(String sourcename)
  {
    if (soundLibrary != null) {
      soundLibrary.pause(sourcename);
    } else {
      errorMessage("Variable 'soundLibrary' null in method 'CommandPause'", 0);
    }
  }
  





  private void CommandStop(String sourcename)
  {
    if (soundLibrary != null) {
      soundLibrary.stop(sourcename);
    } else {
      errorMessage("Variable 'soundLibrary' null in method 'CommandStop'", 0);
    }
  }
  





  private void CommandRewind(String sourcename)
  {
    if (soundLibrary != null) {
      soundLibrary.rewind(sourcename);
    } else {
      errorMessage("Variable 'soundLibrary' null in method 'CommandRewind'", 0);
    }
  }
  





  private void CommandFlush(String sourcename)
  {
    if (soundLibrary != null) {
      soundLibrary.flush(sourcename);
    } else {
      errorMessage("Variable 'soundLibrary' null in method 'CommandFlush'", 0);
    }
  }
  













  private void CommandSetTemporary(String sourcename, boolean temporary)
  {
    if (soundLibrary != null) {
      soundLibrary.setTemporary(sourcename, temporary);
    } else {
      errorMessage("Variable 'soundLibrary' null in method 'CommandSetActive'", 0);
    }
  }
  





  private void CommandRemoveSource(String sourcename)
  {
    if (soundLibrary != null) {
      soundLibrary.removeSource(sourcename);
    } else {
      errorMessage("Variable 'soundLibrary' null in method 'CommandRemoveSource'", 0);
    }
  }
  







  private void CommandMoveListener(float x, float y, float z)
  {
    if (soundLibrary != null) {
      soundLibrary.moveListener(x, y, z);
    } else {
      errorMessage("Variable 'soundLibrary' null in method 'CommandMoveListener'", 0);
    }
  }
  







  private void CommandSetListenerPosition(float x, float y, float z)
  {
    if (soundLibrary != null) {
      soundLibrary.setListenerPosition(x, y, z);
    } else {
      errorMessage("Variable 'soundLibrary' null in method 'CommandSetListenerPosition'", 0);
    }
  }
  







  private void CommandTurnListener(float angle)
  {
    if (soundLibrary != null) {
      soundLibrary.turnListener(angle);
    } else {
      errorMessage("Variable 'soundLibrary' null in method 'CommandTurnListener'", 0);
    }
  }
  






  private void CommandSetListenerAngle(float angle)
  {
    if (soundLibrary != null) {
      soundLibrary.setListenerAngle(angle);
    } else {
      errorMessage("Variable 'soundLibrary' null in method 'CommandSetListenerAngle'", 0);
    }
  }
  













  private void CommandSetListenerOrientation(float lookX, float lookY, float lookZ, float upX, float upY, float upZ)
  {
    if (soundLibrary != null) {
      soundLibrary.setListenerOrientation(lookX, lookY, lookZ, upX, upY, upZ);
    }
    else {
      errorMessage("Variable 'soundLibrary' null in method 'CommandSetListenerOrientation'", 0);
    }
  }
  







  private void CommandCull(String sourcename)
  {
    if (soundLibrary != null) {
      soundLibrary.cull(sourcename);
    } else {
      errorMessage("Variable 'soundLibrary' null in method 'CommandCull'", 0);
    }
  }
  





  private void CommandActivate(String sourcename)
  {
    if (soundLibrary != null) {
      soundLibrary.activate(sourcename);
    } else {
      errorMessage("Variable 'soundLibrary' null in method 'CommandActivate'", 0);
    }
  }
  






  private void CommandSetMasterVolume(float value)
  {
    if (soundLibrary != null) {
      soundLibrary.setMasterVolume(value);
    } else {
      errorMessage("Variable 'soundLibrary' null in method 'CommandSetMasterVolume'", 0);
    }
  }
  

















  protected void ManageSources() {}
  
















  public boolean CommandQueue(CommandObject newCommand)
  {
    synchronized (SoundSystemConfig.THREAD_SYNC)
    {
      if (newCommand == null)
      {

        boolean activations = false;
        


        while ((commandQueue != null) && (commandQueue.size() > 0))
        {

          CommandObject commandObject = (CommandObject)commandQueue.remove(0);
          
          if (commandObject != null)
          {
            switch (Command)
            {
            case 1: 
              CommandInitialize();
              break;
            case 2: 
              CommandLoadSound((FilenameURL)objectArgs[0]);
              
              break;
            case 3: 
              CommandLoadSound((SoundBuffer)objectArgs[0], stringArgs[0]);
              

              break;
            case 4: 
              CommandUnloadSound(stringArgs[0]);
              break;
            case 5: 
              CommandQueueSound(stringArgs[0], (FilenameURL)objectArgs[0]);
              
              break;
            case 6: 
              CommandDequeueSound(stringArgs[0], stringArgs[1]);
              
              break;
            case 7: 
              CommandFadeOut(stringArgs[0], (FilenameURL)objectArgs[0], longArgs[0]);
              

              break;
            case 8: 
              CommandFadeOutIn(stringArgs[0], (FilenameURL)objectArgs[0], longArgs[0], longArgs[1]);
              


              break;
            case 9: 
              CommandCheckFadeVolumes();
              break;
            case 10: 
              CommandNewSource(boolArgs[0], boolArgs[1], boolArgs[2], stringArgs[0], (FilenameURL)objectArgs[0], floatArgs[0], floatArgs[1], floatArgs[2], intArgs[0], floatArgs[3]);
              








              break;
            case 11: 
              CommandRawDataStream((AudioFormat)objectArgs[0], boolArgs[0], stringArgs[0], floatArgs[0], floatArgs[1], floatArgs[2], intArgs[0], floatArgs[3]);
              







              break;
            case 12: 
              CommandQuickPlay(boolArgs[0], boolArgs[1], boolArgs[2], stringArgs[0], (FilenameURL)objectArgs[0], floatArgs[0], floatArgs[1], floatArgs[2], intArgs[0], floatArgs[3], boolArgs[3]);
              









              break;
            case 13: 
              CommandSetPosition(stringArgs[0], floatArgs[0], floatArgs[1], floatArgs[2]);
              


              break;
            case 14: 
              CommandSetVolume(stringArgs[0], floatArgs[0]);
              
              break;
            case 15: 
              CommandSetPitch(stringArgs[0], floatArgs[0]);
              
              break;
            case 16: 
              CommandSetPriority(stringArgs[0], boolArgs[0]);
              
              break;
            case 17: 
              CommandSetLooping(stringArgs[0], boolArgs[0]);
              
              break;
            case 18: 
              CommandSetAttenuation(stringArgs[0], intArgs[0]);
              
              break;
            case 19: 
              CommandSetDistOrRoll(stringArgs[0], floatArgs[0]);
              
              break;
            case 20: 
              CommandChangeDopplerFactor(floatArgs[0]);
              
              break;
            case 21: 
              CommandChangeDopplerVelocity(floatArgs[0]);
              
              break;
            case 22: 
              CommandSetVelocity(stringArgs[0], floatArgs[0], floatArgs[1], floatArgs[2]);
              



              break;
            case 23: 
              CommandSetListenerVelocity(floatArgs[0], floatArgs[1], floatArgs[2]);
              



              break;
            




            case 24: 
              sourcePlayList.add(commandObject);
              break;
            case 25: 
              sourcePlayList.add(commandObject);
              break;
            
            case 26: 
              CommandPause(stringArgs[0]);
              break;
            case 27: 
              CommandStop(stringArgs[0]);
              break;
            case 28: 
              CommandRewind(stringArgs[0]);
              break;
            case 29: 
              CommandFlush(stringArgs[0]);
              break;
            case 30: 
              CommandCull(stringArgs[0]);
              break;
            case 31: 
              activations = true;
              CommandActivate(stringArgs[0]);
              break;
            case 32: 
              CommandSetTemporary(stringArgs[0], boolArgs[0]);
              
              break;
            case 33: 
              CommandRemoveSource(stringArgs[0]);
              break;
            case 34: 
              CommandMoveListener(floatArgs[0], floatArgs[1], floatArgs[2]);
              

              break;
            case 35: 
              CommandSetListenerPosition(floatArgs[0], floatArgs[1], floatArgs[2]);
              


              break;
            case 36: 
              CommandTurnListener(floatArgs[0]);
              break;
            case 37: 
              CommandSetListenerAngle(floatArgs[0]);
              
              break;
            case 38: 
              CommandSetListenerOrientation(floatArgs[0], floatArgs[1], floatArgs[2], floatArgs[3], floatArgs[4], floatArgs[5]);
              





              break;
            case 39: 
              CommandSetMasterVolume(floatArgs[0]);
              
              break;
            case 40: 
              CommandNewLibrary(classArgs[0]);
            }
            
          }
        }
        





        if (activations) {
          soundLibrary.replaySources();
        }
        


        while ((sourcePlayList != null) && (sourcePlayList.size() > 0))
        {

          CommandObject commandObject = (CommandObject)sourcePlayList.remove(0);
          if (commandObject != null)
          {

            switch (Command)
            {
            case 24: 
              CommandPlay(stringArgs[0]);
              break;
            case 25: 
              CommandFeedRawAudioData(stringArgs[0], buffer);
            }
            
          }
        }
        


        return (commandQueue != null) && (commandQueue.size() > 0);
      }
      


      if (commandQueue == null) {
        return false;
      }
      commandQueue.add(newCommand);
      

      return true;
    }
  }
  






  public void removeTemporarySources()
  {
    synchronized (SoundSystemConfig.THREAD_SYNC)
    {
      if (soundLibrary != null) {
        soundLibrary.removeTemporarySources();
      }
    }
  }
  




  public boolean playing(String sourcename)
  {
    synchronized (SoundSystemConfig.THREAD_SYNC)
    {
      if (soundLibrary == null) {
        return false;
      }
      Source src = (Source)soundLibrary.getSources().get(sourcename);
      
      if (src == null) {
        return false;
      }
      return src.playing();
    }
  }
  




  public boolean playing()
  {
    synchronized (SoundSystemConfig.THREAD_SYNC)
    {
      if (soundLibrary == null) {
        return false;
      }
      HashMap<String, Source> sourceMap = soundLibrary.getSources();
      if (sourceMap == null) {
        return false;
      }
      Set<String> keys = sourceMap.keySet();
      Iterator<String> iter = keys.iterator();
      


      while (iter.hasNext())
      {
        String sourcename = (String)iter.next();
        Source source = (Source)sourceMap.get(sourcename);
        if ((source != null) && 
          (source.playing())) {
          return true;
        }
      }
      return false;
    }
  }
  








  private HashMap<String, Source> copySources(HashMap<String, Source> sourceMap)
  {
    Set<String> keys = sourceMap.keySet();
    Iterator<String> iter = keys.iterator();
    



    HashMap<String, Source> returnMap = new HashMap();
    


    while (iter.hasNext())
    {
      String sourcename = (String)iter.next();
      Source source = (Source)sourceMap.get(sourcename);
      if (source != null)
        returnMap.put(sourcename, new Source(source, null));
    }
    return returnMap;
  }
  






  public static boolean libraryCompatible(Class libraryClass)
  {
    SoundSystemLogger logger = SoundSystemConfig.getLogger();
    
    if (logger == null)
    {
      logger = new SoundSystemLogger();
      SoundSystemConfig.setLogger(logger);
    }
    logger.message("", 0);
    logger.message("Checking if " + SoundSystemConfig.getLibraryTitle(libraryClass) + " is compatible...", 0);
    


    boolean comp = SoundSystemConfig.libraryCompatible(libraryClass);
    
    if (comp) {
      logger.message("...yes", 1);
    } else {
      logger.message("...no", 1);
    }
    return comp;
  }
  




  public static Class currentLibrary()
  {
    return currentLibrary(false, null);
  }
  




  public static boolean initialized()
  {
    return initialized(false, false);
  }
  




  public static SoundSystemException getLastException()
  {
    return lastException(false, null);
  }
  





  public static void setException(SoundSystemException e)
  {
    lastException(true, e);
  }
  






  private static boolean initialized(boolean action, boolean value)
  {
    synchronized (SoundSystemConfig.THREAD_SYNC)
    {
      if (action == true)
        initialized = value;
      return initialized;
    }
  }
  







  private static Class currentLibrary(boolean action, Class value)
  {
    synchronized (SoundSystemConfig.THREAD_SYNC)
    {
      if (action == true)
        currentLibrary = value;
      return currentLibrary;
    }
  }
  








  private static SoundSystemException lastException(boolean action, SoundSystemException e)
  {
    synchronized (SoundSystemConfig.THREAD_SYNC)
    {
      if (action == true)
        lastException = e;
      return lastException;
    }
  }
  



  protected static void snooze(long milliseconds)
  {
    try
    {
      Thread.sleep(milliseconds);
    }
    catch (InterruptedException e) {}
  }
  





  protected void message(String message, int indent)
  {
    logger.message(message, indent);
  }
  





  protected void importantMessage(String message, int indent)
  {
    logger.importantMessage(message, indent);
  }
  







  protected boolean errorCheck(boolean error, String message, int indent)
  {
    return logger.errorCheck(error, className, message, indent);
  }
  





  protected void errorMessage(String message, int indent)
  {
    logger.errorMessage(className, message, indent);
  }
}
