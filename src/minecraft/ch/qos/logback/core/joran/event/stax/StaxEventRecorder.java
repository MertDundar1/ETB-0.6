package ch.qos.logback.core.joran.event.stax;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.spi.ElementPath;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.spi.ContextAwareBase;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;













public class StaxEventRecorder
  extends ContextAwareBase
{
  List<StaxEvent> eventList = new ArrayList();
  ElementPath globalElementPath = new ElementPath();
  
  public StaxEventRecorder(Context context) {
    setContext(context);
  }
  
  public void recordEvents(InputStream inputStream) throws JoranException {
    try {
      XMLEventReader xmlEventReader = XMLInputFactory.newInstance().createXMLEventReader(inputStream);
      read(xmlEventReader);
    } catch (XMLStreamException e) {
      throw new JoranException("Problem parsing XML document. See previously reported errors.", e);
    }
  }
  
  public List<StaxEvent> getEventList() {
    return eventList;
  }
  
  private void read(XMLEventReader xmlEventReader) throws XMLStreamException {
    while (xmlEventReader.hasNext()) {
      XMLEvent xmlEvent = xmlEventReader.nextEvent();
      switch (xmlEvent.getEventType()) {
      case 1: 
        addStartElement(xmlEvent);
        break;
      case 4: 
        addCharacters(xmlEvent);
        break;
      case 2: 
        addEndEvent(xmlEvent);
      }
      
    }
  }
  

  private void addStartElement(XMLEvent xmlEvent)
  {
    StartElement se = xmlEvent.asStartElement();
    String tagName = se.getName().getLocalPart();
    globalElementPath.push(tagName);
    ElementPath current = globalElementPath.duplicate();
    StartEvent startEvent = new StartEvent(current, tagName, se.getAttributes(), se.getLocation());
    eventList.add(startEvent);
  }
  
  private void addCharacters(XMLEvent xmlEvent) {
    Characters characters = xmlEvent.asCharacters();
    StaxEvent lastEvent = getLastEvent();
    
    if ((lastEvent instanceof BodyEvent)) {
      BodyEvent be = (BodyEvent)lastEvent;
      be.append(characters.getData());

    }
    else if (!characters.isWhiteSpace()) {
      BodyEvent bodyEvent = new BodyEvent(characters.getData(), xmlEvent.getLocation());
      eventList.add(bodyEvent);
    }
  }
  
  private void addEndEvent(XMLEvent xmlEvent)
  {
    EndElement ee = xmlEvent.asEndElement();
    String tagName = ee.getName().getLocalPart();
    EndEvent endEvent = new EndEvent(tagName, ee.getLocation());
    eventList.add(endEvent);
    globalElementPath.pop();
  }
  
  StaxEvent getLastEvent() {
    if (eventList.isEmpty()) {
      return null;
    }
    int size = eventList.size();
    if (size == 0)
      return null;
    return (StaxEvent)eventList.get(size - 1);
  }
}
