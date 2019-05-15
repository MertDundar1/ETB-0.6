package org.json;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


















































public class JSONPointer
{
  private static final String ENCODING = "utf-8";
  private final List<String> refTokens;
  
  public static class Builder
  {
    private final List<String> refTokens = new ArrayList();
    

    public Builder() {}
    
    public JSONPointer build()
    {
      return new JSONPointer(refTokens);
    }
    











    public Builder append(String token)
    {
      if (token == null) {
        throw new NullPointerException("token cannot be null");
      }
      refTokens.add(token);
      return this;
    }
    






    public Builder append(int arrayIndex)
    {
      refTokens.add(String.valueOf(arrayIndex));
      return this;
    }
  }
  














  public static Builder builder()
  {
    return new Builder();
  }
  










  public JSONPointer(String pointer)
  {
    if (pointer == null) {
      throw new NullPointerException("pointer cannot be null");
    }
    if ((pointer.isEmpty()) || (pointer.equals("#"))) {
      refTokens = Collections.emptyList();
      return;
    }
    
    if (pointer.startsWith("#/")) {
      String refs = pointer.substring(2);
      try {
        refs = URLDecoder.decode(refs, "utf-8");
      } catch (UnsupportedEncodingException e) {
        throw new RuntimeException(e);
      } } else { String refs;
      if (pointer.startsWith("/")) {
        refs = pointer.substring(1);
      } else
        throw new IllegalArgumentException("a JSON pointer should start with '/' or '#/'"); }
    String refs;
    refTokens = new ArrayList();
    int slashIdx = -1;
    int prevSlashIdx = 0;
    do {
      prevSlashIdx = slashIdx + 1;
      slashIdx = refs.indexOf('/', prevSlashIdx);
      if ((prevSlashIdx == slashIdx) || (prevSlashIdx == refs.length()))
      {

        refTokens.add("");
      } else if (slashIdx >= 0) {
        String token = refs.substring(prevSlashIdx, slashIdx);
        refTokens.add(unescape(token));
      }
      else {
        String token = refs.substring(prevSlashIdx);
        refTokens.add(unescape(token));
      }
    } while (
    













      slashIdx >= 0);
  }
  



  public JSONPointer(List<String> refTokens)
  {
    this.refTokens = new ArrayList(refTokens);
  }
  
  private String unescape(String token) {
    return 
    
      token.replace("~1", "/").replace("~0", "~").replace("\\\"", "\"").replace("\\\\", "\\");
  }
  








  public Object queryFrom(Object document)
    throws JSONPointerException
  {
    if (refTokens.isEmpty()) {
      return document;
    }
    Object current = document;
    for (String token : refTokens) {
      if ((current instanceof JSONObject)) {
        current = ((JSONObject)current).opt(unescape(token));
      } else if ((current instanceof JSONArray)) {
        current = readByIndexToken(current, token);
      } else {
        throw new JSONPointerException(String.format(
          "value [%s] is not an array or object therefore its key %s cannot be resolved", new Object[] { current, 
          token }));
      }
    }
    return current;
  }
  
  /* Error */
  private Object readByIndexToken(Object current, String indexToken)
    throws JSONPointerException
  {
    // Byte code:
    //   0: aload_2
    //   1: invokestatic 184	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   4: istore_3
    //   5: aload_1
    //   6: checkcast 165	org/json/JSONArray
    //   9: astore 4
    //   11: iload_3
    //   12: aload 4
    //   14: invokevirtual 190	org/json/JSONArray:length	()I
    //   17: if_icmplt +38 -> 55
    //   20: new 146	org/json/JSONPointerException
    //   23: dup
    //   24: ldc -65
    //   26: iconst_2
    //   27: anewarray 3	java/lang/Object
    //   30: dup
    //   31: iconst_0
    //   32: iload_3
    //   33: invokestatic 193	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   36: aastore
    //   37: dup
    //   38: iconst_1
    //   39: aload 4
    //   41: invokevirtual 190	org/json/JSONArray:length	()I
    //   44: invokestatic 193	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   47: aastore
    //   48: invokestatic 173	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   51: invokespecial 177	org/json/JSONPointerException:<init>	(Ljava/lang/String;)V
    //   54: athrow
    //   55: aload 4
    //   57: iload_3
    //   58: invokevirtual 197	org/json/JSONArray:get	(I)Ljava/lang/Object;
    //   61: areturn
    //   62: astore 5
    //   64: new 146	org/json/JSONPointerException
    //   67: dup
    //   68: new 201	java/lang/StringBuilder
    //   71: dup
    //   72: ldc -53
    //   74: invokespecial 205	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   77: iload_3
    //   78: invokevirtual 206	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   81: invokevirtual 210	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   84: aload 5
    //   86: invokespecial 214	org/json/JSONPointerException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   89: athrow
    //   90: astore_3
    //   91: new 146	org/json/JSONPointerException
    //   94: dup
    //   95: ldc -39
    //   97: iconst_1
    //   98: anewarray 3	java/lang/Object
    //   101: dup
    //   102: iconst_0
    //   103: aload_2
    //   104: aastore
    //   105: invokestatic 173	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   108: aload_3
    //   109: invokespecial 214	org/json/JSONPointerException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   112: athrow
    // Line number table:
    //   Java source line #233	-> byte code offset #0
    //   Java source line #234	-> byte code offset #5
    //   Java source line #235	-> byte code offset #11
    //   Java source line #236	-> byte code offset #20
    //   Java source line #237	-> byte code offset #39
    //   Java source line #236	-> byte code offset #48
    //   Java source line #240	-> byte code offset #55
    //   Java source line #241	-> byte code offset #62
    //   Java source line #242	-> byte code offset #64
    //   Java source line #244	-> byte code offset #90
    //   Java source line #245	-> byte code offset #91
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	113	0	this	JSONPointer
    //   0	113	1	current	Object
    //   0	113	2	indexToken	String
    //   4	74	3	index	int
    //   90	19	3	e	NumberFormatException
    //   9	47	4	currentArr	JSONArray
    //   62	23	5	e	JSONException
    // Exception table:
    //   from	to	target	type
    //   55	61	62	org/json/JSONException
    //   0	61	90	java/lang/NumberFormatException
    //   62	90	90	java/lang/NumberFormatException
  }
  
  public String toString()
  {
    StringBuilder rval = new StringBuilder("");
    for (String token : refTokens) {
      rval.append('/').append(escape(token));
    }
    return rval.toString();
  }
  







  private String escape(String token)
  {
    return 
    

      token.replace("~", "~0").replace("/", "~1").replace("\\", "\\\\").replace("\"", "\\\"");
  }
  


  public String toURIFragment()
  {
    try
    {
      StringBuilder rval = new StringBuilder("#");
      for (String token : refTokens) {
        rval.append('/').append(URLEncoder.encode(token, "utf-8"));
      }
      return rval.toString();
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }
}
