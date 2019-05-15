package joptsimple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


















































public class OptionSpecBuilder
  extends NoArgumentOptionSpec
{
  private final OptionParser parser;
  
  OptionSpecBuilder(OptionParser parser, Collection<String> options, String description)
  {
    super(options, description);
    
    this.parser = parser;
    attachToParser();
  }
  
  private void attachToParser() {
    parser.recognize(this);
  }
  




  public ArgumentAcceptingOptionSpec<String> withRequiredArg()
  {
    ArgumentAcceptingOptionSpec<String> newSpec = new RequiredArgumentOptionSpec(options(), description());
    
    parser.recognize(newSpec);
    
    return newSpec;
  }
  




  public ArgumentAcceptingOptionSpec<String> withOptionalArg()
  {
    ArgumentAcceptingOptionSpec<String> newSpec = new OptionalArgumentOptionSpec(options(), description());
    
    parser.recognize(newSpec);
    
    return newSpec;
  }
  











  public OptionSpecBuilder requiredIf(String dependent, String... otherDependents)
  {
    List<String> dependents = validatedDependents(dependent, otherDependents);
    for (String each : dependents) {
      parser.requiredIf(options(), each);
    }
    
    return this;
  }
  












  public OptionSpecBuilder requiredIf(OptionSpec<?> dependent, OptionSpec<?>... otherDependents)
  {
    parser.requiredIf(options(), dependent);
    for (OptionSpec<?> each : otherDependents) {
      parser.requiredIf(options(), each);
    }
    return this;
  }
  











  public OptionSpecBuilder requiredUnless(String dependent, String... otherDependents)
  {
    List<String> dependents = validatedDependents(dependent, otherDependents);
    for (String each : dependents) {
      parser.requiredUnless(options(), each);
    }
    return this;
  }
  












  public OptionSpecBuilder requiredUnless(OptionSpec<?> dependent, OptionSpec<?>... otherDependents)
  {
    parser.requiredUnless(options(), dependent);
    for (OptionSpec<?> each : otherDependents) {
      parser.requiredUnless(options(), each);
    }
    return this;
  }
  
  private List<String> validatedDependents(String dependent, String... otherDependents) {
    List<String> dependents = new ArrayList();
    dependents.add(dependent);
    Collections.addAll(dependents, otherDependents);
    
    for (String each : dependents) {
      if (!parser.isRecognized(each)) {
        throw new UnconfiguredOptionException(each);
      }
    }
    return dependents;
  }
}
