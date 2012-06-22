package team.GunsPlus.Item;
import org.bukkit.plugin.Plugin;

import team.ApiPlus.API.Type.ItemType;

public class Ammo extends ItemType {

	private int damage = 0;
	
	public Ammo(Plugin plugin, String name, String texture) {
		super(plugin, name, texture);
	}
	
	public int getDamage(){
		return damage;
	}
	
	public void setDamage(int dmg){
		damage = dmg;
	}
}
