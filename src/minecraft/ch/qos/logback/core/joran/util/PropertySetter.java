package ch.qos.logback.core.joran.util;

import ch.qos.logback.core.joran.spi.DefaultClass;
import ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.util.AggregationType;
import ch.qos.logback.core.util.PropertySetterException;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;












































public class PropertySetter
  extends ContextAwareBase
{
  protected Object obj;
  protected Class<?> objClass;
  protected PropertyDescriptor[] propertyDescriptors;
  protected MethodDescriptor[] methodDescriptors;
  
  public PropertySetter(Object obj)
  {
    this.obj = obj;
    objClass = obj.getClass();
  }
  


  protected void introspect()
  {
    try
    {
      BeanInfo bi = Introspector.getBeanInfo(obj.getClass());
      propertyDescriptors = bi.getPropertyDescriptors();
      methodDescriptors = bi.getMethodDescriptors();
    } catch (IntrospectionException ex) {
      addError("Failed to introspect " + obj + ": " + ex.getMessage());
      propertyDescriptors = new PropertyDescriptor[0];
      methodDescriptors = new MethodDescriptor[0];
    }
  }
  

















  public void setProperty(String name, String value)
  {
    if (value == null) {
      return;
    }
    
    name = Introspector.decapitalize(name);
    
    PropertyDescriptor prop = getPropertyDescriptor(name);
    
    if (prop == null) {
      addWarn("No such property [" + name + "] in " + objClass.getName() + ".");
    } else {
      try {
        setProperty(prop, name, value);
      } catch (PropertySetterException ex) {
        addWarn("Failed to set property [" + name + "] to value \"" + value + "\". ", ex);
      }
    }
  }
  











  public void setProperty(PropertyDescriptor prop, String name, String value)
    throws PropertySetterException
  {
    Method setter = prop.getWriteMethod();
    
    if (setter == null) {
      throw new PropertySetterException("No setter for property [" + name + "].");
    }
    

    Class<?>[] paramTypes = setter.getParameterTypes();
    
    if (paramTypes.length != 1) {
      throw new PropertySetterException("#params for setter != 1");
    }
    
    Object arg;
    try
    {
      arg = StringToObjectConverter.convertArg(this, value, paramTypes[0]);
    } catch (Throwable t) {
      throw new PropertySetterException("Conversion to type [" + paramTypes[0] + "] failed. ", t);
    }
    

    if (arg == null) {
      throw new PropertySetterException("Conversion to type [" + paramTypes[0] + "] failed.");
    }
    try
    {
      setter.invoke(obj, new Object[] { arg });
    } catch (Exception ex) {
      throw new PropertySetterException(ex);
    }
  }
  
  public AggregationType computeAggregationType(String name) {
    String cName = capitalizeFirstLetter(name);
    
    Method addMethod = findAdderMethod(cName);
    

    if (addMethod != null) {
      AggregationType type = computeRawAggregationType(addMethod);
      switch (1.$SwitchMap$ch$qos$logback$core$util$AggregationType[type.ordinal()]) {
      case 1: 
        return AggregationType.NOT_FOUND;
      case 2: 
        return AggregationType.AS_BASIC_PROPERTY_COLLECTION;
      case 3: 
        return AggregationType.AS_COMPLEX_PROPERTY_COLLECTION;
      }
      
    }
    Method setterMethod = findSetterMethod(name);
    if (setterMethod != null) {
      return computeRawAggregationType(setterMethod);
    }
    
    return AggregationType.NOT_FOUND;
  }
  
  private Method findAdderMethod(String name)
  {
    name = capitalizeFirstLetter(name);
    return getMethod("add" + name);
  }
  
  private Method findSetterMethod(String name) {
    String dName = Introspector.decapitalize(name);
    PropertyDescriptor propertyDescriptor = getPropertyDescriptor(dName);
    if (propertyDescriptor != null) {
      return propertyDescriptor.getWriteMethod();
    }
    return null;
  }
  
  private Class<?> getParameterClassForMethod(Method method)
  {
    if (method == null) {
      return null;
    }
    Class<?>[] classArray = method.getParameterTypes();
    if (classArray.length != 1) {
      return null;
    }
    return classArray[0];
  }
  
  private AggregationType computeRawAggregationType(Method method)
  {
    Class<?> parameterClass = getParameterClassForMethod(method);
    if (parameterClass == null) {
      return AggregationType.NOT_FOUND;
    }
    if (StringToObjectConverter.canBeBuiltFromSimpleString(parameterClass)) {
      return AggregationType.AS_BASIC_PROPERTY;
    }
    return AggregationType.AS_COMPLEX_PROPERTY;
  }
  







  private boolean isUnequivocallyInstantiable(Class<?> clazz)
  {
    if (clazz.isInterface()) {
      return false;
    }
    


    try
    {
      Object o = clazz.newInstance();
      if (o != null) {
        return true;
      }
      return false;
    }
    catch (InstantiationException e) {
      return false;
    } catch (IllegalAccessException e) {}
    return false;
  }
  
  public Class<?> getObjClass()
  {
    return objClass;
  }
  
  public void addComplexProperty(String name, Object complexProperty) {
    Method adderMethod = findAdderMethod(name);
    
    if (adderMethod != null) {
      Class<?>[] paramTypes = adderMethod.getParameterTypes();
      if (!isSanityCheckSuccessful(name, adderMethod, paramTypes, complexProperty))
      {
        return;
      }
      invokeMethodWithSingleParameterOnThisObject(adderMethod, complexProperty);
    } else {
      addError("Could not find method [add" + name + "] in class [" + objClass.getName() + "].");
    }
  }
  

  void invokeMethodWithSingleParameterOnThisObject(Method method, Object parameter)
  {
    Class<?> ccc = parameter.getClass();
    try {
      method.invoke(obj, new Object[] { parameter });
    } catch (Exception e) {
      addError("Could not invoke method " + method.getName() + " in class " + obj.getClass().getName() + " with parameter of type " + ccc.getName(), e);
    }
  }
  


  public void addBasicProperty(String name, String strValue)
  {
    if (strValue == null) {
      return;
    }
    
    name = capitalizeFirstLetter(name);
    Method adderMethod = findAdderMethod(name);
    
    if (adderMethod == null) {
      addError("No adder for property [" + name + "].");
      return;
    }
    
    Class<?>[] paramTypes = adderMethod.getParameterTypes();
    isSanityCheckSuccessful(name, adderMethod, paramTypes, strValue);
    Object arg;
    try
    {
      arg = StringToObjectConverter.convertArg(this, strValue, paramTypes[0]);
    } catch (Throwable t) {
      addError("Conversion to type [" + paramTypes[0] + "] failed. ", t);
      return;
    }
    if (arg != null) {
      invokeMethodWithSingleParameterOnThisObject(adderMethod, strValue);
    }
  }
  
  public void setComplexProperty(String name, Object complexProperty) {
    String dName = Introspector.decapitalize(name);
    PropertyDescriptor propertyDescriptor = getPropertyDescriptor(dName);
    
    if (propertyDescriptor == null) {
      addWarn("Could not find PropertyDescriptor for [" + name + "] in " + objClass.getName());
      

      return;
    }
    
    Method setter = propertyDescriptor.getWriteMethod();
    
    if (setter == null) {
      addWarn("Not setter method for property [" + name + "] in " + obj.getClass().getName());
      

      return;
    }
    
    Class<?>[] paramTypes = setter.getParameterTypes();
    
    if (!isSanityCheckSuccessful(name, setter, paramTypes, complexProperty)) {
      return;
    }
    try {
      invokeMethodWithSingleParameterOnThisObject(setter, complexProperty);
    }
    catch (Exception e) {
      addError("Could not set component " + obj + " for parent component " + obj, e);
    }
  }
  

  private boolean isSanityCheckSuccessful(String name, Method method, Class<?>[] params, Object complexProperty)
  {
    Class<?> ccc = complexProperty.getClass();
    if (params.length != 1) {
      addError("Wrong number of parameters in setter method for property [" + name + "] in " + obj.getClass().getName());
      

      return false;
    }
    
    if (!params[0].isAssignableFrom(complexProperty.getClass())) {
      addError("A \"" + ccc.getName() + "\" object is not assignable to a \"" + params[0].getName() + "\" variable.");
      
      addError("The class \"" + params[0].getName() + "\" was loaded by ");
      addError("[" + params[0].getClassLoader() + "] whereas object of type ");
      addError("\"" + ccc.getName() + "\" was loaded by [" + ccc.getClassLoader() + "].");
      
      return false;
    }
    
    return true;
  }
  
  private String capitalizeFirstLetter(String name) {
    return name.substring(0, 1).toUpperCase() + name.substring(1);
  }
  
  protected Method getMethod(String methodName) {
    if (methodDescriptors == null) {
      introspect();
    }
    
    for (int i = 0; i < methodDescriptors.length; i++) {
      if (methodName.equals(methodDescriptors[i].getName())) {
        return methodDescriptors[i].getMethod();
      }
    }
    
    return null;
  }
  
  protected PropertyDescriptor getPropertyDescriptor(String name) {
    if (propertyDescriptors == null) {
      introspect();
    }
    
    for (int i = 0; i < propertyDescriptors.length; i++)
    {

      if (name.equals(propertyDescriptors[i].getName()))
      {
        return propertyDescriptors[i];
      }
    }
    
    return null;
  }
  
  public Object getObj() {
    return obj;
  }
  
  Method getRelevantMethod(String name, AggregationType aggregationType) {
    String cName = capitalizeFirstLetter(name);
    Method relevantMethod;
    if (aggregationType == AggregationType.AS_COMPLEX_PROPERTY_COLLECTION) {
      relevantMethod = findAdderMethod(cName); } else { Method relevantMethod;
      if (aggregationType == AggregationType.AS_COMPLEX_PROPERTY) {
        relevantMethod = findSetterMethod(cName);
      } else
        throw new IllegalStateException(aggregationType + " not allowed here"); }
    Method relevantMethod;
    return relevantMethod;
  }
  

  <T extends Annotation> T getAnnotation(String name, Class<T> annonationClass, Method relevantMethod)
  {
    if (relevantMethod != null) {
      return relevantMethod.getAnnotation(annonationClass);
    }
    return null;
  }
  
  Class<?> getDefaultClassNameByAnnonation(String name, Method relevantMethod)
  {
    DefaultClass defaultClassAnnon = (DefaultClass)getAnnotation(name, DefaultClass.class, relevantMethod);
    
    if (defaultClassAnnon != null) {
      return defaultClassAnnon.value();
    }
    return null;
  }
  
  Class<?> getByConcreteType(String name, Method relevantMethod)
  {
    Class<?> paramType = getParameterClassForMethod(relevantMethod);
    if (paramType == null) {
      return null;
    }
    
    boolean isUnequivocallyInstantiable = isUnequivocallyInstantiable(paramType);
    if (isUnequivocallyInstantiable) {
      return paramType;
    }
    return null;
  }
  



  public Class<?> getClassNameViaImplicitRules(String name, AggregationType aggregationType, DefaultNestedComponentRegistry registry)
  {
    Class<?> registryResult = registry.findDefaultComponentType(obj.getClass(), name);
    
    if (registryResult != null) {
      return registryResult;
    }
    
    Method relevantMethod = getRelevantMethod(name, aggregationType);
    if (relevantMethod == null) {
      return null;
    }
    Class<?> byAnnotation = getDefaultClassNameByAnnonation(name, relevantMethod);
    if (byAnnotation != null) {
      return byAnnotation;
    }
    return getByConcreteType(name, relevantMethod);
  }
}
