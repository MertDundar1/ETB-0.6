package com.jcraft.jorbis;

import com.jcraft.jogg.Buffer;






class Mapping0
  extends FuncMapping
{
  class LookMapping0
  {
    InfoMode mode;
    Mapping0.InfoMapping0 map;
    Object[] time_look;
    Object[] floor_look;
    Object[] floor_state;
    Object[] residue_look;
    PsyLook[] psy_look;
    FuncTime[] time_func;
    FuncFloor[] floor_func;
    FuncResidue[] residue_func;
    int ch;
    float[][] decay;
    int lastframe;
    
    LookMapping0() {}
  }
  
  static int seq = 0;
  
  float[][] pcmbundle;
  
  int[] zerobundle;
  int[] nonzero;
  Object[] floormemo;
  
  Object look(DspState vd, InfoMode vm, Object m)
  {
    Info vi = vi;
    LookMapping0 look = new LookMapping0();
    InfoMapping0 info = look.map = (InfoMapping0)m;
    mode = vm;
    
    time_look = new Object[submaps];
    floor_look = new Object[submaps];
    residue_look = new Object[submaps];
    
    time_func = new FuncTime[submaps];
    floor_func = new FuncFloor[submaps];
    residue_func = new FuncResidue[submaps];
    
    for (int i = 0; i < submaps; i++) {
      int timenum = timesubmap[i];
      int floornum = floorsubmap[i];
      int resnum = residuesubmap[i];
      
      time_func[i] = FuncTime.time_P[time_type[timenum]];
      time_look[i] = time_func[i].look(vd, vm, time_param[timenum]);
      floor_func[i] = FuncFloor.floor_P[floor_type[floornum]];
      floor_look[i] = floor_func[i].look(vd, vm, floor_param[floornum]);
      
      residue_func[i] = FuncResidue.residue_P[residue_type[resnum]];
      residue_look[i] = residue_func[i].look(vd, vm, residue_param[resnum]);
    }
    


    if ((psys != 0) && (analysisp != 0)) {}
    


    ch = channels;
    
    return look;
  }
  
  void pack(Info vi, Object imap, Buffer opb) {
    InfoMapping0 info = (InfoMapping0)imap;
    







    if (submaps > 1) {
      opb.write(1, 1);
      opb.write(submaps - 1, 4);
    }
    else {
      opb.write(0, 1);
    }
    
    if (coupling_steps > 0) {
      opb.write(1, 1);
      opb.write(coupling_steps - 1, 8);
      for (int i = 0; i < coupling_steps; i++) {
        opb.write(coupling_mag[i], Util.ilog2(channels));
        opb.write(coupling_ang[i], Util.ilog2(channels));
      }
    }
    else {
      opb.write(0, 1);
    }
    
    opb.write(0, 2);
    

    if (submaps > 1) {
      for (int i = 0; i < channels; i++)
        opb.write(chmuxlist[i], 4);
    }
    for (int i = 0; i < submaps; i++) {
      opb.write(timesubmap[i], 8);
      opb.write(floorsubmap[i], 8);
      opb.write(residuesubmap[i], 8);
    }
  }
  
  Object unpack(Info vi, Buffer opb)
  {
    InfoMapping0 info = new InfoMapping0();
    
    if (opb.read(1) != 0) {
      submaps = (opb.read(4) + 1);
    }
    else {
      submaps = 1;
    }
    
    if (opb.read(1) != 0) {
      coupling_steps = (opb.read(8) + 1);
      
      for (int i = 0; i < coupling_steps; i++) {
        int testM = coupling_mag[i] = opb.read(Util.ilog2(channels));
        int testA = coupling_ang[i] = opb.read(Util.ilog2(channels));
        
        if ((testM < 0) || (testA < 0) || (testM == testA) || (testM >= channels) || (testA >= channels))
        {

          info.free();
          return null;
        }
      }
    }
    
    if (opb.read(2) > 0) {
      info.free();
      return null;
    }
    
    if (submaps > 1) {
      for (int i = 0; i < channels; i++) {
        chmuxlist[i] = opb.read(4);
        if (chmuxlist[i] >= submaps) {
          info.free();
          return null;
        }
      }
    }
    
    for (int i = 0; i < submaps; i++) {
      timesubmap[i] = opb.read(8);
      if (timesubmap[i] >= times) {
        info.free();
        return null;
      }
      floorsubmap[i] = opb.read(8);
      if (floorsubmap[i] >= floors) {
        info.free();
        return null;
      }
      residuesubmap[i] = opb.read(8);
      if (residuesubmap[i] >= residues) {
        info.free();
        return null;
      }
    }
    return info;
  }
  
  Mapping0() { pcmbundle = ((float[][])null);
    zerobundle = null;
    nonzero = null;
    floormemo = null;
  }
  
  synchronized int inverse(Block vb, Object l) { DspState vd = vd;
    Info vi = vi;
    LookMapping0 look = (LookMapping0)l;
    InfoMapping0 info = map;
    InfoMode mode = mode;
    int n = vb.pcmend = blocksizes[W];
    
    float[] window = window[W][lW][nW][windowtype];
    if ((pcmbundle == null) || (pcmbundle.length < channels)) {
      pcmbundle = new float[channels][];
      nonzero = new int[channels];
      zerobundle = new int[channels];
      floormemo = new Object[channels];
    }
    






    for (int i = 0; i < channels; i++) {
      float[] pcm = pcm[i];
      int submap = chmuxlist[i];
      
      floormemo[i] = floor_func[submap].inverse1(vb, floor_look[submap], floormemo[i]);
      
      if (floormemo[i] != null) {
        nonzero[i] = 1;
      }
      else {
        nonzero[i] = 0;
      }
      for (int j = 0; j < n / 2; j++) {
        pcm[j] = 0.0F;
      }
    }
    

    for (int i = 0; i < coupling_steps; i++) {
      if ((nonzero[coupling_mag[i]] != 0) || (nonzero[coupling_ang[i]] != 0)) {
        nonzero[coupling_mag[i]] = 1;
        nonzero[coupling_ang[i]] = 1;
      }
    }
    


    for (int i = 0; i < submaps; i++) {
      int ch_in_bundle = 0;
      for (int j = 0; j < channels; j++) {
        if (chmuxlist[j] == i) {
          if (nonzero[j] != 0) {
            zerobundle[ch_in_bundle] = 1;
          }
          else {
            zerobundle[ch_in_bundle] = 0;
          }
          pcmbundle[(ch_in_bundle++)] = pcm[j];
        }
      }
      
      residue_func[i].inverse(vb, residue_look[i], pcmbundle, zerobundle, ch_in_bundle);
    }
    

    for (int i = coupling_steps - 1; i >= 0; i--) {
      float[] pcmM = pcm[coupling_mag[i]];
      float[] pcmA = pcm[coupling_ang[i]];
      
      for (int j = 0; j < n / 2; j++) {
        float mag = pcmM[j];
        float ang = pcmA[j];
        
        if (mag > 0.0F) {
          if (ang > 0.0F) {
            pcmM[j] = mag;
            pcmA[j] = (mag - ang);
          }
          else {
            pcmA[j] = mag;
            pcmM[j] = (mag + ang);
          }
          
        }
        else if (ang > 0.0F) {
          pcmM[j] = mag;
          pcmA[j] = (mag + ang);
        }
        else {
          pcmA[j] = mag;
          pcmM[j] = (mag - ang);
        }
      }
    }
    



    for (int i = 0; i < channels; i++) {
      float[] pcm = pcm[i];
      int submap = chmuxlist[i];
      floor_func[submap].inverse2(vb, floor_look[submap], floormemo[i], pcm);
    }
    




    for (int i = 0; i < channels; i++) {
      float[] pcm = pcm[i];
      
      ((Mdct)transform[W][0]).backward(pcm, pcm);
    }
    




    for (int i = 0; i < channels; i++) {
      float[] pcm = pcm[i];
      if (nonzero[i] != 0) {
        for (int j = 0; j < n; j++) {
          pcm[j] *= window[j];
        }
        
      } else {
        for (int j = 0; j < n; j++) {
          pcm[j] = 0.0F;
        }
      }
    }
    



    return 0; }
  
  class InfoMapping0 { int submaps;
    
    InfoMapping0() {}
    int[] chmuxlist = new int['Ā'];
    
    int[] timesubmap = new int[16];
    int[] floorsubmap = new int[16];
    int[] residuesubmap = new int[16];
    int[] psysubmap = new int[16];
    int coupling_steps;
    
    int[] coupling_mag = new int['Ā'];
    int[] coupling_ang = new int['Ā'];
    
    void free() {
      chmuxlist = null;
      timesubmap = null;
      floorsubmap = null;
      residuesubmap = null;
      psysubmap = null;
      
      coupling_mag = null;
      coupling_ang = null;
    }
  }
  
  void free_info(Object imap) {}
  
  void free_look(Object imap) {}
}
