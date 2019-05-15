package com.ibm.icu.impl.locale;



public class Extension
{
  private char _key;
  

  protected String _value;
  

  protected Extension(char key)
  {
    _key = key;
  }
  
  Extension(char key, String value) {
    _key = key;
    _value = value;
  }
  
  public char getKey() {
    return _key;
  }
  
  public String getValue() {
    return _value;
  }
  
  public String getID() {
    return _key + "-" + _value;
  }
  
  public String toString() {
    return getID();
  }
}
