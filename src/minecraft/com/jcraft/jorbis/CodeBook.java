package com.jcraft.jorbis;

import com.jcraft.jogg.Buffer;


























class CodeBook
{
  int dim;
  int entries;
  StaticCodeBook c;
  float[] valuelist;
  int[] codelist;
  DecodeAux decode_tree;
  private int[] t;
  
  int encode(int a, Buffer b)
  {
    b.write(codelist[a], c.lengthlist[a]);
    return c.lengthlist[a];
  }
  














  int errorv(float[] a)
  {
    int best = best(a, 1);
    for (int k = 0; k < dim; k++) {
      a[k] = valuelist[(best * dim + k)];
    }
    return best;
  }
  
  int encodev(int best, float[] a, Buffer b)
  {
    for (int k = 0; k < dim; k++) {
      a[k] = valuelist[(best * dim + k)];
    }
    return encode(best, b);
  }
  

  int encodevs(float[] a, Buffer b, int step, int addmul)
  {
    int best = besterror(a, step, addmul);
    return encode(best, b);
  }
  
  CodeBook()
  {
    c = new StaticCodeBook();
    
















































    t = new int[15];
  }
  
  synchronized int decodevs_add(float[] a, int offset, Buffer b, int n) { int step = n / dim;
    


    if (t.length < step) {
      t = new int[step];
    }
    
    for (int i = 0; i < step; i++) {
      int entry = decode(b);
      if (entry == -1)
        return -1;
      t[i] = (entry * dim);
    }
    i = 0; for (int o = 0; i < dim; o += step) {
      for (int j = 0; j < step; j++) {
        a[(offset + o + j)] += valuelist[(t[j] + i)];
      }
      i++;
    }
    



    return 0;
  }
  

  int decodev_add(float[] a, int offset, Buffer b, int n)
  {
    int i;
    if (dim > 8) {
      for (i = 0; i < n;) {
        int entry = decode(b);
        if (entry == -1)
          return -1;
        t = entry * dim;
        for (j = 0; j < dim;)
          a[(offset + i++)] += valuelist[(t + j++)];
      }
    }
    int t;
    int j;
    for (int i = 0; i < n;) {
      int entry = decode(b);
      if (entry == -1)
        return -1;
      int t = entry * dim;
      int j = 0;
      switch (dim) {
      case 8: 
        a[(offset + i++)] += valuelist[(t + j++)];
      case 7: 
        a[(offset + i++)] += valuelist[(t + j++)];
      case 6: 
        a[(offset + i++)] += valuelist[(t + j++)];
      case 5: 
        a[(offset + i++)] += valuelist[(t + j++)];
      case 4: 
        a[(offset + i++)] += valuelist[(t + j++)];
      case 3: 
        a[(offset + i++)] += valuelist[(t + j++)];
      case 2: 
        a[(offset + i++)] += valuelist[(t + j++)];
      case 1: 
        a[(offset + i++)] += valuelist[(t + j++)];
      }
      
    }
    

    return 0;
  }
  


  int decodev_set(float[] a, int offset, Buffer b, int n)
  {
    for (int i = 0; i < n;) {
      int entry = decode(b);
      if (entry == -1)
        return -1;
      t = entry * dim;
      for (j = 0; j < dim;)
        a[(offset + i++)] = valuelist[(t + j++)]; }
    int t;
    int j;
    return 0;
  }
  
  int decodevv_add(float[][] a, int offset, int ch, Buffer b, int n)
  {
    int chptr = 0;
    
    for (int i = offset / ch; i < (offset + n) / ch;) {
      int entry = decode(b);
      if (entry == -1) {
        return -1;
      }
      int t = entry * dim;
      for (int j = 0; j < dim; j++) {
        a[(chptr++)][i] += valuelist[(t + j)];
        if (chptr == ch) {
          chptr = 0;
          i++;
        }
      }
    }
    return 0;
  }
  














  int decode(Buffer b)
  {
    int ptr = 0;
    DecodeAux t = decode_tree;
    int lok = b.look(tabn);
    
    if (lok >= 0) {
      ptr = tab[lok];
      b.adv(tabl[lok]);
      if (ptr <= 0) {
        return -ptr;
      }
    }
    do {
      switch (b.read1()) {
      case 0: 
        ptr = ptr0[ptr];
        break;
      case 1: 
        ptr = ptr1[ptr];
        break;
      case -1: 
      default: 
        return -1;
      }
      
    } while (ptr > 0);
    return -ptr;
  }
  
  int decodevs(float[] a, int index, Buffer b, int step, int addmul)
  {
    int entry = decode(b);
    if (entry == -1)
      return -1;
    switch (addmul) {
    case -1: 
      int i = 0; for (int o = 0; i < dim; o += step) {
        a[(index + o)] = valuelist[(entry * dim + i)];i++; }
      break;
    case 0: 
      int i = 0; for (int o = 0; i < dim; o += step) {
        a[(index + o)] += valuelist[(entry * dim + i)];i++; }
      break;
    case 1: 
      int i = 0; for (int o = 0; i < dim; o += step) {
        a[(index + o)] *= valuelist[(entry * dim + i)];i++; }
      break;
    }
    
    
    return entry;
  }
  

