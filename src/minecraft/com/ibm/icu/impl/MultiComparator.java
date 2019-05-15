package com.ibm.icu.impl;

import java.util.Comparator;




public class MultiComparator<T>
  implements Comparator<T>
{
  private Comparator<T>[] comparators;
  
  public MultiComparator(Comparator<T>... comparators)
  {
    this.comparators = comparators;
  }
  




  public int compare(T arg0, T arg1)
  {
    for (int i = 0; i < comparators.length; i++) {
      int result = comparators[i].compare(arg0, arg1);
      if (result != 0)
      {

        if (result > 0) {
          return i + 1;
        }
        return -(i + 1);
      } }
    return 0;
  }
}
