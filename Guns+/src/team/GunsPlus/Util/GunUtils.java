package team.GunsPlus.Util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import net.minecraft.server.v1_6_R2.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
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
import org.bukkit.util.Vector;
import org.getspout.spoutapi.gui.GenericTexture;
import org.getspout.spoutapi.inventory.SpoutEnchantment;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.item.GenericCustomItem;
import org.getspout.spoutapi.player.SpoutPlayer;

import team.ApiPlus.API.Property.CollectionProperty;
import team.ApiPlus.API.Property.PropertyHolder;
import team.ApiPlus.API.Property.NumberProperty;
import team.ApiPlus.API.Property.ObjectProperty;
import team.ApiPlus.API.Effect.EffectType;
import team.ApiPlus.API.Effect.Effect;
import team.ApiPlus.API.Effect.EntityEffect;
import team.ApiPlus.API.Effect.LocationEffect;
import team.ApiPlus.Util.Utils;
import team.GunsPlus.API.Classes.LivingShooter;
import team.GunsPlus.API.Classes.Shooter;
import team.GunsPlus.API.Event.*;
import team.GunsPlus.Enum.Projectile;
import team.GunsPlus.GunsPlus;
import team.GunsPlus.GunsPlusPlayer;
import team.GunsPlus.Item.Addition;
import team.GunsPlus.Item.Ammo;
import team.GunsPlus.Item.Gun;

public class GunUtils {

	public static Gun getGun(String name) {
		for(Gun g : GunsPlus.allGuns)
			if(((GenericCustomItem) g).getName().equals(name))
				return g;
		return null;
	}

	public static String getFullGunName(Gun g, Addition... adds) {
		String name = ((GenericCustomItem) g).getName();
		for(Addition a : adds)
			name += "+" + a.getName();
		return name;
	}

	public static String getRawGunName(Gun g) {
		String name = ((GenericCustomItem) g).getName();
		return name.split("\\+")[0];
	}

	public static boolean holdsGun(SpoutPlayer p) {
		ItemStack is = p.getItemInHand();
		for(Gun g : GunsPlus.allGuns) {
			SpoutItemStack sis = new SpoutItemStack((GenericCustomItem) g);
			if(is.getTypeId() == sis.getTypeId() && is.getDurability() == sis.getDurability()) {
				return true;
			}
		}
		return false;
	}

	public static void shootProjectile(Location from, Location to, Projectile pro) {
		float speed = (float) pro.getSpeed();
		if(pro.equals(Projectile.ARROW)) {
			Util.launchProjectile(Arrow.class, from, to, speed);
		} else if(pro.equals(Projectile.FIREBALL)) {
			Util.launchProjectile(Fireball.class, from, to, speed);
		} else if(pro.equals(Projectile.SNOWBALL)) {
			Util.launchProjectile(Snowball.class, from, to, speed);
		} else if(pro.equals(Projectile.EGG)) {
			Util.launchProjectile(Egg.class, from, to, speed);
		} else if(pro.equals(Projectile.ENDERPEARL)) {
			Util.launchProjectile(EnderPearl.class, from, to, speed);
		} else if(pro.equals(Projectile.FIRECHARGE)) {
			Util.launchProjectile(SmallFireball.class, from, to, speed);
		}
	}

