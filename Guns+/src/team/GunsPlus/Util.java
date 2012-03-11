package team.GunsPlus;


import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.material.CustomItem;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.getspout.spoutapi.sound.SoundManager;

import team.GunsPlus.GunsPlus;

public class Util {
	
	public static boolean hasSpoutcraft(Player p){
		if(SpoutManager.getPlayerManager().getPlayer(p).isSpoutCraftEnabled())
			return true;
		if(SpoutManager.getPlayer(p).isSpoutCraftEnabled())
			return true;
		return false;
	}
	
	public static boolean isOnDelayQueue(SpoutPlayer sp){
		return GunsPlus.delaying.containsKey(sp)&&GunsPlus.delaying.get(sp)==false?true:false;
	}
	
	public static boolean isDelayed(SpoutPlayer sp){
		return GunsPlus.delaying.containsKey(sp)&&GunsPlus.delaying.get(sp)==true?true:false;
	}
	
	public static void setOnDelayQueue(SpoutPlayer sp){
		GunsPlus.delaying.put(sp, false);
	}
	
	public static void setDelayed(SpoutPlayer sp){
		GunsPlus.delaying.put(sp, true);
	}
	
	public static void removeDelay(SpoutPlayer sp){
		GunsPlus.delaying.remove(sp);
	}
	public static boolean isOnReloadQueue(SpoutPlayer sp){
		return GunsPlus.reloading.containsKey(sp)&&GunsPlus.reloading.get(sp)==false?true:false;
	}
	
	public static boolean isReloading(SpoutPlayer sp){
		return GunsPlus.reloading.containsKey(sp)&&GunsPlus.reloading.get(sp)==true?true:false;
	}
	
	public static void setOnReloadQueue(SpoutPlayer sp){
		GunsPlus.reloading.put(sp, false);
	}
	
	public static void setReloading(SpoutPlayer sp){
		GunsPlus.reloading.put(sp, true);
	}
	
	public static void removeReload(SpoutPlayer sp){
		GunsPlus.reloading.remove(sp);
	}
	
	public static int getFireCounter(SpoutPlayer sp){
		return GunsPlus.fireCounter.containsKey(sp)?GunsPlus.fireCounter.get(sp):-1;
	}
	
	public static void resetFireCounter(SpoutPlayer sp){
		if(GunsPlus.fireCounter.containsKey(sp))
			GunsPlus.fireCounter.put(sp, 0);
	}
	
	public static void setFireCounter(SpoutPlayer sp, int value){
		if(GunsPlus.fireCounter.containsKey(sp))
			GunsPlus.fireCounter.put(sp, value);
	}
	
	public static boolean isZooming(SpoutPlayer sp){
		return GunsPlus.inZoom.contains(sp)?true:false;
	}
	
	public static void setZooming(SpoutPlayer sp, boolean b){
		if(b) GunsPlus.inZoom.add(sp);
		else if(isZooming(sp)) GunsPlus.inZoom.remove(sp);
	}
	
	public static boolean isGunsPlusItem(String name){
		for(int j = 0;j<GunsPlus.allGuns.size();j++){
			if(GunsPlus.allGuns.get(j).getName().equalsIgnoreCase(name)) return true;
		}
		for(int j = 0;j<GunsPlus.allAmmo.size();j++){
			if(GunsPlus.allAmmo.get(j).getName().equalsIgnoreCase(name)) return true;
		}
		return false;
	}
	
	public static CustomItem getGunsPlusItem(String name){
		CustomItem ci = null;
		for(int j = 0;j<GunsPlus.allGuns.size();j++){
			if(GunsPlus.allGuns.get(j).getName().equalsIgnoreCase(name)) return ci;
		}
		for(int j = 0;j<GunsPlus.allAmmo.size();j++){
			if(GunsPlus.allAmmo.get(j).getName().equalsIgnoreCase(name)) return ci;
		}
		return ci;
	}
	
	public static void playCustomSound(GunsPlus plugin,Player player, String url)
	{
		SoundManager SM = SpoutManager.getSoundManager();
		SpoutPlayer sp = SpoutManager.getPlayer(player);
		SM.playCustomSoundEffect(plugin, sp, url, false, sp.getLocation() ,25, 50);
		SM.playGlobalCustomSoundEffect(plugin, url, false, sp.getLocation(), 100, 100);
	}

	
	public static boolean isTransparent(Block block) {
		Material m = block.getType();
			if(GunsPlus.transparentMaterials.contains(m)){
				return true;
			}
		return false;
	}

	public static List<Entity> getNearbyEntities(Location loc, double radiusX, double radiusY,
			double radiusZ) {
		Entity e = loc.getWorld().spawn(loc, ExperienceOrb.class);
		List<Entity> entities = e.getNearbyEntities(radiusX, radiusY, radiusZ);
		e.remove();
		return entities;
	}
	
	
	public static Vector getDirection(Location l) {
        Vector vector = new Vector();
 
        double rotX = l.getYaw();
	    double rotY = l.getPitch();

	    vector.setY(-Math.sin(Math.toRadians(rotY)));
	
	    double h = Math.cos(Math.toRadians(rotY));

	    vector.setX(-h * Math.sin(Math.toRadians(rotX)));
	    vector.setZ(h * Math.cos(Math.toRadians(rotX)));
	
	    return vector;
	}

	//not used, but perhaps useful for Grenades+
	public static Location getHandLocation(Player p){
	    Location loc = p.getLocation().clone();
	    
	    double a = loc.getYaw() / 180D * Math.PI + Math.PI / 2;
	    double l = Math.sqrt(0.8D * 0.8D + 0.4D * 0.4D);
	    
	    loc.setX(loc.getX() + l * Math.cos(a) - 0.8D * Math.sin(a));
	    loc.setY(loc.getY() + p.getEyeHeight() - 0.2D);
	    loc.setZ(loc.getZ() + l * Math.sin(a) + 0.8D * Math.cos(a));
	    return loc;
	}
	
	public static boolean isLWC(Block b) {
		if(GunsPlus.lwc != null) {
			if(GunsPlus.lwc.findProtection(b) != null) return true;
			else return false;
		} else return false;
	}
}
