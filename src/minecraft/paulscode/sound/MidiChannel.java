package paulscode.sound;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.ListIterator;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Transmitter;










































































public class MidiChannel
  implements MetaEventListener
{
  private SoundSystemLogger logger;
  private FilenameURL filenameURL;
  private String sourcename;
  private static final int CHANGE_VOLUME = 7;
  private static final int END_OF_TRACK = 47;
  private static final boolean GET = false;
  private static final boolean SET = true;
  private static final boolean XXX = false;
  private Sequencer sequencer = null;
  



  private Synthesizer synthesizer = null;
  



  private MidiDevice synthDevice = null;
  



  private Sequence sequence = null;
  



  private boolean toLoop = true;
  



  private float gain = 1.0F;
  



  private boolean loading = true;
  



  private LinkedList<FilenameURL> sequenceQueue = null;
  



  private final Object sequenceQueueLock = new Object();
  




  protected float fadeOutGain = -1.0F;
  




  protected float fadeInGain = 1.0F;
  



  protected long fadeOutMilis = 0L;
  



  protected long fadeInMilis = 0L;
  



  protected long lastFadeCheck = 0L;
  



  private FadeThread fadeThread = null;
  







  public MidiChannel(boolean toLoop, String sourcename, String filename)
  {
    loading(true, true);
    

    logger = SoundSystemConfig.getLogger();
    

    filenameURL(true, new FilenameURL(filename));
    sourcename(true, sourcename);
    setLooping(toLoop);
    

    init();
    

    loading(true, false);
  }
  











  public MidiChannel(boolean toLoop, String sourcename, URL midiFile, String identifier)
  {
    loading(true, true);
    

    logger = SoundSystemConfig.getLogger();
    

    filenameURL(true, new FilenameURL(midiFile, identifier));
    sourcename(true, sourcename);
    setLooping(toLoop);
    

    init();
    

    loading(true, false);
  }
  








  public MidiChannel(boolean toLoop, String sourcename, FilenameURL midiFilenameURL)
  {
    loading(true, true);
    

    logger = SoundSystemConfig.getLogger();
    

    filenameURL(true, midiFilenameURL);
    sourcename(true, sourcename);
    setLooping(toLoop);
    

    init();
    

    loading(true, false);
  }
  




  private void init()
  {
    getSequencer();
    

    setSequence(filenameURL(false, null).getURL());
    

    getSynthesizer();
    


    resetGain();
  }
  



  public void cleanup()
  {
    loading(true, true);
    setLooping(true);
    
    if (sequencer != null)
    {
      try
      {
        sequencer.stop();
        sequencer.close();
        sequencer.removeMetaEventListener(this);
      }
      catch (Exception e) {}
    }
    

    logger = null;
    sequencer = null;
    synthesizer = null;
    sequence = null;
    
    synchronized (sequenceQueueLock)
    {
      if (sequenceQueue != null)
        sequenceQueue.clear();
      sequenceQueue = null;
    }
    

    if (fadeThread != null)
    {
      boolean killException = false;
      try
      {
        fadeThread.kill();
        fadeThread.interrupt();
      }
      catch (Exception e)
      {
        killException = true;
      }
      
      if (!killException)
      {

        for (int i = 0; i < 50; i++)
        {
          if (!fadeThread.alive())
            break;
          try { Thread.sleep(100L);
          }
          catch (InterruptedException e) {}
        }
      }
      if ((killException) || (fadeThread.alive()))
      {
        errorMessage("MIDI fade effects thread did not die!");
        message("Ignoring errors... continuing clean-up.");
      }
    }
    
    fadeThread = null;
    
    loading(true, false);
  }
  




  public void queueSound(FilenameURL filenameURL)
  {
    if (filenameURL == null)
    {
      errorMessage("Filename/URL not specified in method 'queueSound'");
      return;
    }
    
    synchronized (sequenceQueueLock)
    {
      if (sequenceQueue == null)
        sequenceQueue = new LinkedList();
      sequenceQueue.add(filenameURL);
    }
  }
  






  public void dequeueSound(String filename)
  {
    if ((filename == null) || (filename.equals("")))
    {
      errorMessage("Filename not specified in method 'dequeueSound'");
      return;
    }
    
    synchronized (sequenceQueueLock)
    {
      if (sequenceQueue != null)
      {
        ListIterator<FilenameURL> i = sequenceQueue.listIterator();
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
    if (milis < 0L)
    {
      errorMessage("Miliseconds may not be negative in method 'fadeOut'.");
      
      return;
    }
    
    fadeOutMilis = milis;
    fadeInMilis = 0L;
    fadeOutGain = 1.0F;
    lastFadeCheck = System.currentTimeMillis();
    
    synchronized (sequenceQueueLock)
    {
      if (sequenceQueue != null) {
        sequenceQueue.clear();
      }
      if (filenameURL != null)
      {
        if (sequenceQueue == null)
          sequenceQueue = new LinkedList();
        sequenceQueue.add(filenameURL);
      }
    }
    if (fadeThread == null)
    {
      fadeThread = new FadeThread(null);
      fadeThread.start();
    }
    fadeThread.interrupt();
  }
  













  public void fadeOutIn(FilenameURL filenameURL, long milisOut, long milisIn)
  {
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
    
    synchronized (sequenceQueueLock)
    {
      if (sequenceQueue == null)
        sequenceQueue = new LinkedList();
      sequenceQueue.clear();
      sequenceQueue.add(filenameURL);
    }
    if (fadeThread == null)
    {
      fadeThread = new FadeThread(null);
      fadeThread.start();
    }
    fadeThread.interrupt();
  }
  







  private synchronized boolean checkFadeOut()
  {
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
        fadeOutGain = 0.0F;
        fadeInGain = 0.0F;
        if (!incrementSequence())
          stop();
        rewind();
        resetGain();
        return false;
      }
      

      float fadeOutReduction = (float)milisPast / (float)fadeOutMilis;
      
      fadeOutGain -= fadeOutReduction;
      if (fadeOutGain <= 0.0F)
      {
        fadeOutGain = -1.0F;
        fadeInGain = 0.0F;
        if (!incrementSequence())
          stop();
        rewind();
        resetGain();
        return false;
      }
      
      resetGain();
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
      resetGain();
    }
    
    return false;
  }
  




  private boolean incrementSequence()
  {
    synchronized (sequenceQueueLock)
    {

      if ((sequenceQueue != null) && (sequenceQueue.size() > 0))
      {

        filenameURL(true, (FilenameURL)sequenceQueue.remove(0));
        

        loading(true, true);
        

        if (sequencer == null)
        {

          getSequencer();

        }
        else
        {
          sequencer.stop();
          
          sequencer.setMicrosecondPosition(0L);
          
          sequencer.removeMetaEventListener(this);
          try {
            Thread.sleep(100L);
          } catch (InterruptedException e) {}
        }
        if (sequencer == null)
        {
          errorMessage("Unable to set the sequence in method 'incrementSequence', because there wasn't a sequencer to use.");
          



          loading(true, false);
          

          return false;
        }
        
        setSequence(filenameURL(false, null).getURL());
        
        sequencer.start();
        

        resetGain();
        
        sequencer.addMetaEventListener(this);
        

        loading(true, false);
        

        return true;
      }
    }
    

    return false;
  }
  




  public void play()
  {
    if (!loading())
    {

      if (sequencer == null) {
        return;
      }
      
      try
      {
        sequencer.start();
        
        sequencer.addMetaEventListener(this);
      }
      catch (Exception e)
      {
        errorMessage("Exception in method 'play'");
        printStackTrace(e);
        SoundSystemException sse = new SoundSystemException(e.getMessage());
        
        SoundSystem.setException(sse);
      }
    }
  }
  



  public void stop()
  {
    if (!loading())
    {

      if (sequencer == null) {
        return;
      }
      
      try
      {
        sequencer.stop();
        
        sequencer.setMicrosecondPosition(0L);
        
        sequencer.removeMetaEventListener(this);
      }
      catch (Exception e)
      {
        errorMessage("Exception in method 'stop'");
        printStackTrace(e);
        SoundSystemException sse = new SoundSystemException(e.getMessage());
        
        SoundSystem.setException(sse);
      }
    }
  }
  



  public void pause()
  {
    if (!loading())
    {

      if (sequencer == null) {
        return;
      }
      
      try
      {
        sequencer.stop();
      }
      catch (Exception e)
      {
        errorMessage("Exception in method 'pause'");
        printStackTrace(e);
        SoundSystemException sse = new SoundSystemException(e.getMessage());
        
        SoundSystem.setException(sse);
      }
    }
  }
  



  public void rewind()
  {
    if (!loading())
    {

      if (sequencer == null) {
        return;
      }
      
      try
      {
        sequencer.setMicrosecondPosition(0L);
      }
      catch (Exception e)
      {
        errorMessage("Exception in method 'rewind'");
        printStackTrace(e);
        SoundSystemException sse = new SoundSystemException(e.getMessage());
        
        SoundSystem.setException(sse);
      }
    }
  }
  




  public void setVolume(float value)
  {
    gain = value;
    resetGain();
  }
  




  public float getVolume()
  {
    return gain;
  }
  










  public void switchSource(boolean toLoop, String sourcename, String filename)
  {
    loading(true, true);
    

    filenameURL(true, new FilenameURL(filename));
    sourcename(true, sourcename);
    setLooping(toLoop);
    
    reset();
    

    loading(true, false);
  }
  













  public void switchSource(boolean toLoop, String sourcename, URL midiFile, String identifier)
  {
    loading(true, true);
    

    filenameURL(true, new FilenameURL(midiFile, identifier));
    sourcename(true, sourcename);
    setLooping(toLoop);
    
    reset();
    

    loading(true, false);
  }
  










  public void switchSource(boolean toLoop, String sourcename, FilenameURL filenameURL)
  {
    loading(true, true);
    

    filenameURL(true, filenameURL);
    sourcename(true, sourcename);
    setLooping(toLoop);
    
    reset();
    

    loading(true, false);
  }
  



  private void reset()
  {
    synchronized (sequenceQueueLock)
    {
      if (sequenceQueue != null) {
        sequenceQueue.clear();
      }
    }
    
    if (sequencer == null)
    {

      getSequencer();

    }
    else
    {
      sequencer.stop();
      
      sequencer.setMicrosecondPosition(0L);
      
      sequencer.removeMetaEventListener(this);
      try {
        Thread.sleep(100L);
      } catch (InterruptedException e) {}
    }
    if (sequencer == null)
    {
      errorMessage("Unable to set the sequence in method 'reset', because there wasn't a sequencer to use.");
      

      return;
    }
    

    setSequence(filenameURL(false, null).getURL());
    
    sequencer.start();
    

    resetGain();
    
    sequencer.addMetaEventListener(this);
  }
  




  public void setLooping(boolean value)
  {
    toLoop(true, value);
  }
  




  public boolean getLooping()
  {
    return toLoop(false, false);
  }
  






  private synchronized boolean toLoop(boolean action, boolean value)
  {
    if (action == true)
      toLoop = value;
    return toLoop;
  }
  



  public boolean loading()
  {
    return loading(false, false);
  }
  






  private synchronized boolean loading(boolean action, boolean value)
  {
    if (action == true)
      loading = value;
    return loading;
  }
  




  public void setSourcename(String value)
  {
    sourcename(true, value);
  }
  




  public String getSourcename()
  {
    return sourcename(false, null);
  }
  






  private synchronized String sourcename(boolean action, String value)
  {
    if (action == true)
      sourcename = value;
    return sourcename;
  }
  




  public void setFilenameURL(FilenameURL value)
  {
    filenameURL(true, value);
  }
  




  public String getFilename()
  {
    return filenameURL(false, null).getFilename();
  }
  




  public FilenameURL getFilenameURL()
  {
    return filenameURL(false, null);
  }
  







  private synchronized FilenameURL filenameURL(boolean action, FilenameURL value)
  {
    if (action == true)
      filenameURL = value;
    return filenameURL;
  }
  




  public void meta(MetaMessage message)
  {
    if (message.getType() == 47)
    {

      SoundSystemConfig.notifyEOS(sourcename, sequenceQueue.size());
      

      if (toLoop)
      {


        if (!checkFadeOut())
        {


          if (!incrementSequence())
          {
            try
            {

              sequencer.setMicrosecondPosition(0L);
              sequencer.start();
              
              resetGain();
            }
            catch (Exception e) {}
          }
        }
        else if (sequencer != null)
        {
          try
          {

            sequencer.setMicrosecondPosition(0L);
            sequencer.start();
            
            resetGain();

          }
          catch (Exception e) {}
        }
        

      }
      else if (!checkFadeOut())
      {
        if (!incrementSequence())
        {
          try
          {

            sequencer.stop();
            
            sequencer.setMicrosecondPosition(0L);
            
            sequencer.removeMetaEventListener(this);

          }
          catch (Exception e) {}
        }
        
      }
      else {
        try
        {
          sequencer.stop();
          
          sequencer.setMicrosecondPosition(0L);
          
          sequencer.removeMetaEventListener(this);
        }
        catch (Exception e) {}
      }
    }
  }
  





  public void resetGain()
  {
    if (gain < 0.0F)
      gain = 0.0F;
    if (gain > 1.0F) {
      gain = 1.0F;
    }
    int midiVolume = (int)(gain * SoundSystemConfig.getMasterGain() * Math.abs(fadeOutGain) * fadeInGain * 127.0F);
    

    if (synthesizer != null)
    {
      javax.sound.midi.MidiChannel[] channels = synthesizer.getChannels();
      for (int c = 0; (channels != null) && (c < channels.length); c++)
      {
        channels[c].controlChange(7, midiVolume);
      }
    }
    else if (synthDevice != null)
    {
      try
      {
        ShortMessage volumeMessage = new ShortMessage();
        for (int i = 0; i < 16; i++)
        {
          volumeMessage.setMessage(176, i, 7, midiVolume);
          
          synthDevice.getReceiver().send(volumeMessage, -1L);
        }
      }
      catch (Exception e)
      {
        errorMessage("Error resetting gain on MIDI device");
        printStackTrace(e);
      }
    }
    else if ((sequencer != null) && ((sequencer instanceof Synthesizer)))
    {
      synthesizer = ((Synthesizer)sequencer);
      javax.sound.midi.MidiChannel[] channels = synthesizer.getChannels();
      for (int c = 0; (channels != null) && (c < channels.length); c++)
      {
        channels[c].controlChange(7, midiVolume);
      }
    }
    else
    {
      try
      {
        Receiver receiver = MidiSystem.getReceiver();
        ShortMessage volumeMessage = new ShortMessage();
        for (int c = 0; c < 16; c++)
        {
          volumeMessage.setMessage(176, c, 7, midiVolume);
          
          receiver.send(volumeMessage, -1L);
        }
      }
      catch (Exception e)
      {
        errorMessage("Error resetting gain on default receiver");
        printStackTrace(e);
      }
    }
  }
  





  private void getSequencer()
  {
    try
    {
      sequencer = MidiSystem.getSequencer();
      if (sequencer != null)
      {
        try
        {
          sequencer.getTransmitter();
        }
        catch (MidiUnavailableException mue)
        {
          message("Unable to get a transmitter from the default MIDI sequencer");
        }
        
        sequencer.open();
      }
    }
    catch (MidiUnavailableException mue)
    {
      message("Unable to open the default MIDI sequencer");
      sequencer = null;
    }
    catch (Exception e)
    {
      if ((e instanceof InterruptedException))
      {
        message("Caught InterruptedException while attempting to open the default MIDI sequencer.  Trying again.");
        
        sequencer = null;
      }
      try
      {
        sequencer = MidiSystem.getSequencer();
        if (sequencer != null)
        {
          try
          {
            sequencer.getTransmitter();
          }
          catch (MidiUnavailableException mue)
          {
            message("Unable to get a transmitter from the default MIDI sequencer");
          }
          
          sequencer.open();
        }
      }
      catch (MidiUnavailableException mue)
      {
        message("Unable to open the default MIDI sequencer");
        sequencer = null;
      }
      catch (Exception e2)
      {
        message("Unknown error opening the default MIDI sequencer");
        sequencer = null;
      }
    }
    
    if (sequencer == null)
      sequencer = openSequencer("Real Time Sequencer");
    if (sequencer == null)
      sequencer = openSequencer("Java Sound Sequencer");
    if (sequencer == null)
    {
      errorMessage("Failed to find an available MIDI sequencer");
      return;
    }
  }
  






  private void setSequence(URL midiSource)
  {
    if (sequencer == null)
    {
      errorMessage("Unable to update the sequence in method 'setSequence', because variable 'sequencer' is null");
      

      return;
    }
    
    if (midiSource == null)
    {
      errorMessage("Unable to load Midi file in method 'setSequence'.");
      return;
    }
    
    try
    {
      sequence = MidiSystem.getSequence(midiSource);
    }
    catch (IOException ioe)
    {
      errorMessage("Input failed while reading from MIDI file in method 'setSequence'.");
      
      printStackTrace(ioe);
      return;
    }
    catch (InvalidMidiDataException imde)
    {
      errorMessage("Invalid MIDI data encountered, or not a MIDI file in method 'setSequence' (1).");
      
      printStackTrace(imde);
      return;
    }
    if (sequence == null)
    {
      errorMessage("MidiSystem 'getSequence' method returned null in method 'setSequence'.");
    }
    else
    {
      try
      {

        sequencer.setSequence(sequence);
      }
      catch (InvalidMidiDataException imde)
      {
        errorMessage("Invalid MIDI data encountered, or not a MIDI file in method 'setSequence' (2).");
        
        printStackTrace(imde);
        return;
      }
      catch (Exception e)
      {
        errorMessage("Problem setting sequence from MIDI file in method 'setSequence'.");
        
        printStackTrace(e);
        return;
      }
    }
  }
  






  private void getSynthesizer()
  {
    if (sequencer == null)
    {
      errorMessage("Unable to load a Synthesizer in method 'getSynthesizer', because variable 'sequencer' is null");
      

      return;
    }
    

    String overrideMIDISynthesizer = SoundSystemConfig.getOverrideMIDISynthesizer();
    
    if ((overrideMIDISynthesizer != null) && (!overrideMIDISynthesizer.equals("")))
    {


      synthDevice = openMidiDevice(overrideMIDISynthesizer);
      
      if (synthDevice != null)
      {
        try
        {

          sequencer.getTransmitter().setReceiver(synthDevice.getReceiver());
          

          return;

        }
        catch (MidiUnavailableException mue)
        {
          errorMessage("Unable to link sequencer transmitter with receiver for MIDI device '" + overrideMIDISynthesizer + "'");
        }
      }
    }
    





    if ((sequencer instanceof Synthesizer))
    {
      synthesizer = ((Synthesizer)sequencer);

    }
    else
    {
      try
      {
        synthesizer = MidiSystem.getSynthesizer();
        synthesizer.open();
      }
      catch (MidiUnavailableException mue)
      {
        message("Unable to open the default synthesizer");
        synthesizer = null;
      }
      

      if (synthesizer == null)
      {

        synthDevice = openMidiDevice("Java Sound Synthesizer");
        if (synthDevice == null)
          synthDevice = openMidiDevice("Microsoft GS Wavetable");
        if (synthDevice == null)
          synthDevice = openMidiDevice("Gervill");
        if (synthDevice == null)
        {

          errorMessage("Failed to find an available MIDI synthesizer");
          
          return;
        }
      }
      

      if (synthesizer == null)
      {
        try
        {

          sequencer.getTransmitter().setReceiver(synthDevice.getReceiver());

        }
        catch (MidiUnavailableException mue)
        {
          errorMessage("Unable to link sequencer transmitter with MIDI device receiver");


        }
        

      }
      else if (synthesizer.getDefaultSoundbank() == null)
      {
        try
        {

          sequencer.getTransmitter().setReceiver(MidiSystem.getReceiver());

        }
        catch (MidiUnavailableException mue)
        {
          errorMessage("Unable to link sequencer transmitter with default receiver");
        }
        
      }
      else
      {
        try
        {

          sequencer.getTransmitter().setReceiver(synthesizer.getReceiver());

        }
        catch (MidiUnavailableException mue)
        {
          errorMessage("Unable to link sequencer transmitter with synthesizer receiver");
        }
      }
    }
  }
  








  private Sequencer openSequencer(String containsString)
  {
    Sequencer s = null;
    s = (Sequencer)openMidiDevice(containsString);
    if (s == null) {
      return null;
    }
    try {
      s.getTransmitter();
    }
    catch (MidiUnavailableException mue)
    {
      message("    Unable to get a transmitter from this sequencer");
      s = null;
      return null;
    }
    
    return s;
  }
  






  private MidiDevice openMidiDevice(String containsString)
  {
    message("Searching for MIDI device with name containing '" + containsString + "'");
    
    MidiDevice device = null;
    MidiDevice.Info[] midiDevices = MidiSystem.getMidiDeviceInfo();
    for (int i = 0; i < midiDevices.length; i++)
    {
      device = null;
      try
      {
        device = MidiSystem.getMidiDevice(midiDevices[i]);
      }
      catch (MidiUnavailableException e)
      {
        message("    Problem in method 'getMidiDevice':  MIDIUnavailableException was thrown");
        
        device = null;
      }
      if ((device != null) && (midiDevices[i].getName().contains(containsString)))
      {

        message("    Found MIDI device named '" + midiDevices[i].getName() + "'");
        
        if ((device instanceof Synthesizer))
          message("        *this is a Synthesizer instance");
        if ((device instanceof Sequencer)) {
          message("        *this is a Sequencer instance");
        }
        try {
          device.open();
        }
        catch (MidiUnavailableException mue)
        {
          message("    Unable to open this MIDI device");
          device = null;
        }
        return device;
      }
    }
    message("    MIDI device not found");
    return null;
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
    return logger.errorCheck(error, "MidiChannel", message, 0);
  }
  




  protected void errorMessage(String message)
  {
    logger.errorMessage("MidiChannel", message, 0);
  }
  




  protected void printStackTrace(Exception e)
  {
    logger.printStackTrace(e, 1);
  }
  



  private class FadeThread
    extends SimpleThread
  {
    private FadeThread() {}
    



    public void run()
    {
      while (!dying())
      {

        if ((fadeOutGain == -1.0F) && (fadeInGain == 1.0F))
          snooze(3600000L);
        MidiChannel.this.checkFadeOut();
        
        snooze(50L);
      }
      
      cleanup();
    }
  }
}
