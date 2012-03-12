package team.GunsPlus.Classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;


import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.gui.GenericTexture;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.material.item.GenericCustomItem;
import org.getspout.spoutapi.player.SpoutPlayer;

import team.GunsPlus.EffectType;
import team.GunsPlus.GunUtils;
import team.GunsPlus.GunsPlus;
import team.GunsPlus.Task;
import team.GunsPlus.Util;

public class Gun extends GenericCustomItem{

	//PRIVATE PROPERTIES
	private ArrayList<ItemStack> ammo = new ArrayList<ItemStack>(); //Holds all accepted ammo itemstacks
	private Map<String,Float> values = new HashMap<String,Float>(); //Holds Damage, Recoil, Etc in single location
	private Map<String, String> resources = new HashMap<String, String>(); //Holds file resources like texture and sounds
	private Map<String, Object> objects = new HashMap<String, Object>(); //holds anything else 
	private ArrayList<EffectType> effects = new ArrayList<EffectType>(); //Holds the effects of the gun
	private GunsPlus plugin;
	
	public Gun(Plugin plugin, String name, String texture) {
		super(plugin, name, texture);
		this.plugin = (GunsPlus) plugin;
	}
	
	
	public void zoom(SpoutPlayer sp){
		if(!objects.containsKey("ZOOMTEXTURE")){
			GenericTexture zoomtex = new GenericTexture((String)resources.get("ZOOMTEXTURE"));
			zoomtex.setAnchor(WidgetAnchor.SCALE).setX(0).setY(0).setPriority(RenderPriority.Low);
			objects.put("ZOOMTEXTURE", zoomtex);
		}
		if(!Util.isZooming(sp)){
			GunUtils.zoomIn(plugin, sp, (GenericTexture)objects.get("ZOOMTEXTURE"), (int) getValue("ZOOMFACTOR")); 
			Util.setZooming(sp, true);
			if(GunsPlus.notifications)  (sp).sendNotification(this.getName(), "Zoomed in!", Material.ENDER_PEARL);
		}else{
			GunUtils.zoomOut(sp); 
			Util.setZooming(sp, false);
			if(GunsPlus.notifications) (sp).sendNotification(this.getName(), "Zoomed out!", Material.GOLDEN_APPLE);
		}
	}

	public void fire(SpoutPlayer sp){
		//something causes a return here
		if(!GunUtils.checkInvForAmmo(sp, getAmmo()))return;
		if(Util.isReloading(sp))return;
		else if(Util.isDelayed(sp)) return;
		else{
			//TODO shoot projectile
			if((Util.isZooming(sp)?getValue("ACCURACYIN"):getValue("ACCURACYOUT"))<=Util.getRandomInteger(0, 100)){
				HashMap<LivingEntity, Integer> targets_damage = GunUtils.getTargets(sp, this);
				for(LivingEntity tar : targets_damage.keySet()){
					if(Util.getRandomInteger(0, 100)<=getValue("CRITICAL")){
						if(GunsPlus.notifications) sp.sendNotification(this.getName(), "Critical hit!", Material.DIAMOND_SWORD);
						targets_damage.put(tar, tar.getHealth());
					}
					tar.damage(targets_damage.get(tar), sp);
				}
			}

			//TODO effects

			GunUtils.removeAmmo(ammo, sp);
			Util.setFireCounter(sp, Util.getFireCounter(sp)+1);
			
			if(getValue("RECOIL") != 0) GunUtils.performRecoil(plugin, sp, getValue("RECOIL"));
			if(getValue("KNOCKBACK") != 0) GunUtils.performKnockBack(sp, getValue("KNOCKBACK"));
			
			if(Util.getFireCounter(sp)>=getValue("SHOTSBETWEENRELOAD")&&GunsPlus.autoreload) reload(sp);
			else if(getValue("SHOTDELAY")>0) delay(sp);
		}
	}
	
	public void reload(SpoutPlayer sp){
		if(Util.getFireCounter(sp) == 0)return;
		if(GunsPlus.notifications)  sp.sendNotification(this.getName(), "Reloading...", Material.WATCH);
			
		if(!GunsPlus.reloading.containsKey(sp)){
			Util.setOnReloadQueue(sp);
		}
		if(Util.isOnReloadQueue(sp)){
			Task reloadTask = new Task(plugin, sp){
				public void run() {
					SpoutPlayer sp = (SpoutPlayer) this.getArg(0);
					Util.removeReload(sp);
					Util.resetFireCounter(sp);
				}
			};
			reloadTask.startDelayed((int)getValue("RELOADTIME"));
			Util.setReloading(sp);
			if(!(getResource("RELOADSOUND")==null)){
				Util.playCustomSound(plugin, sp, getResource("RELOADSOUND"));
			}
			return;
		}else if(Util.isReloading(sp)){
			return;
		}
	}
	
	public void delay(SpoutPlayer sp){
		if(!GunsPlus.delaying.containsKey(sp)){
			Util.setOnDelayQueue(sp);
		}
		if(Util.isOnDelayQueue(sp)){
			Task t = new Task(plugin, sp){
				public void run() {
					SpoutPlayer sp = (SpoutPlayer) this.getArg(0);
					Util.removeDelay(sp);
				}
			};
			t.startDelayed((long) getValue("SHOTDELAY"));
			Util.setDelayed(sp);
		}else if(Util.isDelayed(sp)){
			return;
		}
	}
	
	//GETTERS AND SETTERS
	public ArrayList<ItemStack> getAmmo() {
		return ammo;
	}

	public void setAmmo(ArrayList<ItemStack> ammo) {
		this.ammo = ammo;
	}

	public String getResource(String s) {
		return resources.containsKey(s)?resources.get(s):null;
	}

	public void setResource(String name, String resource) {
		this.resources.put(name, resource);
	}
	
	public void setValue(String name, Float value) {
		values.put(name, value);
	}
	
	public float getValue(String name) {
		return values.containsKey(name)?values.get(name):null;
	}
	
	public void addEffect(EffectType et){
		effects.add(et);
	}
	
	public void removeEffect(EffectType et){
		if(effects.contains(et))
			effects.remove(et);
	}

}
