package com.enjoytheban.api.events.misc;

import com.enjoytheban.api.Event;






public class EventKey
  extends Event
{
  private int key;
  
  public EventKey(int key)
  {
    this.key = key;
  }
  
  public int getKey()
  {
    return key;
  }
  
  public void setKey(int key)
  {
    this.key = key;
  }
}
