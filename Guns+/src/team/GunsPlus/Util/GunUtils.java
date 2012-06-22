package team.GunsPlus.Util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.MobEffect;
import net.minecraft.server.Packet42RemoveMobEffect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;
import org.getspout.spoutapi.gui.GenericTexture;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.item.GenericCustomItem;
import org.getspout.spoutapi.player.SpoutPlayer;

import team.ApiPlus.API.Effect.Effect;
import team.ApiPlus.API.Effect.EntityEffect;
import team.ApiPlus.API.Effect.LocationEffect;
import team.ApiPlus.Util.Utils;
import team.GunsPlus.API.Event.*;
import team.GunsPlus.Enum.EffectType;
import team.GunsPlus.Enum.Projectile;
import team.GunsPlus.GunsPlus;
import team.GunsPlus.GunsPlusPlayer;
import team.GunsPlus.Item.Addition;
import team.GunsPlus.Item.Ammo;
import team.GunsPlus.Item.Gun;

public class GunUtils {

	public static Gun getGun(String name) {
		for (Gun g : GunsPlus.allGuns)
			if (((GenericCustomItem)g).getName().equalsIgnoreCase(name))
				return g;
		return null;
	}

	public static String getFullGunName(Gun g, Addition... adds) {
		String name = ((GenericCustomItem)g).getName();
		for (Addition a : adds)
			name += "+" + a.getName();
		return name;
	}

	public static String getRawGunName(Gun g) {
		String name = ((GenericCustomItem)g).getName();
		return name.split("\\+")[0];
	}

	public static boolean holdsGun(SpoutPlayer p) {
		ItemStack is = p.getItemInHand();
		for (Gun g : GunsPlus.allGuns) {
			SpoutItemStack sis = new SpoutItemStack((GenericCustomItem)g);
			if (is.getTypeId() == sis.getTypeId()
					&& is.getDurability() == sis.getDurability()) {
				return true;
			}
		}
		return false;
	}

	public static void shootProjectile(Location from, Location to, Projectile pro) {
		float speed = (float) pro.getSpeed();
		if (pro.equals(Projectile.ARROW)) {
			Util.launchProjectile(Arrow.class, from, to, speed);
		} else if (pro.equals(Projectile.FIREBALL)) {
			Util.launchProjectile(Fireball.class, from, to, speed);
		} else if (pro.equals(Projectile.SNOWBALL)) {
			Util.launchProjectile(Snowball.class, from, to, speed);
		} else if (pro.equals(Projectile.EGG)) {
			Util.launchProjectile(Egg.class, from, to, speed);
		} else if (pro.equals(Projectile.ENDERPEARL)) {
			Util.launchProjectile(EnderPearl.class, from, to, speed);
		} else if(pro.equals(Projectile.FIRECHARGE)) {
			Util.launchProjectile(SmallFireball.class, from, to, speed); 
		}
	}

	public static HashMap<LivingEntity, Integer> getTargets(Location l, Gun g,
			boolean zoom) {
		HashMap<LivingEntity, Integer> targets = new HashMap<LivingEntity, Integer>();
		Location loc = l.clone();
		HashMap<LivingEntity, Integer> e = null;
		int acc = Utils.getRandomInteger(0, 101) + 1;
		int missing = (Integer) g.getProperty(zoom ? "MISSING_IN" : "MISSING_OUT");
		float randomfactor = (Float) g.getProperty("RANDOMFACTOR");
		int spread = (Integer) (zoom ? ((Integer)g.getProperty("SPREAD_IN") / 2) + 1 : ((Integer)g
				.getProperty("SPREAD_OUT") / 2 + 1));
		if (acc >= missing) {
			if (spread <= 0 && randomfactor <= 0) {
				targets = getTargetEntities(l, g);
			} else {
				for (int i = 1; i <= spread; i += 4) {
					loc = l.clone();
					loc.setYaw(loc.getYaw()
							+ Utils.getRandomInteger(i,
									Math.round(i * randomfactor)));
					e = getTargetEntities(loc, g);
					targets.putAll(e);
					loc.setYaw(loc.getYaw()
							- Utils.getRandomInteger(i,
									i + Math.round(i * randomfactor)));
					e = getTargetEntities(loc, g);
					targets.putAll(e);
					loc.setPitch(loc.getPitch()
							+ Utils.getRandomInteger(i,
									i + Math.round(i * randomfactor)));
					e = getTargetEntities(loc, g);
					targets.putAll(e);
					loc.setPitch(loc.getPitch()
							- Utils.getRandomInteger(i,
									i + Math.round(i * randomfactor)));
					e = getTargetEntities(loc, g);
					targets.putAll(e);
				}
			}
		}
		return targets;
	}

