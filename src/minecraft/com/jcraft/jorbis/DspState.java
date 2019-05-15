package com.jcraft.jorbis;



public class DspState
{
  static final float M_PI = 3.1415927F;
  

  static final int VI_TRANSFORMB = 1;
  

  static final int VI_WINDOWB = 1;
  

  int analysisp;
  

  Info vi;
  

  int modebits;
  

  float[][] pcm;
  
  int pcm_storage;
  
  int pcm_current;
  
  int pcm_returned;
  
  float[] multipliers;
  
  int envelope_storage;
  
  int envelope_current;
  
  int eofflag;
  
  int lW;
  
  int W;
  
  int nW;
  
  int centerW;
  
  long granulepos;
  
  long sequence;
  
  long glue_bits;
  
  long time_bits;
  
  long floor_bits;
  
  long res_bits;
  
  float[][][][][] window;
  
  Object[][] transform;
  
  CodeBook[] fullbooks;
  
  Object[] mode;
  
  byte[] header;
  
  byte[] header1;
  
  byte[] header2;
  

  public DspState()
  {
    transform = new Object[2][];
    window = new float[2][][][][];
    window[0] = new float[2][][][];
    window[0][0] = new float[2][][];
    window[0][1] = new float[2][][];
    window[0][0][0] = new float[2][];
    window[0][0][1] = new float[2][];
    window[0][1][0] = new float[2][];
    window[0][1][1] = new float[2][];
    window[1] = new float[2][][][];
    window[1][0] = new float[2][][];
    window[1][1] = new float[2][][];
    window[1][0][0] = new float[2][];
    window[1][0][1] = new float[2][];
    window[1][1][0] = new float[2][];
    window[1][1][1] = new float[2][];
  }
  
  static float[] window(int type, int window, int left, int right) {
    float[] ret = new float[window];
    switch (type)
    {

    case 0: 
      int leftbegin = window / 4 - left / 2;
      int rightbegin = window - window / 4 - right / 2;
      
      for (int i = 0; i < left; i++) {
        float x = (float)((i + 0.5D) / left * 3.1415927410125732D / 2.0D);
        x = (float)Math.sin(x);
        x *= x;
        x = (float)(x * 1.5707963705062866D);
        x = (float)Math.sin(x);
        ret[(i + leftbegin)] = x;
      }
      
      for (int i = leftbegin + left; i < rightbegin; i++) {
        ret[i] = 1.0F;
      }
      
      for (int i = 0; i < right; i++) {
        float x = (float)((right - i - 0.5D) / right * 3.1415927410125732D / 2.0D);
        x = (float)Math.sin(x);
        x *= x;
        x = (float)(x * 1.5707963705062866D);
        x = (float)Math.sin(x);
        ret[(i + rightbegin)] = x;
      }
      
      break;
    
    default: 
      return null; }
    
    return ret;
  }
  



  int init(Info vi, boolean encp)
  {
    this.vi = vi;
    modebits = Util.ilog2(modes);
    
    transform[0] = new Object[1];
    transform[1] = new Object[1];
    


    transform[0][0] = new Mdct();
    transform[1][0] = new Mdct();
    ((Mdct)transform[0][0]).init(blocksizes[0]);
    ((Mdct)transform[1][0]).init(blocksizes[1]);
    
    window[0][0][0] = new float[1][];
    window[0][0][1] = window[0][0][0];
    window[0][1][0] = window[0][0][0];
    window[0][1][1] = window[0][0][0];
    window[1][0][0] = new float[1][];
    window[1][0][1] = new float[1][];
    window[1][1][0] = new float[1][];
    window[1][1][1] = new float[1][];
    
    for (int i = 0; i < 1; i++) {
      window[0][0][0][i] = window(i, blocksizes[0], blocksizes[0] / 2, blocksizes[0] / 2);
      
      window[1][0][0][i] = window(i, blocksizes[1], blocksizes[0] / 2, blocksizes[0] / 2);
      
      window[1][0][1][i] = window(i, blocksizes[1], blocksizes[0] / 2, blocksizes[1] / 2);
      
      window[1][1][0][i] = window(i, blocksizes[1], blocksizes[1] / 2, blocksizes[0] / 2);
      
      window[1][1][1][i] = window(i, blocksizes[1], blocksizes[1] / 2, blocksizes[1] / 2);
    }
    

    fullbooks = new CodeBook[books];
    for (int i = 0; i < books; i++) {
      fullbooks[i] = new CodeBook();
      fullbooks[i].init_decode(book_param[i]);
    }
    



    pcm_storage = 8192;
    

    pcm = new float[channels][];
    
    for (int i = 0; i < channels; i++) {
      pcm[i] = new float[pcm_storage];
    }
    



    lW = 0;
    W = 0;
    

    centerW = (blocksizes[1] / 2);
    
    pcm_current = centerW;
    

    mode = new Object[modes];
    for (int i = 0; i < modes; i++) {
      int mapnum = mode_param[i].mapping;
      int maptype = map_type[mapnum];
      mode[i] = FuncMapping.mapping_P[maptype].look(this, mode_param[i], map_param[mapnum]);
    }
    
    return 0;
  }
  
