package ch.qos.logback.core.pattern;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;









public class ReplacingCompositeConverter<E>
  extends CompositeConverter<E>
{
  Pattern pattern;
  String regex;
  String replacement;
  
  public ReplacingCompositeConverter() {}
  
  public void start()
  {
    List<String> optionList = getOptionList();
    if (optionList == null) {
      addError("at least two options are expected whereas you have declared none");
      return;
    }
    
    int numOpts = optionList.size();
    
    if (numOpts < 2) {
      addError("at least two options are expected whereas you have declared only " + numOpts + "as [" + optionList + "]");
      return;
    }
    regex = ((String)optionList.get(0));
    pattern = Pattern.compile(regex);
    replacement = ((String)optionList.get(1));
    super.start();
  }
  
  protected String transform(E event, String in)
  {
    if (!started)
      return in;
    return pattern.matcher(in).replaceAll(replacement);
  }
}
