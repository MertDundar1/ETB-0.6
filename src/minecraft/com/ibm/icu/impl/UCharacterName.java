package com.ibm.icu.impl;

import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.text.UnicodeSet;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.MissingResourceException;




























public final class UCharacterName
{
  public static final UCharacterName INSTANCE;
  public static final int LINES_PER_GROUP_ = 32;
  
  static
  {
    try
    {
      INSTANCE = new UCharacterName();
    }
    catch (IOException e) {
      throw new MissingResourceException("Could not construct UCharacterName. Missing unames.icu", "", "");
    }
  }
  









  public int m_groupcount_ = 0;
  














  public String getName(int ch, int choice)
  {
    if ((ch < 0) || (ch > 1114111) || (choice > 4))
    {
      return null;
    }
    
    String result = null;
    
    result = getAlgName(ch, choice);
    

    if ((result == null) || (result.length() == 0)) {
      if (choice == 2) {
        result = getExtendedName(ch);
      } else {
        result = getGroupName(ch, choice);
      }
    }
    
    return result;
  }
  








  public int getCharFromName(int choice, String name)
  {
    if ((choice >= 4) || (name == null) || (name.length() == 0))
    {
      return -1;
    }
    

    int result = getExtendedChar(name.toLowerCase(Locale.ENGLISH), choice);
    if (result >= -1) {
      return result;
    }
    
    String upperCaseName = name.toUpperCase(Locale.ENGLISH);
    


    if ((choice == 0) || (choice == 2))
    {

      int count = 0;
      if (m_algorithm_ != null) {
        count = m_algorithm_.length;
      }
      for (count--; count >= 0; count--) {
        result = m_algorithm_[count].getChar(upperCaseName);
        if (result >= 0) {
          return result;
        }
      }
    }
    
    if (choice == 2) {
      result = getGroupChar(upperCaseName, 0);
      
      if (result == -1) {
        result = getGroupChar(upperCaseName, 3);
      }
    }
    else
    {
      result = getGroupChar(upperCaseName, choice);
    }
    return result;
  }
  


















  public int getGroupLengths(int index, char[] offsets, char[] lengths)
  {
    char length = 65535;
    byte b = 0;
    byte n = 0;
    
    index *= m_groupsize_;
    int stringoffset = UCharacterUtility.toInt(m_groupinfo_[(index + 1)], m_groupinfo_[(index + 2)]);
    


    offsets[0] = '\000';
    


    for (int i = 0; i < 32; stringoffset++) {
      b = m_groupstring_[stringoffset];
      int shift = 4;
      
      while (shift >= 0)
      {
        n = (byte)(b >> shift & 0xF);
        if ((length == 65535) && (n > 11)) {
          length = (char)(n - 12 << 4);
        }
        else {
          if (length != 65535) {
            lengths[i] = ((char)((length | n) + '\f'));
          }
          else {
            lengths[i] = ((char)n);
          }
          
          if (i < 32) {
            offsets[(i + 1)] = ((char)(offsets[i] + lengths[i]));
          }
          
          length = 65535;
          i++;
        }
        
        shift -= 4;
      }
    }
    return stringoffset;
  }
  














