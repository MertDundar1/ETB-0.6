package com.ibm.icu.util;

import com.ibm.icu.lang.UCharacter;














public class CaseInsensitiveString
{
  private String string;
  private int hash = 0;
  
  private String folded = null;
  
  private static String foldCase(String foldee)
  {
    return UCharacter.foldCase(foldee, true);
  }
  
  private void getFolded()
  {
    if (folded == null) {
      folded = foldCase(string);
    }
  }
  




  public CaseInsensitiveString(String s)
  {
    string = s;
  }
  



  public String getString()
  {
    return string;
  }
  



  public boolean equals(Object o)
  {
    if (o == null) {
      return false;
    }
    if (this == o) {
      return true;
    }
    getFolded();
    try {
      CaseInsensitiveString cis = (CaseInsensitiveString)o;
      
      cis.getFolded();
      
      return folded.equals(folded);
    } catch (ClassCastException e) {
      try {
        String s = (String)o;
        
        return folded.equals(foldCase(s));
      } catch (ClassCastException e2) {} }
    return false;
  }
  






  public int hashCode()
  {
    getFolded();
    
    if (hash == 0) {
      hash = folded.hashCode();
    }
    
    return hash;
  }
  



  public String toString()
  {
    return string;
  }
}
