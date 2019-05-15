package com.ibm.icu.text;




























































































































































































public final class UnicodeCompressor
  implements SCSU
{
  private static boolean[] sSingleTagTable = { false, true, true, true, true, true, true, true, true, false, false, true, true, false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false };
  
































  private static boolean[] sUnicodeTagTable = { false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false };
  




































  private int fCurrentWindow = 0;
  

  private int[] fOffsets = new int[8];
  

  private int fMode = 0;
  

  private int[] fIndexCount = new int['Ā'];
  

  private int[] fTimeStamps = new int[8];
  

  private int fTimeStamp = 0;
  







  public UnicodeCompressor()
  {
    reset();
  }
  







  public static byte[] compress(String buffer)
  {
    return compress(buffer.toCharArray(), 0, buffer.length());
  }
  











  public static byte[] compress(char[] buffer, int start, int limit)
  {
    UnicodeCompressor comp = new UnicodeCompressor();
    





    int len = Math.max(4, 3 * (limit - start) + 1);
    byte[] temp = new byte[len];
    
    int byteCount = comp.compress(buffer, start, limit, null, temp, 0, len);
    

    byte[] result = new byte[byteCount];
    System.arraycopy(temp, 0, result, 0, byteCount);
    return result;
  }
  


























  public int compress(char[] charBuffer, int charBufferStart, int charBufferLimit, int[] charsRead, byte[] byteBuffer, int byteBufferStart, int byteBufferLimit)
  {
    int bytePos = byteBufferStart;
    

    int ucPos = charBufferStart;
    

    int curUC = -1;
    

    int curIndex = -1;
    

    int nextUC = -1;
    int forwardUC = -1;
    

    int whichWindow = 0;
    

    int hiByte = 0;
    int loByte = 0;
    


    if ((byteBuffer.length < 4) || (byteBufferLimit - byteBufferStart < 4)) {
      throw new IllegalArgumentException("byteBuffer.length < 4");
    }
    
    while ((ucPos < charBufferLimit) && (bytePos < byteBufferLimit))
      switch (fMode)
      {
      case 0: 
      case 1: 
        while ((ucPos < charBufferLimit) && (bytePos < byteBufferLimit))
        {
          curUC = charBuffer[(ucPos++)];
          

          if (ucPos < charBufferLimit) {
            nextUC = charBuffer[ucPos];
          } else {
            nextUC = -1;
          }
          

          if (curUC < 128) {
            loByte = curUC & 0xFF;
            



            if (sSingleTagTable[loByte] != 0)
            {


              if (bytePos + 1 >= byteBufferLimit) {
                ucPos--;
                
                break label1668;
              }
              byteBuffer[(bytePos++)] = 1;
            }
            
            byteBuffer[(bytePos++)] = ((byte)loByte);




          }
          else if (inDynamicWindow(curUC, fCurrentWindow)) {
            byteBuffer[(bytePos++)] = ((byte)(curUC - fOffsets[fCurrentWindow] + 128));





          }
          else if (!isCompressible(curUC))
          {
            if ((nextUC != -1) && (isCompressible(nextUC)))
            {



              if (bytePos + 2 >= byteBufferLimit) {
                ucPos--;
                break label1668; }
              byteBuffer[(bytePos++)] = 14;
              byteBuffer[(bytePos++)] = ((byte)(curUC >>> 8));
              byteBuffer[(bytePos++)] = ((byte)(curUC & 0xFF));

            }
            else
            {

              if (bytePos + 3 >= byteBufferLimit) {
                ucPos--;
                break label1668; }
              byteBuffer[(bytePos++)] = 15;
              
              hiByte = curUC >>> 8;
              loByte = curUC & 0xFF;
              
              if (sUnicodeTagTable[hiByte] != 0)
              {
                byteBuffer[(bytePos++)] = -16;
              }
              byteBuffer[(bytePos++)] = ((byte)hiByte);
              byteBuffer[(bytePos++)] = ((byte)loByte);
              
              fMode = 1;


            }
            


          }
          else if ((whichWindow = findDynamicWindow(curUC)) != -1)
          {

            if (ucPos + 1 < charBufferLimit) {
              forwardUC = charBuffer[(ucPos + 1)];
            } else {
              forwardUC = -1;
            }
            


            if ((inDynamicWindow(nextUC, whichWindow)) && (inDynamicWindow(forwardUC, whichWindow)))
            {



              if (bytePos + 1 >= byteBufferLimit) {
                ucPos--;
                break label1668; }
              byteBuffer[(bytePos++)] = ((byte)(16 + whichWindow));
              byteBuffer[(bytePos++)] = ((byte)(curUC - fOffsets[whichWindow] + 128));
              

              fTimeStamps[whichWindow] = (++fTimeStamp);
              fCurrentWindow = whichWindow;



            }
            else
            {


              if (bytePos + 1 >= byteBufferLimit) {
                ucPos--;
                break label1668; }
              byteBuffer[(bytePos++)] = ((byte)(1 + whichWindow));
              byteBuffer[(bytePos++)] = ((byte)(curUC - fOffsets[whichWindow] + 128));



            }
            



          }
          else if (((whichWindow = findStaticWindow(curUC)) != -1) && (!inStaticWindow(nextUC, whichWindow)))
          {




            if (bytePos + 1 >= byteBufferLimit) {
              ucPos--;
              break label1668; }
            byteBuffer[(bytePos++)] = ((byte)(1 + whichWindow));
            byteBuffer[(bytePos++)] = ((byte)(curUC - sOffsets[whichWindow]));


          }
          else
          {


            curIndex = makeIndex(curUC);
            fIndexCount[curIndex] += 1;
            

            if (ucPos + 1 < charBufferLimit) {
              forwardUC = charBuffer[(ucPos + 1)];
            } else {
              forwardUC = -1;
            }
            





            if ((fIndexCount[curIndex] > 1) || ((curIndex == makeIndex(nextUC)) && (curIndex == makeIndex(forwardUC))))
            {




              if (bytePos + 2 >= byteBufferLimit) {
                ucPos--;
                break label1668;
              }
              whichWindow = getLRDefinedWindow();
              
              byteBuffer[(bytePos++)] = ((byte)(24 + whichWindow));
              byteBuffer[(bytePos++)] = ((byte)curIndex);
              byteBuffer[(bytePos++)] = ((byte)(curUC - sOffsetTable[curIndex] + 128));
              


              fOffsets[whichWindow] = sOffsetTable[curIndex];
              fCurrentWindow = whichWindow;
              fTimeStamps[whichWindow] = (++fTimeStamp);





            }
            else
            {




              if (bytePos + 3 >= byteBufferLimit) {
                ucPos--;
                break label1668; }
              byteBuffer[(bytePos++)] = 15;
              
              hiByte = curUC >>> 8;
              loByte = curUC & 0xFF;
              
              if (sUnicodeTagTable[hiByte] != 0)
              {
                byteBuffer[(bytePos++)] = -16;
              }
              byteBuffer[(bytePos++)] = ((byte)hiByte);
              byteBuffer[(bytePos++)] = ((byte)loByte);
              
              fMode = 1;
              break;
              







              while ((ucPos < charBufferLimit) && (bytePos < byteBufferLimit))
              {
                curUC = charBuffer[(ucPos++)];
                

                if (ucPos < charBufferLimit) {
                  nextUC = charBuffer[ucPos];
                } else {
                  nextUC = -1;
                }
                

                if ((!isCompressible(curUC)) || ((nextUC != -1) && (!isCompressible(nextUC))))
                {


                  if (bytePos + 2 >= byteBufferLimit) {
                    ucPos--;
                    break label1668; }
                  hiByte = curUC >>> 8;
                  loByte = curUC & 0xFF;
                  
                  if (sUnicodeTagTable[hiByte] != 0)
                  {
                    byteBuffer[(bytePos++)] = -16;
                  }
                  byteBuffer[(bytePos++)] = ((byte)hiByte);
                  byteBuffer[(bytePos++)] = ((byte)loByte);



                }
                else if (curUC < 128) {
                  loByte = curUC & 0xFF;
                  



                  if ((nextUC != -1) && (nextUC < 128) && (sSingleTagTable[loByte] == 0))
                  {



                    if (bytePos + 1 >= byteBufferLimit) {
                      ucPos--;
                      break label1668;
                    }
                    whichWindow = fCurrentWindow;
                    byteBuffer[(bytePos++)] = ((byte)(224 + whichWindow));
                    byteBuffer[(bytePos++)] = ((byte)loByte);
                    

                    fTimeStamps[whichWindow] = (++fTimeStamp);
                    fMode = 0;
                    break;
                  }
                  







                  if (bytePos + 1 >= byteBufferLimit) {
                    ucPos--;
                    
                    break label1668;
                  }
                  
                  byteBuffer[(bytePos++)] = 0;
                  byteBuffer[(bytePos++)] = ((byte)loByte);



                }
                else if ((whichWindow = findDynamicWindow(curUC)) != -1)
                {



                  if (inDynamicWindow(nextUC, whichWindow))
                  {


                    if (bytePos + 1 >= byteBufferLimit) {
                      ucPos--;
                      break label1668; }
                    byteBuffer[(bytePos++)] = ((byte)(224 + whichWindow));
                    byteBuffer[(bytePos++)] = ((byte)(curUC - fOffsets[whichWindow] + 128));
                    


                    fTimeStamps[whichWindow] = (++fTimeStamp);
                    fCurrentWindow = whichWindow;
                    fMode = 0;
                    break;
                  }
                  






                  if (bytePos + 2 >= byteBufferLimit) {
                    ucPos--;
                    break label1668; }
                  hiByte = curUC >>> 8;
                  loByte = curUC & 0xFF;
                  
                  if (sUnicodeTagTable[hiByte] != 0)
                  {
                    byteBuffer[(bytePos++)] = -16;
                  }
                  byteBuffer[(bytePos++)] = ((byte)hiByte);
                  byteBuffer[(bytePos++)] = ((byte)loByte);


                }
                else
                {

                  curIndex = makeIndex(curUC);
                  fIndexCount[curIndex] += 1;
                  

                  if (ucPos + 1 < charBufferLimit) {
                    forwardUC = charBuffer[(ucPos + 1)];
                  } else {
                    forwardUC = -1;
                  }
                  






                  if ((fIndexCount[curIndex] > 1) || ((curIndex == makeIndex(nextUC)) && (curIndex == makeIndex(forwardUC))))
                  {





                    if (bytePos + 2 >= byteBufferLimit) {
                      ucPos--;
                      break label1668;
                    }
                    whichWindow = getLRDefinedWindow();
                    
                    byteBuffer[(bytePos++)] = ((byte)(232 + whichWindow));
                    byteBuffer[(bytePos++)] = ((byte)curIndex);
                    byteBuffer[(bytePos++)] = ((byte)(curUC - sOffsetTable[curIndex] + 128));
                    


                    fOffsets[whichWindow] = sOffsetTable[curIndex];
                    fCurrentWindow = whichWindow;
                    fTimeStamps[whichWindow] = (++fTimeStamp);
                    fMode = 0;
                    break;
                  }
                  







                  if (bytePos + 2 >= byteBufferLimit) {
                    ucPos--;
                    break label1668; }
                  hiByte = curUC >>> 8;
                  loByte = curUC & 0xFF;
                  
                  if (sUnicodeTagTable[hiByte] != 0)
                  {
                    byteBuffer[(bytePos++)] = -16;
                  }
                  byteBuffer[(bytePos++)] = ((byte)hiByte);
                  byteBuffer[(bytePos++)] = ((byte)loByte);
                }
              }
            }
          }
        }
      }
    label1668:
    if (charsRead != null) {
      charsRead[0] = (ucPos - charBufferStart);
    }
    
    return bytePos - byteBufferStart;
  }
  







  public void reset()
  {
    fOffsets[0] = 128;
    fOffsets[1] = 192;
    fOffsets[2] = 1024;
    fOffsets[3] = 1536;
    fOffsets[4] = 2304;
    fOffsets[5] = 12352;
    fOffsets[6] = 12448;
    fOffsets[7] = 65280;
    


    for (int i = 0; i < 8; i++) {
      fTimeStamps[i] = 0;
    }
    

    for (i = 0; i <= 255; i++) {
      fIndexCount[i] = 0;
    }
    
    fTimeStamp = 0;
    fCurrentWindow = 0;
    fMode = 0;
  }
  












  private static int makeIndex(int c)
  {
    if ((c >= 192) && (c < 320))
      return 249;
    if ((c >= 592) && (c < 720))
      return 250;
    if ((c >= 880) && (c < 1008))
      return 251;
    if ((c >= 1328) && (c < 1424))
      return 252;
    if ((c >= 12352) && (c < 12448))
      return 253;
    if ((c >= 12448) && (c < 12576))
      return 254;
    if ((c >= 65376) && (c < 65439)) {
      return 255;
    }
    
    if ((c >= 128) && (c < 13312))
      return c / 128 & 0xFF;
    if ((c >= 57344) && (c <= 65535)) {
      return (c - 44032) / 128 & 0xFF;
    }
    

    return 0;
  }
  













  private boolean inDynamicWindow(int c, int whichWindow)
  {
    return (c >= fOffsets[whichWindow]) && (c < fOffsets[whichWindow] + 128);
  }
  









  private static boolean inStaticWindow(int c, int whichWindow)
  {
    return (c >= sOffsets[whichWindow]) && (c < sOffsets[whichWindow] + 128);
  }
  










  private static boolean isCompressible(int c)
  {
    return (c < 13312) || (c >= 57344);
  }
  












  private int findDynamicWindow(int c)
  {
    for (int i = 7; i >= 0; i--) {
      if (inDynamicWindow(c, i)) {
        fTimeStamps[i] += 1;
        return i;
      }
    }
    
    return -1;
  }
  








  private static int findStaticWindow(int c)
  {
    for (int i = 7; i >= 0; i--) {
      if (inStaticWindow(c, i)) {
        return i;
      }
    }
    
    return -1;
  }
  





  private int getLRDefinedWindow()
  {
    int leastRU = Integer.MAX_VALUE;
    int whichWindow = -1;
    



    for (int i = 7; i >= 0; i--) {
      if (fTimeStamps[i] < leastRU) {
        leastRU = fTimeStamps[i];
        whichWindow = i;
      }
    }
    
    return whichWindow;
  }
}
