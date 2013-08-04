package team.GunsPlus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.gui.GenericTexture;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.inventory.SpoutEnchantment;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.item.GenericCustomItem;
import org.getspout.spoutapi.player.SpoutPlayer;

import team.ApiPlus.API.Property.*;
import team.ApiPlus.Util.Task;
import team.ApiPlus.Util.Utils;
import team.GunsPlus.API.Classes.LivingShooter;
import team.GunsPlus.API.Classes.Shooter;
import team.GunsPlus.API.Event.Gun.GunFireEvent;
import team.GunsPlus.API.Event.Gun.GunReloadEvent;
import team.GunsPlus.API.Event.Gun.GunZoomInEvent;
import team.GunsPlus.API.Event.Gun.GunZoomOutEvent;
import team.GunsPlus.Block.Tripod;
import team.GunsPlus.Enum.Projectile;
import team.GunsPlus.Gui.HUD;
import team.GunsPlus.Item.Ammo;
import team.GunsPlus.Item.Gun;
import team.GunsPlus.Util.GunUtils;
import team.GunsPlus.Util.PlayerUtils;
import team.GunsPlus.Util.Util;

public class GunsPlusPlayer extends LivingShooter {

	private SpoutPlayer player;
	private HUD hud;
	private UUID zoomtexture;
	private boolean zooming = false;

	public GunsPlusPlayer(SpoutPlayer sp, HUD h) {
		player = sp;
		hud = h;
//		for(Gun g : GunsPlus.allGuns)
//			this.resetFireCounter(g);
	}

	public LivingEntity getLivingEntity() {
		return getPlayer();
	}

	public boolean isZooming() {
		return zooming;
	}

	public void setZooming(boolean zooming) {
		this.zooming = zooming;
	}

	public UUID getZoomtexture() {
		return zoomtexture;
	}

	public void setZoomtexture(UUID zoomtexture) {
		this.zoomtexture = zoomtexture;
	}

	public HUD getHUD() {
		return hud;
	}

	public SpoutPlayer getPlayer() {
		return player;
	}

	@Override
	public Location getLocation() {
		return getPlayer().getEyeLocation();
	}

