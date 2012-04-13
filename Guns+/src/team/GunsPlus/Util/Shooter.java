package team.GunsPlus.Util;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.Inventory;

import team.GunsPlus.GunsPlus;
import team.GunsPlus.Enum.Projectile;
import team.GunsPlus.Item.Ammo;
import team.GunsPlus.Item.Gun;

public abstract class Shooter {
	
	private GunsPlus plugin = GunsPlus.plugin;
	private Location location;
	private Boolean reloading = null;
	private Boolean delaying = null;
	private Map<Gun, Integer> fireCounter = new HashMap<Gun, Integer>();
	
	public void resetReload(){
		this.reloading = null;
	}
	
	public void resetDelay(){
		this.delaying = null;
	}
	
	public boolean isReloading() {
		return reloading!=null&&reloading==true?true:false;
	}
	
	public boolean isOnReloadingQueue() {
		return reloading!=null&&reloading==false?true:false;
	}

	public boolean isOnDelayingQueue() {
		return delaying!=null&&delaying==false?true:false;
	}

	public boolean isDelayResetted(){
		return delaying==null?true:false;
	}
	
	public boolean isReloadResetted(){
		return reloading==null?true:false;
	}

	public void setReloading() {
		this.reloading = true;
	}
	
	public void setOnReloadingQueue() {
		this.reloading = false;
	}

	public boolean isDelaying() {
		return delaying!=null&&delaying==true?true:false;
	}

	public void setDelaying() {
		this.delaying = true;
	}
	
	public void setOnDelayingQueue() {
		this.delaying = false;
	}

	public int  getFireCounter(Gun g) {
		return fireCounter.containsKey(g)?fireCounter.get(g):-1;
	}

	public void setFireCounter(Gun g, int i) {
		fireCounter.put(g, i);
	}
	
	public void resetFireCounter(Gun g){
			fireCounter.put(g, 0);
	}
	
	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
	
	public boolean isOutOfAmmo(Gun g){
		if(this.getFireCounter(g)>=g.getValue("SHOTSBETWEENRELOAD")){
			return true;
		}
		return false;
	}

	public void reload(Gun g){
		if(getFireCounter(g) == 0)return;
		if(isReloadResetted()){
			setOnReloadingQueue();
		}
		if(isOnReloadingQueue()){
			Task reloadTask = new Task(plugin, this, g){
				public void run() {
					Shooter s = (Shooter) this.getArg(0);
					Gun g = (Gun) this.getArg(1);
					s.resetReload();
					s.resetFireCounter(g);
				}
			};
			reloadTask.startDelayed((int)g.getValue("RELOADTIME"));
			setReloading();
			if(!(g.getResource("RELOADSOUND")==null)){
				Util.playCustomSound(plugin, getLocation(), g.getResource("RELOADSOUND"), (int) g.getValue("RELOADSOUNDVOLUME"));
			}
			return;
		}else if(isReloading()){
			return;
		}
	}
	
	public void delay(Gun g){
		if(isDelayResetted()){
			setOnDelayingQueue();
		}
		if(isOnDelayingQueue()){
			Task t = new Task(plugin, this){
				public void run() {
					Shooter sp = (Shooter) this.getArg(0);
					sp.resetDelay();
				}
			};
			t.startDelayed((long) g.getValue("SHOTDELAY"));
			setDelaying();
		}else if(isDelaying()){
			return;
		}
	}
	
	public void fire(Gun g, Inventory inv){
		if(!GunUtils.checkInvForAmmo(inv, g.getAmmo()))return;
		if(isReloading())return;
		else if(isDelaying()) return;
		else if(isOutOfAmmo(g)) return;
		else{
			Ammo usedAmmo = GunUtils.getFirstCustomAmmo(inv, g.getAmmo());
			HashMap<LivingEntity, Integer> targets_damage = new HashMap<LivingEntity, Integer>(getTargets(g));
			for(LivingEntity tar : targets_damage.keySet()){
				int damage = Math.abs(targets_damage.get(tar));
				GunUtils.shootProjectile(getLocation(), tar.getLocation(), (Projectile) g.getObject("PROJECTILE"));
				if(Util.getRandomInteger(0, 100)<=g.getValue("CRITICAL")){
					damage = tar.getHealth()+1000;
				}
				if(usedAmmo!=null){
					damage += usedAmmo.getDamage();
				}
				damage(tar, Math.abs(damage));
			}

			GunUtils.removeAmmo(inv, g.getAmmo());
			
			setFireCounter(g, getFireCounter(g)+1);
			
			if(!(g.getResource("SHOTSOUND")==null)){
				if(g.getValue("SHOTDELAY")<5&&Util.getRandomInteger(0, 100)<35){
					Util.playCustomSound(plugin, getLocation(), g.getResource("SHOTSOUND"), (int) g.getValue("SHOTSOUNDVOLUME"));
				}else{
					Util.playCustomSound(plugin, getLocation(), g.getResource("SHOTSOUND"), (int) g.getValue("SHOTSOUNDVOLUME"));
				}
				
			}
			
			recoil(g);
			
			if(GunsPlus.autoreload&&getFireCounter(g)>=g.getValue("SHOTSBETWEENRELOAD")) reload(g);
			if((int)g.getValue("SHOTDELAY")>0) delay(g);
		}
	}
	
	public abstract Map<LivingEntity, Integer> getTargets(Gun gun);
	public abstract void damage(LivingEntity target, int damage);
	public abstract void recoil(Gun gun);
	
}
