package com.ibm.icu.impl;

import com.ibm.icu.text.SymbolTable;
import com.ibm.icu.text.UTF16;
import java.text.ParsePosition;



















































































public class RuleCharacterIterator
{
  private String text;
  private ParsePosition pos;
  private SymbolTable sym;
  private char[] buf;
  private int bufPos;
  private boolean isEscaped;
  public static final int DONE = -1;
  public static final int PARSE_VARIABLES = 1;
  public static final int PARSE_ESCAPES = 2;
  public static final int SKIP_WHITESPACE = 4;
  
  public RuleCharacterIterator(String text, SymbolTable sym, ParsePosition pos)
  {
    if ((text == null) || (pos.getIndex() > text.length())) {
      throw new IllegalArgumentException();
    }
    this.text = text;
    this.sym = sym;
    this.pos = pos;
    buf = null;
  }
  


  public boolean atEnd()
  {
    return (buf == null) && (pos.getIndex() == text.length());
  }
  







  public int next(int options)
  {
    int c = -1;
    isEscaped = false;
    do {
      for (;;) {
        c = _current();
        _advance(UTF16.getCharCount(c));
        
        if ((c != 36) || (buf != null) || ((options & 0x1) == 0) || (sym == null))
          break;
        String name = sym.parseReference(text, pos, text.length());
        

        if (name == null) {
          return c;
        }
        bufPos = 0;
        buf = sym.lookup(name);
        if (buf == null) {
          throw new IllegalArgumentException("Undefined variable: " + name);
        }
        

        if (buf.length == 0) {
          buf = null;
        }
        
      }
      
    } while (((options & 0x4) != 0) && (PatternProps.isWhiteSpace(c)));
    



    if ((c == 92) && ((options & 0x2) != 0)) {
      int[] offset = { 0 };
      c = Utility.unescapeAt(lookahead(), offset);
      jumpahead(offset[0]);
      isEscaped = true;
      if (c < 0) {
        throw new IllegalArgumentException("Invalid escape");
      }
    }
    



    return c;
  }
  





  public boolean isEscaped()
  {
    return isEscaped;
  }
  


  public boolean inVariable()
  {
    return buf != null;
  }
  


















  public Object getPos(Object p)
  {
    if (p == null) {
      return new Object[] { buf, { pos.getIndex(), bufPos } };
    }
    Object[] a = (Object[])p;
    a[0] = buf;
    int[] v = (int[])a[1];
    v[0] = pos.getIndex();
    v[1] = bufPos;
    return p;
  }
  




  public void setPos(Object p)
  {
    Object[] a = (Object[])p;
    buf = ((char[])a[0]);
    int[] v = (int[])a[1];
    pos.setIndex(v[0]);
    bufPos = v[1];
  }
  







  public void skipIgnored(int options)
  {
    if ((options & 0x4) != 0) {
      for (;;) {
        int a = _current();
        if (!PatternProps.isWhiteSpace(a)) break;
        _advance(UTF16.getCharCount(a));
      }
    }
  }
  











  public String lookahead()
  {
    if (buf != null) {
      return new String(buf, bufPos, buf.length - bufPos);
    }
    return text.substring(pos.getIndex());
  }
  





  public void jumpahead(int count)
  {
    if (count < 0) {
      throw new IllegalArgumentException();
    }
    if (buf != null) {
      bufPos += count;
      if (bufPos > buf.length) {
        throw new IllegalArgumentException();
      }
      if (bufPos == buf.length) {
        buf = null;
      }
    } else {
      int i = pos.getIndex() + count;
      pos.setIndex(i);
      if (i > text.length()) {
        throw new IllegalArgumentException();
      }
    }
  }
  





  public String toString()
  {
    int b = pos.getIndex();
    return text.substring(0, b) + '|' + text.substring(b);
  }
  




  private int _current()
  {
    if (buf != null) {
      return UTF16.charAt(buf, 0, buf.length, bufPos);
    }
    int i = pos.getIndex();
    return i < text.length() ? UTF16.charAt(text, i) : -1;
  }
  




  private void _advance(int count)
  {
    if (buf != null) {
      bufPos += count;
      if (bufPos == buf.length) {
        buf = null;
      }
    } else {
      pos.setIndex(pos.getIndex() + count);
      if (pos.getIndex() > text.length()) {
        pos.setIndex(text.length());
      }
    }
  }
}