  public String getGroupName(int index, int length, int choice)
  {
    if ((choice != 0) && (choice != 2))
    {

      if ((59 >= m_tokentable_.length) || (m_tokentable_[59] == 65535))
      {



        int fieldIndex = choice == 4 ? 2 : choice;
        do {
          int oldindex = index;
          index += UCharacterUtility.skipByteSubString(m_groupstring_, index, length, (byte)59);
          
          length -= index - oldindex;
          fieldIndex--; } while (fieldIndex > 0);

      }
      else
      {

        length = 0;
      }
    }
    
    synchronized (m_utilStringBuffer_) {
      m_utilStringBuffer_.delete(0, m_utilStringBuffer_.length());
      

      for (int i = 0; i < length;) {
        byte b = m_groupstring_[(index + i)];
        i++;
        
        if (b >= m_tokentable_.length) {
          if (b == 59) {
            break;
          }
          m_utilStringBuffer_.append(b);
        }
        else {
          char token = m_tokentable_[(b & 0xFF)];
          if (token == 65534)
          {
            token = m_tokentable_[(b << 8 | m_groupstring_[(index + i)] & 0xFF)];
            
            i++;
          }
          if (token == 65535) {
            if (b == 59)
            {


              if ((m_utilStringBuffer_.length() != 0) || (choice != 2))
              {
                break;
              }
              
            }
            else {
              m_utilStringBuffer_.append((char)(b & 0xFF));
            }
          } else {
            UCharacterUtility.getNullTermByteSubString(m_utilStringBuffer_, m_tokenstring_, token);
          }
        }
      }
      

      if (m_utilStringBuffer_.length() > 0) {
        return m_utilStringBuffer_.toString();
      }
    }
    return null;
  }
  



  public String getExtendedName(int ch)
  {
    String result = getName(ch, 0);
    if (result == null)
    {
      if (result == null) {
        result = getExtendedOr10Name(ch);
      }
    }
    return result;
  }
  





  public int getGroup(int codepoint)
  {
    int endGroup = m_groupcount_;
    int msb = getCodepointMSB(codepoint);
    int result = 0;
    


    while (result < endGroup - 1) {
      int gindex = result + endGroup >> 1;
      if (msb < getGroupMSB(gindex)) {
        endGroup = gindex;
      }
      else {
        result = gindex;
      }
    }
    return result;
  }
  






  public String getExtendedOr10Name(int ch)
  {
    String result = null;
    
    if (result == null) {
      int type = getType(ch);
      

      if (type >= TYPE_NAMES_.length) {
        result = "unknown";
      }
      else {
        result = TYPE_NAMES_[type];
      }
      synchronized (m_utilStringBuffer_) {
        m_utilStringBuffer_.delete(0, m_utilStringBuffer_.length());
        m_utilStringBuffer_.append('<');
        m_utilStringBuffer_.append(result);
        m_utilStringBuffer_.append('-');
        String chStr = Integer.toHexString(ch).toUpperCase(Locale.ENGLISH);
        int zeros = 4 - chStr.length();
        while (zeros > 0) {
          m_utilStringBuffer_.append('0');
          zeros--;
        }
        m_utilStringBuffer_.append(chStr);
        m_utilStringBuffer_.append('>');
        result = m_utilStringBuffer_.toString();
      }
    }
    return result;
  }
  





  public int getGroupMSB(int gindex)
  {
    if (gindex >= m_groupcount_) {
      return -1;
    }
    return m_groupinfo_[(gindex * m_groupsize_)];
  }
  





  public static int getCodepointMSB(int codepoint)
  {
    return codepoint >> 5;
  }
  





  public static int getGroupLimit(int msb)
  {
    return (msb << 5) + 32;
  }
  





  public static int getGroupMin(int msb)
  {
    return msb << 5;
  }
  





  public static int getGroupOffset(int codepoint)
  {
    return codepoint & 0x1F;
  }
  






  public static int getGroupMinFromCodepoint(int codepoint)
  {
    return codepoint & 0xFFFFFFE0;
  }
  





  public int getAlgorithmLength()
  {
    return m_algorithm_.length;
  }
  





  public int getAlgorithmStart(int index)
  {
    return m_algorithm_[index].m_rangestart_;
  }
  





  public int getAlgorithmEnd(int index)
  {
    return m_algorithm_[index].m_rangeend_;
  }
  






  public String getAlgorithmName(int index, int codepoint)
  {
    String result = null;
    synchronized (m_utilStringBuffer_) {
      m_utilStringBuffer_.delete(0, m_utilStringBuffer_.length());
      m_algorithm_[index].appendName(codepoint, m_utilStringBuffer_);
      result = m_utilStringBuffer_.toString();
    }
    return result;
  }
  






