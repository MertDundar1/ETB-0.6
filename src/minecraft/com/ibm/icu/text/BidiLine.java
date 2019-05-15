package com.ibm.icu.text;

import java.util.Arrays;











































































final class BidiLine
{
  BidiLine() {}
  
  static void setTrailingWSStart(Bidi bidi)
  {
    byte[] dirProps = bidi.dirProps;
    byte[] levels = bidi.levels;
    int start = length;
    byte paraLevel = bidi.paraLevel;
    






    if (Bidi.NoContextRTL(dirProps[(start - 1)]) == 7) {
      trailingWSStart = start;
      return;
    }
    

    while ((start > 0) && ((Bidi.DirPropFlagNC(dirProps[(start - 1)]) & Bidi.MASK_WS) != 0)) {
      start--;
    }
    

    while ((start > 0) && (levels[(start - 1)] == paraLevel)) {
      start--;
    }
    
    trailingWSStart = start;
  }
  

  static Bidi setLine(Bidi paraBidi, int start, int limit)
  {
    Bidi lineBidi = new Bidi();
    






    int length = lineBidi.length = lineBidi.originalLength = lineBidi.resultLength = limit - start;
    

    text = new char[length];
    System.arraycopy(text, start, text, 0, length);
    paraLevel = paraBidi.GetParaLevelAt(start);
    paraCount = paraCount;
    runs = new BidiRun[0];
    reorderingMode = reorderingMode;
    reorderingOptions = reorderingOptions;
    if (controlCount > 0)
    {
      for (int j = start; j < limit; j++) {
        if (Bidi.IsBidiControlChar(text[j])) {
          controlCount += 1;
        }
      }
      resultLength -= controlCount;
    }
    
    lineBidi.getDirPropsMemory(length);
    dirProps = dirPropsMemory;
    System.arraycopy(dirProps, start, dirProps, 0, length);
    

    lineBidi.getLevelsMemory(length);
    levels = levelsMemory;
    System.arraycopy(paraBidi.levels, start, levels, 0, length);
    
    runCount = -1;
    
    if (direction != 2)
    {
      direction = direction;
      





      if (paraBidi.trailingWSStart <= start) {
        trailingWSStart = 0;
      } else if (paraBidi.trailingWSStart < limit) {
        paraBidi.trailingWSStart -= start;
      } else {
        trailingWSStart = length;
      }
    } else {
      byte[] levels = levels;
      


      setTrailingWSStart(lineBidi);
      int trailingWSStart = trailingWSStart;
      

      if (trailingWSStart == 0)
      {
        direction = ((byte)(paraLevel & 0x1));
      }
      else {
        byte level = (byte)(levels[0] & 0x1);
        


        if ((trailingWSStart < length) && ((paraLevel & 0x1) != level))
        {


          direction = 2;
        }
        else
        {
          for (int i = 1;; i++) {
            if (i == trailingWSStart)
            {
              direction = level;
              break; }
            if ((levels[i] & 0x1) != level) {
              direction = 2;
              break;
            }
          }
        }
      }
      
      switch (direction)
      {
      case 0: 
        paraLevel = ((byte)(paraLevel + 1 & 0xFFFFFFFE));
        



        trailingWSStart = 0;
        break;
      
      case 1: 
        Bidi tmp475_473 = lineBidi;475473paraLevel = ((byte)(475473paraLevel | 0x1));
        


        trailingWSStart = 0;
        break;
      }
      
    }
    
    paraBidi = paraBidi;
    return lineBidi;
  }
  

  static byte getLevelAt(Bidi bidi, int charIndex)
  {
    if ((direction != 2) || (charIndex >= trailingWSStart)) {
      return bidi.GetParaLevelAt(charIndex);
    }
    return levels[charIndex];
  }
  

  static byte[] getLevels(Bidi bidi)
  {
    int start = trailingWSStart;
    int length = bidi.length;
    
    if (start != length)
    {









      Arrays.fill(bidi.levels, start, length, paraLevel);
      

      trailingWSStart = length;
    }
    if (length < bidi.levels.length) {
      byte[] levels = new byte[length];
      System.arraycopy(bidi.levels, 0, levels, 0, length);
      return levels;
    }
    return bidi.levels;
  }
  



