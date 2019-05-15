package net.minecraft.client.stream;

import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tv.twitch.AuthToken;
import tv.twitch.Core;
import tv.twitch.ErrorCode;
import tv.twitch.StandardCoreAPI;
import tv.twitch.chat.Chat;
import tv.twitch.chat.ChatBadgeData;
import tv.twitch.chat.ChatChannelInfo;
import tv.twitch.chat.ChatEmoticonData;
import tv.twitch.chat.ChatEvent;
import tv.twitch.chat.ChatRawMessage;
import tv.twitch.chat.ChatTokenizationOption;
import tv.twitch.chat.ChatTokenizedMessage;
import tv.twitch.chat.ChatUserInfo;
import tv.twitch.chat.IChatAPIListener;
import tv.twitch.chat.IChatChannelListener;
import tv.twitch.chat.StandardChatAPI;

public class ChatController
{
  private static final Logger LOGGER = ;
  protected ChatListener field_153003_a = null;
  protected String field_153004_b = "";
  protected String field_153006_d = "";
  protected String field_153007_e = "";
  protected Core field_175992_e = null;
  protected Chat field_153008_f = null;
  protected ChatState field_153011_i;
  protected AuthToken field_153012_j;
  protected HashMap field_175998_i;
  protected int field_153015_m;
  protected EnumEmoticonMode field_175997_k;
  protected EnumEmoticonMode field_175995_l;
  protected ChatEmoticonData field_175996_m;
  protected int field_175993_n;
  protected int field_175994_o;
  protected IChatAPIListener field_175999_p;
  private static final String __OBFID = "CL_00001819";
  
  public void func_152990_a(ChatListener p_152990_1_)
  {
    field_153003_a = p_152990_1_;
  }
  
  public void func_152994_a(AuthToken p_152994_1_)
  {
    field_153012_j = p_152994_1_;
  }
  
  public void func_152984_a(String p_152984_1_)
  {
    field_153006_d = p_152984_1_;
  }
  
  public void func_152998_c(String p_152998_1_)
  {
    field_153004_b = p_152998_1_;
  }
  
  public ChatState func_153000_j()
  {
    return field_153011_i;
  }
  
  public boolean func_175990_d(String p_175990_1_)
  {
    if (!field_175998_i.containsKey(p_175990_1_))
    {
      return false;
    }
    

    ChatChannelListener var2 = (ChatChannelListener)field_175998_i.get(p_175990_1_);
    return var2.func_176040_a() == EnumChannelState.Connected;
  }
  

  public EnumChannelState func_175989_e(String p_175989_1_)
  {
    if (!field_175998_i.containsKey(p_175989_1_))
    {
      return EnumChannelState.Disconnected;
    }
    

    ChatChannelListener var2 = (ChatChannelListener)field_175998_i.get(p_175989_1_);
    return var2.func_176040_a();
  }
  

