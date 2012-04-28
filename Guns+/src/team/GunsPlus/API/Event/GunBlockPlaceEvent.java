package team.GunsPlus.API.Event;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import team.GunsPlus.Item.Gun;

/**
 * Guns+ Event called whenever a Block is Placed via Effect
 * @author SirTyler (Tyler Martin)
 * @version 1.2
 */
public class GunBlockPlaceEvent extends Event{
    private static final HandlerList handlers = new HandlerList();
    private Player player = null;
    private Gun gun = null;
    private Block block = null;
    
    public GunBlockPlaceEvent(Player p, Gun g, Block b) {
    	player = p;
    	gun = g;
    	block = b;
    }
    
    /**
     * Bukkit Event Method
     * @see <a href="http://jd.bukkit.org/apidocs/org/bukkit/event/Event.html">Bukkit Event</a>
     */
    public HandlerList getHandlers() {
        return handlers;
    }
    
    /**
     * Bukkit Event Method
     * @see <a href="http://jd.bukkit.org/apidocs/org/bukkit/event/Event.html">Bukkit Event</a>
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    /**
     * Get Player associated with Event
     * @return Player that tirggered Event
     */
    public Player getPlayer() {
    	return player;
    }
    
    /**
     * Get Gun associated with Event
     * @return Gun that triggered Event
     */
    public Gun getGun() {
    	return gun;
    }
    
    /**
     * Get Block associated with Event
     * @return Block placed
     */
    public Block getBlock() {
    	return block;
    }
}
