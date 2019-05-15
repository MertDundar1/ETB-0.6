package com.ibm.icu.impl;

import com.ibm.icu.text.Normalizer;
import com.ibm.icu.text.Normalizer.QuickCheckResult;
import com.ibm.icu.text.Normalizer2;
import java.io.InputStream;

public final class Norm2AllModes
{
  public final Normalizer2Impl impl;
  public final ComposeNormalizer2 comp;
  public final DecomposeNormalizer2 decomp;
  public final FCDNormalizer2 fcd;
  public final ComposeNormalizer2 fcc;
  
  public static final class NoopNormalizer2 extends Normalizer2
  {
    public NoopNormalizer2() {}
    
    public StringBuilder normalize(CharSequence src, StringBuilder dest)
    {
      if (dest != src) {
        dest.setLength(0);
        return dest.append(src);
      }
      throw new IllegalArgumentException();
    }
    
    public Appendable normalize(CharSequence src, Appendable dest)
    {
      if (dest != src) {
        try {
          return dest.append(src);
        } catch (java.io.IOException e) {
          throw new RuntimeException(e);
        }
      }
      throw new IllegalArgumentException();
    }
    
    public StringBuilder normalizeSecondAndAppend(StringBuilder first, CharSequence second)
    {
      if (first != second) {
        return first.append(second);
      }
      throw new IllegalArgumentException();
    }
    
    public StringBuilder append(StringBuilder first, CharSequence second)
    {
      if (first != second) {
        return first.append(second);
      }
      throw new IllegalArgumentException();
    }
    
    public String getDecomposition(int c)
    {
      return null;
    }
    

    public boolean isNormalized(CharSequence s) { return true; }
    
    public Normalizer.QuickCheckResult quickCheck(CharSequence s) { return Normalizer.YES; }
    
    public int spanQuickCheckYes(CharSequence s) { return s.length(); }
    
    public boolean hasBoundaryBefore(int c) { return true; }
    
    public boolean hasBoundaryAfter(int c) { return true; }
    
    public boolean isInert(int c) { return true; }
  }
  
  public static abstract class Normalizer2WithImpl extends Normalizer2 {
    public final Normalizer2Impl impl;
    
    public Normalizer2WithImpl(Normalizer2Impl ni) {
      impl = ni;
    }
    

    public StringBuilder normalize(CharSequence src, StringBuilder dest)
    {
      if (dest == src) {
        throw new IllegalArgumentException();
      }
      dest.setLength(0);
      normalize(src, new Normalizer2Impl.ReorderingBuffer(impl, dest, src.length()));
      return dest;
    }
    
    public Appendable normalize(CharSequence src, Appendable dest) {
      if (dest == src) {
        throw new IllegalArgumentException();
      }
      Normalizer2Impl.ReorderingBuffer buffer = new Normalizer2Impl.ReorderingBuffer(impl, dest, src.length());
      
      normalize(src, buffer);
      buffer.flush();
      return dest;
    }
    
    protected abstract void normalize(CharSequence paramCharSequence, Normalizer2Impl.ReorderingBuffer paramReorderingBuffer);
    
    public StringBuilder normalizeSecondAndAppend(StringBuilder first, CharSequence second)
    {
      return normalizeSecondAndAppend(first, second, true);
    }
    
    public StringBuilder append(StringBuilder first, CharSequence second) {
      return normalizeSecondAndAppend(first, second, false);
    }
    
    public StringBuilder normalizeSecondAndAppend(StringBuilder first, CharSequence second, boolean doNormalize) {
      if (first == second) {
        throw new IllegalArgumentException();
      }
      normalizeAndAppend(second, doNormalize, new Normalizer2Impl.ReorderingBuffer(impl, first, first.length() + second.length()));
      

      return first;
    }
    
    protected abstract void normalizeAndAppend(CharSequence paramCharSequence, boolean paramBoolean, Normalizer2Impl.ReorderingBuffer paramReorderingBuffer);
    
    public String getDecomposition(int c)
    {
      return impl.getDecomposition(c);
    }
    
    public String getRawDecomposition(int c) {
      return impl.getRawDecomposition(c);
    }
    
    public int composePair(int a, int b) {
      return impl.composePair(a, b);
    }
    
    public int getCombiningClass(int c)
    {
      return impl.getCC(impl.getNorm16(c));
    }
    

