package com.mojang.realmsclient.util;

public abstract class Option<A> { public Option() {}
  
  public abstract A get();
  
  public static <A> Some<A> some(A a) { return new Some(a); }
  public static <A> None<A> none() { return new None(); }
  

  public static final class Some<A>
    extends Option<A>
  {
    private final A a;
    
    public Some(A a)
    {
      this.a = a;
    }
    
    public A get()
    {
      return a;
    }
    
    public static <A> Option<A> of(A value) {
      return new Some(value);
    }
  }
  
  public static final class None<A>
    extends Option<A>
  {
    public None() {}
    
    public A get()
    {
      throw new RuntimeException("None has no value");
    }
  }
}
