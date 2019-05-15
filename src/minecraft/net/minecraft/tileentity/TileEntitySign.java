package net.minecraft.tileentity;

import com.google.gson.JsonParseException;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.CommandResultStats.Type;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IChatComponent.Serializer;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class TileEntitySign extends TileEntity
{
  public final IChatComponent[] signText = { new ChatComponentText(""), new ChatComponentText(""), new ChatComponentText(""), new ChatComponentText("") };
  




  public int lineBeingEdited = -1;
  private boolean isEditable = true;
  private EntityPlayer field_145917_k;
  private final CommandResultStats field_174883_i = new CommandResultStats();
  private static final String __OBFID = "CL_00000363";
  
  public TileEntitySign() {}
  
  public void writeToNBT(NBTTagCompound compound) { super.writeToNBT(compound);
    
    for (int var2 = 0; var2 < 4; var2++)
    {
      String var3 = IChatComponent.Serializer.componentToJson(signText[var2]);
      compound.setString("Text" + (var2 + 1), var3);
    }
    
    field_174883_i.func_179670_b(compound);
  }
  
  public void readFromNBT(NBTTagCompound compound)
  {
    isEditable = false;
    super.readFromNBT(compound);
    ICommandSender var2 = new ICommandSender()
    {
      private static final String __OBFID = "CL_00002039";
      
      public String getName() {
        return "Sign";
      }
      
      public IChatComponent getDisplayName() {
        return new ChatComponentText(getName());
      }
      
      public void addChatMessage(IChatComponent message) {}
      
      public boolean canCommandSenderUseCommand(int permissionLevel, String command) { return true; }
      
      public BlockPos getPosition()
      {
        return pos;
      }
      
      public Vec3 getPositionVector() {
        return new Vec3(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
      }
      
      public World getEntityWorld() {
        return worldObj;
      }
      
      public Entity getCommandSenderEntity() {
        return null;
      }
      
      public boolean sendCommandFeedback() {
        return false;
      }
      
      public void func_174794_a(CommandResultStats.Type p_174794_1_, int p_174794_2_) {}
    };
    for (int var3 = 0; var3 < 4;)
    {
      String var4 = compound.getString("Text" + (var3 + 1));
      
      try
      {
        IChatComponent var5 = IChatComponent.Serializer.jsonToComponent(var4);
        
        try
        {
          signText[var3] = net.minecraft.util.ChatComponentProcessor.func_179985_a(var2, var5, null);
        }
        catch (CommandException var7)
        {
          signText[var3] = var5;
        }
        var3++;








      }
      catch (JsonParseException var8)
      {







        signText[var3] = new ChatComponentText(var4);
      }
    }
    
    field_174883_i.func_179668_a(compound);
  }
  



  public net.minecraft.network.Packet getDescriptionPacket()
  {
    IChatComponent[] var1 = new IChatComponent[4];
    System.arraycopy(signText, 0, var1, 0, 4);
    return new net.minecraft.network.play.server.S33PacketUpdateSign(worldObj, pos, var1);
  }
  
  public boolean getIsEditable()
  {
    return isEditable;
  }
  



  public void setEditable(boolean p_145913_1_)
  {
    isEditable = p_145913_1_;
    
    if (!p_145913_1_)
    {
      field_145917_k = null;
    }
  }
  
  public void func_145912_a(EntityPlayer p_145912_1_)
  {
    field_145917_k = p_145912_1_;
  }
  
  public EntityPlayer func_145911_b()
  {
    return field_145917_k;
  }
  
  public boolean func_174882_b(final EntityPlayer p_174882_1_)
  {
    ICommandSender var2 = new ICommandSender()
    {
      private static final String __OBFID = "CL_00002038";
      
      public String getName() {
        return p_174882_1_.getName();
      }
      
      public IChatComponent getDisplayName() {
        return p_174882_1_.getDisplayName();
      }
      
      public void addChatMessage(IChatComponent message) {}
      
      public boolean canCommandSenderUseCommand(int permissionLevel, String command) { return true; }
      
      public BlockPos getPosition()
      {
        return pos;
      }
      
      public Vec3 getPositionVector() {
        return new Vec3(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
      }
      
      public World getEntityWorld() {
        return p_174882_1_.getEntityWorld();
      }
      
      public Entity getCommandSenderEntity() {
        return p_174882_1_;
      }
      
      public boolean sendCommandFeedback() {
        return false;
      }
      
      public void func_174794_a(CommandResultStats.Type p_174794_1_, int p_174794_2_) {
        field_174883_i.func_179672_a(this, p_174794_1_, p_174794_2_);
      }
    };
    
    for (int var3 = 0; var3 < signText.length; var3++)
    {
      ChatStyle var4 = signText[var3] == null ? null : signText[var3].getChatStyle();
      
      if ((var4 != null) && (var4.getChatClickEvent() != null))
      {
        ClickEvent var5 = var4.getChatClickEvent();
        
        if (var5.getAction() == ClickEvent.Action.RUN_COMMAND)
        {
          MinecraftServer.getServer().getCommandManager().executeCommand(var2, var5.getValue());
        }
      }
    }
    
    return true;
  }
  
  public CommandResultStats func_174880_d()
  {
    return field_174883_i;
  }
}
