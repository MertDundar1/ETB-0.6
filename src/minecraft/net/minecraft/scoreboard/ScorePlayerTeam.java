package net.minecraft.scoreboard;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import net.minecraft.util.EnumChatFormatting;


public class ScorePlayerTeam
  extends Team
{
  private final Scoreboard theScoreboard;
  private final String field_96675_b;
  private final Set membershipSet = Sets.newHashSet();
  private String teamNameSPT;
  private String namePrefixSPT = "";
  private String colorSuffix = "";
  private boolean allowFriendlyFire = true;
  private boolean canSeeFriendlyInvisibles = true;
  private Team.EnumVisible field_178778_i;
  private Team.EnumVisible field_178776_j;
  private EnumChatFormatting field_178777_k;
  private static final String __OBFID = "CL_00000616";
  
  public ScorePlayerTeam(Scoreboard p_i2308_1_, String p_i2308_2_)
  {
    field_178778_i = Team.EnumVisible.ALWAYS;
    field_178776_j = Team.EnumVisible.ALWAYS;
    field_178777_k = EnumChatFormatting.RESET;
    theScoreboard = p_i2308_1_;
    field_96675_b = p_i2308_2_;
    teamNameSPT = p_i2308_2_;
  }
  



  public String getRegisteredName()
  {
    return field_96675_b;
  }
  
  public String func_96669_c()
  {
    return teamNameSPT;
  }
  
  public void setTeamName(String p_96664_1_)
  {
    if (p_96664_1_ == null)
    {
      throw new IllegalArgumentException("Name cannot be null");
    }
    

    teamNameSPT = p_96664_1_;
    theScoreboard.broadcastTeamRemoved(this);
  }
  

  public Collection getMembershipCollection()
  {
    return membershipSet;
  }
  



  public String getColorPrefix()
  {
    return namePrefixSPT;
  }
  
  public void setNamePrefix(String p_96666_1_)
  {
    if (p_96666_1_ == null)
    {
      throw new IllegalArgumentException("Prefix cannot be null");
    }
    

    namePrefixSPT = p_96666_1_;
    theScoreboard.broadcastTeamRemoved(this);
  }
  




  public String getColorSuffix()
  {
    return colorSuffix;
  }
  
  public void setNameSuffix(String p_96662_1_)
  {
    colorSuffix = p_96662_1_;
    theScoreboard.broadcastTeamRemoved(this);
  }
  
  public String formatString(String input)
  {
    return getColorPrefix() + input + getColorSuffix();
  }
  



  public static String formatPlayerName(Team p_96667_0_, String p_96667_1_)
  {
    return p_96667_0_ == null ? p_96667_1_ : p_96667_0_.formatString(p_96667_1_);
  }
  
  public boolean getAllowFriendlyFire()
  {
    return allowFriendlyFire;
  }
  
  public void setAllowFriendlyFire(boolean p_96660_1_)
  {
    allowFriendlyFire = p_96660_1_;
    theScoreboard.broadcastTeamRemoved(this);
  }
  
  public boolean func_98297_h()
  {
    return canSeeFriendlyInvisibles;
  }
  
  public void setSeeFriendlyInvisiblesEnabled(boolean p_98300_1_)
  {
    canSeeFriendlyInvisibles = p_98300_1_;
    theScoreboard.broadcastTeamRemoved(this);
  }
  
  public Team.EnumVisible func_178770_i()
  {
    return field_178778_i;
  }
  
  public Team.EnumVisible func_178771_j()
  {
    return field_178776_j;
  }
  
  public void func_178772_a(Team.EnumVisible p_178772_1_)
  {
    field_178778_i = p_178772_1_;
    theScoreboard.broadcastTeamRemoved(this);
  }
  
  public void func_178773_b(Team.EnumVisible p_178773_1_)
  {
    field_178776_j = p_178773_1_;
    theScoreboard.broadcastTeamRemoved(this);
  }
  
  public int func_98299_i()
  {
    int var1 = 0;
    
    if (getAllowFriendlyFire())
    {
      var1 |= 0x1;
    }
    
    if (func_98297_h())
    {
      var1 |= 0x2;
    }
    
    return var1;
  }
  
  public void func_98298_a(int p_98298_1_)
  {
    setAllowFriendlyFire((p_98298_1_ & 0x1) > 0);
    setSeeFriendlyInvisiblesEnabled((p_98298_1_ & 0x2) > 0);
  }
  
  public void func_178774_a(EnumChatFormatting p_178774_1_)
  {
    field_178777_k = p_178774_1_;
  }
  
  public EnumChatFormatting func_178775_l()
  {
    return field_178777_k;
  }
}
