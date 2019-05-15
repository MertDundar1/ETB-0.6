package tv.twitch.chat;

import java.util.HashSet;




public class ChatRawMessage
{
  public String userName = null;
  public String message = null;
  public HashSet<ChatUserMode> modes = new HashSet();
  public HashSet<ChatUserSubscription> subscriptions = new HashSet();
  public int nameColorARGB = 0;
  public boolean action = false;
  
  public ChatRawMessage() {}
}
