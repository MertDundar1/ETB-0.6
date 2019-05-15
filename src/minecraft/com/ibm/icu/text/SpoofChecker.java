package com.ibm.icu.text;

import com.ibm.icu.impl.ICUData;
import com.ibm.icu.impl.Trie2;
import com.ibm.icu.impl.Trie2Writable;
import com.ibm.icu.impl.Trie2_16;
import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.lang.UScript;
import com.ibm.icu.util.ULocale;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.Reader;
import java.text.ParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


























































































































public class SpoofChecker
{
  private SpoofChecker() {}
  
  public static enum RestrictionLevel
  {
    ASCII, 
    






    HIGHLY_RESTRICTIVE, 
    




    MODERATELY_RESTRICTIVE, 
    





    MINIMALLY_RESTRICTIVE, 
    




    UNRESTRICTIVE;
    


    private RestrictionLevel() {}
  }
  


  public static final UnicodeSet INCLUSION = new UnicodeSet("[\\-.\\u00B7\\u05F3\\u05F4\\u0F0B\\u200C\\u200D\\u2019]");
  






  public static final UnicodeSet RECOMMENDED = new UnicodeSet("[[0-z\\u00C0-\\u017E\\u01A0\\u01A1\\u01AF\\u01B0\\u01CD-\\u01DC\\u01DE-\\u01E3\\u01E6-\\u01F5\\u01F8-\\u021B\\u021E\\u021F\\u0226-\\u0233\\u02BB\\u02BC\\u02EC\\u0300-\\u0304\\u0306-\\u030C\\u030F-\\u0311\\u0313\\u0314\\u031B\\u0323-\\u0328\\u032D\\u032E\\u0330\\u0331\\u0335\\u0338\\u0339\\u0342-\\u0345\\u037B-\\u03CE\\u03FC-\\u045F\\u048A-\\u0525\\u0531-\\u0586\\u05D0-\\u05F2\\u0621-\\u063F\\u0641-\\u0655\\u0660-\\u0669\\u0670-\\u068D\\u068F-\\u06D5\\u06E5\\u06E6\\u06EE-\\u06FF\\u0750-\\u07B1\\u0901-\\u0939\\u093C-\\u094D\\u0950\\u0960-\\u0972\\u0979-\\u0A4D\\u0A5C-\\u0A74\\u0A81-\\u0B43\\u0B47-\\u0B61\\u0B66-\\u0C56\\u0C60\\u0C61\\u0C66-\\u0CD6\\u0CE0-\\u0CEF\\u0D02-\\u0D28\\u0D2A-\\u0D39\\u0D3D-\\u0D43\\u0D46-\\u0D4D\\u0D57-\\u0D61\\u0D66-\\u0D8E\\u0D91-\\u0DA5\\u0DA7-\\u0DDE\\u0DF2\\u0E01-\\u0ED9\\u0F00\\u0F20-\\u0F8B\\u0F90-\\u109D\\u10D0-\\u10F0\\u10F7-\\u10FA\\u1200-\\u135A\\u135F\\u1380-\\u138F\\u1401-\\u167F\\u1780-\\u17A2\\u17A5-\\u17A7\\u17A9-\\u17B3\\u17B6-\\u17CA\\u17D2\\u17D7-\\u17DC\\u17E0-\\u17E9\\u1810-\\u18A8\\u18AA-\\u18F5\\u1E00-\\u1E99\\u1F00-\\u1FFC\\u2D30-\\u2D65\\u2D80-\\u2DDE\\u3005-\\u3007\\u3041-\\u31B7\\u3400-\\u9FCB\\uA000-\\uA48C\\uA67F\\uA717-\\uA71F\\uA788\\uAA60-\\uAA7B\\uAC00-\\uD7A3\\uFA0E-\\uFA29\\U00020000-\\U0002B734]-[[:Cn:][:nfkcqc=n:][:XIDC=n:]]]");
  








  public static final int SINGLE_SCRIPT_CONFUSABLE = 1;
  








  public static final int MIXED_SCRIPT_CONFUSABLE = 2;
  








  public static final int WHOLE_SCRIPT_CONFUSABLE = 4;
  








  public static final int ANY_CASE = 8;
  








  public static final int RESTRICTION_LEVEL = 16;
  







  /**
   * @deprecated
   */
  public static final int SINGLE_SCRIPT = 16;
  







  public static final int INVISIBLE = 32;
  







  public static final int CHAR_LIMIT = 64;
  







  public static final int MIXED_NUMBERS = 128;
  







  public static final int ALL_CHECKS = -1;
  







  static final int MAGIC = 944111087;
  








  public static class Builder
  {
    int fMagic;
    







    int fChecks;
    







    SpoofChecker.SpoofData fSpoofData;
    







    final UnicodeSet fAllowedCharsSet = new UnicodeSet(0, 1114111);
    
    final Set<ULocale> fAllowedLocales = new LinkedHashSet();
    


    private SpoofChecker.RestrictionLevel fRestrictionLevel;
    



    public Builder()
    {
      fMagic = 944111087;
      fChecks = -1;
      fSpoofData = null;
      fRestrictionLevel = SpoofChecker.RestrictionLevel.HIGHLY_RESTRICTIVE;
    }
    






    public Builder(SpoofChecker src)
    {
      fMagic = fMagic;
      fChecks = fChecks;
      fSpoofData = null;
      fAllowedCharsSet.set(fAllowedCharsSet);
      fAllowedLocales.addAll(fAllowedLocales);
      fRestrictionLevel = fRestrictionLevel;
    }
    





    public SpoofChecker build()
    {
      if (fSpoofData == null) {
        try {
          fSpoofData = SpoofChecker.SpoofData.getDefault();
        } catch (IOException e) {
          return null;
        }
      }
      if (!SpoofChecker.SpoofData.validateDataVersion(fSpoofData.fRawData)) {
        return null;
      }
      SpoofChecker result = new SpoofChecker(null);
      fMagic = fMagic;
      fChecks = fChecks;
      fSpoofData = fSpoofData;
      fAllowedCharsSet = ((UnicodeSet)fAllowedCharsSet.clone());
      fAllowedCharsSet.freeze();
      fAllowedLocales = fAllowedLocales;
      fRestrictionLevel = fRestrictionLevel;
      return result;
    }
    















