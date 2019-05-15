package ch.qos.logback.core.joran.spi;

import java.util.ArrayList;
import java.util.List;























public class ElementSelector
  extends ElementPath
{
  public ElementSelector() {}
  
  public ElementSelector(List<String> list)
  {
    super(list);
  }
  





  public ElementSelector(String p)
  {
    super(p);
  }
  
  public boolean fullPathMatch(ElementPath path) {
    if (path.size() != size()) {
      return false;
    }
    
    int len = size();
    for (int i = 0; i < len; i++) {
      if (!equalityCheck(get(i), path.get(i))) {
        return false;
      }
    }
    
    return true;
  }
  




  public int getTailMatchLength(ElementPath p)
  {
    if (p == null) {
      return 0;
    }
    
    int lSize = partList.size();
    int rSize = partList.size();
    

    if ((lSize == 0) || (rSize == 0)) {
      return 0;
    }
    
    int minLen = lSize <= rSize ? lSize : rSize;
    int match = 0;
    

    for (int i = 1; i <= minLen; i++) {
      String l = (String)partList.get(lSize - i);
      String r = (String)partList.get(rSize - i);
      
      if (!equalityCheck(l, r)) break;
      match++;
    }
    


    return match;
  }
  
  public boolean isContainedIn(ElementPath p) {
    if (p == null) {
      return false;
    }
    return p.toStableString().contains(toStableString());
  }
  





  public int getPrefixMatchLength(ElementPath p)
  {
    if (p == null) {
      return 0;
    }
    
    int lSize = partList.size();
    int rSize = partList.size();
    

    if ((lSize == 0) || (rSize == 0)) {
      return 0;
    }
    
    int minLen = lSize <= rSize ? lSize : rSize;
    int match = 0;
    
    for (int i = 0; i < minLen; i++) {
      String l = (String)partList.get(i);
      String r = (String)partList.get(i);
      
      if (!equalityCheck(l, r)) break;
      match++;
    }
    



    return match;
  }
  
  private boolean equalityCheck(String x, String y) {
    return x.equalsIgnoreCase(y);
  }
  
  public boolean equals(Object o)
  {
    if ((o == null) || (!(o instanceof ElementSelector))) {
      return false;
    }
    
    ElementSelector r = (ElementSelector)o;
    
    if (r.size() != size()) {
      return false;
    }
    
    int len = size();
    
    for (int i = 0; i < len; i++) {
      if (!equalityCheck(get(i), r.get(i))) {
        return false;
      }
    }
    

    return true;
  }
  
  public int hashCode()
  {
    int hc = 0;
    int len = size();
    
    for (int i = 0; i < len; i++)
    {

      hc ^= get(i).toLowerCase().hashCode();
    }
    return hc;
  }
}
