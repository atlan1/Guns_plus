package team.GunsPlus.API.Event.Gun;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import team.GunsPlus.Item.Gun;
import team.GunsPlus.Util.GunUtils;

/**
 * Guns+ Event called whenever a Gun is fired
 *
 * @author SirTyler (Tyler Martin)
 * @version 1.2
 */
public class GunFireEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private Player player = null;
	private Gun gun = null;
	private Location loc = null;
	private Location eyeLoc = null;

	public GunFireEvent(Player p, Gun g) {
		player = p;
		gun = g;
		loc = p.getLocation();
		eyeLoc = p.getEyeLocation();
	}

	/**
	 * Bukkit Event Method
	 *
	 * @see <a
	 * href="http://jd.bukkit.org/apidocs/org/bukkit/event/Event.html">Bukkit
	 * Event</a>
	 */
	public HandlerList getHandlers() {
		return handlers;
	}

	/**
	 * Bukkit Event Method
	 *
	 * @see <a
	 * href="http://jd.bukkit.org/apidocs/org/bukkit/event/Event.html">Bukkit
	 * Event</a>
	 */
	public static HandlerList getHandlerList() {
		return handlers;
	}

	/**
	 * Get player associated with Event
	 *
	 * @return Player who fired Gun
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Get gun associated with Event
	 *
	 * @return Gun fired
	 */
	public Gun getGun() {
		return gun;
	}

	/**
	 * Get list of LivingEntitys targeted
	 *
	 * @return Targeted LivingEntitys
	 */
	public List<LivingEntity> getTargetEntitys() {
		return new ArrayList<LivingEntity>(GunUtils.getTargetEntities(eyeLoc, gun).keySet());
	}

	/**
	 * Get list of Blocks targeted
	 *
	 * @return Targeted Blocks
	 */
	public List<Block> getTargetBlocks() {
		return GunUtils.getTargetBlocks(eyeLoc, gun);
	}

	/**
	 * Get block Aimed at
	 *
	 * @return Targeted Block
	 */
	public Block getTargetBlock() {
		List<Block> bL = GunUtils.getTargetBlocks(eyeLoc, gun);
		Block b = null;
		if (bL == null) {
			return b;
		}
		if (!bL.isEmpty()) {
			b = bL.get(0);
		}
		return b;
	}

	/**
	 * Get Location of Player when Gun was fired
	 *
	 * @return Location of Gun shot
	 */
	public Location getLocation() {
		return loc;
	}

	/**
	 * Get Location of Player's eyes when Gun was fired
	 *
	 * @return Location of Eyes
	 */
	public Location getEyeLocation() {
		return eyeLoc;
	}
}