  int best(float[] a, int step)
  {
    int besti = -1;
    float best = 0.0F;
    int e = 0;
    for (int i = 0; i < entries; i++) {
      if (c.lengthlist[i] > 0) {
        float _this = dist(dim, valuelist, e, a, step);
        if ((besti == -1) || (_this < best)) {
          best = _this;
          besti = i;
        }
      }
      e += dim;
    }
    return besti;
  }
  

  int besterror(float[] a, int step, int addmul)
  {
    int best = best(a, step);
    switch (addmul) {
    case 0: 
      int i = 0; for (int o = 0; i < dim; o += step) {
        a[o] -= valuelist[(best * dim + i)];i++; }
      break;
    case 1: 
      int i = 0; for (int o = 0; i < dim; o += step) {
        float val = valuelist[(best * dim + i)];
        if (val == 0.0F) {
          a[o] = 0.0F;
        }
        else {
          a[o] /= val;
        }
        i++;
      }
    }
    
    






    return best;
  }
  


  private static float dist(int el, float[] ref, int index, float[] b, int step)
  {
    float acc = 0.0F;
    for (int i = 0; i < el; i++) {
      float val = ref[(index + i)] - b[(i * step)];
      acc += val * val;
    }
    return acc;
  }
  
  int init_decode(StaticCodeBook s) {
    c = s;
    entries = entries;
    dim = dim;
    valuelist = s.unquantize();
    
    decode_tree = make_decode_tree();
    if (decode_tree == null) {
      clear();
      return -1;
    }
    return 0;
  }
  


  static int[] make_words(int[] l, int n)
  {
    int[] marker = new int[33];
    int[] r = new int[n];
    
    for (int i = 0; i < n; i++) {
      int length = l[i];
      if (length > 0) {
        int entry = marker[length];
        






        if ((length < 32) && (entry >>> length != 0))
        {

          return null;
        }
        r[i] = entry;
        



        for (int j = length; j > 0; j--) {
          if ((marker[j] & 0x1) != 0)
          {
            if (j == 1) {
              marker[1] += 1; break;
            }
            marker[j] = (marker[(j - 1)] << 1);
            break;
          }
          
          marker[j] += 1;
        }
        




        for (int j = length + 1; j < 33; j++) {
          if (marker[j] >>> 1 != entry) break;
          entry = marker[j];
          marker[j] = (marker[(j - 1)] << 1);
        }
      }
    }
    






    for (int i = 0; i < n; i++) {
      int temp = 0;
      for (int j = 0; j < l[i]; j++) {
        temp <<= 1;
        temp |= r[i] >>> j & 0x1;
      }
      r[i] = temp;
    }
    
    return r;
  }
  
  DecodeAux make_decode_tree()
  {
    int top = 0;
    DecodeAux t = new DecodeAux();
    int[] ptr0 = t.ptr0 = new int[entries * 2];
    int[] ptr1 = t.ptr1 = new int[entries * 2];
    int[] codelist = make_words(c.lengthlist, c.entries);
    
    if (codelist == null)
      return null;
    aux = (entries * 2);
    
    for (int i = 0; i < entries; i++) {
      if (c.lengthlist[i] > 0) {
        int ptr = 0;
        
        for (int j = 0; j < c.lengthlist[i] - 1; j++) {
          int bit = codelist[i] >>> j & 0x1;
          if (bit == 0) {
            if (ptr0[ptr] == 0) {
              ptr0[ptr] = (++top);
            }
            ptr = ptr0[ptr];
          }
          else {
            if (ptr1[ptr] == 0) {
              ptr1[ptr] = (++top);
            }
            ptr = ptr1[ptr];
          }
        }
        
        if ((codelist[i] >>> j & 0x1) == 0) {
          ptr0[ptr] = (-i);
        }
        else {
          ptr1[ptr] = (-i);
        }
      }
    }
    

    tabn = (Util.ilog(entries) - 4);
    
    if (tabn < 5)
      tabn = 5;
    int n = 1 << tabn;
    tab = new int[n];
    tabl = new int[n];
    for (int i = 0; i < n; i++) {
      int p = 0;
      int j = 0;
      for (j = 0; (j < tabn) && ((p > 0) || (j == 0)); j++) {
        if ((i & 1 << j) != 0) {
          p = ptr1[p];
        }
        else {
          p = ptr0[p];
        }
      }
      tab[i] = p;
      tabl[i] = j;
    }
    
    return t;
  }
  
  void clear() {}
  
  class DecodeAux
  {
    int[] tab;
    int[] tabl;
    int tabn;
    int[] ptr0;
    int[] ptr1;
    int aux;
    
    DecodeAux() {}
  }
}
