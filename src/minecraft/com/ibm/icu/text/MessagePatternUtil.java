package com.ibm.icu.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;































public final class MessagePatternUtil
{
  private MessagePatternUtil() {}
  
  public static MessageNode buildMessageNode(String patternString)
  {
    return buildMessageNode(new MessagePattern(patternString));
  }
  







  public static MessageNode buildMessageNode(MessagePattern pattern)
  {
    int limit = pattern.countParts() - 1;
    if (limit < 0)
      throw new IllegalArgumentException("The MessagePattern is empty");
    if (pattern.getPartType(0) != MessagePattern.Part.Type.MSG_START) {
      throw new IllegalArgumentException("The MessagePattern does not represent a MessageFormat pattern");
    }
    
    return buildMessageNode(pattern, 0, limit);
  }
  





  public static class Node
  {
    private Node() {}
  }
  





  public static class MessageNode
    extends MessagePatternUtil.Node
  {
    public List<MessagePatternUtil.MessageContentsNode> getContents()
    {
      return list;
    }
    



    public String toString()
    {
      return list.toString();
    }
    

    private MessageNode() { super(); }
    
    private void addContentsNode(MessagePatternUtil.MessageContentsNode node) {
      if (((node instanceof MessagePatternUtil.TextNode)) && (!list.isEmpty()))
      {
        MessagePatternUtil.MessageContentsNode lastNode = (MessagePatternUtil.MessageContentsNode)list.get(list.size() - 1);
        if ((lastNode instanceof MessagePatternUtil.TextNode)) {
          MessagePatternUtil.TextNode textNode = (MessagePatternUtil.TextNode)lastNode;
          text = (text + text);
          return;
        }
      }
      list.add(node);
    }
    
    private MessageNode freeze() { list = Collections.unmodifiableList(list);
      return this;
    }
    
    private List<MessagePatternUtil.MessageContentsNode> list = new ArrayList();
  }
  




  public static class MessageContentsNode
    extends MessagePatternUtil.Node
  {
    private Type type;
    




    public static enum Type
    {
      TEXT, 
      




      ARG, 
      




      REPLACE_NUMBER;
      
      private Type() {}
    }
    
    public Type getType()
    {
      return type;
    }
    







    public String toString()
    {
      return "{REPLACE_NUMBER}";
    }
    
    private MessageContentsNode(Type type) {
      super();
      this.type = type;
    }
    
    private static MessageContentsNode createReplaceNumberNode() { return new MessageContentsNode(Type.REPLACE_NUMBER); }
  }
  



  public static class TextNode
    extends MessagePatternUtil.MessageContentsNode
  {
    private String text;
    



    public String getText()
    {
      return text;
    }
    



    public String toString()
    {
      return "«" + text + "»";
    }
    
    private TextNode(String text) {
      super(null);
      this.text = text;
    }
  }
  


  public static class ArgNode
    extends MessagePatternUtil.MessageContentsNode
  {
    private MessagePattern.ArgType argType;
    
    private String name;
    

    public MessagePattern.ArgType getArgType()
    {
      return argType;
    }
    


    public String getName()
    {
      return name;
    }
    


    public int getNumber()
    {
      return number;
    }
    


    public String getTypeName()
    {
      return typeName;
    }
    



    public String getSimpleStyle()
    {
      return style;
    }
    



    public MessagePatternUtil.ComplexArgStyleNode getComplexStyle()
    {
      return complexStyle;
    }
    



    public String toString()
    {
      StringBuilder sb = new StringBuilder();
      sb.append('{').append(name);
      if (argType != MessagePattern.ArgType.NONE) {
        sb.append(',').append(typeName);
        if (argType == MessagePattern.ArgType.SIMPLE) {
          if (style != null) {
            sb.append(',').append(style);
          }
        } else {
          sb.append(',').append(complexStyle.toString());
        }
      }
      return '}';
    }
    
    private ArgNode() {
      super(null);
    }
    
    private static ArgNode createArgNode() { return new ArgNode(); }
    



    private int number = -1;
    
    private String typeName;
    
    private String style;
    private MessagePatternUtil.ComplexArgStyleNode complexStyle;
  }
  
