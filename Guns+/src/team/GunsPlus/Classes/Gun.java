package team.GunsPlus.Classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.material.item.GenericCustomItem;
import org.getspout.spoutapi.player.SpoutPlayer;

import team.GunsPlus.EffectType;

public class Gun extends GenericCustomItem{

	//PRIVATE PROPERTIES
	private ArrayList<ItemStack> ammo = new ArrayList<ItemStack>(); //Holds all accepted ammo itemstacks
	private Map<String,Float> values = new HashMap<String,Float>(); //Holds Damage, Recoil, Etc in single location
	private Map<String, String> resources = new HashMap<String, String>(); //Holds file resources like texture and sounds
	private ArrayList<EffectType> effects = new ArrayList<EffectType>(); //Holds the effects of the gun
	
	public Gun(Plugin plugin, String name, String texture) {
		super(plugin, name, texture);
	}
	
	
	public void zoom(SpoutPlayer sp){
		
	}
	
	public void fire(SpoutPlayer sp){
		
	}
	
	public void reload(SpoutPlayer sp){
		
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
		Float f = -1F;
		if(values.containsKey(name)) f = values.get(name);
		return f;
	}
	
	public void addEffect(EffectType et){
		effects.add(et);
	}
	
	public void removeEffect(EffectType et){
		if(effects.contains(et))
			effects.remove(et);
	}

}
