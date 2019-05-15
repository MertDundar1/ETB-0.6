package paulscode.sound;

import java.net.URL;













































public class FilenameURL
{
  private SoundSystemLogger logger;
  private String filename = null;
  



  private URL url = null;
  








  public FilenameURL(URL url, String identifier)
  {
    logger = SoundSystemConfig.getLogger();
    
    filename = identifier;
    this.url = url;
  }
  








  public FilenameURL(String filename)
  {
    logger = SoundSystemConfig.getLogger();
    
    this.filename = filename;
    url = null;
  }
  




  public String getFilename()
  {
    return filename;
  }
  






  public URL getURL()
  {
    if (url == null)
    {

      if (filename.matches(SoundSystemConfig.PREFIX_URL))
      {
        try
        {

          url = new URL(filename);
        }
        catch (Exception e)
        {
          errorMessage("Unable to access online URL in method 'getURL'");
          
          printStackTrace(e);
          return null;
        }
        
      }
      else
      {
        url = getClass().getClassLoader().getResource(SoundSystemConfig.getSoundFilesPackage() + filename);
      }
    }
    
    return url;
  }
  




  private void errorMessage(String message)
  {
    logger.errorMessage("MidiChannel", message, 0);
  }
  




  private void printStackTrace(Exception e)
  {
    logger.printStackTrace(e, 1);
  }
}