    public Builder setData(Reader confusables, Reader confusablesWholeScript)
      throws ParseException, IOException
    {
      fSpoofData = new SpoofChecker.SpoofData();
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      DataOutputStream os = new DataOutputStream(bos);
      
      ConfusabledataBuilder.buildConfusableData(fSpoofData, confusables);
      WSConfusableDataBuilder.buildWSConfusableData(fSpoofData, os, confusablesWholeScript);
      return this;
    }
    










    public Builder setChecks(int checks)
    {
      if (0 != (checks & 0x0)) {
        throw new IllegalArgumentException("Bad Spoof Checks value.");
      }
      fChecks = (checks & 0xFFFFFFFF);
      return this;
    }
    



























    public Builder setAllowedLocales(Set<ULocale> locales)
    {
      fAllowedCharsSet.clear();
      
      for (ULocale locale : locales)
      {

        addScriptChars(locale, fAllowedCharsSet);
      }
      


      fAllowedLocales.clear();
      if (locales.size() == 0) {
        fAllowedCharsSet.add(0, 1114111);
        fChecks &= 0xFFFFFFBF;
        return this;
      }
      


      UnicodeSet tempSet = new UnicodeSet();
      tempSet.applyIntPropertyValue(4106, 0);
      fAllowedCharsSet.addAll(tempSet);
      tempSet.applyIntPropertyValue(4106, 1);
      fAllowedCharsSet.addAll(tempSet);
      

      fAllowedLocales.addAll(locales);
      fChecks |= 0x40;
      return this;
    }
    


    private void addScriptChars(ULocale locale, UnicodeSet allowedChars)
    {
      int[] scripts = UScript.getCode(locale);
      UnicodeSet tmpSet = new UnicodeSet();
      
      for (int i = 0; i < scripts.length; i++) {
        tmpSet.applyIntPropertyValue(4106, scripts[i]);
        allowedChars.addAll(tmpSet);
      }
    }
    













    public Builder setAllowedChars(UnicodeSet chars)
    {
      fAllowedCharsSet.set(chars);
      fAllowedLocales.clear();
      fChecks |= 0x40;
      return this;
    }
    







    public Builder setRestrictionLevel(SpoofChecker.RestrictionLevel restrictionLevel)
    {
      fRestrictionLevel = restrictionLevel;
      fChecks |= 0x10;
      return this;
    }
    
































    private static class WSConfusableDataBuilder
    {
      static String parseExp = "(?m)^([ \\t]*(?:#.*?)?)$|^(?:\\s*([0-9A-F]{4,})(?:..([0-9A-F]{4,}))?\\s*;\\s*([A-Za-z]+)\\s*;\\s*([A-Za-z]+)\\s*;\\s*(?:(A)|(L))[ \\t]*(?:#.*?)?)$|^(.*?)$";
      






      private WSConfusableDataBuilder() {}
      





      static void readWholeFileToString(Reader reader, StringBuffer buffer)
        throws IOException
      {
        LineNumberReader lnr = new LineNumberReader(reader);
        for (;;) {
          String line = lnr.readLine();
          if (line == null) {
            break;
          }
          buffer.append(line);
          buffer.append('\n');
        }
      }
      

      static void buildWSConfusableData(SpoofChecker.SpoofData fSpoofData, DataOutputStream os, Reader confusablesWS)
        throws ParseException, IOException
      {
        Pattern parseRegexp = null;
        StringBuffer input = new StringBuffer();
        int lineNum = 0;
        
        Vector<BuilderScriptSet> scriptSets = null;
        int rtScriptSetsCount = 2;
        
        Trie2Writable anyCaseTrie = new Trie2Writable(0, 0);
        Trie2Writable lowerCaseTrie = new Trie2Writable(0, 0);
        













        scriptSets = new Vector();
        scriptSets.addElement(null);
        scriptSets.addElement(null);
        
        readWholeFileToString(confusablesWS, input);
        
        parseRegexp = Pattern.compile(parseExp);
        



        if (input.charAt(0) == 65279) {
          input.setCharAt(0, ' ');
        }
        

        Matcher matcher = parseRegexp.matcher(input);
        while (matcher.find()) {
          lineNum++;
          if (matcher.start(1) < 0)
          {


            if (matcher.start(8) >= 0)
            {
              throw new ParseException("ConfusablesWholeScript, line " + lineNum + ": Unrecognized input: " + matcher.group(), matcher.start());
            }
            



            int startCodePoint = Integer.parseInt(matcher.group(2), 16);
            if (startCodePoint > 1114111) {
              throw new ParseException("ConfusablesWholeScript, line " + lineNum + ": out of range code point: " + matcher.group(2), matcher.start(2));
            }
            
            int endCodePoint = startCodePoint;
            if (matcher.start(3) >= 0) {
              endCodePoint = Integer.parseInt(matcher.group(3), 16);
            }
            if (endCodePoint > 1114111) {
              throw new ParseException("ConfusablesWholeScript, line " + lineNum + ": out of range code point: " + matcher.group(3), matcher.start(3));
            }
            


            String srcScriptName = matcher.group(4);
            String targScriptName = matcher.group(5);
            int srcScript = UCharacter.getPropertyValueEnum(4106, srcScriptName);
            int targScript = UCharacter.getPropertyValueEnum(4106, targScriptName);
            if (srcScript == -1) {
              throw new ParseException("ConfusablesWholeScript, line " + lineNum + ": Invalid script code t: " + matcher.group(4), matcher.start(4));
            }
            
            if (targScript == -1) {
              throw new ParseException("ConfusablesWholeScript, line " + lineNum + ": Invalid script code t: " + matcher.group(5), matcher.start(5));
            }
            


            Trie2Writable table = anyCaseTrie;
            if (matcher.start(7) >= 0) {
              table = lowerCaseTrie;
            }
            










            for (int cp = startCodePoint; cp <= endCodePoint; cp++) {
              int setIndex = table.get(cp);
              BuilderScriptSet bsset = null;
              if (setIndex > 0) {
                assert (setIndex < scriptSets.size());
                bsset = (BuilderScriptSet)scriptSets.elementAt(setIndex);
              } else {
                bsset = new BuilderScriptSet();
                codePoint = cp;
                trie = table;
                sset = new SpoofChecker.ScriptSet();
                setIndex = scriptSets.size();
                index = setIndex;
                rindex = 0;
                scriptSets.addElement(bsset);
                table.set(cp, setIndex);
              }
              sset.Union(targScript);
              sset.Union(srcScript);
              
              int cpScript = UScript.getScript(cp);
              if (cpScript != srcScript)
              {
                throw new ParseException("ConfusablesWholeScript, line " + lineNum + ": Mismatch between source script and code point " + Integer.toString(cp, 16), matcher.start(5));
              }
            }
          }
        }
        










        rtScriptSetsCount = 2;
        for (int outeri = 2; outeri < scriptSets.size(); outeri++) {
          BuilderScriptSet outerSet = (BuilderScriptSet)scriptSets.elementAt(outeri);
          if (index == outeri)
          {




            rindex = (rtScriptSetsCount++);
            for (int inneri = outeri + 1; inneri < scriptSets.size(); inneri++) {
              BuilderScriptSet innerSet = (BuilderScriptSet)scriptSets.elementAt(inneri);
              if ((sset.equals(sset)) && (sset != sset)) {
                sset = sset;
                index = outeri;
                rindex = rindex;
              }
            }
          }
        }
        








        for (int i = 2; i < scriptSets.size(); i++) {
          BuilderScriptSet bSet = (BuilderScriptSet)scriptSets.elementAt(i);
          if (rindex != i) {
            trie.set(codePoint, rindex);
          }
        }
        






        UnicodeSet ignoreSet = new UnicodeSet();
        ignoreSet.applyIntPropertyValue(4106, 0);
        UnicodeSet inheritedSet = new UnicodeSet();
        inheritedSet.applyIntPropertyValue(4106, 1);
        ignoreSet.addAll(inheritedSet);
        for (int rn = 0; rn < ignoreSet.getRangeCount(); rn++) {
          int rangeStart = ignoreSet.getRangeStart(rn);
          int rangeEnd = ignoreSet.getRangeEnd(rn);
          anyCaseTrie.setRange(rangeStart, rangeEnd, 1, true);
          lowerCaseTrie.setRange(rangeStart, rangeEnd, 1, true);
        }
        



        anyCaseTrie.toTrie2_16().serialize(os);
        lowerCaseTrie.toTrie2_16().serialize(os);
        
        fRawData.fScriptSetsLength = rtScriptSetsCount;
        int rindex = 2;
        for (int i = 2; i < scriptSets.size(); i++) {
          BuilderScriptSet bSet = (BuilderScriptSet)scriptSets.elementAt(i);
          if (rindex >= rindex)
          {



            assert (rindex == rindex);
            sset.output(os);
            rindex++;
          }
        }
      }
      


