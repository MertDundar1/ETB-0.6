package ch.qos.logback.core.joran.spi;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.action.ImplicitAction;
import ch.qos.logback.core.joran.event.BodyEvent;
import ch.qos.logback.core.joran.event.EndEvent;
import ch.qos.logback.core.joran.event.StartEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;

















































public class Interpreter
{
  private static List<Action> EMPTY_LIST = new Vector(0);
  

  private final RuleStore ruleStore;
  

  private final InterpretationContext interpretationContext;
  

  private final ArrayList<ImplicitAction> implicitActions;
  

  private final CAI_WithLocatorSupport cai;
  

  private ElementPath elementPath;
  

  Locator locator;
  
  EventPlayer eventPlayer;
  
  Stack<List<Action>> actionListStack;
  
  ElementPath skip = null;
  
  public Interpreter(Context context, RuleStore rs, ElementPath initialElementPath) {
    cai = new CAI_WithLocatorSupport(context, this);
    ruleStore = rs;
    interpretationContext = new InterpretationContext(context, this);
    implicitActions = new ArrayList(3);
    elementPath = initialElementPath;
    actionListStack = new Stack();
    eventPlayer = new EventPlayer(this);
  }
  
  public EventPlayer getEventPlayer() {
    return eventPlayer;
  }
  
  public void setInterpretationContextPropertiesMap(Map<String, String> propertiesMap)
  {
    interpretationContext.setPropertiesMap(propertiesMap);
  }
  
  /**
   * @deprecated
   */
  public InterpretationContext getExecutionContext() {
    return getInterpretationContext();
  }
  
  public InterpretationContext getInterpretationContext() {
    return interpretationContext;
  }
  
  public void startDocument() {}
  
  public void startElement(StartEvent se)
  {
    setDocumentLocator(se.getLocator());
    startElement(namespaceURI, localName, qName, attributes);
  }
  

  private void startElement(String namespaceURI, String localName, String qName, Attributes atts)
  {
    String tagName = getTagName(localName, qName);
    elementPath.push(tagName);
    
    if (skip != null)
    {
      pushEmptyActionList();
      return;
    }
    
    List<Action> applicableActionList = getApplicableActionList(elementPath, atts);
    if (applicableActionList != null) {
      actionListStack.add(applicableActionList);
      callBeginAction(applicableActionList, tagName, atts);
    }
    else {
      pushEmptyActionList();
      String errMsg = "no applicable action for [" + tagName + "], current ElementPath  is [" + elementPath + "]";
      
      cai.addError(errMsg);
    }
  }
  


  private void pushEmptyActionList()
  {
    actionListStack.add(EMPTY_LIST);
  }
  
  public void characters(BodyEvent be)
  {
    setDocumentLocator(locator);
    
    String body = be.getText();
    List<Action> applicableActionList = (List)actionListStack.peek();
    
    if (body != null) {
      body = body.trim();
      if (body.length() > 0)
      {
        callBodyAction(applicableActionList, body);
      }
    }
  }
  
  public void endElement(EndEvent endEvent) {
    setDocumentLocator(locator);
    endElement(namespaceURI, localName, qName);
  }
  


  private void endElement(String namespaceURI, String localName, String qName)
  {
    List<Action> applicableActionList = (List)actionListStack.pop();
    
    if (skip != null) {
      if (skip.equals(elementPath)) {
        skip = null;
      }
    } else if (applicableActionList != EMPTY_LIST) {
      callEndAction(applicableActionList, getTagName(localName, qName));
    }
    

    elementPath.pop();
  }
  
  public Locator getLocator() {
    return locator;
  }
  
  public void setDocumentLocator(Locator l) {
    locator = l;
  }
  
  String getTagName(String localName, String qName) {
    String tagName = localName;
    
    if ((tagName == null) || (tagName.length() < 1)) {
      tagName = qName;
    }
    
    return tagName;
  }
  
  public void addImplicitAction(ImplicitAction ia) {
    implicitActions.add(ia);
  }
  





  List<Action> lookupImplicitAction(ElementPath elementPath, Attributes attributes, InterpretationContext ec)
  {
    int len = implicitActions.size();
    
    for (int i = 0; i < len; i++) {
      ImplicitAction ia = (ImplicitAction)implicitActions.get(i);
      
      if (ia.isApplicable(elementPath, attributes, ec)) {
        List<Action> actionList = new ArrayList(1);
        actionList.add(ia);
        
        return actionList;
      }
    }
    
    return null;
  }
  


  List<Action> getApplicableActionList(ElementPath elementPath, Attributes attributes)
  {
    List<Action> applicableActionList = ruleStore.matchActions(elementPath);
    

    if (applicableActionList == null) {
      applicableActionList = lookupImplicitAction(elementPath, attributes, interpretationContext);
    }
    

    return applicableActionList;
  }
  
  void callBeginAction(List<Action> applicableActionList, String tagName, Attributes atts)
  {
    if (applicableActionList == null) {
      return;
    }
    
    Iterator<Action> i = applicableActionList.iterator();
    while (i.hasNext()) {
      Action action = (Action)i.next();
      
      try
      {
        action.begin(interpretationContext, tagName, atts);
      } catch (ActionException e) {
        skip = elementPath.duplicate();
        cai.addError("ActionException in Action for tag [" + tagName + "]", e);
      } catch (RuntimeException e) {
        skip = elementPath.duplicate();
        cai.addError("RuntimeException in Action for tag [" + tagName + "]", e);
      }
    }
  }
  
  private void callBodyAction(List<Action> applicableActionList, String body) {
    if (applicableActionList == null) {
      return;
    }
    Iterator<Action> i = applicableActionList.iterator();
    
    while (i.hasNext()) {
      Action action = (Action)i.next();
      try {
        action.body(interpretationContext, body);
      } catch (ActionException ae) {
        cai.addError("Exception in end() methd for action [" + action + "]", ae);
      }
    }
  }
  

  private void callEndAction(List<Action> applicableActionList, String tagName)
  {
    if (applicableActionList == null) {
      return;
    }
    

    Iterator<Action> i = applicableActionList.iterator();
    
    while (i.hasNext()) {
      Action action = (Action)i.next();
      
      try
      {
        action.end(interpretationContext, tagName);
      }
      catch (ActionException ae)
      {
        cai.addError("ActionException in Action for tag [" + tagName + "]", ae);
      }
      catch (RuntimeException e) {
        cai.addError("RuntimeException in Action for tag [" + tagName + "]", e);
      }
    }
  }
  
  public RuleStore getRuleStore() {
    return ruleStore;
  }
}
