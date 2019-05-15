package joptsimple;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import joptsimple.internal.AbbreviationMap;
import joptsimple.util.KeyValuePair;














































































































































































public class OptionParser
  implements OptionDeclarer
{
  private final AbbreviationMap<AbstractOptionSpec<?>> recognizedOptions;
  private final Map<Collection<String>, Set<OptionSpec<?>>> requiredIf;
  private final Map<Collection<String>, Set<OptionSpec<?>>> requiredUnless;
  private OptionParserState state;
  private boolean posixlyCorrect;
  private boolean allowsUnrecognizedOptions;
  private HelpFormatter helpFormatter = new BuiltinHelpFormatter();
  



  public OptionParser()
  {
    recognizedOptions = new AbbreviationMap();
    requiredIf = new HashMap();
    requiredUnless = new HashMap();
    state = OptionParserState.moreOptions(false);
    
    recognize(new NonOptionArgumentSpec());
  }
  









  public OptionParser(String optionSpecification)
  {
    this();
    
    new OptionSpecTokenizer(optionSpecification).configure(this);
  }
  
  public OptionSpecBuilder accepts(String option) {
    return acceptsAll(Collections.singletonList(option));
  }
  
  public OptionSpecBuilder accepts(String option, String description) {
    return acceptsAll(Collections.singletonList(option), description);
  }
  
  public OptionSpecBuilder acceptsAll(Collection<String> options) {
    return acceptsAll(options, "");
  }
  
  public OptionSpecBuilder acceptsAll(Collection<String> options, String description) {
    if (options.isEmpty()) {
      throw new IllegalArgumentException("need at least one option");
    }
    ParserRules.ensureLegalOptions(options);
    
    return new OptionSpecBuilder(this, options, description);
  }
  
  public NonOptionArgumentSpec<String> nonOptions() {
    NonOptionArgumentSpec<String> spec = new NonOptionArgumentSpec();
    
    recognize(spec);
    
    return spec;
  }
  
  public NonOptionArgumentSpec<String> nonOptions(String description) {
    NonOptionArgumentSpec<String> spec = new NonOptionArgumentSpec(description);
    
    recognize(spec);
    
    return spec;
  }
  
  public void posixlyCorrect(boolean setting) {
    posixlyCorrect = setting;
    state = OptionParserState.moreOptions(setting);
  }
  
  boolean posixlyCorrect() {
    return posixlyCorrect;
  }
  
  public void allowsUnrecognizedOptions() {
    allowsUnrecognizedOptions = true;
  }
  
  boolean doesAllowsUnrecognizedOptions() {
    return allowsUnrecognizedOptions;
  }
  
  public void recognizeAlternativeLongOptions(boolean recognize) {
    if (recognize) {
      recognize(new AlternativeLongOptionSpec());
    } else
      recognizedOptions.remove(String.valueOf("W"));
  }
  
  void recognize(AbstractOptionSpec<?> spec) {
    recognizedOptions.putAll(spec.options(), spec);
  }
  








  public void printHelpOn(OutputStream sink)
    throws IOException
  {
    printHelpOn(new OutputStreamWriter(sink));
  }
  








  public void printHelpOn(Writer sink)
    throws IOException
  {
    sink.write(helpFormatter.format(recognizedOptions.toJavaUtilMap()));
    sink.flush();
  }
  





  public void formatHelpWith(HelpFormatter formatter)
  {
    if (formatter == null) {
      throw new NullPointerException();
    }
    helpFormatter = formatter;
  }
  




  public Map<String, OptionSpec<?>> recognizedOptions()
  {
    return new HashMap(recognizedOptions.toJavaUtilMap());
  }
  







  public OptionSet parse(String... arguments)
  {
    ArgumentList argumentList = new ArgumentList(arguments);
    OptionSet detected = new OptionSet(recognizedOptions.toJavaUtilMap());
    detected.add((AbstractOptionSpec)recognizedOptions.get("[arguments]"));
    
    while (argumentList.hasMore()) {
      state.handleArgument(this, argumentList, detected);
    }
    reset();
    
    ensureRequiredOptions(detected);
    
    return detected;
  }
  
  private void ensureRequiredOptions(OptionSet options) {
    Collection<String> missingRequiredOptions = missingRequiredOptions(options);
    boolean helpOptionPresent = isHelpOptionPresent(options);
    
    if ((!missingRequiredOptions.isEmpty()) && (!helpOptionPresent))
      throw new MissingRequiredOptionException(missingRequiredOptions);
  }
  
  private Collection<String> missingRequiredOptions(OptionSet options) {
    Collection<String> missingRequiredOptions = new HashSet();
    
    for (AbstractOptionSpec<?> each : recognizedOptions.toJavaUtilMap().values()) {
      if ((each.isRequired()) && (!options.has(each))) {
        missingRequiredOptions.addAll(each.options());
      }
    }
    for (Map.Entry<Collection<String>, Set<OptionSpec<?>>> eachEntry : requiredIf.entrySet()) {
      AbstractOptionSpec<?> required = specFor((String)((Collection)eachEntry.getKey()).iterator().next());
      
      if ((optionsHasAnyOf(options, (Collection)eachEntry.getValue())) && (!options.has(required))) {
        missingRequiredOptions.addAll(required.options());
      }
    }
    
    for (Map.Entry<Collection<String>, Set<OptionSpec<?>>> eachEntry : requiredUnless.entrySet()) {
      AbstractOptionSpec<?> required = specFor((String)((Collection)eachEntry.getKey()).iterator().next());
      
      if ((!optionsHasAnyOf(options, (Collection)eachEntry.getValue())) && (!options.has(required))) {
        missingRequiredOptions.addAll(required.options());
      }
    }
    
    return missingRequiredOptions;
  }
  
  private boolean optionsHasAnyOf(OptionSet options, Collection<OptionSpec<?>> specs) {
    for (OptionSpec<?> each : specs) {
      if (options.has(each)) {
        return true;
      }
    }
    return false;
  }
  
  private boolean isHelpOptionPresent(OptionSet options) {
    boolean helpOptionPresent = false;
    for (AbstractOptionSpec<?> each : recognizedOptions.toJavaUtilMap().values()) {
      if ((each.isForHelp()) && (options.has(each))) {
        helpOptionPresent = true;
        break;
      }
    }
    return helpOptionPresent;
  }
  
  void handleLongOptionToken(String candidate, ArgumentList arguments, OptionSet detected) {
    KeyValuePair optionAndArgument = parseLongOptionWithArgument(candidate);
    
    if (!isRecognized(key)) {
      throw OptionException.unrecognizedOption(key);
    }
    AbstractOptionSpec<?> optionSpec = specFor(key);
    optionSpec.handleOption(this, arguments, detected, value);
  }
  
  void handleShortOptionToken(String candidate, ArgumentList arguments, OptionSet detected) {
    KeyValuePair optionAndArgument = parseShortOptionWithArgument(candidate);
    
    if (isRecognized(key)) {
      specFor(key).handleOption(this, arguments, detected, value);
    }
    else
      handleShortOptionCluster(candidate, arguments, detected);
  }
  
  private void handleShortOptionCluster(String candidate, ArgumentList arguments, OptionSet detected) {
    char[] options = extractShortOptionsFrom(candidate);
    validateOptionCharacters(options);
    
    for (int i = 0; i < options.length; i++) {
      AbstractOptionSpec<?> optionSpec = specFor(options[i]);
      
      if ((optionSpec.acceptsArguments()) && (options.length > i + 1)) {
        String detectedArgument = String.valueOf(options, i + 1, options.length - 1 - i);
        optionSpec.handleOption(this, arguments, detected, detectedArgument);
        break;
      }
      
      optionSpec.handleOption(this, arguments, detected, null);
    }
  }
  
  void handleNonOptionArgument(String candidate, ArgumentList arguments, OptionSet detectedOptions) {
    specFor("[arguments]").handleOption(this, arguments, detectedOptions, candidate);
  }
  
  void noMoreOptions() {
    state = OptionParserState.noMoreOptions();
  }
  
  boolean looksLikeAnOption(String argument) {
    return (ParserRules.isShortOptionToken(argument)) || (ParserRules.isLongOptionToken(argument));
  }
  
  boolean isRecognized(String option) {
    return recognizedOptions.contains(option);
  }
  
  void requiredIf(Collection<String> precedentSynonyms, String required) {
    requiredIf(precedentSynonyms, specFor(required));
  }
  
  void requiredIf(Collection<String> precedentSynonyms, OptionSpec<?> required) {
    putRequiredOption(precedentSynonyms, required, requiredIf);
  }
  
  void requiredUnless(Collection<String> precedentSynonyms, String required) {
    requiredUnless(precedentSynonyms, specFor(required));
  }
  
  void requiredUnless(Collection<String> precedentSynonyms, OptionSpec<?> required) {
    putRequiredOption(precedentSynonyms, required, requiredUnless);
  }
  

  private void putRequiredOption(Collection<String> precedentSynonyms, OptionSpec<?> required, Map<Collection<String>, Set<OptionSpec<?>>> target)
  {
    for (String each : precedentSynonyms) {
      AbstractOptionSpec<?> spec = specFor(each);
      if (spec == null) {
        throw new UnconfiguredOptionException(precedentSynonyms);
      }
    }
    Set<OptionSpec<?>> associated = (Set)target.get(precedentSynonyms);
    if (associated == null) {
      associated = new HashSet();
      target.put(precedentSynonyms, associated);
    }
    
    associated.add(required);
  }
  
  private AbstractOptionSpec<?> specFor(char option) {
    return specFor(String.valueOf(option));
  }
  
  private AbstractOptionSpec<?> specFor(String option) {
    return (AbstractOptionSpec)recognizedOptions.get(option);
  }
  
  private void reset() {
    state = OptionParserState.moreOptions(posixlyCorrect);
  }
  
  private static char[] extractShortOptionsFrom(String argument) {
    char[] options = new char[argument.length() - 1];
    argument.getChars(1, argument.length(), options, 0);
    
    return options;
  }
  
  private void validateOptionCharacters(char[] options) {
    for (char each : options) {
      String option = String.valueOf(each);
      
      if (!isRecognized(option)) {
        throw OptionException.unrecognizedOption(option);
      }
      if (specFor(option).acceptsArguments())
        return;
    }
  }
  
  private static KeyValuePair parseLongOptionWithArgument(String argument) {
    return KeyValuePair.valueOf(argument.substring(2));
  }
  
  private static KeyValuePair parseShortOptionWithArgument(String argument) {
    return KeyValuePair.valueOf(argument.substring(1));
  }
}