      private static class BuilderScriptSet
      {
        int codePoint;
        

        Trie2Writable trie;
        

        SpoofChecker.ScriptSet sset;
        

        int index;
        

        int rindex;
        

        BuilderScriptSet()
        {
          codePoint = -1;
          trie = null;
          sset = null;
          index = 0;
          rindex = 0;
        }
      }
    }
    



    private static class ConfusabledataBuilder
    {
      private SpoofChecker.SpoofData fSpoofData;
      

      private ByteArrayOutputStream bos;
      

      private DataOutputStream os;
      

      private Hashtable<Integer, SPUString> fSLTable;
      

      private Hashtable<Integer, SPUString> fSATable;
      

      private Hashtable<Integer, SPUString> fMLTable;
      

      private Hashtable<Integer, SPUString> fMATable;
      

      private UnicodeSet fKeySet;
      

      private StringBuffer fStringTable;
      

      private Vector<Integer> fKeyVec;
      

      private Vector<Integer> fValueVec;
      

      private Vector<Integer> fStringLengthsTable;
      

      private SPUStringPool stringPool;
      

      private Pattern fParseLine;
      

      private Pattern fParseHexNum;
      

      private int fLineNum;
      


      ConfusabledataBuilder(SpoofChecker.SpoofData spData, ByteArrayOutputStream bos)
      {
        this.bos = bos;
        os = new DataOutputStream(bos);
        fSpoofData = spData;
        fSLTable = new Hashtable();
        fSATable = new Hashtable();
        fMLTable = new Hashtable();
        fMATable = new Hashtable();
        fKeySet = new UnicodeSet();
        fKeyVec = new Vector();
        fValueVec = new Vector();
        stringPool = new SPUStringPool();
      }
      
