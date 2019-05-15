package com.ibm.icu.text;

import com.ibm.icu.impl.PatternProps;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

















































































































































public class SelectFormat
  extends Format
{
  private static final long serialVersionUID = 2993154333257524984L;
  private String pattern = null;
  



  private transient MessagePattern msgPattern;
  




  public SelectFormat(String pattern)
  {
    applyPattern(pattern);
  }
  


  private void reset()
  {
    pattern = null;
    if (msgPattern != null) {
      msgPattern.clear();
    }
  }
  







  public void applyPattern(String pattern)
  {
    this.pattern = pattern;
    if (msgPattern == null) {
      msgPattern = new MessagePattern();
    }
    try {
      msgPattern.parseSelectStyle(pattern);
    } catch (RuntimeException e) {
      reset();
      throw e;
    }
  }
  





  public String toPattern()
  {
    return pattern;
  }
  






  static int findSubMessage(MessagePattern pattern, int partIndex, String keyword)
  {
    int count = pattern.countParts();
    int msgStart = 0;
    do
    {
      MessagePattern.Part part = pattern.getPart(partIndex++);
      MessagePattern.Part.Type type = part.getType();
      if (type == MessagePattern.Part.Type.ARG_LIMIT) {
        break;
      }
      assert (type == MessagePattern.Part.Type.ARG_SELECTOR);
      
      if (pattern.partSubstringMatches(part, keyword))
      {
        return partIndex; }
      if ((msgStart == 0) && (pattern.partSubstringMatches(part, "other"))) {
        msgStart = partIndex;
      }
      partIndex = pattern.getLimitPartIndex(partIndex);
      partIndex++; } while (partIndex < count);
    return msgStart;
  }
  








  public final String format(String keyword)
  {
    if (!PatternProps.isIdentifier(keyword)) {
      throw new IllegalArgumentException("Invalid formatting argument.");
    }
    
    if ((msgPattern == null) || (msgPattern.countParts() == 0)) {
      throw new IllegalStateException("Invalid format error.");
    }
    

    int msgStart = findSubMessage(msgPattern, 0, keyword);
    if (!msgPattern.jdkAposMode()) {
      int msgLimit = msgPattern.getLimitPartIndex(msgStart);
      return msgPattern.getPatternString().substring(msgPattern.getPart(msgStart).getLimit(), msgPattern.getPatternIndex(msgLimit));
    }
    

    StringBuilder result = null;
    int prevIndex = msgPattern.getPart(msgStart).getLimit();
    int i = msgStart;
    for (;;) { MessagePattern.Part part = msgPattern.getPart(++i);
      MessagePattern.Part.Type type = part.getType();
      int index = part.getIndex();
      if (type == MessagePattern.Part.Type.MSG_LIMIT) {
        if (result == null) {
          return pattern.substring(prevIndex, index);
        }
        return result.append(pattern, prevIndex, index).toString();
      }
      if (type == MessagePattern.Part.Type.SKIP_SYNTAX) {
        if (result == null) {
          result = new StringBuilder();
        }
        result.append(pattern, prevIndex, index);
        prevIndex = part.getLimit();
      } else if (type == MessagePattern.Part.Type.ARG_START) {
        if (result == null) {
          result = new StringBuilder();
        }
        result.append(pattern, prevIndex, index);
        prevIndex = index;
        i = msgPattern.getLimitPartIndex(i);
        index = msgPattern.getPart(i).getLimit();
        MessagePattern.appendReducedApostrophes(pattern, prevIndex, index, result);
        prevIndex = index;
      }
    }
  }
  













  public StringBuffer format(Object keyword, StringBuffer toAppendTo, FieldPosition pos)
  {
    if ((keyword instanceof String)) {
      toAppendTo.append(format((String)keyword));
    } else {
      throw new IllegalArgumentException("'" + keyword + "' is not a String");
    }
    return toAppendTo;
  }
  









  public Object parseObject(String source, ParsePosition pos)
  {
    throw new UnsupportedOperationException();
  }
  




  public boolean equals(Object obj)
  {
    if (this == obj) {
      return true;
    }
    if ((obj == null) || (getClass() != obj.getClass())) {
      return false;
    }
    SelectFormat sf = (SelectFormat)obj;
    return msgPattern == null ? false : msgPattern == null ? true : msgPattern.equals(msgPattern);
  }
  




  public int hashCode()
  {
    if (pattern != null) {
      return pattern.hashCode();
    }
    return 0;
  }
  




  public String toString()
  {
    return "pattern='" + pattern + "'";
  }
  
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
  {
    in.defaultReadObject();
    if (pattern != null) {
      applyPattern(pattern);
    }
  }
}