  static BidiRun getLogicalRun(Bidi bidi, int logicalPosition)
  {
    BidiRun newRun = new BidiRun();
    getRuns(bidi);
    int runCount = bidi.runCount;
    int visualStart = 0;int logicalLimit = 0;
    BidiRun iRun = runs[0];
    
    for (int i = 0; i < runCount; i++) {
      iRun = runs[i];
      logicalLimit = start + limit - visualStart;
      if ((logicalPosition >= start) && (logicalPosition < logicalLimit)) {
        break;
      }
      
      visualStart = limit;
    }
    start = start;
    limit = logicalLimit;
    level = level;
    return newRun;
  }
  
  static BidiRun getVisualRun(Bidi bidi, int runIndex)
  {
    int start = runs[runIndex].start;
    
    byte level = runs[runIndex].level;
    int limit;
    int limit; if (runIndex > 0) {
      limit = start + runs[runIndex].limit - runs[(runIndex - 1)].limit;
    }
    else
    {
      limit = start + runs[0].limit;
    }
    return new BidiRun(start, limit, level);
  }
  

  static void getSingleRun(Bidi bidi, byte level)
  {
    runs = simpleRuns;
    runCount = 1;
    

    runs[0] = new BidiRun(0, length, level);
  }
  


































  private static void reorderLine(Bidi bidi, byte minLevel, byte maxLevel)
  {
    if (maxLevel <= (minLevel | 0x1)) {
      return;
    }
    










    minLevel = (byte)(minLevel + 1);
    
    BidiRun[] runs = bidi.runs;
    byte[] levels = bidi.levels;
    int runCount = bidi.runCount;
    

    if (trailingWSStart < length) {
      runCount--;
    }
    
    maxLevel = (byte)(maxLevel - 1); if (maxLevel >= minLevel) {
      int firstRun = 0;
      


      for (;;)
      {
        if ((firstRun < runCount) && (levels[start] < maxLevel)) {
          firstRun++;
        } else {
          if (firstRun >= runCount) {
            break;
          }
          

          int limitRun = firstRun;
          do { limitRun++; } while ((limitRun < runCount) && (levels[start] >= maxLevel));
          

          int endRun = limitRun - 1;
          while (firstRun < endRun) {
            BidiRun tempRun = runs[firstRun];
            runs[firstRun] = runs[endRun];
            runs[endRun] = tempRun;
            firstRun++;
            endRun--;
          }
          
          if (limitRun == runCount) {
            break;
          }
          firstRun = limitRun + 1;
        }
      }
    }
    

    if ((minLevel & 0x1) == 0) {
      int firstRun = 0;
      

      if (trailingWSStart == length) {
        runCount--;
      }
      

      while (firstRun < runCount) {
        BidiRun tempRun = runs[firstRun];
        runs[firstRun] = runs[runCount];
        runs[runCount] = tempRun;
        firstRun++;
        runCount--;
      }
    }
  }
  

  static int getRunFromLogicalIndex(Bidi bidi, int logicalIndex)
  {
    BidiRun[] runs = bidi.runs;
    int runCount = bidi.runCount;int visualStart = 0;
    
    for (int i = 0; i < runCount; i++) {
      int length = limit - visualStart;
      int logicalStart = start;
      if ((logicalIndex >= logicalStart) && (logicalIndex < logicalStart + length)) {
        return i;
      }
      visualStart += length;
    }
    

    throw new IllegalStateException("Internal ICU error in getRunFromLogicalIndex");
  }
  