      void build(Reader confusables) throws ParseException, IOException {
        StringBuffer fInput = new StringBuffer();
        SpoofChecker.Builder.WSConfusableDataBuilder.readWholeFileToString(confusables, fInput);
        









        fParseLine = Pattern.compile("(?m)^[ \\t]*([0-9A-Fa-f]+)[ \\t]+;[ \\t]*([0-9A-Fa-f]+(?:[ \\t]+[0-9A-Fa-f]+)*)[ \\t]*;\\s*(?:(SL)|(SA)|(ML)|(MA))[ \\t]*(?:#.*?)?$|^([ \\t]*(?:#.*?)?)$|^(.*?)$");
        








        fParseHexNum = Pattern.compile("\\s*([0-9A-F]+)");
        


        if (fInput.charAt(0) == 65279) {
          fInput.setCharAt(0, ' ');
        }
        

        Matcher matcher = fParseLine.matcher(fInput);
        while (matcher.find()) {
          fLineNum += 1;
          if (matcher.start(7) < 0)
          {


            if (matcher.start(8) >= 0)
            {

              throw new ParseException("Confusables, line " + fLineNum + ": Unrecognized Line: " + matcher.group(8), matcher.start(8));
            }
            




            int keyChar = Integer.parseInt(matcher.group(1), 16);
            if (keyChar > 1114111) {
              throw new ParseException("Confusables, line " + fLineNum + ": Bad code point: " + matcher.group(1), matcher.start(1));
            }
            
            Matcher m = fParseHexNum.matcher(matcher.group(2));
            
            StringBuilder mapString = new StringBuilder();
            while (m.find()) {
              int c = Integer.parseInt(m.group(1), 16);
              if (keyChar > 1114111) {
                throw new ParseException("Confusables, line " + fLineNum + ": Bad code point: " + Integer.toString(c, 16), matcher.start(2));
              }
              
              mapString.appendCodePoint(c);
            }
            assert (mapString.length() >= 1);
            



            SPUString smapString = stringPool.addString(mapString.toString());
            

            Hashtable<Integer, SPUString> table = matcher.start(6) >= 0 ? fMATable : matcher.start(5) >= 0 ? fMLTable : matcher.start(4) >= 0 ? fSATable : matcher.start(3) >= 0 ? fSLTable : null;
            

            assert (table != null);
            table.put(Integer.valueOf(keyChar), smapString);
            fKeySet.add(keyChar);
          }
        }
        












        stringPool.sort();
        fStringTable = new StringBuffer();
        fStringLengthsTable = new Vector();
        int previousStringLength = 0;
        int previousStringIndex = 0;
        int poolSize = stringPool.size();
        
        for (int i = 0; i < poolSize; i++) {
          SPUString s = stringPool.getByIndex(i);
          int strLen = fStr.length();
          int strIndex = fStringTable.length();
          assert (strLen >= previousStringLength);
          if (strLen == 1)
          {





            fStrTableIndex = fStr.charAt(0);
          } else {
            if ((strLen > previousStringLength) && (previousStringLength >= 4)) {
              fStringLengthsTable.addElement(Integer.valueOf(previousStringIndex));
              fStringLengthsTable.addElement(Integer.valueOf(previousStringLength));
            }
            fStrTableIndex = strIndex;
            fStringTable.append(fStr);
          }
          previousStringLength = strLen;
          previousStringIndex = strIndex;
        }
        




        if (previousStringLength >= 4) {
          fStringLengthsTable.addElement(Integer.valueOf(previousStringIndex));
          fStringLengthsTable.addElement(Integer.valueOf(previousStringLength));
        }
        












        for (int range = 0; range < fKeySet.getRangeCount(); range++)
        {


          for (int keyChar = fKeySet.getRangeStart(range); keyChar <= fKeySet.getRangeEnd(range); keyChar++) {
            addKeyEntry(keyChar, fSLTable, 16777216);
            addKeyEntry(keyChar, fSATable, 33554432);
            addKeyEntry(keyChar, fMLTable, 67108864);
            addKeyEntry(keyChar, fMATable, 134217728);
          }
        }
        

        outputData();
      }
      













      void addKeyEntry(int keyChar, Hashtable<Integer, SPUString> table, int tableFlag)
      {
        SPUString targetMapping = (SPUString)table.get(Integer.valueOf(keyChar));
        if (targetMapping == null)
        {



          return;
        }
        




        boolean keyHasMultipleValues = false;
        
        for (int i = fKeyVec.size() - 1; i >= 0; i--) {
          int key = ((Integer)fKeyVec.elementAt(i)).intValue();
          if ((key & 0xFFFFFF) != keyChar) {
            break;
          }
          


          String mapping = getMapping(i);
          if (mapping.equals(fStr))
          {



            key |= tableFlag;
            fKeyVec.setElementAt(Integer.valueOf(key), i);
            return;
          }
          keyHasMultipleValues = true;
        }
        




        int newKey = keyChar | tableFlag;
        if (keyHasMultipleValues) {
          newKey |= 0x10000000;
        }
        int adjustedMappingLength = fStr.length() - 1;
        if (adjustedMappingLength > 3) {
          adjustedMappingLength = 3;
        }
        newKey |= adjustedMappingLength << 29;
        
        int newData = fStrTableIndex;
        
        fKeyVec.addElement(Integer.valueOf(newKey));
        fValueVec.addElement(Integer.valueOf(newData));
        



        if (keyHasMultipleValues) {
          int previousKeyIndex = fKeyVec.size() - 2;
          int previousKey = ((Integer)fKeyVec.elementAt(previousKeyIndex)).intValue();
          previousKey |= 0x10000000;
          fKeyVec.setElementAt(Integer.valueOf(previousKey), previousKeyIndex);
        }
      }
      

      String getMapping(int index)
      {
        int key = ((Integer)fKeyVec.elementAt(index)).intValue();
        int value = ((Integer)fValueVec.elementAt(index)).intValue();
        int length = SpoofChecker.getKeyLength(key);
        
        switch (length) {
        case 0: 
          char[] cs = { (char)value };
          return new String(cs);
        case 1: 
        case 2: 
          return fStringTable.substring(value, value + length + 1);
        case 3: 
          length = 0;
          
          for (int i = 0; i < fStringLengthsTable.size(); i += 2) {
            int lastIndexWithLen = ((Integer)fStringLengthsTable.elementAt(i)).intValue();
            if (value <= lastIndexWithLen) {
              length = ((Integer)fStringLengthsTable.elementAt(i + 1)).intValue();
              break;
            }
          }
          assert (length >= 3);
          return fStringTable.substring(value, value + length);
        }
        if (!$assertionsDisabled) { throw new AssertionError();
        }
        return "";
      }
      



      void outputData()
        throws IOException
      {
        SpoofChecker.SpoofDataHeader rawData = fSpoofData.fRawData;
        


        int numKeys = fKeyVec.size();
        
        int previousKey = 0;
        rawData.output(os);
        fCFUKeys = os.size();
        assert (fCFUKeys == 128);
        fCFUKeysSize = numKeys;
        for (int i = 0; i < numKeys; i++) {
          int key = ((Integer)fKeyVec.elementAt(i)).intValue();
          assert ((key & 0xFFFFFF) >= (previousKey & 0xFFFFFF));
          assert ((key & 0xFF000000) != 0);
          os.writeInt(key);
          previousKey = key;
        }
        

        int numValues = fValueVec.size();
        assert (numKeys == numValues);
        fCFUStringIndex = os.size();
        fCFUStringIndexSize = numValues;
        for (i = 0; i < numValues; i++) {
          int value = ((Integer)fValueVec.elementAt(i)).intValue();
          assert (value < 65535);
          os.writeShort((short)value);
        }
        


        int stringsLength = fStringTable.length();
        

        String strings = fStringTable.toString();
        fCFUStringTable = os.size();
        fCFUStringTableLen = stringsLength;
        for (i = 0; i < stringsLength; i++) {
          os.writeChar(strings.charAt(i));
        }
        






        int lengthTableLength = fStringLengthsTable.size();
        int previousLength = 0;
        


        fCFUStringLengthsSize = (lengthTableLength / 2);
        fCFUStringLengths = os.size();
        for (i = 0; i < lengthTableLength; i += 2) {
          int offset = ((Integer)fStringLengthsTable.elementAt(i)).intValue();
          int length = ((Integer)fStringLengthsTable.elementAt(i + 1)).intValue();
          assert (offset < stringsLength);
          assert (length < 40);
          assert (length > previousLength);
          os.writeShort((short)offset);
          os.writeShort((short)length);
          previousLength = length;
        }
        
        os.flush();
        DataInputStream is = new DataInputStream(new ByteArrayInputStream(bos.toByteArray()));
        is.mark(Integer.MAX_VALUE);
        fSpoofData.initPtrs(is);
      }
      
