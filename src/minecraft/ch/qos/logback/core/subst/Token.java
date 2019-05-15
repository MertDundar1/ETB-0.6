package ch.qos.logback.core.subst;














public class Token
{
  public static final Token START_TOKEN = new Token(Type.START, null);
  public static final Token CURLY_LEFT_TOKEN = new Token(Type.CURLY_LEFT, null);
  public static final Token CURLY_RIGHT_TOKEN = new Token(Type.CURLY_RIGHT, null);
  public static final Token DEFAULT_SEP_TOKEN = new Token(Type.DEFAULT, null);
  
  public static enum Type { LITERAL,  START,  CURLY_LEFT,  CURLY_RIGHT,  DEFAULT;
    
    private Type() {} }
  
  Type type;
  String payload;
  public Token(Type type, String payload) { this.type = type;
    this.payload = payload;
  }
  
  public boolean equals(Object o)
  {
    if (this == o) return true;
    if ((o == null) || (getClass() != o.getClass())) { return false;
    }
    Token token = (Token)o;
    
    if (type != type) return false;
    if (payload != null ? !payload.equals(payload) : payload != null) { return false;
    }
    return true;
  }
  
  public int hashCode()
  {
    int result = type != null ? type.hashCode() : 0;
    result = 31 * result + (payload != null ? payload.hashCode() : 0);
    return result;
  }
  
  public String toString()
  {
    String result = "Token{type=" + type;
    
    if (payload != null) {
      result = result + ", payload='" + payload + '\'';
    }
    result = result + '}';
    return result;
  }
}