  static void getRuns(Bidi bidi)
  {
    if (bidi.runCount >= 0) {
      return;
    }
    if (direction != 2)
    {

      getSingleRun(bidi, paraLevel);
    }
    else {
      int length = bidi.length;
      byte[] levels = bidi.levels;
      
      byte level = 126;
      










      int limit = trailingWSStart;
      
      int runCount = 0;
      for (int i = 0; i < limit; i++)
      {
        if (levels[i] != level) {
          runCount++;
          level = levels[i];
        }
      }
      




      if ((runCount == 1) && (limit == length))
      {
        getSingleRun(bidi, levels[0]);

      }
      else
      {
        byte minLevel = 62;
        byte maxLevel = 0;
        

        if (limit < length) {
          runCount++;
        }
        

        bidi.getRunsMemory(runCount);
        BidiRun[] runs = runsMemory;
        






        int runIndex = 0;
        

        i = 0;
        do
        {
          int start = i;
          level = levels[i];
          if (level < minLevel) {
            minLevel = level;
          }
          if (level > maxLevel) {
            maxLevel = level;
          }
          do
          {
            i++; } while ((i < limit) && (levels[i] == level));
          

          runs[runIndex] = new BidiRun(start, i - start, level);
          runIndex++;
        } while (i < limit);
        
        if (limit < length)
        {
          runs[runIndex] = new BidiRun(limit, length - limit, paraLevel);
          

          if (paraLevel < minLevel) {
            minLevel = paraLevel;
          }
        }
        

        bidi.runs = runs;
        bidi.runCount = runCount;
        
        reorderLine(bidi, minLevel, maxLevel);
        


        limit = 0;
        for (i = 0; i < runCount; i++) {
          level = levels[start];
          limit = runs[i].limit += limit;
        }
        




        if (runIndex < runCount) {
          int trailingRun = (paraLevel & 0x1) != 0 ? 0 : runIndex;
          level = paraLevel;
        }
      }
    }
    

    if (insertPoints.size > 0)
    {

      for (int ip = 0; ip < insertPoints.size; ip++) {
        Bidi.Point point = insertPoints.points[ip];
        int runIndex = getRunFromLogicalIndex(bidi, pos);
        runsinsertRemove |= flag;
      }
    }
    

    if (controlCount > 0)
    {

      for (int ic = 0; ic < bidi.length; ic++) {
        char c = text[ic];
        if (Bidi.IsBidiControlChar(c)) {
          int runIndex = getRunFromLogicalIndex(bidi, ic);
          runsinsertRemove -= 1;
        }
      }
    }
  }
  



  static int[] prepareReorder(byte[] levels, byte[] pMinLevel, byte[] pMaxLevel)
  {
    if ((levels == null) || (levels.length <= 0)) {
      return null;
    }
    

    byte minLevel = 62;
    byte maxLevel = 0;
    for (int start = levels.length; start > 0;) {
      byte level = levels[(--start)];
      if (level > 62) {
        return null;
      }
      if (level < minLevel) {
        minLevel = level;
      }
      if (level > maxLevel) {
        maxLevel = level;
      }
    }
    pMinLevel[0] = minLevel;
    pMaxLevel[0] = maxLevel;
    

    int[] indexMap = new int[levels.length];
    for (start = levels.length; start > 0;) {
      start--;
      indexMap[start] = start;
    }
    
    return indexMap;
  }
  
  static int[] reorderLogical(byte[] levels)
  {
    byte[] aMinLevel = new byte[1];
    byte[] aMaxLevel = new byte[1];
    

    int[] indexMap = prepareReorder(levels, aMinLevel, aMaxLevel);
    if (indexMap == null) {
      return null;
    }
    
    byte minLevel = aMinLevel[0];
    byte maxLevel = aMaxLevel[0];
    

    if ((minLevel == maxLevel) && ((minLevel & 0x1) == 0)) {
      return indexMap;
    }
    

    minLevel = (byte)(minLevel | 0x1);
    
    do
    {
      int start = 0;
      


      for (;;)
      {
        if ((start < levels.length) && (levels[start] < maxLevel)) {
          start++;
        } else {
          if (start >= levels.length) {
            break;
          }
          

          int limit = start; do { limit++; } while ((limit < levels.length) && (levels[limit] >= maxLevel));
          











          int sumOfSosEos = start + limit - 1;
          
          do
          {
            indexMap[start] = (sumOfSosEos - indexMap[start]);
            start++; } while (start < limit);
          

          if (limit == levels.length) {
            break;
          }
          start = limit + 1;
        }
      }
      maxLevel = (byte)(maxLevel - 1); } while (maxLevel >= minLevel);
    return indexMap;
  }
  
