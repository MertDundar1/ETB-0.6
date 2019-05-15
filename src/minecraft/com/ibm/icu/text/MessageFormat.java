package com.ibm.icu.text;

import com.ibm.icu.impl.PatternProps;
import com.ibm.icu.impl.Utility;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.ULocale.Category;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.text.AttributedString;
import java.text.CharacterIterator;
import java.text.ChoiceFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.Format.Field;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;















































































































































































































































































































public class MessageFormat
  extends UFormat
{
  static final long serialVersionUID = 7136212545847378652L;
  private transient ULocale ulocale;
  private transient MessagePattern msgPattern;
  private transient Map<Integer, Format> cachedFormatters;
  private transient Set<Integer> customFormatArgStarts;
  private transient Format stockDateFormatter;
  private transient Format stockNumberFormatter;
  private transient PluralSelectorProvider pluralProvider;
  private transient PluralSelectorProvider ordinalProvider;
  
  public MessageFormat(String pattern)
  {
    ulocale = ULocale.getDefault(ULocale.Category.FORMAT);
    applyPattern(pattern);
  }
  









  public MessageFormat(String pattern, Locale locale)
  {
    this(pattern, ULocale.forLocale(locale));
  }
  









  public MessageFormat(String pattern, ULocale locale)
  {
    ulocale = locale;
    applyPattern(pattern);
  }
  








  public void setLocale(Locale locale)
  {
    setLocale(ULocale.forLocale(locale));
  }
  











  public void setLocale(ULocale locale)
  {
    String existingPattern = toPattern();
    ulocale = locale;
    

    stockNumberFormatter = (this.stockDateFormatter = null);
    pluralProvider = null;
    ordinalProvider = null;
    applyPattern(existingPattern);
  }
  





  public Locale getLocale()
  {
    return ulocale.toLocale();
  }
  





  public ULocale getULocale()
  {
    return ulocale;
  }
  








  public void applyPattern(String pttrn)
  {
    try
    {
      if (msgPattern == null) {
        msgPattern = new MessagePattern(pttrn);
      } else {
        msgPattern.parse(pttrn);
      }
      
      cacheExplicitFormats();
    } catch (RuntimeException e) {
      resetPattern();
      throw e;
    }
  }
  














  public void applyPattern(String pattern, MessagePattern.ApostropheMode aposMode)
  {
    if (msgPattern == null) {
      msgPattern = new MessagePattern(aposMode);
    } else if (aposMode != msgPattern.getApostropheMode()) {
      msgPattern.clearPatternAndSetApostropheMode(aposMode);
    }
    applyPattern(pattern);
  }
  




  public MessagePattern.ApostropheMode getApostropheMode()
  {
    if (msgPattern == null) {
      msgPattern = new MessagePattern();
    }
    return msgPattern.getApostropheMode();
  }
  












  public String toPattern()
  {
    if (customFormatArgStarts != null) {
      throw new IllegalStateException("toPattern() is not supported after custom Format objects have been set via setFormat() or similar APIs");
    }
    

    if (msgPattern == null) {
      return "";
    }
    String originalPattern = msgPattern.getPatternString();
    return originalPattern == null ? "" : originalPattern;
  }
  



  private int nextTopLevelArgStart(int partIndex)
  {
    if (partIndex != 0) {
      partIndex = msgPattern.getLimitPartIndex(partIndex);
    }
    for (;;) {
      MessagePattern.Part.Type type = msgPattern.getPartType(++partIndex);
      if (type == MessagePattern.Part.Type.ARG_START) {
        return partIndex;
      }
      if (type == MessagePattern.Part.Type.MSG_LIMIT) {
        return -1;
      }
    }
  }
  
  private boolean argNameMatches(int partIndex, String argName, int argNumber) {
    MessagePattern.Part part = msgPattern.getPart(partIndex);
    return part.getValue() == argNumber ? true : part.getType() == MessagePattern.Part.Type.ARG_NAME ? msgPattern.partSubstringMatches(part, argName) : false;
  }
  

  private String getArgName(int partIndex)
  {
    MessagePattern.Part part = msgPattern.getPart(partIndex);
    if (part.getType() == MessagePattern.Part.Type.ARG_NAME) {
      return msgPattern.getSubstring(part);
    }
    return Integer.toString(part.getValue());
  }
  



























  public void setFormatsByArgumentIndex(Format[] newFormats)
  {
    if (msgPattern.hasNamedArguments()) {
      throw new IllegalArgumentException("This method is not available in MessageFormat objects that use alphanumeric argument names.");
    }
    

    for (int partIndex = 0; (partIndex = nextTopLevelArgStart(partIndex)) >= 0;) {
      int argNumber = msgPattern.getPart(partIndex + 1).getValue();
      if (argNumber < newFormats.length) {
        setCustomArgStartFormat(partIndex, newFormats[argNumber]);
      }
    }
  }
  




















  public void setFormatsByArgumentName(Map<String, Format> newFormats)
  {
    for (int partIndex = 0; (partIndex = nextTopLevelArgStart(partIndex)) >= 0;) {
      String key = getArgName(partIndex + 1);
      if (newFormats.containsKey(key)) {
        setCustomArgStartFormat(partIndex, (Format)newFormats.get(key));
      }
    }
  }
  






















  public void setFormats(Format[] newFormats)
  {
    int formatNumber = 0;
    int partIndex = 0;
    
    while ((formatNumber < newFormats.length) && ((partIndex = nextTopLevelArgStart(partIndex)) >= 0)) {
      setCustomArgStartFormat(partIndex, newFormats[formatNumber]);
      formatNumber++;
    }
  }
  





















  public void setFormatByArgumentIndex(int argumentIndex, Format newFormat)
  {
    if (msgPattern.hasNamedArguments()) {
      throw new IllegalArgumentException("This method is not available in MessageFormat objects that use alphanumeric argument names.");
    }
    

    for (int partIndex = 0; (partIndex = nextTopLevelArgStart(partIndex)) >= 0;) {
      if (msgPattern.getPart(partIndex + 1).getValue() == argumentIndex) {
        setCustomArgStartFormat(partIndex, newFormat);
      }
    }
  }
  


















  public void setFormatByArgumentName(String argumentName, Format newFormat)
  {
    int argNumber = MessagePattern.validateArgumentName(argumentName);
    if (argNumber < -1) {
      return;
    }
    for (int partIndex = 0; (partIndex = nextTopLevelArgStart(partIndex)) >= 0;) {
      if (argNameMatches(partIndex + 1, argumentName, argNumber)) {
        setCustomArgStartFormat(partIndex, newFormat);
      }
    }
  }
  

















  public void setFormat(int formatElementIndex, Format newFormat)
  {
    int formatNumber = 0;
    for (int partIndex = 0; (partIndex = nextTopLevelArgStart(partIndex)) >= 0;) {
      if (formatNumber == formatElementIndex) {
        setCustomArgStartFormat(partIndex, newFormat);
        return;
      }
      formatNumber++;
    }
    throw new ArrayIndexOutOfBoundsException(formatElementIndex);
  }
  























  public Format[] getFormatsByArgumentIndex()
  {
    if (msgPattern.hasNamedArguments()) {
      throw new IllegalArgumentException("This method is not available in MessageFormat objects that use alphanumeric argument names.");
    }
    

    ArrayList<Format> list = new ArrayList();
    for (int partIndex = 0; (partIndex = nextTopLevelArgStart(partIndex)) >= 0;) {
      int argNumber = msgPattern.getPart(partIndex + 1).getValue();
      while (argNumber >= list.size()) {
        list.add(null);
      }
      list.set(argNumber, cachedFormatters == null ? null : (Format)cachedFormatters.get(Integer.valueOf(partIndex)));
    }
    return (Format[])list.toArray(new Format[list.size()]);
  }
  




















  public Format[] getFormats()
  {
    ArrayList<Format> list = new ArrayList();
    for (int partIndex = 0; (partIndex = nextTopLevelArgStart(partIndex)) >= 0;) {
      list.add(cachedFormatters == null ? null : (Format)cachedFormatters.get(Integer.valueOf(partIndex)));
    }
    return (Format[])list.toArray(new Format[list.size()]);
  }
  





  public Set<String> getArgumentNames()
  {
    Set<String> result = new HashSet();
    for (int partIndex = 0; (partIndex = nextTopLevelArgStart(partIndex)) >= 0;) {
      result.add(getArgName(partIndex + 1));
    }
    return result;
  }
  






  public Format getFormatByArgumentName(String argumentName)
  {
    if (cachedFormatters == null) {
      return null;
    }
    int argNumber = MessagePattern.validateArgumentName(argumentName);
    if (argNumber < -1) {
      return null;
    }
    for (int partIndex = 0; (partIndex = nextTopLevelArgStart(partIndex)) >= 0;) {
      if (argNameMatches(partIndex + 1, argumentName, argNumber)) {
        return (Format)cachedFormatters.get(Integer.valueOf(partIndex));
      }
    }
    return null;
  }
  







































































  public final StringBuffer format(Object[] arguments, StringBuffer result, FieldPosition pos)
  {
    format(arguments, null, new AppendableWrapper(result), pos);
    return result;
  }
  




























  public final StringBuffer format(Map<String, Object> arguments, StringBuffer result, FieldPosition pos)
  {
    format(null, arguments, new AppendableWrapper(result), pos);
    return result;
  }
  















  public static String format(String pattern, Object... arguments)
  {
    MessageFormat temp = new MessageFormat(pattern);
    return temp.format(arguments);
  }
  












  public static String format(String pattern, Map<String, Object> arguments)
  {
    MessageFormat temp = new MessageFormat(pattern);
    return temp.format(arguments);
  }
  






  public boolean usesNamedArguments()
  {
    return msgPattern.hasNamedArguments();
  }
  


























  public final StringBuffer format(Object arguments, StringBuffer result, FieldPosition pos)
  {
    format(arguments, new AppendableWrapper(result), pos);
    return result;
  }
  



































  public AttributedCharacterIterator formatToCharacterIterator(Object arguments)
  {
    if (arguments == null) {
      throw new NullPointerException("formatToCharacterIterator must be passed non-null object");
    }
    
    StringBuilder result = new StringBuilder();
    AppendableWrapper wrapper = new AppendableWrapper(result);
    wrapper.useAttributes();
    format(arguments, wrapper, null);
    AttributedString as = new AttributedString(result.toString());
    for (AttributeAndPosition a : attributes) {
      as.addAttribute(key, value, start, limit);
    }
    return as.getIterator();
  }
  





























  public Object[] parse(String source, ParsePosition pos)
  {
    if (msgPattern.hasNamedArguments()) {
      throw new IllegalArgumentException("This method is not available in MessageFormat objects that use named argument.");
    }
    



    int maxArgId = -1;
    for (int partIndex = 0; (partIndex = nextTopLevelArgStart(partIndex)) >= 0;) {
      int argNumber = msgPattern.getPart(partIndex + 1).getValue();
      if (argNumber > maxArgId) {
        maxArgId = argNumber;
      }
    }
    Object[] resultArray = new Object[maxArgId + 1];
    
    int backupStartPos = pos.getIndex();
    parse(0, source, pos, resultArray, null);
    if (pos.getIndex() == backupStartPos) {
      return null;
    }
    
    return resultArray;
  }
  












  public Map<String, Object> parseToMap(String source, ParsePosition pos)
  {
    Map<String, Object> result = new HashMap();
    int backupStartPos = pos.getIndex();
    parse(0, source, pos, null, result);
    if (pos.getIndex() == backupStartPos) {
      return null;
    }
    return result;
  }
  












  public Object[] parse(String source)
    throws ParseException
  {
    ParsePosition pos = new ParsePosition(0);
    Object[] result = parse(source, pos);
    if (pos.getIndex() == 0) {
      throw new ParseException("MessageFormat parse error!", pos.getErrorIndex());
    }
    
    return result;
  }
  















  private void parse(int msgStart, String source, ParsePosition pos, Object[] args, Map<String, Object> argsMap)
  {
    if (source == null) {
      return;
    }
    String msgString = msgPattern.getPatternString();
    int prevIndex = msgPattern.getPart(msgStart).getLimit();
    int sourceOffset = pos.getIndex();
    ParsePosition tempStatus = new ParsePosition(0);
    
    for (int i = msgStart + 1;; i++) {
      MessagePattern.Part part = msgPattern.getPart(i);
      MessagePattern.Part.Type type = part.getType();
      int index = part.getIndex();
      
      int len = index - prevIndex;
      if ((len == 0) || (msgString.regionMatches(prevIndex, source, sourceOffset, len))) {
        sourceOffset += len;
        prevIndex += len;
      } else {
        pos.setErrorIndex(sourceOffset);
        return;
      }
      if (type == MessagePattern.Part.Type.MSG_LIMIT)
      {
        pos.setIndex(sourceOffset);
        return;
      }
      if ((type == MessagePattern.Part.Type.SKIP_SYNTAX) || (type == MessagePattern.Part.Type.INSERT_CHAR)) {
        prevIndex = part.getLimit();
      }
      else
      {
        assert (type == MessagePattern.Part.Type.ARG_START) : ("Unexpected Part " + part + " in parsed message.");
        int argLimit = msgPattern.getLimitPartIndex(i);
        
        MessagePattern.ArgType argType = part.getArgType();
        part = msgPattern.getPart(++i);
        
        Object argId = null;
        int argNumber = 0;
        String key = null;
        if (args != null) {
          argNumber = part.getValue();
          argId = Integer.valueOf(argNumber);
        } else {
          if (part.getType() == MessagePattern.Part.Type.ARG_NAME) {
            key = msgPattern.getSubstring(part);
          } else {
            key = Integer.toString(part.getValue());
          }
          argId = key;
        }
        
        i++;
        Format formatter = null;
        boolean haveArgResult = false;
        Object argResult = null;
        if ((cachedFormatters != null) && ((formatter = (Format)cachedFormatters.get(Integer.valueOf(i - 2))) != null))
        {
          tempStatus.setIndex(sourceOffset);
          argResult = formatter.parseObject(source, tempStatus);
          if (tempStatus.getIndex() == sourceOffset) {
            pos.setErrorIndex(sourceOffset);
            return;
          }
          haveArgResult = true;
          sourceOffset = tempStatus.getIndex();
        } else if ((argType == MessagePattern.ArgType.NONE) || ((cachedFormatters != null) && (cachedFormatters.containsKey(Integer.valueOf(i - 2)))))
        {





          String stringAfterArgument = getLiteralStringUntilNextArgument(argLimit);
          int next;
          int next; if (stringAfterArgument.length() != 0) {
            next = source.indexOf(stringAfterArgument, sourceOffset);
          } else {
            next = source.length();
          }
          if (next < 0) {
            pos.setErrorIndex(sourceOffset);
            return;
          }
          String strValue = source.substring(sourceOffset, next);
          if (!strValue.equals("{" + argId.toString() + "}")) {
            haveArgResult = true;
            argResult = strValue;
          }
          sourceOffset = next;
        }
        else if (argType == MessagePattern.ArgType.CHOICE) {
          tempStatus.setIndex(sourceOffset);
          double choiceResult = parseChoiceArgument(msgPattern, i, source, tempStatus);
          if (tempStatus.getIndex() == sourceOffset) {
            pos.setErrorIndex(sourceOffset);
            return;
          }
          argResult = Double.valueOf(choiceResult);
          haveArgResult = true;
          sourceOffset = tempStatus.getIndex();
        } else { if ((argType.hasPluralStyle()) || (argType == MessagePattern.ArgType.SELECT))
          {
            throw new UnsupportedOperationException("Parsing of plural/select/selectordinal argument is not supported.");
          }
          

          throw new IllegalStateException("unexpected argType " + argType);
        }
        if (haveArgResult) {
          if (args != null) {
            args[argNumber] = argResult;
          } else if (argsMap != null) {
            argsMap.put(key, argResult);
          }
        }
        prevIndex = msgPattern.getPart(argLimit).getLimit();
        i = argLimit;
      }
    }
  }
  











  public Map<String, Object> parseToMap(String source)
    throws ParseException
  {
    ParsePosition pos = new ParsePosition(0);
    Map<String, Object> result = new HashMap();
    parse(0, source, pos, null, result);
    if (pos.getIndex() == 0) {
      throw new ParseException("MessageFormat parse error!", pos.getErrorIndex());
    }
    
    return result;
  }
  


























  public Object parseObject(String source, ParsePosition pos)
  {
    if (!msgPattern.hasNamedArguments()) {
      return parse(source, pos);
    }
    return parseToMap(source, pos);
  }
  





  public Object clone()
  {
    MessageFormat other = (MessageFormat)super.clone();
    
    if (customFormatArgStarts != null) {
      customFormatArgStarts = new HashSet();
      for (Integer key : customFormatArgStarts) {
        customFormatArgStarts.add(key);
      }
    } else {
      customFormatArgStarts = null;
    }
    
    if (cachedFormatters != null) {
      cachedFormatters = new HashMap();
      Iterator<Map.Entry<Integer, Format>> it = cachedFormatters.entrySet().iterator();
      while (it.hasNext()) {
        Map.Entry<Integer, Format> entry = (Map.Entry)it.next();
        cachedFormatters.put(entry.getKey(), entry.getValue());
      }
    } else {
      cachedFormatters = null;
    }
    
    msgPattern = (msgPattern == null ? null : (MessagePattern)msgPattern.clone());
    stockDateFormatter = (stockDateFormatter == null ? null : (Format)stockDateFormatter.clone());
    stockNumberFormatter = (stockNumberFormatter == null ? null : (Format)stockNumberFormatter.clone());
    
    pluralProvider = null;
    ordinalProvider = null;
    return other;
  }
  




  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if ((obj == null) || (getClass() != obj.getClass()))
      return false;
    MessageFormat other = (MessageFormat)obj;
    return (Utility.objectEquals(ulocale, ulocale)) && (Utility.objectEquals(msgPattern, msgPattern)) && (Utility.objectEquals(cachedFormatters, cachedFormatters)) && (Utility.objectEquals(customFormatArgStarts, customFormatArgStarts));
  }
  









  public int hashCode()
  {
    return msgPattern.getPatternString().hashCode();
  }
  






  public static class Field
    extends Format.Field
  {
    private static final long serialVersionUID = 7510380454602616157L;
    






    protected Field(String name)
    {
      super();
    }
    






    protected Object readResolve()
      throws InvalidObjectException
    {
      if (getClass() != Field.class) {
        throw new InvalidObjectException("A subclass of MessageFormat.Field must implement readResolve.");
      }
      
      if (getName().equals(ARGUMENT.getName())) {
        return ARGUMENT;
      }
      throw new InvalidObjectException("Unknown attribute name.");
    }
    










    public static final Field ARGUMENT = new Field("message argument field");
  }
  























































  private void format(int msgStart, double pluralNumber, Object[] args, Map<String, Object> argsMap, AppendableWrapper dest, FieldPosition fp)
  {
    String msgString = msgPattern.getPatternString();
    int prevIndex = msgPattern.getPart(msgStart).getLimit();
    for (int i = msgStart + 1;; i++) {
      MessagePattern.Part part = msgPattern.getPart(i);
      MessagePattern.Part.Type type = part.getType();
      int index = part.getIndex();
      dest.append(msgString, prevIndex, index);
      if (type == MessagePattern.Part.Type.MSG_LIMIT) {
        return;
      }
      prevIndex = part.getLimit();
      if (type == MessagePattern.Part.Type.REPLACE_NUMBER) {
        if (stockNumberFormatter == null) {
          stockNumberFormatter = NumberFormat.getInstance(ulocale);
        }
        dest.formatAndAppend(stockNumberFormatter, Double.valueOf(pluralNumber));

      }
      else if (type == MessagePattern.Part.Type.ARG_START)
      {

        int argLimit = msgPattern.getLimitPartIndex(i);
        MessagePattern.ArgType argType = part.getArgType();
        part = msgPattern.getPart(++i);
        
        String noArg = null;
        Object argId = null;
        Object arg; if (args != null) {
          int argNumber = part.getValue();
          if (attributes != null)
          {
            argId = Integer.valueOf(argNumber); }
          Object arg;
          if ((0 <= argNumber) && (argNumber < args.length)) {
            arg = args[argNumber];
          } else {
            Object arg = null;
            noArg = "{" + argNumber + "}";
          }
        } else { String key;
          String key;
          if (part.getType() == MessagePattern.Part.Type.ARG_NAME) {
            key = msgPattern.getSubstring(part);
          } else {
            key = Integer.toString(part.getValue());
          }
          argId = key;
          Object arg; if ((argsMap != null) && (argsMap.containsKey(key))) {
            arg = argsMap.get(key);
          } else {
            arg = null;
            noArg = "{" + key + "}";
          }
        }
        i++;
        int prevDestLength = length;
        Format formatter = null;
        if (noArg != null) {
          dest.append(noArg);
        } else if (arg == null) {
          dest.append("null");
        } else if ((cachedFormatters != null) && ((formatter = (Format)cachedFormatters.get(Integer.valueOf(i - 2))) != null))
        {
          if (((formatter instanceof ChoiceFormat)) || ((formatter instanceof PluralFormat)) || ((formatter instanceof SelectFormat)))
          {



            String subMsgString = formatter.format(arg);
            if ((subMsgString.indexOf('{') >= 0) || ((subMsgString.indexOf('\'') >= 0) && (!msgPattern.jdkAposMode())))
            {
              MessageFormat subMsgFormat = new MessageFormat(subMsgString, ulocale);
              subMsgFormat.format(0, 0.0D, args, argsMap, dest, null);
            } else if (attributes == null) {
              dest.append(subMsgString);


            }
            else
            {


              dest.formatAndAppend(formatter, arg);
            }
          } else {
            dest.formatAndAppend(formatter, arg);
          }
        } else if ((argType == MessagePattern.ArgType.NONE) || ((cachedFormatters != null) && (cachedFormatters.containsKey(Integer.valueOf(i - 2)))))
        {



          if ((arg instanceof Number))
          {
            if (stockNumberFormatter == null) {
              stockNumberFormatter = NumberFormat.getInstance(ulocale);
            }
            dest.formatAndAppend(stockNumberFormatter, arg);
          } else if ((arg instanceof Date))
          {
            if (stockDateFormatter == null) {
              stockDateFormatter = DateFormat.getDateTimeInstance(3, 3, ulocale);
            }
            
            dest.formatAndAppend(stockDateFormatter, arg);
          } else {
            dest.append(arg.toString());
          }
        } else if (argType == MessagePattern.ArgType.CHOICE) {
          if (!(arg instanceof Number)) {
            throw new IllegalArgumentException("'" + arg + "' is not a Number");
          }
          double number = ((Number)arg).doubleValue();
          int subMsgStart = findChoiceSubMessage(msgPattern, i, number);
          formatComplexSubMessage(subMsgStart, 0.0D, args, argsMap, dest);
        } else if (argType.hasPluralStyle()) {
          if (!(arg instanceof Number)) {
            throw new IllegalArgumentException("'" + arg + "' is not a Number");
          }
          double number = ((Number)arg).doubleValue();
          PluralFormat.PluralSelector selector;
          PluralFormat.PluralSelector selector; if (argType == MessagePattern.ArgType.PLURAL) {
            if (pluralProvider == null) {
              pluralProvider = new PluralSelectorProvider(ulocale, PluralRules.PluralType.CARDINAL);
            }
            selector = pluralProvider;
          } else {
            if (ordinalProvider == null) {
              ordinalProvider = new PluralSelectorProvider(ulocale, PluralRules.PluralType.ORDINAL);
            }
            selector = ordinalProvider;
          }
          int subMsgStart = PluralFormat.findSubMessage(msgPattern, i, selector, number);
          double offset = msgPattern.getPluralOffset(i);
          formatComplexSubMessage(subMsgStart, number - offset, args, argsMap, dest);
        } else if (argType == MessagePattern.ArgType.SELECT) {
          int subMsgStart = SelectFormat.findSubMessage(msgPattern, i, arg.toString());
          formatComplexSubMessage(subMsgStart, 0.0D, args, argsMap, dest);
        }
        else {
          throw new IllegalStateException("unexpected argType " + argType);
        }
        fp = updateMetaData(dest, prevDestLength, fp, argId);
        prevIndex = msgPattern.getPart(argLimit).getLimit();
        i = argLimit;
      }
    }
  }
  

  private void formatComplexSubMessage(int msgStart, double pluralNumber, Object[] args, Map<String, Object> argsMap, AppendableWrapper dest)
  {
    if (!msgPattern.jdkAposMode()) {
      format(msgStart, pluralNumber, args, argsMap, dest, null);
      return;
    }
    




    String msgString = msgPattern.getPatternString();
    
    StringBuilder sb = null;
    int prevIndex = msgPattern.getPart(msgStart).getLimit();
    int i = msgStart;
    for (;;) { MessagePattern.Part part = msgPattern.getPart(++i);
      MessagePattern.Part.Type type = part.getType();
      int index = part.getIndex();
      if (type == MessagePattern.Part.Type.MSG_LIMIT) {
        if (sb == null) {
          String subMsgString = msgString.substring(prevIndex, index); break;
        }
        String subMsgString = sb.append(msgString, prevIndex, index).toString();
        
        break; }
      if ((type == MessagePattern.Part.Type.REPLACE_NUMBER) || (type == MessagePattern.Part.Type.SKIP_SYNTAX)) {
        if (sb == null) {
          sb = new StringBuilder();
        }
        sb.append(msgString, prevIndex, index);
        if (type == MessagePattern.Part.Type.REPLACE_NUMBER) {
          if (stockNumberFormatter == null) {
            stockNumberFormatter = NumberFormat.getInstance(ulocale);
          }
          sb.append(stockNumberFormatter.format(Double.valueOf(pluralNumber)));
        }
        prevIndex = part.getLimit();
      } else if (type == MessagePattern.Part.Type.ARG_START) {
        if (sb == null) {
          sb = new StringBuilder();
        }
        sb.append(msgString, prevIndex, index);
        prevIndex = index;
        i = msgPattern.getLimitPartIndex(i);
        index = msgPattern.getPart(i).getLimit();
        MessagePattern.appendReducedApostrophes(msgString, prevIndex, index, sb);
        prevIndex = index;
      } }
    String subMsgString;
    if (subMsgString.indexOf('{') >= 0) {
      MessageFormat subMsgFormat = new MessageFormat("", ulocale);
      subMsgFormat.applyPattern(subMsgString, MessagePattern.ApostropheMode.DOUBLE_REQUIRED);
      subMsgFormat.format(0, 0.0D, args, argsMap, dest, null);
    } else {
      dest.append(subMsgString);
    }
  }
  






  private String getLiteralStringUntilNextArgument(int from)
  {
    StringBuilder b = new StringBuilder();
    String msgString = msgPattern.getPatternString();
    int prevIndex = msgPattern.getPart(from).getLimit();
    for (int i = from + 1;; i++) {
      MessagePattern.Part part = msgPattern.getPart(i);
      MessagePattern.Part.Type type = part.getType();
      int index = part.getIndex();
      b.append(msgString, prevIndex, index);
      if ((type == MessagePattern.Part.Type.ARG_START) || (type == MessagePattern.Part.Type.MSG_LIMIT)) {
        return b.toString();
      }
      
      assert ((type == MessagePattern.Part.Type.SKIP_SYNTAX) || (type == MessagePattern.Part.Type.INSERT_CHAR)) : ("Unexpected Part " + part + " in parsed message.");
      prevIndex = part.getLimit();
    }
  }
  
  private FieldPosition updateMetaData(AppendableWrapper dest, int prevLength, FieldPosition fp, Object argId)
  {
    if ((attributes != null) && (prevLength < length)) {
      attributes.add(new AttributeAndPosition(argId, prevLength, length));
    }
    if ((fp != null) && (Field.ARGUMENT.equals(fp.getFieldAttribute()))) {
      fp.setBeginIndex(prevLength);
      fp.setEndIndex(length);
      return null;
    }
    return fp;
  }
  







  private static int findChoiceSubMessage(MessagePattern pattern, int partIndex, double number)
  {
    int count = pattern.countParts();
    



    partIndex += 2;
    int msgStart;
    for (;;) {
      msgStart = partIndex;
      partIndex = pattern.getLimitPartIndex(partIndex);
      partIndex++; if (partIndex >= count) {
        break;
      }
      

      MessagePattern.Part part = pattern.getPart(partIndex++);
      MessagePattern.Part.Type type = part.getType();
      if (type == MessagePattern.Part.Type.ARG_LIMIT) {
        break;
      }
      


      assert (type.hasNumericValue());
      double boundary = pattern.getNumericValue(part);
      
      int selectorIndex = pattern.getPatternIndex(partIndex++);
      char boundaryChar = pattern.getPatternString().charAt(selectorIndex);
      if (boundaryChar == '<' ? number <= boundary : number < boundary) {
        break;
      }
    }
    



    return msgStart;
  }
  



  private static double parseChoiceArgument(MessagePattern pattern, int partIndex, String source, ParsePosition pos)
  {
    int start = pos.getIndex();
    int furthest = start;
    double bestNumber = NaN.0D;
    double tempNumber = 0.0D;
    while (pattern.getPartType(partIndex) != MessagePattern.Part.Type.ARG_LIMIT) {
      tempNumber = pattern.getNumericValue(pattern.getPart(partIndex));
      partIndex += 2;
      int msgLimit = pattern.getLimitPartIndex(partIndex);
      int len = matchStringUntilLimitPart(pattern, partIndex, msgLimit, source, start);
      if (len >= 0) {
        int newIndex = start + len;
        if (newIndex > furthest) {
          furthest = newIndex;
          bestNumber = tempNumber;
          if (furthest == source.length()) {
            break;
          }
        }
      }
      partIndex = msgLimit + 1;
    }
    if (furthest == start) {
      pos.setErrorIndex(start);
    } else {
      pos.setIndex(furthest);
    }
    return bestNumber;
  }
  









  private static int matchStringUntilLimitPart(MessagePattern pattern, int partIndex, int limitPartIndex, String source, int sourceOffset)
  {
    int matchingSourceLength = 0;
    String msgString = pattern.getPatternString();
    int prevIndex = pattern.getPart(partIndex).getLimit();
    for (;;) {
      MessagePattern.Part part = pattern.getPart(++partIndex);
      if ((partIndex == limitPartIndex) || (part.getType() == MessagePattern.Part.Type.SKIP_SYNTAX)) {
        int index = part.getIndex();
        int length = index - prevIndex;
        if ((length != 0) && (!source.regionMatches(sourceOffset, msgString, prevIndex, length))) {
          return -1;
        }
        matchingSourceLength += length;
        if (partIndex == limitPartIndex) {
          return matchingSourceLength;
        }
        prevIndex = part.getLimit();
      }
    }
  }
  
  private static final class PluralSelectorProvider implements PluralFormat.PluralSelector
  {
    private ULocale locale;
    private PluralRules rules;
    private PluralRules.PluralType type;
    
    public PluralSelectorProvider(ULocale loc, PluralRules.PluralType type)
    {
      locale = loc;
      this.type = type;
    }
    
    public String select(double number) { if (rules == null) {
        rules = PluralRules.forLocale(locale, type);
      }
      return rules.select(number);
    }
  }
  



  private void format(Object arguments, AppendableWrapper result, FieldPosition fp)
  {
    if ((arguments == null) || ((arguments instanceof Map))) {
      format(null, (Map)arguments, result, fp);
    } else {
      format((Object[])arguments, null, result, fp);
    }
  }
  







  private void format(Object[] arguments, Map<String, Object> argsMap, AppendableWrapper dest, FieldPosition fp)
  {
    if ((arguments != null) && (msgPattern.hasNamedArguments())) {
      throw new IllegalArgumentException("This method is not available in MessageFormat objects that use alphanumeric argument names.");
    }
    

    format(0, 0.0D, arguments, argsMap, dest, fp);
  }
  
  private void resetPattern() {
    if (msgPattern != null) {
      msgPattern.clear();
    }
    if (cachedFormatters != null) {
      cachedFormatters.clear();
    }
    customFormatArgStarts = null;
  }
  
  private static final String[] typeList = { "number", "date", "time", "spellout", "ordinal", "duration" };
  
  private static final int TYPE_NUMBER = 0;
  
  private static final int TYPE_DATE = 1;
  
  private static final int TYPE_TIME = 2;
  private static final int TYPE_SPELLOUT = 3;
  private static final int TYPE_ORDINAL = 4;
  private static final int TYPE_DURATION = 5;
  private static final String[] modifierList = { "", "currency", "percent", "integer" };
  
  private static final int MODIFIER_EMPTY = 0;
  
  private static final int MODIFIER_CURRENCY = 1;
  
  private static final int MODIFIER_PERCENT = 2;
  
  private static final int MODIFIER_INTEGER = 3;
  private static final String[] dateModifierList = { "", "short", "medium", "long", "full" };
  
  private static final int DATE_MODIFIER_EMPTY = 0;
  
  private static final int DATE_MODIFIER_SHORT = 1;
  
  private static final int DATE_MODIFIER_MEDIUM = 2;
  
  private static final int DATE_MODIFIER_LONG = 3;
  private static final int DATE_MODIFIER_FULL = 4;
  
  private Format createAppropriateFormat(String type, String style)
  {
    Format newFormat = null;
    int subformatType = findKeyword(type, typeList);
    switch (subformatType) {
    case 0: 
      switch (findKeyword(style, modifierList)) {
      case 0: 
        newFormat = NumberFormat.getInstance(ulocale);
        break;
      case 1: 
        newFormat = NumberFormat.getCurrencyInstance(ulocale);
        break;
      case 2: 
        newFormat = NumberFormat.getPercentInstance(ulocale);
        break;
      case 3: 
        newFormat = NumberFormat.getIntegerInstance(ulocale);
        break;
      default: 
        newFormat = new DecimalFormat(style, new DecimalFormatSymbols(ulocale));
      }
      break;
    

    case 1: 
      switch (findKeyword(style, dateModifierList)) {
      case 0: 
        newFormat = DateFormat.getDateInstance(2, ulocale);
        break;
      case 1: 
        newFormat = DateFormat.getDateInstance(3, ulocale);
        break;
      case 2: 
        newFormat = DateFormat.getDateInstance(2, ulocale);
        break;
      case 3: 
        newFormat = DateFormat.getDateInstance(1, ulocale);
        break;
      case 4: 
        newFormat = DateFormat.getDateInstance(0, ulocale);
        break;
      default: 
        newFormat = new SimpleDateFormat(style, ulocale); }
      break;
    

    case 2: 
      switch (findKeyword(style, dateModifierList)) {
      case 0: 
        newFormat = DateFormat.getTimeInstance(2, ulocale);
        break;
      case 1: 
        newFormat = DateFormat.getTimeInstance(3, ulocale);
        break;
      case 2: 
        newFormat = DateFormat.getTimeInstance(2, ulocale);
        break;
      case 3: 
        newFormat = DateFormat.getTimeInstance(1, ulocale);
        break;
      case 4: 
        newFormat = DateFormat.getTimeInstance(0, ulocale);
        break;
      default: 
        newFormat = new SimpleDateFormat(style, ulocale); }
      break;
    


    case 3: 
      RuleBasedNumberFormat rbnf = new RuleBasedNumberFormat(ulocale, 1);
      
      String ruleset = style.trim();
      if (ruleset.length() != 0) {
        try {
          rbnf.setDefaultRuleSet(ruleset);
        }
        catch (Exception e) {}
      }
      

      newFormat = rbnf;
      
      break;
    
    case 4: 
      RuleBasedNumberFormat rbnf = new RuleBasedNumberFormat(ulocale, 2);
      
      String ruleset = style.trim();
      if (ruleset.length() != 0) {
        try {
          rbnf.setDefaultRuleSet(ruleset);
        }
        catch (Exception e) {}
      }
      

      newFormat = rbnf;
      
      break;
    
    case 5: 
      RuleBasedNumberFormat rbnf = new RuleBasedNumberFormat(ulocale, 3);
      
      String ruleset = style.trim();
      if (ruleset.length() != 0) {
        try {
          rbnf.setDefaultRuleSet(ruleset);
        }
        catch (Exception e) {}
      }
      

      newFormat = rbnf;
      
      break;
    default: 
      throw new IllegalArgumentException("Unknown format type \"" + type + "\"");
    }
    return newFormat;
  }
  
  private static final Locale rootLocale = new Locale("");
  private static final char SINGLE_QUOTE = '\'';
  
  private static final int findKeyword(String s, String[] list) { s = PatternProps.trimWhiteSpace(s).toLowerCase(rootLocale);
    for (int i = 0; i < list.length; i++) {
      if (s.equals(list[i]))
        return i;
    }
    return -1;
  }
  












  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.defaultWriteObject();
    

    out.writeObject(ulocale.toLanguageTag());
    
    if (msgPattern == null) {
      msgPattern = new MessagePattern();
    }
    out.writeObject(msgPattern.getApostropheMode());
    
    out.writeObject(msgPattern.getPatternString());
    int formatIndex;
    int partIndex; if ((customFormatArgStarts == null) || (customFormatArgStarts.isEmpty())) {
      out.writeInt(0);
    } else {
      out.writeInt(customFormatArgStarts.size());
      formatIndex = 0;
      for (partIndex = 0; (partIndex = nextTopLevelArgStart(partIndex)) >= 0;) {
        if (customFormatArgStarts.contains(Integer.valueOf(partIndex))) {
          out.writeInt(formatIndex);
          out.writeObject(cachedFormatters.get(Integer.valueOf(partIndex)));
        }
        formatIndex++;
      }
    }
    
    out.writeInt(0);
  }
  


  private void readObject(ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    in.defaultReadObject();
    
    String languageTag = (String)in.readObject();
    ulocale = ULocale.forLanguageTag(languageTag);
    MessagePattern.ApostropheMode aposMode = (MessagePattern.ApostropheMode)in.readObject();
    if ((msgPattern == null) || (aposMode != msgPattern.getApostropheMode())) {
      msgPattern = new MessagePattern(aposMode);
    }
    String msg = (String)in.readObject();
    if (msg != null) {
      applyPattern(msg);
    }
    
    for (int numFormatters = in.readInt(); numFormatters > 0; numFormatters--) {
      int formatIndex = in.readInt();
      Format formatter = (Format)in.readObject();
      setFormat(formatIndex, formatter);
    }
    
    for (int numPairs = in.readInt(); numPairs > 0; numPairs--) {
      in.readInt();
      in.readObject();
    }
  }
  
  private void cacheExplicitFormats() {
    if (cachedFormatters != null) {
      cachedFormatters.clear();
    }
    customFormatArgStarts = null;
    

    int limit = msgPattern.countParts() - 2;
    

    for (int i = 1; i < limit; i++) {
      MessagePattern.Part part = msgPattern.getPart(i);
      if (part.getType() == MessagePattern.Part.Type.ARG_START)
      {

        MessagePattern.ArgType argType = part.getArgType();
        if (argType == MessagePattern.ArgType.SIMPLE)
        {

          int index = i;
          i += 2;
          String explicitType = msgPattern.getSubstring(msgPattern.getPart(i++));
          String style = "";
          if ((part = msgPattern.getPart(i)).getType() == MessagePattern.Part.Type.ARG_STYLE) {
            style = msgPattern.getSubstring(part);
            i++;
          }
          Format formatter = createAppropriateFormat(explicitType, style);
          setArgStartFormat(index, formatter);
        }
      }
    }
  }
  
  private void setArgStartFormat(int argStart, Format formatter)
  {
    if (cachedFormatters == null) {
      cachedFormatters = new HashMap();
    }
    cachedFormatters.put(Integer.valueOf(argStart), formatter);
  }
  



  private void setCustomArgStartFormat(int argStart, Format formatter)
  {
    setArgStartFormat(argStart, formatter);
    if (customFormatArgStarts == null) {
      customFormatArgStarts = new HashSet();
    }
    customFormatArgStarts.add(Integer.valueOf(argStart));
  }
  





  private static final char CURLY_BRACE_LEFT = '{';
  




  private static final char CURLY_BRACE_RIGHT = '}';
  



  private static final int STATE_INITIAL = 0;
  



  private static final int STATE_SINGLE_QUOTE = 1;
  



  private static final int STATE_IN_QUOTE = 2;
  



  private static final int STATE_MSG_ELEMENT = 3;
  



  public static String autoQuoteApostrophe(String pattern)
  {
    StringBuilder buf = new StringBuilder(pattern.length() * 2);
    int state = 0;
    int braceCount = 0;
    int i = 0; for (int j = pattern.length(); i < j; i++) {
      char c = pattern.charAt(i);
      switch (state) {
      case 0: 
        switch (c) {
        case '\'': 
          state = 1;
          break;
        case '{': 
          state = 3;
          braceCount++;
        }
        
        break;
      case 1: 
        switch (c) {
        case '\'': 
          state = 0;
          break;
        case '{': 
        case '}': 
          state = 2;
          break;
        default: 
          buf.append('\'');
          state = 0; }
        break;
      

      case 2: 
        switch (c) {
        case '\'': 
          state = 0;
        }
        
        break;
      case 3: 
        switch (c) {
        case '{': 
          braceCount++;
          break;
        case '}': 
          braceCount--; if (braceCount == 0) {
            state = 0;
          }
          break;
        }
        break;
      }
      
      


      buf.append(c);
    }
    
    if ((state == 1) || (state == 2)) {
      buf.append('\'');
    }
    return new String(buf);
  }
  
  private static final class AppendableWrapper
  {
    private Appendable app;
    private int length;
    private List<MessageFormat.AttributeAndPosition> attributes;
    
    public AppendableWrapper(StringBuilder sb) {
      app = sb;
      length = sb.length();
      attributes = null;
    }
    
    public AppendableWrapper(StringBuffer sb) {
      app = sb;
      length = sb.length();
      attributes = null;
    }
    
    public void useAttributes() {
      attributes = new ArrayList();
    }
    
    public void append(CharSequence s) {
      try {
        app.append(s);
        length += s.length();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    
    public void append(CharSequence s, int start, int limit) {
      try {
        app.append(s, start, limit);
        length += limit - start;
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    
    public void append(CharacterIterator iterator) {
      length += append(app, iterator);
    }
    
    public static int append(Appendable result, CharacterIterator iterator) {
      try {
        int start = iterator.getBeginIndex();
        int limit = iterator.getEndIndex();
        int length = limit - start;
        if (start < limit) {
          result.append(iterator.first());
          for (;;) { start++; if (start >= limit) break;
            result.append(iterator.next());
          }
        }
        return length;
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    
    public void formatAndAppend(Format formatter, Object arg) {
      if (attributes == null) {
        append(formatter.format(arg));
      } else {
        AttributedCharacterIterator formattedArg = formatter.formatToCharacterIterator(arg);
        int prevLength = length;
        append(formattedArg);
        
        formattedArg.first();
        int start = formattedArg.getIndex();
        int limit = formattedArg.getEndIndex();
        int offset = prevLength - start;
        while (start < limit) {
          Map<AttributedCharacterIterator.Attribute, Object> map = formattedArg.getAttributes();
          int runLimit = formattedArg.getRunLimit();
          if (map.size() != 0) {
            for (Map.Entry<AttributedCharacterIterator.Attribute, Object> entry : map.entrySet()) {
              attributes.add(new MessageFormat.AttributeAndPosition((AttributedCharacterIterator.Attribute)entry.getKey(), entry.getValue(), offset + start, offset + runLimit));
            }
          }
          


          start = runLimit;
          formattedArg.setIndex(start);
        }
      }
    }
  }
  
  private static final class AttributeAndPosition
  {
    private AttributedCharacterIterator.Attribute key;
    private Object value;
    private int start;
    private int limit;
    
    public AttributeAndPosition(Object fieldValue, int startIndex, int limitIndex)
    {
      init(MessageFormat.Field.ARGUMENT, fieldValue, startIndex, limitIndex);
    }
    
    public AttributeAndPosition(AttributedCharacterIterator.Attribute field, Object fieldValue, int startIndex, int limitIndex) {
      init(field, fieldValue, startIndex, limitIndex);
    }
    
    public void init(AttributedCharacterIterator.Attribute field, Object fieldValue, int startIndex, int limitIndex) {
      key = field;
      value = fieldValue;
      start = startIndex;
      limit = limitIndex;
    }
  }
}
