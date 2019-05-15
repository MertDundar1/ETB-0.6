package ch.qos.logback.core.pattern.parser;

import ch.qos.logback.core.pattern.util.AsIsEscapeUtil;
import ch.qos.logback.core.pattern.util.IEscapeUtil;
import ch.qos.logback.core.spi.ScanException;
import java.util.ArrayList;
import java.util.List;





























public class OptionTokenizer
{
  private static final int EXPECTING_STATE = 0;
  private static final int RAW_COLLECTING_STATE = 1;
  private static final int QUOTED_COLLECTING_STATE = 2;
  final IEscapeUtil escapeUtil;
  final TokenStream tokenStream;
  final String pattern;
  final int patternLength;
  char quoteChar;
  int state = 0;
  
  OptionTokenizer(TokenStream tokenStream) {
    this(tokenStream, new AsIsEscapeUtil());
  }
  
  OptionTokenizer(TokenStream tokenStream, IEscapeUtil escapeUtil) {
    this.tokenStream = tokenStream;
    pattern = pattern;
    patternLength = patternLength;
    this.escapeUtil = escapeUtil;
  }
  
  void tokenize(char firstChar, List<Token> tokenList) throws ScanException {
    StringBuffer buf = new StringBuffer();
    List<String> optionList = new ArrayList();
    char c = firstChar;
    
    while (tokenStream.pointer < patternLength) {
      switch (state) {
      case 0: 
        switch (c) {
        case '\t': 
        case '\n': 
        case '\r': 
        case ' ': 
        case ',': 
          break;
        case '"': 
        case '\'': 
          state = 2;
          quoteChar = c;
          break;
        case '}': 
          emitOptionToken(tokenList, optionList);
          return;
        default: 
          buf.append(c);
          state = 1;
        }
        break;
      case 1: 
        switch (c) {
        case ',': 
          optionList.add(buf.toString().trim());
          buf.setLength(0);
          state = 0;
          break;
        case '}': 
          optionList.add(buf.toString().trim());
          emitOptionToken(tokenList, optionList);
          return;
        default: 
          buf.append(c);
        }
        break;
      case 2: 
        if (c == quoteChar) {
          optionList.add(buf.toString());
          buf.setLength(0);
          state = 0;
        } else if (c == '\\') {
          escape(String.valueOf(quoteChar), buf);
        } else {
          buf.append(c);
        }
        
        break;
      }
      
      c = pattern.charAt(tokenStream.pointer);
      tokenStream.pointer += 1;
    }
    


    if (c == '}') {
      if (state == 0) {
        emitOptionToken(tokenList, optionList);
      } else if (state == 1) {
        optionList.add(buf.toString().trim());
        emitOptionToken(tokenList, optionList);
      } else {
        throw new ScanException("Unexpected end of pattern string in OptionTokenizer");
      }
    } else {
      throw new ScanException("Unexpected end of pattern string in OptionTokenizer");
    }
  }
  
  void emitOptionToken(List<Token> tokenList, List<String> optionList) {
    tokenList.add(new Token(1006, optionList));
    tokenStream.state = TokenStream.TokenizerState.LITERAL_STATE;
  }
  
  void escape(String escapeChars, StringBuffer buf) {
    if (tokenStream.pointer < patternLength) {
      char next = pattern.charAt(tokenStream.pointer++);
      escapeUtil.escape(escapeChars, buf, next, tokenStream.pointer);
    }
  }
}
