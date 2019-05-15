package joptsimple.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;







































public class RegexMatcher
  implements ValueConverter<String>
{
  private final Pattern pattern;
  
  public RegexMatcher(String pattern, int flags)
  {
    this.pattern = Pattern.compile(pattern, flags);
  }
  






  public static ValueConverter<String> regex(String pattern)
  {
    return new RegexMatcher(pattern, 0);
  }
  
  public String convert(String value) {
    if (!pattern.matcher(value).matches()) {
      throw new ValueConversionException("Value [" + value + "] did not match regex [" + pattern.pattern() + ']');
    }
    

    return value;
  }
  
  public Class<String> valueType() {
    return String.class;
  }
  
  public String valuePattern() {
    return pattern.pattern();
  }
}
