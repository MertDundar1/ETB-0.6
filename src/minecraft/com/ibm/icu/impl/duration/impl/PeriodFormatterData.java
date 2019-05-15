package com.ibm.icu.impl.duration.impl;

import com.ibm.icu.impl.duration.TimeUnit;
import java.io.PrintStream;
import java.util.Arrays;




























public class PeriodFormatterData
{
  final DataRecord dr;
  String localeName;
  public static boolean trace = false;
  private static final int FORM_PLURAL = 0;
  
  public PeriodFormatterData(String localeName, DataRecord dr) { this.dr = dr;
    this.localeName = localeName;
    if (localeName == null) {
      throw new NullPointerException("localename is null");
    }
    
    if (dr == null)
    {
      throw new NullPointerException("data record is null");
    }
  }
  



  private static final int FORM_SINGULAR = 1;
  


  private static final int FORM_DUAL = 2;
  


  public int pluralization()
  {
    return dr.pl;
  }
  



  public boolean allowZero()
  {
    return dr.allowZero;
  }
  
  public boolean weeksAloneOnly() {
    return dr.weeksAloneOnly;
  }
  
  public int useMilliseconds() {
    return dr.useMilliseconds;
  }
  








  public boolean appendPrefix(int tl, int td, StringBuffer sb)
  {
    if (dr.scopeData != null) {
      int ix = tl * 3 + td;
      DataRecord.ScopeData sd = dr.scopeData[ix];
      if (sd != null) {
        String prefix = prefix;
        if (prefix != null) {
          sb.append(prefix);
          return requiresDigitPrefix;
        }
      }
    }
    return false;
  }
  







  public void appendSuffix(int tl, int td, StringBuffer sb)
  {
    if (dr.scopeData != null) {
      int ix = tl * 3 + td;
      DataRecord.ScopeData sd = dr.scopeData[ix];
      if (sd != null) {
        String suffix = suffix;
        if (suffix != null) {
          if (trace) {
            System.out.println("appendSuffix '" + suffix + "'");
          }
          sb.append(suffix);
        }
      }
    }
  }
  



  private static final int FORM_PAUCAL = 3;
  


  private static final int FORM_SINGULAR_SPELLED = 4;
  


  private static final int FORM_SINGULAR_NO_OMIT = 5;
  


  private static final int FORM_HALF_SPELLED = 6;
  


  public boolean appendUnit(TimeUnit unit, int count, int cv, int uv, boolean useCountSep, boolean useDigitPrefix, boolean multiple, boolean last, boolean wasSkipped, StringBuffer sb)
  {
    int px = unit.ordinal();
    
    boolean willRequireSkipMarker = false;
    if ((dr.requiresSkipMarker != null) && (dr.requiresSkipMarker[px] != 0) && (dr.skippedUnitMarker != null))
    {
      if ((!wasSkipped) && (last)) {
        sb.append(dr.skippedUnitMarker);
      }
      willRequireSkipMarker = true;
    }
    
    if (uv != 0) {
      boolean useMedium = uv == 1;
      String[] names = useMedium ? dr.mediumNames : dr.shortNames;
      if ((names == null) || (names[px] == null)) {
        names = useMedium ? dr.shortNames : dr.mediumNames;
      }
      if ((names != null) && (names[px] != null)) {
        appendCount(unit, false, false, count, cv, useCountSep, names[px], last, sb);
        
        return false;
      }
    }
    

    if ((cv == 2) && (dr.halfSupport != null)) {
      switch (dr.halfSupport[px]) {
      case 0: 
        break;
      case 2:  if (count > 1000) {
          break;
        }
      
      case 1: 
        count = count / 500 * 500;
        cv = 3;
      }
      
    }
    
    String name = null;
    int form = computeForm(unit, count, cv, (multiple) && (last));
    if (form == 4) {
      if (dr.singularNames == null) {
        form = 1;
        name = dr.pluralNames[px][form];
      } else {
        name = dr.singularNames[px];
      }
    } else if (form == 5) {
      name = dr.pluralNames[px][1];
    } else if (form == 6) {
      name = dr.halfNames[px];
    } else {
      try {
        name = dr.pluralNames[px][form];
      } catch (NullPointerException e) {
        System.out.println("Null Pointer in PeriodFormatterData[" + localeName + "].au px: " + px + " form: " + form + " pn: " + Arrays.toString(dr.pluralNames));
        throw e;
      }
    }
    if (name == null) {
      form = 0;
      name = dr.pluralNames[px][form];
    }
    
    boolean omitCount = (form == 4) || (form == 6) || ((dr.omitSingularCount) && (form == 1)) || ((dr.omitDualCount) && (form == 2));
    



    int suffixIndex = appendCount(unit, omitCount, useDigitPrefix, count, cv, useCountSep, name, last, sb);
    
    if ((last) && (suffixIndex >= 0)) {
      String suffix = null;
      if ((dr.rqdSuffixes != null) && (suffixIndex < dr.rqdSuffixes.length)) {
        suffix = dr.rqdSuffixes[suffixIndex];
      }
      if ((suffix == null) && (dr.optSuffixes != null) && (suffixIndex < dr.optSuffixes.length))
      {
        suffix = dr.optSuffixes[suffixIndex];
      }
      if (suffix != null) {
        sb.append(suffix);
      }
    }
    return willRequireSkipMarker;
  }
  