  public synchronized String getGroupName(int ch, int choice)
  {
    int msb = getCodepointMSB(ch);
    int group = getGroup(ch);
    

    if (msb == m_groupinfo_[(group * m_groupsize_)]) {
      int index = getGroupLengths(group, m_groupoffsets_, m_grouplengths_);
      
      int offset = ch & 0x1F;
      return getGroupName(index + m_groupoffsets_[offset], m_grouplengths_[offset], choice);
    }
    

    return null;
  }
  







  public int getMaxCharNameLength()
  {
    if (initNameSetsLengths()) {
      return m_maxNameLength_;
    }
    
    return 0;
  }
  







  public int getMaxISOCommentLength()
  {
    if (initNameSetsLengths()) {
      return m_maxISOCommentLength_;
    }
    
    return 0;
  }
  







  public void getCharNameCharacters(UnicodeSet set)
  {
    convert(m_nameSet_, set);
  }
  






  public void getISOCommentCharacters(UnicodeSet set)
  {
    convert(m_ISOCommentSet_, set);
  }
  



  static final class AlgorithmName
  {
    static final int TYPE_0_ = 0;
    

    static final int TYPE_1_ = 1;
    

    private int m_rangestart_;
    

    private int m_rangeend_;
    

    private byte m_type_;
    

    private byte m_variant_;
    

    private char[] m_factor_;
    

    private String m_prefix_;
    

    private byte[] m_factorstring_;
    


    AlgorithmName() {}
    


    boolean setInfo(int rangestart, int rangeend, byte type, byte variant)
    {
      if ((rangestart >= 0) && (rangestart <= rangeend) && (rangeend <= 1114111) && ((type == 0) || (type == 1)))
      {

        m_rangestart_ = rangestart;
        m_rangeend_ = rangeend;
        m_type_ = type;
        m_variant_ = variant;
        return true;
      }
      return false;
    }
    





    boolean setFactor(char[] factor)
    {
      if (factor.length == m_variant_) {
        m_factor_ = factor;
        return true;
      }
      return false;
    }
    





    boolean setPrefix(String prefix)
    {
      if ((prefix != null) && (prefix.length() > 0)) {
        m_prefix_ = prefix;
        return true;
      }
      return false;
    }
    







    boolean setFactorString(byte[] string)
    {
      m_factorstring_ = string;
      return true;
    }
    




    boolean contains(int ch)
    {
      return (m_rangestart_ <= ch) && (ch <= m_rangeend_);
    }
    







    void appendName(int ch, StringBuffer str)
    {
      str.append(m_prefix_);
      switch (m_type_)
      {

      case 0: 
        str.append(Utility.hex(ch, m_variant_));
        break;
      
      case 1: 
        int offset = ch - m_rangestart_;
        int[] indexes = m_utilIntBuffer_;
        




        synchronized (m_utilIntBuffer_) {
          for (int i = m_variant_ - 1; i > 0; i--)
          {
            int factor = m_factor_[i] & 0xFF;
            indexes[i] = (offset % factor);
            offset /= factor;
          }
          



          indexes[0] = offset;
          

          str.append(getFactorString(indexes, m_variant_));
        }
      }
      
    }
    




    int getChar(String name)
    {
      int prefixlen = m_prefix_.length();
      if ((name.length() < prefixlen) || (!m_prefix_.equals(name.substring(0, prefixlen))))
      {
        return -1;
      }
      
      switch (m_type_)
      {
      case 0: 
        try
        {
          int result = Integer.parseInt(name.substring(prefixlen), 16);
          

          if ((m_rangestart_ <= result) && (result <= m_rangeend_)) {
            return result;
          }
        }
        catch (NumberFormatException e)
        {
          return -1;
        }
      


      case 1: 
        for (int ch = m_rangestart_; ch <= m_rangeend_; ch++)
        {
          int offset = ch - m_rangestart_;
          int[] indexes = m_utilIntBuffer_;
          




          synchronized (m_utilIntBuffer_) {
            for (int i = m_variant_ - 1; i > 0; i--)
            {
              int factor = m_factor_[i] & 0xFF;
              indexes[i] = (offset % factor);
              offset /= factor;
            }
            



            indexes[0] = offset;
            

            if (compareFactorString(indexes, m_variant_, name, prefixlen))
            {
              return ch;
            }
          }
        }
      }
      
      return -1;
    }
    