	public static HashMap<LivingEntity, Integer> getTargets(Location l, PropertyHolder g, boolean zoom) {
		HashMap<LivingEntity, Integer> targets = new HashMap<LivingEntity, Integer>();
		Location loc = l.clone();
		int bc = ((NumberProperty) g.getProperty("BULLETCOUNT")).getValue().intValue();
		int acc = Utils.getRandomInteger(0, 101) + 1;
		int missing = ((NumberProperty) g.getProperty(zoom ? "MISSING_IN" : "MISSING_OUT")).getValue().intValue();
		float randomfactor = ((NumberProperty) g.getProperty("RANDOMFACTOR")).getValue().floatValue();
		int spread = ((Number) (zoom ? (((NumberProperty) g.getProperty("SPREAD_IN")).getValue().intValue()) : (((NumberProperty) g.getProperty("SPREAD_OUT")).getValue().intValue()))).intValue();
		if(acc >= missing) {
			if(bc>0)
				if(spread <= 0) {
					targets = getTargetEntities(loc, g);
				} else {
					for(int i = 1; i <= spread; i += Math.round(spread/(float)((float)bc/4f))) {
						int rnd = Math.round(i*randomfactor);
						int c = Utils.getRandomInteger(-i+rnd, i+rnd);
						loc = l.clone();
						loc.setYaw(l.getYaw()+c);
						targets.putAll(getTargetEntities(loc, g));
						loc.setYaw(l.getYaw()-c);
						targets.putAll(getTargetEntities(loc, g));
						c = Utils.getRandomInteger(-i+rnd, i+rnd);
						loc.setPitch(l.getPitch()+c);
						targets.putAll(getTargetEntities(loc, g));
						loc.setPitch(l.getPitch()-c);
						targets.putAll(getTargetEntities(loc, g));
					}
				}
		}
		return targets;
	}

	public static HashMap<LivingEntity, Integer> getTargetEntities(final Location pos, PropertyHolder ph) {
		HashMap<LivingEntity, Integer> targets = new HashMap<LivingEntity, Integer>();
		int range = ((NumberProperty) ph.getProperty("RANGE")).getValue().intValue();
		for(Entity e : new ArrayList<Entity>(Utils.getNearbyEntities(pos, range, range, range))) {
			if(e instanceof LivingEntity) {
				EntityLiving el = ((org.bukkit.craftbukkit.v1_6_R2.entity.CraftLivingEntity) ((LivingEntity) e)).getHandle();
				double d = (((LivingEntity) e).getEyeLocation()).distance(pos);
				if(isWall(d, pos)) continue;
				AxisAlignedBB aabbB = AxisAlignedBB.a(el.boundingBox.a, el.boundingBox.b, el.boundingBox.c, el.boundingBox.d, el.boundingBox.e, el.boundingBox.f);
				Vector vecb = pos.getDirection().normalize().multiply(d).add(pos.toVector());
				Vec3D vecm = Vec3D.a(vecb.getX(), vecb.getY(), vecb.getZ());
				boolean body = aabbB.a(vecm);
				boolean head = false;
				if(Util.isHumanoid(e)){
					AxisAlignedBB aabbH = AxisAlignedBB.a(el.boundingBox.a, el.boundingBox.e-.4, el.boundingBox.c, el.boundingBox.d, el.boundingBox.e+.3, el.boundingBox.f);
					head = aabbH.a(vecm);
				}
				int damage = 0;
				int changedamage = (int) Math.ceil(((NumberProperty) ph.getProperty("CHANGEDAMAGE")).getValue().floatValue() * d);
//				System.out.println("CD: "+changedamage+ "; BD: "+body+"; HEAD: "+head);
				if(head){
					damage=((NumberProperty)ph.getProperty("HEADSHOTDAMAGE")).getValue().intValue()+changedamage;
					if(damage<0) damage = 0;
					targets.put((LivingEntity)e, damage*-1); //make damage negative to indicate headshot
				}else if(body){
					damage=((NumberProperty)ph.getProperty("DAMAGE")).getValue().intValue()+changedamage;
					if(damage<0) damage = 0;
					targets.put((LivingEntity)e, damage);
				}
			}
		}
		return targets;
	}
	
	
	public static boolean isWall(double range, Location l){
		for(float dist = .5f;dist<=range;dist=dist+.5f){
			Vector vecb = l.getDirection().normalize().multiply(dist).add(l.toVector());
			if(!Utils.isTransparent(vecb.toLocation(l.getWorld()).getBlock()))
				return true;
		}
		return false;
	}
	