  public int synthesis_init(Info vi) {
    init(vi, false);
    
    pcm_returned = centerW;
    centerW -= blocksizes[W] / 4 + blocksizes[lW] / 4;
    granulepos = -1L;
    sequence = -1L;
    return 0;
  }
  
  DspState(Info vi) {
    this();
    init(vi, false);
    
    pcm_returned = centerW;
    centerW -= blocksizes[W] / 4 + blocksizes[lW] / 4;
    granulepos = -1L;
    sequence = -1L;
  }
  





  public int synthesis_blockin(Block vb)
  {
    if ((centerW > vi.blocksizes[1] / 2) && (pcm_returned > 8192))
    {


      int shiftPCM = centerW - vi.blocksizes[1] / 2;
      shiftPCM = pcm_returned < shiftPCM ? pcm_returned : shiftPCM;
      
      pcm_current -= shiftPCM;
      centerW -= shiftPCM;
      pcm_returned -= shiftPCM;
      if (shiftPCM != 0) {
        for (int i = 0; i < vi.channels; i++) {
          System.arraycopy(pcm[i], shiftPCM, pcm[i], 0, pcm_current);
        }
      }
    }
    
    lW = W;
    W = W;
    nW = -1;
    
    glue_bits += glue_bits;
    time_bits += time_bits;
    floor_bits += floor_bits;
    res_bits += res_bits;
    
    if (sequence + 1L != sequence) {
      granulepos = -1L;
    }
    sequence = sequence;
    

    int sizeW = vi.blocksizes[W];
    int _centerW = centerW + vi.blocksizes[lW] / 4 + sizeW / 4;
    int beginW = _centerW - sizeW / 2;
    int endW = beginW + sizeW;
    int beginSl = 0;
    int endSl = 0;
    

    if (endW > pcm_storage)
    {
      pcm_storage = (endW + vi.blocksizes[1]);
      for (int i = 0; i < vi.channels; i++) {
        float[] foo = new float[pcm_storage];
        System.arraycopy(pcm[i], 0, foo, 0, pcm[i].length);
        pcm[i] = foo;
      }
    }
    

    switch (W) {
    case 0: 
      beginSl = 0;
      endSl = vi.blocksizes[0] / 2;
      break;
    case 1: 
      beginSl = vi.blocksizes[1] / 4 - vi.blocksizes[lW] / 4;
      endSl = beginSl + vi.blocksizes[lW] / 2;
    }
    
    
    for (int j = 0; j < vi.channels; j++) {
      int _pcm = beginW;
      
      int i = 0;
      for (i = beginSl; i < endSl; i++) {
        pcm[j][(_pcm + i)] += pcm[j][i];
      }
      for (; 
          i < sizeW; i++) {
        pcm[j][(_pcm + i)] = pcm[j][i];
      }
    }
    











    if (granulepos == -1L) {
      granulepos = granulepos;
    }
    else {
      granulepos += _centerW - centerW;
      if ((granulepos != -1L) && (granulepos != granulepos)) {
        if ((granulepos > granulepos) && (eofflag != 0))
        {
          _centerW = (int)(_centerW - (granulepos - granulepos));
        }
        
        granulepos = granulepos;
      }
    }
    


    centerW = _centerW;
    pcm_current = endW;
    if (eofflag != 0) {
      eofflag = 1;
    }
    return 0;
  }
  
  public int synthesis_pcmout(float[][][] _pcm, int[] index)
  {
    if (pcm_returned < centerW) {
      if (_pcm != null) {
        for (int i = 0; i < vi.channels; i++) {
          index[i] = pcm_returned;
        }
        _pcm[0] = pcm;
      }
      return centerW - pcm_returned;
    }
    return 0;
  }
  
  public int synthesis_read(int bytes) {
    if ((bytes != 0) && (pcm_returned + bytes > centerW))
      return -1;
    pcm_returned += bytes;
    return 0;
  }
  
  public void clear() {}
}
