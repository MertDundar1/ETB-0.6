package com.ibm.icu.impl;

import com.ibm.icu.lang.UCharacter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;












public class TextTrieMap<V>
{
  private TextTrieMap<V>.Node _root = new Node(null);
  

  boolean _ignoreCase;
  


  public TextTrieMap(boolean ignoreCase)
  {
    _ignoreCase = ignoreCase;
  }
  





  public TextTrieMap<V> put(CharSequence text, V val)
  {
    CharIterator chitr = new CharIterator(text, 0, _ignoreCase);
    _root.add(chitr, val);
    return this;
  }
  








  public Iterator<V> get(String text)
  {
    return get(text, 0);
  }
  










  public Iterator<V> get(CharSequence text, int start)
  {
    return get(text, start, null);
  }
  
  public Iterator<V> get(CharSequence text, int start, int[] matchLen) {
    LongestMatchHandler<V> handler = new LongestMatchHandler(null);
    find(text, start, handler);
    if ((matchLen != null) && (matchLen.length > 0)) {
      matchLen[0] = handler.getMatchLength();
    }
    return handler.getMatches();
  }
  
  public void find(CharSequence text, ResultHandler<V> handler) {
    find(text, 0, handler);
  }
  
  public void find(CharSequence text, int offset, ResultHandler<V> handler) {
    CharIterator chitr = new CharIterator(text, offset, _ignoreCase);
    find(_root, chitr, handler);
  }
  
  private synchronized void find(TextTrieMap<V>.Node node, CharIterator chitr, ResultHandler<V> handler) {
    Iterator<V> values = node.values();
    if ((values != null) && 
      (!handler.handlePrefixMatch(chitr.processedLength(), values))) {
      return;
    }
    

    TextTrieMap<V>.Node nextMatch = node.findMatch(chitr);
    if (nextMatch != null) {
      find(nextMatch, chitr, handler);
    }
  }
  
  public static class CharIterator implements Iterator<Character>
  {
    private boolean _ignoreCase;
    private CharSequence _text;
    private int _nextIdx;
    private int _startIdx;
    private Character _remainingChar;
    
    CharIterator(CharSequence text, int offset, boolean ignoreCase) {
      _text = text;
      _nextIdx = (this._startIdx = offset);
      _ignoreCase = ignoreCase;
    }
    


    public boolean hasNext()
    {
      if ((_nextIdx == _text.length()) && (_remainingChar == null)) {
        return false;
      }
      return true;
    }
    


    public Character next()
    {
      if ((_nextIdx == _text.length()) && (_remainingChar == null)) {
        return null;
      }
      Character next;
      if (_remainingChar != null) {
        Character next = _remainingChar;
        _remainingChar = null;
      }
      else if (_ignoreCase) {
        int cp = UCharacter.foldCase(Character.codePointAt(_text, _nextIdx), true);
        _nextIdx += Character.charCount(cp);
        
        char[] chars = Character.toChars(cp);
        Character next = Character.valueOf(chars[0]);
        if (chars.length == 2) {
          _remainingChar = Character.valueOf(chars[1]);
        }
      } else {
        next = Character.valueOf(_text.charAt(_nextIdx));
        _nextIdx += 1;
      }
      
      return next;
    }
    


    public void remove()
    {
      throw new UnsupportedOperationException("remove() not supproted");
    }
    
    public int nextIndex() {
      return _nextIdx;
    }
    
    public int processedLength() {
      if (_remainingChar != null) {
        throw new IllegalStateException("In the middle of surrogate pair");
      }
      return _nextIdx - _startIdx;
    }
  }
  


  public static abstract interface ResultHandler<V>
  {
    public abstract boolean handlePrefixMatch(int paramInt, Iterator<V> paramIterator);
  }
  


  private static class LongestMatchHandler<V>
    implements TextTrieMap.ResultHandler<V>
  {
    private LongestMatchHandler() {}
    


    private Iterator<V> matches = null;
    private int length = 0;
    
    public boolean handlePrefixMatch(int matchLength, Iterator<V> values) {
      if (matchLength > length) {
        length = matchLength;
        matches = values;
      }
      return true;
    }
    
    public Iterator<V> getMatches() {
      return matches;
    }
    
    public int getMatchLength() {
      return length;
    }
  }
  

