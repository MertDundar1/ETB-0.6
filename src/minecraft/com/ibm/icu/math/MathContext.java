package com.ibm.icu.math;

import java.io.Serializable;



























































































































































































































































































public final class MathContext
  implements Serializable
{
  public static final int PLAIN = 0;
  public static final int SCIENTIFIC = 1;
  public static final int ENGINEERING = 2;
  public static final int ROUND_CEILING = 2;
  public static final int ROUND_DOWN = 1;
  public static final int ROUND_FLOOR = 3;
  public static final int ROUND_HALF_DOWN = 5;
  public static final int ROUND_HALF_EVEN = 6;
  public static final int ROUND_HALF_UP = 4;
  public static final int ROUND_UNNECESSARY = 7;
  public static final int ROUND_UP = 0;
  int digits;
  int form;
  boolean lostDigits;
  int roundingMode;
  private static final int DEFAULT_FORM = 1;
  private static final int DEFAULT_DIGITS = 9;
  private static final boolean DEFAULT_LOSTDIGITS = false;
  private static final int DEFAULT_ROUNDINGMODE = 4;
  private static final int MIN_DIGITS = 0;
  private static final int MAX_DIGITS = 999999999;
  private static final int[] ROUNDS = { 4, 7, 2, 1, 3, 5, 6, 0 };
  

  private static final String[] ROUNDWORDS = { "ROUND_HALF_UP", "ROUND_UNNECESSARY", "ROUND_CEILING", "ROUND_DOWN", "ROUND_FLOOR", "ROUND_HALF_DOWN", "ROUND_HALF_EVEN", "ROUND_UP" };
  









  private static final long serialVersionUID = 7163376998892515376L;
  








  public static final MathContext DEFAULT = new MathContext(9, 1, false, 4);
  




















  public MathContext(int setdigits)
  {
    this(setdigits, 1, false, 4);
  }
  




















  public MathContext(int setdigits, int setform)
  {
    this(setdigits, setform, false, 4);
  }
  





















  public MathContext(int setdigits, int setform, boolean setlostdigits)
  {
    this(setdigits, setform, setlostdigits, 4);
  }
  
























  public MathContext(int setdigits, int setform, boolean setlostdigits, int setroundingmode)
  {
    if (setdigits != 9)
    {
      if (setdigits < 0)
        throw new IllegalArgumentException("Digits too small: " + setdigits);
      if (setdigits > 999999999) {
        throw new IllegalArgumentException("Digits too large: " + setdigits);
      }
    }
    if (setform != 1)
    {
      if ((setform != 2) && 
        (setform != 0))
      {
        throw new IllegalArgumentException("Bad form value: " + setform);
      }
    }
    if (!isValidRound(setroundingmode))
      throw new IllegalArgumentException("Bad roundingMode value: " + setroundingmode);
    digits = setdigits;
    form = setform;
    lostDigits = setlostdigits;
    roundingMode = setroundingmode;
  }
  








  public int getDigits()
  {
    return digits;
  }
  










  public int getForm()
  {
    return form;
  }
  









  public boolean getLostDigits()
  {
    return lostDigits;
  }
  
















  public int getRoundingMode()
  {
    return roundingMode;
  }
  





































  public String toString()
  {
    String formstr = null;
    int r = 0;
    String roundword = null;
    
    if (form == 1) {
      formstr = "SCIENTIFIC";
    } else if (form == 2) {
      formstr = "ENGINEERING";
    } else {
      formstr = "PLAIN";
    }
    
    int $1 = ROUNDS.length; for (r = 0; $1 > 0; r++) {
      if (roundingMode == ROUNDS[r])
      {
        roundword = ROUNDWORDS[r];
        break;
      }
      $1--;
    }
    





    return "digits=" + digits + " " + "form=" + formstr + " " + "lostDigits=" + (lostDigits ? "1" : "0") + " " + "roundingMode=" + roundword;
  }
  



  private static boolean isValidRound(int testround)
  {
    int r = 0;
    int $2 = ROUNDS.length; for (r = 0; $2 > 0; r++) {
      if (testround == ROUNDS[r]) {
        return true;
      }
      $2--;
    }
    


    return false;
  }
}
