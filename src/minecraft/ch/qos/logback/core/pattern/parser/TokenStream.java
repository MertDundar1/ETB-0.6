package ch.qos.logback.core.pattern.parser;

import ch.qos.logback.core.pattern.util.IEscapeUtil;
import ch.qos.logback.core.pattern.util.RegularEscapeUtil;
import ch.qos.logback.core.pattern.util.RestrictedEscapeUtil;
import ch.qos.logback.core.spi.ScanException;
import java.util.ArrayList;
import java.util.List;
































class TokenStream
{
  final String pattern;
  final int patternLength;
  final IEscapeUtil escapeUtil;
  
  static enum TokenizerState
  {
    LITERAL_STATE,  FORMAT_MODIFIER_STATE,  KEYWORD_STATE,  OPTION_STATE,  RIGHT_PARENTHESIS_STATE;
    

    private TokenizerState() {}
  }
  
  final IEscapeUtil optionEscapeUtil = new RestrictedEscapeUtil();
  
  TokenizerState state = TokenizerState.LITERAL_STATE;
  int pointer = 0;
  
  TokenStream(String pattern)
  {
    this(pattern, new RegularEscapeUtil());
  }
  
  TokenStream(String pattern, IEscapeUtil escapeUtil) {
    if ((pattern == null) || (pattern.length() == 0)) {
      throw new IllegalArgumentException("null or empty pattern string not allowed");
    }
    
    this.pattern = pattern;
    patternLength = pattern.length();
    this.escapeUtil = escapeUtil;
  }
  
  List tokenize() throws ScanException {
    List<Token> tokenList = new ArrayList();
    StringBuffer buf = new StringBuffer();
    
    while (pointer < patternLength) {
      char c = pattern.charAt(pointer);
      pointer += 1;
      
      switch (1.$SwitchMap$ch$qos$logback$core$pattern$parser$TokenStream$TokenizerState[state.ordinal()]) {
      case 1: 
        handleLiteralState(c, tokenList, buf);
        break;
      case 2: 
        handleFormatModifierState(c, tokenList, buf);
        break;
      case 3: 
        processOption(c, tokenList, buf);
        break;
      case 4: 
        handleKeywordState(c, tokenList, buf);
        break;
      case 5: 
        handleRightParenthesisState(c, tokenList, buf);
      }
      
    }
    



    switch (1.$SwitchMap$ch$qos$logback$core$pattern$parser$TokenStream$TokenizerState[state.ordinal()]) {
    case 1: 
      addValuedToken(1000, buf, tokenList);
      break;
    case 4: 
      tokenList.add(new Token(1004, buf.toString()));
      break;
    case 5: 
      tokenList.add(Token.RIGHT_PARENTHESIS_TOKEN);
      break;
    
    case 2: 
    case 3: 
      throw new ScanException("Unexpected end of pattern string");
    }
    
    return tokenList;
  }
  
  private void handleRightParenthesisState(char c, List<Token> tokenList, StringBuffer buf) {
    tokenList.add(Token.RIGHT_PARENTHESIS_TOKEN);
    switch (c) {
    case ')': 
      break;
    case '{': 
      state = TokenizerState.OPTION_STATE;
      break;
    case '\\': 
      escape("%{}", buf);
      state = TokenizerState.LITERAL_STATE;
      break;
    default: 
      buf.append(c);
      state = TokenizerState.LITERAL_STATE;
    }
  }
  
  private void processOption(char c, List<Token> tokenList, StringBuffer buf) throws ScanException {
    OptionTokenizer ot = new OptionTokenizer(this);
    ot.tokenize(c, tokenList);
  }
  
  private void handleFormatModifierState(char c, List<Token> tokenList, StringBuffer buf) {
    if (c == '(') {
      addValuedToken(1002, buf, tokenList);
      tokenList.add(Token.BARE_COMPOSITE_KEYWORD_TOKEN);
      state = TokenizerState.LITERAL_STATE;
    } else if (Character.isJavaIdentifierStart(c)) {
      addValuedToken(1002, buf, tokenList);
      state = TokenizerState.KEYWORD_STATE;
      buf.append(c);
    } else {
      buf.append(c);
    }
  }
  
  private void handleLiteralState(char c, List<Token> tokenList, StringBuffer buf) {
    switch (c) {
    case '\\': 
      escape("%()", buf);
      break;
    
    case '%': 
      addValuedToken(1000, buf, tokenList);
      tokenList.add(Token.PERCENT_TOKEN);
      state = TokenizerState.FORMAT_MODIFIER_STATE;
      break;
    
    case ')': 
      addValuedToken(1000, buf, tokenList);
      state = TokenizerState.RIGHT_PARENTHESIS_STATE;
      break;
    
    default: 
      buf.append(c);
    }
  }
  
  private void handleKeywordState(char c, List<Token> tokenList, StringBuffer buf)
  {
    if (Character.isJavaIdentifierPart(c)) {
      buf.append(c);
    } else if (c == '{') {
      addValuedToken(1004, buf, tokenList);
      state = TokenizerState.OPTION_STATE;
    } else if (c == '(') {
      addValuedToken(1005, buf, tokenList);
      state = TokenizerState.LITERAL_STATE;
    } else if (c == '%') {
      addValuedToken(1004, buf, tokenList);
      tokenList.add(Token.PERCENT_TOKEN);
      state = TokenizerState.FORMAT_MODIFIER_STATE;
    } else if (c == ')') {
      addValuedToken(1004, buf, tokenList);
      state = TokenizerState.RIGHT_PARENTHESIS_STATE;
    } else {
      addValuedToken(1004, buf, tokenList);
      if (c == '\\') {
        if (pointer < patternLength) {
          char next = pattern.charAt(pointer++);
          escapeUtil.escape("%()", buf, next, pointer);
        }
      } else {
        buf.append(c);
      }
      state = TokenizerState.LITERAL_STATE;
    }
  }
  
  void escape(String escapeChars, StringBuffer buf) {
    if (pointer < patternLength) {
      char next = pattern.charAt(pointer++);
      escapeUtil.escape(escapeChars, buf, next, pointer);
    }
  }
  
  void optionEscape(String escapeChars, StringBuffer buf) {
    if (pointer < patternLength) {
      char next = pattern.charAt(pointer++);
      optionEscapeUtil.escape(escapeChars, buf, next, pointer);
    }
  }
  


  private void addValuedToken(int type, StringBuffer buf, List<Token> tokenList)
  {
    if (buf.length() > 0) {
      tokenList.add(new Token(type, buf.toString()));
      buf.setLength(0);
    }
  }
}
