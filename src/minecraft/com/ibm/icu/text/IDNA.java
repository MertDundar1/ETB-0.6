package com.ibm.icu.text;

import com.ibm.icu.impl.IDNA2003;
import com.ibm.icu.impl.UTS46;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

































































































































public abstract class IDNA
{
  public static final int DEFAULT = 0;
  public static final int ALLOW_UNASSIGNED = 1;
  public static final int USE_STD3_RULES = 2;
  public static final int CHECK_BIDI = 4;
  public static final int CHECK_CONTEXTJ = 8;
  public static final int NONTRANSITIONAL_TO_ASCII = 16;
  public static final int NONTRANSITIONAL_TO_UNICODE = 32;
  public static final int CHECK_CONTEXTO = 64;
  
  public static IDNA getUTS46Instance(int options)
  {
    return new UTS46(options);
  }
  





  public abstract StringBuilder labelToASCII(CharSequence paramCharSequence, StringBuilder paramStringBuilder, Info paramInfo);
  





  public abstract StringBuilder labelToUnicode(CharSequence paramCharSequence, StringBuilder paramStringBuilder, Info paramInfo);
  





  public abstract StringBuilder nameToASCII(CharSequence paramCharSequence, StringBuilder paramStringBuilder, Info paramInfo);
  





  public abstract StringBuilder nameToUnicode(CharSequence paramCharSequence, StringBuilder paramStringBuilder, Info paramInfo);
  





  public static final class Info
  {
    private EnumSet<IDNA.Error> errors;
    




    private EnumSet<IDNA.Error> labelErrors;
    




    private boolean isTransDiff;
    




    private boolean isBiDi;
    




    private boolean isOkBiDi;
    




    public Info()
    {
      errors = EnumSet.noneOf(IDNA.Error.class);
      labelErrors = EnumSet.noneOf(IDNA.Error.class);
      isTransDiff = false;
      isBiDi = false;
      isOkBiDi = true;
    }
    


    public boolean hasErrors()
    {
      return !errors.isEmpty();
    }
    

    public Set<IDNA.Error> getErrors()
    {
      return errors;
    }
    











    public boolean isTransitionalDifferent() { return isTransDiff; }
    
    private void reset() {
      errors.clear();
      labelErrors.clear();
      isTransDiff = false;
      isBiDi = false;
      isOkBiDi = true;
    }
  }
  








  /**
   * @deprecated
   */
  protected static void resetInfo(Info info)
  {
    info.reset();
  }
  
  /**
   * @deprecated
   */
  protected static boolean hasCertainErrors(Info info, EnumSet<Error> errors) {
    return (!info.errors.isEmpty()) && (!Collections.disjoint(info.errors, errors));
  }
  
  /**
   * @deprecated
   */
  protected static boolean hasCertainLabelErrors(Info info, EnumSet<Error> errors) {
    return (!labelErrors.isEmpty()) && (!Collections.disjoint(labelErrors, errors));
  }
  
  /**
   * @deprecated
   */
  protected static void addLabelError(Info info, Error error) {
    labelErrors.add(error);
  }
  
  /**
   * @deprecated
   */
  protected static void promoteAndResetLabelErrors(Info info) {
    if (!labelErrors.isEmpty()) {
      errors.addAll(labelErrors);
      labelErrors.clear();
    }
  }
  
  /**
   * @deprecated
   */
  protected static void addError(Info info, Error error) {
    errors.add(error);
  }
  
  /**
   * @deprecated
   */
  protected static void setTransitionalDifferent(Info info) {
    isTransDiff = true;
  }
  
  /**
   * @deprecated
   */
  protected static void setBiDi(Info info) {
    isBiDi = true;
  }
  
  /**
   * @deprecated
   */
  protected static boolean isBiDi(Info info) {
    return isBiDi;
  }
  
  /**
   * @deprecated
   */
  protected static void setNotOkBiDi(Info info) {
    isOkBiDi = false;
  }
  
  /**
   * @deprecated
   */
  protected static boolean isOkBiDi(Info info) {
    return isOkBiDi;
  }
  


