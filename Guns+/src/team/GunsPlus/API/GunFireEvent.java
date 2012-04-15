package team.GunsPlus.API;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GunFireEvent extends Event{
    private static final HandlerList handlers = new HandlerList();
    private Player player = null;
    
    public GunFireEvent(Player p) {
    	player = p;
    }
    
    public HandlerList getHandlers() {
        return handlers;
    }
     
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public Player getPlayer() {
    	return player;
    }
}
