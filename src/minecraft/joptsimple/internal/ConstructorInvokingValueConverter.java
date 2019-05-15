package joptsimple.internal;

import java.lang.reflect.Constructor;
import joptsimple.ValueConverter;






























class ConstructorInvokingValueConverter<V>
  implements ValueConverter<V>
{
  private final Constructor<V> ctor;
  
  ConstructorInvokingValueConverter(Constructor<V> ctor)
  {
    this.ctor = ctor;
  }
  
  public V convert(String value) {
    return Reflection.instantiate(ctor, new Object[] { value });
  }
  
  public Class<V> valueType() {
    return ctor.getDeclaringClass();
  }
  
  public String valuePattern() {
    return null;
  }
}
