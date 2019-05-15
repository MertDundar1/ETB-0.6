package com.jagrosh.discordipc.entities;

import java.util.function.Consumer;



























public class Callback
{
  private final Runnable success;
  private final Consumer<String> failure;
  
  public Callback()
  {
    this(null, null);
  }
  







  public Callback(Runnable success)
  {
    this(success, null);
  }
  







  public Callback(Consumer<String> failure)
  {
    this(null, failure);
  }
  








  public Callback(Runnable success, Consumer<String> failure)
  {
    this.success = success;
    this.failure = failure;
  }
  










  public boolean isEmpty()
  {
    return (success == null) && (failure == null);
  }
  



  public void succeed()
  {
    if (success != null) {
      success.run();
    }
  }
  





  public void fail(String message)
  {
    if (failure != null) {
      failure.accept(message);
    }
  }
}
