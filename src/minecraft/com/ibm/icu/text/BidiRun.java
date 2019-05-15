package com.ibm.icu.text;










public class BidiRun
{
  int start;
  








  int limit;
  







  int insertRemove;
  







  byte level;
  








  BidiRun()
  {
    this(0, 0, (byte)0);
  }
  



  BidiRun(int start, int limit, byte embeddingLevel)
  {
    this.start = start;
    this.limit = limit;
    level = embeddingLevel;
  }
  



  void copyFrom(BidiRun run)
  {
    start = start;
    limit = limit;
    level = level;
    insertRemove = insertRemove;
  }
  




  public int getStart()
  {
    return start;
  }
  




  public int getLimit()
  {
    return limit;
  }
  




  public int getLength()
  {
    return limit - start;
  }
  




  public byte getEmbeddingLevel()
  {
    return level;
  }
  






  public boolean isOddRun()
  {
    return (level & 0x1) == 1;
  }
  






  public boolean isEvenRun()
  {
    return (level & 0x1) == 0;
  }
  




  public byte getDirection()
  {
    return (byte)(level & 0x1);
  }
  




  public String toString()
  {
    return "BidiRun " + start + " - " + limit + " @ " + level;
  }
}