  public static class ComplexArgStyleNode
    extends MessagePatternUtil.Node
  {
    private MessagePattern.ArgType argType;
    private double offset;
    private boolean explicitOffset;
    
    public MessagePattern.ArgType getArgType()
    {
      return argType;
    }
    


    public boolean hasExplicitOffset()
    {
      return explicitOffset;
    }
    



    public double getOffset()
    {
      return offset;
    }
    


    public List<MessagePatternUtil.VariantNode> getVariants()
    {
      return list;
    }
    















    public MessagePatternUtil.VariantNode getVariantsByType(List<MessagePatternUtil.VariantNode> numericVariants, List<MessagePatternUtil.VariantNode> keywordVariants)
    {
      if (numericVariants != null) {
        numericVariants.clear();
      }
      keywordVariants.clear();
      MessagePatternUtil.VariantNode other = null;
      for (MessagePatternUtil.VariantNode variant : list) {
        if (variant.isSelectorNumeric()) {
          numericVariants.add(variant);
        } else if ("other".equals(variant.getSelector())) {
          if (other == null)
          {
            other = variant;
          }
        } else {
          keywordVariants.add(variant);
        }
      }
      return other;
    }
    



    public String toString()
    {
      StringBuilder sb = new StringBuilder();
      sb.append('(').append(argType.toString()).append(" style) ");
      if (hasExplicitOffset()) {
        sb.append("offset:").append(offset).append(' ');
      }
      return list.toString();
    }
    
    private ComplexArgStyleNode(MessagePattern.ArgType argType) {
      super();
      this.argType = argType;
    }
    
    private void addVariant(MessagePatternUtil.VariantNode variant) { list.add(variant); }
    
    private ComplexArgStyleNode freeze() {
      list = Collections.unmodifiableList(list);
      return this;
    }
    



    private List<MessagePatternUtil.VariantNode> list = new ArrayList();
  }
  




  public static class VariantNode
    extends MessagePatternUtil.Node
  {
    private String selector;
    



    public String getSelector()
    {
      return selector;
    }
    


    public boolean isSelectorNumeric()
    {
      return numericValue != -1.23456789E8D;
    }
    


    public double getSelectorValue()
    {
      return numericValue;
    }
    


    public MessagePatternUtil.MessageNode getMessage()
    {
      return msgNode;
    }
    



    public String toString()
    {
      StringBuilder sb = new StringBuilder();
      if (isSelectorNumeric()) {
        sb.append(numericValue).append(" (").append(selector).append(") {");
      } else {
        sb.append(selector).append(" {");
      }
      return msgNode.toString() + '}';
    }
    
    private VariantNode() {
      super();
    }
    

    private double numericValue = -1.23456789E8D;
    private MessagePatternUtil.MessageNode msgNode;
  }
  
  private static MessageNode buildMessageNode(MessagePattern pattern, int start, int limit) {
    int prevPatternIndex = pattern.getPart(start).getLimit();
    MessageNode node = new MessageNode(null);
    for (int i = start + 1;; i++) {
      MessagePattern.Part part = pattern.getPart(i);
      int patternIndex = part.getIndex();
      if (prevPatternIndex < patternIndex) {
        node.addContentsNode(new TextNode(pattern.getPatternString().substring(prevPatternIndex, patternIndex), null));
      }
      

      if (i == limit) {
        break;
      }
      MessagePattern.Part.Type partType = part.getType();
      if (partType == MessagePattern.Part.Type.ARG_START) {
        int argLimit = pattern.getLimitPartIndex(i);
        node.addContentsNode(buildArgNode(pattern, i, argLimit));
        i = argLimit;
        part = pattern.getPart(i);
      } else if (partType == MessagePattern.Part.Type.REPLACE_NUMBER) {
        node.addContentsNode(MessageContentsNode.access$600());
      }
      
      prevPatternIndex = part.getLimit();
    }
    return node.freeze();
  }
  
