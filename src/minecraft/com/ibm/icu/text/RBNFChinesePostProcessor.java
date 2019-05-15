package com.ibm.icu.text;





final class RBNFChinesePostProcessor
  implements RBNFPostProcessor
{
  private boolean longForm;
  


  private int format;
  


  private static final String[] rulesetNames = { "%traditional", "%simplified", "%accounting", "%time" };
  




  RBNFChinesePostProcessor() {}
  




  public void init(RuleBasedNumberFormat formatter, String rules) {}
  




  public void process(StringBuffer buf, NFRuleSet ruleSet)
  {
    String name = ruleSet.getName();
    for (int i = 0; i < rulesetNames.length; i++) {
      if (rulesetNames[i].equals(name)) {
        format = i;
        longForm = ((i == 1) || (i == 3));
        break;
      }
    }
    

    if (longForm) {
      for (int i = buf.indexOf("*"); i != -1; i = buf.indexOf("*", i)) {
        buf.delete(i, i + 1);
      }
      return;
    }
    
    String DIAN = "點";
    
    String[][] markers = { { "萬", "億", "兆", "〇" }, { "万", "亿", "兆", "〇" }, { "萬", "億", "兆", "零" } };
    






















    String[] m = markers[format];
    for (int i = 0; i < m.length - 1; i++) {
      int n = buf.indexOf(m[i]);
      if (n != -1) {
        buf.insert(n + m[i].length(), '|');
      }
    }
    

    int x = buf.indexOf("點");
    if (x == -1) {
      x = buf.length();
    }
    int s = 0;
    int n = -1;
    String ling = markers[format][3];
    while (x >= 0) {
      int m = buf.lastIndexOf("|", x);
      int nn = buf.lastIndexOf(ling, x);
      int ns = 0;
      if (nn > m) {
        ns = (nn > 0) && (buf.charAt(nn - 1) != '*') ? 2 : 1;
      }
      x = m - 1;
      

      switch (s * 3 + ns) {
      case 0: 
        s = ns;
        n = -1;
        break;
      case 1: 
        s = ns;
        n = nn;
        break;
      case 2: 
        s = ns;
        n = -1;
        break;
      case 3: 
        s = ns;
        n = -1;
        break;
      case 4: 
        buf.delete(nn - 1, nn + ling.length());
        s = 0;
        n = -1;
        break;
      case 5: 
        buf.delete(n - 1, n + ling.length());
        s = ns;
        n = -1;
        break;
      case 6: 
        s = ns;
        n = -1;
        break;
      case 7: 
        buf.delete(nn - 1, nn + ling.length());
        s = 0;
        n = -1;
        break;
      case 8: 
        s = ns;
        n = -1;
        break;
      default: 
        throw new IllegalStateException();
      }
      
    }
    int i = buf.length(); for (;;) { i--; if (i < 0) break;
      char c = buf.charAt(i);
      if ((c == '*') || (c == '|')) {
        buf.delete(i, i + 1);
      }
    }
  }
}