  public ChatController()
  {
    field_153011_i = ChatState.Uninitialized;
    field_153012_j = new AuthToken();
    field_175998_i = new HashMap();
    field_153015_m = 128;
    field_175997_k = EnumEmoticonMode.None;
    field_175995_l = EnumEmoticonMode.None;
    field_175996_m = null;
    field_175993_n = 500;
    field_175994_o = 2000;
    field_175999_p = new IChatAPIListener()
    {
      private static final String __OBFID = "CL_00002373";
      
      public void chatInitializationCallback(ErrorCode p_chatInitializationCallback_1_) {
        if (ErrorCode.succeeded(p_chatInitializationCallback_1_))
        {
          field_153008_f.setMessageFlushInterval(field_175993_n);
          field_153008_f.setUserChangeEventInterval(field_175994_o);
          func_153001_r();
          func_175985_a(ChatController.ChatState.Initialized);
        }
        else
        {
          func_175985_a(ChatController.ChatState.Uninitialized);
        }
        
        try
        {
          if (field_153003_a != null)
          {
            field_153003_a.func_176023_d(p_chatInitializationCallback_1_);
          }
        }
        catch (Exception var3)
        {
          func_152995_h(var3.toString());
        }
      }
      
      public void chatShutdownCallback(ErrorCode p_chatShutdownCallback_1_) {
        if (ErrorCode.succeeded(p_chatShutdownCallback_1_))
        {
          ErrorCode var2 = field_175992_e.shutdown();
          
          if (ErrorCode.failed(var2))
          {
            String var3 = ErrorCode.getString(var2);
            func_152995_h(String.format("Error shutting down the Twitch sdk: %s", new Object[] { var3 }));
          }
          
          func_175985_a(ChatController.ChatState.Uninitialized);
        }
        else
        {
          func_175985_a(ChatController.ChatState.Initialized);
          func_152995_h(String.format("Error shutting down Twith chat: %s", new Object[] { p_chatShutdownCallback_1_ }));
        }
        
        try
        {
          if (field_153003_a != null)
          {
            field_153003_a.func_176022_e(p_chatShutdownCallback_1_);
          }
        }
        catch (Exception var4)
        {
          func_152995_h(var4.toString());
        }
      }
      
      public void chatEmoticonDataDownloadCallback(ErrorCode p_chatEmoticonDataDownloadCallback_1_) {
        if (ErrorCode.succeeded(p_chatEmoticonDataDownloadCallback_1_))
        {
          func_152988_s();
        }
      }
    };
    field_175992_e = Core.getInstance();
    
    if (field_175992_e == null)
    {
      field_175992_e = new Core(new StandardCoreAPI());
    }
    
    field_153008_f = new Chat(new StandardChatAPI());
  }
  
  public boolean func_175984_n()
  {
    if (field_153011_i != ChatState.Uninitialized)
    {
      return false;
    }
    

    func_175985_a(ChatState.Initializing);
    ErrorCode var1 = field_175992_e.initialize(field_153006_d, null);
    
    if (ErrorCode.failed(var1))
    {
      func_175985_a(ChatState.Uninitialized);
      String var4 = ErrorCode.getString(var1);
      func_152995_h(String.format("Error initializing Twitch sdk: %s", new Object[] { var4 }));
      return false;
    }
    

    field_175995_l = field_175997_k;
    HashSet var2 = new HashSet();
    
    switch (SwitchEnumEmoticonMode.field_175975_c[field_175997_k.ordinal()])
    {
    case 1: 
      var2.add(ChatTokenizationOption.TTV_CHAT_TOKENIZATION_OPTION_NONE);
      break;
    
    case 2: 
      var2.add(ChatTokenizationOption.TTV_CHAT_TOKENIZATION_OPTION_EMOTICON_URLS);
      break;
    
    case 3: 
      var2.add(ChatTokenizationOption.TTV_CHAT_TOKENIZATION_OPTION_EMOTICON_TEXTURES);
    }
    
    var1 = field_153008_f.initialize(var2, field_175999_p);
    
    if (ErrorCode.failed(var1))
    {
      field_175992_e.shutdown();
      func_175985_a(ChatState.Uninitialized);
      String var3 = ErrorCode.getString(var1);
      func_152995_h(String.format("Error initializing Twitch chat: %s", new Object[] { var3 }));
      return false;
    }
    

    func_175985_a(ChatState.Initialized);
    return true;
  }
  



  public boolean func_152986_d(String p_152986_1_)
  {
    return func_175987_a(p_152986_1_, false);
  }
  
  protected boolean func_175987_a(String p_175987_1_, boolean p_175987_2_)
  {
    if (field_153011_i != ChatState.Initialized)
    {
      return false;
    }
    if (field_175998_i.containsKey(p_175987_1_))
    {
      func_152995_h("Already in channel: " + p_175987_1_);
      return false;
    }
    if ((p_175987_1_ != null) && (!p_175987_1_.equals("")))
    {
      ChatChannelListener var3 = new ChatChannelListener(p_175987_1_);
      field_175998_i.put(p_175987_1_, var3);
      boolean var4 = var3.func_176038_a(p_175987_2_);
      
      if (!var4)
      {
        field_175998_i.remove(p_175987_1_);
      }
      
      return var4;
    }
    

    return false;
  }
  

