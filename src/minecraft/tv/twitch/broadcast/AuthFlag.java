package tv.twitch.broadcast;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public enum AuthFlag
{
  TTV_AuthOption_None(0), 
  TTV_AuthOption_Broadcast(1), 
  TTV_AuthOption_Chat(2);
  
  static { s_Map = new HashMap();
    


    EnumSet localEnumSet = EnumSet.allOf(AuthFlag.class);
    
    for (AuthFlag localAuthFlag : localEnumSet)
    {
      s_Map.put(Integer.valueOf(localAuthFlag.getValue()), localAuthFlag);
    }
  }
  
  public static AuthFlag lookupValue(int paramInt)
  {
    AuthFlag localAuthFlag = (AuthFlag)s_Map.get(Integer.valueOf(paramInt));
    return localAuthFlag;
  }
  
  public static int getNativeValue(HashSet<AuthFlag> paramHashSet)
  {
    if (paramHashSet == null)
    {
      return TTV_AuthOption_None.getValue();
    }
    
    int i = 0;
    
    for (AuthFlag localAuthFlag : paramHashSet)
    {
      if (localAuthFlag != null)
      {
        i |= localAuthFlag.getValue();
      }
    }
    
    return i;
  }
  
  private static Map<Integer, AuthFlag> s_Map;
  private int m_Value;
  private AuthFlag(int paramInt)
  {
    m_Value = paramInt;
  }
  
  public int getValue()
  {
    return m_Value;
  }
}
