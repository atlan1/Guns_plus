package team.GunsPlus;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandEx implements CommandExecutor{
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			if(args.length == 1) {
				String cmd = args[0];
				if(cmd.equalsIgnoreCase("credits")) {
					player.sendMessage(GunsPlusListener.credit);
					return true;
				}
			}
		} else {
			//Console
			if(args.length == 1) {
				String cmd = args[0];
				if(cmd.equalsIgnoreCase("credits")) {
					sender.sendMessage(GunsPlusListener.credit);
					return true;
				}
			}
		}
		return false;
	}

}
