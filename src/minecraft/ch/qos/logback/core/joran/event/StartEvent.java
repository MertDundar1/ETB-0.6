package ch.qos.logback.core.joran.event;

import ch.qos.logback.core.joran.spi.ElementPath;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.helpers.AttributesImpl;













public class StartEvent
  extends SaxEvent
{
  public final Attributes attributes;
  public final ElementPath elementPath;
  
  StartEvent(ElementPath elementPath, String namespaceURI, String localName, String qName, Attributes attributes, Locator locator)
  {
    super(namespaceURI, localName, qName, locator);
    
    this.attributes = new AttributesImpl(attributes);
    this.elementPath = elementPath;
  }
  
  public Attributes getAttributes() {
    return attributes;
  }
  

  public String toString()
  {
    return "StartEvent(" + getQName() + ")  [" + locator.getLineNumber() + "," + locator.getColumnNumber() + "]";
  }
}
