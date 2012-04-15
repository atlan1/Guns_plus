package team.GunsPlus.API.Event;

import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GunProjectileEvent extends Event{
    private static final HandlerList handlers = new HandlerList();
    private Projectile pro;
    
    public GunProjectileEvent(Projectile p) {
    	pro = p;
    }
    
    public HandlerList getHandlers() {
        return handlers;
    }
     
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public Projectile getProjectile() {
    	return pro;
    }

}
