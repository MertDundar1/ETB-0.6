package com.ibm.icu.util;

import java.util.ArrayList;
import java.util.HashMap;



















public abstract class StringTrieBuilder
{
  /**
   * @deprecated
   */
  protected StringTrieBuilder() {}
  
  public static enum Option
  {
    FAST, 
    









    SMALL;
    


    private Option() {}
  }
  


  /**
   * @deprecated
   */
  protected void addImpl(CharSequence s, int value)
  {
    if (state != State.ADDING)
    {
      throw new IllegalStateException("Cannot add (string, value) pairs after build().");
    }
    if (s.length() > 65535)
    {
      throw new IndexOutOfBoundsException("The maximum string length is 0xffff.");
    }
    if (root == null) {
      root = createSuffixNode(s, 0, value);
    } else {
      root = root.add(this, s, 0, value);
    }
  }
  
  /**
   * @deprecated
   */
  protected final void buildImpl(Option buildOption)
  {
    switch (1.$SwitchMap$com$ibm$icu$util$StringTrieBuilder$State[state.ordinal()]) {
    case 1: 
      if (root == null) {
        throw new IndexOutOfBoundsException("No (string, value) pairs were added.");
      }
      if (buildOption == Option.FAST) {
        state = State.BUILDING_FAST;




      }
      else
      {



        state = State.BUILDING_SMALL;
      }
      break;
    
    case 2: 
    case 3: 
      throw new IllegalStateException("Builder failed and must be clear()ed.");
    case 4: 
      return;
    }
    
    




    root = root.register(this);
    root.markRightEdgesFirst(-1);
    root.write(this);
    state = State.BUILT;
  }
  
  /**
   * @deprecated
   */
  protected void clearImpl()
  {
    strings.setLength(0);
    nodes.clear();
    root = null;
    state = State.ADDING;
  }
  






  private final Node registerNode(Node newNode)
  {
    if (state == State.BUILDING_FAST) {
      return newNode;
    }
    
    Node oldNode = (Node)nodes.get(newNode);
    if (oldNode != null) {
      return oldNode;
    }
    

    oldNode = (Node)nodes.put(newNode, newNode);
    assert (oldNode == null);
    return newNode;
  }
  








  private final ValueNode registerFinalValue(int value)
  {
    lookupFinalValueNode.setFinalValue(value);
    Node oldNode = (Node)nodes.get(lookupFinalValueNode);
    if (oldNode != null) {
      return (ValueNode)oldNode;
    }
    ValueNode newNode = new ValueNode(value);
    

    oldNode = (Node)nodes.put(newNode, newNode);
    assert (oldNode == null);
    return newNode;
  }
  
  private static abstract class Node { protected int offset;
    
    public Node() { offset = 0; }
    

    public abstract int hashCode();
    

    public boolean equals(Object other)
    {
      return (this == other) || (getClass() == other.getClass());
    }
    




    public Node add(StringTrieBuilder builder, CharSequence s, int start, int sValue)
    {
      return this;
    }
    





    public Node register(StringTrieBuilder builder)
    {
      return this;
    }
    
























    public int markRightEdgesFirst(int edgeNumber)
    {
      if (offset == 0) {
        offset = edgeNumber;
      }
      return edgeNumber;
    }
    



    public abstract void write(StringTrieBuilder paramStringTrieBuilder);
    


    public final void writeUnlessInsideRightEdge(int firstRight, int lastRight, StringTrieBuilder builder)
    {
      if ((offset < 0) && ((offset < lastRight) || (firstRight < offset)))
        write(builder);
    }
    
    public final int getOffset() { return offset; }
  }
  
  private static class ValueNode extends StringTrieBuilder.Node {
    protected boolean hasValue;
    protected int value;
    
    public ValueNode() {}
    
    public ValueNode(int v) {
      hasValue = true;
      value = v;
    }
    
    public final void setValue(int v) { assert (!hasValue);
      hasValue = true;
      value = v;
    }
    
    private void setFinalValue(int v) { hasValue = true;
      value = v;
    }
    
    public int hashCode() {
      int hash = 1118481;
      if (hasValue) {
        hash = hash * 37 + value;
      }
      return hash;
    }
    
    public boolean equals(Object other) {
      if (this == other) {
        return true;
      }
      if (!super.equals(other)) {
        return false;
      }
      ValueNode o = (ValueNode)other;
      return (hasValue == hasValue) && ((!hasValue) || (value == value));
    }
    