	public static boolean consumeAmmo(Inventory inv, ArrayList<ItemStack> ammo) {
		if(ammo.isEmpty())
			return true;
		HashMap<Integer, ? extends ItemStack> invAll = new HashMap<Integer, SpoutItemStack>();
		ItemStack ammoStack = null;
		int slot = 0;
		for(ItemStack theStack : ammo) {
			invAll = inv.all(theStack.getTypeId());
			for(int j = 0; j < inv.getSize(); j++) {
				if(invAll.containsKey(j)) {
					ItemStack hi = invAll.get(j);
					if(hi.getTypeId() == theStack.getTypeId() && hi.getDurability() == theStack.getDurability()) {
						ammoStack = hi;
						slot = j;
						break;
					}
				}
			}
		}
		if(ammoStack == null) {
			return true;
		}
		if(new SpoutItemStack(ammoStack).isCustomItem()){
			Ammo a = getAmmo(ammoStack);
			int maxdur = ((NumberProperty)a.getProperty("AMMODURABILITY")).getValue().intValue();
			int amountbefore = ammoStack.getAmount();
			if(maxdur>0)
				ammoStack = Utils.changeDurability((GenericCustomItem)a, maxdur, ammoStack, (short)1, true);
			inv.setItem(slot, ammoStack);
			if(amountbefore>ammoStack.getAmount()) return true;
		}else{
			if(ammoStack.getAmount() > 1) {
				ammoStack.setAmount(ammoStack.getAmount() - 1);
				inv.setItem(slot, ammoStack);
			} else {
				inv.remove(ammoStack);
			}
			return true;
		}
		return false;
	}

	public static boolean clipEmpty(Inventory inv, ArrayList<ItemStack> ammo) {
		if(ammo.isEmpty())
			return true;
		HashMap<Integer, ? extends ItemStack> invAll = new HashMap<Integer, SpoutItemStack>();
		ItemStack ammoStack = null;
		for(ItemStack theStack : ammo) {
			invAll = inv.all(theStack.getTypeId());
			for(int j = 0; j < inv.getSize(); j++) {
				if(invAll.containsKey(j)) {
					ItemStack hi = invAll.get(j);
					if(hi.getTypeId() == theStack.getTypeId() && hi.getDurability() == theStack.getDurability()) {
						ammoStack = new SpoutItemStack(hi);
						break;
					}
				}
			}
		}
		if(ammoStack == null) {
			return true;
		}
		if(new SpoutItemStack(ammoStack).isCustomItem()){
			Ammo a = getAmmo(ammoStack);
			int maxdur = ((NumberProperty)a.getProperty("AMMODURABILITY")).getValue().intValue();
			int amountbefore = ammoStack.getAmount();
			if(maxdur>0)
				ammoStack = Utils.changeDurability((GenericCustomItem)a, maxdur, ammoStack, (short)1, true);
			if(amountbefore>ammoStack.getAmount()) return true;
		}else{
			return true;
		}
		return false;
	}

	
	@SuppressWarnings("deprecation")
	public static void removeGunInHand(SpoutPlayer sp) {
		if(isGun(sp.getItemInHand())) {
			ItemStack remove = sp.getItemInHand();
			if(remove == null)
				return;
			if(remove.getAmount() > 1) {
				remove.setAmount(remove.getAmount() - 1);
			} else {
				remove = null;
				sp.setItemInHand(null);
			}
			sp.updateInventory();
		}
	}

	public static Ammo getFirstCustomAmmo(Inventory inv, ArrayList<ItemStack> ammo) {
		if(ammo.isEmpty())
			return null;
		HashMap<Integer, ? extends ItemStack> invAll = new HashMap<Integer, SpoutItemStack>();
		ItemStack ammoStack = null;
		for(ItemStack theStack : ammo) {
			invAll = inv.all(theStack.getTypeId());
			for(int j = 0; j < inv.getSize(); j++) {
				if(invAll.containsKey(j)) {
					ItemStack hi = invAll.get(j);
					if(hi.getTypeId() == theStack.getTypeId() && hi.getDurability() == theStack.getDurability()) {
						ammoStack = hi;
						for(Ammo y : GunsPlus.allAmmo) {
							if(new SpoutItemStack(y).getDurability() == ammoStack.getDurability() && new SpoutItemStack(y).getTypeId() == ammoStack.getTypeId())
								return y;
						}
					}
				}
			}
		}
		return null;
	}
	
