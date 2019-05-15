package tv.twitch.chat;

import java.util.HashSet;

public class ChatTokenizedMessage
{
  public String displayName;
  public HashSet<ChatUserMode> modes = new HashSet();
  public HashSet<ChatUserSubscription> subscriptions = new HashSet();
  public int nameColorARGB;
  public ChatMessageToken[] tokenList;
  public boolean action;
  
  public ChatTokenizedMessage() {}
}
