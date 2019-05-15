package net.minecraft.client.audio;

public class SoundEventAccessor implements ISoundEventAccessor
{
  private final SoundPoolEntry entry;
  private final int weight;
  private static final String __OBFID = "CL_00001153";
  
  SoundEventAccessor(SoundPoolEntry entry, int weight)
  {
    this.entry = entry;
    this.weight = weight;
  }
  
  public int getWeight()
  {
    return weight;
  }
  
  public SoundPoolEntry cloneEntry()
  {
    return new SoundPoolEntry(entry);
  }
  
  public Object cloneEntry1()
  {
    return cloneEntry();
  }
}
