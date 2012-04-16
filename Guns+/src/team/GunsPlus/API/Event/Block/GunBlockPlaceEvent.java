package team.GunsPlus.API.Event.Block;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import team.GunsPlus.Item.Gun;

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
    
    public Block getBlock() {
    	return block;
    }
}
