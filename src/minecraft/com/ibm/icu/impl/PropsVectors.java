package com.ibm.icu.impl;

import java.util.Arrays;
import java.util.Comparator;




























public class PropsVectors
{
  private int[] v;
  private int columns;
  private int maxRows;
  private int rows;
  private int prevRow;
  private boolean isCompacted;
  public static final int FIRST_SPECIAL_CP = 1114112;
  public static final int INITIAL_VALUE_CP = 1114112;
  public static final int ERROR_VALUE_CP = 1114113;
  public static final int MAX_CP = 1114113;
  public static final int INITIAL_ROWS = 4096;
  public static final int MEDIUM_ROWS = 65536;
  public static final int MAX_ROWS = 1114114;
  
  private boolean areElementsSame(int index1, int[] target, int index2, int length)
  {
    for (int i = 0; i < length; i++) {
      if (v[(index1 + i)] != target[(index2 + i)]) {
        return false;
      }
    }
    return true;
  }
  



  private int findRow(int rangeStart)
  {
    int index = 0;
    



    index = prevRow * columns;
    if (rangeStart >= v[index]) {
      if (rangeStart < v[(index + 1)])
      {
        return index;
      }
      index += columns;
      if (rangeStart < v[(index + 1)]) {
        prevRow += 1;
        return index;
      }
      index += columns;
      if (rangeStart < v[(index + 1)]) {
        prevRow += 2;
        return index; }
      if (rangeStart - v[(index + 1)] < 10)
      {
        prevRow += 2;
        do {
          prevRow += 1;
          index += columns;
        } while (rangeStart >= v[(index + 1)]);
        return index;
      }
      
    }
    else if (rangeStart < v[1])
    {
      prevRow = 0;
      return 0;
    }
    

    int start = 0;
    int mid = 0;
    int limit = rows;
    while (start < limit - 1) {
      mid = (start + limit) / 2;
      index = columns * mid;
      if (rangeStart < v[index]) {
        limit = mid;
      } else { if (rangeStart < v[(index + 1)]) {
          prevRow = mid;
          return index;
        }
        start = mid;
      }
    }
    


    prevRow = start;
    index = start * columns;
    return index;
  }
  
















  public PropsVectors(int numOfColumns)
  {
    if (numOfColumns < 1) {
      throw new IllegalArgumentException("numOfColumns need to be no less than 1; but it is " + numOfColumns);
    }
    
    columns = (numOfColumns + 2);
    v = new int[4096 * columns];
    maxRows = 4096;
    rows = 3;
    prevRow = 0;
    isCompacted = false;
    v[0] = 0;
    v[1] = 1114112;
    int index = columns;
    for (int cp = 1114112; cp <= 1114113; cp++) {
      v[index] = cp;
      v[(index + 1)] = (cp + 1);
      index += columns;
    }
  }
  









  public void setValue(int start, int end, int column, int value, int mask)
  {
    if ((start < 0) || (start > end) || (end > 1114113) || (column < 0) || (column >= columns - 2))
    {
      throw new IllegalArgumentException();
    }
    if (isCompacted) {
      throw new IllegalStateException("Shouldn't be called aftercompact()!");
    }
    


    int limit = end + 1;
    

    column += 2;
    value &= mask;
    


    int firstRow = findRow(start);
    int lastRow = findRow(end);
    





    boolean splitFirstRow = (start != v[firstRow]) && (value != (v[(firstRow + column)] & mask));
    boolean splitLastRow = (limit != v[(lastRow + 1)]) && (value != (v[(lastRow + column)] & mask));
    

    if ((splitFirstRow) || (splitLastRow)) {
      int rowsToExpand = 0;
      if (splitFirstRow) {
        rowsToExpand++;
      }
      if (splitLastRow) {
        rowsToExpand++;
      }
      int newMaxRows = 0;
      if (rows + rowsToExpand > maxRows) {
        if (maxRows < 65536) {
          newMaxRows = 65536;
        } else if (maxRows < 1114114) {
          newMaxRows = 1114114;
        } else {
          throw new IndexOutOfBoundsException("MAX_ROWS exceeded! Increase it to a higher valuein the implementation");
        }
        

        int[] temp = new int[newMaxRows * columns];
        System.arraycopy(v, 0, temp, 0, rows * columns);
        v = temp;
        maxRows = newMaxRows;
      }
      


      int count = rows * columns - (lastRow + columns);
      if (count > 0) {
        System.arraycopy(v, lastRow + columns, v, lastRow + (1 + rowsToExpand) * columns, count);
      }
      
      rows += rowsToExpand;
      


      if (splitFirstRow)
      {
        count = lastRow - firstRow + columns;
        System.arraycopy(v, firstRow, v, firstRow + columns, count);
        lastRow += columns; int 
        

          tmp422_421 = start;v[(firstRow + columns)] = tmp422_421;v[(firstRow + 1)] = tmp422_421;
        firstRow += columns;
      }
      

      if (splitLastRow)
      {
        System.arraycopy(v, lastRow, v, lastRow + columns, columns); int 
        

          tmp484_482 = limit;v[(lastRow + columns)] = tmp484_482;v[(lastRow + 1)] = tmp484_482;
      }
    }
    

    prevRow = (lastRow / columns);
    

    firstRow += column;
    lastRow += column;
    mask ^= 0xFFFFFFFF;
    for (;;) {
      v[firstRow] = (v[firstRow] & mask | value);
      if (firstRow == lastRow) {
        break;
      }
      firstRow += columns;
    }
  }
  


  public int getValue(int c, int column)
  {
    if ((isCompacted) || (c < 0) || (c > 1114113) || (column < 0) || (column >= columns - 2))
    {
      return 0;
    }
    int index = findRow(c);
    return v[(index + 2 + column)];
  }
  