  public boolean func_175991_l(String p_175991_1_)
  {
    if (field_153011_i != ChatState.Initialized)
    {
      return false;
    }
    if (!field_175998_i.containsKey(p_175991_1_))
    {
      func_152995_h("Not in channel: " + p_175991_1_);
      return false;
    }
    

    ChatChannelListener var2 = (ChatChannelListener)field_175998_i.get(p_175991_1_);
    return var2.func_176034_g();
  }
  

  public boolean func_152993_m()
  {
    if (field_153011_i != ChatState.Initialized)
    {
      return false;
    }
    

    ErrorCode var1 = field_153008_f.shutdown();
    
    if (ErrorCode.failed(var1))
    {
      String var2 = ErrorCode.getString(var1);
      func_152995_h(String.format("Error shutting down chat: %s", new Object[] { var2 }));
      return false;
    }
    

    func_152996_t();
    func_175985_a(ChatState.ShuttingDown);
    return true;
  }
  


  public void func_175988_p()
  {
    if (func_153000_j() != ChatState.Uninitialized)
    {
      func_152993_m();
      
      if (func_153000_j() == ChatState.ShuttingDown)
      {
        while (func_153000_j() != ChatState.Uninitialized)
        {
          try
          {
            Thread.sleep(200L);
            func_152997_n();
          }
          catch (InterruptedException localInterruptedException) {}
        }
      }
    }
  }
  



  public void func_152997_n()
  {
    if (field_153011_i != ChatState.Uninitialized)
    {
      ErrorCode var1 = field_153008_f.flushEvents();
      
      if (ErrorCode.failed(var1))
      {
        String var2 = ErrorCode.getString(var1);
        func_152995_h(String.format("Error flushing chat events: %s", new Object[] { var2 }));
      }
    }
  }
  
  public boolean func_175986_a(String p_175986_1_, String p_175986_2_)
  {
    if (field_153011_i != ChatState.Initialized)
    {
      return false;
    }
    if (!field_175998_i.containsKey(p_175986_1_))
    {
      func_152995_h("Not in channel: " + p_175986_1_);
      return false;
    }
    

    ChatChannelListener var3 = (ChatChannelListener)field_175998_i.get(p_175986_1_);
    return var3.func_176037_b(p_175986_2_);
  }
  

  protected void func_175985_a(ChatState p_175985_1_)
  {
    if (p_175985_1_ != field_153011_i)
    {
      field_153011_i = p_175985_1_;
      
      try
      {
        if (field_153003_a != null)
        {
          field_153003_a.func_176017_a(p_175985_1_);
        }
      }
      catch (Exception var3)
      {
        func_152995_h(var3.toString());
      }
    }
  }
  
  protected void func_153001_r()
  {
    if (field_175995_l != EnumEmoticonMode.None)
    {
      if (field_175996_m == null)
      {
        ErrorCode var1 = field_153008_f.downloadEmoticonData();
        
        if (ErrorCode.failed(var1))
        {
          String var2 = ErrorCode.getString(var1);
          func_152995_h(String.format("Error trying to download emoticon data: %s", new Object[] { var2 }));
        }
      }
    }
  }
  
  protected void func_152988_s()
  {
    if (field_175996_m == null)
    {
      field_175996_m = new ChatEmoticonData();
      ErrorCode var1 = field_153008_f.getEmoticonData(field_175996_m);
      
      if (ErrorCode.succeeded(var1))
      {
        try
        {
          if (field_153003_a == null)
            return;
          field_153003_a.func_176021_d();

        }
        catch (Exception var3)
        {
          func_152995_h(var3.toString());
        }
        
      }
      else {
        func_152995_h("Error preparing emoticon data: " + ErrorCode.getString(var1));
      }
    }
  }
  
  protected void func_152996_t()
  {
    if (field_175996_m != null)
    {
      ErrorCode var1 = field_153008_f.clearEmoticonData();
      
      if (ErrorCode.succeeded(var1))
      {
        field_175996_m = null;
        
        try
        {
          if (field_153003_a == null)
            return;
          field_153003_a.func_176024_e();

        }
        catch (Exception var3)
        {
          func_152995_h(var3.toString());
        }
      }
      else
      {
        func_152995_h("Error clearing emoticon data: " + ErrorCode.getString(var1));
      }
    }
  }
  