  static int[] reorderVisual(byte[] levels)
  {
    byte[] aMinLevel = new byte[1];
    byte[] aMaxLevel = new byte[1];
    


    int[] indexMap = prepareReorder(levels, aMinLevel, aMaxLevel);
    if (indexMap == null) {
      return null;
    }
    
    byte minLevel = aMinLevel[0];
    byte maxLevel = aMaxLevel[0];
    

    if ((minLevel == maxLevel) && ((minLevel & 0x1) == 0)) {
      return indexMap;
    }
    

    minLevel = (byte)(minLevel | 0x1);
    
    do
    {
      int start = 0;
      


      for (;;)
      {
        if ((start < levels.length) && (levels[start] < maxLevel)) {
          start++;
        } else {
          if (start >= levels.length) {
            break;
          }
          

          int limit = start; do { limit++; } while ((limit < levels.length) && (levels[limit] >= maxLevel));
          






          int end = limit - 1;
          while (start < end) {
            int temp = indexMap[start];
            indexMap[start] = indexMap[end];
            indexMap[end] = temp;
            
            start++;
            end--;
          }
          
          if (limit == levels.length) {
            break;
          }
          start = limit + 1;
        }
      }
      maxLevel = (byte)(maxLevel - 1); } while (maxLevel >= minLevel);
    
    return indexMap;
  }
  
  static int getVisualIndex(Bidi bidi, int logicalIndex)
  {
    int visualIndex = -1;
    

    switch (direction) {
    case 0: 
      visualIndex = logicalIndex;
      break;
    case 1: 
      visualIndex = bidi.length - logicalIndex - 1;
      break;
    default: 
      getRuns(bidi);
      BidiRun[] runs = bidi.runs;
      int visualStart = 0;
      

      for (int i = 0; i < runCount; i++) {
        int length = limit - visualStart;
        int offset = logicalIndex - start;
        if ((offset >= 0) && (offset < length)) {
          if (runs[i].isEvenRun())
          {
            visualIndex = visualStart + offset; break;
          }
          
          visualIndex = visualStart + length - offset - 1;
          
          break;
        }
        visualStart += length;
      }
      if (i >= runCount) {
        return -1;
      }
      break;
    }
    if (insertPoints.size > 0)
    {
      BidiRun[] runs = bidi.runs;
      
      int visualStart = 0;int markFound = 0;
      int length; for (int i = 0;; visualStart += length) {
        length = limit - visualStart;
        int insertRemove = insertRemove;
        if ((insertRemove & 0x5) > 0) {
          markFound++;
        }
        
        if (visualIndex < limit) {
          return visualIndex + markFound;
        }
        if ((insertRemove & 0xA) > 0) {
          markFound++;
        }
        i++;
      }
    }
    











    if (controlCount > 0)
    {
      BidiRun[] runs = bidi.runs;
      
      int visualStart = 0;int controlFound = 0;
      char uchar = text[logicalIndex];
      
      if (Bidi.IsBidiControlChar(uchar)) {
        return -1;
      }
      int length;
      for (int i = 0;; visualStart += length) {
        length = limit - visualStart;
        int insertRemove = insertRemove;
        
        if (visualIndex >= limit) {
          controlFound -= insertRemove;
        }
        else
        {
          if (insertRemove == 0)
            return visualIndex - controlFound;
          int limit;
          int start; int limit; if (runs[i].isEvenRun())
          {
            int start = start;
            limit = logicalIndex;
          }
          else {
            start = logicalIndex + 1;
            limit = start + length;
          }
          for (int j = start; j < limit; j++) {
            uchar = text[j];
            if (Bidi.IsBidiControlChar(uchar)) {
              controlFound++;
            }
          }
          return visualIndex - controlFound;
        }
        i++;
      }
    }
    



























    return visualIndex;
  }
  



