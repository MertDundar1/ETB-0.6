package com.sun.jna;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;




















public abstract class Union
  extends Structure
{
  private Structure.StructField activeField;
  Structure.StructField biggestField;
  
  protected Union() {}
  
  protected Union(Pointer p)
  {
    super(p);
  }
  
  protected Union(Pointer p, int alignType) {
    super(p, alignType);
  }
  
  protected Union(TypeMapper mapper) {
    super(mapper);
  }
  
  protected Union(Pointer p, int alignType, TypeMapper mapper) {
    super(p, alignType, mapper);
  }
  




  public void setType(Class type)
  {
    ensureAllocated();
    for (Iterator i = fields().values().iterator(); i.hasNext();) {
      Structure.StructField f = (Structure.StructField)i.next();
      if (type == type) {
        activeField = f;
        return;
      }
    }
    throw new IllegalArgumentException("No field of type " + type + " in " + this);
  }
  




  public void setType(String fieldName)
  {
    ensureAllocated();
    Structure.StructField f = (Structure.StructField)fields().get(fieldName);
    if (f != null) {
      activeField = f;
    }
    else {
      throw new IllegalArgumentException("No field named " + fieldName + " in " + this);
    }
  }
  




  public Object readField(String fieldName)
  {
    ensureAllocated();
    setType(fieldName);
    return super.readField(fieldName);
  }
  



  public void writeField(String fieldName)
  {
    ensureAllocated();
    setType(fieldName);
    super.writeField(fieldName);
  }
  



  public void writeField(String fieldName, Object value)
  {
    ensureAllocated();
    setType(fieldName);
    super.writeField(fieldName, value);
  }
  











  public Object getTypedValue(Class type)
  {
    ensureAllocated();
    for (Iterator i = fields().values().iterator(); i.hasNext();) {
      Structure.StructField f = (Structure.StructField)i.next();
      if (type == type) {
        activeField = f;
        read();
        return getField(activeField);
      }
    }
    throw new IllegalArgumentException("No field of type " + type + " in " + this);
  }
  









  public Object setTypedValue(Object object)
  {
    Structure.StructField f = findField(object.getClass());
    if (f != null) {
      activeField = f;
      setField(f, object);
      return this;
    }
    throw new IllegalArgumentException("No field of type " + object.getClass() + " in " + this);
  }
  




  private Structure.StructField findField(Class type)
  {
    ensureAllocated();
    for (Iterator i = fields().values().iterator(); i.hasNext();) {
      Structure.StructField f = (Structure.StructField)i.next();
      if (type.isAssignableFrom(type)) {
        return f;
      }
    }
    return null;
  }
  
  void writeField(Structure.StructField field)
  {
    if (field == activeField) {
      super.writeField(field);
    }
  }
  



  Object readField(Structure.StructField field)
  {
    if ((field == activeField) || ((!Structure.class.isAssignableFrom(type)) && (!String.class.isAssignableFrom(type)) && (!WString.class.isAssignableFrom(type))))
    {


      return super.readField(field);
    }
    


    return null;
  }
  


  int calculateSize(boolean force, boolean avoidFFIType)
  {
    int size = super.calculateSize(force, avoidFFIType);
    if (size != -1) {
      int fsize = 0;
      for (Iterator i = fields().values().iterator(); i.hasNext();) {
        Structure.StructField f = (Structure.StructField)i.next();
        offset = 0;
        if ((size > fsize) || ((size == fsize) && (Structure.class.isAssignableFrom(type))))
        {






          fsize = size;
          biggestField = f;
        }
      }
      size = calculateAlignedSize(fsize);
      if (size > 0)
      {
        if (((this instanceof Structure.ByValue)) && (!avoidFFIType)) {
          getTypeInfo();
        }
      }
    }
    return size;
  }
  
  protected int getNativeAlignment(Class type, Object value, boolean isFirstElement) {
    return super.getNativeAlignment(type, value, true);
  }
  



  Pointer getTypeInfo()
  {
    if (biggestField == null)
    {
      return null;
    }
    return super.getTypeInfo();
  }
}
