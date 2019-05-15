package ch.qos.logback.classic.pattern;











public class ClassNameOnlyAbbreviator
  implements Abbreviator
{
  public ClassNameOnlyAbbreviator() {}
  










  public String abbreviate(String fqClassName)
  {
    int lastIndex = fqClassName.lastIndexOf('.');
    if (lastIndex != -1) {
      return fqClassName.substring(lastIndex + 1, fqClassName.length());
    }
    return fqClassName;
  }
}