    int add(int[] set, int maxlength)
    {
      int length = UCharacterName.add(set, m_prefix_);
      switch (m_type_)
      {

      case 0: 
        length += m_variant_;
        


        break;
      



      case 1: 
        for (int i = m_variant_ - 1; i > 0; i--)
        {
          int maxfactorlength = 0;
          int count = 0;
          for (int factor = m_factor_[i]; factor > 0; factor--) {
            synchronized (m_utilStringBuffer_) {
              m_utilStringBuffer_.delete(0, m_utilStringBuffer_.length());
              
              count = UCharacterUtility.getNullTermByteSubString(m_utilStringBuffer_, m_factorstring_, count);
              


              UCharacterName.add(set, m_utilStringBuffer_);
              if (m_utilStringBuffer_.length() > maxfactorlength)
              {

                maxfactorlength = m_utilStringBuffer_.length();
              }
            }
          }
          
          length += maxfactorlength;
        }
      }
      
      if (length > maxlength) {
        return length;
      }
      return maxlength;
    }
    















    private StringBuffer m_utilStringBuffer_ = new StringBuffer();
    


    private int[] m_utilIntBuffer_ = new int['Ā'];
    









    private String getFactorString(int[] index, int length)
    {
      int size = m_factor_.length;
      if ((index == null) || (length != size)) {
        return null;
      }
      
      synchronized (m_utilStringBuffer_) {
        m_utilStringBuffer_.delete(0, m_utilStringBuffer_.length());
        int count = 0;
        
        size--;
        for (int i = 0; i <= size; i++) {
          int factor = m_factor_[i];
          count = UCharacterUtility.skipNullTermByteSubString(m_factorstring_, count, index[i]);
          
          count = UCharacterUtility.getNullTermByteSubString(m_utilStringBuffer_, m_factorstring_, count);
          

          if (i != size) {
            count = UCharacterUtility.skipNullTermByteSubString(m_factorstring_, count, factor - index[i] - 1);
          }
        }
        

        return m_utilStringBuffer_.toString();
      }
    }
    










    private boolean compareFactorString(int[] index, int length, String str, int offset)
    {
      int size = m_factor_.length;
      if ((index == null) || (length != size)) {
        return false;
      }
      int count = 0;
      int strcount = offset;
      
      size--;
      for (int i = 0; i <= size; i++)
      {
        int factor = m_factor_[i];
        count = UCharacterUtility.skipNullTermByteSubString(m_factorstring_, count, index[i]);
        
        strcount = UCharacterUtility.compareNullTermByteSubString(str, m_factorstring_, strcount, count);
        
        if (strcount < 0) {
          return false;
        }
        
        if (i != size) {
          count = UCharacterUtility.skipNullTermByteSubString(m_factorstring_, count, factor - index[i]);
        }
      }
      
      if (strcount != str.length()) {
        return false;
      }
      return true;
    }
  }
  





  int m_groupsize_ = 0;
  
  private char[] m_tokentable_;
  
  private byte[] m_tokenstring_;
  
  private char[] m_groupinfo_;
  private byte[] m_groupstring_;
  private AlgorithmName[] m_algorithm_;
  
  boolean setToken(char[] token, byte[] tokenstring)
  {
    if ((token != null) && (tokenstring != null) && (token.length > 0) && (tokenstring.length > 0))
    {
      m_tokentable_ = token;
      m_tokenstring_ = tokenstring;
      return true;
    }
    return false;
  }
  





