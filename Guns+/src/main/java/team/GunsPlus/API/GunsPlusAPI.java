package team.GunsPlus.API;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.player.SpoutPlayer;

import team.GunsPlus.GunsPlus;
import team.GunsPlus.GunsPlusPlayer;
import team.GunsPlus.Block.TripodData;
import team.GunsPlus.Item.Addition;
import team.GunsPlus.Item.Ammo;
import team.GunsPlus.Item.Gun;
import team.GunsPlus.Util.GunUtils;
import team.GunsPlus.Util.PlayerUtils;

/**
 * Guns+ API Class - Contains Methods for use by External plugins.
 * 
 * @author SirTyler (Tyler Martin)
 * @version 1.2
 */

public class GunsPlusAPI {
	private GunsPlus plugin;

	public GunsPlusAPI(GunsPlus _plus) {
		plugin = _plus;
	}

	/**
	 * Get Version of Guns+
	 * 
	 * @return Version of Guns+
	 */
	public String getVersion() {
		return plugin.getDescription().getVersion();
	}

	/**
	 * Get GunsPlusPlayer that represents Player
	 * 
	 * @param player
	 *            Player Object to check
	 * @return Instance of GunsPlusPlayer
	 */
	public GunsPlusPlayer getPlayer(Player player) {
		return PlayerUtils.getPlayerBySpoutPlayer((SpoutPlayer) player);
	}

	/**
	 * Get list of all loaded Guns
	 * 
	 * @return List of Guns
	 */
	public List<Gun> getAllGuns() {
		return GunsPlus.allGuns;
	}

	/**
	 * Get gun by given Name
	 * 
	 * @param name
	 *            String of Name to Check
	 * @return Found Gun
	 */
	public Gun getGunByName(String name) {
		return GunUtils.getGun(name);
	}

	/**
	 * Get gun by given ItemStack
	 * 
	 * @param itemstack
	 *            ItemStack to Check
	 * @return Found Gun
	 */
	public Gun getGunByItemStack(ItemStack itemstack) {
		return GunUtils.getGun(itemstack);
	}

	/**
	 * Checks if the given ItemStack is a Gun
	 * 
	 * @param itemstack
	 *            ItemStack to Check
	 * @return True if ItemStack is Gun
	 */
	public boolean isGun(ItemStack itemstack) {
		return GunUtils.isGun(itemstack);
	}

	/**
	 * Get list of all loaded Ammo
	 * 
	 * @return List of Ammo
	 */
	public List<Ammo> getAllAmmo() {
		return GunsPlus.allAmmo;
	}

	/**
	 * Get list of all loaded Additions
	 * 
	 * @return List of Additions
	 */
	public List<Addition> getAllAdditions() {
		return GunsPlus.allAdditions;
	}

	/**
	 * Get list of all Tripod Blocks
	 * 
	 * @return List of Tripod Blocks
	 */
	public List<Block> getAllTripodBlocks() {
		List<Block> blocklist = new ArrayList<Block>();
		for(TripodData td : GunsPlus.allTripodBlocks) {
			blocklist.add(td.getLocation().getBlock());
		}
		return blocklist;
	}

	/**
	 * Get list of Online GunsPlusPlayer
	 * 
	 * @return List of Online GunsPlusPlayer
	 */
	public List<GunsPlusPlayer> getOnlineGunsPlusPlayers() {
		return GunsPlus.GunsPlusPlayers;
	}

}
