package ch.qos.logback.classic.gaffer;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.beans.Introspector;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class PropertyUtil implements GroovyObject
{
  public PropertyUtil()
  {
    PropertyUtil this;
    CallSite[] arrayOfCallSite = $getCallSiteArray();
    MetaClass localMetaClass = $getStaticMetaClass();
    metaClass = localMetaClass;
  }
  
  public static boolean hasAdderMethod(Object obj, String name)
  {
    CallSite[] arrayOfCallSite = $getCallSiteArray();String addMethod = null; GStringImpl localGStringImpl1; GStringImpl localGStringImpl2; if ((__$stMC) || (BytecodeInterface8.disabledStandardMetaClass())) { localGStringImpl1 = new GStringImpl(new Object[] { arrayOfCallSite[0].callStatic(PropertyUtil.class, name) }, new String[] { "add", "" });addMethod = (String)ShortTypeHandling.castToString(localGStringImpl1); } else { localGStringImpl2 = new GStringImpl(new Object[] { upperCaseFirstLetter(name) }, new String[] { "add", "" });addMethod = (String)ShortTypeHandling.castToString(localGStringImpl2); }
    return DefaultTypeTransformation.booleanUnbox(arrayOfCallSite[1].call(arrayOfCallSite[2].callGetProperty(obj), obj, addMethod));return DefaultTypeTransformation.booleanUnbox(Integer.valueOf(0));
  }
  
  public static NestingType nestingType(Object obj, String name) {
    CallSite[] arrayOfCallSite = $getCallSiteArray();Object decapitalizedName = arrayOfCallSite[3].call(Introspector.class, name);
    if (DefaultTypeTransformation.booleanUnbox(arrayOfCallSite[4].call(obj, decapitalizedName))) {
      return (NestingType)ShortTypeHandling.castToEnum(arrayOfCallSite[5].callGetProperty(NestingType.class), NestingType.class);
    }
    if (DefaultTypeTransformation.booleanUnbox(arrayOfCallSite[6].callStatic(PropertyUtil.class, obj, name))) {
      return (NestingType)ShortTypeHandling.castToEnum(arrayOfCallSite[7].callGetProperty(NestingType.class), NestingType.class);
    }
    return (NestingType)ShortTypeHandling.castToEnum(arrayOfCallSite[8].callGetProperty(NestingType.class), NestingType.class);return null;
  }
  
  public static void attach(NestingType nestingType, Object component, Object subComponent, String name) {
    CallSite[] arrayOfCallSite = $getCallSiteArray();NestingType localNestingType = nestingType;
    if (ScriptBytecodeAdapter.isCase(localNestingType, arrayOfCallSite[9].callGetProperty(NestingType.class))) {
      Object localObject1 = arrayOfCallSite[10].call(Introspector.class, name);name = (String)ShortTypeHandling.castToString(localObject1);
      Object localObject2 = subComponent;ScriptBytecodeAdapter.setProperty(localObject2, null, component, (String)ShortTypeHandling.castToString(new GStringImpl(new Object[] { name }, new String[] { "", "" })));
      return;
    } else if (!ScriptBytecodeAdapter.isCase(localNestingType, arrayOfCallSite[11].callGetProperty(NestingType.class))) { return; }
    String firstUpperName = (String)ShortTypeHandling.castToString(arrayOfCallSite[12].call(PropertyUtil.class, name));
    ScriptBytecodeAdapter.invokeMethodN(PropertyUtil.class, component, (String)ShortTypeHandling.castToString(new GStringImpl(new Object[] { firstUpperName }, new String[] { "add", "" })), new Object[] { subComponent });
  }
  

  public static String transformFirstLetter(String s, Closure closure)
  {
    CallSite[] arrayOfCallSite = $getCallSiteArray(); if ((!BytecodeInterface8.isOrigInt()) || (!BytecodeInterface8.isOrigZ()) || (__$stMC) || (BytecodeInterface8.disabledStandardMetaClass())) { if (((ScriptBytecodeAdapter.compareEqual(s, null)) || (ScriptBytecodeAdapter.compareEqual(arrayOfCallSite[13].call(s), Integer.valueOf(0))) ? 1 : 0) != 0) {
        return s;
      }
    }
    else if (((ScriptBytecodeAdapter.compareEqual(s, null)) || (ScriptBytecodeAdapter.compareEqual(arrayOfCallSite[14].call(s), Integer.valueOf(0))) ? 1 : 0) != 0) {
      return s;
    }
    String firstLetter = (String)ShortTypeHandling.castToString(arrayOfCallSite[15].callConstructor(String.class, arrayOfCallSite[16].call(s, Integer.valueOf(0))));
    
    String modifiedFistLetter = (String)ShortTypeHandling.castToString(arrayOfCallSite[17].call(closure, firstLetter));
    
    if (ScriptBytecodeAdapter.compareEqual(arrayOfCallSite[18].call(s), Integer.valueOf(1))) {
      return modifiedFistLetter;
    } else
      return (String)ShortTypeHandling.castToString(arrayOfCallSite[19].call(modifiedFistLetter, arrayOfCallSite[20].call(s, Integer.valueOf(1)))); return null;
  }
  
  class _upperCaseFirstLetter_closure1 extends Closure implements GeneratedClosure { public _upperCaseFirstLetter_closure1(Object _thisObject) { super(_thisObject); }
    
    public Object doCall(String it) { CallSite[] arrayOfCallSite = $getCallSiteArray();return arrayOfCallSite[0].call(it);return null; } public Object call(String it) { CallSite[] arrayOfCallSite = $getCallSiteArray(); if ((__$stMC) || (BytecodeInterface8.disabledStandardMetaClass())) return arrayOfCallSite[1].callCurrent(this, it); else return doCall(it); return null; } } public static String upperCaseFirstLetter(String s) { CallSite[] arrayOfCallSite = $getCallSiteArray();return (String)ShortTypeHandling.castToString(arrayOfCallSite[21].callStatic(PropertyUtil.class, s, new _upperCaseFirstLetter_closure1(PropertyUtil.class, PropertyUtil.class)));return null;
  }
}