  boolean setAlgorithm(AlgorithmName[] alg)
  {
    if ((alg != null) && (alg.length != 0)) {
      m_algorithm_ = alg;
      return true;
    }
    return false;
  }
  






  boolean setGroupCountSize(int count, int size)
  {
    if ((count <= 0) || (size <= 0)) {
      return false;
    }
    m_groupcount_ = count;
    m_groupsize_ = size;
    return true;
  }
  






  boolean setGroup(char[] group, byte[] groupstring)
  {
    if ((group != null) && (groupstring != null) && (group.length > 0) && (groupstring.length > 0))
    {
      m_groupinfo_ = group;
      m_groupstring_ = groupstring;
      return true;
    }
    return false;
  }
  














  private char[] m_groupoffsets_ = new char[33];
  private char[] m_grouplengths_ = new char[33];
  




  private static final String NAME_FILE_NAME_ = "data/icudt51b/unames.icu";
  




  private static final int GROUP_SHIFT_ = 5;
  




  private static final int GROUP_MASK_ = 31;
  




  private static final int NAME_BUFFER_SIZE_ = 100000;
  




  private static final int OFFSET_HIGH_OFFSET_ = 1;
  




  private static final int OFFSET_LOW_OFFSET_ = 2;
  




  private static final int SINGLE_NIBBLE_MAX_ = 11;
  



  private int[] m_nameSet_ = new int[8];
  



  private int[] m_ISOCommentSet_ = new int[8];
  


  private StringBuffer m_utilStringBuffer_ = new StringBuffer();
  


  private int[] m_utilIntBuffer_ = new int[2];
  


  private int m_maxISOCommentLength_;
  


  private int m_maxNameLength_;
  


  private static final String[] TYPE_NAMES_ = { "unassigned", "uppercase letter", "lowercase letter", "titlecase letter", "modifier letter", "other letter", "non spacing mark", "enclosing mark", "combining spacing mark", "decimal digit number", "letter number", "other number", "space separator", "line separator", "paragraph separator", "control", "format", "private use area", "surrogate", "dash punctuation", "start punctuation", "end punctuation", "connector punctuation", "other punctuation", "math symbol", "currency symbol", "modifier symbol", "other symbol", "initial punctuation", "final punctuation", "noncharacter", "lead surrogate", "trail surrogate" };
  









  private static final String UNKNOWN_TYPE_NAME_ = "unknown";
  









  private static final int NON_CHARACTER_ = 30;
  








  private static final int LEAD_SURROGATE_ = 31;
  








  private static final int TRAIL_SURROGATE_ = 32;
  








  static final int EXTENDED_CATEGORY_ = 33;
  









  private UCharacterName()
    throws IOException
  {
    InputStream is = ICUData.getRequiredStream("data/icudt51b/unames.icu");
    BufferedInputStream b = new BufferedInputStream(is, 100000);
    UCharacterNameReader reader = new UCharacterNameReader(b);
    reader.read(this);
    b.close();
  }
  









  private String getAlgName(int ch, int choice)
  {
    if ((choice == 0) || (choice == 2))
    {


      synchronized (m_utilStringBuffer_) {
        m_utilStringBuffer_.delete(0, m_utilStringBuffer_.length());
        
        for (int index = m_algorithm_.length - 1; index >= 0; index--)
        {
          if (m_algorithm_[index].contains(ch)) {
            m_algorithm_[index].appendName(ch, m_utilStringBuffer_);
            return m_utilStringBuffer_.toString();
          }
        }
      }
    }
    return null;
  }
  






  private synchronized int getGroupChar(String name, int choice)
  {
    for (int i = 0; i < m_groupcount_; i++)
    {

      int startgpstrindex = getGroupLengths(i, m_groupoffsets_, m_grouplengths_);
      


      int result = getGroupChar(startgpstrindex, m_grouplengths_, name, choice);
      
      if (result != -1) {
        return m_groupinfo_[(i * m_groupsize_)] << '\005' | result;
      }
    }
    
    return -1;
  }
  