  public int[] getRow(int rowIndex)
  {
    if (isCompacted) {
      throw new IllegalStateException("Illegal Invocation of the method after compact()");
    }
    
    if ((rowIndex < 0) || (rowIndex > rows)) {
      throw new IllegalArgumentException("rowIndex out of bound!");
    }
    int[] rowToReturn = new int[columns - 2];
    System.arraycopy(v, rowIndex * columns + 2, rowToReturn, 0, columns - 2);
    
    return rowToReturn;
  }
  







  public int getRowStart(int rowIndex)
  {
    if (isCompacted) {
      throw new IllegalStateException("Illegal Invocation of the method after compact()");
    }
    
    if ((rowIndex < 0) || (rowIndex > rows)) {
      throw new IllegalArgumentException("rowIndex out of bound!");
    }
    return v[(rowIndex * columns)];
  }
  







  public int getRowEnd(int rowIndex)
  {
    if (isCompacted) {
      throw new IllegalStateException("Illegal Invocation of the method after compact()");
    }
    
    if ((rowIndex < 0) || (rowIndex > rows)) {
      throw new IllegalArgumentException("rowIndex out of bound!");
    }
    return v[(rowIndex * columns + 1)] - 1;
  }
  

















  public void compact(CompactHandler compactor)
  {
    if (isCompacted) {
      return;
    }
    


    isCompacted = true;
    int valueColumns = columns - 2;
    

    Integer[] indexArray = new Integer[rows];
    for (int i = 0; i < rows; i++) {
      indexArray[i] = Integer.valueOf(columns * i);
    }
    
    Arrays.sort(indexArray, new Comparator() {
      public int compare(Integer o1, Integer o2) {
        int indexOfRow1 = o1.intValue();
        int indexOfRow2 = o2.intValue();
        int count = columns;
        


        int index = 2;
        do {
          if (v[(indexOfRow1 + index)] != v[(indexOfRow2 + index)]) {
            return v[(indexOfRow1 + index)] < v[(indexOfRow2 + index)] ? -1 : 1;
          }
          
          index++; if (index == columns) {
            index = 0;
          }
          count--; } while (count > 0);
        
        return 0;


      }
      



    });
    int count = -valueColumns;
    for (int i = 0; i < rows; i++) {
      int start = v[indexArray[i].intValue()];
      


      if ((count < 0) || (!areElementsSame(indexArray[i].intValue() + 2, v, indexArray[(i - 1)].intValue() + 2, valueColumns)))
      {
        count += valueColumns;
      }
      
      if (start == 1114112) {
        compactor.setRowIndexForInitialValue(count);
      } else if (start == 1114113) {
        compactor.setRowIndexForErrorValue(count);
      }
    }
    


    count += valueColumns;
    


    compactor.startRealValues(count);
    







    int[] temp = new int[count];
    count = -valueColumns;
    for (int i = 0; i < rows; i++) {
      int start = v[indexArray[i].intValue()];
      int limit = v[(indexArray[i].intValue() + 1)];
      


      if ((count < 0) || (!areElementsSame(indexArray[i].intValue() + 2, temp, count, valueColumns)))
      {
        count += valueColumns;
        System.arraycopy(v, indexArray[i].intValue() + 2, temp, count, valueColumns);
      }
      

      if (start < 1114112) {
        compactor.setRowIndexForRange(start, limit - 1, count);
      }
    }
    v = temp;
    


    rows = (count / valueColumns + 1);
  }
  




  public int[] getCompactedArray()
  {
    if (!isCompacted) {
      throw new IllegalStateException("Illegal Invocation of the method before compact()");
    }
    
    return v;
  }
  




  public int getCompactedRows()
  {
    if (!isCompacted) {
      throw new IllegalStateException("Illegal Invocation of the method before compact()");
    }
    
    return rows;
  }
  




  public int getCompactedColumns()
  {
    if (!isCompacted) {
      throw new IllegalStateException("Illegal Invocation of the method before compact()");
    }
    
    return columns - 2;
  }
  



  public IntTrie compactToTrieWithRowIndexes()
  {
    PVecToTrieCompactHandler compactor = new PVecToTrieCompactHandler();
    compact(compactor);
    return builder.serialize(new DefaultGetFoldedValue(builder), new DefaultGetFoldingOffset(null));
  }
  
  private static class DefaultGetFoldingOffset implements Trie.DataManipulate {
    private DefaultGetFoldingOffset() {}
    
    public int getFoldingOffset(int value) {
      return value;
    }
  }
  
  private static class DefaultGetFoldedValue implements TrieBuilder.DataManipulate
  {
    private IntTrieBuilder builder;
    
    public DefaultGetFoldedValue(IntTrieBuilder inBuilder)
    {
      builder = inBuilder;
    }
    
    public int getFoldedValue(int start, int offset) {
      int initialValue = builder.m_initialValue_;
      int limit = start + 1024;
      while (start < limit) {
        boolean[] inBlockZero = new boolean[1];
        int value = builder.getValue(start, inBlockZero);
        if (inBlockZero[0] != 0) {
          start += 32;
        } else { if (value != initialValue) {
            return offset;
          }
          start++;
        }
      }
      return 0;
    }
  }
  
  public static abstract interface CompactHandler
  {
    public abstract void setRowIndexForRange(int paramInt1, int paramInt2, int paramInt3);
    
    public abstract void setRowIndexForInitialValue(int paramInt);
    
    public abstract void setRowIndexForErrorValue(int paramInt);
    
    public abstract void startRealValues(int paramInt);
  }
}
