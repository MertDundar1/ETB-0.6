package com.ibm.icu.text;

























public class BidiClassifier
{
  protected Object context;
  
























  public BidiClassifier(Object context)
  {
    this.context = context;
  }
  







  public void setContext(Object context)
  {
    this.context = context;
  }
  



  public Object getContext()
  {
    return context;
  }
  












  public int classify(int c)
  {
    return 19;
  }
}
