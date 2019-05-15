package com.google.common.reflect;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import javax.annotation.Nullable;




























@Beta
public final class Parameter
  implements AnnotatedElement
{
  private final Invokable<?, ?> declaration;
  private final int position;
  private final TypeToken<?> type;
  private final ImmutableList<Annotation> annotations;
  
  Parameter(Invokable<?, ?> declaration, int position, TypeToken<?> type, Annotation[] annotations)
  {
    this.declaration = declaration;
    this.position = position;
    this.type = type;
    this.annotations = ImmutableList.copyOf(annotations);
  }
  
  public TypeToken<?> getType()
  {
    return type;
  }
  
  public Invokable<?, ?> getDeclaringInvokable()
  {
    return declaration;
  }
  
  public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
    return getAnnotation(annotationType) != null;
  }
  
  @Nullable
  public <A extends Annotation> A getAnnotation(Class<A> annotationType)
  {
    Preconditions.checkNotNull(annotationType);
    for (Annotation annotation : annotations) {
      if (annotationType.isInstance(annotation)) {
        return (Annotation)annotationType.cast(annotation);
      }
    }
    return null;
  }
  
  public Annotation[] getAnnotations() {
    return getDeclaredAnnotations();
  }
  
  public Annotation[] getDeclaredAnnotations() {
    return (Annotation[])annotations.toArray(new Annotation[annotations.size()]);
  }
  
  public boolean equals(@Nullable Object obj) {
    if ((obj instanceof Parameter)) {
      Parameter that = (Parameter)obj;
      return (position == position) && (declaration.equals(declaration));
    }
    return false;
  }
  
  public int hashCode() {
    return position;
  }
  
  public String toString() {
    return type + " arg" + position;
  }
}