  /**
   * @deprecated
   */
  protected IDNA() {}
  


  public static enum Error
  {
    EMPTY_LABEL, 
    





    LABEL_TOO_LONG, 
    





    DOMAIN_NAME_TOO_LONG, 
    



    LEADING_HYPHEN, 
    



    TRAILING_HYPHEN, 
    



    HYPHEN_3_4, 
    



    LEADING_COMBINING_MARK, 
    



    DISALLOWED, 
    




    PUNYCODE, 
    




    LABEL_HAS_DOT, 
    







    INVALID_ACE_LABEL, 
    



    BIDI, 
    



    CONTEXTJ, 
    





    CONTEXTO_PUNCTUATION, 
    




    CONTEXTO_DIGITS;
    
















    private Error() {}
  }
  
















  public static StringBuffer convertToASCII(String src, int options)
    throws StringPrepParseException
  {
    UCharacterIterator iter = UCharacterIterator.getInstance(src);
    return convertToASCII(iter, options);
  }
  























  public static StringBuffer convertToASCII(StringBuffer src, int options)
    throws StringPrepParseException
  {
    UCharacterIterator iter = UCharacterIterator.getInstance(src);
    return convertToASCII(iter, options);
  }
  























  public static StringBuffer convertToASCII(UCharacterIterator src, int options)
    throws StringPrepParseException
  {
    return IDNA2003.convertToASCII(src, options);
  }
  




























  public static StringBuffer convertIDNToASCII(UCharacterIterator src, int options)
    throws StringPrepParseException
  {
    return convertIDNToASCII(src.getText(), options);
  }
  




























  public static StringBuffer convertIDNToASCII(StringBuffer src, int options)
    throws StringPrepParseException
  {
    return convertIDNToASCII(src.toString(), options);
  }
  




























  public static StringBuffer convertIDNToASCII(String src, int options)
    throws StringPrepParseException
  {
    return IDNA2003.convertIDNToASCII(src, options);
  }
  
























  public static StringBuffer convertToUnicode(String src, int options)
    throws StringPrepParseException
  {
    UCharacterIterator iter = UCharacterIterator.getInstance(src);
    return convertToUnicode(iter, options);
  }
  























  public static StringBuffer convertToUnicode(StringBuffer src, int options)
    throws StringPrepParseException
  {
    UCharacterIterator iter = UCharacterIterator.getInstance(src);
    return convertToUnicode(iter, options);
  }
  























  public static StringBuffer convertToUnicode(UCharacterIterator src, int options)
    throws StringPrepParseException
  {
    return IDNA2003.convertToUnicode(src, options);
  }
  

























  public static StringBuffer convertIDNToUnicode(UCharacterIterator src, int options)
    throws StringPrepParseException
  {
    return convertIDNToUnicode(src.getText(), options);
  }
  

























  public static StringBuffer convertIDNToUnicode(StringBuffer src, int options)
    throws StringPrepParseException
  {
    return convertIDNToUnicode(src.toString(), options);
  }
  

























  public static StringBuffer convertIDNToUnicode(String src, int options)
    throws StringPrepParseException
  {
    return IDNA2003.convertIDNToUnicode(src, options);
  }
  


























  public static int compare(StringBuffer s1, StringBuffer s2, int options)
    throws StringPrepParseException
  {
    if ((s1 == null) || (s2 == null)) {
      throw new IllegalArgumentException("One of the source buffers is null");
    }
    return IDNA2003.compare(s1.toString(), s2.toString(), options);
  }
  

























  public static int compare(String s1, String s2, int options)
    throws StringPrepParseException
  {
    if ((s1 == null) || (s2 == null)) {
      throw new IllegalArgumentException("One of the source buffers is null");
    }
    return IDNA2003.compare(s1, s2, options);
  }
  

























  public static int compare(UCharacterIterator s1, UCharacterIterator s2, int options)
    throws StringPrepParseException
  {
    if ((s1 == null) || (s2 == null)) {
      throw new IllegalArgumentException("One of the source buffers is null");
    }
    return IDNA2003.compare(s1.getText(), s2.getText(), options);
  }
}
