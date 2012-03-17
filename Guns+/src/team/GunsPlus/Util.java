package team.GunsPlus;


import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.util.Vector;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.CustomItem;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.getspout.spoutapi.sound.SoundManager;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import team.GunsPlus.Enum.EffectSection;
import team.GunsPlus.Enum.EffectType;
import team.GunsPlus.GunsPlus;
import team.GunsPlus.Item.Ammo;
import team.GunsPlus.Item.Gun;

public class Util {
	
	public static boolean hasSpoutcraft(Player p){
		SpoutPlayer sp = (SpoutPlayer) p;
		if(sp.isSpoutCraftEnabled()){
			return true;
		}
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
		for(int i = 0;i<GunsPlus.allGuns.size();i++){
			if(GunsPlus.allGuns.get(i).getName().equalsIgnoreCase(name)){
				ci = GunsPlus.allGuns.get(i);
				return ci;
			}
		}
		for(int j = 0;j<GunsPlus.allAmmo.size();j++){
			if(GunsPlus.allAmmo.get(j).getName().equalsIgnoreCase(name)){
				ci = GunsPlus.allAmmo.get(j);
				return ci;
			}
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
	
	public static int getRandomInteger(int start, int end){
		Random rand = new Random();
		return start+rand.nextInt(end+1);
	}
	
	public static boolean is1x1x2(Entity e){
		int id = e.getEntityId();
	
		switch(id){
			case 50:
				return true;
			case 51:
				return true;
			case 54:
				return true;
			case 57:
				return true;
			case 61:
				return true;
			case 97:
				return true;
			case 120:
				return true;
		}
		return false;
	}
	
	public static boolean is1x1x1(Entity e){
		int id = e.getEntityId();
	
		switch(id){
			case 60:
				return true;
			case 90:
				return true;
			case 91:
				return true;
			case 92:
				return true;
			case 93:
				return true;
			case 94:
				return true;
			case 95:
				return true;
			case 96:
				return true;
			case 98:
				return true;
		}
		return false;
	}
	
	public static boolean is1x1x3(Entity e){
		int id = e.getEntityId();
	
		switch(id){
			case 58:
				return true;
		}
		return false;
	}
	
	
	public static boolean is2x2x1(Entity e){
		int id = e.getEntityId();
	
		switch(id){
			case 59:
				return true;
			case 52:
				return true;
		}
		return false;
	}
	
	public static boolean is2x2x2(Entity e){
		int id = e.getEntityId();
		switch(id){
			case 56:
				return true;
			case 55:
				return true;
			case 62:
				return true;
			case 99:
				return true;
		}
		return false;
	}
	
	
	public static void printCustomIDs(){
		if (GunsPlus.generalConfig.getBoolean("id-info-guns", true)) {
			GunsPlus.log.log(Level.INFO, GunsPlus.PRE
					+ " ------------  ID's of the guns: -----------------");
			if(GunsPlus.allGuns.isEmpty()) GunsPlus.log.log(Level.INFO, "EMPTY");
			for (Gun gun : GunsPlus.allGuns) {
				GunsPlus.log.log(Level.INFO, "ID of " + gun.getName() + ":"
						+ Material.FLINT.getId() + ":"
						+ new SpoutItemStack(gun).getDurability());
			}
		}
		if (GunsPlus.generalConfig.getBoolean("id-info-ammo", true)) {
			GunsPlus.log.log(Level.INFO, GunsPlus.PRE
					+ " ------------  ID's of the ammo: -----------------");
			if(GunsPlus.allAmmo.isEmpty()) GunsPlus.log.log(Level.INFO, "EMPTY");
			for (Ammo ammo : GunsPlus.allAmmo) {
				GunsPlus.log.log(Level.INFO, "ID of " + ammo.getName() + ":"
						+ Material.FLINT.getId() + ":"
						+ new SpoutItemStack(ammo).getDurability());
			}
		}
	}
	
	public static boolean isAllowedInEffectSection(EffectType efftyp, EffectSection effsec){
		switch(effsec){
			case SHOOTER:
				switch(efftyp){
					case EXPLOSION:
						return false;
					case LIGHTNING:
						return false;
					case SMOKE:
						return false;
					case FIRE:
						return true;
					case PUSH:
						return true;
					case DRAW:
						return true;
					case SPAWN:
						return false;
					case POTION:
						return true;
					case PLACE:
						return false;
					case BREAK:
						return false;
				}break;
			case SHOOTERLOCATION:
				switch(efftyp){
					case EXPLOSION:
						return true;
					case LIGHTNING:
						return true;
					case SMOKE:
						return true;
					case FIRE:
						return true;
					case PUSH:
						return false;
					case DRAW:
						return false;
					case SPAWN:
						return true;
					case POTION:
						return false;
					case PLACE:
						return true;
					case BREAK:
						return true;
				}break;
			case TARGETLOCATION:
				switch(efftyp){
					case EXPLOSION:
						return true;
					case LIGHTNING:
						return true;
					case SMOKE:
						return true;
					case FIRE:
						return true;
					case PUSH:
						return false;
					case DRAW:
						return false;
					case SPAWN:
						return true;
					case POTION:
						return false;
					case PLACE:
						return true;
					case BREAK:
						return true;
				}break;
			case TARGETENTITY:
				switch(efftyp){
					case EXPLOSION:
						return false;
					case LIGHTNING:
						return false;
					case SMOKE:
						return false;
					case FIRE:
						return true;
					case PUSH:
						return true;
					case DRAW:
						return true;
					case SPAWN:
						return false;
					case POTION:
						return true;
					case PLACE:
						return false;
					case BREAK:
						return false;
				}break;
			case FLIGHTPATH:
				switch(efftyp){
					case EXPLOSION:
						return true;
					case LIGHTNING:
						return true;
					case SMOKE:
						return true;
					case FIRE:
						return true;
					case PUSH:
						return false;
					case DRAW:
						return false;
					case SPAWN:
						return true;
					case POTION:
						return false;
					case PLACE:
						return true;
					case BREAK:
						return true;
				}break;
			case UNDEFINED:
				return false;
		}
		return false;
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
	
//	public static boolean canAccessLWCBlock (SpoutPlayer sp , Block b) {
//		if(GunsPlus.lwc != null) {
//			System.out.println(GunsPlus.lwc.canAccessProtection(sp, b));
//			return GunsPlus.lwc.canAccessProtection(sp, b);
//		} else return false;
//	}

	public static boolean isBlockAction(Action a) {
		switch(a){
			case RIGHT_CLICK_BLOCK:
				return true;
			case LEFT_CLICK_BLOCK:
				return true;
		}
		return false;
	}
	
	public static boolean inRegion(Player player, Location loc) {
		if(Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
			WorldGuardPlugin wg = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
			if(!wg.canBuild(player, loc)) {
				return false;
			} else return true;
		} else return false;
	}
}