  private static ArgNode buildArgNode(MessagePattern pattern, int start, int limit) {
    ArgNode node = ArgNode.access$800();
    MessagePattern.Part part = pattern.getPart(start);
    MessagePattern.ArgType argType = argType = part.getArgType();
    part = pattern.getPart(++start);
    name = pattern.getSubstring(part);
    if (part.getType() == MessagePattern.Part.Type.ARG_NUMBER) {
      number = part.getValue();
    }
    start++;
    switch (1.$SwitchMap$com$ibm$icu$text$MessagePattern$ArgType[argType.ordinal()])
    {
    case 1: 
      typeName = pattern.getSubstring(pattern.getPart(start++));
      if (start < limit)
      {
        style = pattern.getSubstring(pattern.getPart(start));
      }
      break;
    case 2: 
      typeName = "choice";
      complexStyle = buildChoiceStyleNode(pattern, start, limit);
      break;
    case 3: 
      typeName = "plural";
      complexStyle = buildPluralStyleNode(pattern, start, limit, argType);
      break;
    case 4: 
      typeName = "select";
      complexStyle = buildSelectStyleNode(pattern, start, limit);
      break;
    case 5: 
      typeName = "selectordinal";
      complexStyle = buildPluralStyleNode(pattern, start, limit, argType);
      break;
    }
    
    

    return node;
  }
  
  private static ComplexArgStyleNode buildChoiceStyleNode(MessagePattern pattern, int start, int limit)
  {
    ComplexArgStyleNode node = new ComplexArgStyleNode(MessagePattern.ArgType.CHOICE, null);
    while (start < limit) {
      int valueIndex = start;
      MessagePattern.Part part = pattern.getPart(start);
      double value = pattern.getNumericValue(part);
      start += 2;
      int msgLimit = pattern.getLimitPartIndex(start);
      VariantNode variant = new VariantNode(null);
      selector = pattern.getSubstring(pattern.getPart(valueIndex + 1));
      numericValue = value;
      msgNode = buildMessageNode(pattern, start, msgLimit);
      node.addVariant(variant);
      start = msgLimit + 1;
    }
    return node.freeze();
  }
  

  private static ComplexArgStyleNode buildPluralStyleNode(MessagePattern pattern, int start, int limit, MessagePattern.ArgType argType)
  {
    ComplexArgStyleNode node = new ComplexArgStyleNode(argType, null);
    MessagePattern.Part offset = pattern.getPart(start);
    if (offset.getType().hasNumericValue()) {
      explicitOffset = true;
      offset = pattern.getNumericValue(offset);
      start++;
    }
    while (start < limit) {
      MessagePattern.Part selector = pattern.getPart(start++);
      double value = -1.23456789E8D;
      MessagePattern.Part part = pattern.getPart(start);
      if (part.getType().hasNumericValue()) {
        value = pattern.getNumericValue(part);
        start++;
      }
      int msgLimit = pattern.getLimitPartIndex(start);
      VariantNode variant = new VariantNode(null);
      selector = pattern.getSubstring(selector);
      numericValue = value;
      msgNode = buildMessageNode(pattern, start, msgLimit);
      node.addVariant(variant);
      start = msgLimit + 1;
    }
    return node.freeze();
  }
  
  private static ComplexArgStyleNode buildSelectStyleNode(MessagePattern pattern, int start, int limit)
  {
    ComplexArgStyleNode node = new ComplexArgStyleNode(MessagePattern.ArgType.SELECT, null);
    while (start < limit) {
      MessagePattern.Part selector = pattern.getPart(start++);
      int msgLimit = pattern.getLimitPartIndex(start);
      VariantNode variant = new VariantNode(null);
      selector = pattern.getSubstring(selector);
      msgNode = buildMessageNode(pattern, start, msgLimit);
      node.addVariant(variant);
      start = msgLimit + 1;
    }
    return node.freeze();
  }
}
