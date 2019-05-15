package org.json;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

















































































public class JSONWriter
{
  private static final int maxdepth = 200;
  private boolean comma;
  protected char mode;
  private final JSONObject[] stack;
  private int top;
  protected Appendable writer;
  
  public JSONWriter(Appendable w)
  {
    comma = false;
    mode = 'i';
    stack = new JSONObject['È'];
    top = 0;
    writer = w;
  }
  




  private JSONWriter append(String string)
    throws JSONException
  {
    if (string == null) {
      throw new JSONException("Null pointer");
    }
    if ((mode == 'o') || (mode == 'a')) {
      try {
        if ((comma) && (mode == 'a')) {
          writer.append(',');
        }
        writer.append(string);

      }
      catch (IOException e)
      {
        throw new JSONException(e);
      }
      if (mode == 'o') {
        mode = 'k';
      }
      comma = true;
      return this;
    }
    throw new JSONException("Value out of sequence.");
  }
  







  public JSONWriter array()
    throws JSONException
  {
    if ((mode == 'i') || (mode == 'o') || (mode == 'a')) {
      push(null);
      append("[");
      comma = false;
      return this;
    }
    throw new JSONException("Misplaced array.");
  }
  





  private JSONWriter end(char m, char c)
    throws JSONException
  {
    if (mode != m) {
      throw new JSONException(m == 'a' ? 
        "Misplaced endArray." : 
        "Misplaced endObject.");
    }
    pop(m);
    try {
      writer.append(c);

    }
    catch (IOException e)
    {
      throw new JSONException(e);
    }
    comma = true;
    return this;
  }
  




  public JSONWriter endArray()
    throws JSONException
  {
    return end('a', ']');
  }
  




  public JSONWriter endObject()
    throws JSONException
  {
    return end('k', '}');
  }
  






  public JSONWriter key(String string)
    throws JSONException
  {
    if (string == null) {
      throw new JSONException("Null key.");
    }
    if (mode == 'k') {
      try {
        JSONObject topObject = stack[(top - 1)];
        
        if (topObject.has(string)) {
          throw new JSONException("Duplicate key \"" + string + "\"");
        }
        topObject.put(string, true);
        if (comma) {
          writer.append(',');
        }
        writer.append(JSONObject.quote(string));
        writer.append(':');
        comma = false;
        mode = 'o';
        return this;

      }
      catch (IOException e)
      {
        throw new JSONException(e);
      }
    }
    throw new JSONException("Misplaced key.");
  }
  








  public JSONWriter object()
    throws JSONException
  {
    if (mode == 'i') {
      mode = 'o';
    }
    if ((mode == 'o') || (mode == 'a')) {
      append("{");
      push(new JSONObject());
      comma = false;
      return this;
    }
    throw new JSONException("Misplaced object.");
  }
  





  private void pop(char c)
    throws JSONException
  {
    if (top <= 0) {
      throw new JSONException("Nesting error.");
    }
    char m = stack[(top - 1)] == null ? 'a' : 'k';
    if (m != c) {
      throw new JSONException("Nesting error.");
    }
    top -= 1;
    mode = 
    
      (stack[(top - 1)] == null ? 
      'a' : top == 0 ? 'd' : 
      'k');
  }
  



  private void push(JSONObject jo)
    throws JSONException
  {
    if (top >= 200) {
      throw new JSONException("Nesting too deep.");
    }
    stack[top] = jo;
    mode = (jo == null ? 'a' : 'k');
    top += 1;
  }
  






















  public static String valueToString(Object value)
    throws JSONException
  {
    if ((value == null) || (value.equals(null))) {
      return "null";
    }
    if ((value instanceof JSONString))
    {
      try {
        object = ((JSONString)value).toJSONString();
      } catch (Exception e) { Object object;
        throw new JSONException(e); }
      Object object;
      if ((object instanceof String)) {
        return (String)object;
      }
      throw new JSONException("Bad value from toJSONString: " + object);
    }
    if ((value instanceof Number))
    {
      String numberAsString = JSONObject.numberToString((Number)value);
      
      try
      {
        BigDecimal unused = new BigDecimal(numberAsString);
        
        return numberAsString;
      }
      catch (NumberFormatException ex)
      {
        return JSONObject.quote(numberAsString);
      }
    }
    if (((value instanceof Boolean)) || ((value instanceof JSONObject)) || 
      ((value instanceof JSONArray))) {
      return value.toString();
    }
    if ((value instanceof Map)) {
      Map<?, ?> map = (Map)value;
      return new JSONObject(map).toString();
    }
    if ((value instanceof Collection)) {
      Collection<?> coll = (Collection)value;
      return new JSONArray(coll).toString();
    }
    if (value.getClass().isArray()) {
      return new JSONArray(value).toString();
    }
    if ((value instanceof Enum)) {
      return JSONObject.quote(((Enum)value).name());
    }
    return JSONObject.quote(value.toString());
  }
  





  public JSONWriter value(boolean b)
    throws JSONException
  {
    return append(b ? "true" : "false");
  }
  




  public JSONWriter value(double d)
    throws JSONException
  {
    return value(new Double(d));
  }
  




  public JSONWriter value(long l)
    throws JSONException
  {
    return append(Long.toString(l));
  }
  






  public JSONWriter value(Object object)
    throws JSONException
  {
    return append(valueToString(object));
  }
}
