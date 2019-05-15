package ch.qos.logback.classic.gaffer;

import ch.qos.logback.core.joran.spi.NoAutoStartUtil;
import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;
import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.util.Iterator;
import java.util.List;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class ComponentDelegate
  extends ContextAwareBase implements GroovyObject
{
  private final Object component;
  private final List fieldsToCascade;
  
  public ComponentDelegate(Object arg1)
  {
    Object component;
    CallSite[] arrayOfCallSite = $getCallSiteArray();List localList = ScriptBytecodeAdapter.createList(new Object[0]);fieldsToCascade = localList;MetaClass localMetaClass = $getStaticMetaClass();metaClass = localMetaClass;
    

    Object localObject1 = component;this.component = localObject1;
  }
  
  public String getLabel() { CallSite[] arrayOfCallSite = $getCallSiteArray();return "component";return null; }
  
  public String getLabelFistLetterInUpperCase() { CallSite[] arrayOfCallSite = $getCallSiteArray(); if ((__$stMC) || (BytecodeInterface8.disabledStandardMetaClass())) return (String)ShortTypeHandling.castToString(arrayOfCallSite[0].call(arrayOfCallSite[1].call(arrayOfCallSite[2].call(arrayOfCallSite[3].callCurrent(this), Integer.valueOf(0))), arrayOfCallSite[4].call(arrayOfCallSite[5].callCurrent(this), Integer.valueOf(1)))); else return (String)ShortTypeHandling.castToString(arrayOfCallSite[6].call(arrayOfCallSite[7].call(arrayOfCallSite[8].call(getLabel(), Integer.valueOf(0))), arrayOfCallSite[9].call(getLabel(), Integer.valueOf(1)))); return null;
  }
  
  public void methodMissing(String name, Object args) { CallSite[] arrayOfCallSite = $getCallSiteArray();NestingType nestingType = (NestingType)ShortTypeHandling.castToEnum(arrayOfCallSite[10].call(PropertyUtil.class, component, name), NestingType.class);
    if (ScriptBytecodeAdapter.compareEqual(nestingType, arrayOfCallSite[11].callGetProperty(NestingType.class))) {
      arrayOfCallSite[12].callCurrent(this, new GStringImpl(new Object[] { arrayOfCallSite[13].callCurrent(this), arrayOfCallSite[14].callCurrent(this), arrayOfCallSite[15].callGetProperty(arrayOfCallSite[16].call(component)), name }, new String[] { "", " ", " of type [", "] has no appplicable [", "] property " }));
      return;
    }
    
    String subComponentName = null;
    Class clazz = null;
    Closure closure = null;
    Object localObject1 = arrayOfCallSite[17].callCurrent(this, args);subComponentName = (String)ShortTypeHandling.castToString(arrayOfCallSite[18].call(localObject1, Integer.valueOf(0)));clazz = (Class)ShortTypeHandling.castToClass(arrayOfCallSite[19].call(localObject1, Integer.valueOf(1)));closure = (Closure)ScriptBytecodeAdapter.castToType(arrayOfCallSite[20].call(localObject1, Integer.valueOf(2)), Closure.class);
    if ((!BytecodeInterface8.isOrigZ()) || (__$stMC) || (BytecodeInterface8.disabledStandardMetaClass())) { if (ScriptBytecodeAdapter.compareNotEqual(clazz, null)) {
        Object subComponent = arrayOfCallSite[21].call(clazz);
        String str1; if (((DefaultTypeTransformation.booleanUnbox(subComponentName)) && (DefaultTypeTransformation.booleanUnbox(arrayOfCallSite[22].call(subComponent, name))) ? 1 : 0) != 0) {
          str1 = subComponentName;ScriptBytecodeAdapter.setProperty(str1, null, subComponent, "name"); }
        Object localObject2;
        if ((subComponent instanceof ContextAware)) {
          localObject2 = arrayOfCallSite[23].callGroovyObjectGetProperty(this);ScriptBytecodeAdapter.setProperty(localObject2, null, subComponent, "context");
        }
        if (DefaultTypeTransformation.booleanUnbox(closure)) {
          ComponentDelegate subDelegate = (ComponentDelegate)ScriptBytecodeAdapter.castToType(arrayOfCallSite[24].callConstructor(ComponentDelegate.class, subComponent), ComponentDelegate.class);
          
          arrayOfCallSite[25].callCurrent(this, subDelegate);
          Object localObject3 = arrayOfCallSite[26].callGroovyObjectGetProperty(this);ScriptBytecodeAdapter.setGroovyObjectProperty(localObject3, ComponentDelegate.class, subDelegate, "context");
          arrayOfCallSite[27].callCurrent(this, subComponent);
          ComponentDelegate localComponentDelegate1 = subDelegate;ScriptBytecodeAdapter.setGroovyObjectProperty(localComponentDelegate1, ComponentDelegate.class, closure, "delegate");
          Object localObject4 = arrayOfCallSite[28].callGetProperty(Closure.class);ScriptBytecodeAdapter.setGroovyObjectProperty(localObject4, ComponentDelegate.class, closure, "resolveStrategy");
          arrayOfCallSite[29].call(closure);
        }
        if ((((subComponent instanceof LifeCycle)) && (DefaultTypeTransformation.booleanUnbox(arrayOfCallSite[30].call(NoAutoStartUtil.class, subComponent))) ? 1 : 0) != 0) {
          arrayOfCallSite[31].call(subComponent);
        }
        arrayOfCallSite[32].call(PropertyUtil.class, nestingType, component, subComponent, name);
      } else {
        arrayOfCallSite[33].callCurrent(this, new GStringImpl(new Object[] { name, arrayOfCallSite[34].callCurrent(this), arrayOfCallSite[35].callCurrent(this), arrayOfCallSite[36].callGetProperty(arrayOfCallSite[37].call(component)) }, new String[] { "No 'class' argument specified for [", "] in ", " ", " of type [", "]" }));
      }
    }
    else if (ScriptBytecodeAdapter.compareNotEqual(clazz, null)) {
      Object subComponent = arrayOfCallSite[38].call(clazz);
      String str2; if (((DefaultTypeTransformation.booleanUnbox(subComponentName)) && (DefaultTypeTransformation.booleanUnbox(arrayOfCallSite[39].call(subComponent, name))) ? 1 : 0) != 0) {
        str2 = subComponentName;ScriptBytecodeAdapter.setProperty(str2, null, subComponent, "name"); }
      Object localObject5;
      if ((subComponent instanceof ContextAware)) {
        localObject5 = arrayOfCallSite[40].callGroovyObjectGetProperty(this);ScriptBytecodeAdapter.setProperty(localObject5, null, subComponent, "context");
      }
      if (DefaultTypeTransformation.booleanUnbox(closure)) {
        ComponentDelegate subDelegate = (ComponentDelegate)ScriptBytecodeAdapter.castToType(arrayOfCallSite[41].callConstructor(ComponentDelegate.class, subComponent), ComponentDelegate.class);
        
        arrayOfCallSite[42].callCurrent(this, subDelegate);
        Object localObject6 = arrayOfCallSite[43].callGroovyObjectGetProperty(this);ScriptBytecodeAdapter.setGroovyObjectProperty(localObject6, ComponentDelegate.class, subDelegate, "context");
        arrayOfCallSite[44].callCurrent(this, subComponent);
        ComponentDelegate localComponentDelegate2 = subDelegate;ScriptBytecodeAdapter.setGroovyObjectProperty(localComponentDelegate2, ComponentDelegate.class, closure, "delegate");
        Object localObject7 = arrayOfCallSite[45].callGetProperty(Closure.class);ScriptBytecodeAdapter.setGroovyObjectProperty(localObject7, ComponentDelegate.class, closure, "resolveStrategy");
        arrayOfCallSite[46].call(closure);
      }
      if ((((subComponent instanceof LifeCycle)) && (DefaultTypeTransformation.booleanUnbox(arrayOfCallSite[47].call(NoAutoStartUtil.class, subComponent))) ? 1 : 0) != 0) {
        arrayOfCallSite[48].call(subComponent);
      }
      arrayOfCallSite[49].call(PropertyUtil.class, nestingType, component, subComponent, name);
    } else {
      arrayOfCallSite[50].callCurrent(this, new GStringImpl(new Object[] { name, getLabel(), getComponentName(), arrayOfCallSite[51].callGetProperty(arrayOfCallSite[52].call(component)) }, new String[] { "No 'class' argument specified for [", "] in ", " ", " of type [", "]" }));
    }
  }
  
  public void cascadeFields(ComponentDelegate subDelegate) {
    CallSite[] arrayOfCallSite = $getCallSiteArray();String k = null; Object localObject; for (Iterator localIterator = (Iterator)ScriptBytecodeAdapter.castToType(arrayOfCallSite[53].call(fieldsToCascade), Iterator.class); localIterator.hasNext(); 
        ScriptBytecodeAdapter.setProperty(localObject, null, arrayOfCallSite[54].callGroovyObjectGetProperty(subDelegate), (String)ShortTypeHandling.castToString(new GStringImpl(new Object[] { k }, new String[] { "", "" }))))
    {
      k = (String)ShortTypeHandling.castToString(localIterator.next());
      localObject = ScriptBytecodeAdapter.getGroovyObjectProperty(ComponentDelegate.class, this, (String)ShortTypeHandling.castToString(new GStringImpl(new Object[] { k }, new String[] { "", "" })));
    }
  }
  
  public void injectParent(Object subComponent) {
    CallSite[] arrayOfCallSite = $getCallSiteArray(); Object localObject; if (DefaultTypeTransformation.booleanUnbox(arrayOfCallSite[55].call(subComponent, "parent"))) {
      localObject = component;ScriptBytecodeAdapter.setProperty(localObject, null, subComponent, "parent");
    }
  }
  
  public void propertyMissing(String name, Object value) {
    CallSite[] arrayOfCallSite = $getCallSiteArray();NestingType nestingType = (NestingType)ShortTypeHandling.castToEnum(arrayOfCallSite[56].call(PropertyUtil.class, component, name), NestingType.class);
    if (ScriptBytecodeAdapter.compareEqual(nestingType, arrayOfCallSite[57].callGetProperty(NestingType.class))) {
      arrayOfCallSite[58].callCurrent(this, new GStringImpl(new Object[] { arrayOfCallSite[59].callCurrent(this), arrayOfCallSite[60].callCurrent(this), arrayOfCallSite[61].callGetProperty(arrayOfCallSite[62].call(component)), name }, new String[] { "", " ", " of type [", "] has no appplicable [", "] property " }));
      return;
    }
    arrayOfCallSite[63].call(PropertyUtil.class, nestingType, component, value, name);
  }
  
  public Object analyzeArgs(Object... args)
  {
    CallSite[] arrayOfCallSite = $getCallSiteArray();String name = null;
    Class clazz = null;
    Closure closure = null;
    
    if (ScriptBytecodeAdapter.compareGreaterThan(arrayOfCallSite[64].call(args), Integer.valueOf(3))) {
      arrayOfCallSite[65].callCurrent(this, new GStringImpl(new Object[] { args }, new String[] { "At most 3 arguments allowed but you passed ", "" }));
      return ScriptBytecodeAdapter.createList(new Object[] { name, clazz, closure });
    }
    
    if ((__$stMC) || (BytecodeInterface8.disabledStandardMetaClass())) { if ((arrayOfCallSite[66].call(args, Integer.valueOf(-1)) instanceof Closure)) {
        Object localObject1 = arrayOfCallSite[67].call(args, Integer.valueOf(-1));closure = (Closure)ScriptBytecodeAdapter.castToType(localObject1, Closure.class); Object 
          tmp202_197 = arrayOfCallSite[68].call(args, arrayOfCallSite[69].call(args, Integer.valueOf(-1)));args = (Object[])ScriptBytecodeAdapter.castToType(tmp202_197, [Ljava.lang.Object.class);tmp202_197;
      }
    }
    else if ((BytecodeInterface8.objectArrayGet(args, Integer.valueOf(-1).intValue()) instanceof Closure)) {
      Object localObject2 = BytecodeInterface8.objectArrayGet(args, Integer.valueOf(-1).intValue());closure = (Closure)ScriptBytecodeAdapter.castToType(localObject2, Closure.class); Object 
        tmp287_282 = arrayOfCallSite[70].call(args, BytecodeInterface8.objectArrayGet(args, Integer.valueOf(-1).intValue()));args = (Object[])ScriptBytecodeAdapter.castToType(tmp287_282, [Ljava.lang.Object.class);tmp287_282; }
    Object localObject3;
    Object localObject4;
    if ((!BytecodeInterface8.isOrigInt()) || (!BytecodeInterface8.isOrigZ()) || (__$stMC) || (BytecodeInterface8.disabledStandardMetaClass())) { if (ScriptBytecodeAdapter.compareEqual(arrayOfCallSite[71].call(args), Integer.valueOf(1))) {
        localObject3 = arrayOfCallSite[72].callCurrent(this, arrayOfCallSite[73].call(args, Integer.valueOf(0)));clazz = (Class)ShortTypeHandling.castToClass(localObject3);
      }
    }
    else if (ScriptBytecodeAdapter.compareEqual(arrayOfCallSite[74].call(args), Integer.valueOf(1))) {
      localObject4 = arrayOfCallSite[75].callCurrent(this, BytecodeInterface8.objectArrayGet(args, 0));clazz = (Class)ShortTypeHandling.castToClass(localObject4); }
    Object localObject6;
    Object localObject8;
    if ((!BytecodeInterface8.isOrigInt()) || (!BytecodeInterface8.isOrigZ()) || (__$stMC) || (BytecodeInterface8.disabledStandardMetaClass())) { if (ScriptBytecodeAdapter.compareEqual(arrayOfCallSite[76].call(args), Integer.valueOf(2))) {
        Object localObject5 = arrayOfCallSite[77].callCurrent(this, arrayOfCallSite[78].call(args, Integer.valueOf(0)));name = (String)ShortTypeHandling.castToString(localObject5);
        localObject6 = arrayOfCallSite[79].callCurrent(this, arrayOfCallSite[80].call(args, Integer.valueOf(1)));clazz = (Class)ShortTypeHandling.castToClass(localObject6);
      }
    }
    else if (ScriptBytecodeAdapter.compareEqual(arrayOfCallSite[81].call(args), Integer.valueOf(2))) {
      Object localObject7 = arrayOfCallSite[82].callCurrent(this, BytecodeInterface8.objectArrayGet(args, 0));name = (String)ShortTypeHandling.castToString(localObject7);
      localObject8 = arrayOfCallSite[83].callCurrent(this, BytecodeInterface8.objectArrayGet(args, 1));clazz = (Class)ShortTypeHandling.castToClass(localObject8);
    }
    
    return ScriptBytecodeAdapter.createList(new Object[] { name, clazz, closure });return null;
  }
  
  public Class parseClassArgument(Object arg) {
    CallSite[] arrayOfCallSite = $getCallSiteArray(); if ((arg instanceof Class)) {
      return (Class)ShortTypeHandling.castToClass(arg);
    } else if ((arg instanceof String)) {
      return Class.forName((String)ShortTypeHandling.castToString(arg));
    } else {
      arrayOfCallSite[84].callCurrent(this, new GStringImpl(new Object[] { arrayOfCallSite[85].callGetProperty(arrayOfCallSite[86].call(arg)) }, new String[] { "Unexpected argument type ", "" }));
      return (Class)ShortTypeHandling.castToClass(null); } return null;
  }
  
  public String parseNameArgument(Object arg)
  {
    CallSite[] arrayOfCallSite = $getCallSiteArray(); if ((arg instanceof String)) {
      return (String)ShortTypeHandling.castToString(arg);
    } else {
      arrayOfCallSite[87].callCurrent(this, "With 2 or 3 arguments, the first argument must be the component name, i.e of type string");
      return (String)ShortTypeHandling.castToString(null); } return null;
  }
  
  public String getComponentName()
  {
    CallSite[] arrayOfCallSite = $getCallSiteArray(); if (DefaultTypeTransformation.booleanUnbox(arrayOfCallSite[88].call(component, "name"))) {
      return (String)ShortTypeHandling.castToString(new GStringImpl(new Object[] { arrayOfCallSite[89].callGetProperty(component) }, new String[] { "[", "]" }));
    } else
      return ""; return null;
  }
  
  public final Object getComponent()
  {
    return component;
  }
  
  public final List getFieldsToCascade()
  {
    return fieldsToCascade;
  }
}
