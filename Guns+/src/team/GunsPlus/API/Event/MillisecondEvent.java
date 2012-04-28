package team.GunsPlus.API.Event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Guns+ Event called for Millisecond Tasks
 * @author Atlan1
 * @version 1.2
 */
public class MillisecondEvent extends Event{

	private static final HandlerList handlers = new HandlerList();
	
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
}
