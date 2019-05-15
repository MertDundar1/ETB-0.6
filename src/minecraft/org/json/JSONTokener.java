package org.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;












































public class JSONTokener
{
  private long character;
  private boolean eof;
  private long index;
  private long line;
  private char previous;
  private final Reader reader;
  private boolean usePrevious;
  private long characterPreviousLine;
  
  public JSONTokener(Reader reader)
  {
    this.reader = (reader.markSupported() ? 
      reader : 
      new BufferedReader(reader));
    eof = false;
    usePrevious = false;
    previous = '\000';
    index = 0L;
    character = 1L;
    characterPreviousLine = 0L;
    line = 1L;
  }
  




  public JSONTokener(InputStream inputStream)
  {
    this(new InputStreamReader(inputStream));
  }
  





  public JSONTokener(String s)
  {
    this(new StringReader(s));
  }
  






  public void back()
    throws JSONException
  {
    if ((usePrevious) || (index <= 0L)) {
      throw new JSONException("Stepping back two steps is not supported");
    }
    decrementIndexes();
    usePrevious = true;
    eof = false;
  }
  


  private void decrementIndexes()
  {
    index -= 1L;
    if ((previous == '\r') || (previous == '\n')) {
      line -= 1L;
      character = characterPreviousLine;
    } else if (character > 0L) {
      character -= 1L;
    }
  }
  





  public static int dehexchar(char c)
  {
    if ((c >= '0') && (c <= '9')) {
      return c - '0';
    }
    if ((c >= 'A') && (c <= 'F')) {
      return c - '7';
    }
    if ((c >= 'a') && (c <= 'f')) {
      return c - 'W';
    }
    return -1;
  }
  




  public boolean end()
  {
    return (eof) && (!usePrevious);
  }
  






  public boolean more()
    throws JSONException
  {
    if (usePrevious) {
      return true;
    }
    try {
      reader.mark(1);
    } catch (IOException e) {
      throw new JSONException("Unable to preserve stream position", e);
    }
    try
    {
      if (reader.read() <= 0) {
        eof = true;
        return false;
      }
      reader.reset();
    } catch (IOException e) {
      throw new JSONException("Unable to read the next character from the stream", e);
    }
    return true;
  }
  



  public char next()
    throws JSONException
  {
    int c;
    

    if (usePrevious) {
      usePrevious = false;
      c = previous;
    } else {
      try {
        c = reader.read();
      } catch (IOException exception) { int c;
        throw new JSONException(exception);
      } }
    int c;
    if (c <= 0) {
      eof = true;
      return '\000';
    }
    incrementIndexes(c);
    previous = ((char)c);
    return previous;
  }
  




  private void incrementIndexes(int c)
  {
    if (c > 0) {
      index += 1L;
      if (c == 13) {
        line += 1L;
        characterPreviousLine = character;
        character = 0L;
      } else if (c == 10) {
        if (previous != '\r') {
          line += 1L;
          characterPreviousLine = character;
        }
        character = 0L;
      } else {
        character += 1L;
      }
    }
  }
  





  public char next(char c)
    throws JSONException
  {
    char n = next();
    if (n != c) {
      if (n > 0) {
        throw syntaxError("Expected '" + c + "' and instead saw '" + 
          n + "'");
      }
      throw syntaxError("Expected '" + c + "' and instead saw ''");
    }
    return n;
  }
  








  public String next(int n)
    throws JSONException
  {
    if (n == 0) {
      return "";
    }
    
    char[] chars = new char[n];
    int pos = 0;
    
    while (pos < n) {
      chars[pos] = next();
      if (end()) {
        throw syntaxError("Substring bounds error");
      }
      pos++;
    }
    return new String(chars);
  }
  

  public char nextClean()
    throws JSONException
  {
    char c;
    
    do
    {
      c = next();
    } while ((c != 0) && (c <= ' '));
    return c;
  }
  













  public String nextString(char quote)
    throws JSONException
  {
    StringBuilder sb = new StringBuilder();
    for (;;) {
      char c = next();
      switch (c) {
      case '\000': 
      case '\n': 
      case '\r': 
        throw syntaxError("Unterminated string");
      case '\\': 
        c = next();
        switch (c) {
        case 'b': 
          sb.append('\b');
          break;
        case 't': 
          sb.append('\t');
          break;
        case 'n': 
          sb.append('\n');
          break;
        case 'f': 
          sb.append('\f');
          break;
        case 'r': 
          sb.append('\r');
          break;
        case 'u': 
          try {
            sb.append((char)Integer.parseInt(next(4), 16));
          } catch (NumberFormatException e) {
            throw syntaxError("Illegal escape.", e);
          }
        
        case '"': 
        case '\'': 
        case '/': 
        case '\\': 
          sb.append(c);
          break;
        default: 
          throw syntaxError("Illegal escape.");
        }
        break;
      default: 
        if (c == quote) {
          return sb.toString();
        }
        sb.append(c);
      }
      
    }
  }
  






  public String nextTo(char delimiter)
    throws JSONException
  {
    StringBuilder sb = new StringBuilder();
    for (;;) {
      char c = next();
      if ((c == delimiter) || (c == 0) || (c == '\n') || (c == '\r')) {
        if (c != 0) {
          back();
        }
        return sb.toString().trim();
      }
      sb.append(c);
    }
  }
  








  public String nextTo(String delimiters)
    throws JSONException
  {
    StringBuilder sb = new StringBuilder();
    for (;;) {
      char c = next();
      if ((delimiters.indexOf(c) >= 0) || (c == 0) || 
        (c == '\n') || (c == '\r')) {
        if (c != 0) {
          back();
        }
        return sb.toString().trim();
      }
      sb.append(c);
    }
  }
  






  public Object nextValue()
    throws JSONException
  {
    char c = nextClean();
    

    switch (c) {
    case '"': 
    case '\'': 
      return nextString(c);
    case '{': 
      back();
      return new JSONObject(this);
    case '[': 
      back();
      return new JSONArray(this);
    }
    
    








    StringBuilder sb = new StringBuilder();
    while ((c >= ' ') && (",:]}/\\\"[{;=#".indexOf(c) < 0)) {
      sb.append(c);
      c = next();
    }
    back();
    
    String string = sb.toString().trim();
    if ("".equals(string)) {
      throw syntaxError("Missing value");
    }
    return JSONObject.stringToValue(string);
  }
  








  public char skipTo(char to)
    throws JSONException
  {
    try
    {
      long startIndex = index;
      long startCharacter = character;
      long startLine = line;
      reader.mark(1000000);
      char c;
      do { c = next();
        if (c == 0)
        {


          reader.reset();
          index = startIndex;
          character = startCharacter;
          line = startLine;
          return '\000';
        }
      } while (c != to);
      reader.mark(1);
    } catch (IOException exception) {
      throw new JSONException(exception); }
    char c;
    back();
    return c;
  }
  





  public JSONException syntaxError(String message)
  {
    return new JSONException(message + toString());
  }
  






  public JSONException syntaxError(String message, Throwable causedBy)
  {
    return new JSONException(message + toString(), causedBy);
  }
  





  public String toString()
  {
    return 
      " at " + index + " [character " + character + " line " + line + "]";
  }
}