  public int appendCount(TimeUnit unit, boolean omitCount, boolean useDigitPrefix, int count, int cv, boolean useSep, String name, boolean last, StringBuffer sb)
  {
    if ((cv == 2) && (dr.halves == null)) {
      cv = 0;
    }
    
    if ((!omitCount) && (useDigitPrefix) && (dr.digitPrefix != null)) {
      sb.append(dr.digitPrefix);
    }
    
    int index = unit.ordinal();
    switch (cv) {
    case 0: 
      if (!omitCount) {
        appendInteger(count / 1000, 1, 10, sb);
      }
      
      break;
    case 1: 
      int val = count / 1000;
      
      if ((unit == TimeUnit.MINUTE) && ((dr.fiveMinutes != null) || (dr.fifteenMinutes != null)))
      {
        if ((val != 0) && (val % 5 == 0)) {
          if ((dr.fifteenMinutes != null) && ((val == 15) || (val == 45))) {
            val = val == 15 ? 1 : 3;
            if (!omitCount) appendInteger(val, 1, 10, sb);
            name = dr.fifteenMinutes;
            index = 8;
            break;
          }
          if (dr.fiveMinutes != null) {
            val /= 5;
            if (!omitCount) appendInteger(val, 1, 10, sb);
            name = dr.fiveMinutes;
            index = 9;
            break;
          }
        }
      }
      if (!omitCount) appendInteger(val, 1, 10, sb);
      break;
    

    case 2: 
      int v = count / 500;
      if ((v != 1) && 
        (!omitCount)) { appendCountValue(count, 1, 0, sb);
      }
      if ((v & 0x1) == 1)
      {
        if ((v == 1) && (dr.halfNames != null) && (dr.halfNames[index] != null)) {
          sb.append(name);
          return last ? index : -1;
        }
        
        int solox = v == 1 ? 0 : 1;
        if ((dr.genders != null) && (dr.halves.length > 2) && 
          (dr.genders[index] == 1)) {
          solox += 2;
        }
        
        int hp = dr.halfPlacements == null ? 0 : dr.halfPlacements[(solox & 0x1)];
        

        String half = dr.halves[solox];
        String measure = dr.measures == null ? null : dr.measures[index];
        switch (hp) {
        case 0: 
          sb.append(half);
          break;
        case 1: 
          if (measure != null) {
            sb.append(measure);
            sb.append(half);
            if ((useSep) && (!omitCount)) {
              sb.append(dr.countSep);
            }
            sb.append(name);
          } else {
            sb.append(name);
            sb.append(half);
            return last ? index : -1;
          }
          return -1;
        case 2: 
          if (measure != null) {
            sb.append(measure);
          }
          if ((useSep) && (!omitCount)) {
            sb.append(dr.countSep);
          }
          sb.append(name);
          sb.append(half);
          return last ? index : -1;
        }
      }
      break;
    default: 
      int decimals = 1;
      switch (cv) {
      case 4:  decimals = 2; break;
      case 5:  decimals = 3; break;
      }
      
      if (!omitCount) appendCountValue(count, 1, decimals, sb);
      break;
    }
    if ((!omitCount) && (useSep)) {
      sb.append(dr.countSep);
    }
    if ((!omitCount) && (dr.measures != null) && (index < dr.measures.length)) {
      String measure = dr.measures[index];
      if (measure != null) {
        sb.append(measure);
      }
    }
    sb.append(name);
    return last ? index : -1;
  }
  








  public void appendCountValue(int count, int integralDigits, int decimalDigits, StringBuffer sb)
  {
    int ival = count / 1000;
    if (decimalDigits == 0) {
      appendInteger(ival, integralDigits, 10, sb);
      return;
    }
    
    if ((dr.requiresDigitSeparator) && (sb.length() > 0)) {
      sb.append(' ');
    }
    appendDigits(ival, integralDigits, 10, sb);
    int dval = count % 1000;
    if (decimalDigits == 1) {
      dval /= 100;
    } else if (decimalDigits == 2) {
      dval /= 10;
    }
    sb.append(dr.decimalSep);
    appendDigits(dval, decimalDigits, decimalDigits, sb);
    if (dr.requiresDigitSeparator) {
      sb.append(' ');
    }
  }
  