  protected void func_152995_h(String p_152995_1_)
  {
    LOGGER.error(TwitchStream.field_152949_a, "[Chat controller] {}", new Object[] { p_152995_1_ });
  }
  
  public class ChatChannelListener implements IChatChannelListener
  {
    protected String field_176048_a = null;
    protected boolean field_176046_b = false;
    protected ChatController.EnumChannelState field_176047_c;
    protected List field_176044_d;
    protected LinkedList field_176045_e;
    protected LinkedList field_176042_f;
    protected ChatBadgeData field_176043_g;
    private static final String __OBFID = "CL_00002370";
    
    public ChatChannelListener(String p_i46061_2_)
    {
      field_176047_c = ChatController.EnumChannelState.Created;
      field_176044_d = Lists.newArrayList();
      field_176045_e = new LinkedList();
      field_176042_f = new LinkedList();
      field_176043_g = null;
      field_176048_a = p_i46061_2_;
    }
    
    public ChatController.EnumChannelState func_176040_a()
    {
      return field_176047_c;
    }
    
    public boolean func_176038_a(boolean p_176038_1_)
    {
      field_176046_b = p_176038_1_;
      ErrorCode var2 = ErrorCode.TTV_EC_SUCCESS;
      
      if (p_176038_1_)
      {
        var2 = field_153008_f.connectAnonymous(field_176048_a, this);
      }
      else
      {
        var2 = field_153008_f.connect(field_176048_a, field_153004_b, field_153012_j.data, this);
      }
      
      if (ErrorCode.failed(var2))
      {
        String var3 = ErrorCode.getString(var2);
        func_152995_h(String.format("Error connecting: %s", new Object[] { var3 }));
        func_176036_d(field_176048_a);
        return false;
      }
      

      func_176035_a(ChatController.EnumChannelState.Connecting);
      func_176041_h();
      return true;
    }
    

    public boolean func_176034_g()
    {
      switch (ChatController.SwitchEnumEmoticonMode.field_175976_a[field_176047_c.ordinal()])
      {
      case 1: 
      case 2: 
        ErrorCode var1 = field_153008_f.disconnect(field_176048_a);
        
        if (ErrorCode.failed(var1))
        {
          String var2 = ErrorCode.getString(var1);
          func_152995_h(String.format("Error disconnecting: %s", new Object[] { var2 }));
          return false;
        }
        
        func_176035_a(ChatController.EnumChannelState.Disconnecting);
        return true;
      }
      
      


      return false;
    }
    

    protected void func_176035_a(ChatController.EnumChannelState p_176035_1_)
    {
      if (p_176035_1_ != field_176047_c)
      {
        field_176047_c = p_176035_1_;
      }
    }
    
    public void func_176032_a(String p_176032_1_)
    {
      if (field_175995_l == ChatController.EnumEmoticonMode.None)
      {
        field_176045_e.clear();
        field_176042_f.clear();

      }
      else
      {

        if (field_176045_e.size() > 0)
        {
          ListIterator var2 = field_176045_e.listIterator();
          
          while (var2.hasNext())
          {
            ChatRawMessage var3 = (ChatRawMessage)var2.next();
            
            if (userName.equals(p_176032_1_))
            {
              var2.remove();
            }
          }
        }
        
        if (field_176042_f.size() > 0)
        {
          ListIterator var2 = field_176042_f.listIterator();
          
          while (var2.hasNext())
          {
            ChatTokenizedMessage var5 = (ChatTokenizedMessage)var2.next();
            
            if (displayName.equals(p_176032_1_))
            {
              var2.remove();
            }
          }
        }
      }
      
      try
      {
        if (field_153003_a != null)
        {
          field_153003_a.func_176019_a(field_176048_a, p_176032_1_);
        }
      }
      catch (Exception var4)
      {
        func_152995_h(var4.toString());
      }
    }
    
    public boolean func_176037_b(String p_176037_1_)
    {
      if (field_176047_c != ChatController.EnumChannelState.Connected)
      {
        return false;
      }
      

      ErrorCode var2 = field_153008_f.sendMessage(field_176048_a, p_176037_1_);
      
      if (ErrorCode.failed(var2))
      {
        String var3 = ErrorCode.getString(var2);
        func_152995_h(String.format("Error sending chat message: %s", new Object[] { var3 }));
        return false;
      }
      

      return true;
    }
    


