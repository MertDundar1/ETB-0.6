package ch.qos.logback.core.subst;

import ch.qos.logback.core.spi.ScanException;
import java.util.ArrayList;
import java.util.List;











public class Tokenizer
{
  final String pattern;
  final int patternLength;
  
  static enum TokenizerState
  {
    LITERAL_STATE,  START_STATE,  DEFAULT_VAL_STATE;
    
    private TokenizerState() {}
  }
  
  public Tokenizer(String pattern) {
    this.pattern = pattern;
    patternLength = pattern.length();
  }
  
  TokenizerState state = TokenizerState.LITERAL_STATE;
  int pointer = 0;
  
  List<Token> tokenize() throws ScanException {
    List<Token> tokenList = new ArrayList();
    StringBuilder buf = new StringBuilder();
    
    while (pointer < patternLength) {
      char c = pattern.charAt(pointer);
      pointer += 1;
      
      switch (1.$SwitchMap$ch$qos$logback$core$subst$Tokenizer$TokenizerState[state.ordinal()]) {
      case 1: 
        handleLiteralState(c, tokenList, buf);
        break;
      case 2: 
        handleStartState(c, tokenList, buf);
        break;
      case 3: 
        handleDefaultValueState(c, tokenList, buf);
      }
      
    }
    
    switch (1.$SwitchMap$ch$qos$logback$core$subst$Tokenizer$TokenizerState[state.ordinal()]) {
    case 1: 
      addLiteralToken(tokenList, buf);
      break;
    case 2: 
      throw new ScanException("Unexpected end of pattern string");
    }
    return tokenList;
  }
  
  private void handleDefaultValueState(char c, List<Token> tokenList, StringBuilder stringBuilder) {
    switch (c) {
    case '-': 
      tokenList.add(Token.DEFAULT_SEP_TOKEN);
      state = TokenizerState.LITERAL_STATE;
      break;
    case '$': 
      stringBuilder.append(':');
      addLiteralToken(tokenList, stringBuilder);
      stringBuilder.setLength(0);
      state = TokenizerState.START_STATE;
      break;
    default: 
      stringBuilder.append(':').append(c);
      state = TokenizerState.LITERAL_STATE;
    }
  }
  
  private void handleStartState(char c, List<Token> tokenList, StringBuilder stringBuilder)
  {
    if (c == '{') {
      tokenList.add(Token.START_TOKEN);
    } else {
      stringBuilder.append('$').append(c);
    }
    state = TokenizerState.LITERAL_STATE;
  }
  
  private void handleLiteralState(char c, List<Token> tokenList, StringBuilder stringBuilder) {
    if (c == '$') {
      addLiteralToken(tokenList, stringBuilder);
      stringBuilder.setLength(0);
      state = TokenizerState.START_STATE;
    } else if (c == ':') {
      addLiteralToken(tokenList, stringBuilder);
      stringBuilder.setLength(0);
      state = TokenizerState.DEFAULT_VAL_STATE;
    } else if (c == '{') {
      addLiteralToken(tokenList, stringBuilder);
      tokenList.add(Token.CURLY_LEFT_TOKEN);
      stringBuilder.setLength(0);
    } else if (c == '}') {
      addLiteralToken(tokenList, stringBuilder);
      tokenList.add(Token.CURLY_RIGHT_TOKEN);
      stringBuilder.setLength(0);
    } else {
      stringBuilder.append(c);
    }
  }
  
  private void addLiteralToken(List<Token> tokenList, StringBuilder stringBuilder)
  {
    if (stringBuilder.length() == 0)
      return;
    tokenList.add(new Token(Token.Type.LITERAL, stringBuilder.toString()));
  }
}
