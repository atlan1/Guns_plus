package team.GunsPlus;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.material.CustomItem;

import team.GunsPlus.Manager.GunsPlus;

public class CommandEx implements CommandExecutor{
	private GunsPlus plugin;
	
	public CommandEx(GunsPlus gunsPlus) {
		plugin = gunsPlus;
		help[0] = (ChatColor.GOLD + "------Guns+ Version:" + plugin.getDescription().getVersion() + "-------");
		help[1] = (ChatColor.BLUE + "-   /Guns+ Credits   ~  Displays Credits of Guns+");
		help[2] = (ChatColor.BLUE + "-   /Guns+ Reload    ~  Reloads Config");
		help[3] = (ChatColor.BLUE + "-   /Guns+ List      ~  Lists all Loaded Items");
	}
	String[] help = new String[4];

	@SuppressWarnings("static-access")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length == 1) {
			String cmd = args[0];
			if(cmd.equalsIgnoreCase("credits")) {
				sender.sendMessage(GunsPlusListener.credit);
				return true;
			} else if(cmd.equalsIgnoreCase("reload")) {
				if(sender instanceof Player && !((Player) sender).hasPermission("gunsplus.reload")) {
					sender.sendMessage(ChatColor.RED + "Permission Denied");
				} else {
					plugin.reload();
					sender.sendMessage(ChatColor.YELLOW + GunsPlus.PRE + " Configuration files reloaded!");
				}
				return true;
			} else if(cmd.equalsIgnoreCase("help")) {
				sender.sendMessage(help);
				return true;
			} else if(cmd.equalsIgnoreCase("list")) {
				sender.sendMessage(ChatColor.BLUE + "---Guns----");
				for(CustomItem c : plugin.allGuns) {
					sender.sendMessage(ChatColor.GRAY + "- " + c.getName());
				}
				return true;
			}
		}
		return false;
	}

}