      public static void buildConfusableData(SpoofChecker.SpoofData spData, Reader confusables) throws IOException, ParseException
      {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ConfusabledataBuilder builder = new ConfusabledataBuilder(spData, bos);
        builder.build(confusables);
      }
      




      private static class SPUString
      {
        String fStr;
        


        int fStrTableIndex;
        



        SPUString(String s)
        {
          fStr = s;
          fStrTableIndex = 0;
        }
      }
      
      private static class SPUStringComparator implements Comparator<SpoofChecker.Builder.ConfusabledataBuilder.SPUString>
      {
        private SPUStringComparator() {}
        
        public int compare(SpoofChecker.Builder.ConfusabledataBuilder.SPUString sL, SpoofChecker.Builder.ConfusabledataBuilder.SPUString sR)
        {
          int lenL = fStr.length();
          int lenR = fStr.length();
          if (lenL < lenR)
            return -1;
          if (lenL > lenR) {
            return 1;
          }
          return fStr.compareTo(fStr);
        }
      }
      

      private static class SPUStringPool
      {
        private Vector<SpoofChecker.Builder.ConfusabledataBuilder.SPUString> fVec;
        
        private Hashtable<String, SpoofChecker.Builder.ConfusabledataBuilder.SPUString> fHash;
        
        public SPUStringPool()
        {
          fVec = new Vector();
          fHash = new Hashtable();
        }
        
        public int size() {
          return fVec.size();
        }
        
        public SpoofChecker.Builder.ConfusabledataBuilder.SPUString getByIndex(int index)
        {
          SpoofChecker.Builder.ConfusabledataBuilder.SPUString retString = (SpoofChecker.Builder.ConfusabledataBuilder.SPUString)fVec.elementAt(index);
          return retString;
        }
        


        public SpoofChecker.Builder.ConfusabledataBuilder.SPUString addString(String src)
        {
          SpoofChecker.Builder.ConfusabledataBuilder.SPUString hashedString = (SpoofChecker.Builder.ConfusabledataBuilder.SPUString)fHash.get(src);
          if (hashedString == null) {
            hashedString = new SpoofChecker.Builder.ConfusabledataBuilder.SPUString(src);
            fHash.put(src, hashedString);
            fVec.addElement(hashedString);
          }
          return hashedString;
        }
        
        public void sort()
        {
          Collections.sort(fVec, new SpoofChecker.Builder.ConfusabledataBuilder.SPUStringComparator(null));
        }
      }
    }
  }
  









  public RestrictionLevel getRestrictionLevel()
  {
    return fRestrictionLevel;
  }
  





  public int getChecks()
  {
    return fChecks;
  }
  












  public Set<ULocale> getAllowedLocales()
  {
    return fAllowedLocales;
  }
  









  public UnicodeSet getAllowedChars()
  {
    return fAllowedCharsSet;
  }
  






  public static class CheckResult
  {
    public int checks;
    





    /**
     * @deprecated
     */
    public int position;
    




    public UnicodeSet numerics;
    




    public SpoofChecker.RestrictionLevel restrictionLevel;
    





    public CheckResult()
    {
      checks = 0;
      position = 0;
    }
  }
  











  public boolean failsChecks(String text, CheckResult checkResult)
  {
    int length = text.length();
    
    int result = 0;
    if (checkResult != null) {
      position = 0;
      numerics = null;
      restrictionLevel = null;
    }
    


    IdentifierInfo identifierInfo = null;
    if (0 != (fChecks & 0x90)) {
      identifierInfo = getIdentifierInfo().setIdentifier(text).setIdentifierProfile(fAllowedCharsSet);
    }
    
    if (0 != (fChecks & 0x10)) {
      RestrictionLevel textRestrictionLevel = identifierInfo.getRestrictionLevel();
      if (textRestrictionLevel.compareTo(fRestrictionLevel) > 0) {
        result |= 0x10;
      }
      if (checkResult != null) {
        restrictionLevel = textRestrictionLevel;
      }
    }
    
    if (0 != (fChecks & 0x80)) {
      UnicodeSet numerics = identifierInfo.getNumerics();
      if (numerics.size() > 1) {
        result |= 0x80;
      }
      if (checkResult != null) {
        numerics = numerics;
      }
    }
    int i;
    if (0 != (fChecks & 0x40))
    {

      for (i = 0; i < length;)
      {
        int c = Character.codePointAt(text, i);
        i = Character.offsetByCodePoints(text, i, 1);
        if (!fAllowedCharsSet.contains(c)) {
          result |= 0x40;
        }
      }
    }
    

    if (0 != (fChecks & 0x26))
    {
      String nfdText = nfdNormalizer.normalize(text);
      int firstNonspacingMark;
      boolean haveMultipleMarks; UnicodeSet marksSeenSoFar; int i; if (0 != (fChecks & 0x20))
      {




        firstNonspacingMark = 0;
        haveMultipleMarks = false;
        marksSeenSoFar = new UnicodeSet();
        
        for (i = 0; i < length;) {
          int c = Character.codePointAt(nfdText, i);
          i = Character.offsetByCodePoints(nfdText, i, 1);
          if (Character.getType(c) != 6) {
            firstNonspacingMark = 0;
            if (haveMultipleMarks) {
              marksSeenSoFar.clear();
              haveMultipleMarks = false;
            }
            
          }
          else if (firstNonspacingMark == 0) {
            firstNonspacingMark = c;
          }
          else {
            if (!haveMultipleMarks) {
              marksSeenSoFar.add(firstNonspacingMark);
              haveMultipleMarks = true;
            }
            if (marksSeenSoFar.contains(c))
            {

              result |= 0x20;
              break;
            }
            marksSeenSoFar.add(c);
          }
        }
      }
      if (0 != (fChecks & 0x6))
      {












        if (identifierInfo == null) {
          identifierInfo = getIdentifierInfo();
          identifierInfo.setIdentifier(text);
        }
        int scriptCount = identifierInfo.getScriptCount();
        
        ScriptSet scripts = new ScriptSet();
        wholeScriptCheck(nfdText, scripts);
        int confusableScriptCount = scripts.countMembers();
        
        if ((0 != (fChecks & 0x4)) && (confusableScriptCount >= 2) && (scriptCount == 1)) {
          result |= 0x4;
        }
        
        if ((0 != (fChecks & 0x2)) && (confusableScriptCount >= 1) && (scriptCount > 1)) {
          result |= 0x2;
        }
      }
    }
    if (checkResult != null) {
      checks = result;
    }
    releaseIdentifierInfo(identifierInfo);
    return 0 != result;
  }
  








