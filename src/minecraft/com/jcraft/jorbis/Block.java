package com.jcraft.jorbis;

import com.jcraft.jogg.Buffer;
import com.jcraft.jogg.Packet;


























public class Block
{
  float[][] pcm = new float[0][];
  Buffer opb = new Buffer();
  
  int lW;
  
  int W;
  
  int nW;
  int pcmend;
  int mode;
  int eofflag;
  long granulepos;
  long sequence;
  DspState vd;
  int glue_bits;
  int time_bits;
  int floor_bits;
  int res_bits;
  
  public Block(DspState vd)
  {
    this.vd = vd;
    if (analysisp != 0) {
      opb.writeinit();
    }
  }
  
  public void init(DspState vd) {
    this.vd = vd;
  }
  
  public int clear() {
    if ((vd != null) && 
      (vd.analysisp != 0)) {
      opb.writeclear();
    }
    
    return 0;
  }
  
  public int synthesis(Packet op) {
    Info vi = vd.vi;
    

    opb.readinit(packet_base, packet, bytes);
    

    if (opb.read(1) != 0)
    {
      return -1;
    }
    

    int _mode = opb.read(vd.modebits);
    if (_mode == -1) {
      return -1;
    }
    mode = _mode;
    W = mode_param[mode].blockflag;
    if (W != 0) {
      lW = opb.read(1);
      nW = opb.read(1);
      if (nW == -1) {
        return -1;
      }
    } else {
      lW = 0;
      nW = 0;
    }
    

    granulepos = granulepos;
    sequence = (packetno - 3L);
    eofflag = e_o_s;
    

    pcmend = blocksizes[W];
    if (pcm.length < channels) {
      pcm = new float[channels][];
    }
    for (int i = 0; i < channels; i++) {
      if ((pcm[i] == null) || (pcm[i].length < pcmend)) {
        pcm[i] = new float[pcmend];
      }
      else {
        for (int j = 0; j < pcmend; j++) {
          pcm[i][j] = 0.0F;
        }
      }
    }
    

    int type = map_type[mode_param[mode].mapping];
    return FuncMapping.mapping_P[type].inverse(this, vd.mode[mode]);
  }
}
