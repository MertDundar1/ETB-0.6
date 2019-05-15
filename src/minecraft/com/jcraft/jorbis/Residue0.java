package com.jcraft.jorbis;

import com.jcraft.jogg.Buffer;






















class Residue0
  extends FuncResidue
{
  Residue0() {}
  
  void pack(Object vr, Buffer opb)
  {
    InfoResidue0 info = (InfoResidue0)vr;
    int acc = 0;
    opb.write(begin, 24);
    opb.write(end, 24);
    
    opb.write(grouping - 1, 24);
    
    opb.write(partitions - 1, 6);
    opb.write(groupbook, 8);
    



    for (int j = 0; j < partitions; j++) {
      int i = secondstages[j];
      if (Util.ilog(i) > 3)
      {
        opb.write(i, 3);
        opb.write(1, 1);
        opb.write(i >>> 3, 5);
      }
      else {
        opb.write(i, 4);
      }
      acc += Util.icount(i);
    }
    for (int j = 0; j < acc; j++) {
      opb.write(booklist[j], 8);
    }
  }
  
  Object unpack(Info vi, Buffer opb) {
    int acc = 0;
    InfoResidue0 info = new InfoResidue0();
    begin = opb.read(24);
    end = opb.read(24);
    grouping = (opb.read(24) + 1);
    partitions = (opb.read(6) + 1);
    groupbook = opb.read(8);
    
    for (int j = 0; j < partitions; j++) {
      int cascade = opb.read(3);
      if (opb.read(1) != 0) {
        cascade |= opb.read(5) << 3;
      }
      secondstages[j] = cascade;
      acc += Util.icount(cascade);
    }
    
    for (int j = 0; j < acc; j++) {
      booklist[j] = opb.read(8);
    }
    
    if (groupbook >= books) {
      free_info(info);
      return null;
    }
    
    for (int j = 0; j < acc; j++) {
      if (booklist[j] >= books) {
        free_info(info);
        return null;
      }
    }
    return info;
  }
  
  Object look(DspState vd, InfoMode vm, Object vr) {
    InfoResidue0 info = (InfoResidue0)vr;
    LookResidue0 look = new LookResidue0();
    int acc = 0;
    
    int maxstage = 0;
    info = info;
    map = mapping;
    
    parts = partitions;
    fullbooks = fullbooks;
    phrasebook = fullbooks[groupbook];
    
    int dim = phrasebook.dim;
    
    partbooks = new int[parts][];
    
    for (int j = 0; j < parts; j++) {
      int i = secondstages[j];
      int stages = Util.ilog(i);
      if (stages != 0) {
        if (stages > maxstage)
          maxstage = stages;
        partbooks[j] = new int[stages];
        for (int k = 0; k < stages; k++) {
          if ((i & 1 << k) != 0) {
            partbooks[j][k] = booklist[(acc++)];
          }
        }
      }
    }
    
    partvals = ((int)Math.rint(Math.pow(parts, dim)));
    stages = maxstage;
    decodemap = new int[partvals][];
    for (int j = 0; j < partvals; j++) {
      int val = j;
      int mult = partvals / parts;
      decodemap[j] = new int[dim];
      
      for (int k = 0; k < dim; k++) {
        int deco = val / mult;
        val -= deco * mult;
        mult /= parts;
        decodemap[j][k] = deco;
      }
    }
    return look;
  }
  






  private static int[][][] _01inverse_partword = new int[2][][];
  
  void free_info(Object i) {}
  
  void free_look(Object i) {}
  
  static synchronized int _01inverse(Block vb, Object vl, float[][] in, int ch, int decodepart) { LookResidue0 look = (LookResidue0)vl;
    InfoResidue0 info = info;
    

    int samples_per_partition = grouping;
    int partitions_per_word = phrasebook.dim;
    int n = end - begin;
    
    int partvals = n / samples_per_partition;
    int partwords = (partvals + partitions_per_word - 1) / partitions_per_word;
    
    if (_01inverse_partword.length < ch) {
      _01inverse_partword = new int[ch][][];
    }
    
    for (int j = 0; j < ch; j++) {
      if ((_01inverse_partword[j] == null) || (_01inverse_partword[j].length < partwords)) {
        _01inverse_partword[j] = new int[partwords][];
      }
    }
    
    for (int s = 0; s < stages; s++)
    {

      int i = 0; for (int l = 0; i < partvals; l++) {
        if (s == 0)
        {
          for (j = 0; j < ch; j++) {
            int temp = phrasebook.decode(opb);
            if (temp == -1) {
              return 0;
            }
            _01inverse_partword[j][l] = decodemap[temp];
            if (_01inverse_partword[j][l] == null) {
              return 0;
            }
          }
        }
        

        for (int k = 0; (k < partitions_per_word) && (i < partvals); i++) {
          for (j = 0; j < ch; j++) {
            int offset = begin + i * samples_per_partition;
            int index = _01inverse_partword[j][l][k];
            if ((secondstages[index] & 1 << s) != 0) {
              CodeBook stagebook = fullbooks[partbooks[index][s]];
              if (stagebook != null) {
                if (decodepart == 0) {
                  if (stagebook.decodevs_add(in[j], offset, opb, samples_per_partition) == -1)
                  {
                    return 0;
                  }
                }
                else if ((decodepart == 1) && 
                  (stagebook.decodev_add(in[j], offset, opb, samples_per_partition) == -1))
                {
                  return 0;
                }
              }
            }
          }
          k++;
        }
      }
    }
    



















    return 0;
  }
  
  static int[][] _2inverse_partword = (int[][])null;
  
  static synchronized int _2inverse(Block vb, Object vl, float[][] in, int ch)
  {
    LookResidue0 look = (LookResidue0)vl;
    InfoResidue0 info = info;
    

    int samples_per_partition = grouping;
    int partitions_per_word = phrasebook.dim;
    int n = end - begin;
    
    int partvals = n / samples_per_partition;
    int partwords = (partvals + partitions_per_word - 1) / partitions_per_word;
    
    if ((_2inverse_partword == null) || (_2inverse_partword.length < partwords)) {
      _2inverse_partword = new int[partwords][];
    }
    for (int s = 0; s < stages; s++) {
      int i = 0; for (int l = 0; i < partvals; l++) {
        if (s == 0)
        {
          int temp = phrasebook.decode(opb);
          if (temp == -1) {
            return 0;
          }
          _2inverse_partword[l] = decodemap[temp];
          if (_2inverse_partword[l] == null) {
            return 0;
          }
        }
        

        for (int k = 0; (k < partitions_per_word) && (i < partvals); i++) {
          int offset = begin + i * samples_per_partition;
          int index = _2inverse_partword[l][k];
          if ((secondstages[index] & 1 << s) != 0) {
            CodeBook stagebook = fullbooks[partbooks[index][s]];
            if ((stagebook != null) && 
              (stagebook.decodevv_add(in, offset, ch, opb, samples_per_partition) == -1))
            {
              return 0;
            }
          }
          k++;
        }
      }
    }
    










    return 0;
  }
  
  int inverse(Block vb, Object vl, float[][] in, int[] nonzero, int ch) {
    int used = 0;
    for (int i = 0; i < ch; i++) {
      if (nonzero[i] != 0) {
        in[(used++)] = in[i];
      }
    }
    if (used != 0) {
      return _01inverse(vb, vl, in, used, 0);
    }
    return 0;
  }
  




  class InfoResidue0
  {
    int begin;
    



    int end;
    


    int grouping;
    


    int partitions;
    


    int groupbook;
    


    int[] secondstages = new int[64];
    int[] booklist = new int['Ā'];
    

    float[] entmax = new float[64];
    float[] ampmax = new float[64];
    int[] subgrp = new int[64];
    int[] blimit = new int[64];
    
    InfoResidue0() {}
  }
  
  class LookResidue0
  {
    Residue0.InfoResidue0 info;
    int map;
    int parts;
    int stages;
    CodeBook[] fullbooks;
    CodeBook phrasebook;
    int[][] partbooks;
    int partvals;
    int[][] decodemap;
    int postbits;
    int phrasebits;
    int frames;
    
    LookResidue0() {}
  }
}
