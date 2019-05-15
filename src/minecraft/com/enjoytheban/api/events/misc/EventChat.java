package com.enjoytheban.api.events.misc;

import com.enjoytheban.api.Event;










public class EventChat
  extends Event
{
  private String message;
  private ChatType type;
  
  public EventChat(ChatType type, String message)
  {
    this.type = type;
    this.message = message;
    setType((byte)0);
  }
  
  public String getMessage()
  {
    return message;
  }
  
  public void setMessage(String message)
  {
    this.message = message;
  }
  
  public ChatType getChatType() {
    return type;
  }
  
  public void setType(ChatType type) {
    this.type = type;
  }
  
  public static enum ChatType {
    Send,  Receive;
  }
}
