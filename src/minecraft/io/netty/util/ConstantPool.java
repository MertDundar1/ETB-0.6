package io.netty.util;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;























public abstract class ConstantPool<T extends Constant<T>>
{
  private final ConcurrentMap<String, T> constants = PlatformDependent.newConcurrentHashMap();
  
  private AtomicInteger nextId = new AtomicInteger(1);
  
  public ConstantPool() {}
  
  public T valueOf(Class<?> firstNameComponent, String secondNameComponent)
  {
    if (firstNameComponent == null) {
      throw new NullPointerException("firstNameComponent");
    }
    if (secondNameComponent == null) {
      throw new NullPointerException("secondNameComponent");
    }
    
    return valueOf(firstNameComponent.getName() + '#' + secondNameComponent);
  }
  







  public T valueOf(String name)
  {
    checkNotNullAndNotEmpty(name);
    return getOrCreate(name);
  }
  




  private T getOrCreate(String name)
  {
    T constant = (Constant)constants.get(name);
    if (constant == null) {
      T tempConstant = newConstant(nextId(), name);
      constant = (Constant)constants.putIfAbsent(name, tempConstant);
      if (constant == null) {
        return tempConstant;
      }
    }
    
    return constant;
  }
  


  public boolean exists(String name)
  {
    checkNotNullAndNotEmpty(name);
    return constants.containsKey(name);
  }
  



  public T newInstance(String name)
  {
    checkNotNullAndNotEmpty(name);
    return createOrThrow(name);
  }
  




  private T createOrThrow(String name)
  {
    T constant = (Constant)constants.get(name);
    if (constant == null) {
      T tempConstant = newConstant(nextId(), name);
      constant = (Constant)constants.putIfAbsent(name, tempConstant);
      if (constant == null) {
        return tempConstant;
      }
    }
    
    throw new IllegalArgumentException(String.format("'%s' is already in use", new Object[] { name }));
  }
  
  private String checkNotNullAndNotEmpty(String name) {
    ObjectUtil.checkNotNull(name, "name");
    
    if (name.isEmpty()) {
      throw new IllegalArgumentException("empty name");
    }
    
    return name;
  }
  
  protected abstract T newConstant(int paramInt, String paramString);
  
  @Deprecated
  public final int nextId() {
    return nextId.getAndIncrement();
  }
}