    public StringTrieBuilder.Node add(StringTrieBuilder builder, CharSequence s, int start, int sValue) {
      if (start == s.length()) {
        throw new IllegalArgumentException("Duplicate string.");
      }
      
      ValueNode node = builder.createSuffixNode(s, start, sValue);
      node.setValue(value);
      return node;
    }
    
    public void write(StringTrieBuilder builder) {
      offset = builder.writeValueAndFinal(value, true);
    }
  }
  
  private static final class IntermediateValueNode extends StringTrieBuilder.ValueNode
  {
    private StringTrieBuilder.Node next;
    
    public IntermediateValueNode(int v, StringTrieBuilder.Node nextNode) {
      next = nextNode;
      setValue(v);
    }
    
    public int hashCode() {
      return (82767594 + value) * 37 + next.hashCode();
    }
    
    public boolean equals(Object other) {
      if (this == other) {
        return true;
      }
      if (!super.equals(other)) {
        return false;
      }
      IntermediateValueNode o = (IntermediateValueNode)other;
      return next == next;
    }
    
    public int markRightEdgesFirst(int edgeNumber) {
      if (offset == 0) {
        offset = (edgeNumber = next.markRightEdgesFirst(edgeNumber));
      }
      return edgeNumber;
    }
    
    public void write(StringTrieBuilder builder) {
      next.write(builder);
      offset = builder.writeValueAndFinal(value, false); } }
  
  private static final class LinearMatchNode extends StringTrieBuilder.ValueNode { private CharSequence strings;
    private int stringOffset;
    private int length;
    private StringTrieBuilder.Node next;
    private int hash;
    
    public LinearMatchNode(CharSequence builderStrings, int sOffset, int len, StringTrieBuilder.Node nextNode) { strings = builderStrings;
      stringOffset = sOffset;
      length = len;
      next = nextNode;
    }
    
    public int hashCode() { return hash; }
    
    public boolean equals(Object other) {
      if (this == other) {
        return true;
      }
      if (!super.equals(other)) {
        return false;
      }
      LinearMatchNode o = (LinearMatchNode)other;
      if ((length != length) || (next != next)) {
        return false;
      }
      int i = stringOffset;int j = stringOffset; for (int limit = stringOffset + length; i < limit; j++) {
        if (strings.charAt(i) != strings.charAt(j)) {
          return false;
        }
        i++;
      }
      


      return true;
    }
    
    public StringTrieBuilder.Node add(StringTrieBuilder builder, CharSequence s, int start, int sValue) {
      if (start == s.length()) {
        if (hasValue) {
          throw new IllegalArgumentException("Duplicate string.");
        }
        setValue(sValue);
        return this;
      }
      
      int limit = stringOffset + length;
      for (int i = stringOffset; i < limit; start++) {
        if (start == s.length())
        {
          int prefixLength = i - stringOffset;
          LinearMatchNode suffixNode = new LinearMatchNode(strings, i, length - prefixLength, next);
          suffixNode.setValue(sValue);
          length = prefixLength;
          next = suffixNode;
          return this;
        }
        char thisChar = strings.charAt(i);
        char newChar = s.charAt(start);
        if (thisChar != newChar)
        {
          StringTrieBuilder.DynamicBranchNode branchNode = new StringTrieBuilder.DynamicBranchNode();
          StringTrieBuilder.Node result;
          StringTrieBuilder.Node thisSuffixNode;
          StringTrieBuilder.Node result; if (i == stringOffset)
          {
            if (hasValue)
            {
              branchNode.setValue(value);
              value = 0;
              hasValue = false;
            }
            stringOffset += 1;
            length -= 1;
            StringTrieBuilder.Node thisSuffixNode = length > 0 ? this : next;
            
            result = branchNode; } else { StringTrieBuilder.Node result;
            if (i == limit - 1)
            {
              length -= 1;
              StringTrieBuilder.Node thisSuffixNode = next;
              next = branchNode;
              result = this;
            }
            else {
              int prefixLength = i - stringOffset;
              i++;
              thisSuffixNode = new LinearMatchNode(strings, i, length - (prefixLength + 1), next);
              
              length = prefixLength;
              next = branchNode;
              result = this;
            } }
          StringTrieBuilder.ValueNode newSuffixNode = builder.createSuffixNode(s, start + 1, sValue);
          branchNode.add(thisChar, thisSuffixNode);
          branchNode.add(newChar, newSuffixNode);
          return result;
        }
        i++;
      }
      


















































      next = next.add(builder, s, start, sValue);
      return this;
    }
    
