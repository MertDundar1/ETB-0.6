package com.ibm.icu.text;

import com.ibm.icu.impl.ICUConfig;
import com.ibm.icu.impl.PatternProps;
import com.ibm.icu.util.Freezable;
import java.util.ArrayList;
import java.util.Locale;










































































































public final class MessagePattern
  implements Cloneable, Freezable<MessagePattern>
{
  public static final int ARG_NAME_NOT_NUMBER = -1;
  public static final int ARG_NAME_NOT_VALID = -2;
  public static final double NO_NUMERIC_VALUE = -1.23456789E8D;
  private static final int MAX_PREFIX_LENGTH = 24;
  private ApostropheMode aposMode;
  private String msg;
  
  public static enum ApostropheMode
  {
    DOUBLE_OPTIONAL, 
    







    DOUBLE_REQUIRED;
    

    private ApostropheMode() {}
  }
  
  public MessagePattern()
  {
    aposMode = defaultAposMode;
  }
  




  public MessagePattern(ApostropheMode mode)
  {
    aposMode = mode;
  }
  









  public MessagePattern(String pattern)
  {
    aposMode = defaultAposMode;
    parse(pattern);
  }
  









  public MessagePattern parse(String pattern)
  {
    preParse(pattern);
    parseMessage(0, 0, 0, ArgType.NONE);
    postParse();
    return this;
  }
  









  public MessagePattern parseChoiceStyle(String pattern)
  {
    preParse(pattern);
    parseChoiceStyle(0, 0);
    postParse();
    return this;
  }
  









  public MessagePattern parsePluralStyle(String pattern)
  {
    preParse(pattern);
    parsePluralOrSelectStyle(ArgType.PLURAL, 0, 0);
    postParse();
    return this;
  }
  









  public MessagePattern parseSelectStyle(String pattern)
  {
    preParse(pattern);
    parsePluralOrSelectStyle(ArgType.SELECT, 0, 0);
    postParse();
    return this;
  }
  





  public void clear()
  {
    if (isFrozen()) {
      throw new UnsupportedOperationException("Attempt to clear() a frozen MessagePattern instance.");
    }
    
    msg = null;
    hasArgNames = (this.hasArgNumbers = 0);
    needsAutoQuoting = false;
    parts.clear();
    if (numericValues != null) {
      numericValues.clear();
    }
  }
  





  public void clearPatternAndSetApostropheMode(ApostropheMode mode)
  {
    clear();
    aposMode = mode;
  }
  





  public boolean equals(Object other)
  {
    if (this == other) {
      return true;
    }
    if ((other == null) || (getClass() != other.getClass())) {
      return false;
    }
    MessagePattern o = (MessagePattern)other;
    return (aposMode.equals(aposMode)) && (msg == null ? msg == null : msg.equals(msg)) && (parts.equals(parts));
  }
  








  public int hashCode()
  {
    return (aposMode.hashCode() * 37 + (msg != null ? msg.hashCode() : 0)) * 37 + parts.hashCode();
  }
  



  public ApostropheMode getApostropheMode()
  {
    return aposMode;
  }
  



  boolean jdkAposMode()
  {
    return aposMode == ApostropheMode.DOUBLE_REQUIRED;
  }
  



  public String getPatternString()
  {
    return msg;
  }
  




  public boolean hasNamedArguments()
  {
    return hasArgNames;
  }
  




  public boolean hasNumberedArguments()
  {
    return hasArgNumbers;
  }
  




  public String toString()
  {
    return msg;
  }
  










  public static int validateArgumentName(String name)
  {
    if (!PatternProps.isIdentifier(name)) {
      return -2;
    }
    return parseArgNumber(name, 0, name.length());
  }
  

























  public String autoQuoteApostropheDeep()
  {
    if (!needsAutoQuoting) {
      return msg;
    }
    StringBuilder modified = null;
    
    int count = countParts();
    for (int i = count; i > 0;) {
      Part part;
      if ((part = getPart(--i)).getType() == MessagePattern.Part.Type.INSERT_CHAR) {
        if (modified == null) {
          modified = new StringBuilder(msg.length() + 10).append(msg);
        }
        modified.insert(index, (char)value);
      }
    }
    if (modified == null) {
      return msg;
    }
    return modified.toString();
  }
  






  public int countParts()
  {
    return parts.size();
  }
  






  public Part getPart(int i)
  {
    return (Part)parts.get(i);
  }
  







  public MessagePattern.Part.Type getPartType(int i)
  {
    return parts.get(i)).type;
  }
  







  public int getPatternIndex(int partIndex)
  {
    return parts.get(partIndex)).index;
  }
  






  public String getSubstring(Part part)
  {
    int index = index;
    return msg.substring(index, index + length);
  }
  






  public boolean partSubstringMatches(Part part, String s)
  {
    return msg.regionMatches(index, s, 0, length);
  }
  





  public double getNumericValue(Part part)
  {
    MessagePattern.Part.Type type = type;
    if (type == MessagePattern.Part.Type.ARG_INT)
      return value;
    if (type == MessagePattern.Part.Type.ARG_DOUBLE) {
      return ((Double)numericValues.get(value)).doubleValue();
    }
    return -1.23456789E8D;
  }
  















  public double getPluralOffset(int pluralStart)
  {
    Part part = (Part)parts.get(pluralStart);
    if (type.hasNumericValue()) {
      return getNumericValue(part);
    }
    return 0.0D;
  }
  









  public int getLimitPartIndex(int start)
  {
    int limit = parts.get(start)).limitPartIndex;
    if (limit < start) {
      return start;
    }
    return limit;
  }
  
  public static final class Part {
    private static final int MAX_LENGTH = 65535;
    private static final int MAX_VALUE = 32767;
    private final Type type;
    private final int index;
    private final char length;
    private short value;
    private int limitPartIndex;
    
    private Part(Type t, int i, int l, int v) { type = t;
      index = i;
      length = ((char)l);
      value = ((short)v);
    }
    




    public Type getType()
    {
      return type;
    }
    




    public int getIndex()
    {
      return index;
    }
    





    public int getLength()
    {
      return length;
    }
    





    public int getLimit()
    {
      return index + length;
    }
    





    public int getValue()
    {
      return value;
    }
    





    public MessagePattern.ArgType getArgType()
    {
      Type type = getType();
      if ((type == Type.ARG_START) || (type == Type.ARG_LIMIT)) {
        return MessagePattern.argTypes[value];
      }
      return MessagePattern.ArgType.NONE;
    }
    













    public static enum Type
    {
      MSG_START, 
      







      MSG_LIMIT, 
      






      SKIP_SYNTAX, 
      





      INSERT_CHAR, 
      






      REPLACE_NUMBER, 
      









      ARG_START, 
      





      ARG_LIMIT, 
      



      ARG_NUMBER, 
      




      ARG_NAME, 
      




      ARG_TYPE, 
      




      ARG_STYLE, 
      




      ARG_SELECTOR, 
      





      ARG_INT, 
      






      ARG_DOUBLE;
      


      private Type() {}
      

      public boolean hasNumericValue()
      {
        return (this == ARG_INT) || (this == ARG_DOUBLE);
      }
    }
    




    public String toString()
    {
      String valueString = (type == Type.ARG_START) || (type == Type.ARG_LIMIT) ? getArgType().name() : Integer.toString(value);
      
      return type.name() + "(" + valueString + ")@" + index;
    }
    





    public boolean equals(Object other)
    {
      if (this == other) {
        return true;
      }
      if ((other == null) || (getClass() != other.getClass())) {
        return false;
      }
      Part o = (Part)other;
      return (type.equals(type)) && (index == index) && (length == length) && (value == value) && (limitPartIndex == limitPartIndex);
    }
    









    public int hashCode()
    {
      return ((type.hashCode() * 37 + index) * 37 + length) * 37 + value;
    }
  }
  






















  public static enum ArgType
  {
    NONE, 
    




    SIMPLE, 
    




    CHOICE, 
    








    PLURAL, 
    



    SELECT, 
    





    SELECTORDINAL;
    


    private ArgType() {}
    

    public boolean hasPluralStyle()
    {
      return (this == PLURAL) || (this == SELECTORDINAL);
    }
  }
  





  public Object clone()
  {
    if (isFrozen()) {
      return this;
    }
    return cloneAsThawed();
  }
  



  public MessagePattern cloneAsThawed()
  {
    MessagePattern newMsg;
    

    try
    {
      newMsg = (MessagePattern)super.clone();
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
    parts = ((ArrayList)parts.clone());
    if (numericValues != null) {
      numericValues = ((ArrayList)numericValues.clone());
    }
    frozen = false;
    return newMsg;
  }
  




  public MessagePattern freeze()
  {
    frozen = true;
    return this;
  }
  




  public boolean isFrozen()
  {
    return frozen;
  }
  
  private void preParse(String pattern) {
    if (isFrozen()) {
      throw new UnsupportedOperationException("Attempt to parse(" + prefix(pattern) + ") on frozen MessagePattern instance.");
    }
    
    msg = pattern;
    hasArgNames = (this.hasArgNumbers = 0);
    needsAutoQuoting = false;
    parts.clear();
    if (numericValues != null) {
      numericValues.clear();
    }
  }
  

  private void postParse() {}
  
  private int parseMessage(int index, int msgStartLength, int nestingLevel, ArgType parentType)
  {
    if (nestingLevel > 32767) {
      throw new IndexOutOfBoundsException();
    }
    int msgStart = parts.size();
    addPart(MessagePattern.Part.Type.MSG_START, index, msgStartLength, nestingLevel);
    index += msgStartLength;
    label275: label448: while (index < msg.length()) {
      char c = msg.charAt(index++);
      if (c == '\'') {
        if (index == msg.length())
        {

          addPart(MessagePattern.Part.Type.INSERT_CHAR, index, 0, 39);
          needsAutoQuoting = true;
        } else {
          c = msg.charAt(index);
          if (c == '\'')
          {
            addPart(MessagePattern.Part.Type.SKIP_SYNTAX, index++, 1, 0);
          } else if ((aposMode == ApostropheMode.DOUBLE_REQUIRED) || (c == '{') || (c == '}') || ((parentType == ArgType.CHOICE) && (c == '|')) || ((parentType.hasPluralStyle()) && (c == '#')))
          {





            addPart(MessagePattern.Part.Type.SKIP_SYNTAX, index - 1, 1, 0);
            for (;;)
            {
              index = msg.indexOf('\'', index + 1);
              if (index < 0) break label275;
              if ((index + 1 >= msg.length()) || (msg.charAt(index + 1) != '\'')) {
                break;
              }
              addPart(MessagePattern.Part.Type.SKIP_SYNTAX, ++index, 1, 0);
            }
            
            addPart(MessagePattern.Part.Type.SKIP_SYNTAX, index++, 1, 0);
            

            break label448;
            
            index = msg.length();
            
            addPart(MessagePattern.Part.Type.INSERT_CHAR, index, 0, 39);
            needsAutoQuoting = true;


          }
          else
          {

            addPart(MessagePattern.Part.Type.INSERT_CHAR, index, 0, 39);
            needsAutoQuoting = true;
          }
        }
      } else if ((parentType.hasPluralStyle()) && (c == '#'))
      {

        addPart(MessagePattern.Part.Type.REPLACE_NUMBER, index - 1, 1, 0);
      } else if (c == '{') {
        index = parseArg(index - 1, 1, nestingLevel);
      } else if (((nestingLevel > 0) && (c == '}')) || ((parentType == ArgType.CHOICE) && (c == '|')))
      {


        int limitLength = (parentType == ArgType.CHOICE) && (c == '}') ? 0 : 1;
        addLimitPart(msgStart, MessagePattern.Part.Type.MSG_LIMIT, index - 1, limitLength, nestingLevel);
        if (parentType == ArgType.CHOICE)
        {
          return index - 1;
        }
        
        return index;
      }
    }
    
    if ((nestingLevel > 0) && (!inTopLevelChoiceMessage(nestingLevel, parentType))) {
      throw new IllegalArgumentException("Unmatched '{' braces in message " + prefix());
    }
    
    addLimitPart(msgStart, MessagePattern.Part.Type.MSG_LIMIT, index, 0, nestingLevel);
    return index;
  }
  
  private int parseArg(int index, int argStartLength, int nestingLevel) {
    int argStart = parts.size();
    ArgType argType = ArgType.NONE;
    addPart(MessagePattern.Part.Type.ARG_START, index, argStartLength, argType.ordinal());
    int nameIndex = index = skipWhiteSpace(index + argStartLength);
    if (index == msg.length()) {
      throw new IllegalArgumentException("Unmatched '{' braces in message " + prefix());
    }
    

    index = skipIdentifier(index);
    int number = parseArgNumber(nameIndex, index);
    if (number >= 0) {
      int length = index - nameIndex;
      if ((length > 65535) || (number > 32767)) {
        throw new IndexOutOfBoundsException("Argument number too large: " + prefix(nameIndex));
      }
      
      hasArgNumbers = true;
      addPart(MessagePattern.Part.Type.ARG_NUMBER, nameIndex, length, number);
    } else if (number == -1) {
      int length = index - nameIndex;
      if (length > 65535) {
        throw new IndexOutOfBoundsException("Argument name too long: " + prefix(nameIndex));
      }
      
      hasArgNames = true;
      addPart(MessagePattern.Part.Type.ARG_NAME, nameIndex, length, 0);
    } else {
      throw new IllegalArgumentException("Bad argument syntax: " + prefix(nameIndex));
    }
    index = skipWhiteSpace(index);
    if (index == msg.length()) {
      throw new IllegalArgumentException("Unmatched '{' braces in message " + prefix());
    }
    
    char c = msg.charAt(index);
    if (c != '}')
    {
      if (c != ',') {
        throw new IllegalArgumentException("Bad argument syntax: " + prefix(nameIndex));
      }
      
      int typeIndex = index = skipWhiteSpace(index + 1);
      while ((index < msg.length()) && (isArgTypeChar(msg.charAt(index)))) {
        index++;
      }
      int length = index - typeIndex;
      index = skipWhiteSpace(index);
      if (index == msg.length()) {
        throw new IllegalArgumentException("Unmatched '{' braces in message " + prefix());
      }
      
      if ((length == 0) || (((c = msg.charAt(index)) != ',') && (c != '}'))) {
        throw new IllegalArgumentException("Bad argument syntax: " + prefix(nameIndex));
      }
      if (length > 65535) {
        throw new IndexOutOfBoundsException("Argument type name too long: " + prefix(nameIndex));
      }
      
      argType = ArgType.SIMPLE;
      if (length == 6)
      {
        if (isChoice(typeIndex)) {
          argType = ArgType.CHOICE;
        } else if (isPlural(typeIndex)) {
          argType = ArgType.PLURAL;
        } else if (isSelect(typeIndex)) {
          argType = ArgType.SELECT;
        }
      } else if ((length == 13) && 
        (isSelect(typeIndex)) && (isOrdinal(typeIndex + 6))) {
        argType = ArgType.SELECTORDINAL;
      }
      

      parts.get(argStart)).value = ((short)argType.ordinal());
      if (argType == ArgType.SIMPLE) {
        addPart(MessagePattern.Part.Type.ARG_TYPE, typeIndex, length, 0);
      }
      
      if (c == '}') {
        if (argType != ArgType.SIMPLE) {
          throw new IllegalArgumentException("No style field for complex argument: " + prefix(nameIndex));
        }
      }
      else {
        index++;
        if (argType == ArgType.SIMPLE) {
          index = parseSimpleStyle(index);
        } else if (argType == ArgType.CHOICE) {
          index = parseChoiceStyle(index, nestingLevel);
        } else {
          index = parsePluralOrSelectStyle(argType, index, nestingLevel);
        }
      }
    }
    
    addLimitPart(argStart, MessagePattern.Part.Type.ARG_LIMIT, index, 1, argType.ordinal());
    return index + 1;
  }
  
  private int parseSimpleStyle(int index) {
    int start = index;
    int nestedBraces = 0;
    while (index < msg.length()) {
      char c = msg.charAt(index++);
      if (c == '\'')
      {

        index = msg.indexOf('\'', index);
        if (index < 0) {
          throw new IllegalArgumentException("Quoted literal argument style text reaches to the end of the message: " + prefix(start));
        }
        


        index++;
      } else if (c == '{') {
        nestedBraces++;
      } else if (c == '}') {
        if (nestedBraces > 0) {
          nestedBraces--;
        } else {
          index--;int length = index - start;
          if (length > 65535) {
            throw new IndexOutOfBoundsException("Argument style text too long: " + prefix(start));
          }
          
          addPart(MessagePattern.Part.Type.ARG_STYLE, start, length, 0);
          return index;
        }
      }
    }
    throw new IllegalArgumentException("Unmatched '{' braces in message " + prefix());
  }
  
  private int parseChoiceStyle(int index, int nestingLevel)
  {
    int start = index;
    index = skipWhiteSpace(index);
    if ((index == msg.length()) || (msg.charAt(index) == '}')) {
      throw new IllegalArgumentException("Missing choice argument pattern in " + prefix());
    }
    

    for (;;)
    {
      int numberIndex = index;
      index = skipDouble(index);
      int length = index - numberIndex;
      if (length == 0) {
        throw new IllegalArgumentException("Bad choice pattern syntax: " + prefix(start));
      }
      if (length > 65535) {
        throw new IndexOutOfBoundsException("Choice number too long: " + prefix(numberIndex));
      }
      
      parseDouble(numberIndex, index, true);
      
      index = skipWhiteSpace(index);
      if (index == msg.length()) {
        throw new IllegalArgumentException("Bad choice pattern syntax: " + prefix(start));
      }
      char c = msg.charAt(index);
      if ((c != '#') && (c != '<') && (c != '≤')) {
        throw new IllegalArgumentException("Expected choice separator (#<≤) instead of '" + c + "' in choice pattern " + prefix(start));
      }
      

      addPart(MessagePattern.Part.Type.ARG_SELECTOR, index, 1, 0);
      
      index = parseMessage(++index, 0, nestingLevel + 1, ArgType.CHOICE);
      
      if (index == msg.length()) {
        return index;
      }
      if (msg.charAt(index) == '}') {
        if (!inMessageFormatPattern(nestingLevel)) {
          throw new IllegalArgumentException("Bad choice pattern syntax: " + prefix(start));
        }
        
        return index;
      }
      index = skipWhiteSpace(index + 1);
    }
  }
  
  private int parsePluralOrSelectStyle(ArgType argType, int index, int nestingLevel) {
    int start = index;
    boolean isEmpty = true;
    boolean hasOther = false;
    

    for (;;)
    {
      index = skipWhiteSpace(index);
      boolean eos = index == msg.length();
      if ((eos) || (msg.charAt(index) == '}')) {
        if (eos == inMessageFormatPattern(nestingLevel)) {
          throw new IllegalArgumentException("Bad " + argType.toString().toLowerCase(Locale.ENGLISH) + " pattern syntax: " + prefix(start));
        }
        


        if (!hasOther) {
          throw new IllegalArgumentException("Missing 'other' keyword in " + argType.toString().toLowerCase(Locale.ENGLISH) + " pattern in " + prefix());
        }
        


        return index;
      }
      int selectorIndex = index;
      if ((argType.hasPluralStyle()) && (msg.charAt(selectorIndex) == '='))
      {
        index = skipDouble(index + 1);
        int length = index - selectorIndex;
        if (length == 1) {
          throw new IllegalArgumentException("Bad " + argType.toString().toLowerCase(Locale.ENGLISH) + " pattern syntax: " + prefix(start));
        }
        


        if (length > 65535) {
          throw new IndexOutOfBoundsException("Argument selector too long: " + prefix(selectorIndex));
        }
        
        addPart(MessagePattern.Part.Type.ARG_SELECTOR, selectorIndex, length, 0);
        parseDouble(selectorIndex + 1, index, false);
      } else {
        index = skipIdentifier(index);
        int length = index - selectorIndex;
        if (length == 0) {
          throw new IllegalArgumentException("Bad " + argType.toString().toLowerCase(Locale.ENGLISH) + " pattern syntax: " + prefix(start));
        }
        



        if ((argType.hasPluralStyle()) && (length == 6) && (index < msg.length()) && (msg.regionMatches(selectorIndex, "offset:", 0, 7)))
        {


          if (!isEmpty) {
            throw new IllegalArgumentException("Plural argument 'offset:' (if present) must precede key-message pairs: " + prefix(start));
          }
          


          int valueIndex = skipWhiteSpace(index + 1);
          index = skipDouble(valueIndex);
          if (index == valueIndex) {
            throw new IllegalArgumentException("Missing value for plural 'offset:' " + prefix(start));
          }
          
          if (index - valueIndex > 65535) {
            throw new IndexOutOfBoundsException("Plural offset value too long: " + prefix(valueIndex));
          }
          
          parseDouble(valueIndex, index, false);
          isEmpty = false;
          continue;
        }
        
        if (length > 65535) {
          throw new IndexOutOfBoundsException("Argument selector too long: " + prefix(selectorIndex));
        }
        
        addPart(MessagePattern.Part.Type.ARG_SELECTOR, selectorIndex, length, 0);
        if (msg.regionMatches(selectorIndex, "other", 0, length)) {
          hasOther = true;
        }
      }
      


      index = skipWhiteSpace(index);
      if ((index == msg.length()) || (msg.charAt(index) != '{')) {
        throw new IllegalArgumentException("No message fragment after " + argType.toString().toLowerCase(Locale.ENGLISH) + " selector: " + prefix(selectorIndex));
      }
      


      index = parseMessage(index, 1, nestingLevel + 1, argType);
      isEmpty = false;
    }
  }
  










  private static int parseArgNumber(CharSequence s, int start, int limit)
  {
    if (start >= limit) {
      return -2;
    }
    


    char c = s.charAt(start++);
    boolean badNumber; if (c == '0') {
      if (start == limit) {
        return 0;
      }
      int number = 0;
      badNumber = true;
    } else { boolean badNumber;
      if (('1' <= c) && (c <= '9')) {
        int number = c - '0';
        badNumber = false;
      } else {
        return -1; } }
    boolean badNumber;
    int number; while (start < limit) {
      c = s.charAt(start++);
      if (('0' <= c) && (c <= '9')) {
        if (number >= 214748364) {
          badNumber = true;
        }
        number = number * 10 + (c - '0');
      } else {
        return -1;
      }
    }
    
    if (badNumber) {
      return -2;
    }
    return number;
  }
  
  private int parseArgNumber(int start, int limit)
  {
    return parseArgNumber(msg, start, limit);
  }
  





  private void parseDouble(int start, int limit, boolean allowInfinity)
  {
    assert (start < limit);
    


    int value = 0;
    int isNegative = 0;
    int index = start;
    char c = msg.charAt(index++);
    if (c == '-') {
      isNegative = 1;
      if (index == limit) {
        break label263;
      }
      c = msg.charAt(index++);
    } else if (c == '+') {
      if (index == limit) {
        break label263;
      }
      c = msg.charAt(index++);
    }
    if (c == '∞') {
      if ((allowInfinity) && (index == limit)) {
        addArgDoublePart(isNegative != 0 ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY, start, limit - start);

      }
      

    }
    else
    {

      while (('0' <= c) && (c <= '9')) {
        value = value * 10 + (c - '0');
        if (value > 32767 + isNegative) {
          break;
        }
        if (index == limit) {
          addPart(MessagePattern.Part.Type.ARG_INT, start, limit - start, isNegative != 0 ? -value : value);
          return;
        }
        c = msg.charAt(index++);
      }
      
      double numericValue = Double.parseDouble(msg.substring(start, limit));
      addArgDoublePart(numericValue, start, limit - start);
      return; }
    label263:
    throw new NumberFormatException("Bad syntax for numeric value: " + msg.substring(start, limit));
  }
  






  static void appendReducedApostrophes(String s, int start, int limit, StringBuilder sb)
  {
    int doubleApos = -1;
    for (;;) {
      int i = s.indexOf('\'', start);
      if ((i < 0) || (i >= limit)) {
        sb.append(s, start, limit);
        break;
      }
      if (i == doubleApos)
      {
        sb.append('\'');
        start++;
        doubleApos = -1;
      }
      else {
        sb.append(s, start, i);
        doubleApos = start = i + 1;
      }
    }
  }
  
  private int skipWhiteSpace(int index) {
    return PatternProps.skipWhiteSpace(msg, index);
  }
  
  private int skipIdentifier(int index) {
    return PatternProps.skipIdentifier(msg, index);
  }
  



  private int skipDouble(int index)
  {
    while (index < msg.length()) {
      char c = msg.charAt(index);
      
      if (((c < '0') && ("+-.".indexOf(c) < 0)) || ((c > '9') && (c != 'e') && (c != 'E') && (c != '∞'))) {
        break;
      }
      index++;
    }
    return index;
  }
  
  private static boolean isArgTypeChar(int c) {
    return ((97 <= c) && (c <= 122)) || ((65 <= c) && (c <= 90));
  }
  
  private boolean isChoice(int index) {
    char c;
    return (((c = msg.charAt(index++)) == 'c') || (c == 'C')) && (((c = msg.charAt(index++)) == 'h') || (c == 'H')) && (((c = msg.charAt(index++)) == 'o') || (c == 'O')) && (((c = msg.charAt(index++)) == 'i') || (c == 'I')) && (((c = msg.charAt(index++)) == 'c') || (c == 'C')) && (((c = msg.charAt(index)) == 'e') || (c == 'E'));
  }
  



  private boolean isPlural(int index)
  {
    char c;
    

    return (((c = msg.charAt(index++)) == 'p') || (c == 'P')) && (((c = msg.charAt(index++)) == 'l') || (c == 'L')) && (((c = msg.charAt(index++)) == 'u') || (c == 'U')) && (((c = msg.charAt(index++)) == 'r') || (c == 'R')) && (((c = msg.charAt(index++)) == 'a') || (c == 'A')) && (((c = msg.charAt(index)) == 'l') || (c == 'L'));
  }
  



  private boolean isSelect(int index)
  {
    char c;
    

    return (((c = msg.charAt(index++)) == 's') || (c == 'S')) && (((c = msg.charAt(index++)) == 'e') || (c == 'E')) && (((c = msg.charAt(index++)) == 'l') || (c == 'L')) && (((c = msg.charAt(index++)) == 'e') || (c == 'E')) && (((c = msg.charAt(index++)) == 'c') || (c == 'C')) && (((c = msg.charAt(index)) == 't') || (c == 'T'));
  }
  



  private boolean isOrdinal(int index)
  {
    char c;
    

    return (((c = msg.charAt(index++)) == 'o') || (c == 'O')) && (((c = msg.charAt(index++)) == 'r') || (c == 'R')) && (((c = msg.charAt(index++)) == 'd') || (c == 'D')) && (((c = msg.charAt(index++)) == 'i') || (c == 'I')) && (((c = msg.charAt(index++)) == 'n') || (c == 'N')) && (((c = msg.charAt(index++)) == 'a') || (c == 'A')) && (((c = msg.charAt(index)) == 'l') || (c == 'L'));
  }
  










  private boolean inMessageFormatPattern(int nestingLevel)
  {
    return (nestingLevel > 0) || (parts.get(0)).type == MessagePattern.Part.Type.MSG_START);
  }
  



  private boolean inTopLevelChoiceMessage(int nestingLevel, ArgType parentType)
  {
    return (nestingLevel == 1) && (parentType == ArgType.CHOICE) && (parts.get(0)).type != MessagePattern.Part.Type.MSG_START);
  }
  


  private void addPart(MessagePattern.Part.Type type, int index, int length, int value)
  {
    parts.add(new Part(type, index, length, value, null));
  }
  
  private void addLimitPart(int start, MessagePattern.Part.Type type, int index, int length, int value) {
    parts.get(start)).limitPartIndex = parts.size();
    addPart(type, index, length, value);
  }
  
  private void addArgDoublePart(double numericValue, int start, int length) { int numericIndex;
    int numericIndex;
    if (numericValues == null) {
      numericValues = new ArrayList();
      numericIndex = 0;
    } else {
      numericIndex = numericValues.size();
      if (numericIndex > 32767) {
        throw new IndexOutOfBoundsException("Too many numeric values");
      }
    }
    numericValues.add(Double.valueOf(numericValue));
    addPart(MessagePattern.Part.Type.ARG_DOUBLE, start, length, numericIndex);
  }
  







  private static String prefix(String s, int start)
  {
    StringBuilder prefix = new StringBuilder(44);
    if (start == 0) {
      prefix.append("\"");
    } else {
      prefix.append("[at pattern index ").append(start).append("] \"");
    }
    int substringLength = s.length() - start;
    if (substringLength <= 24) {
      prefix.append(start == 0 ? s : s.substring(start));
    } else {
      int limit = start + 24 - 4;
      if (Character.isHighSurrogate(s.charAt(limit - 1)))
      {
        limit--;
      }
      prefix.append(s, start, limit).append(" ...");
    }
    return "\"";
  }
  
  private static String prefix(String s) {
    return prefix(s, 0);
  }
  
  private String prefix(int start) {
    return prefix(msg, start);
  }
  
  private String prefix() {
    return prefix(msg, 0);
  }
  


  private ArrayList<Part> parts = new ArrayList();
  
  private ArrayList<Double> numericValues;
  private boolean hasArgNames;
  private boolean hasArgNumbers;
  private boolean needsAutoQuoting;
  private boolean frozen;
  private static final ApostropheMode defaultAposMode = ApostropheMode.valueOf(ICUConfig.get("com.ibm.icu.text.MessagePattern.ApostropheMode", "DOUBLE_OPTIONAL"));
  


  private static final ArgType[] argTypes = ArgType.values();
}
