package team.GunsPlus.Classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.material.item.GenericCustomItem;
import org.getspout.spoutapi.player.SpoutPlayer;

public class Gun extends GenericCustomItem{

	//PRIVATE PROPERTIES
	private ArrayList<ItemStack> ammo = new ArrayList<ItemStack>();
	private Map<String,Float> values = new HashMap<String,Float>(); //Holds Damage, Recoil, Etc in single location
	private Map<String, String> resources = new HashMap<String, String>(); //Holds file resources like texture and sounds
	
	public Gun(Plugin plugin, String name, String texture) {
		super(plugin, name, texture);
	}
	
	
	public void zoom(SpoutPlayer sp){
		//When doing Scope texture placement, set the render priority to normal or low
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
		return values.containsKey(name)?values.get(name):null;
	}

}
