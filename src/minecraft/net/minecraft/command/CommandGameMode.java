package net.minecraft.command;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.WorldSettings.GameType;

public class CommandGameMode extends CommandBase
{
  private static final String __OBFID = "CL_00000448";
  
  public CommandGameMode() {}
  
  public String getCommandName()
  {
    return "gamemode";
  }
  



  public int getRequiredPermissionLevel()
  {
    return 2;
  }
  
  public String getCommandUsage(ICommandSender sender)
  {
    return "commands.gamemode.usage";
  }
  
  public void processCommand(ICommandSender sender, String[] args) throws CommandException
  {
    if (args.length <= 0)
    {
      throw new WrongUsageException("commands.gamemode.usage", new Object[0]);
    }
    

    WorldSettings.GameType var3 = getGameModeFromCommand(sender, args[0]);
    EntityPlayerMP var4 = args.length >= 2 ? getPlayer(sender, args[1]) : getCommandSenderAsPlayer(sender);
    var4.setGameType(var3);
    fallDistance = 0.0F;
    
    if (sender.getEntityWorld().getGameRules().getGameRuleBooleanValue("sendCommandFeedback"))
    {
      var4.addChatMessage(new ChatComponentTranslation("gameMode.changed", new Object[0]));
    }
    
    ChatComponentTranslation var5 = new ChatComponentTranslation("gameMode." + var3.getName(), new Object[0]);
    
    if (var4 != sender)
    {
      notifyOperators(sender, this, 1, "commands.gamemode.success.other", new Object[] { var4.getName(), var5 });
    }
    else
    {
      notifyOperators(sender, this, 1, "commands.gamemode.success.self", new Object[] { var5 });
    }
  }
  



  protected WorldSettings.GameType getGameModeFromCommand(ICommandSender p_71539_1_, String p_71539_2_)
    throws CommandException
  {
    return (!p_71539_2_.equalsIgnoreCase(WorldSettings.GameType.SURVIVAL.getName())) && (!p_71539_2_.equalsIgnoreCase("s")) ? WorldSettings.GameType.CREATIVE : (!p_71539_2_.equalsIgnoreCase(WorldSettings.GameType.CREATIVE.getName())) && (!p_71539_2_.equalsIgnoreCase("c")) ? WorldSettings.GameType.ADVENTURE : (!p_71539_2_.equalsIgnoreCase(WorldSettings.GameType.ADVENTURE.getName())) && (!p_71539_2_.equalsIgnoreCase("a")) ? WorldSettings.GameType.SPECTATOR : (!p_71539_2_.equalsIgnoreCase(WorldSettings.GameType.SPECTATOR.getName())) && (!p_71539_2_.equalsIgnoreCase("sp")) ? net.minecraft.world.WorldSettings.getGameTypeById(parseInt(p_71539_2_, 0, WorldSettings.GameType.values().length - 2)) : WorldSettings.GameType.SURVIVAL;
  }
  
  public java.util.List addTabCompletionOptions(ICommandSender sender, String[] args, net.minecraft.util.BlockPos pos)
  {
    return args.length == 2 ? getListOfStringsMatchingLastWord(args, getListOfPlayerUsernames()) : args.length == 1 ? getListOfStringsMatchingLastWord(args, new String[] { "survival", "creative", "adventure", "spectator" }) : null;
  }
  



  protected String[] getListOfPlayerUsernames()
  {
    return MinecraftServer.getServer().getAllUsernames();
  }
  



  public boolean isUsernameIndex(String[] args, int index)
  {
    return index == 1;
  }
}
