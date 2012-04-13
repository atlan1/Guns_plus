package team.GunsPlus.Util;

import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.CustomItem;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.getspout.spoutapi.sound.SoundManager;

import com.sk89q.worldguard.protection.flags.DefaultFlag;

import team.GunsPlus.Block.TripodData;
import team.GunsPlus.Enum.EffectSection;
import team.GunsPlus.Enum.EffectType;
import team.GunsPlus.GunsPlus;
import team.GunsPlus.Item.Addition;
import team.GunsPlus.Item.Ammo;
import team.GunsPlus.Item.Gun;

public class Util {

	public static void warn(String msg) {
		if (GunsPlus.warnings) {
			GunsPlus.log.warning(GunsPlus.PRE + " " + msg);
		}
	}

	public static void info(String msg) {
		GunsPlus.log.info(GunsPlus.PRE + " " + msg);
	}

	public static void warnIfNull(Object o, String msg) {
		if (o == null)
			warn(msg);
	}

	public static Projectile launchProjectile(Class<? extends Projectile> c,
			Location from, Location to, float speed) {
		Projectile e = from.getWorld().spawn(from, c);
		e.setVelocity(to.toVector().multiply(speed));
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

	public static boolean canSee(Location observer, Location observed, int range) {
		BlockIterator bitr = new BlockIterator(
				setLookingAt(observer, observed), 0, range);
		while (bitr.hasNext()) {
			Block b = bitr.next();
			if (!Util.isTransparent(b)) {
				return false;
			}
		}
		return true;
	}

	public static Location getMiddle(Location l, float YShift) {
		Location loc = l;
		loc = loc.getBlock().getLocation();
		Vector vec = loc.toVector();
		vec.add(new Vector(0.5, YShift, 0.5));
		loc = vec.toLocation(loc.getWorld());
		return loc;
	}

	public static boolean isGunsPlusItem(String name) {
		for (int j = 0; j < GunsPlus.allGuns.size(); j++) {
			if (GunsPlus.allGuns.get(j).getName().equalsIgnoreCase(name))
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
		return false;
	}

	public static CustomItem getGunsPlusItem(String name) {
		CustomItem ci = null;
		for (int i = 0; i < GunsPlus.allGuns.size(); i++) {
			if (GunsPlus.allGuns.get(i).getName().equalsIgnoreCase(name)) {
				ci = GunsPlus.allGuns.get(i);
				return ci;
			}
		}
		for (int j = 0; j < GunsPlus.allAmmo.size(); j++) {
			if (GunsPlus.allAmmo.get(j).getName().equalsIgnoreCase(name)) {
				ci = GunsPlus.allAmmo.get(j);
				return ci;
			}
		}
		for (int j = 0; j < GunsPlus.allAdditions.size(); j++) {
			if (GunsPlus.allAdditions.get(j).getName().equalsIgnoreCase(name)) {
				ci = GunsPlus.allAdditions.get(j);
				return ci;
			}
		}
		return ci;
	}

	public static void playCustomSound(GunsPlus plugin, Location l, String url,
			int volume) {
		SoundManager SM = SpoutManager.getSoundManager();
		SM.playGlobalCustomSoundEffect(plugin, url, false, l, 40, volume);
	}

	public static boolean isTransparent(Block block) {
		Material m = block.getType();
		if (GunsPlus.transparentMaterials.contains(m)) {
			return true;
		}
		return false;
	}

	public static List<Entity> getNearbyEntities(Location loc, double radiusX,
			double radiusY, double radiusZ) {
		Entity e = loc.getWorld().spawn(loc, ExperienceOrb.class);
		List<Entity> entities = e.getNearbyEntities(radiusX, radiusY, radiusZ);
		e.remove();
		return entities;
	}

	public static int getRandomInteger(int start, int end) {
		Random rand = new Random();
		return start + rand.nextInt(end + 1);
	}

	public static boolean is1x1x2(Entity e) {
		int id = e.getEntityId();

		switch (id) {
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

	public static boolean is1x1x1(Entity e) {
		int id = e.getEntityId();

		switch (id) {
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

	public static boolean is1x1x3(Entity e) {
		int id = e.getEntityId();

		switch (id) {
		case 58:
			return true;
		}
		return false;
	}

	public static boolean is2x2x1(Entity e) {
		int id = e.getEntityId();

		switch (id) {
		case 59:
			return true;
		case 52:
			return true;
		}
		return false;
	}

	public static boolean is2x2x2(Entity e) {
		int id = e.getEntityId();
		switch (id) {
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

	public static void printCustomIDs() {
		if (GunsPlus.generalConfig.getBoolean("id-info-guns", true)) {
			GunsPlus.log.log(Level.INFO, GunsPlus.PRE
					+ " ------------  ID's of the guns: -----------------");
			if (GunsPlus.allGuns.isEmpty())
				GunsPlus.log.log(Level.INFO, "EMPTY");
			for (Gun gun : GunsPlus.allGuns) {
				GunsPlus.log.log(Level.INFO, "ID of " + gun.getName() + ":"
						+ new SpoutItemStack(gun).getTypeId() + ":"
						+ new SpoutItemStack(gun).getDurability());
			}
		}
		if (GunsPlus.generalConfig.getBoolean("id-info-ammo", true)) {
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
		if (GunsPlus.generalConfig.getBoolean("id-info-additions", true)) {
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
		if(GunsPlus.tripodenabled){
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
			}
			break;
		case SHOOTERLOCATION:
			switch (efftyp) {
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
			}
			break;
		case TARGETLOCATION:
			switch (efftyp) {
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
			}
			break;
		case TARGETENTITY:
			switch (efftyp) {
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
			}
			break;
		case FLIGHTPATH:
			switch (efftyp) {
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
			}
			break;
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

	public static Location setLookingAt(Location loc, Location lookat) {
		loc = loc.clone();
		double dx = lookat.getX() - loc.getX();
		double dy = lookat.getY() - loc.getY();
		double dz = lookat.getZ() - loc.getZ();

		if (dx != 0) {
			if (dx < 0) {
				loc.setYaw((float) (1.5 * Math.PI));
			} else {
				loc.setYaw((float) (0.5 * Math.PI));
			}
			loc.setYaw((float) loc.getYaw() - (float) Math.atan(dz / dx));
		} else if (dz < 0) {
			loc.setYaw((float) Math.PI);
		}
		double dxz = Math.sqrt(Math.pow(dx, 2) + Math.pow(dz, 2));
		loc.setPitch((float) -Math.atan(dy / dxz));
		loc.setYaw(-loc.getYaw() * 180f / (float) Math.PI);
		loc.setPitch(loc.getPitch() * 180f / (float) Math.PI);

		return loc;
	}

	public static Location getHandLocation(Player p) {
		Location loc = p.getLocation().clone();

		double a = loc.getYaw() / 180D * Math.PI + Math.PI / 2;
		double l = Math.sqrt(0.8D * 0.8D + 0.4D * 0.4D);

		loc.setX(loc.getX() + l * Math.cos(a) - 0.8D * Math.sin(a));
		loc.setY(loc.getY() + p.getEyeHeight() - 0.2D);
		loc.setZ(loc.getZ() + l * Math.sin(a) + 0.8D * Math.cos(a));
		return loc;
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

	public static boolean tntIsAllowedInRegion(Location loc) {
		if (GunsPlus.wg != null) {
			if (!GunsPlus.wg.getGlobalRegionManager().allows(DefaultFlag.TNT,
					loc)) {
				return false;
			} else
				return true;
		} else
			return false;
	}
}