    public boolean isNormalized(CharSequence s)
    {
      return s.length() == spanQuickCheckYes(s);
    }
    
    public Normalizer.QuickCheckResult quickCheck(CharSequence s) {
      return isNormalized(s) ? Normalizer.YES : Normalizer.NO;
    }
    
    public int getQuickCheck(int c) {
      return 1;
    }
  }
  
  public static final class DecomposeNormalizer2 extends Norm2AllModes.Normalizer2WithImpl
  {
    public DecomposeNormalizer2(Normalizer2Impl ni)
    {
      super();
    }
    
    protected void normalize(CharSequence src, Normalizer2Impl.ReorderingBuffer buffer)
    {
      impl.decompose(src, 0, src.length(), buffer);
    }
    
    protected void normalizeAndAppend(CharSequence src, boolean doNormalize, Normalizer2Impl.ReorderingBuffer buffer)
    {
      impl.decomposeAndAppend(src, doNormalize, buffer);
    }
    
    public int spanQuickCheckYes(CharSequence s) {
      return impl.decompose(s, 0, s.length(), null);
    }
    
    public int getQuickCheck(int c) {
      return impl.isDecompYes(impl.getNorm16(c)) ? 1 : 0;
    }
    
    public boolean hasBoundaryBefore(int c) { return impl.hasDecompBoundary(c, true); }
    
    public boolean hasBoundaryAfter(int c) { return impl.hasDecompBoundary(c, false); }
    
    public boolean isInert(int c) { return impl.isDecompInert(c); }
  }
  
  public static final class ComposeNormalizer2 extends Norm2AllModes.Normalizer2WithImpl { private final boolean onlyContiguous;
    
    public ComposeNormalizer2(Normalizer2Impl ni, boolean fcc) { super();
      onlyContiguous = fcc;
    }
    
    protected void normalize(CharSequence src, Normalizer2Impl.ReorderingBuffer buffer)
    {
      impl.compose(src, 0, src.length(), onlyContiguous, true, buffer);
    }
    
    protected void normalizeAndAppend(CharSequence src, boolean doNormalize, Normalizer2Impl.ReorderingBuffer buffer)
    {
      impl.composeAndAppend(src, doNormalize, onlyContiguous, buffer);
    }
    

    public boolean isNormalized(CharSequence s)
    {
      return impl.compose(s, 0, s.length(), onlyContiguous, false, new Normalizer2Impl.ReorderingBuffer(impl, new StringBuilder(), 5));
    }
    

    public Normalizer.QuickCheckResult quickCheck(CharSequence s)
    {
      int spanLengthAndMaybe = impl.composeQuickCheck(s, 0, s.length(), onlyContiguous, false);
      if ((spanLengthAndMaybe & 0x1) != 0)
        return Normalizer.MAYBE;
      if (spanLengthAndMaybe >>> 1 == s.length()) {
        return Normalizer.YES;
      }
      return Normalizer.NO;
    }
    
    public int spanQuickCheckYes(CharSequence s)
    {
      return impl.composeQuickCheck(s, 0, s.length(), onlyContiguous, true) >>> 1;
    }
    
    public int getQuickCheck(int c) {
      return impl.getCompQuickCheck(impl.getNorm16(c));
    }
    
    public boolean hasBoundaryBefore(int c) { return impl.hasCompBoundaryBefore(c); }
    
    public boolean hasBoundaryAfter(int c) {
      return impl.hasCompBoundaryAfter(c, onlyContiguous, false);
    }
    
    public boolean isInert(int c) {
      return impl.hasCompBoundaryAfter(c, onlyContiguous, true);
    }
  }
  
  public static final class FCDNormalizer2 extends Norm2AllModes.Normalizer2WithImpl
  {
    public FCDNormalizer2(Normalizer2Impl ni)
    {
      super();
    }
    
    protected void normalize(CharSequence src, Normalizer2Impl.ReorderingBuffer buffer)
    {
      impl.makeFCD(src, 0, src.length(), buffer);
    }
    
    protected void normalizeAndAppend(CharSequence src, boolean doNormalize, Normalizer2Impl.ReorderingBuffer buffer)
    {
      impl.makeFCDAndAppend(src, doNormalize, buffer);
    }
    
    public int spanQuickCheckYes(CharSequence s) {
      return impl.makeFCD(s, 0, s.length(), null);
    }
    