    public StringTrieBuilder.Node register(StringTrieBuilder builder) {
      next = next.register(builder);
      
      int maxLinearMatchLength = builder.getMaxLinearMatchLength();
      while (length > maxLinearMatchLength) {
        int nextOffset = stringOffset + length - maxLinearMatchLength;
        length -= maxLinearMatchLength;
        LinearMatchNode suffixNode = new LinearMatchNode(strings, nextOffset, maxLinearMatchLength, next);
        
        suffixNode.setHashCode();
        next = builder.registerNode(suffixNode); }
      StringTrieBuilder.Node result;
      StringTrieBuilder.Node result;
      if ((hasValue) && (!builder.matchNodesCanHaveValues())) {
        int intermediateValue = value;
        value = 0;
        hasValue = false;
        setHashCode();
        result = new StringTrieBuilder.IntermediateValueNode(intermediateValue, builder.registerNode(this));
      } else {
        setHashCode();
        result = this;
      }
      return builder.registerNode(result);
    }
    
    public int markRightEdgesFirst(int edgeNumber) {
      if (offset == 0) {
        offset = (edgeNumber = next.markRightEdgesFirst(edgeNumber));
      }
      return edgeNumber;
    }
    
    public void write(StringTrieBuilder builder) {
      next.write(builder);
      builder.write(stringOffset, length);
      offset = builder.writeValueAndType(hasValue, value, builder.getMinLinearMatch() + length - 1);
    }
    
    private void setHashCode()
    {
      hash = ((124151391 + length) * 37 + next.hashCode());
      if (hasValue) {
        hash = (hash * 37 + value);
      }
      int i = stringOffset; for (int limit = stringOffset + length; i < limit; i++) {
        hash = (hash * 37 + strings.charAt(i));
      }
    }
  }
  


  private static final class DynamicBranchNode
    extends StringTrieBuilder.ValueNode
  {
    public DynamicBranchNode() {}
    

    public void add(char c, StringTrieBuilder.Node node)
    {
      int i = find(c);
      chars.insert(i, c);
      equal.add(i, node);
    }
    
    public StringTrieBuilder.Node add(StringTrieBuilder builder, CharSequence s, int start, int sValue) {
      if (start == s.length()) {
        if (hasValue) {
          throw new IllegalArgumentException("Duplicate string.");
        }
        setValue(sValue);
        return this;
      }
      
      char c = s.charAt(start++);
      int i = find(c);
      if ((i < chars.length()) && (c == chars.charAt(i))) {
        equal.set(i, ((StringTrieBuilder.Node)equal.get(i)).add(builder, s, start, sValue));
      } else {
        chars.insert(i, c);
        equal.add(i, builder.createSuffixNode(s, start, sValue));
      }
      return this;
    }
    
    public StringTrieBuilder.Node register(StringTrieBuilder builder) {
      StringTrieBuilder.Node subNode = register(builder, 0, chars.length());
      StringTrieBuilder.BranchHeadNode head = new StringTrieBuilder.BranchHeadNode(chars.length(), subNode);
      StringTrieBuilder.Node result = head;
      if (hasValue) {
        if (builder.matchNodesCanHaveValues()) {
          head.setValue(value);
        } else {
          result = new StringTrieBuilder.IntermediateValueNode(value, builder.registerNode(head));
        }
      }
      return builder.registerNode(result);
    }
    
    private StringTrieBuilder.Node register(StringTrieBuilder builder, int start, int limit) { int length = limit - start;
      if (length > builder.getMaxBranchLinearSubNodeLength())
      {
        int middle = start + length / 2;
        return builder.registerNode(new StringTrieBuilder.SplitBranchNode(chars.charAt(middle), register(builder, start, middle), register(builder, middle, limit)));
      }
      



      StringTrieBuilder.ListBranchNode listNode = new StringTrieBuilder.ListBranchNode(length);
      do {
        char c = chars.charAt(start);
        StringTrieBuilder.Node node = (StringTrieBuilder.Node)equal.get(start);
        if (node.getClass() == StringTrieBuilder.ValueNode.class)
        {
          listNode.add(c, value);
        } else {
          listNode.add(c, node.register(builder));
        }
        start++; } while (start < limit);
      return builder.registerNode(listNode);
    }
    
    private int find(char c) {
      int start = 0;
      int limit = chars.length();
      while (start < limit) {
        int i = (start + limit) / 2;
        char middleChar = chars.charAt(i);
        if (c < middleChar) {
          limit = i;
        } else { if (c == middleChar) {
            return i;
          }
          start = i + 1;
        }
      }
      return start;
    }
    
    private StringBuilder chars = new StringBuilder();
    private ArrayList<StringTrieBuilder.Node> equal = new ArrayList(); }
  