  public boolean failsChecks(String text)
  {
    return failsChecks(text, null);
  }
  


























  public int areConfusable(String s1, String s2)
  {
    if ((fChecks & 0x7) == 0) {
      throw new IllegalArgumentException("No confusable checks are enabled.");
    }
    int flagsForSkeleton = fChecks & 0x8;
    
    int result = 0;
    IdentifierInfo identifierInfo = getIdentifierInfo();
    identifierInfo.setIdentifier(s1);
    int s1ScriptCount = identifierInfo.getScriptCount();
    identifierInfo.setIdentifier(s2);
    int s2ScriptCount = identifierInfo.getScriptCount();
    releaseIdentifierInfo(identifierInfo);
    
    if (0 != (fChecks & 0x1))
    {
      if ((s1ScriptCount <= 1) && (s2ScriptCount <= 1)) {
        flagsForSkeleton |= 0x1;
        String s1Skeleton = getSkeleton(flagsForSkeleton, s1);
        String s2Skeleton = getSkeleton(flagsForSkeleton, s2);
        if (s1Skeleton.equals(s2Skeleton)) {
          result |= 0x1;
        }
      }
    }
    
    if (0 != (result & 0x1))
    {


      return result;
    }
    


    boolean possiblyWholeScriptConfusables = (s1ScriptCount <= 1) && (s2ScriptCount <= 1) && (0 != (fChecks & 0x4));
    


    if ((0 != (fChecks & 0x2)) || (possiblyWholeScriptConfusables))
    {


      flagsForSkeleton &= 0xFFFFFFFE;
      String s1Skeleton = getSkeleton(flagsForSkeleton, s1);
      String s2Skeleton = getSkeleton(flagsForSkeleton, s2);
      if (s1Skeleton.equals(s2Skeleton)) {
        result |= 0x2;
        if (possiblyWholeScriptConfusables) {
          result |= 0x4;
        }
      }
    }
    return result;
  }
  
















  public String getSkeleton(int type, String id)
  {
    int tableMask = 0;
    switch (type) {
    case 0: 
      tableMask = 67108864;
      break;
    case 1: 
      tableMask = 16777216;
      break;
    case 8: 
      tableMask = 134217728;
      break;
    case 9: 
      tableMask = 33554432;
      break;
    case 2: case 3: case 4: case 5: 
    case 6: case 7: default: 
      throw new IllegalArgumentException("SpoofChecker.getSkeleton(), bad type value.");
    }
    
    


    String nfdId = nfdNormalizer.normalize(id);
    int normalizedLen = nfdId.length();
    StringBuilder skelSB = new StringBuilder();
    for (int inputIndex = 0; inputIndex < normalizedLen;) {
      int c = Character.codePointAt(nfdId, inputIndex);
      inputIndex += Character.charCount(c);
      confusableLookup(c, tableMask, skelSB);
    }
    String skelStr = skelSB.toString();
    skelStr = nfdNormalizer.normalize(skelStr);
    return skelStr;
  }
  








  private void confusableLookup(int inChar, int tableMask, StringBuilder dest)
  {
    int low = 0;
    int mid = 0;
    int limit = fSpoofData.fRawData.fCFUKeysSize;
    
    boolean foundChar = false;
    do
    {
      int delta = (limit - low) / 2;
      mid = low + delta;
      int midc = fSpoofData.fCFUKeys[mid] & 0x1FFFFF;
      if (inChar == midc) {
        foundChar = true;
        break; }
      if (inChar < midc) {
        limit = mid;

      }
      else
      {
        low = mid + 1;
      }
    } while (low < limit);
    if (!foundChar) {
      dest.appendCodePoint(inChar);
      return;
    }
    
    boolean foundKey = false;
    int keyFlags = fSpoofData.fCFUKeys[mid] & 0xFF000000;
    if ((keyFlags & tableMask) == 0)
    {

      if (0 != (keyFlags & 0x10000000))
      {
        for (int altMid = mid - 1; (fSpoofData.fCFUKeys[altMid] & 0xFFFFFF) == inChar; altMid--) {
          keyFlags = fSpoofData.fCFUKeys[altMid] & 0xFF000000;
          if (0 != (keyFlags & tableMask)) {
            mid = altMid;
            foundKey = true;
            break;
          }
        }
        if (!foundKey) {
          for (altMid = mid + 1; (fSpoofData.fCFUKeys[altMid] & 0xFFFFFF) == inChar; altMid++) {
            keyFlags = fSpoofData.fCFUKeys[altMid] & 0xFF000000;
            if (0 != (keyFlags & tableMask)) {
              mid = altMid;
              foundKey = true;
              break;
            }
          }
        }
      }
      if (!foundKey)
      {

        dest.appendCodePoint(inChar);
        return;
      }
    }
    
    int stringLen = getKeyLength(keyFlags) + 1;
    int keyTableIndex = mid;
    


    short value = fSpoofData.fCFUValues[keyTableIndex];
    if (stringLen == 1) {
      dest.append((char)value);
      return;
    }
    










    if (stringLen == 4) {
      int stringLengthsLimit = fSpoofData.fRawData.fCFUStringLengthsSize;
      for (int ix = 0; ix < stringLengthsLimit; ix++) {
        if (fSpoofData.fCFUStringLengths[ix].fLastString >= value) {
          stringLen = fSpoofData.fCFUStringLengths[ix].fStrLength;
          break;
        }
      }
      assert (ix < stringLengthsLimit);
    }
    
    assert (value + stringLen <= fSpoofData.fRawData.fCFUStringTableLen);
    dest.append(fSpoofData.fCFUStrings, value, stringLen);
  }
  






