package ch.qos.logback.core.pattern.parser;



public class Node
{
  static final int LITERAL = 0;
  

  static final int SIMPLE_KEYWORD = 1;
  

  static final int COMPOSITE_KEYWORD = 2;
  

  final int type;
  

  final Object value;
  
  Node next;
  

  Node(int type)
  {
    this(type, null);
  }
  
  Node(int type, Object value) {
    this.type = type;
    this.value = value;
  }
  


  public int getType()
  {
    return type;
  }
  


  public Object getValue()
  {
    return value;
  }
  
  public Node getNext() {
    return next;
  }
  
  public void setNext(Node next) {
    this.next = next;
  }
  
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Node)) {
      return false;
    }
    Node r = (Node)o;
    
    return (type == type) && (value != null ? value.equals(value) : value == null) && (next != null ? next.equals(next) : next == null);
  }
  


  public int hashCode()
  {
    int result = type;
    result = 31 * result + (value != null ? value.hashCode() : 0);
    return result;
  }
  
  String printNext() {
    if (next != null) {
      return " -> " + next;
    }
    return "";
  }
  
  public String toString()
  {
    StringBuilder buf = new StringBuilder();
    switch (type) {
    case 0: 
      buf.append("LITERAL(" + value + ")");
      break;
    default: 
      buf.append(super.toString());
    }
    
    buf.append(printNext());
    
    return buf.toString();
  }
}