  public void appendInteger(int num, int mindigits, int maxdigits, StringBuffer sb)
  {
    if ((dr.numberNames != null) && (num < dr.numberNames.length)) {
      String name = dr.numberNames[num];
      if (name != null) {
        sb.append(name);
        return;
      }
    }
    
    if ((dr.requiresDigitSeparator) && (sb.length() > 0)) {
      sb.append(' ');
    }
    switch (dr.numberSystem) {
    case 0:  appendDigits(num, mindigits, maxdigits, sb); break;
    case 1:  sb.append(Utils.chineseNumber(num, Utils.ChineseDigits.TRADITIONAL));
      break;
    case 2:  sb.append(Utils.chineseNumber(num, Utils.ChineseDigits.SIMPLIFIED));
      break;
    case 3:  sb.append(Utils.chineseNumber(num, Utils.ChineseDigits.KOREAN));
    }
    
    if (dr.requiresDigitSeparator) {
      sb.append(' ');
    }
  }
  








  public void appendDigits(long num, int mindigits, int maxdigits, StringBuffer sb)
  {
    char[] buf = new char[maxdigits];
    int ix = maxdigits;
    while ((ix > 0) && (num > 0L)) {
      buf[(--ix)] = ((char)(int)(dr.zero + num % 10L));
      num /= 10L;
    }
    for (int e = maxdigits - mindigits; ix > e;) {
      buf[(--ix)] = dr.zero;
    }
    sb.append(buf, ix, maxdigits - ix);
  }
  



  public void appendSkippedUnit(StringBuffer sb)
  {
    if (dr.skippedUnitMarker != null) {
      sb.append(dr.skippedUnitMarker);
    }
  }
  















  public boolean appendUnitSeparator(TimeUnit unit, boolean longSep, boolean afterFirst, boolean beforeLast, StringBuffer sb)
  {
    if (((longSep) && (dr.unitSep != null)) || (dr.shortUnitSep != null)) {
      if ((longSep) && (dr.unitSep != null)) {
        int ix = (afterFirst ? 2 : 0) + (beforeLast ? 1 : 0);
        sb.append(dr.unitSep[ix]);
        return (dr.unitSepRequiresDP != null) && (dr.unitSepRequiresDP[ix] != 0);
      }
      sb.append(dr.shortUnitSep);
    }
    return false;
  }
  














  private int computeForm(TimeUnit unit, int count, int cv, boolean lastOfMultiple)
  {
    if (trace) {
      System.err.println("pfd.cf unit: " + unit + " count: " + count + " cv: " + cv + " dr.pl: " + dr.pl);
      Thread.dumpStack();
    }
    if (dr.pl == 0) {
      return 0;
    }
    

    int val = count / 1000;
    
    switch (cv)
    {
    case 0: 
    case 1: 
      break;
    case 2: 
      switch (dr.fractionHandling) {
      case 0: 
        return 0;
      



      case 1: 
      case 2: 
        int v = count / 500;
        if (v == 1) {
          if ((dr.halfNames != null) && (dr.halfNames[unit.ordinal()] != null)) {
            return 6;
          }
          return 5;
        }
        if ((v & 0x1) == 1) {
          if ((dr.pl == 5) && (v > 21)) {
            return 5;
          }
          if ((v == 3) && (dr.pl == 1) && (dr.fractionHandling != 2))
          {
            return 0;
          }
        }
        

        break;
      
      case 3: 
        int v = count / 500;
        if ((v == 1) || (v == 3)) {
          return 3;
        }
        
        break;
      
      default: 
        throw new IllegalStateException();
      }
      break;
    default: 
      switch (dr.decimalHandling) {
      case 0: 
        break; case 1:  return 5;
      case 2: 
        if (count < 1000) {
          return 5;
        }
        break;
      case 3: 
        if (dr.pl == 3) {
          return 3;
        }
        
        break;
      }
      
      return 0;
    }
    
    

    if ((trace) && (count == 0)) {
      System.err.println("EZeroHandling = " + dr.zeroHandling);
    }
    if ((count == 0) && (dr.zeroHandling == 1)) {
      return 4;
    }
    
    int form = 0;
    switch (dr.pl) {
    case 0: 
      break;
    case 1:  if (val == 1) {
        form = 4;
      }
      break;
    case 2: 
      if (val == 2) {
        form = 2;
      } else if (val == 1) {
        form = 1;
      }
      break;
    case 3: 
      int v = val;
      v %= 100;
      if (v > 20) {
        v %= 10;
      }
      if (v == 1) {
        form = 1;
      } else if ((v > 1) && (v < 5)) {
        form = 3;
      }
      break;
    














    case 4: 
      if (val == 2) {
        form = 2;
      } else if (val == 1) {
        if (lastOfMultiple) {
          form = 4;
        } else {
          form = 1;
        }
      } else if ((unit == TimeUnit.YEAR) && (val > 11)) {
        form = 5;
      }
      break;
    case 5: 
      if (val == 2) {
        form = 2;
      } else if (val == 1) {
        form = 1;
      } else if (val > 10) {
        form = 5;
      }
      break;
    default: 
      System.err.println("dr.pl is " + dr.pl);
      throw new IllegalStateException();
    }
    
    return form;
  }
}