  void wholeScriptCheck(CharSequence text, ScriptSet result)
  {
    int inputIdx = 0;
    

    Trie2 table = 0 != (fChecks & 0x8) ? fSpoofData.fAnyCaseTrie : fSpoofData.fLowerCaseTrie;
    result.setAll();
    while (inputIdx < text.length()) {
      int c = Character.codePointAt(text, inputIdx);
      inputIdx = Character.offsetByCodePoints(text, inputIdx, 1);
      int index = table.get(c);
      if (index == 0)
      {



        int cpScript = UScript.getScript(c);
        assert (cpScript > 1);
        result.intersect(cpScript);
      } else if (index != 1)
      {

        result.intersect(fSpoofData.fScriptSets[index]);
      }
    }
  }
  





  private IdentifierInfo fCachedIdentifierInfo = null;
  private int fMagic;
  
  private IdentifierInfo getIdentifierInfo() { IdentifierInfo returnIdInfo = null;
    synchronized (this) {
      returnIdInfo = fCachedIdentifierInfo;
      fCachedIdentifierInfo = null;
    }
    if (returnIdInfo == null) {
      returnIdInfo = new IdentifierInfo();
    }
    return returnIdInfo;
  }
  
  private void releaseIdentifierInfo(IdentifierInfo idInfo)
  {
    if (idInfo != null) {
      synchronized (this) {
        if (fCachedIdentifierInfo == null) {
          fCachedIdentifierInfo = idInfo;
        }
      }
    }
  }
  

  private int fChecks;
  
  private SpoofData fSpoofData;
  
  private Set<ULocale> fAllowedLocales;
  private UnicodeSet fAllowedCharsSet;
  private RestrictionLevel fRestrictionLevel;
  private static Normalizer2 nfdNormalizer = Normalizer2.getNFDInstance();
  






  static final int SL_TABLE_FLAG = 16777216;
  






  static final int SA_TABLE_FLAG = 33554432;
  






  static final int ML_TABLE_FLAG = 67108864;
  






  static final int MA_TABLE_FLAG = 134217728;
  






  static final int KEY_MULTIPLE_VALUES = 268435456;
  





  static final int KEY_LENGTH_SHIFT = 29;
  






  static final int getKeyLength(int x)
  {
    return x >> 29 & 0x3;
  }
  



  private static class SpoofDataHeader
  {
    int fMagic;
    

    byte[] fFormatVersion = new byte[4];
    

    int fLength;
    

    int fCFUKeys;
    

    int fCFUKeysSize;
    
    int fCFUStringIndex;
    
    int fCFUStringIndexSize;
    
    int fCFUStringTable;
    
    int fCFUStringTableLen;
    
    int fCFUStringLengths;
    
    int fCFUStringLengthsSize;
    
    int fAnyCaseTrie;
    
    int fAnyCaseTrieLength;
    
    int fLowerCaseTrie;
    
    int fLowerCaseTrieLength;
    
    int fScriptSets;
    
    int fScriptSetsLength;
    
    int[] unused = new int[15];
    
    public SpoofDataHeader() {}
    
    public SpoofDataHeader(DataInputStream dis)
      throws IOException
    {
      fMagic = dis.readInt();
      for (int i = 0; i < fFormatVersion.length; i++) {
        fFormatVersion[i] = dis.readByte();
      }
      fLength = dis.readInt();
      fCFUKeys = dis.readInt();
      fCFUKeysSize = dis.readInt();
      fCFUStringIndex = dis.readInt();
      fCFUStringIndexSize = dis.readInt();
      fCFUStringTable = dis.readInt();
      fCFUStringTableLen = dis.readInt();
      fCFUStringLengths = dis.readInt();
      fCFUStringLengthsSize = dis.readInt();
      fAnyCaseTrie = dis.readInt();
      fAnyCaseTrieLength = dis.readInt();
      fLowerCaseTrie = dis.readInt();
      fLowerCaseTrieLength = dis.readInt();
      fScriptSets = dis.readInt();
      fScriptSetsLength = dis.readInt();
      for (i = 0; i < unused.length; i++) {
        unused[i] = dis.readInt();
      }
    }
    
    public void output(DataOutputStream os) throws IOException
    {
      os.writeInt(fMagic);
      for (int i = 0; i < fFormatVersion.length; i++) {
        os.writeByte(fFormatVersion[i]);
      }
      os.writeInt(fLength);
      os.writeInt(fCFUKeys);
      os.writeInt(fCFUKeysSize);
      os.writeInt(fCFUStringIndex);
      os.writeInt(fCFUStringIndexSize);
      os.writeInt(fCFUStringTable);
      os.writeInt(fCFUStringTableLen);
      os.writeInt(fCFUStringLengths);
      os.writeInt(fCFUStringLengthsSize);
      os.writeInt(fAnyCaseTrie);
      os.writeInt(fAnyCaseTrieLength);
      os.writeInt(fLowerCaseTrie);
      os.writeInt(fLowerCaseTrieLength);
      os.writeInt(fScriptSets);
      os.writeInt(fScriptSetsLength);
      for (i = 0; i < unused.length; i++) {
        os.writeInt(unused[i]);
      }
    }
  }
  
  private static class SpoofData
  {
    SpoofChecker.SpoofDataHeader fRawData;
    int[] fCFUKeys;
    short[] fCFUValues;
    SpoofStringLengthsElement[] fCFUStringLengths;
    char[] fCFUStrings;
    Trie2 fAnyCaseTrie;
    Trie2 fLowerCaseTrie;
    SpoofChecker.ScriptSet[] fScriptSets;
    