    protected void func_176041_h()
    {
      if (field_175995_l != ChatController.EnumEmoticonMode.None)
      {
        if (field_176043_g == null)
        {
          ErrorCode var1 = field_153008_f.downloadBadgeData(field_176048_a);
          
          if (ErrorCode.failed(var1))
          {
            String var2 = ErrorCode.getString(var1);
            func_152995_h(String.format("Error trying to download badge data: %s", new Object[] { var2 }));
          }
        }
      }
    }
    
    protected void func_176039_i()
    {
      if (field_176043_g == null)
      {
        field_176043_g = new ChatBadgeData();
        ErrorCode var1 = field_153008_f.getBadgeData(field_176048_a, field_176043_g);
        
        if (ErrorCode.succeeded(var1))
        {
          try
          {
            if (field_153003_a == null)
              return;
            field_153003_a.func_176016_c(field_176048_a);

          }
          catch (Exception var3)
          {
            func_152995_h(var3.toString());
          }
          
        }
        else {
          func_152995_h("Error preparing badge data: " + ErrorCode.getString(var1));
        }
      }
    }
    
    protected void func_176033_j()
    {
      if (field_176043_g != null)
      {
        ErrorCode var1 = field_153008_f.clearBadgeData(field_176048_a);
        
        if (ErrorCode.succeeded(var1))
        {
          field_176043_g = null;
          
          try
          {
            if (field_153003_a == null)
              return;
            field_153003_a.func_176020_d(field_176048_a);

          }
          catch (Exception var3)
          {
            func_152995_h(var3.toString());
          }
        }
        else
        {
          func_152995_h("Error releasing badge data: " + ErrorCode.getString(var1));
        }
      }
    }
    
    protected void func_176031_c(String p_176031_1_)
    {
      try
      {
        if (field_153003_a != null)
        {
          field_153003_a.func_180606_a(p_176031_1_);
        }
      }
      catch (Exception var3)
      {
        func_152995_h(var3.toString());
      }
    }
    
    protected void func_176036_d(String p_176036_1_)
    {
      try
      {
        if (field_153003_a != null)
        {
          field_153003_a.func_180607_b(p_176036_1_);
        }
      }
      catch (Exception var3)
      {
        func_152995_h(var3.toString());
      }
    }
    
    private void func_176030_k()
    {
      if (field_176047_c != ChatController.EnumChannelState.Disconnected)
      {
        func_176035_a(ChatController.EnumChannelState.Disconnected);
        func_176036_d(field_176048_a);
        func_176033_j();
      }
    }
    
    public void chatStatusCallback(String p_chatStatusCallback_1_, ErrorCode p_chatStatusCallback_2_)
    {
      if (!ErrorCode.succeeded(p_chatStatusCallback_2_))
      {
        field_175998_i.remove(p_chatStatusCallback_1_);
        func_176030_k();
      }
    }
    
    public void chatChannelMembershipCallback(String p_chatChannelMembershipCallback_1_, ChatEvent p_chatChannelMembershipCallback_2_, ChatChannelInfo p_chatChannelMembershipCallback_3_)
    {
      switch (ChatController.SwitchEnumEmoticonMode.field_175974_b[p_chatChannelMembershipCallback_2_.ordinal()])
      {
      case 1: 
        func_176035_a(ChatController.EnumChannelState.Connected);
        func_176031_c(p_chatChannelMembershipCallback_1_);
        break;
      
      case 2: 
        func_176030_k();
      }
      
    }
    