  private class Node
  {
    private char[] _text;
    
    private List<V> _values;
    private List<TextTrieMap<V>.Node> _children;
    
    private Node() {}
    
    private Node(List<V> text, List<TextTrieMap<V>.Node> values)
    {
      _text = text;
      _values = values;
      _children = children;
    }
    
    public Iterator<V> values() {
      if (_values == null) {
        return null;
      }
      return _values.iterator();
    }
    
    public void add(TextTrieMap.CharIterator chitr, V value) {
      StringBuilder buf = new StringBuilder();
      while (chitr.hasNext()) {
        buf.append(chitr.next());
      }
      add(TextTrieMap.toCharArray(buf), 0, value);
    }
    
    public TextTrieMap<V>.Node findMatch(TextTrieMap.CharIterator chitr) {
      if (_children == null) {
        return null;
      }
      if (!chitr.hasNext()) {
        return null;
      }
      TextTrieMap<V>.Node match = null;
      Character ch = chitr.next();
      for (TextTrieMap<V>.Node child : _children) {
        if (ch.charValue() < _text[0]) {
          break;
        }
        if (ch.charValue() == _text[0]) {
          if (!child.matchFollowing(chitr)) break;
          match = child; break;
        }
      }
      

      return match;
    }
    
    private void add(char[] text, int offset, V value) {
      if (text.length == offset) {
        _values = addValue(_values, value);
        return;
      }
      
      if (_children == null) {
        _children = new LinkedList();
        TextTrieMap<V>.Node child = new Node(TextTrieMap.this, TextTrieMap.subArray(text, offset), addValue(null, value), null);
        _children.add(child);
        return;
      }
      

      ListIterator<TextTrieMap<V>.Node> litr = _children.listIterator();
      while (litr.hasNext()) {
        TextTrieMap<V>.Node next = (Node)litr.next();
        if (text[offset] < _text[0]) {
          litr.previous();
          break;
        }
        if (text[offset] == _text[0]) {
          int matchLen = next.lenMatches(text, offset);
          if (matchLen == _text.length)
          {
            next.add(text, offset + matchLen, value);
          }
          else {
            next.split(matchLen);
            next.add(text, offset + matchLen, value);
          }
          return;
        }
      }
      
      litr.add(new Node(TextTrieMap.this, TextTrieMap.subArray(text, offset), addValue(null, value), null));
    }
    
    private boolean matchFollowing(TextTrieMap.CharIterator chitr) {
      boolean matched = true;
      int idx = 1;
      while (idx < _text.length) {
        if (!chitr.hasNext()) {
          matched = false;
          break;
        }
        Character ch = chitr.next();
        if (ch.charValue() != _text[idx]) {
          matched = false;
          break;
        }
        idx++;
      }
      return matched;
    }
    
    private int lenMatches(char[] text, int offset) {
      int textLen = text.length - offset;
      int limit = _text.length < textLen ? _text.length : textLen;
      int len = 0;
      while ((len < limit) && 
        (_text[len] == text[(offset + len)]))
      {

        len++;
      }
      return len;
    }
    
    private void split(int offset)
    {
      char[] childText = TextTrieMap.subArray(_text, offset);
      _text = TextTrieMap.subArray(_text, 0, offset);
      

      TextTrieMap<V>.Node child = new Node(TextTrieMap.this, childText, _values, _children);
      _values = null;
      
      _children = new LinkedList();
      _children.add(child);
    }
    
    private List<V> addValue(List<V> list, V value) {
      if (list == null) {
        list = new LinkedList();
      }
      list.add(value);
      return list;
    }
  }
  
  private static char[] toCharArray(CharSequence text) {
    char[] array = new char[text.length()];
    for (int i = 0; i < array.length; i++) {
      array[i] = text.charAt(i);
    }
    return array;
  }
  
  private static char[] subArray(char[] array, int start) {
    if (start == 0) {
      return array;
    }
    char[] sub = new char[array.length - start];
    System.arraycopy(array, start, sub, 0, sub.length);
    return sub;
  }
  
  private static char[] subArray(char[] array, int start, int limit) {
    if ((start == 0) && (limit == array.length)) {
      return array;
    }
    char[] sub = new char[limit - start];
    System.arraycopy(array, start, sub, 0, limit - start);
    return sub;
  }
}