    public static SpoofData getDefault() throws IOException
    {
      InputStream is = ICUData.getRequiredStream("data/icudt51b/confusables.cfu");
      
      SpoofData This = new SpoofData(is);
      return This;
    }
    





    public SpoofData()
    {
      fRawData = new SpoofChecker.SpoofDataHeader();
      
      fRawData.fMagic = 944111087;
      fRawData.fFormatVersion[0] = 1;
      fRawData.fFormatVersion[1] = 0;
      fRawData.fFormatVersion[2] = 0;
      fRawData.fFormatVersion[3] = 0;
    }
    


    public SpoofData(InputStream is)
      throws IOException
    {
      DataInputStream dis = new DataInputStream(new BufferedInputStream(is));
      dis.skip(128L);
      assert (dis.markSupported());
      dis.mark(Integer.MAX_VALUE);
      
      fRawData = new SpoofChecker.SpoofDataHeader(dis);
      initPtrs(dis);
    }
    

    static boolean validateDataVersion(SpoofChecker.SpoofDataHeader rawData)
    {
      if ((rawData == null) || (fMagic != 944111087) || (fFormatVersion[0] > 1) || (fFormatVersion[1] > 0))
      {
        return false;
      }
      return true;
    }
    










    void initPtrs(DataInputStream dis)
      throws IOException
    {
      fCFUKeys = null;
      fCFUValues = null;
      fCFUStringLengths = null;
      fCFUStrings = null;
      



      dis.reset();
      dis.skip(fRawData.fCFUKeys);
      if (fRawData.fCFUKeys != 0) {
        fCFUKeys = new int[fRawData.fCFUKeysSize];
        for (int i = 0; i < fRawData.fCFUKeysSize; i++) {
          fCFUKeys[i] = dis.readInt();
        }
      }
      
      dis.reset();
      dis.skip(fRawData.fCFUStringIndex);
      if (fRawData.fCFUStringIndex != 0) {
        fCFUValues = new short[fRawData.fCFUStringIndexSize];
        for (int i = 0; i < fRawData.fCFUStringIndexSize; i++) {
          fCFUValues[i] = dis.readShort();
        }
      }
      
      dis.reset();
      dis.skip(fRawData.fCFUStringTable);
      if (fRawData.fCFUStringTable != 0) {
        fCFUStrings = new char[fRawData.fCFUStringTableLen];
        for (int i = 0; i < fRawData.fCFUStringTableLen; i++) {
          fCFUStrings[i] = dis.readChar();
        }
      }
      
      dis.reset();
      dis.skip(fRawData.fCFUStringLengths);
      if (fRawData.fCFUStringLengths != 0) {
        fCFUStringLengths = new SpoofStringLengthsElement[fRawData.fCFUStringLengthsSize];
        for (int i = 0; i < fRawData.fCFUStringLengthsSize; i++) {
          fCFUStringLengths[i] = new SpoofStringLengthsElement(null);
          fCFUStringLengths[i].fLastString = dis.readShort();
          fCFUStringLengths[i].fStrLength = dis.readShort();
        }
      }
      
      dis.reset();
      dis.skip(fRawData.fAnyCaseTrie);
      if ((fAnyCaseTrie == null) && (fRawData.fAnyCaseTrie != 0)) {
        fAnyCaseTrie = Trie2.createFromSerialized(dis);
      }
      dis.reset();
      dis.skip(fRawData.fLowerCaseTrie);
      if ((fLowerCaseTrie == null) && (fRawData.fLowerCaseTrie != 0)) {
        fLowerCaseTrie = Trie2.createFromSerialized(dis);
      }
      
      dis.reset();
      dis.skip(fRawData.fScriptSets);
      if (fRawData.fScriptSets != 0) {
        fScriptSets = new SpoofChecker.ScriptSet[fRawData.fScriptSetsLength];
        for (int i = 0; i < fRawData.fScriptSetsLength; i++) {
          fScriptSets[i] = new SpoofChecker.ScriptSet(dis);
        }
      }
    }
    




    private static class SpoofStringLengthsElement
    {
      short fLastString;
      



      short fStrLength;
      



      private SpoofStringLengthsElement() {}
    }
  }
  



  private static class ScriptSet
  {
    public ScriptSet() {}
    



    public ScriptSet(DataInputStream dis)
      throws IOException
    {
      for (int j = 0; j < bits.length; j++) {
        bits[j] = dis.readInt();
      }
    }
    
    public void output(DataOutputStream os) throws IOException {
      for (int i = 0; i < bits.length; i++) {
        os.writeInt(bits[i]);
      }
    }
    
    public boolean equals(ScriptSet other) {
      for (int i = 0; i < bits.length; i++) {
        if (bits[i] != bits[i]) {
          return false;
        }
      }
      return true;
    }
    
    public void Union(int script) {
      int index = script / 32;
      int bit = 1 << (script & 0x1F);
      assert (index < bits.length * 4 * 4);
      bits[index] |= bit;
    }
    
    public void Union(ScriptSet other)
    {
      for (int i = 0; i < bits.length; i++) {
        bits[i] |= bits[i];
      }
    }
    
    public void intersect(ScriptSet other) {
      for (int i = 0; i < bits.length; i++) {
        bits[i] &= bits[i];
      }
    }
    
    public void intersect(int script) {
      int index = script / 32;
      int bit = 1 << (script & 0x1F);
      assert (index < bits.length * 4 * 4);
      
      for (int i = 0; i < index; i++) {
        bits[i] = 0;
      }
      bits[index] &= bit;
      for (i = index + 1; i < bits.length; i++) {
        bits[i] = 0;
      }
    }
    
    public void setAll() {
      for (int i = 0; i < bits.length; i++) {
        bits[i] = -1;
      }
    }
    
    public void resetAll()
    {
      for (int i = 0; i < bits.length; i++) {
        bits[i] = 0;
      }
    }
    

    public int countMembers()
    {
      int count = 0;
      for (int i = 0; i < bits.length; i++) {
        int x = bits[i];
        while (x != 0) {
          count++;
          x &= x - 1;
        }
      }
      

      return count;
    }
    
    private int[] bits = new int[6];
  }
}
