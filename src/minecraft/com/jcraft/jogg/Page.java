package com.jcraft.jogg;


























public class Page
{
  private static int[] crc_lookup = new int['Ā'];
  
  static { for (int i = 0; i < crc_lookup.length; i++) {
      crc_lookup[i] = crc_entry(i);
    }
  }
  
  private static int crc_entry(int index) {
    int r = index << 24;
    for (int i = 0; i < 8; i++) {
      if ((r & 0x80000000) != 0) {
        r = r << 1 ^ 0x4C11DB7;

      }
      else
      {

        r <<= 1;
      }
    }
    return r & 0xFFFFFFFF;
  }
  
  public byte[] header_base;
  public int header;
  public int header_len;
  public byte[] body_base;
  public int body;
  public int body_len;
  int version()
  {
    return header_base[(header + 4)] & 0xFF;
  }
  
  int continued() {
    return header_base[(header + 5)] & 0x1;
  }
  
  public int bos() {
    return header_base[(header + 5)] & 0x2;
  }
  
  public int eos() {
    return header_base[(header + 5)] & 0x4;
  }
  
  public long granulepos() {
    long foo = header_base[(header + 13)] & 0xFF;
    foo = foo << 8 | header_base[(header + 12)] & 0xFF;
    foo = foo << 8 | header_base[(header + 11)] & 0xFF;
    foo = foo << 8 | header_base[(header + 10)] & 0xFF;
    foo = foo << 8 | header_base[(header + 9)] & 0xFF;
    foo = foo << 8 | header_base[(header + 8)] & 0xFF;
    foo = foo << 8 | header_base[(header + 7)] & 0xFF;
    foo = foo << 8 | header_base[(header + 6)] & 0xFF;
    return foo;
  }
  
  public int serialno() {
    return header_base[(header + 14)] & 0xFF | (header_base[(header + 15)] & 0xFF) << 8 | (header_base[(header + 16)] & 0xFF) << 16 | (header_base[(header + 17)] & 0xFF) << 24;
  }
  

  int pageno()
  {
    return header_base[(header + 18)] & 0xFF | (header_base[(header + 19)] & 0xFF) << 8 | (header_base[(header + 20)] & 0xFF) << 16 | (header_base[(header + 21)] & 0xFF) << 24;
  }
  

  void checksum()
  {
    int crc_reg = 0;
    
    for (int i = 0; i < header_len; i++) {
      crc_reg = crc_reg << 8 ^ crc_lookup[(crc_reg >>> 24 & 0xFF ^ header_base[(header + i)] & 0xFF)];
    }
    
    for (int i = 0; i < body_len; i++) {
      crc_reg = crc_reg << 8 ^ crc_lookup[(crc_reg >>> 24 & 0xFF ^ body_base[(body + i)] & 0xFF)];
    }
    
    header_base[(header + 22)] = ((byte)crc_reg);
    header_base[(header + 23)] = ((byte)(crc_reg >>> 8));
    header_base[(header + 24)] = ((byte)(crc_reg >>> 16));
    header_base[(header + 25)] = ((byte)(crc_reg >>> 24));
  }
  
  public Page copy() {
    return copy(new Page());
  }
  
  public Page copy(Page p) {
    byte[] tmp = new byte[header_len];
    System.arraycopy(header_base, header, tmp, 0, header_len);
    header_len = header_len;
    header_base = tmp;
    header = 0;
    tmp = new byte[body_len];
    System.arraycopy(body_base, body, tmp, 0, body_len);
    body_len = body_len;
    body_base = tmp;
    body = 0;
    return p;
  }
  
  public Page() {}
}
