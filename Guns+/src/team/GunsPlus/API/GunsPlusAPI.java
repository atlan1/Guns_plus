package team.GunsPlus.API;

import org.bukkit.entity.Player;

import team.GunsPlus.GunsPlus;
import team.GunsPlus.GunsPlusPlayer;

public class GunsPlusAPI {
	private GunsPlus plugin;
	
	public GunsPlusAPI(GunsPlus _plus) {
		plugin = _plus;
	}
	
	public String getVersion() {
		return plugin.getDescription().getVersion();
	}
	
	public GunsPlusPlayer getPlayer(Player player) {
		GunsPlusPlayer p = null;
		p = GunsPlus.GunsPlusPlayers.get(GunsPlus.GunsPlusPlayers.indexOf(player));
		return p;
	}
	
}
