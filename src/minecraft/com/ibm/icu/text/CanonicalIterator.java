package com.ibm.icu.text;

import com.ibm.icu.impl.Norm2AllModes;
import com.ibm.icu.impl.Normalizer2Impl;
import com.ibm.icu.impl.Utility;
import com.ibm.icu.lang.UCharacter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


































public final class CanonicalIterator
{
  public CanonicalIterator(String source)
  {
    Norm2AllModes allModes = Norm2AllModes.getNFCInstance();
    nfd = decomp;
    nfcImpl = impl.ensureCanonIterData();
    setSource(source);
  }
  




  public String getSource()
  {
    return source;
  }
  



  public void reset()
  {
    done = false;
    for (int i = 0; i < current.length; i++) {
      current[i] = 0;
    }
  }
  






  public String next()
  {
    if (done) { return null;
    }
    

    buffer.setLength(0);
    for (int i = 0; i < pieces.length; i++) {
      buffer.append(pieces[i][current[i]]);
    }
    String result = buffer.toString();
    


    for (int i = current.length - 1;; i--) {
      if (i < 0) {
        done = true;
        break;
      }
      current[i] += 1;
      if (current[i] < pieces[i].length) break;
      current[i] = 0;
    }
    return result;
  }
  





  public void setSource(String newSource)
  {
    source = nfd.normalize(newSource);
    done = false;
    

    if (newSource.length() == 0) {
      pieces = new String[1][];
      current = new int[1];
      pieces[0] = { "" };
      return;
    }
    

    List<String> segmentList = new ArrayList();
    
    int start = 0;
    



    int i = UTF16.findOffsetFromCodePoint(source, 1);
    int cp;
    for (; i < source.length(); i += Character.charCount(cp)) {
      cp = source.codePointAt(i);
      if (nfcImpl.isCanonSegmentStarter(cp)) {
        segmentList.add(source.substring(start, i));
        start = i;
      }
    }
    segmentList.add(source.substring(start, i));
    

    pieces = new String[segmentList.size()][];
    current = new int[segmentList.size()];
    for (i = 0; i < pieces.length; i++) {
      if (PROGRESS) System.out.println("SEGMENT");
      pieces[i] = getEquivalents((String)segmentList.get(i));
    }
  }
  











  /**
   * @deprecated
   */
  public static void permute(String source, boolean skipZeros, Set<String> output)
  {
    if ((source.length() <= 2) && (UTF16.countCodePoint(source) <= 1)) {
      output.add(source);
      return;
    }
    

    Set<String> subpermute = new HashSet();
    int cp;
    String chStr; for (int i = 0; i < source.length(); i += UTF16.getCharCount(cp)) {
      cp = UTF16.charAt(source, i);
      



      if ((!skipZeros) || (i == 0) || (UCharacter.getCombiningClass(cp) != 0))
      {




        subpermute.clear();
        permute(source.substring(0, i) + source.substring(i + UTF16.getCharCount(cp)), skipZeros, subpermute);
        


        chStr = UTF16.valueOf(source, i);
        for (String s : subpermute) {
          String piece = chStr + s;
          
          output.add(piece);
        }
      }
    }
  }
  





















  private static boolean PROGRESS = false;
  
  private static boolean SKIP_ZEROS = true;
  
  private final Normalizer2 nfd;
  
  private final Normalizer2Impl nfcImpl;
  
  private String source;
  
  private boolean done;
  
  private String[][] pieces;
  
  private int[] current;
  
  private transient StringBuilder buffer = new StringBuilder();
  

  private String[] getEquivalents(String segment)
  {
    Set<String> result = new HashSet();
    Set<String> basic = getEquivalents2(segment);
    Set<String> permutations = new HashSet();
    



    Iterator<String> it = basic.iterator();
    while (it.hasNext()) {
      String item = (String)it.next();
      permutations.clear();
      permute(item, SKIP_ZEROS, permutations);
      Iterator<String> it2 = permutations.iterator();
      while (it2.hasNext()) {
        String possible = (String)it2.next();
        




        if (Normalizer.compare(possible, segment, 0) == 0)
        {
          if (PROGRESS) System.out.println("Adding Permutation: " + Utility.hex(possible));
          result.add(possible);

        }
        else if (PROGRESS) { System.out.println("-Skipping Permutation: " + Utility.hex(possible));
        }
      }
    }
    

    String[] finalResult = new String[result.size()];
    result.toArray(finalResult);
    return finalResult;
  }
  

  private Set<String> getEquivalents2(String segment)
  {
    Set<String> result = new HashSet();
    
    if (PROGRESS) { System.out.println("Adding: " + Utility.hex(segment));
    }
    result.add(segment);
    StringBuffer workingBuffer = new StringBuffer();
    UnicodeSet starts = new UnicodeSet();
    int cp;
    UnicodeSetIterator iter;
    String prefix;
    for (int i = 0; i < segment.length(); i += Character.charCount(cp))
    {

      cp = segment.codePointAt(i);
      if (nfcImpl.getCanonStartSet(cp, starts))
      {


        for (iter = new UnicodeSetIterator(starts); iter.next();) {
          int cp2 = codepoint;
          Set<String> remainder = extract(cp2, segment, i, workingBuffer);
          if (remainder != null)
          {



            prefix = segment.substring(0, i);
            prefix = prefix + UTF16.valueOf(cp2);
            for (String item : remainder)
              result.add(prefix + item);
          }
        } }
    }
    return result;
  }
  








































  private Set<String> extract(int comp, String segment, int segmentPos, StringBuffer buf)
  {
    if (PROGRESS) { System.out.println(" extract: " + Utility.hex(UTF16.valueOf(comp)) + ", " + Utility.hex(segment.substring(segmentPos)));
    }
    
    String decomp = nfcImpl.getDecomposition(comp);
    if (decomp == null) {
      decomp = UTF16.valueOf(comp);
    }
    

    boolean ok = false;
    
    int decompPos = 0;
    int decompCp = UTF16.charAt(decomp, 0);
    decompPos += UTF16.getCharCount(decompCp);
    
    buf.setLength(0);
    int cp;
    for (int i = segmentPos; i < segment.length(); i += UTF16.getCharCount(cp)) {
      cp = UTF16.charAt(segment, i);
      if (cp == decompCp) {
        if (PROGRESS) System.out.println("  matches: " + Utility.hex(UTF16.valueOf(cp)));
        if (decompPos == decomp.length()) {
          buf.append(segment.substring(i + UTF16.getCharCount(cp)));
          ok = true;
          break;
        }
        decompCp = UTF16.charAt(decomp, decompPos);
        decompPos += UTF16.getCharCount(decompCp);
      }
      else {
        if (PROGRESS) { System.out.println("  buffer: " + Utility.hex(UTF16.valueOf(cp)));
        }
        UTF16.append(buf, cp);
      }
    }
    










    if (!ok) return null;
    if (PROGRESS) System.out.println("Matches");
    if (buf.length() == 0) return SET_WITH_NULL_STRING;
    String remainder = buf.toString();
    







    if (0 != Normalizer.compare(UTF16.valueOf(comp) + remainder, segment.substring(segmentPos), 0)) { return null;
    }
    
    return getEquivalents2(remainder);
  }
  












  private static final Set<String> SET_WITH_NULL_STRING = new HashSet();
  
  static { SET_WITH_NULL_STRING.add(""); }
}
