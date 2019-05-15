package com.ibm.icu.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;






























public class RangeDateRule
  implements DateRule
{
  public RangeDateRule() {}
  
  public void add(DateRule rule)
  {
    add(new Date(Long.MIN_VALUE), rule);
  }
  





  public void add(Date start, DateRule rule)
  {
    ranges.add(new Range(start, rule));
  }
  






  public Date firstAfter(Date start)
  {
    int index = startIndex(start);
    if (index == ranges.size()) {
      index = 0;
    }
    Date result = null;
    
    Range r = rangeAt(index);
    Range e = rangeAt(index + 1);
    
    if ((r != null) && (rule != null))
    {
      if (e != null) {
        result = rule.firstBetween(start, start);
      } else {
        result = rule.firstAfter(start);
      }
    }
    return result;
  }
  



  public Date firstBetween(Date start, Date end)
  {
    if (end == null) {
      return firstAfter(start);
    }
    

    int index = startIndex(start);
    Date result = null;
    
    Range next = rangeAt(index);
    
    while ((result == null) && (next != null) && (!start.after(end)))
    {
      Range r = next;
      next = rangeAt(index + 1);
      
      if (rule != null) {
        Date e = (next != null) && (!start.after(end)) ? start : end;
        
        result = rule.firstBetween(start, e);
      }
    }
    return result;
  }
  



  public boolean isOn(Date date)
  {
    Range r = rangeAt(startIndex(date));
    return (r != null) && (rule != null) && (rule.isOn(date));
  }
  





  public boolean isBetween(Date start, Date end)
  {
    return firstBetween(start, end) == null;
  }
  



  private int startIndex(Date start)
  {
    int lastIndex = ranges.size();
    
    for (int i = 0; i < ranges.size(); i++) {
      Range r = (Range)ranges.get(i);
      if (start.before(start)) {
        break;
      }
      lastIndex = i;
    }
    return lastIndex;
  }
  
  private Range rangeAt(int index) {
    return index < ranges.size() ? (Range)ranges.get(index) : null;
  }
  

  List<Range> ranges = new ArrayList(2);
}
