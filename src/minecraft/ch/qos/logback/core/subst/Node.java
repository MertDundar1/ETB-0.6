package ch.qos.logback.core.subst;

import java.io.PrintStream;





public class Node
{
  Type type;
  Object payload;
  Object defaultPart;
  Node next;
  
  static enum Type
  {
    LITERAL,  VARIABLE;
    

    private Type() {}
  }
  

  public Node(Type type, Object payload)
  {
    this.type = type;
    this.payload = payload;
  }
  
  public Node(Type type, Object payload, Object defaultPart)
  {
    this.type = type;
    this.payload = payload;
    this.defaultPart = defaultPart;
  }
  
  void append(Node newNode) {
    if (newNode == null)
      return;
    Node n = this;
    for (;;) {
      if (next == null) {
        next = newNode;
        return;
      }
      n = next;
    }
  }
  
  public String toString()
  {
    switch (1.$SwitchMap$ch$qos$logback$core$subst$Node$Type[type.ordinal()]) {
    case 1: 
      return "Node{type=" + type + ", payload='" + payload + "'}";
    


    case 2: 
      StringBuilder payloadBuf = new StringBuilder();
      StringBuilder defaultPartBuf2 = new StringBuilder();
      if (defaultPart != null) {
        recursive((Node)defaultPart, defaultPartBuf2);
      }
      recursive((Node)payload, payloadBuf);
      String r = "Node{type=" + type + ", payload='" + payloadBuf.toString() + "'";
      

      if (defaultPart != null)
        r = r + ", defaultPart=" + defaultPartBuf2.toString();
      r = r + '}';
      return r;
    }
    return null;
  }
  
  public void dump() {
    System.out.print(toString());
    System.out.print(" -> ");
    if (next != null) {
      next.dump();
    } else {
      System.out.print(" null");
    }
  }
  
  void recursive(Node n, StringBuilder sb) {
    Node c = n;
    while (c != null) {
      sb.append(c.toString()).append(" --> ");
      c = next;
    }
    sb.append("null ");
  }
  
  public void setNext(Node n) {
    next = n;
  }
  
  public boolean equals(Object o)
  {
    if (this == o) return true;
    if ((o == null) || (getClass() != o.getClass())) { return false;
    }
    Node node = (Node)o;
    
    if (type != type) return false;
    if (payload != null ? !payload.equals(payload) : payload != null) return false;
    if (defaultPart != null ? !defaultPart.equals(defaultPart) : defaultPart != null) return false;
    if (next != null ? !next.equals(next) : next != null) { return false;
    }
    
    return true;
  }
  
  public int hashCode()
  {
    int result = type != null ? type.hashCode() : 0;
    result = 31 * result + (payload != null ? payload.hashCode() : 0);
    result = 31 * result + (defaultPart != null ? defaultPart.hashCode() : 0);
    result = 31 * result + (next != null ? next.hashCode() : 0);
    return result;
  }
}
