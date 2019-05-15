package com.ibm.icu.impl.data;

import com.ibm.icu.impl.PatternProps;
import com.ibm.icu.impl.Utility;
import com.ibm.icu.text.UTF16;
import java.io.IOException;
























public class TokenIterator
{
  private ResourceReader reader;
  private String line;
  private StringBuffer buf;
  private boolean done;
  private int pos;
  private int lastpos;
  
  public TokenIterator(ResourceReader r)
  {
    reader = r;
    line = null;
    done = false;
    buf = new StringBuffer();
    pos = (this.lastpos = -1);
  }
  


  public String next()
    throws IOException
  {
    if (done) {
      return null;
    }
    for (;;) {
      if (line == null) {
        line = reader.readLineSkippingComments();
        if (line == null) {
          done = true;
          return null;
        }
        pos = 0;
      }
      buf.setLength(0);
      lastpos = pos;
      pos = nextToken(pos);
      if (pos >= 0) break;
      line = null;
    }
    
    return buf.toString();
  }
  






  public int getLineNumber()
  {
    return reader.getLineNumber();
  }
  



  public String describePosition()
  {
    return reader.describePosition() + ':' + (lastpos + 1);
  }
  











  private int nextToken(int position)
  {
    position = PatternProps.skipWhiteSpace(line, position);
    if (position == line.length()) {
      return -1;
    }
    int startpos = position;
    char c = line.charAt(position++);
    char quote = '\000';
    switch (c) {
    case '"': 
    case '\'': 
      quote = c;
      break;
    case '#': 
      return -1;
    default: 
      buf.append(c);
    }
    
    int[] posref = null;
    while (position < line.length()) {
      c = line.charAt(position);
      if (c == '\\') {
        if (posref == null) {
          posref = new int[1];
        }
        posref[0] = (position + 1);
        int c32 = Utility.unescapeAt(line, posref);
        if (c32 < 0) {
          throw new RuntimeException("Invalid escape at " + reader.describePosition() + ':' + position);
        }
        

        UTF16.append(buf, c32);
        position = posref[0];
      } else { if (((quote != 0) && (c == quote)) || ((quote == 0) && (PatternProps.isWhiteSpace(c))))
        {
          position++;return position; }
        if ((quote == 0) && (c == '#')) {
          return position;
        }
        buf.append(c);
        position++;
      }
    }
    if (quote != 0) {
      throw new RuntimeException("Unterminated quote at " + reader.describePosition() + ':' + startpos);
    }
    

    return position;
  }
}
