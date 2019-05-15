package ch.qos.logback.core.util;






class CharSequenceState
{
  final char c;
  




  int occurrences;
  





  public CharSequenceState(char c)
  {
    this.c = c;
    occurrences = 1;
  }
  
  void incrementOccurrences() {
    occurrences += 1;
  }
}
