package ch.qos.logback.core.rolling.helper;





public class TokenConverter
{
  static final int IDENTITY = 0;
  



  static final int INTEGER = 1;
  



  static final int DATE = 1;
  



  int type;
  



  TokenConverter next;
  




  protected TokenConverter(int t)
  {
    type = t;
  }
  
  public TokenConverter getNext() {
    return next;
  }
  
  public void setNext(TokenConverter next) {
    this.next = next;
  }
  
  public int getType() {
    return type;
  }
  
  public void setType(int i) {
    type = i;
  }
}
