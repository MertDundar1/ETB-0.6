package com.ibm.icu.text;

import com.ibm.icu.impl.Norm2AllModes;
import com.ibm.icu.impl.Norm2AllModes.Normalizer2WithImpl;
import com.ibm.icu.impl.Normalizer2Impl;
import com.ibm.icu.impl.Normalizer2Impl.UTF16Plus;
import com.ibm.icu.impl.UCaseProps;
import com.ibm.icu.lang.UCharacter;
import java.nio.CharBuffer;
import java.text.CharacterIterator;





















































































































public final class Normalizer
  implements Cloneable
{
  private UCharacterIterator text;
  private Normalizer2 norm2;
  private Mode mode;
  private int options;
  private int currentIndex;
  private int nextIndex;
  private StringBuilder buffer;
  private int bufferPos;
  public static final int UNICODE_3_2 = 32;
  public static final int DONE = -1;
  
  private static final class ModeImpl
  {
    private final Normalizer2 normalizer2;
    
    private ModeImpl(Normalizer2 n2) { normalizer2 = n2; }
  }
  
  private static final class NFDModeImpl { private NFDModeImpl() {}
    
    private static final Normalizer.ModeImpl INSTANCE = new Normalizer.ModeImpl(getNFCInstancedecomp, null);
    
     }
  private static final class NFKDModeImpl { private NFKDModeImpl() {}
    private static final Normalizer.ModeImpl INSTANCE = new Normalizer.ModeImpl(getNFKCInstancedecomp, null);
    
     }
  private static final class NFCModeImpl { private NFCModeImpl() {}
    private static final Normalizer.ModeImpl INSTANCE = new Normalizer.ModeImpl(getNFCInstancecomp, null);
    
     }
  private static final class NFKCModeImpl { private NFKCModeImpl() {}
    private static final Normalizer.ModeImpl INSTANCE = new Normalizer.ModeImpl(getNFKCInstancecomp, null);
    
     }
  private static final class FCDModeImpl { private FCDModeImpl() {}
    private static final Normalizer.ModeImpl INSTANCE = new Normalizer.ModeImpl(Norm2AllModes.getFCDNormalizer2(), null);
    
     }
  
  private static final class Unicode32 { private Unicode32() {}
    private static final UnicodeSet INSTANCE = new UnicodeSet("[:age=3.2:]").freeze();
    
     }
  private static final class NFD32ModeImpl { private static final Normalizer.ModeImpl INSTANCE = new Normalizer.ModeImpl(new FilteredNormalizer2(getNFCInstancedecomp, Normalizer.Unicode32.access$100()), null);
    
    private NFD32ModeImpl() {}
  }
  
  private static final class NFKD32ModeImpl { private static final Normalizer.ModeImpl INSTANCE = new Normalizer.ModeImpl(new FilteredNormalizer2(getNFKCInstancedecomp, Normalizer.Unicode32.access$100()), null);
    
    private NFKD32ModeImpl() {}
  }
  
  private static final class NFC32ModeImpl { private static final Normalizer.ModeImpl INSTANCE = new Normalizer.ModeImpl(new FilteredNormalizer2(getNFCInstancecomp, Normalizer.Unicode32.access$100()), null);
    
    private NFC32ModeImpl() {}
  }
  
  private static final class NFKC32ModeImpl { private static final Normalizer.ModeImpl INSTANCE = new Normalizer.ModeImpl(new FilteredNormalizer2(getNFKCInstancecomp, Normalizer.Unicode32.access$100()), null);
    
    private NFKC32ModeImpl() {}
  }
  
  private static final class FCD32ModeImpl { private static final Normalizer.ModeImpl INSTANCE = new Normalizer.ModeImpl(new FilteredNormalizer2(Norm2AllModes.getFCDNormalizer2(), Normalizer.Unicode32.access$100()), null);
    




    private FCD32ModeImpl() {}
  }
  




  public static abstract class Mode
  {
    public Mode() {}
    



    /**
     * @deprecated
     */
    protected abstract Normalizer2 getNormalizer2(int paramInt);
  }
  



  private static final class NONEMode
    extends Normalizer.Mode
  {
    private NONEMode() {}
    



    protected Normalizer2 getNormalizer2(int options) { return Norm2AllModes.NOOP_NORMALIZER2; } }
  
  private static final class NFDMode extends Normalizer.Mode { private NFDMode() {}
    
    protected Normalizer2 getNormalizer2(int options) { return (options & 0x20) != 0 ? Normalizer.ModeImpl.access$300(Normalizer.NFD32ModeImpl.access$200()) : Normalizer.ModeImpl.access$300(Normalizer.NFDModeImpl.access$400()); }
  }
  
  private static final class NFKDMode extends Normalizer.Mode {
    private NFKDMode() {}
    
    protected Normalizer2 getNormalizer2(int options) { return (options & 0x20) != 0 ? Normalizer.ModeImpl.access$300(Normalizer.NFKD32ModeImpl.access$500()) : Normalizer.ModeImpl.access$300(Normalizer.NFKDModeImpl.access$600()); }
  }
  
  private static final class NFCMode extends Normalizer.Mode {
    private NFCMode() {}
    
    protected Normalizer2 getNormalizer2(int options) { return (options & 0x20) != 0 ? Normalizer.ModeImpl.access$300(Normalizer.NFC32ModeImpl.access$700()) : Normalizer.ModeImpl.access$300(Normalizer.NFCModeImpl.access$800()); }
  }
  
  private static final class NFKCMode extends Normalizer.Mode {
    private NFKCMode() {}
    
    protected Normalizer2 getNormalizer2(int options) { return (options & 0x20) != 0 ? Normalizer.ModeImpl.access$300(Normalizer.NFKC32ModeImpl.access$900()) : Normalizer.ModeImpl.access$300(Normalizer.NFKCModeImpl.access$1000()); }
  }
  
  private static final class FCDMode extends Normalizer.Mode {
    private FCDMode() {}
    
    protected Normalizer2 getNormalizer2(int options) { return (options & 0x20) != 0 ? Normalizer.ModeImpl.access$300(Normalizer.FCD32ModeImpl.access$1100()) : Normalizer.ModeImpl.access$300(Normalizer.FCDModeImpl.access$1200()); }
  }
  






  public static final Mode NONE = new NONEMode(null);
  




  public static final Mode NFD = new NFDMode(null);
  




  public static final Mode NFKD = new NFKDMode(null);
  




  public static final Mode NFC = new NFCMode(null);
  




  public static final Mode DEFAULT = NFC;
  




  public static final Mode NFKC = new NFKCMode(null);
  




  public static final Mode FCD = new FCDMode(null);
  











  /**
   * @deprecated
   */
  public static final Mode NO_OP = NONE;
  













  /**
   * @deprecated
   */
  public static final Mode COMPOSE = NFC;
  













  /**
   * @deprecated
   */
  public static final Mode COMPOSE_COMPAT = NFKC;
  













  /**
   * @deprecated
   */
  public static final Mode DECOMP = NFD;
  













  /**
   * @deprecated
   */
  public static final Mode DECOMP_COMPAT = NFKD;
  















  /**
   * @deprecated
   */
  public static final int IGNORE_HANGUL = 1;
  















  public static final QuickCheckResult NO = new QuickCheckResult(0, null);
  




  public static final QuickCheckResult YES = new QuickCheckResult(1, null);
  





  public static final QuickCheckResult MAYBE = new QuickCheckResult(2, null);
  








  public static final int FOLD_CASE_DEFAULT = 0;
  








  public static final int INPUT_IS_FCD = 131072;
  








  public static final int COMPARE_IGNORE_CASE = 65536;
  








  public static final int COMPARE_CODE_POINT_ORDER = 32768;
  







  public static final int FOLD_CASE_EXCLUDE_SPECIAL_I = 1;
  







  public static final int COMPARE_NORM_OPTIONS_SHIFT = 20;
  







  private static final int COMPARE_EQUIV = 524288;
  








  public Normalizer(String str, Mode mode, int opt)
  {
    text = UCharacterIterator.getInstance(str);
    this.mode = mode;
    options = opt;
    norm2 = mode.getNormalizer2(opt);
    buffer = new StringBuilder();
  }
  














  public Normalizer(CharacterIterator iter, Mode mode, int opt)
  {
    text = UCharacterIterator.getInstance((CharacterIterator)iter.clone());
    this.mode = mode;
    options = opt;
    norm2 = mode.getNormalizer2(opt);
    buffer = new StringBuilder();
  }
  









  public Normalizer(UCharacterIterator iter, Mode mode, int options)
  {
    try
    {
      text = ((UCharacterIterator)iter.clone());
      this.mode = mode;
      this.options = options;
      norm2 = mode.getNormalizer2(options);
      buffer = new StringBuilder();
    } catch (CloneNotSupportedException e) {
      throw new IllegalStateException(e.toString());
    }
  }
  








  public Object clone()
  {
    try
    {
      Normalizer copy = (Normalizer)super.clone();
      text = ((UCharacterIterator)text.clone());
      mode = mode;
      options = options;
      norm2 = norm2;
      buffer = new StringBuilder(buffer);
      bufferPos = bufferPos;
      currentIndex = currentIndex;
      nextIndex = nextIndex;
      return copy;
    }
    catch (CloneNotSupportedException e) {
      throw new IllegalStateException(e);
    }
  }
  



  private static final Normalizer2 getComposeNormalizer2(boolean compat, int options)
  {
    return (compat ? NFKC : NFC).getNormalizer2(options);
  }
  
  private static final Normalizer2 getDecomposeNormalizer2(boolean compat, int options) { return (compat ? NFKD : NFD).getNormalizer2(options); }
  










  public static String compose(String str, boolean compat)
  {
    return compose(str, compat, 0);
  }
  










  public static String compose(String str, boolean compat, int options)
  {
    return getComposeNormalizer2(compat, options).normalize(str);
  }
  














  public static int compose(char[] source, char[] target, boolean compat, int options)
  {
    return compose(source, 0, source.length, target, 0, target.length, compat, options);
  }
  




















  public static int compose(char[] src, int srcStart, int srcLimit, char[] dest, int destStart, int destLimit, boolean compat, int options)
  {
    CharBuffer srcBuffer = CharBuffer.wrap(src, srcStart, srcLimit - srcStart);
    CharsAppendable app = new CharsAppendable(dest, destStart, destLimit);
    getComposeNormalizer2(compat, options).normalize(srcBuffer, app);
    return app.length();
  }
  









  public static String decompose(String str, boolean compat)
  {
    return decompose(str, compat, 0);
  }
  










  public static String decompose(String str, boolean compat, int options)
  {
    return getDecomposeNormalizer2(compat, options).normalize(str);
  }
  














  public static int decompose(char[] source, char[] target, boolean compat, int options)
  {
    return decompose(source, 0, source.length, target, 0, target.length, compat, options);
  }
  




















  public static int decompose(char[] src, int srcStart, int srcLimit, char[] dest, int destStart, int destLimit, boolean compat, int options)
  {
    CharBuffer srcBuffer = CharBuffer.wrap(src, srcStart, srcLimit - srcStart);
    CharsAppendable app = new CharsAppendable(dest, destStart, destLimit);
    getDecomposeNormalizer2(compat, options).normalize(srcBuffer, app);
    return app.length();
  }
  














  public static String normalize(String str, Mode mode, int options)
  {
    return mode.getNormalizer2(options).normalize(str);
  }
  











  public static String normalize(String src, Mode mode)
  {
    return normalize(src, mode, 0);
  }
  














  public static int normalize(char[] source, char[] target, Mode mode, int options)
  {
    return normalize(source, 0, source.length, target, 0, target.length, mode, options);
  }
  





















  public static int normalize(char[] src, int srcStart, int srcLimit, char[] dest, int destStart, int destLimit, Mode mode, int options)
  {
    CharBuffer srcBuffer = CharBuffer.wrap(src, srcStart, srcLimit - srcStart);
    CharsAppendable app = new CharsAppendable(dest, destStart, destLimit);
    mode.getNormalizer2(options).normalize(srcBuffer, app);
    return app.length();
  }
  









  public static String normalize(int char32, Mode mode, int options)
  {
    if ((mode == NFD) && (options == 0)) {
      String decomposition = getNFCInstanceimpl.getDecomposition(char32);
      
      if (decomposition == null) {
        decomposition = UTF16.valueOf(char32);
      }
      return decomposition;
    }
    return normalize(UTF16.valueOf(char32), mode, options);
  }
  






  public static String normalize(int char32, Mode mode)
  {
    return normalize(char32, mode, 0);
  }
  









  public static QuickCheckResult quickCheck(String source, Mode mode)
  {
    return quickCheck(source, mode, 0);
  }
  



















  public static QuickCheckResult quickCheck(String source, Mode mode, int options)
  {
    return mode.getNormalizer2(options).quickCheck(source);
  }
  












  public static QuickCheckResult quickCheck(char[] source, Mode mode, int options)
  {
    return quickCheck(source, 0, source.length, mode, options);
  }
  
























  public static QuickCheckResult quickCheck(char[] source, int start, int limit, Mode mode, int options)
  {
    CharBuffer srcBuffer = CharBuffer.wrap(source, start, limit - start);
    return mode.getNormalizer2(options).quickCheck(srcBuffer);
  }
  





















  public static boolean isNormalized(char[] src, int start, int limit, Mode mode, int options)
  {
    CharBuffer srcBuffer = CharBuffer.wrap(src, start, limit - start);
    return mode.getNormalizer2(options).isNormalized(srcBuffer);
  }
  
















  public static boolean isNormalized(String str, Mode mode, int options)
  {
    return mode.getNormalizer2(options).isNormalized(str);
  }
  










  public static boolean isNormalized(int char32, Mode mode, int options)
  {
    return isNormalized(UTF16.valueOf(char32), mode, options);
  }
  






















































  public static int compare(char[] s1, int s1Start, int s1Limit, char[] s2, int s2Start, int s2Limit, int options)
  {
    if ((s1 == null) || (s1Start < 0) || (s1Limit < 0) || (s2 == null) || (s2Start < 0) || (s2Limit < 0) || (s1Limit < s1Start) || (s2Limit < s2Start))
    {


      throw new IllegalArgumentException();
    }
    return internalCompare(CharBuffer.wrap(s1, s1Start, s1Limit - s1Start), CharBuffer.wrap(s2, s2Start, s2Limit - s2Start), options);
  }
  
















































  public static int compare(String s1, String s2, int options)
  {
    return internalCompare(s1, s2, options);
  }
  
































  public static int compare(char[] s1, char[] s2, int options)
  {
    return internalCompare(CharBuffer.wrap(s1), CharBuffer.wrap(s2), options);
  }
  







  public static int compare(int char32a, int char32b, int options)
  {
    return internalCompare(UTF16.valueOf(char32a), UTF16.valueOf(char32b), options | 0x20000);
  }
  







  public static int compare(int char32a, String str2, int options)
  {
    return internalCompare(UTF16.valueOf(char32a), str2, options);
  }
  














































  public static int concatenate(char[] left, int leftStart, int leftLimit, char[] right, int rightStart, int rightLimit, char[] dest, int destStart, int destLimit, Mode mode, int options)
  {
    if (dest == null) {
      throw new IllegalArgumentException();
    }
    

    if ((right == dest) && (rightStart < destLimit) && (destStart < rightLimit)) {
      throw new IllegalArgumentException("overlapping right and dst ranges");
    }
    

    StringBuilder destBuilder = new StringBuilder(leftLimit - leftStart + rightLimit - rightStart + 16);
    destBuilder.append(left, leftStart, leftLimit - leftStart);
    CharBuffer rightBuffer = CharBuffer.wrap(right, rightStart, rightLimit - rightStart);
    mode.getNormalizer2(options).append(destBuilder, rightBuffer);
    int destLength = destBuilder.length();
    if (destLength <= destLimit - destStart) {
      destBuilder.getChars(0, destLength, dest, destStart);
      return destLength;
    }
    throw new IndexOutOfBoundsException(Integer.toString(destLength));
  }
  



























  public static String concatenate(char[] left, char[] right, Mode mode, int options)
  {
    StringBuilder dest = new StringBuilder(left.length + right.length + 16).append(left);
    return mode.getNormalizer2(options).append(dest, CharBuffer.wrap(right)).toString();
  }
  






























  public static String concatenate(String left, String right, Mode mode, int options)
  {
    StringBuilder dest = new StringBuilder(left.length() + right.length() + 16).append(left);
    return mode.getNormalizer2(options).append(dest, right).toString();
  }
  






  public static int getFC_NFKC_Closure(int c, char[] dest)
  {
    String closure = getFC_NFKC_Closure(c);
    int length = closure.length();
    if ((length != 0) && (dest != null) && (length <= dest.length)) {
      closure.getChars(0, length, dest, 0);
    }
    return length;
  }
  










  public static String getFC_NFKC_Closure(int c)
  {
    Normalizer2 nfkc = INSTANCEnormalizer2;
    UCaseProps csp = UCaseProps.INSTANCE;
    
    StringBuilder folded = new StringBuilder();
    int folded1Length = csp.toFullFolding(c, folded, 0);
    if (folded1Length < 0) {
      Normalizer2Impl nfkcImpl = impl;
      if (nfkcImpl.getCompQuickCheck(nfkcImpl.getNorm16(c)) != 0) {
        return "";
      }
      folded.appendCodePoint(c);
    }
    else if (folded1Length > 31) {
      folded.appendCodePoint(folded1Length);
    }
    
    String kc1 = nfkc.normalize(folded);
    
    String kc2 = nfkc.normalize(UCharacter.foldCase(kc1, 0));
    
    if (kc1.equals(kc2)) {
      return "";
    }
    return kc2;
  }
  









  public int current()
  {
    if ((bufferPos < buffer.length()) || (nextNormalize())) {
      return buffer.codePointAt(bufferPos);
    }
    return -1;
  }
  







  public int next()
  {
    if ((bufferPos < buffer.length()) || (nextNormalize())) {
      int c = buffer.codePointAt(bufferPos);
      bufferPos += Character.charCount(c);
      return c;
    }
    return -1;
  }
  








  public int previous()
  {
    if ((bufferPos > 0) || (previousNormalize())) {
      int c = buffer.codePointBefore(bufferPos);
      bufferPos -= Character.charCount(c);
      return c;
    }
    return -1;
  }
  





  public void reset()
  {
    text.setToStart();
    currentIndex = (this.nextIndex = 0);
    clearBuffer();
  }
  








  public void setIndexOnly(int index)
  {
    text.setIndex(index);
    currentIndex = (this.nextIndex = index);
    clearBuffer();
  }
  


















  /**
   * @deprecated
   */
  public int setIndex(int index)
  {
    setIndexOnly(index);
    return current();
  }
  




  /**
   * @deprecated
   */
  public int getBeginIndex()
  {
    return 0;
  }
  




  /**
   * @deprecated
   */
  public int getEndIndex()
  {
    return endIndex();
  }
  




  public int first()
  {
    reset();
    return next();
  }
  






  public int last()
  {
    text.setToLimit();
    currentIndex = (this.nextIndex = text.getIndex());
    clearBuffer();
    return previous();
  }
  














  public int getIndex()
  {
    if (bufferPos < buffer.length()) {
      return currentIndex;
    }
    return nextIndex;
  }
  







  public int startIndex()
  {
    return 0;
  }
  






  public int endIndex()
  {
    return text.getLength();
  }
  




























  public void setMode(Mode newMode)
  {
    mode = newMode;
    norm2 = mode.getNormalizer2(options);
  }
  




  public Mode getMode()
  {
    return mode;
  }
  
















  public void setOption(int option, boolean value)
  {
    if (value) {
      options |= option;
    } else {
      options &= (option ^ 0xFFFFFFFF);
    }
    norm2 = mode.getNormalizer2(options);
  }
  





  public int getOption(int option)
  {
    if ((options & option) != 0) {
      return 1;
    }
    return 0;
  }
  









  public int getText(char[] fillIn)
  {
    return text.getText(fillIn);
  }
  




  public int getLength()
  {
    return text.getLength();
  }
  




  public String getText()
  {
    return text.getText();
  }
  





  public void setText(StringBuffer newText)
  {
    UCharacterIterator newIter = UCharacterIterator.getInstance(newText);
    if (newIter == null) {
      throw new IllegalStateException("Could not create a new UCharacterIterator");
    }
    text = newIter;
    reset();
  }
  





  public void setText(char[] newText)
  {
    UCharacterIterator newIter = UCharacterIterator.getInstance(newText);
    if (newIter == null) {
      throw new IllegalStateException("Could not create a new UCharacterIterator");
    }
    text = newIter;
    reset();
  }
  





  public void setText(String newText)
  {
    UCharacterIterator newIter = UCharacterIterator.getInstance(newText);
    if (newIter == null) {
      throw new IllegalStateException("Could not create a new UCharacterIterator");
    }
    text = newIter;
    reset();
  }
  





  public void setText(CharacterIterator newText)
  {
    UCharacterIterator newIter = UCharacterIterator.getInstance(newText);
    if (newIter == null) {
      throw new IllegalStateException("Could not create a new UCharacterIterator");
    }
    text = newIter;
    reset();
  }
  




  public void setText(UCharacterIterator newText)
  {
    try
    {
      UCharacterIterator newIter = (UCharacterIterator)newText.clone();
      if (newIter == null) {
        throw new IllegalStateException("Could not create a new UCharacterIterator");
      }
      text = newIter;
      reset();
    } catch (CloneNotSupportedException e) {
      throw new IllegalStateException("Could not clone the UCharacterIterator");
    }
  }
  
  private void clearBuffer() {
    buffer.setLength(0);
    bufferPos = 0;
  }
  
  private boolean nextNormalize() {
    clearBuffer();
    currentIndex = nextIndex;
    text.setIndex(nextIndex);
    
    int c = text.nextCodePoint();
    if (c < 0) {
      return false;
    }
    StringBuilder segment = new StringBuilder().appendCodePoint(c);
    while ((c = text.nextCodePoint()) >= 0) {
      if (norm2.hasBoundaryBefore(c)) {
        text.moveCodePointIndex(-1);
        break;
      }
      segment.appendCodePoint(c);
    }
    nextIndex = text.getIndex();
    norm2.normalize(segment, buffer);
    return buffer.length() != 0;
  }
  
  private boolean previousNormalize() {
    clearBuffer();
    nextIndex = currentIndex;
    text.setIndex(currentIndex);
    StringBuilder segment = new StringBuilder();
    int c;
    while ((c = text.previousCodePoint()) >= 0) {
      if (c <= 65535) {
        segment.insert(0, (char)c);
      } else {
        segment.insert(0, Character.toChars(c));
      }
      if (norm2.hasBoundaryBefore(c)) {
        break;
      }
    }
    currentIndex = text.getIndex();
    norm2.normalize(segment, buffer);
    bufferPos = buffer.length();
    return buffer.length() != 0;
  }
  


  private static int internalCompare(CharSequence s1, CharSequence s2, int options)
  {
    int normOptions = options >>> 20;
    options |= 0x80000;
    





















    if (((options & 0x20000) == 0) || ((options & 0x1) != 0)) { Normalizer2 n2;
      Normalizer2 n2;
      if ((options & 0x1) != 0) {
        n2 = NFD.getNormalizer2(normOptions);
      } else {
        n2 = FCD.getNormalizer2(normOptions);
      }
      

      int spanQCYes1 = n2.spanQuickCheckYes(s1);
      int spanQCYes2 = n2.spanQuickCheckYes(s2);
      









      if (spanQCYes1 < s1.length()) {
        StringBuilder fcd1 = new StringBuilder(s1.length() + 16).append(s1, 0, spanQCYes1);
        s1 = n2.normalizeSecondAndAppend(fcd1, s1.subSequence(spanQCYes1, s1.length()));
      }
      if (spanQCYes2 < s2.length()) {
        StringBuilder fcd2 = new StringBuilder(s2.length() + 16).append(s2, 0, spanQCYes2);
        s2 = n2.normalizeSecondAndAppend(fcd2, s2.subSequence(spanQCYes2, s2.length()));
      }
    }
    
    return cmpEquivFold(s1, s2, options);
  }
  






























































































  private static final CmpEquivLevel[] createCmpEquivLevelStack()
  {
    return new CmpEquivLevel[] { new CmpEquivLevel(null), new CmpEquivLevel(null) };
  }
  


















  static int cmpEquivFold(CharSequence cs1, CharSequence cs2, int options)
  {
    CmpEquivLevel[] stack1 = null;CmpEquivLevel[] stack2 = null;
    






    Normalizer2Impl nfcImpl;
    






    Normalizer2Impl nfcImpl;
    





    if ((options & 0x80000) != 0) {
      nfcImpl = getNFCInstanceimpl;
    } else
      nfcImpl = null;
    StringBuilder fold2;
    UCaseProps csp; StringBuilder fold2; StringBuilder fold1; if ((options & 0x10000) != 0) {
      UCaseProps csp = UCaseProps.INSTANCE;
      StringBuilder fold1 = new StringBuilder();
      fold2 = new StringBuilder();
    } else {
      csp = null;
      fold1 = fold2 = null;
    }
    

    int s1 = 0;
    int limit1 = cs1.length();
    int s2 = 0;
    int limit2 = cs2.length();
    int level2;
    int level1 = level2 = 0;
    int c2; int c1 = c2 = -1;
    





    for (;;)
    {
      if (c1 < 0) {
        for (;;)
        {
          if (s1 == limit1) {
            if (level1 == 0) {
              c1 = -1;
              break;
            }
          } else {
            c1 = cs1.charAt(s1++);
            break;
          }
          
          do
          {
            level1--;
            cs1 = cs;
          } while (cs1 == null);
          s1 = s;
          limit1 = cs1.length();
        }
      }
      
      if (c2 < 0) {
        for (;;)
        {
          if (s2 == limit2) {
            if (level2 == 0) {
              c2 = -1;
              break;
            }
          } else {
            c2 = cs2.charAt(s2++);
            break;
          }
          
          do
          {
            level2--;
            cs2 = cs;
          } while (cs2 == null);
          s2 = s;
          limit2 = cs2.length();
        }
      }
      




      if (c1 == c2) {
        if (c1 < 0) {
          return 0;
        }
        c1 = c2 = -1;
      } else {
        if (c1 < 0)
          return -1;
        if (c2 < 0) {
          return 1;
        }
        


        int cp1 = c1;
        if (UTF16.isSurrogate((char)c1))
        {

          if (Normalizer2Impl.UTF16Plus.isSurrogateLead(c1)) { char c;
            if ((s1 != limit1) && (Character.isLowSurrogate(c = cs1.charAt(s1))))
            {
              cp1 = Character.toCodePoint((char)c1, c); }
          } else {
            char c;
            if ((0 <= s1 - 2) && (Character.isHighSurrogate(c = cs1.charAt(s1 - 2)))) {
              cp1 = Character.toCodePoint(c, (char)c1);
            }
          }
        }
        
        int cp2 = c2;
        if (UTF16.isSurrogate((char)c2))
        {

          if (Normalizer2Impl.UTF16Plus.isSurrogateLead(c2)) { char c;
            if ((s2 != limit2) && (Character.isLowSurrogate(c = cs2.charAt(s2))))
            {
              cp2 = Character.toCodePoint((char)c2, c); }
          } else {
            char c;
            if ((0 <= s2 - 2) && (Character.isHighSurrogate(c = cs2.charAt(s2 - 2)))) {
              cp2 = Character.toCodePoint(c, (char)c2);
            }
          }
        }
        


        int length;
        

        if ((level1 == 0) && ((options & 0x10000) != 0) && ((length = csp.toFullFolding(cp1, fold1, options)) >= 0))
        {


          if (UTF16.isSurrogate((char)c1)) {
            if (Normalizer2Impl.UTF16Plus.isSurrogateLead(c1))
            {
              s1++;



            }
            else
            {


              s2--;
              c2 = cs2.charAt(s2 - 1);
            }
          }
          

          if (stack1 == null) {
            stack1 = createCmpEquivLevelStack();
          }
          0cs = cs1;
          0s = s1;
          level1++;
          


          if (length <= 31) {
            fold1.delete(0, fold1.length() - length);
          } else {
            fold1.setLength(0);
            fold1.appendCodePoint(length);
          }
          

          cs1 = fold1;
          s1 = 0;
          limit1 = fold1.length();
          

          c1 = -1;
        }
        else {
          int length;
          if ((level2 == 0) && ((options & 0x10000) != 0) && ((length = csp.toFullFolding(cp2, fold2, options)) >= 0))
          {


            if (UTF16.isSurrogate((char)c2)) {
              if (Normalizer2Impl.UTF16Plus.isSurrogateLead(c2))
              {
                s2++;



              }
              else
              {


                s1--;
                c1 = cs1.charAt(s1 - 1);
              }
            }
            

            if (stack2 == null) {
              stack2 = createCmpEquivLevelStack();
            }
            0cs = cs2;
            0s = s2;
            level2++;
            


            if (length <= 31) {
              fold2.delete(0, fold2.length() - length);
            } else {
              fold2.setLength(0);
              fold2.appendCodePoint(length);
            }
            

            cs2 = fold2;
            s2 = 0;
            limit2 = fold2.length();
            

            c2 = -1;
          }
          else {
            String decomp1;
            if ((level1 < 2) && ((options & 0x80000) != 0) && ((decomp1 = nfcImpl.getDecomposition(cp1)) != null))
            {


              if (UTF16.isSurrogate((char)c1)) {
                if (Normalizer2Impl.UTF16Plus.isSurrogateLead(c1))
                {
                  s1++;



                }
                else
                {


                  s2--;
                  c2 = cs2.charAt(s2 - 1);
                }
              }
              

              if (stack1 == null) {
                stack1 = createCmpEquivLevelStack();
              }
              cs = cs1;
              s = s1;
              level1++;
              

              if (level1 < 2) {
                cs = null;
              }
              

              cs1 = decomp1;
              s1 = 0;
              limit1 = decomp1.length();
              

              c1 = -1;
            }
            else {
              String decomp2;
              if ((level2 >= 2) || ((options & 0x80000) == 0) || ((decomp2 = nfcImpl.getDecomposition(cp2)) == null)) {
                break;
              }
              
              if (UTF16.isSurrogate((char)c2)) {
                if (Normalizer2Impl.UTF16Plus.isSurrogateLead(c2))
                {
                  s2++;



                }
                else
                {


                  s1--;
                  c1 = cs1.charAt(s1 - 1);
                }
              }
              

              if (stack2 == null) {
                stack2 = createCmpEquivLevelStack();
              }
              cs = cs2;
              s = s2;
              level2++;
              

              if (level2 < 2) {
                cs = null;
              }
              

              cs2 = decomp2;
              s2 = 0;
              limit2 = decomp2.length();
              

              c2 = -1;
            }
          }
        }
      }
    }
    














    if ((c1 >= 55296) && (c2 >= 55296) && ((options & 0x8000) != 0))
    {
      if (((c1 > 56319) || (s1 == limit1) || (!Character.isLowSurrogate(cs1.charAt(s1)))) && ((!Character.isLowSurrogate((char)c1)) || (0 == s1 - 1) || (!Character.isHighSurrogate(cs1.charAt(s1 - 2)))))
      {





        c1 -= 10240;
      }
      
      if (((c2 > 56319) || (s2 == limit2) || (!Character.isLowSurrogate(cs2.charAt(s2)))) && ((!Character.isLowSurrogate((char)c2)) || (0 == s2 - 1) || (!Character.isHighSurrogate(cs2.charAt(s2 - 2)))))
      {





        c2 -= 10240;
      }
    }
    
    return c1 - c2; }
  
  public static final class QuickCheckResult { private QuickCheckResult(int value) {} }
  
  private static final class CmpEquivLevel { CharSequence cs;
    int s;
    
    private CmpEquivLevel() {} }
  
  private static final class CharsAppendable implements Appendable { private final char[] chars;
    private final int start;
    private final int limit;
    private int offset;
    
    public CharsAppendable(char[] dest, int destStart, int destLimit) { chars = dest;
      start = (this.offset = destStart);
      limit = destLimit;
    }
    
    public int length() { int len = offset - start;
      if (offset <= limit) {
        return len;
      }
      throw new IndexOutOfBoundsException(Integer.toString(len));
    }
    
    public Appendable append(char c) {
      if (offset < limit) {
        chars[offset] = c;
      }
      offset += 1;
      return this;
    }
    
    public Appendable append(CharSequence s) { return append(s, 0, s.length()); }
    
    public Appendable append(CharSequence s, int sStart, int sLimit) {
      int len = sLimit - sStart;
      if (len <= limit - offset) {
        while (sStart < sLimit) {
          chars[(offset++)] = s.charAt(sStart++);
        }
      }
      offset += len;
      
      return this;
    }
  }
}
