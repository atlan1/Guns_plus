package team.GunsPlus.Classes;

import java.util.ArrayList;


import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.material.item.GenericCustomItem;
import org.getspout.spoutapi.player.SpoutPlayer;

public class Gun extends GenericCustomItem{

	//PRIVATE PROPERTIES
	private ArrayList<ItemStack> ammo = new ArrayList<ItemStack>();
	
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
	
}