  private int getGroupChar(int index, char[] length, String name, int choice)
  {
    byte b = 0;
    

    int namelen = name.length();
    


    for (int result = 0; result <= 32; result++) {
      int nindex = 0;
      int len = length[result];
      
      if ((choice != 0) && (choice != 2))
      {





        int fieldIndex = choice == 4 ? 2 : choice;
        do {
          int oldindex = index;
          index += UCharacterUtility.skipByteSubString(m_groupstring_, index, len, (byte)59);
          
          len -= index - oldindex;
          fieldIndex--; } while (fieldIndex > 0);
      }
      


      for (int count = 0; (count < len) && (nindex != -1) && (nindex < namelen);)
      {
        b = m_groupstring_[(index + count)];
        count++;
        
        if (b >= m_tokentable_.length) {
          if (name.charAt(nindex++) != (b & 0xFF)) {
            nindex = -1;
          }
        }
        else {
          char token = m_tokentable_[(b & 0xFF)];
          if (token == 65534)
          {
            token = m_tokentable_[(b << 8 | m_groupstring_[(index + count)] & 0xFF)];
            
            count++;
          }
          if (token == 65535) {
            if (name.charAt(nindex++) != (b & 0xFF)) {
              nindex = -1;
            }
            
          }
          else {
            nindex = UCharacterUtility.compareNullTermByteSubString(name, m_tokenstring_, nindex, token);
          }
        }
      }
      

      if ((namelen == nindex) && ((count == len) || (m_groupstring_[(index + count)] == 59)))
      {
        return result;
      }
      
      index += len;
    }
    return -1;
  }
  





  private static int getType(int ch)
  {
    if (UCharacterUtility.isNonCharacter(ch))
    {
      return 30;
    }
    int result = UCharacter.getType(ch);
    if (result == 18) {
      if (ch <= 56319) {
        result = 31;
      }
      else {
        result = 32;
      }
    }
    return result;
  }
  







  private static int getExtendedChar(String name, int choice)
  {
    if (name.charAt(0) == '<') {
      if (choice == 2) {
        int endIndex = name.length() - 1;
        if (name.charAt(endIndex) == '>') {
          int startIndex = name.lastIndexOf('-');
          if (startIndex >= 0) {
            startIndex++;
            int result = -1;
            try {
              result = Integer.parseInt(name.substring(startIndex, endIndex), 16);

            }
            catch (NumberFormatException e)
            {
              return -1;
            }
            

            String type = name.substring(1, startIndex - 1);
            int length = TYPE_NAMES_.length;
            for (int i = 0; i < length; i++) {
              if (type.compareTo(TYPE_NAMES_[i]) == 0) {
                if (getType(result) != i) break;
                return result;
              }
            }
          }
        }
      }
      

      return -1;
    }
    return -2;
  }
  








  private static void add(int[] set, char ch)
  {
    set[(ch >>> '\005')] |= '\001' << (ch & 0x1F);
  }
  







  private static boolean contains(int[] set, char ch)
  {
    return (set[(ch >>> '\005')] & '\001' << (ch & 0x1F)) != 0;
  }
  






  private static int add(int[] set, String str)
  {
    int result = str.length();
    
    for (int i = result - 1; i >= 0; i--) {
      add(set, str.charAt(i));
    }
    return result;
  }
  






  private static int add(int[] set, StringBuffer str)
  {
    int result = str.length();
    
    for (int i = result - 1; i >= 0; i--) {
      add(set, str.charAt(i));
    }
    return result;
  }
  







  private int addAlgorithmName(int maxlength)
  {
    int result = 0;
    for (int i = m_algorithm_.length - 1; i >= 0; i--) {
      result = m_algorithm_[i].add(m_nameSet_, maxlength);
      if (result > maxlength) {
        maxlength = result;
      }
    }
    return maxlength;
  }
  






