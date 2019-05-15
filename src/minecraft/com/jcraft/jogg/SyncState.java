package com.jcraft.jogg;






public class SyncState
{
  public byte[] data;
  




  int storage;
  




  int fill;
  




  int returned;
  



  int unsynced;
  



  int headerbytes;
  



  int bodybytes;
  




  public SyncState() {}
  




  public int clear()
  {
    data = null;
    return 0;
  }
  
  public int buffer(int size)
  {
    if (returned != 0) {
      fill -= returned;
      if (fill > 0) {
        System.arraycopy(data, returned, data, 0, fill);
      }
      returned = 0;
    }
    
    if (size > storage - fill)
    {
      int newsize = size + fill + 4096;
      if (data != null) {
        byte[] foo = new byte[newsize];
        System.arraycopy(data, 0, foo, 0, data.length);
        data = foo;
      }
      else {
        data = new byte[newsize];
      }
      storage = newsize;
    }
    
    return fill;
  }
  
  public int wrote(int bytes) {
    if (fill + bytes > storage)
      return -1;
    fill += bytes;
    return 0;
  }
  







  private Page pageseek = new Page();
  private byte[] chksum = new byte[4];
  
  public int pageseek(Page og) {
    int page = returned;
    
    int bytes = fill - returned;
    
    if (headerbytes == 0)
    {
      if (bytes < 27) {
        return 0;
      }
      
      if ((data[page] != 79) || (data[(page + 1)] != 103) || (data[(page + 2)] != 103) || (data[(page + 3)] != 83))
      {
        headerbytes = 0;
        bodybytes = 0;
        

        int next = 0;
        for (int ii = 0; ii < bytes - 1; ii++) {
          if (data[(page + 1 + ii)] == 79) {
            next = page + 1 + ii;
            break;
          }
        }
        
        if (next == 0) {
          next = fill;
        }
        returned = next;
        return -(next - page);
      }
      int _headerbytes = (data[(page + 26)] & 0xFF) + 27;
      if (bytes < _headerbytes) {
        return 0;
      }
      

      for (int i = 0; i < (data[(page + 26)] & 0xFF); i++) {
        bodybytes += (data[(page + 27 + i)] & 0xFF);
      }
      headerbytes = _headerbytes;
    }
    
    if (bodybytes + headerbytes > bytes) {
      return 0;
    }
    
    synchronized (chksum)
    {

      System.arraycopy(data, page + 22, chksum, 0, 4);
      data[(page + 22)] = 0;
      data[(page + 23)] = 0;
      data[(page + 24)] = 0;
      data[(page + 25)] = 0;
      

      Page log = pageseek;
      header_base = data;
      header = page;
      header_len = headerbytes;
      
      body_base = data;
      body = (page + headerbytes);
      body_len = bodybytes;
      log.checksum();
      

      if ((chksum[0] != data[(page + 22)]) || (chksum[1] != data[(page + 23)]) || (chksum[2] != data[(page + 24)]) || (chksum[3] != data[(page + 25)]))
      {


        System.arraycopy(chksum, 0, data, page + 22, 4);
        

        headerbytes = 0;
        bodybytes = 0;
        
        int next = 0;
        for (int ii = 0; ii < bytes - 1; ii++) {
          if (data[(page + 1 + ii)] == 79) {
            next = page + 1 + ii;
            break;
          }
        }
        
        if (next == 0)
          next = fill;
        returned = next;
        return -(next - page);
      }
    }
    


    page = returned;
    
    if (og != null) {
      header_base = data;
      header = page;
      header_len = headerbytes;
      body_base = data;
      body = (page + headerbytes);
      body_len = bodybytes;
    }
    
    unsynced = 0;
    returned += (bytes = headerbytes + bodybytes);
    headerbytes = 0;
    bodybytes = 0;
    return bytes;
  }
  














  public int pageout(Page og)
  {
    for (;;)
    {
      int ret = pageseek(og);
      if (ret > 0)
      {
        return 1;
      }
      if (ret == 0)
      {
        return 0;
      }
      

      if (unsynced == 0) {
        unsynced = 1;
        return -1;
      }
    }
  }
  

  public int reset()
  {
    fill = 0;
    returned = 0;
    unsynced = 0;
    headerbytes = 0;
    bodybytes = 0;
    return 0;
  }
  
  public void init() {}
  
  public int getDataOffset()
  {
    return returned;
  }
  
  public int getBufferOffset() {
    return fill;
  }
}
