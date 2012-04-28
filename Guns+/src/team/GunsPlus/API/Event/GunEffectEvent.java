package team.GunsPlus.API.Event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import team.GunsPlus.Enum.Effect;
import team.GunsPlus.Item.Gun;

/**
 * Guns+ Event called whenever a Gun uses an Effect
 * @author SirTyler (Tyler Martin)
 * @version 1.2
 */
public class GunEffectEvent extends Event{
    private static final HandlerList handlers = new HandlerList();
    private Player player = null;
    private Gun gun = null;
    private Effect eff = null;
    
    public GunEffectEvent(Player p, Gun g, Effect e) {
    	player = p;
    	gun = g;
    	eff = e;
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
    
    /**
     * Get Effect associated with Event
     * @return Effect used by Event
     */
    public Effect getEffect() {
    	return eff;
    }
}