    public int getQuickCheck(int c) {
      return impl.isDecompYes(impl.getNorm16(c)) ? 1 : 0;
    }
    
    public boolean hasBoundaryBefore(int c) { return impl.hasFCDBoundaryBefore(c); }
    
    public boolean hasBoundaryAfter(int c) { return impl.hasFCDBoundaryAfter(c); }
    
    public boolean isInert(int c) { return impl.isFCDInert(c); }
  }
  

  private Norm2AllModes(Normalizer2Impl ni)
  {
    impl = ni;
    comp = new ComposeNormalizer2(ni, false);
    decomp = new DecomposeNormalizer2(ni);
    fcd = new FCDNormalizer2(ni);
    fcc = new ComposeNormalizer2(ni, true);
  }
  





  private static Norm2AllModes getInstanceFromSingleton(Norm2AllModesSingleton singleton)
  {
    if (exception != null) {
      throw exception;
    }
    return allModes;
  }
  
  public static Norm2AllModes getNFCInstance() { return getInstanceFromSingleton(NFCSingleton.INSTANCE); }
  
  public static Norm2AllModes getNFKCInstance() {
    return getInstanceFromSingleton(NFKCSingleton.INSTANCE);
  }
  
  public static Norm2AllModes getNFKC_CFInstance() { return getInstanceFromSingleton(NFKC_CFSingleton.INSTANCE); }
  
  public static Normalizer2WithImpl getN2WithImpl(int index)
  {
    switch (index) {
    case 0:  return getNFCInstancedecomp;
    case 1:  return getNFKCInstancedecomp;
    case 2:  return getNFCInstancecomp;
    case 3:  return getNFKCInstancecomp; }
    return null;
  }
  
  public static Norm2AllModes getInstance(InputStream data, String name) {
    if (data == null) { Norm2AllModesSingleton singleton;
      Norm2AllModesSingleton singleton;
      if (name.equals("nfc")) {
        singleton = NFCSingleton.INSTANCE; } else { Norm2AllModesSingleton singleton;
        if (name.equals("nfkc")) {
          singleton = NFKCSingleton.INSTANCE; } else { Norm2AllModesSingleton singleton;
          if (name.equals("nfkc_cf")) {
            singleton = NFKC_CFSingleton.INSTANCE;
          } else
            singleton = null;
        } }
      if (singleton != null) {
        if (exception != null) {
          throw exception;
        }
        return allModes;
      }
    }
    return (Norm2AllModes)cache.getInstance(name, data); }
  
  private static CacheBase<String, Norm2AllModes, InputStream> cache = new SoftCache() {
    protected Norm2AllModes createInstance(String key, InputStream data) {
      Normalizer2Impl impl;
      Normalizer2Impl impl;
      if (data == null) {
        impl = new Normalizer2Impl().load("data/icudt51b/" + key + ".nrm");
      } else {
        impl = new Normalizer2Impl().load(data);
      }
      return new Norm2AllModes(impl, null);
    }
  };
  
  public static final NoopNormalizer2 NOOP_NORMALIZER2 = new NoopNormalizer2();
  




  public static Normalizer2 getFCDNormalizer2() { return getNFCInstancefcd; }
  
  private static final class Norm2AllModesSingleton {
    private Norm2AllModes allModes;
    private RuntimeException exception;
    
    private Norm2AllModesSingleton(String name) { try { Normalizer2Impl impl = new Normalizer2Impl().load("data/icudt51b/" + name + ".nrm");
        
        allModes = new Norm2AllModes(impl, null);
      } catch (RuntimeException e) {
        exception = e;
      }
    }
  }
  
  private static final class NFCSingleton {
    private NFCSingleton() {}
    
    private static final Norm2AllModes.Norm2AllModesSingleton INSTANCE = new Norm2AllModes.Norm2AllModesSingleton("nfc", null);
    
     }
  private static final class NFKCSingleton { private static final Norm2AllModes.Norm2AllModesSingleton INSTANCE = new Norm2AllModes.Norm2AllModesSingleton("nfkc", null);
    
    private NFKCSingleton() {} }
  private static final class NFKC_CFSingleton { private static final Norm2AllModes.Norm2AllModesSingleton INSTANCE = new Norm2AllModes.Norm2AllModesSingleton("nfkc_cf", null);
    
    private NFKC_CFSingleton() {}
  }
}