  static int getLogicalIndex(Bidi bidi, int visualIndex)
  {
    BidiRun[] runs = bidi.runs;
    int runCount = bidi.runCount;
    if (insertPoints.size > 0)
    {
      int markFound = 0;
      int visualStart = 0;
      int length;
      for (int i = 0;; visualStart += length) {
        length = limit - visualStart;
        int insertRemove = insertRemove;
        if ((insertRemove & 0x5) > 0) {
          if (visualIndex <= visualStart + markFound) {
            return -1;
          }
          markFound++;
        }
        
        if (visualIndex < limit + markFound) {
          visualIndex -= markFound;
          break;
        }
        if ((insertRemove & 0xA) > 0) {
          if (visualIndex == visualStart + length + markFound) {
            return -1;
          }
          markFound++;
        }
        i++;









      }
      









    }
    else if (controlCount > 0)
    {
      int controlFound = 0;
      int visualStart = 0;
      
      int length;
      
      for (int i = 0;; visualStart += length) {
        length = limit - visualStart;
        int insertRemove = insertRemove;
        
        if (visualIndex >= limit - controlFound + insertRemove) {
          controlFound -= insertRemove;
        }
        else
        {
          if (insertRemove == 0) {
            visualIndex += controlFound;
            break;
          }
          
          int logicalStart = start;
          boolean evenRun = runs[i].isEvenRun();
          int logicalEnd = logicalStart + length - 1;
          for (int j = 0; j < length; j++) {
            int k = evenRun ? logicalStart + j : logicalEnd - j;
            char uchar = text[k];
            if (Bidi.IsBidiControlChar(uchar)) {
              controlFound++;
            }
            if (visualIndex + controlFound == visualStart + j) {
              break;
            }
          }
          visualIndex += controlFound;
          break;
        }
        i++;
      }
    }
    




























    if (runCount <= 10)
    {
      for (int i = 0; visualIndex >= limit; i++) {}
    }
    
    int begin = 0;int limit = runCount;
    int i;
    for (;;)
    {
      i = begin + limit >>> 1;
      if (visualIndex >= limit) {
        begin = i + 1;
      } else { if ((i == 0) || (visualIndex >= 1limit)) {
          break;
        }
        limit = i;
      }
    }
    

    int start = start;
    if (runs[i].isEvenRun())
    {

      if (i > 0) {
        visualIndex -= 1limit;
      }
      return start + visualIndex;
    }
    
    return start + limit - visualIndex - 1;
  }
  


  static int[] getLogicalMap(Bidi bidi)
  {
    BidiRun[] runs = bidi.runs;
    
    int[] indexMap = new int[bidi.length];
    if (bidi.length > resultLength) {
      Arrays.fill(indexMap, -1);
    }
    
    int visualStart = 0;
    for (int j = 0; j < bidi.runCount; j++) {
      int logicalStart = start;
      int visualLimit = limit;
      if (runs[j].isEvenRun()) {
        do {
          indexMap[(logicalStart++)] = (visualStart++);
        } while (visualStart < visualLimit);
      } else {
        logicalStart += visualLimit - visualStart;
        do {
          indexMap[(--logicalStart)] = (visualStart++);
        } while (visualStart < visualLimit);
      }
    }
    

    if (insertPoints.size > 0) {
      int markFound = 0;int runCount = bidi.runCount;
      
      runs = bidi.runs;
      visualStart = 0;
      int length;
      for (int i = 0; i < runCount; visualStart += length) {
        length = limit - visualStart;
        int insertRemove = insertRemove;
        if ((insertRemove & 0x5) > 0) {
          markFound++;
        }
        if (markFound > 0) {
          int logicalStart = start;
          int logicalLimit = logicalStart + length;
          for (int j = logicalStart; j < logicalLimit; j++) {
            indexMap[j] += markFound;
          }
        }
        if ((insertRemove & 0xA) > 0) {
          markFound++;
        }
        i++;







      }
      







    }
    else if (controlCount > 0) {
      int controlFound = 0;int runCount = bidi.runCount;
      


      runs = bidi.runs;
      visualStart = 0;
      int length;
      for (int i = 0; i < runCount; visualStart += length) {
        length = limit - visualStart;
        int insertRemove = insertRemove;
        
        if (controlFound - insertRemove != 0)
        {

          int logicalStart = start;
          boolean evenRun = runs[i].isEvenRun();
          int logicalLimit = logicalStart + length;
          
          if (insertRemove == 0) {
            for (int j = logicalStart; j < logicalLimit; j++) {
              indexMap[j] -= controlFound;
            }
          }
          
          for (int j = 0; j < length; j++) {
            int k = evenRun ? logicalStart + j : logicalLimit - j - 1;
            char uchar = text[k];
            if (Bidi.IsBidiControlChar(uchar)) {
              controlFound++;
              indexMap[k] = -1;
            }
            else {
              indexMap[k] -= controlFound;
            }
          }
        }
        i++;
      }
    }
    

























    return indexMap;
  }
  

