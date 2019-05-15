package net.minecraft.command;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public class CommandExecuteAt extends CommandBase
{
  private static final String __OBFID = "CL_00002344";
  
  public CommandExecuteAt() {}
  
  public String getCommandName()
  {
    return "execute";
  }
  



  public int getRequiredPermissionLevel()
  {
    return 2;
  }
  
  public String getCommandUsage(ICommandSender sender)
  {
    return "commands.execute.usage";
  }
  
  public void processCommand(final ICommandSender sender, String[] args) throws CommandException
  {
    if (args.length < 5)
    {
      throw new WrongUsageException("commands.execute.usage", new Object[0]);
    }
    

    final Entity var3 = func_175759_a(sender, args[0], Entity.class);
    final double var4 = func_175761_b(posX, args[1], false);
    double var6 = func_175761_b(posY, args[2], false);
    final double var8 = func_175761_b(posZ, args[3], false);
    final BlockPos var10 = new BlockPos(var4, var6, var8);
    byte var11 = 4;
    
    if (("detect".equals(args[4])) && (args.length > 10))
    {
      World var12 = sender.getEntityWorld();
      double var13 = func_175761_b(var4, args[5], false);
      double var15 = func_175761_b(var6, args[6], false);
      double var17 = func_175761_b(var8, args[7], false);
      Block var19 = getBlockByText(sender, args[8]);
      int var20 = parseInt(args[9], -1, 15);
      BlockPos var21 = new BlockPos(var13, var15, var17);
      IBlockState var22 = var12.getBlockState(var21);
      
      if ((var22.getBlock() != var19) || ((var20 >= 0) && (var22.getBlock().getMetaFromState(var22) != var20)))
      {
        throw new CommandException("commands.execute.failed", new Object[] { "detect", var3.getName() });
      }
      
      var11 = 10;
    }
    
    String var24 = func_180529_a(args, var11);
    ICommandSender var14 = new ICommandSender()
    {
      private static final String __OBFID = "CL_00002343";
      
      public String getName() {
        return var3.getName();
      }
      
      public IChatComponent getDisplayName() {
        return var3.getDisplayName();
      }
      
      public void addChatMessage(IChatComponent message) {
        sender.addChatMessage(message);
      }
      
      public boolean canCommandSenderUseCommand(int permissionLevel, String command) {
        return sender.canCommandSenderUseCommand(permissionLevel, command);
      }
      
      public BlockPos getPosition() {
        return var10;
      }
      
      public net.minecraft.util.Vec3 getPositionVector() {
        return new net.minecraft.util.Vec3(var4, var8, val$var8);
      }
      
      public World getEntityWorld() {
        return var3worldObj;
      }
      
      public Entity getCommandSenderEntity() {
        return var3;
      }
      
      public boolean sendCommandFeedback() {
        MinecraftServer var1 = MinecraftServer.getServer();
        return (var1 == null) || (worldServers[0].getGameRules().getGameRuleBooleanValue("commandBlockOutput"));
      }
      
      public void func_174794_a(CommandResultStats.Type p_174794_1_, int p_174794_2_) {
        var3.func_174794_a(p_174794_1_, p_174794_2_);
      }
    };
    ICommandManager var25 = MinecraftServer.getServer().getCommandManager();
    
    try
    {
      int var16 = var25.executeCommand(var14, var24);
      
      if (var16 < 1)
      {
        throw new CommandException("commands.execute.allInvocationsFailed", new Object[] { var24 });
      }
    }
    catch (Throwable var23)
    {
      throw new CommandException("commands.execute.failed", new Object[] { var24, var3.getName() });
    }
  }
  

  public java.util.List addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
  {
    return (args.length == 9) && ("detect".equals(args[4])) ? func_175762_a(args, Block.blockRegistry.getKeys()) : (args.length > 5) && (args.length <= 8) && ("detect".equals(args[4])) ? func_175771_a(args, 5, pos) : (args.length > 1) && (args.length <= 4) ? func_175771_a(args, 1, pos) : args.length == 1 ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames()) : null;
  }
  



  public boolean isUsernameIndex(String[] args, int index)
  {
    return index == 0;
  }
}
