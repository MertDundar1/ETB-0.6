package com.mojang.realmsclient.gui;

import java.util.Map;

public enum ChatFormatting
{
  BLACK('0'), 
  DARK_BLUE('1'), 
  DARK_GREEN('2'), 
  DARK_AQUA('3'), 
  DARK_RED('4'), 
  DARK_PURPLE('5'), 
  GOLD('6'), 
  GRAY('7'), 
  DARK_GRAY('8'), 
  BLUE('9'), 
  GREEN('a'), 
  AQUA('b'), 
  RED('c'), 
  LIGHT_PURPLE('d'), 
  YELLOW('e'), 
  WHITE('f'), 
  OBFUSCATED('k', true), 
  BOLD('l', true), 
  STRIKETHROUGH('m', true), 
  UNDERLINE('n', true), 
  ITALIC('o', true), 
  RESET('r');
  
  public static final char PREFIX_CODE = '§';
  private static final Map<Character, ChatFormatting> FORMATTING_BY_CHAR;
  private static final Map<String, ChatFormatting> FORMATTING_BY_NAME;
  private static final java.util.regex.Pattern STRIP_FORMATTING_PATTERN;
  private final char code;
  private final boolean isFormat;
  private final String toString;
  
  private ChatFormatting(char code)
  {
    this(code, false);
  }
  
  private ChatFormatting(char code, boolean isFormat) {
    this.code = code;
    this.isFormat = isFormat;
    
    toString = ("§" + code);
  }
  
  static
  {
    FORMATTING_BY_CHAR = new java.util.HashMap();
    FORMATTING_BY_NAME = new java.util.HashMap();
    STRIP_FORMATTING_PATTERN = java.util.regex.Pattern.compile("(?i)" + String.valueOf('§') + "[0-9A-FK-OR]");
    
















    for (ChatFormatting format : values()) {
      FORMATTING_BY_CHAR.put(Character.valueOf(format.getChar()), format);
      FORMATTING_BY_NAME.put(format.getName(), format);
    }
  }
  
  public char getChar() {
    return code;
  }
  
  public boolean isFormat() {
    return isFormat;
  }
  
  public boolean isColor() {
    return (!isFormat) && (this != RESET);
  }
  
  public String getName() {
    return name().toLowerCase();
  }
  
  public String toString()
  {
    return toString;
  }
  
  public static String stripFormatting(String input) {
    return input == null ? null : STRIP_FORMATTING_PATTERN.matcher(input).replaceAll("");
  }
  
  public static ChatFormatting getByChar(char code) {
    return (ChatFormatting)FORMATTING_BY_CHAR.get(Character.valueOf(code));
  }
  
  public static ChatFormatting getByName(String name) {
    if (name == null) return null;
    return (ChatFormatting)FORMATTING_BY_NAME.get(name.toLowerCase());
  }
  
  public static java.util.Collection<String> getNames(boolean getColors, boolean getFormats) {
    java.util.List<String> result = new java.util.ArrayList();
    
    for (ChatFormatting format : values()) {
      if (((!format.isColor()) || (getColors)) && (
        (!format.isFormat()) || (getFormats))) {
        result.add(format.getName());
      }
    }
    return result;
  }
}