  private static abstract class BranchNode extends StringTrieBuilder.Node { protected int hash;
    
    public BranchNode() {}
    
    public int hashCode() { return hash; }
    
    protected int firstEdgeNumber;
  }
  
  private static final class ListBranchNode extends StringTrieBuilder.BranchNode { private StringTrieBuilder.Node[] equal;
    private int length;
    
    public ListBranchNode(int capacity) { hash = (165535188 + capacity);
      equal = new StringTrieBuilder.Node[capacity];
      values = new int[capacity];
      units = new char[capacity];
    }
    
    public boolean equals(Object other) {
      if (this == other) {
        return true;
      }
      if (!super.equals(other)) {
        return false;
      }
      ListBranchNode o = (ListBranchNode)other;
      for (int i = 0; i < length; i++) {
        if ((units[i] != units[i]) || (values[i] != values[i]) || (equal[i] != equal[i])) {
          return false;
        }
      }
      return true;
    }
    
    public int hashCode() {
      return super.hashCode();
    }
    
    public int markRightEdgesFirst(int edgeNumber) {
      if (offset == 0) {
        firstEdgeNumber = edgeNumber;
        int step = 0;
        int i = length;
        do {
          StringTrieBuilder.Node edge = equal[(--i)];
          if (edge != null) {
            edgeNumber = edge.markRightEdgesFirst(edgeNumber - step);
          }
          
          step = 1;
        } while (i > 0);
        offset = edgeNumber;
      }
      return edgeNumber;
    }
    

    private int[] values;
    private char[] units;
    public void write(StringTrieBuilder builder)
    {
      int unitNumber = length - 1;
      StringTrieBuilder.Node rightEdge = equal[unitNumber];
      int rightEdgeNumber = rightEdge == null ? firstEdgeNumber : rightEdge.getOffset();
      do {
        unitNumber--;
        if (equal[unitNumber] != null) {
          equal[unitNumber].writeUnlessInsideRightEdge(firstEdgeNumber, rightEdgeNumber, builder);
        }
      } while (unitNumber > 0);
      

      unitNumber = length - 1;
      if (rightEdge == null) {
        builder.writeValueAndFinal(values[unitNumber], true);
      } else {
        rightEdge.write(builder);
      }
      offset = builder.write(units[unitNumber]);
      for (;;) {
        unitNumber--; if (unitNumber < 0) break;
        boolean isFinal;
        int value;
        boolean isFinal; if (equal[unitNumber] == null)
        {
          int value = values[unitNumber];
          isFinal = true;
        }
        else {
          assert (equal[unitNumber].getOffset() > 0);
          value = offset - equal[unitNumber].getOffset();
          isFinal = false;
        }
        builder.writeValueAndFinal(value, isFinal);
        offset = builder.write(units[unitNumber]);
      }
    }
    
    public void add(int c, int value) {
      units[length] = ((char)c);
      equal[length] = null;
      values[length] = value;
      length += 1;
      hash = ((hash * 37 + c) * 37 + value);
    }
    
    public void add(int c, StringTrieBuilder.Node node) {
      units[length] = ((char)c);
      equal[length] = node;
      values[length] = 0;
      length += 1;
      hash = ((hash * 37 + c) * 37 + node.hashCode());
    }
  }
  