	public static HashMap<LivingEntity, Integer> getTargetEntities(
			Location loc, Gun g) {
		HashMap<LivingEntity, Integer> targets = new HashMap<LivingEntity, Integer>();
		BlockIterator bitr = new BlockIterator(loc, 0d,
				((Number) g.getProperty("RANGE")).intValue());
		Block b;
		Location l, el;
		while (bitr.hasNext()) {
			b = bitr.next();
			Location blockcenter = Util.getMiddle(b.getLocation(), -0.5f);
			Set<LivingEntity> entities = new HashSet<LivingEntity>();
			if (!Util.isTransparent(b))
				break;
			for (Entity e : new ArrayList<Entity>(Utils.getNearbyEntities(b.getLocation(), 0.4, 0.4,
					0.4))) {
				if (e instanceof LivingEntity) {
					entities.add((LivingEntity) e);
				}
			}
			for (LivingEntity e : entities) {
				l = Util.getMiddle(e.getLocation(), -0.5f);
				el = e.getEyeLocation();
				double changedamage = (int) Math.ceil(((Number) g
						.getProperty("CHANGEDAMAGE")).floatValue()
						* loc.toVector().distance(l.toVector()));
				if (l.toVector().distance(blockcenter.toVector()) > el
						.toVector().distance(blockcenter.toVector())) {
					targets.put(
							e,
							(int) (((Number) g.getProperty("HEADSHOTDAMAGE")).intValue() + changedamage<0?0:((Number) g.getProperty("HEADSHOTDAMAGE")).intValue() + changedamage)
									* -1);
				} else {
					targets.put(e,
							(int) (((Number) g.getProperty("DAMAGE")).intValue()<0?0:((Number) g.getProperty("DAMAGE")).intValue() + changedamage));
				}
			}
		}
		return targets;
	}

	public static void removeAmmo(Inventory inv, ArrayList<ItemStack> ammo) {
		if (ammo.isEmpty())
			return;
		HashMap<Integer, ? extends ItemStack> invAll = new HashMap<Integer, SpoutItemStack>();
		ItemStack ammoStack = null;
		for (ItemStack theStack : ammo) {
			invAll = inv.all(theStack.getTypeId());
			for (int j = 0; j < inv.getSize(); j++) {
				if (invAll.containsKey(j)) {
					ItemStack hi = invAll.get(j);
					if (hi.getTypeId() == theStack.getTypeId()
							&& hi.getDurability() == theStack.getDurability()) {
						ammoStack = hi;
						break;
					}
				}
			}
		}
		if (ammoStack == null) {
			return;
		}
		if (ammoStack.getAmount() > 1) {
			ammoStack.setAmount(ammoStack.getAmount() - 1);
		} else {
			inv.remove(ammoStack);
		}
	}

	@SuppressWarnings("deprecation")
	public static void removeGunInHand(SpoutPlayer sp) {
		if (isGun(sp.getItemInHand())) {
			ItemStack remove = sp.getItemInHand();
			if (remove == null)
				return;
			if (remove.getAmount() > 1) {
				remove.setAmount(remove.getAmount() - 1);
			} else {
				remove = null;
				sp.setItemInHand(null);
			}
			sp.updateInventory();
		}
	}

	public static Ammo getFirstCustomAmmo(Inventory inv,
			ArrayList<ItemStack> ammo) {
		if (ammo.isEmpty())
			return null;
		HashMap<Integer, ? extends ItemStack> invAll = new HashMap<Integer, SpoutItemStack>();
		ItemStack ammoStack = null;
		for (ItemStack theStack : ammo) {
			invAll = inv.all(theStack.getTypeId());
			for (int j = 0; j < inv.getSize(); j++) {
				if (invAll.containsKey(j)) {
					ItemStack hi = invAll.get(j);
					if (hi.getTypeId() == theStack.getTypeId()
							&& hi.getDurability() == theStack.getDurability()) {
						ammoStack = hi;
						for (Ammo y : GunsPlus.allAmmo) {
							if (new SpoutItemStack(y).getDurability() == ammoStack
									.getDurability()
									&& new SpoutItemStack(y).getTypeId() == ammoStack
											.getTypeId())
								return y;
						}
					}
				}
			}
		}
		return null;
	}

