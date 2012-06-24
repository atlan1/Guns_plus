package team.GunsPlus.Util;

import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.item.GenericCustomItem;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.getspout.spoutapi.sound.SoundManager;

import team.ApiPlus.Util.Utils;
import team.GunsPlus.GunsPlus;
import team.GunsPlus.Block.Tripod;
import team.GunsPlus.Block.TripodData;
import team.GunsPlus.Enum.EffectSection;
import team.GunsPlus.Enum.EffectType;
import team.GunsPlus.Item.Addition;
import team.GunsPlus.Item.Ammo;
import team.GunsPlus.Item.Gun;
import team.GunsPlus.Manager.ConfigLoader;

public class Util {
	
	public static boolean containsCustomItems(List<ItemStack> items){
		for(ItemStack i : items){
			if(isCustomItem(i)){
				return true;
			}
		}
		return false;
	}
	
	public static boolean isCustomItem(ItemStack item){
		return new SpoutItemStack(item).isCustomItem();
	}
	
	public static Block getBlockInSight(Location l, int blockIndex, int maxradius){
		BlockIterator bi = new BlockIterator(l.getWorld(), l.toVector(), l.getDirection(), 0d, maxradius);
		Block b = null;
		for(int i = 0; i<blockIndex; i++){
			if(bi.hasNext()){
				b =  bi.next(); 
			}else break;
		}
		return b;
	}

	public static void warn(String msg) {
		if (GunsPlus.warnings) {
			GunsPlus.log.warning(GunsPlus.PRE + " " + msg);
		}
	}

	public static void info(String msg) {
		GunsPlus.log.info(GunsPlus.PRE + " " + msg);
	}
	
