package team.GunsPlus;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.gui.GenericTexture;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.player.SpoutPlayer;

import team.GunsPlus.Enum.Projectile;
import team.GunsPlus.Gui.HUD;
import team.GunsPlus.Item.Ammo;
import team.GunsPlus.Item.Gun;
import team.GunsPlus.Util.GunUtils;
import team.GunsPlus.Util.PlayerUtils;
import team.GunsPlus.Util.Shooter;
import team.GunsPlus.Util.Task;
import team.GunsPlus.Util.Util;

public class GunsPlusPlayer extends Shooter {

	private SpoutPlayer player;
	private HUD hud;
	private GenericTexture zoomtexture;
	private boolean zooming = false;

	public GunsPlusPlayer(SpoutPlayer sp, HUD h) {
		player = sp;
		hud = h;
		for (Gun g : GunsPlus.allGuns)
			this.resetFireCounter(g);
	}

	public boolean isZooming() {
		return zooming;
	}

	public void setZooming(boolean zooming) {
		this.zooming = zooming;
	}

	public GenericTexture getZoomtexture() {
		return zoomtexture;
	}

	public void setZoomtexture(GenericTexture zoomtexture) {
		this.zoomtexture = zoomtexture;
	}

	public HUD getHUD() {
		return hud;
	}

	public SpoutPlayer getPlayer() {
		return player;
	}

	@Override
	public Location getLocation(){
		return getPlayer().getLocation();
	}
	
	public void zoom(Gun g) {
		if (Util.enteredTripod(getPlayer()) && GunsPlus.forcezoom)
			return;
		if (!g.getObjects().containsKey("ZOOMTEXTURE")) {
			GenericTexture zoomtex = new GenericTexture((String) g
					.getResources().get("ZOOMTEXTURE"));
			zoomtex.setAnchor(WidgetAnchor.SCALE).setX(0).setY(0)
					.setPriority(RenderPriority.Low);
			g.getObjects().put("ZOOMTEXTURE", zoomtex);
		}
		if (!isZooming()) {
			GunUtils.zoomIn(GunsPlus.plugin, this, (GenericTexture) g
					.getObjects().get("ZOOMTEXTURE"), (int) g
					.getValue("ZOOMFACTOR"));
			setZooming(true);
			if (GunsPlus.notifications)
				(getPlayer()).sendNotification(g.getName(), "Zoomed in!",
						Material.SULPHUR);
		} else {
			GunUtils.zoomOut(this);
			setZooming(false);
			if (GunsPlus.notifications)
				(getPlayer()).sendNotification(g.getName(), "Zoomed out!",
						Material.SULPHUR);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void fire(Gun g, Inventory inv) {
		if (!GunUtils.checkInvForAmmo(inv, g.getAmmo()))
			return;
		if (isReloading())
			return;
		else if (isDelaying())
			return;
		else if (isOutOfAmmo(g))
			return;
		else {
			Ammo usedAmmo = GunUtils.getFirstCustomAmmo(inv, g.getAmmo());
			HashMap<LivingEntity, Integer> targets_damage = new HashMap<LivingEntity, Integer>(
					getTargets(g));
			for (LivingEntity tar : targets_damage.keySet()) {
				if (tar.equals(getPlayer())) {
					continue;
				}
				int damage = targets_damage.get(tar);
				GunUtils.shootProjectile(getLocation(), tar.getLocation(),
						(Projectile) g.getObject("PROJECTILE"));
				if (damage < 0)
					PlayerUtils.sendNotification(getPlayer(), "Headshot!",
							"with a " + GunUtils.getGunNameWITHOUTAdditions(g),
							new ItemStack(Material.ARROW), 2000);
				targets_damage.put(tar, Math.abs(damage));
				damage = targets_damage.get(tar);
				if (Util.getRandomInteger(0, 100) <= g.getValue("CRITICAL")) {
					PlayerUtils.sendNotification(getPlayer(), "Critical!",
							"with a " + GunUtils.getGunNameWITHOUTAdditions(g),
							new ItemStack(Material.DIAMOND_SWORD), 2000);
					damage = tar.getHealth() + 1000;
				}
				if (usedAmmo != null) {
					damage += usedAmmo.getDamage();
				}
				damage(tar, Math.abs(damage));
			}
			
			GunUtils.performEffects(new HashSet<LivingEntity>(targets_damage.keySet()),player, g);

			GunUtils.removeAmmo(inv, g.getAmmo());
			getPlayer().updateInventory();
			setFireCounter(g, getFireCounter(g) + 1);

			if (!(g.getResource("SHOTSOUND") == null)) {
				if (g.getValue("SHOTDELAY") < 5
						&& Util.getRandomInteger(0, 100) < 35) {
					Util.playCustomSound(GunsPlus.plugin, getLocation(),
							g.getResource("SHOTSOUND"),
							(int) g.getValue("SHOTSOUNDVOLUME"));
				} else {
					Util.playCustomSound(GunsPlus.plugin, getLocation(),
							g.getResource("SHOTSOUND"),
							(int) g.getValue("SHOTSOUNDVOLUME"));
				}

			}

			recoil(g);

			if (GunsPlus.autoreload
					&& getFireCounter(g) >= g.getValue("SHOTSBETWEENRELOAD"))
				reload(g);
			if ((int) g.getValue("SHOTDELAY") > 0)
				delay(g);
		}
	}

	@Override
	public void reload(Gun g) {
		if (getFireCounter(g) == 0)
			return;
		PlayerUtils.sendNotification(getPlayer(),
				GunUtils.getGunNameWITHOUTAdditions(g), "Reloading...",
				new ItemStack(Material.WATCH), 2000);
		if (isReloadResetted()) {
			setOnReloadingQueue();
		}
		if (isOnReloadingQueue()) {
			Task reloadTask = new Task(GunsPlus.plugin, this, g) {
				public void run() {
					Shooter s = (Shooter) this.getArg(0);
					Gun g = (Gun) this.getArg(1);
					s.resetReload();
					s.resetFireCounter(g);
				}
			};
			reloadTask.startDelayed((int) g.getValue("RELOADTIME"));
			setReloading();
			if (!(g.getResource("RELOADSOUND") == null)) {
				Util.playCustomSound(GunsPlus.plugin, getLocation(),
						g.getResource("RELOADSOUND"),
						(int) g.getValue("RELOADSOUNDVOLUME"));
			}
			return;
		} else if (isReloading()) {
			return;
		}
	}

	@Override
	public Map<LivingEntity, Integer> getTargets(Gun gun) {
		return GunUtils.getTargets(player.getEyeLocation(), gun, isZooming());
	}

	@Override
	public void damage(LivingEntity target, int damage) {
		target.damage(damage, getPlayer());
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof GunsPlusPlayer) {
			GunsPlusPlayer gp = (GunsPlusPlayer) o;
			if (gp.getPlayer().equals(this.getPlayer())) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public void recoil(Gun gun) {
		if (!Util.enteredTripod(getPlayer())) {
			if (gun.getValue("KNOCKBACK") > 0)
				PlayerUtils.performKnockBack(getPlayer(),
						gun.getValue("KNOCKBACK"));
			if (gun.getValue("RECOIL") > 0)
				PlayerUtils.performRecoil(getPlayer(), gun.getValue("RECOIL"));
		}
	}
}