	public static boolean checkInvForAmmo(Inventory inv,
			ArrayList<ItemStack> ammo) {
		if (ammo.isEmpty())
			return true;
		HashMap<Integer, ? extends ItemStack> invAll = new HashMap<Integer, SpoutItemStack>();
		for (ItemStack theStack : ammo) {
			invAll = inv.all(theStack.getTypeId());
			for (int j = 0; j < inv.getSize(); j++) {
				if (invAll.containsKey(j)) {
					ItemStack hi = invAll.get(j);
					if (hi.getTypeId() == theStack.getTypeId()
							&& hi.getDurability() == theStack.getDurability()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static boolean checkInvForGun(Inventory inv, Gun g) {
		if (g == null)
			return true;
		HashMap<Integer, ? extends ItemStack> invAll = new HashMap<Integer, SpoutItemStack>();
		SpoutItemStack theStack = new SpoutItemStack((GenericCustomItem)g);
		invAll = inv.all(theStack.getTypeId());
		for (int j = 0; j < inv.getSize(); j++) {
			if (invAll.containsKey(j)) {
				ItemStack hi = invAll.get(j);
				if (hi.getTypeId() == theStack.getTypeId()
						&& hi.getDurability() == theStack.getDurability()) {
					return true;
				}
			}
		}
		return false;
	}

	public static int getAmmoCount(Inventory inv, ArrayList<ItemStack> ammo) {
		HashMap<Integer, ? extends ItemStack> invAll = new HashMap<Integer, SpoutItemStack>();
		int counter = 0;
		if (ammo == null)
			return counter;
		for (ItemStack theStack : ammo) {
			invAll = inv.all(theStack.getTypeId());
			for (int j = 0; j < inv.getSize(); j++) {
				if (invAll.containsKey(j)) {
					ItemStack hi = invAll.get(j);
					if (hi.getTypeId() == theStack.getTypeId()
							&& hi.getDurability() == theStack.getDurability()) {
						counter += hi.getAmount();
					}
				}
			}
		}
		return counter;
	}
	
	@SuppressWarnings("unchecked")
	public static void performEffects(Shooter shooter, HashSet<LivingEntity> tars, Gun gun){
		HashSet<LivingEntity> targets = new HashSet<LivingEntity>();
		LivingEntity shooterEntity = null;
		Location shooterLocation, targetLocation;
		int gunrange = (Integer) gun.getProperty("RANGE");
		if(shooter instanceof LivingShooter){
			shooterEntity = ((LivingShooter)shooter).getLivingEntity();
		}
		for(Effect e : (List<Effect>)gun.getProperty("EFFECTS")){
			
			Bukkit.getServer().getPluginManager().callEvent(new GunEffectEvent(shooter, gun, e));
			if(targets.isEmpty()&&shooterEntity!=null){//make sure effects will be performed
				targets.add(shooterEntity);            //if there are no targets
			}
			for(LivingEntity target : targets){
				targetLocation = target.getLocation();
				shooterLocation = shooter.getLocation();
				if(shooterEntity!=null&&shooterEntity.equals(target)){
					targetLocation = shooterEntity.getTargetBlock(null, gunrange).getLocation();
				}
				switch(EffectType.getType(e.getClass())){
					case BREAK:
						EffectUtils.performLocationEffect((LocationEffect) e, shooterLocation, targetLocation);
						break;
					case PLACE:
						EffectUtils.performLocationEffect((LocationEffect) e, shooterLocation, targetLocation);
						break;
					case POTION:
						EffectUtils.performEntityEffect((EntityEffect) e, shooterEntity, target);
						break;
					case PARTICLE:
						EffectUtils.performLocationEffect((LocationEffect) e, shooterLocation, targetLocation);
						break;
					case BURN:
						EffectUtils.performEntityEffect((EntityEffect) e, shooterEntity, target);
						break;
					case SPAWN:
						EffectUtils.performLocationEffect((LocationEffect) e, shooterLocation, targetLocation);
						break;
					case LIGHTNING:
						EffectUtils.performLocationEffect((LocationEffect) e, shooterLocation, targetLocation);
						break;
					case EXPLOSION:
						EffectUtils.performLocationEffect((LocationEffect) e, shooterLocation, targetLocation);
						break;
					case MOVE:
						EffectUtils.performEntityEffect((EntityEffect) e, shooterEntity, target);
						break;
				}
			}
		}
	}

	public static Gun getGunInHand(SpoutPlayer p) {
		ItemStack is = p.getItemInHand();

		if (holdsGun(p)) {
			for (Gun g : GunsPlus.allGuns) {
				SpoutItemStack sis = new SpoutItemStack((GenericCustomItem)g);
				if (is.getTypeId() == sis.getTypeId()
						&& is.getDurability() == sis.getDurability()) {
					return g;
				}
			}
		}
		return null;
	}

	public static Gun getGun(ItemStack item) {
		for (Gun g : GunsPlus.allGuns) {
			SpoutItemStack sis = new SpoutItemStack((GenericCustomItem)g);
			if (item.getTypeId() == sis.getTypeId()
					&& item.getDurability() == sis.getDurability()) {
				return g;
			}
		}
		return null;
	}

	public static void zoomOut(GunsPlusPlayer gp) {
		// PotionEffect pe = new PotionEffect(PotionEffectType.SLOW, 0, 100);
		// p.addPotionEffect(pe, true);
		SpoutPlayer sp = (SpoutPlayer) gp.getPlayer();
		CraftPlayer cp = (CraftPlayer) sp;

		try {
			Field field = EntityLiving.class.getDeclaredField("effects");
			field.setAccessible(true);
			@SuppressWarnings("rawtypes")
			HashMap effects = (HashMap) field.get(cp.getHandle());
			effects.remove(2);
			EntityPlayer player = cp.getHandle();
			player.netServerHandler.sendPacket(new Packet42RemoveMobEffect(
					player.id, new MobEffect(2, 0, 0)));
			cp.getHandle().getDataWatcher().watch(8, Integer.valueOf(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
		sp.getMainScreen().removeWidget(
				PlayerUtils.getPlayerBySpoutPlayer(sp).getZoomtexture());
	}

	public static void zoomIn(GunsPlus plugin, GunsPlusPlayer gp,
			GenericTexture zTex, int zoomfactor) {
		// SpoutPlayer sp = (SpoutPlayer) p;
		// PotionEffect pe = new PotionEffect(PotionEffectType.SLOW, 24000,
		// zoomfactor);
		// p.addPotionEffect(pe, true);
		SpoutPlayer sp = (SpoutPlayer) gp.getPlayer();
		CraftPlayer cp = (CraftPlayer) sp;

		cp.getHandle().addEffect(new MobEffect(2, 24000, zoomfactor));
		try {
			Field field;
			field = EntityLiving.class.getDeclaredField("effects");
			field.setAccessible(true);
			@SuppressWarnings("rawtypes")
			HashMap effects = (HashMap) field.get(cp.getHandle());
			effects.remove(2);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}

		if (!(zTex == null)) {
			GenericTexture t = zTex;
			t.setHeight(sp.getMainScreen().getHeight()).setWidth(
					sp.getMainScreen().getWidth());
			sp.getMainScreen().attachWidget(plugin, t);
			gp.setZoomtexture(zTex);
		}
	}

	public static boolean isHudEnabled(Gun g) {
		return ((Boolean) g.getProperty("HUDENABLED"));
	}

	public static boolean isMountable(Gun g) {
		return ((Boolean) g.getProperty("MOUNTABLE"));
	}
	
	public static boolean isShootable(Gun g) {
		return ((Boolean) g.getProperty("SHOOTABLE"));
	}

	public static boolean isGun(ItemStack i) {
		if (i != null)
			for (Gun g : GunsPlus.allGuns) {
				SpoutItemStack sis = new SpoutItemStack((GenericCustomItem)g);
				if (i.getTypeId() == sis.getTypeId()
						&& i.getDurability() == sis.getDurability()) {
					return true;
				}
			}
		return false;
	}
	public static List<Block> getTargetBlocks(Location loc, Gun g) {
		List<Block> targets = new ArrayList<Block>();
		BlockIterator bitr = new BlockIterator(loc.add(0, 0, 0), 0d,
				(Integer) g.getProperty("RANGE"));
		Block b;
		while (bitr.hasNext()) {
			b = bitr.next();
			if (!Util.isTransparent(b)) targets.add(b);
		}
		return targets;
	}
}