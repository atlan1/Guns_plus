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

import team.GunsPlus.GunsPlus;

public class Util {
	
	public static boolean hasSpoutcraft(Player p){
		if(SpoutManager.getPlayer(p).isSpoutCraftEnabled())
			return true;
		return false;
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
}