  private int addExtendedName(int maxlength)
  {
    for (int i = TYPE_NAMES_.length - 1; i >= 0; i--)
    {




      int length = 9 + add(m_nameSet_, TYPE_NAMES_[i]);
      if (length > maxlength) {
        maxlength = length;
      }
    }
    return maxlength;
  }
  











  private int[] addGroupName(int offset, int length, byte[] tokenlength, int[] set)
  {
    int resultnlength = 0;
    int resultplength = 0;
    while (resultplength < length) {
      char b = (char)(m_groupstring_[(offset + resultplength)] & 0xFF);
      resultplength++;
      if (b == ';') {
        break;
      }
      
      if (b >= m_tokentable_.length) {
        add(set, b);
        resultnlength++;
      }
      else {
        char token = m_tokentable_[(b & 0xFF)];
        if (token == 65534)
        {
          b = (char)(b << '\b' | m_groupstring_[(offset + resultplength)] & 0xFF);
          
          token = m_tokentable_[b];
          resultplength++;
        }
        if (token == 65535) {
          add(set, b);
          resultnlength++;

        }
        else
        {
          byte tlength = tokenlength[b];
          if (tlength == 0) {
            synchronized (m_utilStringBuffer_) {
              m_utilStringBuffer_.delete(0, m_utilStringBuffer_.length());
              
              UCharacterUtility.getNullTermByteSubString(m_utilStringBuffer_, m_tokenstring_, token);
              

              tlength = (byte)add(set, m_utilStringBuffer_);
            }
            tokenlength[b] = tlength;
          }
          resultnlength += tlength;
        }
      }
    }
    m_utilIntBuffer_[0] = resultnlength;
    m_utilIntBuffer_[1] = resultplength;
    return m_utilIntBuffer_;
  }
  







  private void addGroupName(int maxlength)
  {
    int maxisolength = 0;
    char[] offsets = new char[34];
    char[] lengths = new char[34];
    byte[] tokenlengths = new byte[m_tokentable_.length];
    


    for (int i = 0; i < m_groupcount_; i++) {
      int offset = getGroupLengths(i, offsets, lengths);
      


      for (int linenumber = 0; linenumber < 32; 
          linenumber++) {
        int lineoffset = offset + offsets[linenumber];
        int length = lengths[linenumber];
        if (length != 0)
        {



          int[] parsed = addGroupName(lineoffset, length, tokenlengths, m_nameSet_);
          
          if (parsed[0] > maxlength)
          {
            maxlength = parsed[0];
          }
          lineoffset += parsed[1];
          if (parsed[1] < length)
          {


            length -= parsed[1];
            
            parsed = addGroupName(lineoffset, length, tokenlengths, m_nameSet_);
            
            if (parsed[0] > maxlength)
            {
              maxlength = parsed[0];
            }
            lineoffset += parsed[1];
            if (parsed[1] < length)
            {


              length -= parsed[1];
              
              parsed = addGroupName(lineoffset, length, tokenlengths, m_ISOCommentSet_);
              
              if (parsed[1] > maxisolength)
                maxisolength = length;
            }
          }
        }
      }
    }
    m_maxISOCommentLength_ = maxisolength;
    m_maxNameLength_ = maxlength;
  }
  




  private boolean initNameSetsLengths()
  {
    if (m_maxNameLength_ > 0) {
      return true;
    }
    
    String extra = "0123456789ABCDEF<>-";
    

    for (int i = extra.length() - 1; i >= 0; i--) {
      add(m_nameSet_, extra.charAt(i));
    }
    

    m_maxNameLength_ = addAlgorithmName(0);
    
    m_maxNameLength_ = addExtendedName(m_maxNameLength_);
    
    addGroupName(m_maxNameLength_);
    return true;
  }
  






  private void convert(int[] set, UnicodeSet uset)
  {
    uset.clear();
    if (!initNameSetsLengths()) {
      return;
    }
    

    for (char c = 'ÿ'; c > 0; c = (char)(c - '\001')) {
      if (contains(set, c)) {
        uset.add(c);
      }
    }
  }
}