	@SuppressWarnings("unchecked")
	public void zoom(Gun g) {
		if(!player.hasPermission("gunsplus.zoom.all") && GunsPlus.useperms) {
			if(!player.hasPermission("gunsplus.zoom." + ((GenericCustomItem) g).getName().toLowerCase().replace(" ", "_")))
				return;
		}
		if(Util.enteredTripod(getPlayer()) && Tripod.forcezoom)
			return;
		if(!g.getProperties().containsKey("LOADEDZOOMTEXTURE")) {
			GenericTexture zoomtex = new GenericTexture(((StringProperty) g.getProperty("ZOOMTEXTURE")).getValue());
			zoomtex.setAnchor(WidgetAnchor.SCALE).setX(0).setY(0).setPriority(RenderPriority.Low);
			g.getProperties().put("LOADEDZOOMTEXTURE", new ObjectProperty<GenericTexture>(zoomtex));
		}
		if(!isZooming()) {
			GunUtils.zoomIn(GunsPlus.plugin, this, ((ObjectProperty<GenericTexture>) g.getProperty("LOADEDZOOMTEXTURE")).getValue(), ((NumberProperty) g.getProperty("ZOOMFACTOR")).getValue().intValue());
			setZooming(true);
//			PlayerUtils.sendNotification(getPlayer(), ((GenericCustomItem) g).getName(), "Zoomed in!", new ItemStack(Material.SULPHUR), 2000);
			Bukkit.getPluginManager().callEvent(new GunZoomInEvent(this.getPlayer(), g));
		} else {
			GunUtils.zoomOut(this);
			setZooming(false);
//			PlayerUtils.sendNotification(getPlayer(), ((GenericCustomItem) g).getName(), "Zoomed out!", new ItemStack(Material.SULPHUR), 2000);
			Bukkit.getPluginManager().callEvent(new GunZoomOutEvent(this.getPlayer(), g));
		}
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public void fire(Gun g) {
		if(isFireing())
			return;
		setFireing(true);
		if(!player.hasPermission("gunsplus.fire.all") && GunsPlus.useperms) {
			if(!player.hasPermission("gunsplus.fire." + ((GenericCustomItem) g).getName().toLowerCase().replace(" ", "_"))) {
				setFireing(false);
				return;
			}
		}
		Inventory inv = getPlayer().getInventory();
		ArrayList<ItemStack> ammoTypes = (ArrayList<ItemStack>) ((CollectionProperty<ItemStack>) g.getProperty("AMMO")).getValue();
		if(!GunUtils.isShootable(g) && !GunUtils.isMountable(g)) {
			PlayerUtils.sendNotification(getPlayer(), "This gun is ready for", "the scrap heap!", new ItemStack(Material.IRON_INGOT), 2000);
			setFireing(false);
			return;
		} else if(GunUtils.isShootable(g) && !GunUtils.isMountable(g) && Util.enteredTripod(getPlayer())) {
			PlayerUtils.sendNotification(getPlayer(), "Use this gun ", "only outside a tripod!", new SpoutItemStack((GenericCustomItem) g), 2000);
			setFireing(false);
			return;
		} else if(!GunUtils.isShootable(g) && GunUtils.isMountable(g) && !Util.enteredTripod(getPlayer())) {
			PlayerUtils.sendNotification(getPlayer(), "Enter a tripod to", "use this heavy gun!", new SpoutItemStack((GenericCustomItem) g), 2000);
			setFireing(false);
			return;
		}
		if(Util.enteredTripod(getPlayer()))
			inv = Util.getTripodDataOfEntered(getPlayer()).getInventory();
		if(!GunUtils.checkInvForAmmo(inv, ammoTypes)) {
			setFireing(false);
			return;
		}
		if(isReloading()) {
			setFireing(false);
			return;
		} else if(isDelaying()) {
			setFireing(false);
			return;
		}else {
			Utils.makeUnstackable(getPlayer().getItemInHand());
			
			PropertyContainer pc = new PropertyContainer(g.getProperties());
			Ammo usedAmmo = GunUtils.getFirstCustomAmmo(inv, ammoTypes);
			if(usedAmmo != null) {
				Util.setProperties(usedAmmo, pc);
			}
			int maxdur = ((NumberProperty)pc.getProperty("DURABILITY")).getValue().shortValue();
			if(maxdur>0)
				getPlayer().setItemInHand(Utils.changeDurability((GenericCustomItem) g, maxdur, getPlayer().getItemInHand(), (short)1, true));
			HashMap<LivingEntity, Integer> targets_damage = new HashMap<LivingEntity, Integer>(GunUtils.getTargets(player.getEyeLocation(), pc, isZooming()));

			if(targets_damage.isEmpty()) {
				Location from = Util.getBlockInSight(this.getPlayer().getEyeLocation(), 2, 5).getLocation();
				GunUtils.shootProjectile(from, this.getPlayer().getEyeLocation().getDirection().toLocation(getLocation().getWorld()), ((ObjectProperty<Projectile>) pc.getProperty("PROJECTILE")).getValue());
			}
			for(LivingEntity tar : new HashSet<LivingEntity>(targets_damage.keySet())) {
				if(tar.equals(this.getPlayer())) {
					continue;
				}
				int damage = targets_damage.get(tar);
				Location from = Util.getBlockInSight(this.getPlayer().getEyeLocation(), 2, 5).getLocation();
				GunUtils.shootProjectile(from, tar.getEyeLocation(), ((ObjectProperty<Projectile>) pc.getProperty("PROJECTILE")).getValue());
				if(damage < 0) {
					PlayerUtils.sendNotification(this.getPlayer(), "Headshot!", "with a " + GunUtils.getRawGunName(g), new ItemStack(Material.ARROW), 2000);
					targets_damage.put(tar, Math.abs(damage));
					damage = targets_damage.get(tar);
				}
				if(!(((NumberProperty) pc.getProperty("CRITICAL")).getValue().intValue() <= 0) && Utils.getRandomInteger(1, 100) <= ((NumberProperty) pc.getProperty("CRITICAL")).getValue().intValue()) {
					PlayerUtils.sendNotification(this.getPlayer(), "Critical!", "with a " + GunUtils.getRawGunName(g), new ItemStack(Material.DIAMOND_SWORD), 2000);
					damage = tar.getHealth() + 20;
				}
//				System.out.println("DAMAGE: "+damage);
				tar.damage(damage, getPlayer());
			}

			g.performEffects(this, new HashSet<LivingEntity>(new ArrayList<LivingEntity>(targets_damage.keySet())), pc);

			if(!(pc.getProperty("SHOTSOUND") == null)) {
				if(((NumberProperty) pc.getProperty("SHOTDELAY")).getValue().intValue() < 5 && Utils.getRandomInteger(0, 100) < 35) {
					Util.playCustomSound(GunsPlus.plugin, getLocation(), ((StringProperty) pc.getProperty("SHOTSOUND")).getValue(), ((NumberProperty) pc.getProperty("SHOTSOUNDVOLUME")).getValue().intValue());
				} else {
					Util.playCustomSound(GunsPlus.plugin, getLocation(), ((StringProperty) pc.getProperty("SHOTSOUND")).getValue(), ((NumberProperty) pc.getProperty("SHOTSOUNDVOLUME")).getValue().intValue());
				}

			}

			boolean clipempty = GunUtils.clipEmpty(inv, ammoTypes);
			if(clipempty){
				this.setCanShoot(g, false);
			}
			Bukkit.getServer().getPluginManager().callEvent(new GunFireEvent(getPlayer(), g));
			
//			setFireCounter(g, getFireCounter(g) + 1);
			recoil(pc);
			boolean autoreload = GunsPlus.autoreload && !getCanShoot(g);
			if(autoreload){//getFireCounter(g) >= ((NumberProperty) pc.getProperty("SHOTSBETWEENRELOAD")).getValue().intValue()
				GunUtils.consumeAmmo(inv, ammoTypes);
				reload(g, true);
			}
			if(((NumberProperty) pc.getProperty("SHOTDELAY")).getValue().intValue() > 0)
				delay(g);
			if(!autoreload)
				GunUtils.consumeAmmo(inv, ammoTypes);
			getPlayer().updateInventory();
			setFireing(false);
		}


    }
	@SuppressWarnings("unchecked")
	@Override
	public void reload(Gun g, boolean autoreload) {
		if(!player.hasPermission("gunsplus.reload.all") && GunsPlus.useperms) {
			if(!player.hasPermission("gunsplus.reload." + ((GenericCustomItem) g).getName().toLowerCase().replace(" ", "_")))
				return;
		}
		if(getCurrentClipSize(g)==getMaxClipSize(g)&&!autoreload){
			return;
		}else if(getCurrentClipSize(g)>0&&!autoreload){
			Ammo a = GunUtils.getFirstCustomAmmo(getPlayer().getInventory(),  (ArrayList<ItemStack>) ((CollectionProperty<ItemStack>) g.getProperty("AMMO")).getValue());
			SpoutItemStack i = GunUtils.getFirstCustomAmmoStack(getPlayer().getInventory(),  (ArrayList<ItemStack>) ((CollectionProperty<ItemStack>) g.getProperty("AMMO")).getValue());
			int slot = getPlayer().getInventory().first(i);
			SpoutItemStack drop = new SpoutItemStack(i);
			drop.setAmount(1);
			i = new SpoutItemStack(Utils.changeDurability(a, ((NumberProperty)a.getProperty("AMMODURABILITY")).getValue().intValue(), i, (short) -i.getEnchantmentLevel(SpoutEnchantment.DURABILITY), false));
			i.setAmount(i.getAmount()-1);
			getPlayer().getInventory().setItem(slot, i);
			getPlayer().getWorld().dropItemNaturally(getPlayer().getLocation(), drop);
		}
		PlayerUtils.sendNotification(getPlayer(), GunUtils.getRawGunName(g), "Reloading...", new ItemStack(Material.WATCH), 2000);
		Bukkit.getPluginManager().callEvent(new GunReloadEvent(this.getPlayer(), g));
		if(isReloadResetted()) {
			setOnReloadingQueue();
		}
		if(isOnReloadingQueue()) {
			Task reloadTask = new Task(GunsPlus.plugin, this, g) {
				public void run() {
					Shooter s = (Shooter) this.getArg(0);
					Gun g = (Gun) this.getArg(1);
					s.resetReload();
					s.setCanShoot(g, true);
//					s.resetFireCounter(g);
				}
			};
			reloadTask.startTaskDelayed(((NumberProperty) g.getProperty("RELOADTIME")).getValue().intValue());
			setReloading();
			if(!(g.getProperty("RELOADSOUND") == null)) {
				Util.playCustomSound(GunsPlus.plugin, getLocation(), ((StringProperty) g.getProperty("RELOADSOUND")).getValue(), ((NumberProperty) g.getProperty("RELOADSOUNDVOLUME")).getValue().intValue());
			}
			return;
		} else if(isReloading()) {
			return;
		}
	}
	
	@SuppressWarnings("unchecked")
	public int getCurrentClipSize(Gun g) {
		Inventory inv = getPlayer().getInventory();
		ArrayList<ItemStack> ammoTypes =  (ArrayList<ItemStack>) ((CollectionProperty<ItemStack>) g.getProperty("AMMO")).getValue();
		Ammo a = GunUtils.getFirstCustomAmmo(inv, ammoTypes);
		SpoutItemStack sis = GunUtils.getFirstCustomAmmoStack(inv, ammoTypes); 
		if(a!=null){
			return sis.getEnchantmentLevel(SpoutEnchantment.MAX_DURABILITY)-sis.getEnchantmentLevel(SpoutEnchantment.DURABILITY);
		}
		return 1;
	}
	
	@SuppressWarnings("unchecked")
	public int getMaxClipSize(Gun g) {
		Inventory inv = getPlayer().getInventory();
		ArrayList<ItemStack> ammoTypes =  (ArrayList<ItemStack>) ((CollectionProperty<ItemStack>) g.getProperty("AMMO")).getValue();
		Ammo a = GunUtils.getFirstCustomAmmo(inv, ammoTypes);
		SpoutItemStack sis = GunUtils.getFirstCustomAmmoStack(inv, ammoTypes); 
		if(a!=null){
			return sis.getEnchantmentLevel(SpoutEnchantment.MAX_DURABILITY);
		}
		return 1;
	}
	
	@SuppressWarnings("unchecked")
	public int getTotalAmmo(Gun g) {
		return GunUtils.getAmmoCount(getPlayer().getInventory(), (ArrayList<ItemStack>) ((CollectionProperty<ItemStack>) g.getProperty("AMMO")).getValue());
	}
	
	@Override
	public void delay(Gun g) {
		if(isDelayResetted()) {
			setOnDelayingQueue();
		}
		if(isOnDelayingQueue()) {
			Task t = new Task(GunsPlus.plugin, this) {
				public void run() {
					Shooter s = (Shooter) this.getArg(0);
					s.resetDelay();
				}
			};
			t.startTaskDelayed(((NumberProperty) g.getProperty("SHOTDELAY")).getValue().intValue());
			setDelaying();
		} else if(isDelaying()) {
			return;
		}
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof GunsPlusPlayer) {
			GunsPlusPlayer gp = (GunsPlusPlayer) o;
			if(gp.getPlayer().equals(this.getPlayer())) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public void recoil(PropertyHolder gun) {
		if(!Util.enteredTripod(getPlayer())) {
			if(((NumberProperty) gun.getProperty("KNOCKBACK")).getValue().floatValue() > 0)
				PlayerUtils.performKnockBack(getPlayer(), ((NumberProperty) gun.getProperty("KNOCKBACK")).getValue().floatValue());
			if(((NumberProperty) gun.getProperty("RECOIL")).getValue().floatValue() > 0)
				PlayerUtils.performRecoil(getPlayer(), ((NumberProperty) gun.getProperty("RECOIL")).getValue().floatValue());
		}
	}
}