    public void chatChannelUserChangeCallback(String p_chatChannelUserChangeCallback_1_, ChatUserInfo[] p_chatChannelUserChangeCallback_2_, ChatUserInfo[] p_chatChannelUserChangeCallback_3_, ChatUserInfo[] p_chatChannelUserChangeCallback_4_)
    {
      for (int var5 = 0; var5 < p_chatChannelUserChangeCallback_3_.length; var5++)
      {
        int var6 = field_176044_d.indexOf(p_chatChannelUserChangeCallback_3_[var5]);
        
        if (var6 >= 0)
        {
          field_176044_d.remove(var6);
        }
      }
      
      for (var5 = 0; var5 < p_chatChannelUserChangeCallback_4_.length; var5++)
      {
        int var6 = field_176044_d.indexOf(p_chatChannelUserChangeCallback_4_[var5]);
        
        if (var6 >= 0)
        {
          field_176044_d.remove(var6);
        }
        
        field_176044_d.add(p_chatChannelUserChangeCallback_4_[var5]);
      }
      
      for (var5 = 0; var5 < p_chatChannelUserChangeCallback_2_.length; var5++)
      {
        field_176044_d.add(p_chatChannelUserChangeCallback_2_[var5]);
      }
      
      try
      {
        if (field_153003_a != null)
        {
          field_153003_a.func_176018_a(field_176048_a, p_chatChannelUserChangeCallback_2_, p_chatChannelUserChangeCallback_3_, p_chatChannelUserChangeCallback_4_);
        }
      }
      catch (Exception var7)
      {
        func_152995_h(var7.toString());
      }
    }
    
    public void chatChannelRawMessageCallback(String p_chatChannelRawMessageCallback_1_, ChatRawMessage[] p_chatChannelRawMessageCallback_2_)
    {
      for (int var3 = 0; var3 < p_chatChannelRawMessageCallback_2_.length; var3++)
      {
        field_176045_e.addLast(p_chatChannelRawMessageCallback_2_[var3]);
      }
      
      try
      {
        if (field_153003_a != null)
        {
          field_153003_a.func_180605_a(field_176048_a, p_chatChannelRawMessageCallback_2_);
        }
      }
      catch (Exception var4)
      {
        func_152995_h(var4.toString());
      }
      
      while (field_176045_e.size() > field_153015_m)
      {
        field_176045_e.removeFirst();
      }
    }
    
    public void chatChannelTokenizedMessageCallback(String p_chatChannelTokenizedMessageCallback_1_, ChatTokenizedMessage[] p_chatChannelTokenizedMessageCallback_2_)
    {
      for (int var3 = 0; var3 < p_chatChannelTokenizedMessageCallback_2_.length; var3++)
      {
        field_176042_f.addLast(p_chatChannelTokenizedMessageCallback_2_[var3]);
      }
      
      try
      {
        if (field_153003_a != null)
        {
          field_153003_a.func_176025_a(field_176048_a, p_chatChannelTokenizedMessageCallback_2_);
        }
      }
      catch (Exception var4)
      {
        func_152995_h(var4.toString());
      }
      
      while (field_176042_f.size() > field_153015_m)
      {
        field_176042_f.removeFirst();
      }
    }
    
    public void chatClearCallback(String p_chatClearCallback_1_, String p_chatClearCallback_2_)
    {
      func_176032_a(p_chatClearCallback_2_);
    }
    
    public void chatBadgeDataDownloadCallback(String p_chatBadgeDataDownloadCallback_1_, ErrorCode p_chatBadgeDataDownloadCallback_2_)
    {
      if (ErrorCode.succeeded(p_chatBadgeDataDownloadCallback_2_))
      {
        func_176039_i();
      }
    }
  }
  
  public static abstract interface ChatListener
  {
    public abstract void func_176023_d(ErrorCode paramErrorCode);
    
    public abstract void func_176022_e(ErrorCode paramErrorCode);
    
    public abstract void func_176021_d();
    
    public abstract void func_176024_e();
    
    public abstract void func_176017_a(ChatController.ChatState paramChatState);
    
    public abstract void func_176025_a(String paramString, ChatTokenizedMessage[] paramArrayOfChatTokenizedMessage);
    
    public abstract void func_180605_a(String paramString, ChatRawMessage[] paramArrayOfChatRawMessage);
    
    public abstract void func_176018_a(String paramString, ChatUserInfo[] paramArrayOfChatUserInfo1, ChatUserInfo[] paramArrayOfChatUserInfo2, ChatUserInfo[] paramArrayOfChatUserInfo3);
    
