package ch.qos.logback.classic.html;

import ch.qos.logback.core.html.CssBuilder;



















public class UrlCssBuilder
  implements CssBuilder
{
  public UrlCssBuilder() {}
  
  String url = "http://logback.qos.ch/css/classic.css";
  
  public String getUrl() {
    return url;
  }
  
  public void setUrl(String url) {
    this.url = url;
  }
  
  public void addCss(StringBuilder sbuf) {
    sbuf.append("<link REL=StyleSheet HREF=\"");
    sbuf.append(url);
    sbuf.append("\" TITLE=\"Basic\" />");
  }
}
