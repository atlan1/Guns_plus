package team.GunsPlus.API.Event.Gun;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import team.GunsPlus.Item.Gun;

/**
 * Guns+ Event that is called whenever a Player reloads a Gun
 * @author SirTyler (Tyler Martin)
 * @version 1.2
 */
public class GunReloadEvent extends Event{
    private static final HandlerList handlers = new HandlerList();
    private Player player = null;
    private Gun gun = null;
    
    public GunReloadEvent(Player p, Gun g) {
    	player = p;
    	gun = g;
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
     * @return Player that triggered Event
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
}
