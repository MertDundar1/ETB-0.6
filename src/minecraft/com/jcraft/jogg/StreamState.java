package com.jcraft.jogg;




public class StreamState
{
  byte[] body_data;
  


  int body_storage;
  


  int body_fill;
  


  private int body_returned;
  


  int[] lacing_vals;
  


  long[] granule_vals;
  


  int lacing_storage;
  

  int lacing_fill;
  

  int lacing_packet;
  

  int lacing_returned;
  

  byte[] header = new byte['Ě'];
  
  int header_fill;
  
  public int e_o_s;
  
  int b_o_s;
  
  int serialno;
  
  int pageno;
  
  long packetno;
  long granulepos;
  
  public StreamState()
  {
    init();
  }
  
  StreamState(int serialno) {
    this();
    init(serialno);
  }
  
  void init() {
    body_storage = 16384;
    body_data = new byte[body_storage];
    lacing_storage = 1024;
    lacing_vals = new int[lacing_storage];
    granule_vals = new long[lacing_storage];
  }
  
  public void init(int serialno) {
    if (body_data == null) {
      init();
    }
    else {
      for (int i = 0; i < body_data.length; i++)
        body_data[i] = 0;
      for (int i = 0; i < lacing_vals.length; i++)
        lacing_vals[i] = 0;
      for (int i = 0; i < granule_vals.length; i++)
        granule_vals[i] = 0L;
    }
    this.serialno = serialno;
  }
  
  public void clear() {
    body_data = null;
    lacing_vals = null;
    granule_vals = null;
  }
  
  void destroy() {
    clear();
  }
  
  void body_expand(int needed) {
    if (body_storage <= body_fill + needed) {
      body_storage += needed + 1024;
      byte[] foo = new byte[body_storage];
      System.arraycopy(body_data, 0, foo, 0, body_data.length);
      body_data = foo;
    }
  }
  
  void lacing_expand(int needed) {
    if (lacing_storage <= lacing_fill + needed) {
      lacing_storage += needed + 32;
      int[] foo = new int[lacing_storage];
      System.arraycopy(lacing_vals, 0, foo, 0, lacing_vals.length);
      lacing_vals = foo;
      
      long[] bar = new long[lacing_storage];
      System.arraycopy(granule_vals, 0, bar, 0, granule_vals.length);
      granule_vals = bar;
    }
  }
  
  public int packetin(Packet op)
  {
    int lacing_val = bytes / 255 + 1;
    
    if (body_returned != 0)
    {



      body_fill -= body_returned;
      if (body_fill != 0) {
        System.arraycopy(body_data, body_returned, body_data, 0, body_fill);
      }
      body_returned = 0;
    }
    

    body_expand(bytes);
    lacing_expand(lacing_val);
    





    System.arraycopy(packet_base, packet, body_data, body_fill, bytes);
    body_fill += bytes;
    


    for (int j = 0; j < lacing_val - 1; j++) {
      lacing_vals[(lacing_fill + j)] = 255;
      granule_vals[(lacing_fill + j)] = granulepos;
    }
    lacing_vals[(lacing_fill + j)] = (bytes % 255);
    granulepos = (granule_vals[(lacing_fill + j)] = granulepos);
    

    lacing_vals[lacing_fill] |= 0x100;
    
    lacing_fill += lacing_val;
    

    packetno += 1L;
    
    if (e_o_s != 0)
      e_o_s = 1;
    return 0;
  }
  




  public int packetout(Packet op)
  {
    int ptr = lacing_returned;
    
    if (lacing_packet <= ptr) {
      return 0;
    }
    
    if ((lacing_vals[ptr] & 0x400) != 0)
    {
      lacing_returned += 1;
      


      packetno += 1L;
      return -1;
    }
    


    int size = lacing_vals[ptr] & 0xFF;
    int bytes = 0;
    
    packet_base = body_data;
    packet = body_returned;
    e_o_s = (lacing_vals[ptr] & 0x200);
    b_o_s = (lacing_vals[ptr] & 0x100);
    bytes += size;
    
    while (size == 255) {
      int val = lacing_vals[(++ptr)];
      size = val & 0xFF;
      if ((val & 0x200) != 0)
        e_o_s = 512;
      bytes += size;
    }
    
    packetno = packetno;
    granulepos = granule_vals[ptr];
    bytes = bytes;
    
    body_returned += bytes;
    
    lacing_returned = (ptr + 1);
    
    packetno += 1L;
    return 1;
  }
  