    public abstract void func_180606_a(String paramString);
    
    public abstract void func_180607_b(String paramString);
    
    public abstract void func_176019_a(String paramString1, String paramString2);
    
    public abstract void func_176016_c(String paramString);
    
    public abstract void func_176020_d(String paramString);
  }
  
  public static enum ChatState
  {
    Uninitialized("Uninitialized", 0), 
    Initializing("Initializing", 1), 
    Initialized("Initialized", 2), 
    ShuttingDown("ShuttingDown", 3);
    
    private static final ChatState[] $VALUES = { Uninitialized, Initializing, Initialized, ShuttingDown };
    private static final String __OBFID = "CL_00001817";
    
    private ChatState(String stateName, int id) {}
  }
  
  public static enum EnumChannelState
  {
    Created("Created", 0), 
    Connecting("Connecting", 1), 
    Connected("Connected", 2), 
    Disconnecting("Disconnecting", 3), 
    Disconnected("Disconnected", 4);
    
    private static final EnumChannelState[] $VALUES = { Created, Connecting, Connected, Disconnecting, Disconnected };
    private static final String __OBFID = "CL_00002371";
    
    private EnumChannelState(String p_i46062_1_, int p_i46062_2_) {}
  }
  
  public static enum EnumEmoticonMode
  {
    None("None", 0), 
    Url("Url", 1), 
    TextureAtlas("TextureAtlas", 2);
    
    private static final EnumEmoticonMode[] $VALUES = { None, Url, TextureAtlas };
    
    private static final String __OBFID = "CL_00002369";
    

    private EnumEmoticonMode(String p_i46060_1_, int p_i46060_2_) {}
  }
  
  static final class SwitchEnumEmoticonMode
  {
    static final int[] field_175976_a;
    static final int[] field_175974_b;
    static final int[] field_175975_c = new int[ChatController.EnumEmoticonMode.values().length];
    private static final String __OBFID = "CL_00002372";
    
    static
    {
      try
      {
        field_175975_c[ChatController.EnumEmoticonMode.None.ordinal()] = 1;
      }
      catch (NoSuchFieldError localNoSuchFieldError1) {}
      



      try
      {
        field_175975_c[ChatController.EnumEmoticonMode.Url.ordinal()] = 2;
      }
      catch (NoSuchFieldError localNoSuchFieldError2) {}
      



      try
      {
        field_175975_c[ChatController.EnumEmoticonMode.TextureAtlas.ordinal()] = 3;
      }
      catch (NoSuchFieldError localNoSuchFieldError3) {}
      



      field_175974_b = new int[ChatEvent.values().length];
      
      try
      {
        field_175974_b[ChatEvent.TTV_CHAT_JOINED_CHANNEL.ordinal()] = 1;
      }
      catch (NoSuchFieldError localNoSuchFieldError4) {}
      



      try
      {
        field_175974_b[ChatEvent.TTV_CHAT_LEFT_CHANNEL.ordinal()] = 2;
      }
      catch (NoSuchFieldError localNoSuchFieldError5) {}
      



      field_175976_a = new int[ChatController.EnumChannelState.values().length];
      
      try
      {
        field_175976_a[ChatController.EnumChannelState.Connected.ordinal()] = 1;
      }
      catch (NoSuchFieldError localNoSuchFieldError6) {}
      



      try
      {
        field_175976_a[ChatController.EnumChannelState.Connecting.ordinal()] = 2;
      }
      catch (NoSuchFieldError localNoSuchFieldError7) {}
      



      try
      {
        field_175976_a[ChatController.EnumChannelState.Created.ordinal()] = 3;
      }
      catch (NoSuchFieldError localNoSuchFieldError8) {}
      



      try
      {
        field_175976_a[ChatController.EnumChannelState.Disconnected.ordinal()] = 4;
      }
      catch (NoSuchFieldError localNoSuchFieldError9) {}
      



      try
      {
        field_175976_a[ChatController.EnumChannelState.Disconnecting.ordinal()] = 5;
      }
      catch (NoSuchFieldError localNoSuchFieldError10) {}
    }
    
    SwitchEnumEmoticonMode() {}
  }
}
