package net.minecraft.util;


public class LongHashMap
{
  private transient Entry[] hashArray = new Entry['က'];
  

  private transient int numHashElements;
  

  private int field_180201_c;
  

  private int capacity = 3072;
  



  private final float percentUseable = 0.75F;
  
  private volatile transient int modCount;
  
  private static final String __OBFID = "CL_00001492";
  
  public LongHashMap()
  {
    field_180201_c = (hashArray.length - 1);
  }
  



  private static int getHashedKey(long originalKey)
  {
    return (int)(originalKey ^ originalKey >>> 27);
  }
  



  private static int hash(int integer)
  {
    integer ^= integer >>> 20 ^ integer >>> 12;
    return integer ^ integer >>> 7 ^ integer >>> 4;
  }
  



  private static int getHashIndex(int p_76158_0_, int p_76158_1_)
  {
    return p_76158_0_ & p_76158_1_;
  }
  
  public int getNumHashElements()
  {
    return numHashElements;
  }
  



  public Object getValueByKey(long p_76164_1_)
  {
    int var3 = getHashedKey(p_76164_1_);
    
    for (Entry var4 = hashArray[getHashIndex(var3, field_180201_c)]; var4 != null; var4 = nextEntry)
    {
      if (key == p_76164_1_)
      {
        return value;
      }
    }
    
    return null;
  }
  
  public boolean containsItem(long p_76161_1_)
  {
    return getEntry(p_76161_1_) != null;
  }
  
  final Entry getEntry(long p_76160_1_)
  {
    int var3 = getHashedKey(p_76160_1_);
    
    for (Entry var4 = hashArray[getHashIndex(var3, field_180201_c)]; var4 != null; var4 = nextEntry)
    {
      if (key == p_76160_1_)
      {
        return var4;
      }
    }
    
    return null;
  }
  



  public void add(long p_76163_1_, Object p_76163_3_)
  {
    int var4 = getHashedKey(p_76163_1_);
    int var5 = getHashIndex(var4, field_180201_c);
    
    for (Entry var6 = hashArray[var5]; var6 != null; var6 = nextEntry)
    {
      if (key == p_76163_1_)
      {
        value = p_76163_3_;
        return;
      }
    }
    
    modCount += 1;
    createKey(var4, p_76163_1_, p_76163_3_, var5);
  }
  



  private void resizeTable(int p_76153_1_)
  {
    Entry[] var2 = hashArray;
    int var3 = var2.length;
    
    if (var3 == 1073741824)
    {
      capacity = Integer.MAX_VALUE;
    }
    else
    {
      Entry[] var4 = new Entry[p_76153_1_];
      copyHashTableTo(var4);
      hashArray = var4;
      field_180201_c = (hashArray.length - 1);
      float var10001 = p_76153_1_;
      getClass();
      capacity = ((int)(var10001 * 0.75F));
    }
  }
  



  private void copyHashTableTo(Entry[] p_76154_1_)
  {
    Entry[] var2 = hashArray;
    int var3 = p_76154_1_.length;
    
    for (int var4 = 0; var4 < var2.length; var4++)
    {
      Entry var5 = var2[var4];
      
      if (var5 != null)
      {
        var2[var4] = null;
        
        Entry var6;
        do
        {
          var6 = nextEntry;
          int var7 = getHashIndex(hash, var3 - 1);
          nextEntry = p_76154_1_[var7];
          p_76154_1_[var7] = var5;
          var5 = var6;
        }
        while (var6 != null);
      }
    }
  }
  



  public Object remove(long p_76159_1_)
  {
    Entry var3 = removeKey(p_76159_1_);
    return var3 == null ? null : value;
  }
  



  final Entry removeKey(long p_76152_1_)
  {
    int var3 = getHashedKey(p_76152_1_);
    int var4 = getHashIndex(var3, field_180201_c);
    Entry var5 = hashArray[var4];
    
    Entry var7;
    
    for (Entry var6 = var5; var6 != null; var6 = var7)
    {
      var7 = nextEntry;
      
      if (key == p_76152_1_)
      {
        modCount += 1;
        numHashElements -= 1;
        
        if (var5 == var6)
        {
          hashArray[var4] = var7;
        }
        else
        {
          nextEntry = var7;
        }
        
        return var6;
      }
      
      var5 = var6;
    }
    
    return var6;
  }
  



  private void createKey(int p_76156_1_, long p_76156_2_, Object p_76156_4_, int p_76156_5_)
  {
    Entry var6 = hashArray[p_76156_5_];
    hashArray[p_76156_5_] = new Entry(p_76156_1_, p_76156_2_, p_76156_4_, var6);
    
    if (numHashElements++ >= capacity)
    {
      resizeTable(2 * hashArray.length);
    }
  }
  
  public double getKeyDistribution()
  {
    int countValid = 0;
    
    for (int i = 0; i < hashArray.length; i++)
    {
      if (hashArray[i] != null)
      {
        countValid++;
      }
    }
    
    return 1.0D * countValid / numHashElements;
  }
  
  static class Entry
  {
    final long key;
    Object value;
    Entry nextEntry;
    final int hash;
    private static final String __OBFID = "CL_00001493";
    
    Entry(int p_i1553_1_, long p_i1553_2_, Object p_i1553_4_, Entry p_i1553_5_)
    {
      value = p_i1553_4_;
      nextEntry = p_i1553_5_;
      key = p_i1553_2_;
      hash = p_i1553_1_;
    }
    
    public final long getKey()
    {
      return key;
    }
    
    public final Object getValue()
    {
      return value;
    }
    
    public final boolean equals(Object p_equals_1_)
    {
      if (!(p_equals_1_ instanceof Entry))
      {
        return false;
      }
      

      Entry var2 = (Entry)p_equals_1_;
      Long var3 = Long.valueOf(getKey());
      Long var4 = Long.valueOf(var2.getKey());
      
      if ((var3 == var4) || ((var3 != null) && (var3.equals(var4))))
      {
        Object var5 = getValue();
        Object var6 = var2.getValue();
        
        if ((var5 == var6) || ((var5 != null) && (var5.equals(var6))))
        {
          return true;
        }
      }
      
      return false;
    }
    

    public final int hashCode()
    {
      return LongHashMap.getHashedKey(key);
    }
    
    public final String toString()
    {
      return getKey() + "=" + getValue();
    }
  }
}
