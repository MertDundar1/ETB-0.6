package com.ibm.icu.text;

import com.ibm.icu.impl.Norm2AllModes;
import java.io.InputStream;
































































public abstract class Normalizer2
{
  public static enum Mode
  {
    COMPOSE, 
    







    DECOMPOSE, 
    











    FCD, 
    








    COMPOSE_CONTIGUOUS;
    


    private Mode() {}
  }
  


  public static Normalizer2 getNFCInstance()
  {
    return getNFCInstancecomp;
  }
  






  public static Normalizer2 getNFDInstance()
  {
    return getNFCInstancedecomp;
  }
  






  public static Normalizer2 getNFKCInstance()
  {
    return getNFKCInstancecomp;
  }
  






  public static Normalizer2 getNFKDInstance()
  {
    return getNFKCInstancedecomp;
  }
  






  public static Normalizer2 getNFKCCasefoldInstance()
  {
    return getNFKC_CFInstancecomp;
  }
  




















  public static Normalizer2 getInstance(InputStream data, String name, Mode mode)
  {
    Norm2AllModes all2Modes = Norm2AllModes.getInstance(data, name);
    switch (1.$SwitchMap$com$ibm$icu$text$Normalizer2$Mode[mode.ordinal()]) {
    case 1:  return comp;
    case 2:  return decomp;
    case 3:  return fcd;
    case 4:  return fcc; }
    return null;
  }
  






  public String normalize(CharSequence src)
  {
    if ((src instanceof String))
    {

      int spanLength = spanQuickCheckYes(src);
      if (spanLength == src.length()) {
        return (String)src;
      }
      StringBuilder sb = new StringBuilder(src.length()).append(src, 0, spanLength);
      return normalizeSecondAndAppend(sb, src.subSequence(spanLength, src.length())).toString();
    }
    return normalize(src, new StringBuilder(src.length())).toString();
  }
  












  public abstract StringBuilder normalize(CharSequence paramCharSequence, StringBuilder paramStringBuilder);
  












  public abstract Appendable normalize(CharSequence paramCharSequence, Appendable paramAppendable);
  












  public abstract StringBuilder normalizeSecondAndAppend(StringBuilder paramStringBuilder, CharSequence paramCharSequence);
  











  public abstract StringBuilder append(StringBuilder paramStringBuilder, CharSequence paramCharSequence);
  











  public abstract String getDecomposition(int paramInt);
  











  public String getRawDecomposition(int c)
  {
    return null;
  }
  












  public int composePair(int a, int b)
  {
    return -1;
  }
  





  public int getCombiningClass(int c)
  {
    return 0;
  }
  
  public abstract boolean isNormalized(CharSequence paramCharSequence);
  
  public abstract Normalizer.QuickCheckResult quickCheck(CharSequence paramCharSequence);
  
  public abstract int spanQuickCheckYes(CharSequence paramCharSequence);
  
  public abstract boolean hasBoundaryBefore(int paramInt);
  
  public abstract boolean hasBoundaryAfter(int paramInt);
  
  public abstract boolean isInert(int paramInt);
  
  /**
   * @deprecated
   */
  protected Normalizer2() {}
}
