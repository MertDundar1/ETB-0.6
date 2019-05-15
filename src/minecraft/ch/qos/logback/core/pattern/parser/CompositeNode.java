package ch.qos.logback.core.pattern.parser;






public class CompositeNode
  extends SimpleKeywordNode
{
  Node childNode;
  





  CompositeNode(String keyword)
  {
    super(2, keyword);
  }
  
  public Node getChildNode()
  {
    return childNode;
  }
  
  public void setChildNode(Node childNode) {
    this.childNode = childNode;
  }
  
  public boolean equals(Object o) {
    if (!super.equals(o)) {
      return false;
    }
    if (!(o instanceof CompositeNode)) {
      return false;
    }
    CompositeNode r = (CompositeNode)o;
    
    return childNode == null ? true : childNode != null ? childNode.equals(childNode) : false;
  }
  

  public int hashCode()
  {
    return super.hashCode();
  }
  
  public String toString() {
    StringBuilder buf = new StringBuilder();
    if (childNode != null) {
      buf.append("CompositeNode(" + childNode + ")");
    } else {
      buf.append("CompositeNode(no child)");
    }
    buf.append(printNext());
    return buf.toString();
  }
}
