package net.minecraft.client.gui.spectator.categories;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.spectator.ISpectatorMenuObject;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class TeleportToTeam implements net.minecraft.client.gui.spectator.ISpectatorMenuView, ISpectatorMenuObject
{
  private final List field_178672_a = Lists.newArrayList();
  private static final String __OBFID = "CL_00001920";
  
  public TeleportToTeam()
  {
    Minecraft var1 = Minecraft.getMinecraft();
    Iterator var2 = theWorld.getScoreboard().getTeams().iterator();
    
    while (var2.hasNext())
    {
      ScorePlayerTeam var3 = (ScorePlayerTeam)var2.next();
      field_178672_a.add(new TeamSelectionObject(var3));
    }
  }
  
  public List func_178669_a()
  {
    return field_178672_a;
  }
  
  public IChatComponent func_178670_b()
  {
    return new ChatComponentText("Select a team to teleport to");
  }
  
  public void func_178661_a(SpectatorMenu p_178661_1_)
  {
    p_178661_1_.func_178647_a(this);
  }
  
  public IChatComponent func_178664_z_()
  {
    return new ChatComponentText("Teleport to team member");
  }
  
  public void func_178663_a(float p_178663_1_, int p_178663_2_)
  {
    Minecraft.getMinecraft().getTextureManager().bindTexture(net.minecraft.client.gui.GuiSpectator.field_175269_a);
    Gui.drawModalRectWithCustomSizedTexture(0, 0, 16.0F, 0.0F, 16, 16, 256.0F, 256.0F);
  }
  
  public boolean func_178662_A_()
  {
    Iterator var1 = field_178672_a.iterator();
    
    ISpectatorMenuObject var2;
    do
    {
      if (!var1.hasNext())
      {
        return false;
      }
      
      var2 = (ISpectatorMenuObject)var1.next();
    }
    while (!var2.func_178662_A_());
    
    return true;
  }
  
  class TeamSelectionObject implements ISpectatorMenuObject
  {
    private final ScorePlayerTeam field_178676_b;
    private final ResourceLocation field_178677_c;
    private final List field_178675_d;
    private static final String __OBFID = "CL_00001919";
    
    public TeamSelectionObject(ScorePlayerTeam p_i45492_2_)
    {
      field_178676_b = p_i45492_2_;
      field_178675_d = Lists.newArrayList();
      Iterator var3 = p_i45492_2_.getMembershipCollection().iterator();
      
      while (var3.hasNext())
      {
        String var4 = (String)var3.next();
        NetworkPlayerInfo var5 = Minecraft.getMinecraft().getNetHandler().func_175104_a(var4);
        
        if (var5 != null)
        {
          field_178675_d.add(var5);
        }
      }
      
      if (!field_178675_d.isEmpty())
      {
        String var6 = ((NetworkPlayerInfo)field_178675_d.get(new Random().nextInt(field_178675_d.size()))).func_178845_a().getName();
        field_178677_c = AbstractClientPlayer.getLocationSkin(var6);
        AbstractClientPlayer.getDownloadImageSkin(field_178677_c, var6);
      }
      else
      {
        field_178677_c = net.minecraft.client.resources.DefaultPlayerSkin.func_177335_a();
      }
    }
    
    public void func_178661_a(SpectatorMenu p_178661_1_)
    {
      p_178661_1_.func_178647_a(new TeleportToPlayer(field_178675_d));
    }
    
    public IChatComponent func_178664_z_()
    {
      return new ChatComponentText(field_178676_b.func_96669_c());
    }
    
    public void func_178663_a(float p_178663_1_, int p_178663_2_)
    {
      int var3 = -1;
      String var4 = FontRenderer.getFormatFromString(field_178676_b.getColorPrefix());
      
      if (var4.length() >= 2)
      {
        var3 = getMinecraftfontRendererObj.func_175064_b(var4.charAt(1));
      }
      
      if (var3 >= 0)
      {
        float var5 = (var3 >> 16 & 0xFF) / 255.0F;
        float var6 = (var3 >> 8 & 0xFF) / 255.0F;
        float var7 = (var3 & 0xFF) / 255.0F;
        Gui.drawRect(1.0D, 1.0D, 15.0D, 15.0D, MathHelper.func_180183_b(var5 * p_178663_1_, var6 * p_178663_1_, var7 * p_178663_1_) | p_178663_2_ << 24);
      }
      
      Minecraft.getMinecraft().getTextureManager().bindTexture(field_178677_c);
      net.minecraft.client.renderer.GlStateManager.color(p_178663_1_, p_178663_1_, p_178663_1_, p_178663_2_ / 255.0F);
      Gui.drawScaledCustomSizeModalRect(2, 2, 8.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
      Gui.drawScaledCustomSizeModalRect(2, 2, 40.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
    }
    
    public boolean func_178662_A_()
    {
      return !field_178675_d.isEmpty();
    }
  }
}