	public static SpoutItemStack getFirstCustomAmmoStack(Inventory inv, ArrayList<ItemStack> ammo) {
		if(ammo.isEmpty())
			return null;
		HashMap<Integer, ? extends ItemStack> invAll = new HashMap<Integer, SpoutItemStack>();
		ItemStack ammoStack = null;
		for(ItemStack theStack : ammo) {
			invAll = inv.all(theStack.getTypeId());
			for(int j = 0; j < inv.getSize(); j++) {
				if(invAll.containsKey(j)) {
					ItemStack hi = invAll.get(j);
					if(hi.getTypeId() == theStack.getTypeId() && hi.getDurability() == theStack.getDurability()) {
						ammoStack = hi;
						Ammo a = getAmmo(ammoStack);
						int maxdur = ((NumberProperty)a.getProperty("AMMODURABILITY")).getValue().intValue();
						if(maxdur>0)
							ammoStack = Utils.changeDurability((GenericCustomItem)a, maxdur, ammoStack, (short)0, false);
						return new SpoutItemStack(ammoStack);
					}
				}
			}
		}
		return null;
	}

	public static boolean checkInvForAmmo(Inventory inv, ArrayList<ItemStack> ammo) {
		if(ammo.isEmpty())
			return true;
		HashMap<Integer, ? extends ItemStack> invAll = new HashMap<Integer, SpoutItemStack>();
		for(ItemStack theStack : ammo) {
			invAll = inv.all(theStack.getTypeId());
			for(int j = 0; j < inv.getSize(); j++) {
				if(invAll.containsKey(j)) {
					ItemStack hi = invAll.get(j);
					if(hi.getTypeId() == theStack.getTypeId() && hi.getDurability() == theStack.getDurability()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static boolean checkInvForGun(Inventory inv, Gun g) {
		if(g == null)
			return true;
		HashMap<Integer, ? extends ItemStack> invAll = new HashMap<Integer, SpoutItemStack>();
		SpoutItemStack theStack = new SpoutItemStack((GenericCustomItem) g);
		invAll = inv.all(theStack.getTypeId());
		for(int j = 0; j < inv.getSize(); j++) {
			if(invAll.containsKey(j)) {
				ItemStack hi = invAll.get(j);
				if(hi.getTypeId() == theStack.getTypeId() && hi.getDurability() == theStack.getDurability()) {
					return true;
				}
			}
		}
		return false;
	}

	public static int getAmmoCount(Inventory inv, ArrayList<ItemStack> ammo) {
		HashMap<Integer, ? extends ItemStack> invAll = new HashMap<Integer, SpoutItemStack>();
		int counter = 0;
		if(ammo == null)
			return counter;
		for(ItemStack theStack : ammo) {
			invAll = inv.all(theStack.getTypeId());
			for(int j = 0; j < inv.getSize(); j++) {
				if(invAll.containsKey(j)) {
					SpoutItemStack hi = new SpoutItemStack(invAll.get(j));
					if(hi.getTypeId() == theStack.getTypeId() && hi.getDurability() == theStack.getDurability()) {
						if(!isAmmo(hi)){
							counter += hi.getAmount();
						}else{
							counter+=(hi.getAmount()*hi.getEnchantmentLevel(SpoutEnchantment.MAX_DURABILITY))-hi.getEnchantmentLevel(SpoutEnchantment.DURABILITY);
						}
					}
				}
			}
		}
		return counter;
	}

	@SuppressWarnings("unchecked")
	public static void performEffects(Shooter shooter, HashSet<LivingEntity> tars, PropertyHolder gunP, Gun gun) {
		LivingEntity shooterEntity = null;
		Location shooterLocation, targetLocation;
		int gunrange = ((NumberProperty) gunP.getProperty("RANGE")).getValue().intValue();
		if(shooter instanceof LivingShooter) {
			shooterEntity = ((LivingShooter) shooter).getLivingEntity();
		}
		if(tars.isEmpty() && shooterEntity != null) {
			tars.add(shooterEntity);
		}
		for(Effect e : (List<Effect>) ((CollectionProperty<Effect>) gunP.getProperty("EFFECTS")).getValue()) {

			Bukkit.getServer().getPluginManager().callEvent(new GunEffectEvent(shooter, gun, e));

			for(LivingEntity target : tars) {
				targetLocation = target.getLocation();
				shooterLocation = shooter.getLocation();
				if(shooterEntity != null && shooterEntity.equals(target)) {
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

		if(holdsGun(p)) {
			for(Gun g : GunsPlus.allGuns) {
				SpoutItemStack sis = new SpoutItemStack((GenericCustomItem) g);
				if(is.getTypeId() == sis.getTypeId() && is.getDurability() == sis.getDurability()) {
					return g;
				}
			}
		}
		return null;
	}

	public static void zoomOut(GunsPlusPlayer gp) {
		// PotionEffect pe = new PotionEffect(PotionEffectType.SLOW, 0, 100);
		// p.addPotionEffect(pe, true);
		SpoutPlayer sp = (SpoutPlayer) gp.getPlayer();
		org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer cp = (org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer) sp;

		try {
			Field field = EntityLiving.class.getDeclaredField("effects");
			field.setAccessible(true);
			@SuppressWarnings("rawtypes")
			HashMap effects = (HashMap) field.get(cp.getHandle());
			effects.remove(2);
			EntityPlayer player = cp.getHandle();
			player.playerConnection.sendPacket(new net.minecraft.server.v1_6_R2.Packet42RemoveMobEffect(player.id, new MobEffect(2, 0, 0)));
			cp.getHandle().getDataWatcher().watch(8, Integer.valueOf(0));
		} catch(Exception e) {
			e.printStackTrace();
		}
		UUID w = PlayerUtils.getPlayerBySpoutPlayer(sp).getZoomtexture();
		if(sp.getMainScreen().containsWidget(w)) {
			sp.getMainScreen().removeWidget(sp.getMainScreen().getWidget(w));
			if(sp.getGameMode().equals(GameMode.SURVIVAL))
				sp.getMainScreen().toggleSurvivalHUD(false);
		}
	}

	public static void zoomIn(GunsPlus plugin, GunsPlusPlayer gp, GenericTexture zTex, int zoomfactor) {
		// SpoutPlayer sp = (SpoutPlayer) p;
		// PotionEffect pe = new PotionEffect(PotionEffectType.SLOW, 24000,
		// zoomfactor);
		// p.addPotionEffect(pe, true);
		SpoutPlayer sp = (SpoutPlayer) gp.getPlayer();
		org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer cp = (org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer) sp;

		cp.getHandle().addEffect(new MobEffect(2, 24000, zoomfactor));
		try {
			Field field;
			field = EntityLiving.class.getDeclaredField("effects");
			field.setAccessible(true);
			@SuppressWarnings("rawtypes")
			HashMap effects = (HashMap) field.get(cp.getHandle());
			effects.remove(2);
		} catch(NoSuchFieldException e) {
			e.printStackTrace();
		} catch(IllegalAccessException e1) {
			e1.printStackTrace();
		}

		if(!(zTex == null)) {
			GenericTexture t = (GenericTexture) zTex.copy();
			t.setHeight(sp.getMainScreen().getHeight()).setWidth(sp.getMainScreen().getWidth());
			sp.getMainScreen().attachWidget(plugin, t);
			gp.setZoomtexture(t.getId());
			if(sp.getGameMode().equals(GameMode.SURVIVAL))
				sp.getMainScreen().toggleSurvivalHUD(false);
		}
	}

	@SuppressWarnings("unchecked")
	public static boolean isHudEnabled(Gun g) {
		return ((ObjectProperty<Boolean>) g.getProperty("HUDENABLED")).getValue();
	}

	@SuppressWarnings("unchecked")
	public static boolean isMountable(Gun g) {
		return ((ObjectProperty<Boolean>) g.getProperty("MOUNTABLE")).getValue();
	}

	@SuppressWarnings("unchecked")
	public static boolean isShootable(Gun g) {
		return ((ObjectProperty<Boolean>) g.getProperty("SHOOTABLE")).getValue();
	}

	public static boolean isGun(ItemStack i) {
		if(i != null)
			for(Gun g : GunsPlus.allGuns) {
				SpoutItemStack sis = new SpoutItemStack((GenericCustomItem) g);
				if(i.getTypeId() == sis.getTypeId() && i.getDurability() == sis.getDurability()) {
					return true;
				}
			}
		return false;
	}
	
	public static boolean isAmmo(ItemStack i) {
		if(i != null)
			for(Ammo g : GunsPlus.allAmmo) {
				SpoutItemStack sis = new SpoutItemStack((GenericCustomItem) g);
				if(i.getTypeId() == sis.getTypeId() && i.getDurability() == sis.getDurability()) {
					return true;
				}
			}
		return false;
	}
	
	public static boolean isAddition(ItemStack i) {
		if(i != null)
			for(Addition g : GunsPlus.allAdditions) {
				SpoutItemStack sis = new SpoutItemStack((GenericCustomItem) g);
				if(i.getTypeId() == sis.getTypeId() && i.getDurability() == sis.getDurability()) {
					return true;
				}
			}
		return false;
	}

	public static boolean isGunsPlusItem(ItemStack i) {
		return isGun(i)||isAmmo(i)||isAddition(i);
	}
	
	public static GenericCustomItem getGunsPlusItem(ItemStack i) {
		if(isGun(i))return (GenericCustomItem)getGun(i);
		if(isAmmo(i))return (GenericCustomItem)getAmmo(i);
		if(isAddition(i))return (GenericCustomItem)getAddition(i);
		return null;
	}
	
	public static Gun getGun(ItemStack i) {
		if(i != null)
			for(Gun g : GunsPlus.allGuns) {
				SpoutItemStack sis = new SpoutItemStack((GenericCustomItem) g);
				if(i.getTypeId() == sis.getTypeId() && i.getDurability() == sis.getDurability()) {
					return g;
				}
			}
		return null;
	}
	
	public static Ammo getAmmo(ItemStack i) {
		if(i != null)
			for(Ammo g : GunsPlus.allAmmo) {
				SpoutItemStack sis = new SpoutItemStack((GenericCustomItem) g);
				if(i.getTypeId() == sis.getTypeId() && i.getDurability() == sis.getDurability()) {
					return g;
				}
			}
		return null;
	}
	
	public static Addition getAddition(ItemStack i) {
		if(i != null)
			for(Addition g : GunsPlus.allAdditions) {
				SpoutItemStack sis = new SpoutItemStack((GenericCustomItem) g);
				if(i.getTypeId() == sis.getTypeId() && i.getDurability() == sis.getDurability()) {
					return g;
				}
			}
		return null;
	}
	
	public static List<Block> getTargetBlocks(Location loc, Gun g) {
		List<Block> targets = new ArrayList<Block>();
		BlockIterator bitr = new BlockIterator(loc.add(0, 0, 0), 0d, ((NumberProperty) g.getProperty("RANGE")).getValue().intValue());
		Block b;
		while(bitr.hasNext()) {
			b = bitr.next();
			if(!Utils.isTransparent(b))
				targets.add(b);
		}
		return targets;
	}
}