package skinsrestorer.mcp;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;

import skinsrestorer.shared.api.SkinsRestorerAPI;
import skinsrestorer.shared.format.SkinProfile;
import skinsrestorer.shared.storage.SkinStorage;

import java.util.Arrays;
import java.util.List;


public class CommandSkin extends CommandBase {
	@Override
	public String getCommandName() {
		return "skin";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/skin set|change <skinName>\n/skin clear|reset";
	}

	@Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
    	// Hey! getRequiredPermissionLevel is ignored!!!
		if (!(sender instanceof EntityPlayer)) {
			return false;
		}
		return true;
    }


	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (!(sender instanceof EntityPlayer)) {
			throw new WrongUsageException("This command is only for players");
		}

		final EntityPlayer player = (EntityPlayer) sender;

		if (args.length == 0) {
			throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
		}

		if (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("change")) {
			if (args.length >= 2) {
				changeCommand(player, args);
			} else {
				throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
			}
		} else if (args[0].equalsIgnoreCase("clear") || args[0].equalsIgnoreCase("reset")) {
			clearCommand(player);
		} else {
			throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
		}

		sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, 1);
	}

	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
	{
		if (args.length == 1) {
			return Arrays.asList("set", "clear");
		}
		return null;
	}

	// Skin clear command.
	public void clearCommand(EntityPlayer player) {
		SkinStorage.getInstance().removeSkinData(player.getName());
		player.addChatMessage(new ChatComponentText("Skin reset. Please relog."));
	}

	// Skin change command.
	public void changeCommand(final EntityPlayer player, final String[] args) {
		player.addChatMessage(new ChatComponentText("Skin change initiated. Please wait..."));
		SkinsRestorerAPI.setSkin(player, args[1]);
	}

}

