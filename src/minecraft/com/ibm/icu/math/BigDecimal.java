package com.ibm.icu.math;

import com.ibm.icu.lang.UCharacter;
import java.io.Serializable;
import java.math.BigInteger;
































































































































































































































public class BigDecimal
  extends Number
  implements Serializable, Comparable<BigDecimal>
{
  public static final BigDecimal ZERO = new BigDecimal(0L);
  










  public static final BigDecimal ONE = new BigDecimal(1L);
  










  public static final BigDecimal TEN = new BigDecimal(10);
  



  public static final int ROUND_CEILING = 2;
  



  public static final int ROUND_DOWN = 1;
  



  public static final int ROUND_FLOOR = 3;
  



  public static final int ROUND_HALF_DOWN = 5;
  



  public static final int ROUND_HALF_EVEN = 6;
  



  public static final int ROUND_HALF_UP = 4;
  



  public static final int ROUND_UNNECESSARY = 7;
  



  public static final int ROUND_UP = 0;
  



  private static final byte ispos = 1;
  



  private static final byte iszero = 0;
  



  private static final byte isneg = -1;
  



  private static final int MinExp = -999999999;
  



  private static final int MaxExp = 999999999;
  



  private static final int MinArg = -999999999;
  


  private static final int MaxArg = 999999999;
  


  private static final MathContext plainMC = new MathContext(0, 0);
  




  private static final long serialVersionUID = 8245355804974198832L;
  




  private static byte[] bytecar = new byte['¾'];
  private static byte[] bytedig = diginit();
  













  private byte ind;
  













  private byte form = 0;
  














  private byte[] mant;
  














  private int exp;
  














  public BigDecimal(java.math.BigDecimal bd)
  {
    this(bd.toString());
  }
  














  public BigDecimal(BigInteger bi)
  {
    this(bi.toString(10));
  }
  




















  public BigDecimal(BigInteger bi, int scale)
  {
    this(bi.toString(10));
    if (scale < 0)
      throw new NumberFormatException("Negative scale: " + scale);
    exp = (-scale);
  }
  















  public BigDecimal(char[] inchars)
  {
    this(inchars, 0, inchars.length);
  }
  























  public BigDecimal(char[] inchars, int offset, int length)
  {
    int i = 0;
    char si = '\000';
    boolean eneg = false;
    int k = 0;
    int elen = 0;
    int j = 0;
    char sj = '\000';
    int dvalue = 0;
    int mag = 0;
    





    if (length <= 0) {
      bad(inchars);
    }
    

    ind = 1;
    if (inchars[offset] == '-') {
      length--;
      if (length == 0)
        bad(inchars);
      ind = -1;
      offset++;
    } else if (inchars[offset] == '+') {
      length--;
      if (length == 0)
        bad(inchars);
      offset++;
    }
    

    boolean exotic = false;
    boolean hadexp = false;
    int d = 0;
    int dotoff = -1;
    int last = -1;
    
    int $1 = length;
    for (i = offset; 
        $1 > 0; i++) {
      si = inchars[i];
      if ((si >= '0') && 
        (si <= '9')) {
        last = i;
        d++;

      }
      else if (si == '.') {
        if (dotoff >= 0)
          bad(inchars);
        dotoff = i - offset;

      }
      else if ((si != 'e') && 
        (si != 'E')) {
        if (!UCharacter.isDigit(si)) {
          bad(inchars);
        }
        exotic = true;
        last = i;
        d++;

      }
      else
      {
        if (i - offset > length - 2)
          bad(inchars);
        eneg = false;
        if (inchars[(i + 1)] == '-') {
          eneg = true;
          k = i + 2;
        } else if (inchars[(i + 1)] == '+') {
          k = i + 2;
        } else {
          k = i + 1;
        }
        elen = length - (k - offset);
        if (((elen == 0 ? 1 : 0) | (elen > 9 ? 1 : 0)) != 0) {
          bad(inchars);
        }
        int $2 = elen;
        for (j = k; 
            $2 > 0; j++) {
          sj = inchars[j];
          if (sj < '0')
            bad(inchars);
          if (sj > '9') {
            if (!UCharacter.isDigit(sj))
              bad(inchars);
            dvalue = UCharacter.digit(sj, 10);
            if (dvalue < 0)
              bad(inchars);
          } else {
            dvalue = sj - '0'; }
          exp = (exp * 10 + dvalue);$2--;
        }
        
        if (eneg)
          exp = (-exp);
        hadexp = true;
        break;
      }
      $1--;
    }
    































































    if (d == 0)
      bad(inchars);
    if (dotoff >= 0) {
      exp = (exp + dotoff - d);
    }
    

    int $3 = last - 1;
    for (i = offset; 
        i <= $3; i++) {
      si = inchars[i];
      if (si == '0') {
        offset++;
        dotoff--;
        d--;
      } else if (si == '.') {
        offset++;
        dotoff--;
      } else { if (si <= '9') {
          break;
        }
        if (UCharacter.digit(si, 10) != 0) {
          break;
        }
        offset++;
        dotoff--;
        d--;
      }
    }
    


    mant = new byte[d];
    j = offset;
    if (exotic)
    {

      int $4 = d;
      for (i = 0; 
          $4 > 0; i++) {
        if (i == dotoff)
          j++;
        sj = inchars[j];
        if (sj <= '9') {
          mant[i] = ((byte)(sj - '0'));
        } else {
          dvalue = UCharacter.digit(sj, 10);
          if (dvalue < 0)
            bad(inchars);
          mant[i] = ((byte)dvalue);
        }
        j++;$4--;
      }
      

    }
    else
    {

      int $5 = d;
      for (i = 0; 
          $5 > 0; i++) {
        if (i == dotoff)
          j++;
        mant[i] = ((byte)(inchars[j] - '0'));
        j++;$5--;
      }
    }
    









    if (mant[0] == 0) {
      ind = 0;
      
      if (exp > 0)
        exp = 0;
      if (hadexp) {
        mant = ZEROmant;
        exp = 0;
      }
      

    }
    else if (hadexp) {
      form = 1;
      
      mag = exp + mant.length - 1;
      if (((mag < -999999999 ? 1 : 0) | (mag > 999999999 ? 1 : 0)) != 0) {
        bad(inchars);
      }
    }
  }
  





















  public BigDecimal(double num)
  {
    this(new java.math.BigDecimal(num).toString());
  }
  













  public BigDecimal(int num)
  {
    int i = 0;
    
    if ((num <= 9) && 
      (num >= -9))
    {


      if (num == 0) {
        mant = ZEROmant;
        ind = 0;
      } else if (num == 1) {
        mant = ONEmant;
        ind = 1;
      } else if (num == -1) {
        mant = ONEmant;
        ind = -1;
      }
      else {
        mant = new byte[1];
        if (num > 0) {
          mant[0] = ((byte)num);
          ind = 1;
        } else {
          mant[0] = ((byte)-num);
          ind = -1;
        }
      }
      

      return;
    }
    


    if (num > 0) {
      ind = 1;
      num = -num;
    } else {
      ind = -1;
    }
    

    int mun = num;
    
    i = 9;
    for (;; i--) {
      mun /= 10;
      if (mun == 0) {
        break;
      }
    }
    
    mant = new byte[10 - i];
    
    i = 10 - i - 1;
    for (;; i--) {
      mant[i] = ((byte)-(byte)(num % 10));
      num /= 10;
      if (num == 0) {
        break;
      }
    }
  }
  













  public BigDecimal(long num)
  {
    int i = 0;
    



    if (num > 0L) {
      ind = 1;
      num = -num;
    } else if (num == 0L) {
      ind = 0;
    } else {
      ind = -1; }
    long mun = num;
    
    i = 18;
    for (;; i--) {
      mun /= 10L;
      if (mun == 0L) {
        break;
      }
    }
    
    mant = new byte[19 - i];
    
    i = 19 - i - 1;
    for (;; i--) {
      mant[i] = ((byte)-(byte)(int)(num % 10L));
      num /= 10L;
      if (num == 0L) {
        break;
      }
    }
  }
  








































  public BigDecimal(String string)
  {
    this(string.toCharArray(), 0, string.length());
  }
  










  private BigDecimal() {}
  










  public BigDecimal abs()
  {
    return abs(plainMC);
  }
  











  public BigDecimal abs(MathContext set)
  {
    if (ind == -1)
      return negate(set);
    return plus(set);
  }
  












  public BigDecimal add(BigDecimal rhs)
  {
    return add(rhs, plainMC);
  }
  


















  public BigDecimal add(BigDecimal rhs, MathContext set)
  {
    int newlen = 0;
    int tlen = 0;
    int mult = 0;
    byte[] t = null;
    int ia = 0;
    int ib = 0;
    int ea = 0;
    int eb = 0;
    byte ca = 0;
    byte cb = 0;
    
    if (lostDigits)
      checkdigits(rhs, digits);
    BigDecimal lhs = this;
    


    if ((ind == 0) && 
      (form != 0))
      return rhs.plus(set);
    if ((ind == 0) && 
      (form != 0)) {
      return lhs.plus(set);
    }
    
    int reqdig = digits;
    if (reqdig > 0) {
      if (mant.length > reqdig)
        lhs = clone(lhs).round(set);
      if (mant.length > reqdig) {
        rhs = clone(rhs).round(set);
      }
    }
    
    BigDecimal res = new BigDecimal();
    








    byte[] usel = mant;
    int usellen = mant.length;
    byte[] user = mant;
    int userlen = mant.length;
    

    if (exp == exp)
    {
      exp = exp;
    } else if (exp > exp) {
      newlen = usellen + exp - exp;
      



      if ((newlen >= userlen + reqdig + 1) && 
        (reqdig > 0))
      {
        mant = usel;
        exp = exp;
        ind = ind;
        if (usellen < reqdig) {
          mant = extend(mant, reqdig);
          exp -= reqdig - usellen;
        }
        return res.finish(set, false);
      }
      
      exp = exp;
      if ((newlen > reqdig + 1) && 
        (reqdig > 0))
      {
        tlen = newlen - reqdig - 1;
        userlen -= tlen;
        exp += tlen;
        newlen = reqdig + 1;
      }
      if (newlen > usellen)
        usellen = newlen;
    } else {
      newlen = userlen + exp - exp;
      if ((newlen >= usellen + reqdig + 1) && 
        (reqdig > 0))
      {
        mant = user;
        exp = exp;
        ind = ind;
        if (userlen < reqdig) {
          mant = extend(mant, reqdig);
          exp -= reqdig - userlen;
        }
        return res.finish(set, false);
      }
      
      exp = exp;
      if ((newlen > reqdig + 1) && 
        (reqdig > 0))
      {
        tlen = newlen - reqdig - 1;
        usellen -= tlen;
        exp += tlen;
        newlen = reqdig + 1;
      }
      if (newlen > userlen) {
        userlen = newlen;
      }
    }
    





    if (ind == 0) {
      ind = 1;
    } else
      ind = ind;
    if ((ind == -1 ? 1 : 0) == (ind == -1 ? 1 : 0)) {
      mult = 1;
    }
    else {
      mult = -1;
      





      if (ind != 0)
      {
        if (((usellen < userlen ? 1 : 0) | (ind == 0 ? 1 : 0)) != 0) {
          t = usel;
          usel = user;
          user = t;
          tlen = usellen;
          usellen = userlen;
          userlen = tlen;
          ind = ((byte)-ind);
        } else if (usellen <= userlen)
        {



          ia = 0;
          ib = 0;
          ea = usel.length - 1;
          eb = user.length - 1;
          for (;;)
          {
            if (ia <= ea) {
              ca = usel[ia];
            } else {
              if (ib > eb) {
                if (form == 0) break;
                return ZERO;
              }
              

              ca = 0;
            }
            if (ib <= eb) {
              cb = user[ib];
            } else
              cb = 0;
            if (ca != cb) {
              if (ca >= cb) break;
              t = usel;
              usel = user;
              user = t;
              tlen = usellen;
              usellen = userlen;
              userlen = tlen;
              ind = ((byte)-ind); break;
            }
            


            ia++;
            ib++;
          }
        }
      }
    }
    






    mant = byteaddsub(usel, usellen, user, userlen, mult, false);
    


    return res.finish(set, false);
  }
  











  public int compareTo(BigDecimal rhs)
  {
    return compareTo(rhs, plainMC);
  }
  



























  public int compareTo(BigDecimal rhs, MathContext set)
  {
    int thislength = 0;
    int i = 0;
    

    if (lostDigits) {
      checkdigits(rhs, digits);
    }
    if (((ind == ind ? 1 : 0) & (exp == exp ? 1 : 0)) != 0)
    {
      thislength = mant.length;
      if (thislength < mant.length)
        return (byte)-ind;
      if (thislength > mant.length) {
        return ind;
      }
      


      if (((thislength <= digits ? 1 : 0) | (digits == 0 ? 1 : 0)) != 0)
      {
        int $6 = thislength;
        for (i = 0; 
            $6 > 0; i++) {
          if (mant[i] < mant[i])
            return (byte)-ind;
          if (mant[i] > mant[i]) {
            return ind;
          }
          $6--;
        }
        




        return 0;
      }
    }
    else
    {
      if (ind < ind)
        return -1;
      if (ind > ind) {
        return 1;
      }
    }
    BigDecimal newrhs = clone(rhs);
    ind = ((byte)-ind);
    return addind;
  }
  














  public BigDecimal divide(BigDecimal rhs)
  {
    return dodivide('D', rhs, plainMC, -1);
  }
  





















  public BigDecimal divide(BigDecimal rhs, int round)
  {
    MathContext set = new MathContext(0, 0, false, round);
    
    return dodivide('D', rhs, set, -1);
  }
  

























  public BigDecimal divide(BigDecimal rhs, int scale, int round)
  {
    if (scale < 0)
      throw new ArithmeticException("Negative scale: " + scale);
    MathContext set = new MathContext(0, 0, false, round);
    return dodivide('D', rhs, set, scale);
  }
  












  public BigDecimal divide(BigDecimal rhs, MathContext set)
  {
    return dodivide('D', rhs, set, -1);
  }
  












  public BigDecimal divideInteger(BigDecimal rhs)
  {
    return dodivide('I', rhs, plainMC, 0);
  }
  














  public BigDecimal divideInteger(BigDecimal rhs, MathContext set)
  {
    return dodivide('I', rhs, set, 0);
  }
  










  public BigDecimal max(BigDecimal rhs)
  {
    return max(rhs, plainMC);
  }
  
















  public BigDecimal max(BigDecimal rhs, MathContext set)
  {
    if (compareTo(rhs, set) >= 0) {
      return plus(set);
    }
    return rhs.plus(set);
  }
  










  public BigDecimal min(BigDecimal rhs)
  {
    return min(rhs, plainMC);
  }
  
















  public BigDecimal min(BigDecimal rhs, MathContext set)
  {
    if (compareTo(rhs, set) <= 0) {
      return plus(set);
    }
    return rhs.plus(set);
  }
  













  public BigDecimal multiply(BigDecimal rhs)
  {
    return multiply(rhs, plainMC);
  }
  














  public BigDecimal multiply(BigDecimal rhs, MathContext set)
  {
    byte[] multer = null;
    byte[] multand = null;
    
    int acclen = 0;
    

    int n = 0;
    byte mult = 0;
    if (lostDigits)
      checkdigits(rhs, digits);
    BigDecimal lhs = this;
    

    int padding = 0;
    int reqdig = digits;
    if (reqdig > 0) {
      if (mant.length > reqdig)
        lhs = clone(lhs).round(set);
      if (mant.length > reqdig) {
        rhs = clone(rhs).round(set);
      }
    }
    else
    {
      if (exp > 0)
        padding += exp;
      if (exp > 0) {
        padding += exp;
      }
    }
    



    if (mant.length < mant.length) {
      multer = mant;
      multand = mant;
    } else {
      multer = mant;
      multand = mant;
    }
    

    int multandlen = multer.length + multand.length - 1;
    
    if (multer[0] * multand[0] > 9) {
      acclen = multandlen + 1;
    } else {
      acclen = multandlen;
    }
    
    BigDecimal res = new BigDecimal();
    byte[] acc = new byte[acclen];
    




    int $7 = multer.length;
    for (n = 0; 
        $7 > 0; n++) {
      mult = multer[n];
      if (mult != 0)
      {
        acc = byteaddsub(acc, acc.length, multand, multandlen, mult, true);
      }
      
      multandlen--;$7--;
    }
    

    ind = ((byte)(ind * ind));
    exp = (exp + exp - padding);
    


    if (padding == 0) {
      mant = acc;
    } else
      mant = extend(acc, acc.length + padding);
    return res.finish(set, false);
  }
  












  public BigDecimal negate()
  {
    return negate(plainMC);
  }
  













  public BigDecimal negate(MathContext set)
  {
    if (lostDigits)
      checkdigits((BigDecimal)null, digits);
    BigDecimal res = clone(this);
    ind = ((byte)-ind);
    return res.finish(set, false);
  }
  











  public BigDecimal plus()
  {
    return plus(plainMC);
  }
  














  public BigDecimal plus(MathContext set)
  {
    if (lostDigits) {
      checkdigits((BigDecimal)null, digits);
    }
    if ((form == 0) && 
      (form == 0)) {
      if (mant.length <= digits)
        return this;
      if (digits == 0)
        return this;
    }
    return clone(this).finish(set, false);
  }
  


















  public BigDecimal pow(BigDecimal rhs)
  {
    return pow(rhs, plainMC);
  }
  

























  public BigDecimal pow(BigDecimal rhs, MathContext set)
  {
    int workdigits = 0;
    int L = 0;
    


    int i = 0;
    if (lostDigits)
      checkdigits(rhs, digits);
    int n = rhs.intcheck(-999999999, 999999999);
    BigDecimal lhs = this;
    
    int reqdig = digits;
    if (reqdig == 0) {
      if (ind == -1)
        throw new ArithmeticException("Negative power: " + rhs.toString());
      workdigits = 0;
    } else {
      if (mant.length + exp > reqdig) {
        throw new ArithmeticException("Too many digits: " + rhs.toString());
      }
      
      if (mant.length > reqdig) {
        lhs = clone(lhs).round(set);
      }
      
      L = mant.length + exp;
      workdigits = reqdig + L + 1;
    }
    



    MathContext workset = new MathContext(workdigits, form, false, roundingMode);
    
    BigDecimal res = ONE;
    if (n == 0)
      return res;
    if (n < 0)
      n = -n;
    boolean seenbit = false;
    
    i = 1;
    for (;; i++) {
      n += n;
      if (n < 0) {
        seenbit = true;
        res = res.multiply(lhs, workset);
      }
      if (i == 31)
        break;
      if (seenbit)
      {
        res = res.multiply(res, workset);
      }
    }
    if (ind < 0)
      res = ONE.divide(res, workset);
    return res.finish(set, true);
  }
  















  public BigDecimal remainder(BigDecimal rhs)
  {
    return dodivide('R', rhs, plainMC, -1);
  }
  















  public BigDecimal remainder(BigDecimal rhs, MathContext set)
  {
    return dodivide('R', rhs, set, -1);
  }
  












  public BigDecimal subtract(BigDecimal rhs)
  {
    return subtract(rhs, plainMC);
  }
  












  public BigDecimal subtract(BigDecimal rhs, MathContext set)
  {
    if (lostDigits) {
      checkdigits(rhs, digits);
    }
    

    BigDecimal newrhs = clone(rhs);
    ind = ((byte)-ind);
    return add(newrhs, set);
  }
  














  public byte byteValueExact()
  {
    int num = intValueExact();
    if (((num > 127 ? 1 : 0) | (num < -128 ? 1 : 0)) != 0)
      throw new ArithmeticException("Conversion overflow: " + toString());
    return (byte)num;
  }
  














  public double doubleValue()
  {
    return Double.valueOf(toString()).doubleValue();
  }
  




















  public boolean equals(Object obj)
  {
    int i = 0;
    char[] lca = null;
    char[] rca = null;
    
    if (obj == null)
      return false;
    if (!(obj instanceof BigDecimal))
      return false;
    BigDecimal rhs = (BigDecimal)obj;
    if (ind != ind)
      return false;
    if (((mant.length == mant.length ? 1 : 0) & (exp == exp ? 1 : 0) & (form == form ? 1 : 0)) != 0)
    {



      int $8 = mant.length;
      for (i = 0; 
          $8 > 0; i++) {
        if (mant[i] != mant[i]) {
          return false;
        }
        $8--;
      }
      
    }
    else
    {
      lca = layout();
      rca = rhs.layout();
      if (lca.length != rca.length) {
        return false;
      }
      
      int $9 = lca.length;
      for (i = 0; 
          $9 > 0; i++) {
        if (lca[i] != rca[i]) {
          return false;
        }
        $9--;
      }
    }
    


    return true;
  }
  












  public float floatValue()
  {
    return Float.valueOf(toString()).floatValue();
  }
  







































  public String format(int before, int after)
  {
    return format(before, after, -1, -1, 1, 4);
  }
  






































































  public String format(int before, int after, int explaces, int exdigits, int exformint, int exround)
  {
    int mag = 0;
    int thisafter = 0;
    int lead = 0;
    byte[] newmant = null;
    int chop = 0;
    int need = 0;
    int oldexp = 0;
    
    int p = 0;
    char[] newa = null;
    int i = 0;
    int places = 0;
    

    if (((before < -1 ? 1 : 0) | (before == 0 ? 1 : 0)) != 0)
      badarg("format", 1, String.valueOf(before));
    if (after < -1)
      badarg("format", 2, String.valueOf(after));
    if (((explaces < -1 ? 1 : 0) | (explaces == 0 ? 1 : 0)) != 0)
      badarg("format", 3, String.valueOf(explaces));
    if (exdigits < -1) {
      badarg("format", 4, String.valueOf(explaces));
    }
    if ((exformint != 1) && 
      (exformint != 2)) {
      if (exformint == -1) {
        exformint = 1;
      }
      else {
        badarg("format", 5, String.valueOf(exformint));
      }
    }
    

    if (exround != 4) {
      try {
        if (exround == -1) {
          exround = 4;
        } else
          new MathContext(9, 1, false, exround);
      } catch (IllegalArgumentException $10) {
        badarg("format", 6, String.valueOf(exround));
      }
    }
    
    BigDecimal num = clone(this);
    









    if (exdigits == -1) {
      form = 0;
    } else if (ind == 0) {
      form = 0;
    }
    else {
      mag = exp + mant.length;
      if (mag > exdigits) {
        form = ((byte)exformint);
      } else if (mag < -5) {
        form = ((byte)exformint);
      } else {
        form = 0;
      }
    }
    





    if (after >= 0)
    {
      for (;;)
      {
        if (form == 0) {
          thisafter = -exp;
        } else if (form == 1) {
          thisafter = mant.length - 1;
        } else {
          lead = (exp + mant.length - 1) % 3;
          if (lead < 0)
            lead = 3 + lead;
          lead++;
          if (lead >= mant.length) {
            thisafter = 0;
          } else {
            thisafter = mant.length - lead;
          }
        }
        if (thisafter != after)
        {
          if (thisafter < after)
          {
            newmant = extend(mant, mant.length + after - thisafter);
            mant = newmant;
            exp -= after - thisafter;
            if (exp < -999999999) {
              throw new ArithmeticException("Exponent Overflow: " + exp);
            }
            
          }
          else
          {
            chop = thisafter - after;
            if (chop > mant.length)
            {
              mant = ZEROmant;
              ind = 0;
              exp = 0;

            }
            else
            {
              need = mant.length - chop;
              oldexp = exp;
              num.round(need, exround);
              

              if (exp - oldexp == chop)
                break;
            }
          } }
      }
    }
    char[] a = num.layout();
    


    if (before > 0)
    {

      int $11 = a.length;
      for (p = 0; 
          $11 > 0; p++) {
        if (a[p] == '.')
          break;
        if (a[p] == 'E') {
          break;
        }
        $11--;
      }
      






      if (p > before)
        badarg("format", 1, String.valueOf(before));
      if (p < before) {
        newa = new char[a.length + before - p];
        
        int $12 = before - p;
        for (i = 0; 
            $12 > 0; i++) {
          newa[i] = ' ';$12--;
        }
        
        System.arraycopy(a, 0, newa, i, a.length);
        a = newa;
      }
    }
    

    if (explaces > 0)
    {

      int $13 = a.length - 1;
      for (p = a.length - 1; 
          $13 > 0; p--) {
        if (a[p] == 'E') {
          break;
        }
        $13--;
      }
      



      if (p == 0) {
        newa = new char[a.length + explaces + 2];
        System.arraycopy(a, 0, newa, 0, a.length);
        
        int $14 = explaces + 2;
        for (i = a.length; 
            $14 > 0; i++) {
          newa[i] = ' ';$14--;
        }
        
        a = newa;
      } else {
        places = a.length - p - 2;
        if (places > explaces)
          badarg("format", 3, String.valueOf(explaces));
        if (places < explaces) {
          newa = new char[a.length + explaces - places];
          System.arraycopy(a, 0, newa, 0, p + 2);
          

          int $15 = explaces - places;
          for (i = p + 2; 
              $15 > 0; i++) {
            newa[i] = '0';$15--;
          }
          
          System.arraycopy(a, p + 2, newa, i, places);
          

          a = newa;
        }
      }
    }
    
    return new String(a);
  }
  













  public int hashCode()
  {
    return toString().hashCode();
  }
  










  public int intValue()
  {
    return toBigInteger().intValue();
  }
  










  public int intValueExact()
  {
    int useexp = 0;
    
    int i = 0;
    int topdig = 0;
    


    if (ind == 0) {
      return 0;
    }
    int lodigit = mant.length - 1;
    if (exp < 0) {
      lodigit += exp;
      
      if (!allzero(mant, lodigit + 1))
        throw new ArithmeticException("Decimal part non-zero: " + toString());
      if (lodigit < 0)
        return 0;
      useexp = 0;
    } else {
      if (exp + lodigit > 9)
        throw new ArithmeticException("Conversion overflow: " + toString());
      useexp = exp;
    }
    
    int result = 0;
    
    int $16 = lodigit + useexp;
    for (i = 0; 
        i <= $16; i++) {
      result *= 10;
      if (i <= lodigit) {
        result += mant[i];
      }
    }
    

    if (lodigit + useexp == 9)
    {

      topdig = result / 1000000000;
      if (topdig != mant[0])
      {
        if ((result == Integer.MIN_VALUE) && 
          (ind == -1) && 
          (mant[0] == 2))
          return result;
        throw new ArithmeticException("Conversion overflow: " + toString());
      }
    }
    

    if (ind == 1)
      return result;
    return -result;
  }
  










  public long longValue()
  {
    return toBigInteger().longValue();
  }
  










  public long longValueExact()
  {
    int cstart = 0;
    int useexp = 0;
    
    int i = 0;
    long topdig = 0L;
    
    if (ind == 0)
      return 0L;
    int lodigit = mant.length - 1;
    if (exp < 0) {
      lodigit += exp;
      
      if (lodigit < 0) {
        cstart = 0;
      } else
        cstart = lodigit + 1;
      if (!allzero(mant, cstart))
        throw new ArithmeticException("Decimal part non-zero: " + toString());
      if (lodigit < 0)
        return 0L;
      useexp = 0;
    } else {
      if (exp + mant.length > 18)
        throw new ArithmeticException("Conversion overflow: " + toString());
      useexp = exp;
    }
    




    long result = 0L;
    
    int $17 = lodigit + useexp;
    for (i = 0; 
        i <= $17; i++) {
      result *= 10L;
      if (i <= lodigit) {
        result += mant[i];
      }
    }
    

    if (lodigit + useexp == 18) {
      topdig = result / 1000000000000000000L;
      if (topdig != mant[0])
      {
        if ((result == Long.MIN_VALUE) && 
          (ind == -1) && 
          (mant[0] == 9))
          return result;
        throw new ArithmeticException("Conversion overflow: " + toString());
      }
    }
    

    if (ind == 1)
      return result;
    return -result;
  }
  

















  public BigDecimal movePointLeft(int n)
  {
    BigDecimal res = clone(this);
    exp -= n;
    return res.finish(plainMC, false);
  }
  
















  public BigDecimal movePointRight(int n)
  {
    BigDecimal res = clone(this);
    exp += n;
    return res.finish(plainMC, false);
  }
  








  public int scale()
  {
    if (exp >= 0)
      return 0;
    return -exp;
  }
  



















  public BigDecimal setScale(int scale)
  {
    return setScale(scale, 7);
  }
  

























  public BigDecimal setScale(int scale, int round)
  {
    int padding = 0;
    int newlen = 0;
    

    int ourscale = scale();
    if ((ourscale == scale) && 
      (form == 0))
      return this;
    BigDecimal res = clone(this);
    if (ourscale <= scale)
    {
      if (ourscale == 0) {
        padding = exp + scale;
      } else
        padding = scale - ourscale;
      mant = extend(mant, mant.length + padding);
      exp = (-scale);
    } else {
      if (scale < 0) {
        throw new ArithmeticException("Negative scale: " + scale);
      }
      newlen = mant.length - (ourscale - scale);
      res = res.round(newlen, round);
      

      if (exp != -scale) {
        mant = extend(mant, mant.length + 1);
        exp -= 1;
      }
    }
    form = 0;
    return res;
  }
  










  public short shortValueExact()
  {
    int num = intValueExact();
    if (((num > 32767 ? 1 : 0) | (num < 32768 ? 1 : 0)) != 0)
      throw new ArithmeticException("Conversion overflow: " + toString());
    return (short)num;
  }
  









  public int signum()
  {
    return ind;
  }
  













  public java.math.BigDecimal toBigDecimal()
  {
    return new java.math.BigDecimal(unscaledValue(), scale());
  }
  









  public BigInteger toBigInteger()
  {
    BigDecimal res = null;
    int newlen = 0;
    byte[] newmant = null;
    
    if (((exp >= 0 ? 1 : 0) & (form == 0 ? 1 : 0)) != 0) {
      res = this;
    } else if (exp >= 0) {
      res = clone(this);
      form = 0;


    }
    else if (-exp >= mant.length) {
      res = ZERO;
    } else {
      res = clone(this);
      newlen = mant.length + exp;
      newmant = new byte[newlen];
      System.arraycopy(mant, 0, newmant, 0, newlen);
      
      mant = newmant;
      form = 0;
      exp = 0;
    }
    


    return new BigInteger(new String(res.layout()));
  }
  










  public BigInteger toBigIntegerExact()
  {
    if (exp < 0)
    {
      if (!allzero(mant, mant.length + exp))
        throw new ArithmeticException("Decimal part non-zero: " + toString());
    }
    return toBigInteger();
  }
  








  public char[] toCharArray()
  {
    return layout();
  }
  














  public String toString()
  {
    return new String(layout());
  }
  









  public BigInteger unscaledValue()
  {
    BigDecimal res = null;
    if (exp >= 0) {
      res = this;
    } else {
      res = clone(this);
      exp = 0;
    }
    return res.toBigInteger();
  }
  



















  public static BigDecimal valueOf(double dub)
  {
    return new BigDecimal(new Double(dub).toString());
  }
  








  public static BigDecimal valueOf(long lint)
  {
    return valueOf(lint, 0);
  }
  

















  public static BigDecimal valueOf(long lint, int scale)
  {
    BigDecimal res = null;
    
    if (lint == 0L) {
      res = ZERO;
    } else if (lint == 1L) {
      res = ONE;
    } else if (lint == 10L) {
      res = TEN;
    } else {
      res = new BigDecimal(lint);
    }
    
    if (scale == 0)
      return res;
    if (scale < 0)
      throw new NumberFormatException("Negative scale: " + scale);
    res = clone(res);
    exp = (-scale);
    return res;
  }
  












  private char[] layout()
  {
    int i = 0;
    StringBuilder sb = null;
    int euse = 0;
    int sig = 0;
    char csign = '\000';
    char[] rec = null;
    

    int len = 0;
    char[] cmant = new char[mant.length];
    
    int $18 = mant.length;
    for (i = 0; 
        $18 > 0; i++) {
      cmant[i] = ((char)(mant[i] + 48));$18--;
    }
    

    if (form != 0) {
      sb = new StringBuilder(cmant.length + 15);
      if (ind == -1)
        sb.append('-');
      euse = exp + cmant.length - 1;
      
      if (form == 1) {
        sb.append(cmant[0]);
        if (cmant.length > 1) {
          sb.append('.').append(cmant, 1, cmant.length - 1);
        }
      } else {
        sig = euse % 3;
        if (sig < 0)
          sig = 3 + sig;
        euse -= sig;
        sig++;
        if (sig >= cmant.length) {
          sb.append(cmant, 0, cmant.length);
          
          for (int $19 = sig - cmant.length; 
              $19 > 0; $19--) {
            sb.append('0');
          }
        }
        else {
          sb.append(cmant, 0, sig).append('.').append(cmant, sig, cmant.length - sig);
        }
      }
      
      if (euse != 0) {
        if (euse < 0) {
          csign = '-';
          euse = -euse;
        } else {
          csign = '+'; }
        sb.append('E').append(csign).append(euse);
      }
      rec = new char[sb.length()];
      int srcEnd = sb.length();
      if (0 != srcEnd) {
        sb.getChars(0, srcEnd, rec, 0);
      }
      return rec;
    }
    

    if (exp == 0) {
      if (ind >= 0)
        return cmant;
      rec = new char[cmant.length + 1];
      rec[0] = '-';
      System.arraycopy(cmant, 0, rec, 1, cmant.length);
      return rec;
    }
    

    int needsign = ind == -1 ? 1 : 0;
    



    int mag = exp + cmant.length;
    
    if (mag < 1) {
      len = needsign + 2 - exp;
      rec = new char[len];
      if (needsign != 0)
        rec[0] = '-';
      rec[needsign] = '0';
      rec[(needsign + 1)] = '.';
      
      int $20 = -mag;
      for (i = needsign + 2; 
          $20 > 0; i++) {
        rec[i] = '0';$20--;
      }
      
      System.arraycopy(cmant, 0, rec, needsign + 2 - mag, cmant.length);
      
      return rec;
    }
    
    if (mag > cmant.length) {
      len = needsign + mag;
      rec = new char[len];
      if (needsign != 0)
        rec[0] = '-';
      System.arraycopy(cmant, 0, rec, needsign, cmant.length);
      
      int $21 = mag - cmant.length;
      for (i = needsign + cmant.length; 
          $21 > 0; i++) {
        rec[i] = '0';$21--;
      }
      
      return rec;
    }
    

    len = needsign + 1 + cmant.length;
    rec = new char[len];
    if (needsign != 0)
      rec[0] = '-';
    System.arraycopy(cmant, 0, rec, needsign, mag);
    rec[(needsign + mag)] = '.';
    System.arraycopy(cmant, mag, rec, needsign + mag + 1, cmant.length - mag);
    
    return rec;
  }
  





  private int intcheck(int min, int max)
  {
    int i = intValueExact();
    
    if (((i < min ? 1 : 0) | (i > max ? 1 : 0)) != 0)
      throw new ArithmeticException("Conversion overflow: " + i);
    return i;
  }
  































  private BigDecimal dodivide(char code, BigDecimal rhs, MathContext set, int scale)
  {
    int thisdigit = 0;
    int i = 0;
    byte v2 = 0;
    int ba = 0;
    int mult = 0;
    int start = 0;
    int padding = 0;
    int d = 0;
    byte[] newvar1 = null;
    byte lasthave = 0;
    int actdig = 0;
    byte[] newmant = null;
    
    if (lostDigits)
      checkdigits(rhs, digits);
    BigDecimal lhs = this;
    

    if (ind == 0)
      throw new ArithmeticException("Divide by 0");
    if (ind == 0) {
      if (form != 0)
        return ZERO;
      if (scale == -1)
        return lhs;
      return lhs.setScale(scale);
    }
    

    int reqdig = digits;
    if (reqdig > 0) {
      if (mant.length > reqdig)
        lhs = clone(lhs).round(set);
      if (mant.length > reqdig)
        rhs = clone(rhs).round(set);
    } else {
      if (scale == -1) {
        scale = lhs.scale();
      }
      reqdig = mant.length;
      
      if (scale != -exp)
        reqdig = reqdig + scale + exp;
      reqdig = reqdig - (mant.length - 1) - exp;
      if (reqdig < mant.length)
        reqdig = mant.length;
      if (reqdig < mant.length) {
        reqdig = mant.length;
      }
    }
    
    int newexp = exp - exp + mant.length - mant.length;
    
    if ((newexp < 0) && 
      (code != 'D')) {
      if (code == 'I') {
        return ZERO;
      }
      return clone(lhs).finish(set, false);
    }
    

    BigDecimal res = new BigDecimal();
    ind = ((byte)(ind * ind));
    exp = newexp;
    mant = new byte[reqdig + 1];
    


    int newlen = reqdig + reqdig + 1;
    byte[] var1 = extend(mant, newlen);
    int var1len = newlen;
    
    byte[] var2 = mant;
    int var2len = newlen;
    

    int b2b = var2[0] * 10 + 1;
    if (var2.length > 1) {
      b2b += var2[1];
    }
    
    int have = 0;
    for (;;)
    {
      thisdigit = 0;
      


      while (var1len >= var2len)
      {
        if (var1len == var2len)
        {


          int $22 = var1len;
          for (i = 0; 
              $22 > 0; i++)
          {
            if (i < var2.length) {
              v2 = var2[i];
            } else
              v2 = 0;
            if (var1[i] < v2)
              break label683;
            if (var1[i] > v2) {
              break label539;
            }
            $22--;
          }
          














          thisdigit++;
          mant[have] = ((byte)thisdigit);
          have++;
          var1[0] = 0;
          
          break label797;
          
          label539:
          
          ba = var1[0];
        }
        else
        {
          ba = var1[0] * 10;
          if (var1len > 1) {
            ba += var1[1];
          }
        }
        mult = ba * 10 / b2b;
        if (mult == 0)
          mult = 1;
        thisdigit += mult;
        
        var1 = byteaddsub(var1, var1len, var2, var2len, -mult, true);
        if (var1[0] == 0)
        {





          int $23 = var1len - 2;
          for (start = 0; 
              start <= $23; start++) {
            if (var1[start] != 0)
              break;
            var1len--;
          }
          
          if (start != 0)
          {

            System.arraycopy(var1, start, var1, 0, var1len);
          }
        }
      }
      label683:
      if (((have != 0 ? 1 : 0) | (thisdigit != 0 ? 1 : 0)) != 0) {
        mant[have] = ((byte)thisdigit);
        have++;
        if (have == reqdig + 1)
          break;
        if (var1[0] == 0) {
          break;
        }
      }
      if ((scale >= 0) && 
        (-exp > scale)) {
        break;
      }
      if ((code != 'D') && 
        (exp <= 0))
        break;
      exp -= 1;
      


      var2len--;
    }
    

    label797:
    
    if (have == 0) {
      have = 1;
    }
    if (((code == 'I' ? 1 : 0) | (code == 'R' ? 1 : 0)) != 0) {
      if (have + exp > reqdig) {
        throw new ArithmeticException("Integer overflow");
      }
      if (code == 'R')
      {

        if (mant[0] == 0)
          return clone(lhs).finish(set, false);
        if (var1[0] == 0)
          return ZERO;
        ind = ind;
        



        padding = reqdig + reqdig + 1 - mant.length;
        exp = (exp - padding + exp);
        



        d = var1len;
        
        for (i = d - 1; 
            i >= 1; i--) {
          if (((exp < exp ? 1 : 0) & (exp < exp ? 1 : 0)) == 0)
            break;
          if (var1[i] != 0)
            break;
          d--;
          exp += 1;
        }
        
        if (d < var1.length) {
          newvar1 = new byte[d];
          System.arraycopy(var1, 0, newvar1, 0, d);
          var1 = newvar1;
        }
        mant = var1;
        return res.finish(set, false);



      }
      



    }
    else if (var1[0] != 0) {
      lasthave = mant[(have - 1)];
      if (lasthave % 5 == 0) {
        mant[(have - 1)] = ((byte)(lasthave + 1));
      }
    }
    


    if (scale >= 0)
    {

      if (have != mant.length)
      {
        exp -= mant.length - have;
      }
      actdig = mant.length - (-exp - scale);
      res.round(actdig, roundingMode);
      

      if (exp != -scale) {
        mant = extend(mant, mant.length + 1);
        exp -= 1;
      }
      return res.finish(set, true);
    }
    


    if (have == mant.length) {
      res.round(set);
      have = reqdig;
    } else {
      if (mant[0] == 0) {
        return ZERO;
      }
      

      newmant = new byte[have];
      System.arraycopy(mant, 0, newmant, 0, have);
      mant = newmant;
    }
    return res.finish(set, true);
  }
  

  private void bad(char[] s)
  {
    throw new NumberFormatException("Not a number: " + String.valueOf(s));
  }
  




  private void badarg(String name, int pos, String value)
  {
    throw new IllegalArgumentException("Bad argument " + pos + " " + "to" + " " + name + ":" + " " + value);
  }
  








  private static final byte[] extend(byte[] inarr, int newlen)
  {
    if (inarr.length == newlen)
      return inarr;
    byte[] newarr = new byte[newlen];
    System.arraycopy(inarr, 0, newarr, 0, inarr.length);
    
    return newarr;
  }
  






























  private static final byte[] byteaddsub(byte[] a, int avlen, byte[] b, int bvlen, int m, boolean reuse)
  {
    int op = 0;
    int dp90 = 0;
    
    int i = 0;
    

    int alength = a.length;
    int blength = b.length;
    int ap = avlen - 1;
    int bp = bvlen - 1;
    int maxarr = bp;
    if (maxarr < ap)
      maxarr = ap;
    byte[] reb = (byte[])null;
    if ((reuse) && 
      (maxarr + 1 == alength))
      reb = a;
    if (reb == null) {
      reb = new byte[maxarr + 1];
    }
    boolean quickm = false;
    if (m == 1) {
      quickm = true;
    } else if (m == -1) {
      quickm = true;
    }
    int digit = 0;
    
    for (op = maxarr; 
        op >= 0; op--) {
      if (ap >= 0) {
        if (ap < alength)
          digit += a[ap];
        ap--;
      }
      if (bp >= 0) {
        if (bp < blength) {
          if (quickm) {
            if (m > 0) {
              digit += b[bp];
            } else
              digit -= b[bp];
          } else
            digit += b[bp] * m;
        }
        bp--;
      }
      
      if ((digit < 10) && 
        (digit >= 0))
      {
        reb[op] = ((byte)digit);
        digit = 0;
      }
      else
      {
        dp90 = digit + 90;
        reb[op] = bytedig[dp90];
        digit = bytecar[dp90];
      }
    }
    
    if (digit == 0) {
      return reb;
    }
    


    byte[] newarr = (byte[])null;
    if ((reuse) && 
      (maxarr + 2 == a.length))
      newarr = a;
    if (newarr == null)
      newarr = new byte[maxarr + 2];
    newarr[0] = ((byte)digit);
    
    if (maxarr < 10) {
      int $24 = maxarr + 1;
      for (i = 0; 
          $24 > 0; i++) {
        newarr[(i + 1)] = reb[i];$24--;
      }
    }
    else {
      System.arraycopy(reb, 0, newarr, 1, maxarr + 1); }
    return newarr;
  }
  





  private static final byte[] diginit()
  {
    int op = 0;
    int digit = 0;
    byte[] work = new byte['¾'];
    
    for (op = 0; 
        op <= 189; op++) {
      digit = op - 90;
      if (digit >= 0) {
        work[op] = ((byte)(digit % 10));
        bytecar[op] = ((byte)(digit / 10));
      }
      else
      {
        digit += 100;
        work[op] = ((byte)(digit % 10));
        bytecar[op] = ((byte)(digit / 10 - 10));
      }
    }
    return work;
  }
  





  private static final BigDecimal clone(BigDecimal dec)
  {
    BigDecimal copy = new BigDecimal();
    ind = ind;
    exp = exp;
    form = form;
    mant = mant;
    return copy;
  }
  




  private void checkdigits(BigDecimal rhs, int dig)
  {
    if (dig == 0) {
      return;
    }
    if ((mant.length > dig) && 
      (!allzero(mant, dig)))
      throw new ArithmeticException("Too many digits: " + toString());
    if (rhs == null)
      return;
    if ((mant.length > dig) && 
      (!allzero(mant, dig))) {
      throw new ArithmeticException("Too many digits: " + rhs.toString());
    }
  }
  



  private BigDecimal round(MathContext set)
  {
    return round(digits, roundingMode);
  }
  












  private BigDecimal round(int len, int mode)
  {
    boolean reuse = false;
    byte first = 0;
    
    byte[] newmant = null;
    int adjust = mant.length - len;
    if (adjust <= 0) {
      return this;
    }
    exp += adjust;
    int sign = ind;
    byte[] oldmant = mant;
    if (len > 0)
    {
      mant = new byte[len];
      System.arraycopy(oldmant, 0, mant, 0, len);
      reuse = true;
      first = oldmant[len];
    } else {
      mant = ZEROmant;
      ind = 0;
      reuse = false;
      if (len == 0) {
        first = oldmant[0];
      } else {
        first = 0;
      }
    }
    
    int increment = 0;
    

    if (mode == 4) {
      if (first >= 5)
        increment = sign;
    } else if (mode == 7)
    {
      if (!allzero(oldmant, len))
        throw new ArithmeticException("Rounding necessary");
    } else if (mode == 5) {
      if (first > 5) {
        increment = sign;
      } else if ((first == 5) && 
        (!allzero(oldmant, len + 1)))
        increment = sign;
    } else if (mode == 6) {
      if (first > 5) {
        increment = sign;
      } else if (first == 5) {
        if (!allzero(oldmant, len + 1)) {
          increment = sign;
        }
        else if (mant[(mant.length - 1)] % 2 != 0)
          increment = sign;
      }
    } else if (mode != 1)
    {
      if (mode == 0) {
        if (!allzero(oldmant, len))
          increment = sign;
      } else if (mode == 2) {
        if ((sign > 0) && 
          (!allzero(oldmant, len)))
          increment = sign;
      } else if (mode == 3) {
        if ((sign < 0) && 
          (!allzero(oldmant, len)))
          increment = sign;
      } else {
        throw new IllegalArgumentException("Bad round value: " + mode);
      }
    }
    

    if (increment != 0)
    {
      if (ind == 0)
      {
        mant = ONEmant;
        ind = ((byte)increment);
      }
      else {
        if (ind == -1)
          increment = -increment;
        newmant = byteaddsub(mant, mant.length, ONEmant, 1, increment, reuse);
        if (newmant.length > mant.length)
        {
          exp += 1;
          
          System.arraycopy(newmant, 0, mant, 0, mant.length);
        }
        else {
          mant = newmant;
        }
      }
    }
    
    if (exp > 999999999)
      throw new ArithmeticException("Exponent Overflow: " + exp);
    return this;
  }
  






  private static final boolean allzero(byte[] array, int start)
  {
    int i = 0;
    if (start < 0) {
      start = 0;
    }
    int $25 = array.length - 1;
    for (i = start; 
        i <= $25; i++) {
      if (array[i] != 0) {
        return false;
      }
    }
    return true;
  }
  








  private BigDecimal finish(MathContext set, boolean strip)
  {
    int d = 0;
    int i = 0;
    byte[] newmant = null;
    int mag = 0;
    int sig = 0;
    
    if ((digits != 0) && 
      (mant.length > digits)) {
      round(set);
    }
    


    if ((strip) && 
      (form != 0)) {
      d = mant.length;
      

      for (i = d - 1; 
          i >= 1; i--) {
        if (mant[i] != 0)
          break;
        d--;
        exp += 1;
      }
      
      if (d < mant.length) {
        newmant = new byte[d];
        System.arraycopy(mant, 0, newmant, 0, d);
        mant = newmant;
      }
    }
    
    form = 0;
    


    int $26 = mant.length;
    for (i = 0; 
        $26 > 0; i++) {
      if (mant[i] != 0)
      {

        if (i > 0)
        {
          newmant = new byte[mant.length - i];
          System.arraycopy(mant, i, newmant, 0, mant.length - i);
          
          mant = newmant;
        }
        

        mag = exp + mant.length;
        if (mag > 0) {
          if ((mag > digits) && 
            (digits != 0))
            form = ((byte)form);
          if (mag - 1 <= 999999999)
            return this;
        } else if (mag < -5) {
          form = ((byte)form);
        }
        mag--;
        if (((mag < -999999999 ? 1 : 0) | (mag > 999999999 ? 1 : 0)) != 0)
        {

          if (form == 2) {
            sig = mag % 3;
            if (sig < 0)
              sig = 3 + sig;
            mag -= sig;
            
            if ((mag >= -999999999) && 
              (mag <= 999999999)) {}
          }
          else {
            throw new ArithmeticException("Exponent Overflow: " + mag);
          }
        }
        return this;
      }
      $26--;
    }
    











































    ind = 0;
    
    if (form != 0) {
      exp = 0;
    } else if (exp > 0) {
      exp = 0;

    }
    else if (exp < -999999999) {
      throw new ArithmeticException("Exponent Overflow: " + exp);
    }
    
    mant = ZEROmant;
    return this;
  }
}
