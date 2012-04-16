package team.GunsPlus.API.Event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import team.GunsPlus.Enum.Effect;
import team.GunsPlus.Item.Gun;

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
    
    public HandlerList getHandlers() {
        return handlers;
    }
     
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public Player getPlayer() {
    	return player;
    }
    
    public Gun getGun() {
    	return gun;
    }
    
    public Effect getEffect() {
    	return eff;
    }
}
