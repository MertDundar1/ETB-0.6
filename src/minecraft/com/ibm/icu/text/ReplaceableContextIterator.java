package com.ibm.icu.text;

import com.ibm.icu.impl.UCaseProps.ContextIterator;
















class ReplaceableContextIterator
  implements UCaseProps.ContextIterator
{
  protected Replaceable rep;
  protected int index;
  protected int limit;
  protected int cpStart;
  protected int cpLimit;
  protected int contextStart;
  protected int contextLimit;
  protected int dir;
  protected boolean reachedLimit;
  
  ReplaceableContextIterator()
  {
    rep = null;
    limit = (this.cpStart = this.cpLimit = this.index = this.contextStart = this.contextLimit = 0);
    dir = 0;
    reachedLimit = false;
  }
  



  public void setText(Replaceable rep)
  {
    this.rep = rep;
    limit = (this.contextLimit = rep.length());
    cpStart = (this.cpLimit = this.index = this.contextStart = 0);
    dir = 0;
    reachedLimit = false;
  }
  



  public void setIndex(int index)
  {
    cpStart = (this.cpLimit = index);
    this.index = 0;
    dir = 0;
    reachedLimit = false;
  }
  



  public int getCaseMapCPStart()
  {
    return cpStart;
  }
  






  public void setLimit(int lim)
  {
    if ((0 <= lim) && (lim <= rep.length())) {
      limit = lim;
    } else {
      limit = rep.length();
    }
    reachedLimit = false;
  }
  




  public void setContextLimits(int contextStart, int contextLimit)
  {
    if (contextStart < 0) {
      this.contextStart = 0;
    } else if (contextStart <= rep.length()) {
      this.contextStart = contextStart;
    } else {
      this.contextStart = rep.length();
    }
    if (contextLimit < this.contextStart) {
      this.contextLimit = this.contextStart;
    } else if (contextLimit <= rep.length()) {
      this.contextLimit = contextLimit;
    } else {
      this.contextLimit = rep.length();
    }
    reachedLimit = false;
  }
  






  public int nextCaseMapCP()
  {
    if (cpLimit < limit) {
      cpStart = cpLimit;
      int c = rep.char32At(cpLimit);
      cpLimit += UTF16.getCharCount(c);
      return c;
    }
    return -1;
  }
  







  public int replace(String text)
  {
    int delta = text.length() - (cpLimit - cpStart);
    rep.replace(cpStart, cpLimit, text);
    cpLimit += delta;
    limit += delta;
    contextLimit += delta;
    return delta;
  }
  



  public boolean didReachLimit()
  {
    return reachedLimit;
  }
  
  public void reset(int direction)
  {
    if (direction > 0)
    {
      dir = 1;
      index = cpLimit;
    } else if (direction < 0)
    {
      dir = -1;
      index = cpStart;
    }
    else {
      dir = 0;
      index = 0;
    }
    reachedLimit = false;
  }
  

  public int next()
  {
    if (dir > 0) {
      if (index < contextLimit) {
        int c = rep.char32At(index);
        index += UTF16.getCharCount(c);
        return c;
      }
      
      reachedLimit = true;
    }
    else if ((dir < 0) && (index > contextStart)) {
      int c = rep.char32At(index - 1);
      index -= UTF16.getCharCount(c);
      return c;
    }
    return -1;
  }
}
