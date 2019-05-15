package ch.qos.logback.core.pattern.parser;

import ch.qos.logback.core.pattern.FormatInfo;












public class FormattingNode
  extends Node
{
  FormatInfo formatInfo;
  
  FormattingNode(int type)
  {
    super(type);
  }
  
  FormattingNode(int type, Object value) {
    super(type, value);
  }
  
  public FormatInfo getFormatInfo() {
    return formatInfo;
  }
  
  public void setFormatInfo(FormatInfo formatInfo) {
    this.formatInfo = formatInfo;
  }
  
  public boolean equals(Object o) {
    if (!super.equals(o)) {
      return false;
    }
    
    if (!(o instanceof FormattingNode)) {
      return false;
    }
    FormattingNode r = (FormattingNode)o;
    
    return formatInfo == null ? true : formatInfo != null ? formatInfo.equals(formatInfo) : false;
  }
  

  public int hashCode()
  {
    int result = super.hashCode();
    result = 31 * result + (formatInfo != null ? formatInfo.hashCode() : 0);
    return result;
  }
}
