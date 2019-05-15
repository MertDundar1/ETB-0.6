package paulscode.sound;




















public class CommandThread
  extends SimpleThread
{
  protected SoundSystemLogger logger;
  


















  private SoundSystem soundSystem;
  


















  protected String className = "CommandThread";
  





  public CommandThread(SoundSystem s)
  {
    logger = SoundSystemConfig.getLogger();
    
    soundSystem = s;
  }
  





  protected void cleanup()
  {
    kill();
    
    logger = null;
    soundSystem = null;
    
    super.cleanup();
  }
  






  public void run()
  {
    long previousTime = System.currentTimeMillis();
    long currentTime = previousTime;
    
    if (soundSystem == null)
    {
      errorMessage("SoundSystem was null in method run().", 0);
      cleanup();
      return;
    }
    

    snooze(3600000L);
    
    while (!dying())
    {

      soundSystem.ManageSources();
      

      soundSystem.CommandQueue(null);
      

      currentTime = System.currentTimeMillis();
      if ((!dying()) && (currentTime - previousTime > 10000L))
      {
        previousTime = currentTime;
        soundSystem.removeTemporarySources();
      }
      

      if (!dying()) {
        snooze(3600000L);
      }
    }
    cleanup();
  }
  




  protected void message(String message, int indent)
  {
    logger.message(message, indent);
  }
  




  protected void importantMessage(String message, int indent)
  {
    logger.importantMessage(message, indent);
  }
  






  protected boolean errorCheck(boolean error, String message)
  {
    return logger.errorCheck(error, className, message, 0);
  }
  




  protected void errorMessage(String message, int indent)
  {
    logger.errorMessage(className, message, indent);
  }
}
