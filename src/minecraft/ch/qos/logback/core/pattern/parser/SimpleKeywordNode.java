package ch.qos.logback.core.pattern.parser;

import java.util.List;












public class SimpleKeywordNode
  extends FormattingNode
{
  List<String> optionList;
  
  SimpleKeywordNode(Object value)
  {
    super(1, value);
  }
  
  protected SimpleKeywordNode(int type, Object value) {
    super(type, value);
  }
  
  public List<String> getOptions() {
    return optionList;
  }
  
  public void setOptions(List<String> optionList) {
    this.optionList = optionList;
  }
  
  public boolean equals(Object o) {
    if (!super.equals(o)) {
      return false;
    }
    
    if (!(o instanceof SimpleKeywordNode)) {
      return false;
    }
    SimpleKeywordNode r = (SimpleKeywordNode)o;
    
    return optionList == null ? true : optionList != null ? optionList.equals(optionList) : false;
  }
  

  public int hashCode()
  {
    return super.hashCode();
  }
  
  public String toString() {
    StringBuilder buf = new StringBuilder();
    if (optionList == null) {
      buf.append("KeyWord(" + value + "," + formatInfo + ")");
    } else {
      buf.append("KeyWord(" + value + ", " + formatInfo + "," + optionList + ")");
    }
    
    buf.append(printNext());
    return buf.toString();
  }
}