  public int pagein(Page og)
  {
    byte[] header_base = header_base;
    int header = header;
    byte[] body_base = body_base;
    int body = body;
    int bodysize = body_len;
    int segptr = 0;
    
    int version = og.version();
    int continued = og.continued();
    int bos = og.bos();
    int eos = og.eos();
    long granulepos = og.granulepos();
    int _serialno = og.serialno();
    int _pageno = og.pageno();
    int segments = header_base[(header + 26)] & 0xFF;
    


    int lr = lacing_returned;
    int br = body_returned;
    

    if (br != 0) {
      body_fill -= br;
      if (body_fill != 0) {
        System.arraycopy(body_data, br, body_data, 0, body_fill);
      }
      body_returned = 0;
    }
    
    if (lr != 0)
    {
      if (lacing_fill - lr != 0) {
        System.arraycopy(lacing_vals, lr, lacing_vals, 0, lacing_fill - lr);
        System.arraycopy(granule_vals, lr, granule_vals, 0, lacing_fill - lr);
      }
      lacing_fill -= lr;
      lacing_packet -= lr;
      lacing_returned = 0;
    }
    


    if (_serialno != serialno)
      return -1;
    if (version > 0) {
      return -1;
    }
    lacing_expand(segments + 1);
    

    if (_pageno != pageno)
    {


      for (int i = lacing_packet; i < lacing_fill; i++) {
        body_fill -= (lacing_vals[i] & 0xFF);
      }
      
      lacing_fill = lacing_packet;
      

      if (pageno != -1) {
        lacing_vals[(lacing_fill++)] = 1024;
        lacing_packet += 1;
      }
      


      if (continued != 0) {
        bos = 0;
        for (; segptr < segments; segptr++) {
          int val = header_base[(header + 27 + segptr)] & 0xFF;
          body += val;
          bodysize -= val;
          if (val < 255) {
            segptr++;
            break;
          }
        }
      }
    }
    
    if (bodysize != 0) {
      body_expand(bodysize);
      System.arraycopy(body_base, body, body_data, body_fill, bodysize);
      body_fill += bodysize;
    }
    

    int saved = -1;
    while (segptr < segments) {
      int val = header_base[(header + 27 + segptr)] & 0xFF;
      lacing_vals[lacing_fill] = val;
      granule_vals[lacing_fill] = -1L;
      
      if (bos != 0) {
        lacing_vals[lacing_fill] |= 0x100;
        bos = 0;
      }
      
      if (val < 255) {
        saved = lacing_fill;
      }
      lacing_fill += 1;
      segptr++;
      
      if (val < 255) {
        lacing_packet = lacing_fill;
      }
    }
    
    if (saved != -1) {
      granule_vals[saved] = granulepos;
    }
    

    if (eos != 0) {
      e_o_s = 1;
      if (lacing_fill > 0) {
        lacing_vals[(lacing_fill - 1)] |= 0x200;
      }
    }
    pageno = (_pageno + 1);
    return 0;
  }
  















  public int flush(Page og)
  {
    int vals = 0;
    int maxvals = lacing_fill > 255 ? 255 : lacing_fill;
    int bytes = 0;
    int acc = 0;
    long granule_pos = granule_vals[0];
    
    if (maxvals == 0) {
      return 0;
    }
    




    if (b_o_s == 0) {
      granule_pos = 0L;
      for (vals = 0; vals < maxvals; vals++) {
        if ((lacing_vals[vals] & 0xFF) < 255) {
          vals++;
          break;
        }
      }
    }
    
    for (vals = 0; vals < maxvals; vals++) {
      if (acc > 4096)
        break;
      acc += (lacing_vals[vals] & 0xFF);
      granule_pos = granule_vals[vals];
    }
    


    System.arraycopy("OggS".getBytes(), 0, header, 0, 4);
    

    header[4] = 0;
    

    header[5] = 0;
    if ((lacing_vals[0] & 0x100) == 0) {
      int tmp186_185 = 5; byte[] tmp186_182 = header;tmp186_182[tmp186_185] = ((byte)(tmp186_182[tmp186_185] | 0x1));
    }
    if (b_o_s == 0) {
      int tmp204_203 = 5; byte[] tmp204_200 = header;tmp204_200[tmp204_203] = ((byte)(tmp204_200[tmp204_203] | 0x2));
    }
    if ((e_o_s != 0) && (lacing_fill == vals)) {
      int tmp230_229 = 5; byte[] tmp230_226 = header;tmp230_226[tmp230_229] = ((byte)(tmp230_226[tmp230_229] | 0x4)); }
    b_o_s = 1;
    

    for (int i = 6; i < 14; i++) {
      header[i] = ((byte)(int)granule_pos);
      granule_pos >>>= 8;
    }
    


    int _serialno = serialno;
    for (i = 14; i < 18; i++) {
      header[i] = ((byte)_serialno);
      _serialno >>>= 8;
    }
    



    if (pageno == -1) {
      pageno = 0;
    }
    



    int _pageno = pageno++;
    for (i = 18; i < 22; i++) {
      header[i] = ((byte)_pageno);
      _pageno >>>= 8;
    }
    


    header[22] = 0;
    header[23] = 0;
    header[24] = 0;
    header[25] = 0;
    

    header[26] = ((byte)vals);
    for (i = 0; i < vals; i++) {
      header[(i + 27)] = ((byte)lacing_vals[i]);
      bytes += (header[(i + 27)] & 0xFF);
    }
    

    header_base = header;
    header = 0;
    header_len = (this.header_fill = vals + 27);
    body_base = body_data;
    body = body_returned;
    body_len = bytes;
    


    lacing_fill -= vals;
    System.arraycopy(lacing_vals, vals, lacing_vals, 0, lacing_fill * 4);
    System.arraycopy(granule_vals, vals, granule_vals, 0, lacing_fill * 8);
    body_returned += bytes;
    


    og.checksum();
    

    return 1;
  }
  


  public int pageout(Page og)
  {
    if (((e_o_s != 0) && (lacing_fill != 0)) || (body_fill - body_returned > 4096) || (lacing_fill >= 255) || ((lacing_fill != 0) && (b_o_s == 0)))
    {


      return flush(og);
    }
    return 0;
  }
  
  public int eof() {
    return e_o_s;
  }
  
  public int reset() {
    body_fill = 0;
    body_returned = 0;
    
    lacing_fill = 0;
    lacing_packet = 0;
    lacing_returned = 0;
    
    header_fill = 0;
    
    e_o_s = 0;
    b_o_s = 0;
    pageno = -1;
    packetno = 0L;
    granulepos = 0L;
    return 0;
  }
}
