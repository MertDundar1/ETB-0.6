package joptsimple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import joptsimple.internal.Objects;



























public class OptionSet
{
  private final List<OptionSpec<?>> detectedSpecs;
  private final Map<String, AbstractOptionSpec<?>> detectedOptions;
  private final Map<AbstractOptionSpec<?>, List<String>> optionsToArguments;
  private final Map<String, AbstractOptionSpec<?>> recognizedSpecs;
  private final Map<String, List<?>> defaultValues;
  
  OptionSet(Map<String, AbstractOptionSpec<?>> recognizedSpecs)
  {
    detectedSpecs = new ArrayList();
    detectedOptions = new HashMap();
    optionsToArguments = new IdentityHashMap();
    defaultValues = defaultValues(recognizedSpecs);
    this.recognizedSpecs = recognizedSpecs;
  }
  




  public boolean hasOptions()
  {
    return !detectedOptions.isEmpty();
  }
  






  public boolean has(String option)
  {
    return detectedOptions.containsKey(option);
  }
  












  public boolean has(OptionSpec<?> option)
  {
    return optionsToArguments.containsKey(option);
  }
  






  public boolean hasArgument(String option)
  {
    AbstractOptionSpec<?> spec = (AbstractOptionSpec)detectedOptions.get(option);
    return (spec != null) && (hasArgument(spec));
  }
  













  public boolean hasArgument(OptionSpec<?> option)
  {
    Objects.ensureNotNull(option);
    
    List<String> values = (List)optionsToArguments.get(option);
    return (values != null) && (!values.isEmpty());
  }
  













  public Object valueOf(String option)
  {
    Objects.ensureNotNull(option);
    
    AbstractOptionSpec<?> spec = (AbstractOptionSpec)detectedOptions.get(option);
    if (spec == null) {
      List<?> defaults = defaultValuesFor(option);
      return defaults.isEmpty() ? null : defaults.get(0);
    }
    
    return valueOf(spec);
  }
  












  public <V> V valueOf(OptionSpec<V> option)
  {
    Objects.ensureNotNull(option);
    
    List<V> values = valuesOf(option);
    switch (values.size()) {
    case 0: 
      return null;
    case 1: 
      return values.get(0);
    }
    throw new MultipleArgumentsForOptionException(option.options());
  }
  









  public List<?> valuesOf(String option)
  {
    Objects.ensureNotNull(option);
    
    AbstractOptionSpec<?> spec = (AbstractOptionSpec)detectedOptions.get(option);
    return spec == null ? defaultValuesFor(option) : valuesOf(spec);
  }
  













  public <V> List<V> valuesOf(OptionSpec<V> option)
  {
    Objects.ensureNotNull(option);
    
    List<String> values = (List)optionsToArguments.get(option);
    if ((values == null) || (values.isEmpty())) {
      return defaultValueFor(option);
    }
    AbstractOptionSpec<V> spec = (AbstractOptionSpec)option;
    List<V> convertedValues = new ArrayList();
    for (String each : values) {
      convertedValues.add(spec.convert(each));
    }
    return Collections.unmodifiableList(convertedValues);
  }
  





  public List<OptionSpec<?>> specs()
  {
    List<OptionSpec<?>> specs = detectedSpecs;
    specs.remove(detectedOptions.get("[arguments]"));
    
    return Collections.unmodifiableList(specs);
  }
  




  public Map<OptionSpec<?>, List<?>> asMap()
  {
    Map<OptionSpec<?>, List<?>> map = new HashMap();
    for (AbstractOptionSpec<?> spec : recognizedSpecs.values())
      if (!spec.representsNonOptions())
        map.put(spec, valuesOf(spec));
    return Collections.unmodifiableMap(map);
  }
  


  public List<?> nonOptionArguments()
  {
    return Collections.unmodifiableList(valuesOf((OptionSpec)detectedOptions.get("[arguments]")));
  }
  
  void add(AbstractOptionSpec<?> spec) {
    addWithArgument(spec, null);
  }
  
  void addWithArgument(AbstractOptionSpec<?> spec, String argument) {
    detectedSpecs.add(spec);
    
    for (String each : spec.options()) {
      detectedOptions.put(each, spec);
    }
    List<String> optionArguments = (List)optionsToArguments.get(spec);
    
    if (optionArguments == null) {
      optionArguments = new ArrayList();
      optionsToArguments.put(spec, optionArguments);
    }
    
    if (argument != null) {
      optionArguments.add(argument);
    }
  }
  
  public boolean equals(Object that) {
    if (this == that) {
      return true;
    }
    if ((that == null) || (!getClass().equals(that.getClass()))) {
      return false;
    }
    OptionSet other = (OptionSet)that;
    Map<AbstractOptionSpec<?>, List<String>> thisOptionsToArguments = new HashMap(optionsToArguments);
    
    Map<AbstractOptionSpec<?>, List<String>> otherOptionsToArguments = new HashMap(optionsToArguments);
    
    return (detectedOptions.equals(detectedOptions)) && (thisOptionsToArguments.equals(otherOptionsToArguments));
  }
  

  public int hashCode()
  {
    Map<AbstractOptionSpec<?>, List<String>> thisOptionsToArguments = new HashMap(optionsToArguments);
    
    return detectedOptions.hashCode() ^ thisOptionsToArguments.hashCode();
  }
  
  private <V> List<V> defaultValuesFor(String option)
  {
    if (defaultValues.containsKey(option)) {
      return (List)defaultValues.get(option);
    }
    return Collections.emptyList();
  }
  
  private <V> List<V> defaultValueFor(OptionSpec<V> option) {
    return defaultValuesFor((String)option.options().iterator().next());
  }
  
  private static Map<String, List<?>> defaultValues(Map<String, AbstractOptionSpec<?>> recognizedSpecs) {
    Map<String, List<?>> defaults = new HashMap();
    for (Map.Entry<String, AbstractOptionSpec<?>> each : recognizedSpecs.entrySet())
      defaults.put(each.getKey(), ((AbstractOptionSpec)each.getValue()).defaultValues());
    return defaults;
  }
}
