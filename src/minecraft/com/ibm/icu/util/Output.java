package com.ibm.icu.util;










public class Output<T>
{
  public T value;
  








  public String toString()
  {
    return value == null ? "null" : value.toString();
  }
  





  public Output() {}
  





  public Output(T value)
  {
    this.value = value;
  }
}
