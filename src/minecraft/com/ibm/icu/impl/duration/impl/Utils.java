package com.ibm.icu.impl.duration.impl;

import java.util.Locale;




public class Utils
{
  public Utils() {}
  
  public static final Locale localeFromString(String s)
  {
    String language = s;
    String region = "";
    String variant = "";
    
    int x = language.indexOf("_");
    if (x != -1) {
      region = language.substring(x + 1);
      language = language.substring(0, x);
    }
    x = region.indexOf("_");
    if (x != -1) {
      variant = region.substring(x + 1);
      region = region.substring(0, x);
    }
    return new Locale(language, region, variant);
  }
  




















  public static String chineseNumber(long n, ChineseDigits zh)
  {
    if (n < 0L) {
      n = -n;
    }
    if (n <= 10L) {
      if (n == 2L) {
        return String.valueOf(liang);
      }
      return String.valueOf(digits[((int)n)]);
    }
    

    char[] buf = new char[40];
    char[] digits = String.valueOf(n).toCharArray();
    



    boolean inZero = true;
    boolean forcedZero = false;
    int x = buf.length;
    int i = digits.length;int u = -1;int l = -1; for (;;) { i--; if (i < 0) break;
      if (u == -1) {
        if (l != -1) {
          buf[(--x)] = levels[l];
          inZero = true;
          forcedZero = false;
        }
        u++;
      } else {
        buf[(--x)] = units[(u++)];
        if (u == 3) {
          u = -1;
          l++;
        }
      }
      int d = digits[i] - '0';
      if (d == 0) {
        if ((x < buf.length - 1) && (u != 0)) {
          buf[x] = '*';
        }
        if ((inZero) || (forcedZero)) {
          buf[(--x)] = '*';
        } else {
          buf[(--x)] = digits[0];
          inZero = true;
          forcedZero = u == 1;
        }
      } else {
        inZero = false;
        buf[(--x)] = digits[d];
      }
    }
    



    if (n > 1000000L) {
      boolean last = true;
      int i = buf.length - 3;
      do {
        if (buf[i] == '0') {
          break;
        }
        i -= 8;
        last = !last;
      } while (i > x);
      
      i = buf.length - 7;
      do {
        if ((buf[i] == digits[0]) && (!last)) {
          buf[i] = '*';
        }
        i -= 8;
        last = !last;
      } while (i > x);
      

      if (n >= 100000000L) {
        i = buf.length - 8;
        do {
          boolean empty = true;
          int j = i - 1; for (int e = Math.max(x - 1, i - 8); j > e; j--) {
            if (buf[j] != '*') {
              empty = false;
              break;
            }
          }
          if (empty) {
            if ((buf[(i + 1)] != '*') && (buf[(i + 1)] != digits[0])) {
              buf[i] = digits[0];
            } else {
              buf[i] = '*';
            }
          }
          i -= 8;
        } while (i > x);
      }
    }
    

    for (int i = x; i < buf.length; i++) {
      if ((buf[i] == digits[2]) && 
        ((i >= buf.length - 1) || (buf[(i + 1)] != units[0])) && (
        (i <= x) || ((buf[(i - 1)] != units[0]) && (buf[(i - 1)] != digits[0]) && (buf[(i - 1)] != '*'))))
      {
        buf[i] = liang;
      }
    }
    
    if ((buf[x] == digits[1]) && ((ko) || (buf[(x + 1)] == units[0]))) {
      x++;
    }
    

    int w = x;
    for (int r = x; r < buf.length; r++) {
      if (buf[r] != '*') {
        buf[(w++)] = buf[r];
      }
    }
    return new String(buf, x, w - x);
  }
  


  public static class ChineseDigits
  {
    final char[] digits;
    

    final char[] units;
    
    final char[] levels;
    
    final char liang;
    
    final boolean ko;
    

    ChineseDigits(String digits, String units, String levels, char liang, boolean ko)
    {
      this.digits = digits.toCharArray();
      this.units = units.toCharArray();
      this.levels = levels.toCharArray();
      this.liang = liang;
      this.ko = ko;
    }
    
    public static final ChineseDigits DEBUG = new ChineseDigits("0123456789s", "sbq", "WYZ", 'L', false);
    

    public static final ChineseDigits TRADITIONAL = new ChineseDigits("零一二三四五六七八九十", "十百千", "萬億兆", '兩', false);
    




    public static final ChineseDigits SIMPLIFIED = new ChineseDigits("零一二三四五六七八九十", "十百千", "万亿兆", '两', false);
    






    public static final ChineseDigits KOREAN = new ChineseDigits("영일이삼사오육칠팔구십", "십백천", "만억?", 51060, true);
  }
}
