package com.ibm.icu.impl;

import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.VersionInfo;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;





































































































































































































public final class ICUResourceBundleReader
  implements ICUBinary.Authenticate
{
  private static final byte[] DATA_FORMAT_ID = { 82, 101, 115, 66 };
  

  private static final int URES_INDEX_LENGTH = 0;
  

  private static final int URES_INDEX_KEYS_TOP = 1;
  

  private static final int URES_INDEX_BUNDLE_TOP = 3;
  

  private static final int URES_INDEX_ATTRIBUTES = 5;
  

  private static final int URES_INDEX_16BIT_TOP = 6;
  

  private static final int URES_INDEX_POOL_CHECKSUM = 7;
  

  private static final int URES_ATT_NO_FALLBACK = 1;
  

  private static final int URES_ATT_IS_POOL_BUNDLE = 2;
  

  private static final int URES_ATT_USES_POOL_BUNDLE = 4;
  

  private static final boolean DEBUG = false;
  

  private byte[] dataVersion;
  

  private String s16BitUnits;
  

  private byte[] poolBundleKeys;
  

  private String poolBundleKeysAsString;
  

  private int rootRes;
  
  private int localKeyLimit;
  
  private boolean noFallback;
  
  private boolean isPoolBundle;
  
  private boolean usesPoolBundle;
  
  private int[] indexes;
  
  private byte[] keyStrings;
  
  private String keyStringsAsString;
  
  private byte[] resourceBytes;
  
  private int resourceBottom;
  
  private static ReaderCache CACHE = new ReaderCache(null);
  private static final ICUResourceBundleReader NULL_READER = new ICUResourceBundleReader();
  private ICUResourceBundleReader() {}
  
  private static class ReaderInfo {
    final String baseName;
    final String localeID;
    final ClassLoader loader;
    
    ReaderInfo(String baseName, String localeID, ClassLoader loader) { this.baseName = (baseName == null ? "" : baseName);
      this.localeID = (localeID == null ? "" : localeID);
      this.loader = loader;
    }
    
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (!(obj instanceof ReaderInfo)) {
        return false;
      }
      ReaderInfo info = (ReaderInfo)obj;
      return (baseName.equals(baseName)) && (localeID.equals(localeID)) && (loader.equals(loader));
    }
    

    public int hashCode()
    {
      return baseName.hashCode() ^ localeID.hashCode() ^ loader.hashCode();
    }
  }
  
  private static class ReaderCache extends SoftCache<ICUResourceBundleReader.ReaderInfo, ICUResourceBundleReader, ICUResourceBundleReader.ReaderInfo>
  {
    private ReaderCache() {}
    
    protected ICUResourceBundleReader createInstance(ICUResourceBundleReader.ReaderInfo key, ICUResourceBundleReader.ReaderInfo data)
    {
      String fullName = ICUResourceBundleReader.getFullName(baseName, localeID);
      InputStream stream = ICUData.getStream(loader, fullName);
      if (stream == null) {
        return ICUResourceBundleReader.NULL_READER;
      }
      return new ICUResourceBundleReader(stream, baseName, localeID, loader, null);
    }
  }
  





  private ICUResourceBundleReader(InputStream stream, String baseName, String localeID, ClassLoader loader)
  {
    BufferedInputStream bs = new BufferedInputStream(stream);
    




    try
    {
      dataVersion = ICUBinary.readHeader(bs, DATA_FORMAT_ID, this);
      


      readData(bs);
      stream.close();
    }
    catch (IOException ex) {
      String fullName = getFullName(baseName, localeID);
      throw new RuntimeException("Data file " + fullName + " is corrupt - " + ex.getMessage());
    }
    

    if (usesPoolBundle) {
      ICUResourceBundleReader poolBundleReader = getReader(baseName, "pool", loader);
      if (!isPoolBundle) {
        throw new IllegalStateException("pool.res is not a pool bundle");
      }
      if (indexes[7] != indexes[7]) {
        throw new IllegalStateException("pool.res has a different checksum than this bundle");
      }
      poolBundleKeys = keyStrings;
      poolBundleKeysAsString = keyStringsAsString;
    }
  }
  
  static ICUResourceBundleReader getReader(String baseName, String localeID, ClassLoader root) {
    ReaderInfo info = new ReaderInfo(baseName, localeID, root);
    ICUResourceBundleReader reader = (ICUResourceBundleReader)CACHE.getInstance(info, info);
    if (reader == NULL_READER) {
      return null;
    }
    return reader;
  }
  
  private void readData(InputStream stream) throws IOException
  {
    DataInputStream ds = new DataInputStream(stream);
    



    rootRes = ds.readInt();
    

    int indexes0 = ds.readInt();
    int indexLength = indexes0 & 0xFF;
    indexes = new int[indexLength];
    indexes[0] = indexes0;
    for (int i = 1; i < indexLength; i++) {
      indexes[i] = ds.readInt();
    }
    resourceBottom = (1 + indexLength << 2);
    
    if (indexLength > 5)
    {

      int att = indexes[5];
      noFallback = ((att & 0x1) != 0);
      isPoolBundle = ((att & 0x2) != 0);
      usesPoolBundle = ((att & 0x4) != 0);
    }
    
    int length = indexes[3] * 4;
    




    if (indexes[1] > 1 + indexLength) {
      int keysBottom = 1 + indexLength << 2;
      int keysTop = indexes[1] << 2;
      resourceBottom = keysTop;
      if (isPoolBundle)
      {



        keysTop -= keysBottom;
        keysBottom = 0;
      } else {
        localKeyLimit = keysTop;
      }
      keyStrings = new byte[keysTop];
      ds.readFully(keyStrings, keysBottom, keysTop - keysBottom);
      if (isPoolBundle)
      {
        while ((keysBottom < keysTop) && (keyStrings[(keysTop - 1)] == -86)) {
          keyStrings[(--keysTop)] = 0;
        }
        keyStringsAsString = new String(keyStrings, "US-ASCII");
      }
    }
    




    if ((indexLength > 6) && (indexes[6] > indexes[1]))
    {

      int num16BitUnits = (indexes[6] - indexes[1]) * 2;
      
      char[] c16BitUnits = new char[num16BitUnits];
      


      byte[] c16BitUnitsBytes = new byte[num16BitUnits * 2];
      ds.readFully(c16BitUnitsBytes);
      for (int i = 0; i < num16BitUnits; i++) {
        c16BitUnits[i] = ((char)(c16BitUnitsBytes[(i * 2)] << 8 | c16BitUnitsBytes[(i * 2 + 1)] & 0xFF));
      }
      s16BitUnits = new String(c16BitUnits);
      resourceBottom = (indexes[6] << 2);
    } else {
      s16BitUnits = "\000";
    }
    

    resourceBytes = new byte[length - resourceBottom];
    ds.readFully(resourceBytes);
  }
  
  VersionInfo getVersion() {
    return VersionInfo.getInstance(dataVersion[0], dataVersion[1], dataVersion[2], dataVersion[3]);
  }
  

  public boolean isDataVersionAcceptable(byte[] version)
  {
    return ((version[0] == 1) && (version[1] >= 1)) || (version[0] == 2);
  }
  
  int getRootResource() {
    return rootRes;
  }
  
  boolean getNoFallback() { return noFallback; }
  
  boolean getUsesPoolBundle() {
    return usesPoolBundle;
  }
  
  static int RES_GET_TYPE(int res) {
    return res >>> 28;
  }
  
  private static int RES_GET_OFFSET(int res) { return res & 0xFFFFFFF; }
  
  private int getResourceByteOffset(int offset) {
    return (offset << 2) - resourceBottom;
  }
  
  static int RES_GET_INT(int res) {
    return res << 4 >> 4;
  }
  
  static int RES_GET_UINT(int res) { return res & 0xFFFFFFF; }
  
  static boolean URES_IS_TABLE(int type) {
    return (type == 2) || (type == 5) || (type == 4);
  }
  
  private static byte[] emptyBytes = new byte[0];
  private static ByteBuffer emptyByteBuffer = ByteBuffer.allocate(0).asReadOnlyBuffer();
  private static char[] emptyChars = new char[0];
  private static int[] emptyInts = new int[0];
  private static String emptyString = "";
  private static final String ICU_RESOURCE_SUFFIX = ".res";
  
  private char getChar(int offset) { return (char)(resourceBytes[offset] << 8 | resourceBytes[(offset + 1)] & 0xFF); }
  
  private char[] getChars(int offset, int count) {
    char[] chars = new char[count];
    for (int i = 0; i < count; i++) {
      chars[i] = ((char)(resourceBytes[offset] << 8 | resourceBytes[(offset + 1)] & 0xFF));offset += 2;
    }
    return chars;
  }
  
  private int getInt(int offset) { return resourceBytes[offset] << 24 | (resourceBytes[(offset + 1)] & 0xFF) << 16 | (resourceBytes[(offset + 2)] & 0xFF) << 8 | resourceBytes[(offset + 3)] & 0xFF; }
  


  private int[] getInts(int offset, int count)
  {
    int[] ints = new int[count];
    for (int i = 0; i < count; i++) {
      ints[i] = (resourceBytes[offset] << 24 | (resourceBytes[(offset + 1)] & 0xFF) << 16 | (resourceBytes[(offset + 2)] & 0xFF) << 8 | resourceBytes[(offset + 3)] & 0xFF);offset += 4;
    }
    


    return ints;
  }
  
  private char[] getTable16KeyOffsets(int offset) { int length = s16BitUnits.charAt(offset++);
    if (length > 0) {
      return s16BitUnits.substring(offset, offset + length).toCharArray();
    }
    return emptyChars;
  }
  
  private char[] getTableKeyOffsets(int offset) {
    int length = getChar(offset);
    if (length > 0) {
      return getChars(offset + 2, length);
    }
    return emptyChars;
  }
  
  private int[] getTable32KeyOffsets(int offset) {
    int length = getInt(offset);
    if (length > 0) {
      return getInts(offset + 4, length);
    }
    return emptyInts;
  }
  
  private static final class ByteSequence
  {
    private byte[] bytes;
    private int offset;
    
    public ByteSequence(byte[] bytes, int offset) {
      this.bytes = bytes;
      this.offset = offset;
    }
    
    public byte charAt(int index) { return bytes[(offset + index)]; }
  }
  
  private String makeKeyStringFromBytes(int keyOffset) {
    StringBuilder sb = new StringBuilder();
    byte b;
    while ((b = keyStrings[(keyOffset++)]) != 0) {
      sb.append((char)b);
    }
    return sb.toString();
  }
  
  private String makeKeyStringFromString(int keyOffset) { int endOffset = keyOffset;
    while (poolBundleKeysAsString.charAt(endOffset) != 0) {
      endOffset++;
    }
    return poolBundleKeysAsString.substring(keyOffset, endOffset);
  }
  
  private ByteSequence RES_GET_KEY16(char keyOffset) { if (keyOffset < localKeyLimit) {
      return new ByteSequence(keyStrings, keyOffset);
    }
    return new ByteSequence(poolBundleKeys, keyOffset - localKeyLimit);
  }
  
  private String getKey16String(int keyOffset) {
    if (keyOffset < localKeyLimit) {
      return makeKeyStringFromBytes(keyOffset);
    }
    return makeKeyStringFromString(keyOffset - localKeyLimit);
  }
  
  private ByteSequence RES_GET_KEY32(int keyOffset) {
    if (keyOffset >= 0) {
      return new ByteSequence(keyStrings, keyOffset);
    }
    return new ByteSequence(poolBundleKeys, keyOffset & 0x7FFFFFFF);
  }
  
  private String getKey32String(int keyOffset) {
    if (keyOffset >= 0) {
      return makeKeyStringFromBytes(keyOffset);
    }
    return makeKeyStringFromString(keyOffset & 0x7FFFFFFF);
  }
  


  private static int compareKeys(CharSequence key, ByteSequence tableKey)
  {
    for (int i = 0; i < key.length(); i++) {
      int c2 = tableKey.charAt(i);
      if (c2 == 0) {
        return 1;
      }
      int diff = key.charAt(i) - c2;
      if (diff != 0) {
        return diff;
      }
    }
    return -tableKey.charAt(i);
  }
  
  private int compareKeys(CharSequence key, char keyOffset) { return compareKeys(key, RES_GET_KEY16(keyOffset)); }
  
  private int compareKeys32(CharSequence key, int keyOffset) {
    return compareKeys(key, RES_GET_KEY32(keyOffset));
  }
  
  String getString(int res) {
    int offset = RES_GET_OFFSET(res);
    
    if (RES_GET_TYPE(res) == 6) {
      int first = s16BitUnits.charAt(offset);
      if ((first & 0xFC00) != 56320) {
        if (first == 0) {
          return emptyString;
        }
        
        for (int endOffset = offset + 1; s16BitUnits.charAt(endOffset) != 0; endOffset++) {}
        return s16BitUnits.substring(offset, endOffset); }
      int length; if (first < 57327) {
        int length = first & 0x3FF;
        offset++;
      } else if (first < 57343) {
        int length = first - 57327 << 16 | s16BitUnits.charAt(offset + 1);
        offset += 2;
      } else {
        length = s16BitUnits.charAt(offset + 1) << '\020' | s16BitUnits.charAt(offset + 2);
        offset += 3;
      }
      return s16BitUnits.substring(offset, offset + length); }
    if (res == offset) {
      if (res == 0) {
        return emptyString;
      }
      offset = getResourceByteOffset(offset);
      int length = getInt(offset);
      return new String(getChars(offset + 4, length));
    }
    
    return null;
  }
  
  String getAlias(int res)
  {
    int offset = RES_GET_OFFSET(res);
    
    if (RES_GET_TYPE(res) == 3) {
      if (offset == 0) {
        return emptyString;
      }
      offset = getResourceByteOffset(offset);
      int length = getInt(offset);
      return new String(getChars(offset + 4, length));
    }
    
    return null;
  }
  
  byte[] getBinary(int res, byte[] ba)
  {
    int offset = RES_GET_OFFSET(res);
    
    if (RES_GET_TYPE(res) == 1) {
      if (offset == 0) {
        return emptyBytes;
      }
      offset = getResourceByteOffset(offset);
      int length = getInt(offset);
      if ((ba == null) || (ba.length != length)) {
        ba = new byte[length];
      }
      System.arraycopy(resourceBytes, offset + 4, ba, 0, length);
      return ba;
    }
    
    return null;
  }
  
  ByteBuffer getBinary(int res)
  {
    int offset = RES_GET_OFFSET(res);
    
    if (RES_GET_TYPE(res) == 1) {
      if (offset == 0)
      {


        return emptyByteBuffer.duplicate();
      }
      offset = getResourceByteOffset(offset);
      int length = getInt(offset);
      return ByteBuffer.wrap(resourceBytes, offset + 4, length).slice().asReadOnlyBuffer();
    }
    
    return null;
  }
  
  int[] getIntVector(int res)
  {
    int offset = RES_GET_OFFSET(res);
    
    if (RES_GET_TYPE(res) == 14) {
      if (offset == 0) {
        return emptyInts;
      }
      offset = getResourceByteOffset(offset);
      int length = getInt(offset);
      return getInts(offset + 4, length);
    }
    
    return null;
  }
  
  Container getArray(int res)
  {
    int type = RES_GET_TYPE(res);
    int offset = RES_GET_OFFSET(res);
    switch (type) {
    case 8: 
    case 9: 
      if (offset == 0) {
        return new Container(this);
      }
      break;
    default: 
      return null;
    }
    switch (type) {
    case 8: 
      return new Array(this, offset);
    case 9: 
      return new Array16(this, offset);
    }
    return null;
  }
  
  Table getTable(int res)
  {
    int type = RES_GET_TYPE(res);
    int offset = RES_GET_OFFSET(res);
    switch (type) {
    case 2: 
    case 4: 
    case 5: 
      if (offset == 0) {
        return new Table(this);
      }
      break;
    case 3: default: 
      return null;
    }
    switch (type) {
    case 2: 
      return new Table1632(this, offset);
    case 5: 
      return new Table16(this, offset);
    case 4: 
      return new Table32(this, offset);
    }
    return null;
  }
  

  static class Container
  {
    protected ICUResourceBundleReader reader;
    protected int size;
    protected int itemsOffset;
    
    int getSize()
    {
      return size;
    }
    
    int getContainerResource(int index) { return -1; }
    
    protected int getContainer16Resource(int index) {
      if ((index < 0) || (size <= index)) {
        return -1;
      }
      return 0x60000000 | reader.s16BitUnits.charAt(itemsOffset + index);
    }
    
    protected int getContainer32Resource(int index) {
      if ((index < 0) || (size <= index)) {
        return -1;
      }
      return reader.getInt(itemsOffset + 4 * index);
    }
    
    Container(ICUResourceBundleReader reader) { this.reader = reader; }
  }
  
  private static final class Array
    extends ICUResourceBundleReader.Container {
    int getContainerResource(int index) { return getContainer32Resource(index); }
    
    Array(ICUResourceBundleReader reader, int offset) {
      super();
      offset = reader.getResourceByteOffset(offset);
      size = reader.getInt(offset);
      itemsOffset = (offset + 4);
    }
  }
  
  private static final class Array16 extends ICUResourceBundleReader.Container {
    int getContainerResource(int index) { return getContainer16Resource(index); }
    
    Array16(ICUResourceBundleReader reader, int offset) {
      super();
      size = s16BitUnits.charAt(offset);
      itemsOffset = (offset + 1);
    }
  }
  
  static class Table extends ICUResourceBundleReader.Container { protected char[] keyOffsets;
    protected int[] key32Offsets;
    private static final int URESDATA_ITEM_NOT_FOUND = -1;
    
    String getKey(int index) { if ((index < 0) || (size <= index)) {
        return null;
      }
      return keyOffsets != null ? reader.getKey16String(keyOffsets[index]) : reader.getKey32String(key32Offsets[index]);
    }
    





    int findTableItem(CharSequence key)
    {
      int start = 0;
      int limit = size;
      while (start < limit) {
        int mid = start + limit >>> 1;
        int result; int result; if (keyOffsets != null) {
          result = reader.compareKeys(key, keyOffsets[mid]);
        } else {
          result = reader.compareKeys32(key, key32Offsets[mid]);
        }
        if (result < 0) {
          limit = mid;
        } else if (result > 0) {
          start = mid + 1;
        }
        else {
          return mid;
        }
      }
      return -1;
    }
    
    int getTableResource(String resKey) { return getContainerResource(findTableItem(resKey)); }
    
    Table(ICUResourceBundleReader reader) {
      super();
    }
  }
  
  private static final class Table1632 extends ICUResourceBundleReader.Table {
    int getContainerResource(int index) { return getContainer32Resource(index); }
    
    Table1632(ICUResourceBundleReader reader, int offset) {
      super();
      offset = reader.getResourceByteOffset(offset);
      keyOffsets = reader.getTableKeyOffsets(offset);
      size = keyOffsets.length;
      itemsOffset = (offset + 2 * (size + 2 & 0xFFFFFFFE));
    }
  }
  
  private static final class Table16 extends ICUResourceBundleReader.Table {
    int getContainerResource(int index) { return getContainer16Resource(index); }
    
    Table16(ICUResourceBundleReader reader, int offset) {
      super();
      keyOffsets = reader.getTable16KeyOffsets(offset);
      size = keyOffsets.length;
      itemsOffset = (offset + 1 + size);
    }
  }
  
  private static final class Table32 extends ICUResourceBundleReader.Table {
    int getContainerResource(int index) { return getContainer32Resource(index); }
    
    Table32(ICUResourceBundleReader reader, int offset) {
      super();
      offset = reader.getResourceByteOffset(offset);
      key32Offsets = reader.getTable32KeyOffsets(offset);
      size = key32Offsets.length;
      itemsOffset = (offset + 4 * (1 + size));
    }
  }
  




  public static String getFullName(String baseName, String localeName)
  {
    if ((baseName == null) || (baseName.length() == 0)) {
      if (localeName.length() == 0) {
        return localeName = ULocale.getDefault().toString();
      }
      return localeName + ".res";
    }
    if (baseName.indexOf('.') == -1) {
      if (baseName.charAt(baseName.length() - 1) != '/') {
        return baseName + "/" + localeName + ".res";
      }
      return baseName + localeName + ".res";
    }
    
    baseName = baseName.replace('.', '/');
    if (localeName.length() == 0) {
      return baseName + ".res";
    }
    return baseName + "_" + localeName + ".res";
  }
}
