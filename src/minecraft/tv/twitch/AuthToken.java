package tv.twitch;


public class AuthToken
{
  public String data;
  

  public AuthToken() {}
  
  public boolean getIsValid()
  {
    return (data != null) && (data.length() > 0);
  }
}