  private static final class SplitBranchNode
    extends StringTrieBuilder.BranchNode
  {
    private char unit;
    
    private StringTrieBuilder.Node lessThan;
    private StringTrieBuilder.Node greaterOrEqual;
    
    public SplitBranchNode(char middleUnit, StringTrieBuilder.Node lessThanNode, StringTrieBuilder.Node greaterOrEqualNode)
    {
      hash = (((206918985 + middleUnit) * 37 + lessThanNode.hashCode()) * 37 + greaterOrEqualNode.hashCode());
      
      unit = middleUnit;
      lessThan = lessThanNode;
      greaterOrEqual = greaterOrEqualNode;
    }
    
    public boolean equals(Object other) {
      if (this == other) {
        return true;
      }
      if (!super.equals(other)) {
        return false;
      }
      SplitBranchNode o = (SplitBranchNode)other;
      return (unit == unit) && (lessThan == lessThan) && (greaterOrEqual == greaterOrEqual);
    }
    
    public int hashCode() {
      return super.hashCode();
    }
    
    public int markRightEdgesFirst(int edgeNumber) {
      if (offset == 0) {
        firstEdgeNumber = edgeNumber;
        edgeNumber = greaterOrEqual.markRightEdgesFirst(edgeNumber);
        offset = (edgeNumber = lessThan.markRightEdgesFirst(edgeNumber - 1));
      }
      return edgeNumber;
    }
    
    public void write(StringTrieBuilder builder)
    {
      lessThan.writeUnlessInsideRightEdge(firstEdgeNumber, greaterOrEqual.getOffset(), builder);
      
      greaterOrEqual.write(builder);
      
      assert (lessThan.getOffset() > 0);
      builder.writeDeltaTo(lessThan.getOffset());
      offset = builder.write(unit);
    }
  }
  
  private static final class BranchHeadNode extends StringTrieBuilder.ValueNode
  {
    private int length;
    private StringTrieBuilder.Node next;
    
    public BranchHeadNode(int len, StringTrieBuilder.Node subNode)
    {
      length = len;
      next = subNode;
    }
    
    public int hashCode() {
      return (248302782 + length) * 37 + next.hashCode();
    }
    
    public boolean equals(Object other) {
      if (this == other) {
        return true;
      }
      if (!super.equals(other)) {
        return false;
      }
      BranchHeadNode o = (BranchHeadNode)other;
      return (length == length) && (next == next);
    }
    
    public int markRightEdgesFirst(int edgeNumber) {
      if (offset == 0) {
        offset = (edgeNumber = next.markRightEdgesFirst(edgeNumber));
      }
      return edgeNumber;
    }
    
    public void write(StringTrieBuilder builder) {
      next.write(builder);
      if (length <= builder.getMinLinearMatch()) {
        offset = builder.writeValueAndType(hasValue, value, length - 1);
      } else {
        builder.write(length - 1);
        offset = builder.writeValueAndType(hasValue, value, 0);
      }
    }
  }
  


  private ValueNode createSuffixNode(CharSequence s, int start, int sValue)
  {
    ValueNode node = registerFinalValue(sValue);
    if (start < s.length()) {
      int offset = strings.length();
      strings.append(s, start, s.length());
      node = new LinearMatchNode(strings, offset, s.length() - start, node);
    }
    return node;
  }
  

  /**
   * @deprecated
   */
  protected abstract boolean matchNodesCanHaveValues();
  

  /**
   * @deprecated
   */
  protected abstract int getMaxBranchLinearSubNodeLength();
  
  /**
   * @deprecated
   */
  protected abstract int getMinLinearMatch();
  
  /**
   * @deprecated
   */
  protected abstract int getMaxLinearMatchLength();
  
  /**
   * @deprecated
   */
  protected abstract int write(int paramInt);
  
  /**
   * @deprecated
   */
  protected abstract int write(int paramInt1, int paramInt2);
  
  /**
   * @deprecated
   */
  protected abstract int writeValueAndFinal(int paramInt, boolean paramBoolean);
  
  /**
   * @deprecated
   */
  protected abstract int writeValueAndType(boolean paramBoolean, int paramInt1, int paramInt2);
  
  /**
   * @deprecated
   */
  protected abstract int writeDeltaTo(int paramInt);
  
  private static enum State
  {
    ADDING,  BUILDING_FAST,  BUILDING_SMALL,  BUILT;
    private State() {} }
  private State state = State.ADDING;
  


  /**
   * @deprecated
   */
  protected StringBuilder strings = new StringBuilder();
  
  private Node root;
  
  private HashMap<Node, Node> nodes = new HashMap();
  private ValueNode lookupFinalValueNode = new ValueNode();
}
