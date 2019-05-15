package ch.qos.logback.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



























public class StringCollectionUtil
{
  public StringCollectionUtil() {}
  
  public static void retainMatching(Collection<String> values, String... patterns)
  {
    retainMatching(values, Arrays.asList(patterns));
  }
  











  public static void retainMatching(Collection<String> values, Collection<String> patterns)
  {
    if (patterns.isEmpty()) return;
    List<String> matches = new ArrayList(values.size());
    for (String p : patterns) {
      pattern = Pattern.compile(p);
      for (String value : values) {
        if (pattern.matcher(value).matches())
          matches.add(value);
      }
    }
    Pattern pattern;
    values.retainAll(matches);
  }
  











  public static void removeMatching(Collection<String> values, String... patterns)
  {
    removeMatching(values, Arrays.asList(patterns));
  }
  











  public static void removeMatching(Collection<String> values, Collection<String> patterns)
  {
    List<String> matches = new ArrayList(values.size());
    for (String p : patterns) {
      pattern = Pattern.compile(p);
      for (String value : values) {
        if (pattern.matcher(value).matches())
          matches.add(value);
      }
    }
    Pattern pattern;
    values.removeAll(matches);
  }
}
