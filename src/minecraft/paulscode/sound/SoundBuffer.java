package paulscode.sound;

import javax.sound.sampled.AudioFormat;

















































public class SoundBuffer
{
  public byte[] audioData;
  public AudioFormat audioFormat;
  
  public SoundBuffer(byte[] audioData, AudioFormat audioFormat)
  {
    this.audioData = audioData;
    this.audioFormat = audioFormat;
  }
  



  public void cleanup()
  {
    audioData = null;
    audioFormat = null;
  }
  






  public void trimData(int maxLength)
  {
    if ((audioData == null) || (maxLength == 0)) {
      audioData = null;
    } else if (audioData.length > maxLength)
    {
      byte[] trimmedArray = new byte[maxLength];
      System.arraycopy(audioData, 0, trimmedArray, 0, maxLength);
      
      audioData = trimmedArray;
    }
  }
}
