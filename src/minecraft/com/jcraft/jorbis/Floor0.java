package com.jcraft.jorbis;

import com.jcraft.jogg.Buffer;























class Floor0
  extends FuncFloor
{
  float[] lsp;
  
  void pack(Object i, Buffer opb)
  {
    InfoFloor0 info = (InfoFloor0)i;
    opb.write(order, 8);
    opb.write(rate, 16);
    opb.write(barkmap, 16);
    opb.write(ampbits, 6);
    opb.write(ampdB, 8);
    opb.write(numbooks - 1, 4);
    for (int j = 0; j < numbooks; j++)
      opb.write(books[j], 8);
  }
  
  Object unpack(Info vi, Buffer opb) {
    InfoFloor0 info = new InfoFloor0();
    order = opb.read(8);
    rate = opb.read(16);
    barkmap = opb.read(16);
    ampbits = opb.read(6);
    ampdB = opb.read(8);
    numbooks = (opb.read(4) + 1);
    
    if ((order < 1) || (rate < 1) || (barkmap < 1) || (numbooks < 1)) {
      return null;
    }
    
    for (int j = 0; j < numbooks; j++) {
      books[j] = opb.read(8);
      if ((books[j] < 0) || (books[j] >= books)) {
        return null;
      }
    }
    return info;
  }
  
  Object look(DspState vd, InfoMode mi, Object i)
  {
    Info vi = vi;
    InfoFloor0 info = (InfoFloor0)i;
    LookFloor0 look = new LookFloor0();
    m = order;
    n = (blocksizes[blockflag] / 2);
    ln = barkmap;
    vi = info;
    lpclook.init(ln, m);
    

    float scale = ln / toBARK((float)(rate / 2.0D));
    






    linearmap = new int[n];
    for (int j = 0; j < n; j++) {
      int val = (int)Math.floor(toBARK((float)(rate / 2.0D / n * j)) * scale);
      if (val >= ln)
        val = ln;
      linearmap[j] = val;
    }
    return look;
  }
  
  static float toBARK(float f) {
    return (float)(13.1D * Math.atan(7.4E-4D * f) + 2.24D * Math.atan(f * f * 1.85E-8D) + 1.0E-4D * f);
  }
  
  Object state(Object i) {
    EchstateFloor0 state = new EchstateFloor0();
    InfoFloor0 info = (InfoFloor0)i;
    

    codewords = new int[order];
    curve = new float[barkmap];
    frameno = -1L;
    return state;
  }
  








  int forward(Block vb, Object i, float[] in, float[] out, Object vs)
  {
    return 0;
  }
  
  Floor0() { lsp = null; }
  
  int inverse(Block vb, Object i, float[] out)
  {
    LookFloor0 look = (LookFloor0)i;
    InfoFloor0 info = vi;
    int ampraw = opb.read(ampbits);
    if (ampraw > 0) {
      int maxval = (1 << ampbits) - 1;
      float amp = ampraw / maxval * ampdB;
      int booknum = opb.read(Util.ilog(numbooks));
      
      if ((booknum != -1) && (booknum < numbooks))
      {
        synchronized (this) {
          if ((lsp == null) || (lsp.length < m)) {
            lsp = new float[m];
          }
          else {
            for (int j = 0; j < m; j++) {
              lsp[j] = 0.0F;
            }
          }
          CodeBook b = vd.fullbooks[books[booknum]];
          float last = 0.0F;
          
          for (int j = 0; j < m; j++) {
            out[j] = 0.0F;
          }
          for (int j = 0; j < m; j += dim) {
            if (b.decodevs(lsp, j, opb, 1, -1) == -1) {
              for (int k = 0; k < n; k++)
                out[k] = 0.0F;
              return 0;
            }
          }
          for (int j = 0; j < m;) {
            for (int k = 0; k < dim; j++) {
              lsp[j] += last;k++; }
            last = lsp[(j - 1)];
          }
          
          Lsp.lsp_to_curve(out, linearmap, n, ln, lsp, m, amp, ampdB);
          

          return 1;
        }
      }
    }
    return 0;
  }
  
  Object inverse1(Block vb, Object i, Object memo) {
    LookFloor0 look = (LookFloor0)i;
    InfoFloor0 info = vi;
    float[] lsp = null;
    if ((memo instanceof float[])) {
      lsp = (float[])memo;
    }
    
    int ampraw = opb.read(ampbits);
    if (ampraw > 0) {
      int maxval = (1 << ampbits) - 1;
      float amp = ampraw / maxval * ampdB;
      int booknum = opb.read(Util.ilog(numbooks));
      
      if ((booknum != -1) && (booknum < numbooks)) {
        CodeBook b = vd.fullbooks[books[booknum]];
        float last = 0.0F;
        
        if ((lsp == null) || (lsp.length < m + 1)) {
          lsp = new float[m + 1];
        }
        else {
          for (int j = 0; j < lsp.length; j++) {
            lsp[j] = 0.0F;
          }
        }
        for (int j = 0; j < m; j += dim) {
          if (b.decodev_set(lsp, j, opb, dim) == -1) {
            return null;
          }
        }
        
        for (int j = 0; j < m;) {
          for (int k = 0; k < dim; j++) {
            lsp[j] += last;k++; }
          last = lsp[(j - 1)];
        }
        lsp[m] = amp;
        return lsp;
      }
    }
    return null;
  }
  
  int inverse2(Block vb, Object i, Object memo, float[] out) {
    LookFloor0 look = (LookFloor0)i;
    InfoFloor0 info = vi;
    
    if (memo != null) {
      float[] lsp = (float[])memo;
      float amp = lsp[m];
      
      Lsp.lsp_to_curve(out, linearmap, n, ln, lsp, m, amp, ampdB);
      
      return 1;
    }
    for (int j = 0; j < n; j++) {
      out[j] = 0.0F;
    }
    return 0;
  }
  
  static float fromdB(float x) {
    return (float)Math.exp(x * 0.11512925D);
  }
  
  static void lsp_to_lpc(float[] lsp, float[] lpc, int m) {
    int m2 = m / 2;
    float[] O = new float[m2];
    float[] E = new float[m2];
    
    float[] Ae = new float[m2 + 1];
    float[] Ao = new float[m2 + 1];
    
    float[] Be = new float[m2];
    float[] Bo = new float[m2];
    


    for (int i = 0; i < m2; i++) {
      O[i] = ((float)(-2.0D * Math.cos(lsp[(i * 2)])));
      E[i] = ((float)(-2.0D * Math.cos(lsp[(i * 2 + 1)])));
    }
    

    for (int j = 0; j < m2; j++) {
      Ae[j] = 0.0F;
      Ao[j] = 1.0F;
      Be[j] = 0.0F;
      Bo[j] = 1.0F;
    }
    Ao[j] = 1.0F;
    Ae[j] = 1.0F;
    

    for (i = 1; i < m + 1; i++) { float B;
      float A = B = 0.0F;
      for (j = 0; j < m2; j++) {
        float temp = O[j] * Ao[j] + Ae[j];
        Ae[j] = Ao[j];
        Ao[j] = A;
        A += temp;
        
        temp = E[j] * Bo[j] + Be[j];
        Be[j] = Bo[j];
        Bo[j] = B;
        B += temp;
      }
      lpc[(i - 1)] = ((A + Ao[j] + B - Ae[j]) / 2.0F);
      Ao[j] = A;
      Ae[j] = B;
    }
  }
  

  static void lpc_to_curve(float[] curve, float[] lpc, float amp, LookFloor0 l, String name, int frameno)
  {
    float[] lcurve = new float[Math.max(ln * 2, m * 2 + 2)];
    
    if (amp == 0.0F) {
      for (int j = 0; j < n; j++)
        curve[j] = 0.0F;
      return;
    }
    lpclook.lpc_to_curve(lcurve, lpc, amp);
    
    for (int i = 0; i < n; i++)
      curve[i] = lcurve[linearmap[i]];
  }
  
  void free_info(Object i) {}
  
  class InfoFloor0 {
    int order;
    int rate;
    int barkmap;
    int ampbits;
    int ampdB;
    int numbooks;
    int[] books = new int[16];
    
    InfoFloor0() {}
  }
  
  class LookFloor0 { int n;
    int ln;
    int m;
    int[] linearmap;
    Floor0.InfoFloor0 vi;
    Lpc lpclook = new Lpc();
    
    LookFloor0() {}
  }
  
  void free_look(Object i) {}
  
  void free_state(Object vs) {}
  
  class EchstateFloor0
  {
    int[] codewords;
    float[] curve;
    long frameno;
    long codes;
    
    EchstateFloor0() {}
  }
}
