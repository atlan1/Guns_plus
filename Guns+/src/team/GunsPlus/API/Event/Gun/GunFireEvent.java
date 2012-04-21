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

public class GunFireEvent extends Event{
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
    
    public List<LivingEntity> getTargetEntitys() {
    	return new ArrayList<LivingEntity>(GunUtils.getTargetEntities(eyeLoc, gun).keySet());
    }
    
    public List<Block> getTargetBlocks() {
    	return GunUtils.getTargetBlocks(eyeLoc, gun);
    }
    
    public Block getTargetBlock() {
    	return GunUtils.getTargetBlocks(eyeLoc, gun).get(0);
    }
    
    public Location getLocation() {
    	return loc;
    }
    
    public Location getEyeLocation() {
    	return eyeLoc;
    }
}