	public static void debug(Exception e){
		if(GunsPlus.debug){
			GunsPlus.log.info(GunsPlus.PRE + "[Debug] " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static Projectile launchProjectile(Class<? extends Projectile> c,
			Location from, Location to, float speed) {
		Projectile e = from.getWorld().spawn(from, c);
		e.setVelocity(to.toVector().multiply(speed));
		Bukkit.getPluginManager().callEvent(new ProjectileLaunchEvent(e));
		return e;
	}

	public static boolean enteredTripod(SpoutPlayer sp) {
		for (TripodData td : GunsPlus.allTripodBlocks) {
			if (td.getOwner() == null)
				continue;
			if (td.getOwner().getPlayer().equals(sp) && td.isEntered()) {
				return true;
			}
		}
		return false;
	}

	public static TripodData getTripodDataOfEntered(SpoutPlayer sp) {
		for (TripodData td : GunsPlus.allTripodBlocks) {
			if (td.getOwner() == null)
				continue;
			if (td.getOwner().getPlayer().equals(sp) && td.isEntered()) {
				return td;
			}
		}
		return null;
	}

	public static TripodData loadTripodData(Block b) {
		for (TripodData td : GunsPlus.allTripodBlocks)
			if (td.getLocation().getBlock().equals(b))
				return td;
		return null;
	}

	public static TripodData loadTripodData(Location location) {
		return loadTripodData(location.getBlock());
	}

	public static boolean isTripod(Location l) {
		for (TripodData td : GunsPlus.allTripodBlocks)
			if (td.getLocation().toVector().equals(l.toVector()))
				return true;
		return false;
	}

	public static boolean isTripod(Block b) {
		return isTripod(b.getLocation());
	}

	public static boolean canSee(final Location observer, final Location observed, int range) {
		Location o = observer.clone();
		Location w = observed.clone();
		if(o.toVector().distance(w.toVector())>range) return false;
		BlockIterator bitr = new BlockIterator(Utils.setLookingAt(o, w), 0, range);
		while (bitr.hasNext()) {
			Block b = bitr.next();
			if(b.equals(w.getBlock())) return true;
			if (!Utils.isTransparent(b)) {
				break;
			}
		}
		return false;
	}

	public static Location getMiddle(Location l, float YShift) {
 		Location loc = l;
		loc = loc.getBlock().getLocation();
		Vector vec = loc.toVector();
		vec.add(new Vector(0.5, YShift, 0.5));
		loc = vec.toLocation(loc.getWorld());
		return loc;
	}

	public static boolean isGunsPlusMaterial(String name) {
		for (int j = 0; j < GunsPlus.allGuns.size(); j++) {
			if (((GenericCustomItem)GunsPlus.allGuns.get(j)).getName().equalsIgnoreCase(name))
				return true;
		}
		for (int j = 0; j < GunsPlus.allAmmo.size(); j++) {
			if (GunsPlus.allAmmo.get(j).getName().equalsIgnoreCase(name))
				return true;
		}
		for (int j = 0; j < GunsPlus.allAdditions.size(); j++) {
			if (GunsPlus.allAdditions.get(j).getName().equalsIgnoreCase(name))
				return true;
		}
		if(Tripod.tripodenabled&&GunsPlus.tripod.getName().equals(name))
			return true;
		return false;
	}

	public static Object getGunsPlusMaterial(String name) {
		Object cm = null;
		if(!isGunsPlusMaterial(name)) return cm;
		for (int i = 0; i < GunsPlus.allGuns.size(); i++) {
			if (((GenericCustomItem)GunsPlus.allGuns.get(i)).getName().equalsIgnoreCase(name)) {
				cm = GunsPlus.allGuns.get(i);
				return cm;
			}
		}
		for (int j = 0; j < GunsPlus.allAmmo.size(); j++) {
			if (GunsPlus.allAmmo.get(j).getName().equalsIgnoreCase(name)) {
				cm = GunsPlus.allAmmo.get(j);
				return cm;
			}
		}
		for (int j = 0; j < GunsPlus.allAdditions.size(); j++) {
			if (GunsPlus.allAdditions.get(j).getName().equalsIgnoreCase(name)) {
				cm = GunsPlus.allAdditions.get(j);
				return cm;
			}
		}
		if(Tripod.tripodenabled&&name.equals(GunsPlus.tripod.getName()))
			cm = GunsPlus.tripod;
		return cm;
	}
	
	public static void playCustomSound(GunsPlus plugin, Location l, String url,
			int volume) {
		SoundManager SM = SpoutManager.getSoundManager();
		SM.playGlobalCustomSoundEffect(plugin, url, false, l, 40, volume);
	}

	public static void printCustomIDs() {
		if (ConfigLoader.generalConfig.getBoolean("id-info-guns", true)) {
			GunsPlus.log.log(Level.INFO, GunsPlus.PRE
					+ " ------------  ID's of the guns: -----------------");
			if (GunsPlus.allGuns.isEmpty())
				GunsPlus.log.log(Level.INFO, "EMPTY");
			for (Gun gun : GunsPlus.allGuns) {
				GunsPlus.log.log(Level.INFO, "ID of " + ((GenericCustomItem) gun).getName() + ":"
						+ new SpoutItemStack((GenericCustomItem)gun).getTypeId() + ":"
						+ new SpoutItemStack((GenericCustomItem)gun).getDurability());
			}
		}
		if (ConfigLoader.generalConfig.getBoolean("id-info-ammo", true)) {
			GunsPlus.log.log(Level.INFO, GunsPlus.PRE
					+ " ------------  ID's of the ammo: -----------------");
			if (GunsPlus.allAmmo.isEmpty())
				GunsPlus.log.log(Level.INFO, "EMPTY");
			for (Ammo ammo : GunsPlus.allAmmo) {
				GunsPlus.log.log(Level.INFO, "ID of " + ammo.getName() + ":"
						+ new SpoutItemStack(ammo).getTypeId() + ":"
						+ new SpoutItemStack(ammo).getDurability());
			}
		}
		if (ConfigLoader.generalConfig.getBoolean("id-info-additions", true)) {
			GunsPlus.log
					.log(Level.INFO,
							GunsPlus.PRE
									+ " ------------  ID's of the additions: -----------------");
			if (GunsPlus.allAdditions.isEmpty())
				GunsPlus.log.log(Level.INFO, "EMPTY");
			for (Addition add : GunsPlus.allAdditions) {
				GunsPlus.log.log(Level.INFO, "ID of " + add.getName() + ":"
						+ new SpoutItemStack(add).getTypeId() + ":"
						+ new SpoutItemStack(add).getDurability());
			}
		}
		if(Tripod.tripodenabled){
			info(" ------------ loaded the tripod block --------------");
			info(" ID: "+new SpoutItemStack(GunsPlus.tripod).getTypeId()+":"+new SpoutItemStack(GunsPlus.tripod).getDurability());
		}
	}

	public static boolean isAllowedInEffectSection(EffectType efftyp,
			EffectSection effsec) {
		switch (effsec) {
		case SHOOTER:
			switch (efftyp) {
			case EXPLOSION:
				return false;
			case LIGHTNING:
				return false;
			case PARTICLE:
				return false;
			case BURN:
				return true;
			case MOVE:
				return true;
			case SPAWN:
				return false;
			case POTION:
				return true;
			case PLACE:
				return false;
			case BREAK:
				return false;
			}
			break;
		case SHOOTERLOCATION:
			switch (efftyp) {
			case EXPLOSION:
				return true;
			case LIGHTNING:
				return true;
			case PARTICLE:
				return true;
			case BURN:
				return true;
			case MOVE:
				return false;
			case SPAWN:
				return true;
			case POTION:
				return false;
			case PLACE:
				return true;
			case BREAK:
				return true;
			}
			break;
		case TARGETLOCATION:
			switch (efftyp) {
			case EXPLOSION:
				return true;
			case LIGHTNING:
				return true;
			case PARTICLE:
				return true;
			case BURN:
				return true;
			case MOVE:
				return false;
			case SPAWN:
				return true;
			case POTION:
				return false;
			case PLACE:
				return true;
			case BREAK:
				return true;
			}
			break;
		case TARGETENTITY:
			switch (efftyp) {
			case EXPLOSION:
				return false;
			case LIGHTNING:
				return false;
			case PARTICLE:
				return false;
			case BURN:
				return true;
			case MOVE:
				return true;
			case SPAWN:
				return false;
			case POTION:
				return true;
			case PLACE:
				return false;
			case BREAK:
				return false;
			}
			break;
		case FLIGHTPATH:
			switch (efftyp) {
			case EXPLOSION:
				return true;
			case LIGHTNING:
				return true;
			case PARTICLE:
				return true;
			case BURN:
				return true;
			case MOVE:
				return false;
			case SPAWN:
				return true;
			case POTION:
				return false;
			case PLACE:
				return true;
			case BREAK:
				return true;
			}
			break;
		case UNDEFINED:
			return false;
		}
		return false;
	}

	public static boolean isBlockAction(Action a) {
		switch (a) {
		case RIGHT_CLICK_BLOCK:
			return true;
		case LEFT_CLICK_BLOCK:
			return true;
		}
		return false;
	}
}