  static int[] getVisualMap(Bidi bidi)
  {
    BidiRun[] runs = bidi.runs;
    
    int allocLength = bidi.length > resultLength ? bidi.length : resultLength;
    
    int[] indexMap = new int[allocLength];
    
    int visualStart = 0;
    int idx = 0;
    for (int j = 0; j < bidi.runCount; j++) {
      int logicalStart = start;
      int visualLimit = limit;
      if (runs[j].isEvenRun()) {
        do {
          indexMap[(idx++)] = (logicalStart++);
          visualStart++; } while (visualStart < visualLimit);
      } else {
        logicalStart += visualLimit - visualStart;
        do {
          indexMap[(idx++)] = (--logicalStart);
          visualStart++; } while (visualStart < visualLimit);
      }
    }
    

    if (insertPoints.size > 0) {
      int markFound = 0;int runCount = bidi.runCount;
      
      runs = bidi.runs;
      
      for (int i = 0; i < runCount; i++) {
        int insertRemove = insertRemove;
        if ((insertRemove & 0x5) > 0) {
          markFound++;
        }
        if ((insertRemove & 0xA) > 0) {
          markFound++;
        }
      }
      
      int k = resultLength;
      for (i = runCount - 1; (i >= 0) && (markFound > 0); i--) {
        int insertRemove = insertRemove;
        if ((insertRemove & 0xA) > 0) {
          indexMap[(--k)] = -1;
          markFound--;
        }
        visualStart = i > 0 ? 1limit : 0;
        for (int j = limit - 1; (j >= visualStart) && (markFound > 0); j--) {
          indexMap[(--k)] = indexMap[j];
        }
        if ((insertRemove & 0x5) > 0) {
          indexMap[(--k)] = -1;
          markFound--;
        }
      }
    }
    else if (controlCount > 0) {
      int runCount = bidi.runCount;
      


      runs = bidi.runs;
      visualStart = 0;
      
      int k = 0;
      int length; for (int i = 0; i < runCount; visualStart += length) {
        length = limit - visualStart;
        int insertRemove = insertRemove;
        
        if ((insertRemove == 0) && (k == visualStart)) {
          k += length;
        }
        else
        {
          if (insertRemove == 0) {
            int visualLimit = limit;
            for (int j = visualStart; j < visualLimit; j++) {
              indexMap[(k++)] = indexMap[j];
            }
          }
          
          int logicalStart = start;
          boolean evenRun = runs[i].isEvenRun();
          int logicalEnd = logicalStart + length - 1;
          for (int j = 0; j < length; j++) {
            int m = evenRun ? logicalStart + j : logicalEnd - j;
            char uchar = text[m];
            if (!Bidi.IsBidiControlChar(uchar)) {
              indexMap[(k++)] = m;
            }
          }
        }
        i++;
      }
    }
    
























    if (allocLength == resultLength) {
      return indexMap;
    }
    int[] newMap = new int[resultLength];
    System.arraycopy(indexMap, 0, newMap, 0, resultLength);
    return newMap;
  }
  
  static int[] invertMap(int[] srcMap)
  {
    int srcLength = srcMap.length;
    int destLength = -1;int count = 0;
    

    for (int i = 0; i < srcLength; i++) {
      int srcEntry = srcMap[i];
      if (srcEntry > destLength) {
        destLength = srcEntry;
      }
      if (srcEntry >= 0) {
        count++;
      }
    }
    destLength++;
    int[] destMap = new int[destLength];
    if (count < destLength)
    {
      Arrays.fill(destMap, -1);
    }
    for (i = 0; i < srcLength; i++) {
      int srcEntry = srcMap[i];
      if (srcEntry >= 0) {
        destMap[srcEntry] = i;
      }
    }
    return destMap;
  }
}
