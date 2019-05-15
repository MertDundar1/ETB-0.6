package com.ibm.icu.impl;

import com.ibm.icu.text.StringTransform;
import com.ibm.icu.text.SymbolTable;
import com.ibm.icu.text.UnicodeSet;
import com.ibm.icu.util.Freezable;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;















public class UnicodeRegex
  implements Cloneable, Freezable<UnicodeRegex>, StringTransform
{
  private SymbolTable symbolTable;
  
  public UnicodeRegex() {}
  
  public SymbolTable getSymbolTable()
  {
    return symbolTable;
  }
  



  public UnicodeRegex setSymbolTable(SymbolTable symbolTable)
  {
    this.symbolTable = symbolTable;
    return this;
  }
  



















  public String transform(String regex)
  {
    StringBuilder result = new StringBuilder();
    UnicodeSet temp = new UnicodeSet();
    ParsePosition pos = new ParsePosition(0);
    int state = 0;
    




    for (int i = 0; i < regex.length(); i++)
    {
      char ch = regex.charAt(i);
      switch (state) {
      case 0: 
        if (ch == '\\') {
          if (UnicodeSet.resemblesPattern(regex, i))
          {
            i = processSet(regex, i, result, temp, pos);
            continue;
          }
          state = 1;
        } else if (ch == '[')
        {
          if (UnicodeSet.resemblesPattern(regex, i))
            i = processSet(regex, i, result, temp, pos); }
        break;
      



      case 1: 
        if (ch == 'Q') {
          state = 1;
        } else {
          state = 0;
        }
        break;
      
      case 2: 
        if (ch == '\\') {
          state = 3;
        }
        
        break;
      case 3: 
        if (ch == 'E') {
          state = 0;
        }
        state = 2;
      }
      
      result.append(ch);
    }
    return result.toString();
  }
  




  public static String fix(String regex)
  {
    return STANDARD.transform(regex);
  }
  





  public static Pattern compile(String regex)
  {
    return Pattern.compile(STANDARD.transform(regex));
  }
  





  public static Pattern compile(String regex, int options)
  {
    return Pattern.compile(STANDARD.transform(regex), options);
  }
  





  public String compileBnf(String bnfLines)
  {
    return compileBnf(Arrays.asList(bnfLines.split("\\r\\n?|\\n")));
  }
  

























  public String compileBnf(List<String> lines)
  {
    Map<String, String> variables = getVariables(lines);
    Set<String> unused = new LinkedHashSet(variables.keySet());
    String variable;
    String definition;
    for (int i = 0; i < 2; i++) {
      for (Map.Entry<String, String> entry : variables.entrySet()) {
        variable = (String)entry.getKey();
        definition = (String)entry.getValue();
        
        for (Map.Entry<String, String> entry2 : variables.entrySet()) {
          String variable2 = (String)entry2.getKey();
          String definition2 = (String)entry2.getValue();
          if (!variable.equals(variable2))
          {

            String altered2 = definition2.replace(variable, definition);
            if (!altered2.equals(definition2)) {
              unused.remove(variable);
              variables.put(variable2, altered2);
              if (log != null)
                try {
                  log.append(variable2 + "=" + altered2 + ";");
                } catch (IOException e) {
                  throw ((IllegalArgumentException)new IllegalArgumentException().initCause(e));
                }
            }
          }
        }
      }
    }
    if (unused.size() != 1) {
      throw new IllegalArgumentException("Not a single root: " + unused);
    }
    return (String)variables.get(unused.iterator().next());
  }
  
  public String getBnfCommentString() {
    return bnfCommentString;
  }
  
  public void setBnfCommentString(String bnfCommentString) {
    this.bnfCommentString = bnfCommentString;
  }
  
  public String getBnfVariableInfix() {
    return bnfVariableInfix;
  }
  
  public void setBnfVariableInfix(String bnfVariableInfix) {
    this.bnfVariableInfix = bnfVariableInfix;
  }
  
  public String getBnfLineSeparator() {
    return bnfLineSeparator;
  }
  
  public void setBnfLineSeparator(String bnfLineSeparator) {
    this.bnfLineSeparator = bnfLineSeparator;
  }
  






  public static List<String> appendLines(List<String> result, String file, String encoding)
    throws IOException
  {
    return appendLines(result, new FileInputStream(file), encoding);
  }
  







  public static List<String> appendLines(List<String> result, InputStream inputStream, String encoding)
    throws UnsupportedEncodingException, IOException
  {
    BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, encoding == null ? "UTF-8" : encoding));
    for (;;) {
      String line = in.readLine();
      if (line == null) break;
      result.add(line);
    }
    return result;
  }
  




  public UnicodeRegex cloneAsThawed()
  {
    try
    {
      return (UnicodeRegex)clone();
    } catch (CloneNotSupportedException e) {
      throw new IllegalArgumentException();
    }
  }
  



  public UnicodeRegex freeze()
  {
    return this;
  }
  



  public boolean isFrozen()
  {
    return true;
  }
  
  private int processSet(String regex, int i, StringBuilder result, UnicodeSet temp, ParsePosition pos)
  {
    try
    {
      pos.setIndex(i);
      UnicodeSet x = temp.clear().applyPattern(regex, pos, symbolTable, 0);
      x.complement().complement();
      result.append(x.toPattern(false));
      return pos.getIndex() - 1;
    }
    catch (Exception e) {
      throw ((IllegalArgumentException)new IllegalArgumentException("Error in " + regex).initCause(e));
    }
  }
  
  private static UnicodeRegex STANDARD = new UnicodeRegex();
  private String bnfCommentString = "#";
  private String bnfVariableInfix = "=";
  private String bnfLineSeparator = "\n";
  private Appendable log = null;
  
  private Comparator<Object> LongestFirst = new Comparator() {
    public int compare(Object obj0, Object obj1) {
      String arg0 = obj0.toString();
      String arg1 = obj1.toString();
      int len0 = arg0.length();
      int len1 = arg1.length();
      if (len0 != len1) return len1 - len0;
      return arg0.compareTo(arg1);
    }
  };
  
  private Map<String, String> getVariables(List<String> lines) {
    Map<String, String> variables = new TreeMap(LongestFirst);
    String variable = null;
    StringBuffer definition = new StringBuffer();
    int count = 0;
    for (String line : lines) {
      count++;
      
      if (line.length() != 0) {
        if (line.charAt(0) == 65279) { line = line.substring(1);
        }
        if (bnfCommentString != null) {
          int hashPos = line.indexOf(bnfCommentString);
          if (hashPos >= 0) line = line.substring(0, hashPos);
        }
        String trimline = line.trim();
        if (trimline.length() != 0)
        {

          String linePart = line;
          if (linePart.trim().length() != 0) {
            boolean terminated = trimline.endsWith(";");
            if (terminated) {
              linePart = linePart.substring(0, linePart.lastIndexOf(';'));
            }
            int equalsPos = linePart.indexOf(bnfVariableInfix);
            if (equalsPos >= 0) {
              if (variable != null) {
                throw new IllegalArgumentException("Missing ';' before " + count + ") " + line);
              }
              variable = linePart.substring(0, equalsPos).trim();
              if (variables.containsKey(variable)) {
                throw new IllegalArgumentException("Duplicate variable definition in " + line);
              }
              definition.append(linePart.substring(equalsPos + 1).trim());
            } else {
              if (variable == null) {
                throw new IllegalArgumentException("Missing '=' at " + count + ") " + line);
              }
              definition.append(bnfLineSeparator).append(linePart);
            }
            
            if (terminated) {
              variables.put(variable, definition.toString());
              variable = null;
              definition.setLength(0);
            }
          } } } }
    if (variable != null) {
      throw new IllegalArgumentException("Missing ';' at end");
    }
    return variables;
  }
}
