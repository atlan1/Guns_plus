package team.GunsPlus.Classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import org.bukkit.Material;
import org.bukkit.entity.Player;
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
import team.GunsPlus.Util;
import team.old.GunsPlus.Classes.Task;

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
		if(!plugin.inZoom.contains(sp)){
			GunUtils.zoomIn(plugin, sp, (GenericTexture)objects.get("ZOOMTEXTURE"), Math.round(values.get("ZOOMFACTOR"))); 
			plugin.inZoom.add(sp);
			if(GunsPlus.generalConfig.getBoolean("send-notifications"))
			(sp).sendNotification(this.getName(), "Zoomed in!", Material.ENDER_PEARL);
		}else{
			GunUtils.zoomOut(sp); 
			plugin.inZoom.remove(sp);
			if(GunsPlus.warnings) (sp).sendNotification(this.getName(), "Zoomed out!", Material.GOLDEN_APPLE);
		}
	}
	//TODO decrease the fireCounter by one on fire
	public void fire(SpoutPlayer sp){
		
	}
	
	public void reload(SpoutPlayer sp){
		if(plugin.fireCounter.get(sp)== Math.round(values.get("SHOTSBETWEENRELOAD")))return;
		if(GunsPlus.generalConfig.getBoolean("send-notifications"))
			sp.sendNotification(this.getName(), "Reloading...", Material.WATCH);
		if(plugin.reloading.contains(sp)){
			Task t = new Task(plugin, sp){
				public void run() {
					SpoutPlayer p = (SpoutPlayer) this.getArg(0);
					plugin.fireCounter.put(p, Math.round(values.get("SHOTSBETWEENRELOAD")));
				}
			};
			t.startDelayed(Math.round(values.get("RELOAD")));
			plugin.reloading.remove(sp);
			if(!(resources.get("RELOADSOUND")==null)){
				Util.playCustomSound(plugin, sp, resources.get("RELOADSOUND"));
			}
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
